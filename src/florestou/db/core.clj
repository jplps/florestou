(ns florestou.db.core
  "Database component with HikariCP connection pooling."
  (:require [com.stuartsierra.component :as component]
            [next.jdbc.connection :as connection]
            [florestou.db.migrations :refer [run-migrations]])
  (:import [com.zaxxer.hikari HikariDataSource]))

(defn- pool-spec
  "Convert a db-spec map to HikariCP-compatible pool configuration."
  [db-spec]
  {:jdbcUrl (connection/jdbc-url db-spec)
   :username (:user db-spec)
   :password (:password db-spec)})

(defrecord Database [conn db-spec seed?]
  component/Lifecycle
  (start [this]
    (if (:conn this)
      this
      (let [conn (connection/->pool HikariDataSource (pool-spec db-spec))]
        (run-migrations conn (boolean seed?))
        (assoc this :conn conn))))

  (stop [this]
    (when-let [conn (:conn this)]
      (.close conn))
    (assoc this :conn nil)))

(defn new-database
  "Create a Database component. When `seed?` is true, dummy data is inserted after migrations."
  ([db-spec] (new-database db-spec false))
  ([db-spec seed?]
   (map->Database {:db-spec db-spec :conn nil :seed? seed?})))
