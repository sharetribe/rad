(ns rad.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.tools.logging :as log]
            [ring.util.response :as resp]
            [rad.data-sources.uservoice-tickets :as uv-tickets]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clojure.data.json :as json]))

(use 'overtone.at-at)

(def my-pool (mk-pool))

(def data-source-specs [
  uv-tickets/datasource-spec
])

(defn process-data-sources [specs]
  (map (fn [spec]
    (let [current (atom {})]
      (every (:update-every spec) (fn []
        (log/info "Updating from data source" (:id spec))
        (swap! current (fn [x] ((:fetch spec))))
      ) my-pool)
      [(:id spec) (fn [] @current)]
    )
  ) specs)
)

(def data-sources (into {} (process-data-sources data-source-specs)))

(defn open-unassigned-uv-tickets []
  (let [
    data ((:uservoice-tickets data-sources))
  ]
    (log/info (str (first data)))
    (count (filter (fn [ticket]
      (not (contains? ticket "assignee"))
    ) data))
  )
)

(defn wait-time [ticket now]
  (let [format (f/formatter "yyyy/MM/dd HH:mm:ss Z")]
    (t/in-minutes
      (t/interval
        (f/parse format
          (get (first (get ticket "messages")) "created_at"))
        now))
  )
)

(defn open-response-time []
  (let [
    now (t/now)
    data ((:uservoice-tickets data-sources))
    waiting-answer (filter (fn [ticket]
      (and
        (=
          (get
            (first (get ticket "messages")) "is_admin_response")
          false)
        (not (contains? ticket "assignee"))
        )) data)
    longest-wait (reduce (fn [a b]
      (if (> (wait-time a now) (wait-time b now)) a b)
    ) waiting-answer)
    ]
    longest-wait
  )
)

(def page-specs [
  {:id :open-support-tickets
   :data open-unassigned-uv-tickets
   :values (fn [data] {
     :template :measurement-status
     :number data
     :description "Open unassigned support tickets"
     :level (cond
         (< data 5) 0
         (< data 10) 1
         :else 2)
   })
  },
  {:id :open-response-time
   :data open-response-time
   :values (fn [data]
     (let [
       minutes (wait-time data (t/now))
       formatted (str (quot minutes 60) "h " (rem minutes 60) "min")]
       {
       :template :measurement-status
       :number formatted
       :description "Longest wait time for open unassinged ticket"
       :level
         (cond
           (< minutes 720) 0
           (< minutes 1440) 1
           :else 2)
       })
     )
  }
])

(defn pages []
  (map (fn [page]
    ((:values page) ((:data page)))
  ) page-specs)
)

(defn next-page-iterator [page-count]
  (let [i (atom 0)]
    (fn []
      (swap! i inc)
      (mod @i page-count)
    )
  )
)

(def next-page (next-page-iterator (count page-specs)))

(defn select-page [pages]
  (nth (pages) (next-page))
)

(defn process-page [page]
  (let [
    measurement ((:measurement page))
    formatted ((:measurement-format page) measurement)
    level ((:level page) measurement)
  ]
    {
      :measurement formatted
      :description (:description page)
      :level level
    }
  )
)

(defroutes app-routes
  (GET "/api" [] (json/write-str (select-page pages)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
