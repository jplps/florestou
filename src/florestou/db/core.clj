(ns florestou.db.core
  (:require [com.stuartsierra.component :as component]
            [next.jdbc.connection :as connection]
            [florestou.db.migrations :refer [run-migrations]])
  (:import [com.zaxxer.hikari HikariDataSource]))

(defrecord Database [conn db-spec]
  component/Lifecycle
  (start [this]
    (if (:conn this)
      this
      (let [conn (connection/->pool HikariDataSource db-spec)]
        (run-migrations)
        (assoc this :conn conn))))

  (stop [this]
    (when-let [conn (:conn this)]
      (.close conn)
      (dissoc this :conn :db-spec))))

(defn new-database [db-spec]
  (map->Database {:db-spec db-spec :conn nil}))
