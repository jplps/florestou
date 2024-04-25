(ns florestou.core
  (:require [com.stuartsierra.component :as component]
            [florestou.helpers :refer [load-config]]
            [florestou.db.core :as db])
  (:gen-class))

(defn system-map [config]
  (component/system-map
   :db (db/new-database (:db-spec config))))

(defn base-system []
  (system-map (load-config)))

(defonce system (base-system))

(defn -main [& _args]
  (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
  (alter-var-root #'system component/start)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. #(alter-var-root #'system component/stop))))
