(ns rad.main
  (:require [ring.adapter.jetty :as jetty]
            [rad.handler :refer [app start-processing]]))

(defn main []
  (jetty/run-jetty app {:port 3000 :join? false})
  (start-processing)
)

(def server nil)
(defn start []
  (alter-var-root #'server (fn [_] (jetty/run-jetty app {:port 3030 :join? false}))))
(defn stop []
  (when (some? server)
    (.stop server)
    (alter-var-root #'server (constantly nil))))

(comment
  (start)
  (stop)
  )


