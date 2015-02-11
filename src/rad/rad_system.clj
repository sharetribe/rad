(ns rad.rad-system
  (:require [com.stuartsierra.component :as component]
            [rad.server :refer [new-server]]
            [rad.handler :refer (app)]))

(defn rad-system [config-options]
  (let [{:keys [port api-key api-secret]} config-options]
    (component/system-map
     :server (new-server port app))))
