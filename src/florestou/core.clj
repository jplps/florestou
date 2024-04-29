(ns florestou.core
  (:require [com.stuartsierra.component :as component]
            [florestou.helpers :refer [load-config]]
            [florestou.db.core :as db]
            [florestou.domains.product.service :as ps]
            [florestou.domains.category.service :as cs]
            [florestou.http.core :as hs])
  (:gen-class))

(defn system-map [config]
  (component/system-map
   :db (db/new-database (:db-spec config))
   :product-service (component/using (ps/new-service) [:db])
   :category-service (component/using (cs/new-service) [:db])
   :http-server (component/using (hs/new-server (:port config)) [:product-service :category-service])))

(defn base-system []
  (system-map (load-config)))

(defonce system (base-system))

(defn -main [& _args]
  (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
  (alter-var-root #'system component/start)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. #(alter-var-root #'system component/stop))))

(comment
  ;; Evaluate the do block below to start the system.
  ;; Reevaluate it to restart it quickly.
  (do   (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
        (load-file "src/florestou/core.clj")
        (alter-var-root #'system component/stop)
        (alter-var-root #'system (constantly (base-system)))
        (alter-var-root #'system component/start)
        nil))
