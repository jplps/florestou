(ns florestou.core
  "System bootstrap and entry point."
  (:require [com.stuartsierra.component :as component]
            [florestou.helpers :refer [load-config]]
            [florestou.db.core :as db]
            [florestou.domains.product.postgres :as product-repo]
            [florestou.domains.category.postgres :as category-repo]
            [florestou.http.core :as http-server])
  (:gen-class))

(defn system-map
  "Build the Component system map from the given config."
  [config]
  (component/system-map
   :db (db/new-database (:db-spec config) true)
   :product-service (component/using (product-repo/new-repository) [:db])
   :category-service (component/using (category-repo/new-repository) [:db])
   :http-server (component/using (http-server/new-server (-> config :http :port)) [:product-service :category-service])))

(defn base-system
  "Create a system map from the default configuration."
  []
  (system-map (load-config)))

(defonce system (base-system))

(defn -main
  "Application entry point. Starts the system and registers a shutdown hook."
  [& _args]
  (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
  (alter-var-root #'system component/start)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. #(alter-var-root #'system component/stop))))

(comment
  (do (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
      (load-file "src/florestou/core.clj")
      (alter-var-root #'system component/stop)
      (alter-var-root #'system (constantly (base-system)))
      (alter-var-root #'system component/start)
      nil))
