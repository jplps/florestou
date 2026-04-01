(ns user
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
            [florestou.core :as core]))

(set-refresh-dirs "src")

(defonce system nil)

(defn start
  "Start the system."
  []
  (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
  (alter-var-root #'system (fn [s]
                             (if s
                               (do (println "System already running.") s)
                               (component/start (core/base-system))))))

(defn stop
  "Stop the system gracefully."
  []
  (alter-var-root #'system (fn [s]
                             (when s (component/stop s)))))

(defn halt
  "Stop the system and clear the reference."
  []
  (stop)
  (alter-var-root #'system (constantly nil)))

(defn reset
  "Halt the system, reload changed namespaces, and start fresh."
  []
  (halt)
  (let [result (refresh :after 'user/start)]
    (when (instance? Throwable result)
      (throw result))
    result))
