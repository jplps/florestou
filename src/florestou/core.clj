(ns florestou.core
  (:require [com.stuartsierra.component :as component]
            [florestou.helpers :refer [load-config]]
            [florestou.db.core :as db]
            [florestou.domains.product.service :as ps]
            [florestou.domains.category.service :as cs])
  (:gen-class))

(defn system-map [config]
  (component/system-map
   :db (db/new-database (:db-spec config))
   :product-service (component/using (ps/new-service) [:db])
   :category-service (component/using (cs/new-service) [:db])))

(defn base-system []
  (system-map (load-config)))

(defonce system (base-system))

(defn -main [& _args]
  (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
  (alter-var-root #'system component/start)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. #(alter-var-root #'system component/stop))))
