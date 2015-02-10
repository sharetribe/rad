(ns rad.data-sources.uservoice-tickets
  (:import [com.uservoice Client])
  (:require [clojure.data.json :as json]
            [environ.core :refer [env]]))

(def datasource-spec {
  :id :uservoice-tickets
  :update-every 60000
  :fetch (fn []
    (let [
        api-key (env :uservoice-api-key)
        api-secret (env :uservoice-api-secret)
        client (Client. "sharetribe" api-key api-secret)
        res (.get client "/api/v1/tickets?state=open&per_page=200")
      ]
      (get (json/read-str (.toString res)) "tickets")))
})
