(ns florestou.db.helpers
  "JDBC convenience wrappers with PostgreSQL-specific result transformation."
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [clojure.walk :as walk])
  (:import [org.postgresql.jdbc PgArray]
           [java.sql Array]))

(defn pgarray-to-vector
  "Convert a PgArray to a Clojure vector."
  [pgarray]
  (vec (.getArray pgarray)))

(defn transform-value
  "Normalize a single result value: PgArray to vector, namespaced keyword to simple keyword."
  [value]
  (cond
    (instance? PgArray value)
    (pgarray-to-vector value)

    (instance? Array value)
    (pgarray-to-vector value)

    (and (keyword? value) (namespace value))
    (keyword (name value))

    :else value))

(defn transform-map
  "Recursively transform all keys and values in a map."
  [m]
  (walk/postwalk
   (fn [x]
     (if (map? x)
       (into {} (map (fn [[k v]] [(transform-value k) (transform-value v)]) x))
       x))
   m))

(defn transform-result
  "Transform a query result (map or vector of maps)."
  [result]
  (cond
    (map? result) (transform-map result)
    (vector? result) (mapv transform-map result)
    :else result))

(defn insert!
  "Insert a row into `table` and return the transformed result."
  [ds table key-map]
  (transform-result (sql/insert! ds table key-map)))

(defn update!
  "Update rows in `table` matching `where-params` and return the transformed result."
  [ds table key-map where-params]
  (transform-map (sql/update! ds table key-map where-params)))

(defn delete!
  "Delete rows from `table` matching `where-params` and return the transformed result."
  [ds table where-params]
  (transform-result (sql/delete! ds table where-params)))

(defn get-by-id!
  "Fetch a single row from `table` by primary key."
  [ds table primary-key]
  (transform-result (sql/get-by-id ds table primary-key)))

(defn find-by-keys!
  "Fetch rows from `table` matching `key-map`."
  [ds table key-map]
  (transform-result (sql/find-by-keys ds table key-map)))

(defn execute!
  "Execute a raw SQL query and return the transformed result."
  [ds sql-params]
  (transform-result (jdbc/execute! ds sql-params)))
