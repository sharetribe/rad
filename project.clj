(defproject rad "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/tools.logging "0.3.1"]
                 [environ "1.0.0"]
                 [overtone/at-at "1.2.0"]
                 [clj-time "0.9.0"]
                 [com.uservoice/uservoice-java "0.0.2"]]
  :plugins [[lein-ring "0.8.13"]]
  :uberjar-name "rad.jar"
  :ring {:handler rad.handler/app :init rad.handler/start-processing}
  :profiles
  {:uberjar {:aot :all}
   :dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
