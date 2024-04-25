(ns florestou.db.helpers
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [florestou.helpers :refer [load-config]]))

;; Datasource object created from the loaded configuration.
(defonce datasource (jdbc/get-datasource (:db-spec (load-config))))

(defn insert!
  "Inserts data into the specified table. Returns the number of affected rows."
  [table data]
  (sql/insert! datasource table data))

(defn query!
  "Executes the SQL query and returns the result set as a vector of maps."
  [sql-params]
  (let [results (jdbc/execute! datasource sql-params)]
    (mapv (fn [result]
            (into {} (map (fn [[k v]] [(keyword (name k)) v]) result)))
          results)))

(defn execute!
  "Executes the SQL statement. Returns the number of affected rows."
  [sql-params]
  (jdbc/execute! datasource sql-params))
