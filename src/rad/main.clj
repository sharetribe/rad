(ns rad.main
  (:require [ring.adapter.jetty :as jetty]
            [rad.handler :refer [app start-processing]]))

(defn main []
  (jetty/run-jetty app {:port 3000})
  (start-processing)
)