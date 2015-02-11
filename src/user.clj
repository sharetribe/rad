(ns user
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer (refresh)]
            [rad.rad-system :as system]
            [rad.handler :refer (app)]))

(def system nil)

(defn init []
  (alter-var-root #'system
                  (constantly (system/rad-system {:port 3030 :app app}))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
                  (fn [s]
                    (println s)
                    (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

