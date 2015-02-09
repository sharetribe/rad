(ns rad.handler
  (:import [com.uservoice Client])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.tools.logging :as log]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [environ.core :refer [env]]
            [clojure.data.json :as json]))

(use 'overtone.at-at)

(def my-pool (mk-pool))

(defn get-uv-tickets []
  (let [
    api-key (env :uservoice-api-key)
    api-secret (env :uservoice-api-secret)
    client (Client. "sharetribe" api-key api-secret)
    res (.get client "/api/v1/tickets?state=open&per_page=100&assignee_id=none")
    ]
    (get (json/read-str (.toString res)) "tickets")))

(defn process-page-specs [specs]
  (map (fn [spec]
    (let [current (atom {})]
      (every (:every spec) (fn []
        (log/info "Processing page" (:id spec))
        (swap! current (fn [x] ((:values spec) ((:api spec))))))
        my-pool)
      (fn [] @current)))
    specs)
)

(def page-specs [
  {:id :open-support-tickets
   :every 60000
   :api (fn [] (count (get-uv-tickets)))
   :values (fn [data] {
     :template :measurement-status
     :number data
     :description "Open unassigned support tickets"
     :level (cond
         (< data 5) 0
         (< data 10) 1
         :else 2)
   })
  }
])

(def pages (process-page-specs page-specs))

(defn select-page [pages]
  ((first pages))
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
