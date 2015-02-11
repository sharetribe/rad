(ns rad.server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]))

(defrecord Server [port app server]
  component/Lifecycle

  (start [this]
    (if server
      this
      (do
        (println "Starting server. Listening in port: " port)
        (assoc this :server (jetty/run-jetty app {:port port :join? false})))))

  (stop [this]
    (if-not server
      this
      (do
        (println "Stopping server")
        (.stop server)
        (assoc this :server nil)))))

(defn new-server
  ([port app] (map->Server {:port port :app app :server nil}))
  ([port app server] (map->Server {:port port :app app :server server})))
