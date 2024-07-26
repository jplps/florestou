(ns florestou.db.helpers
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [clojure.walk :as walk]
            [florestou.helpers :refer [load-config]])
  (:import [org.postgresql.jdbc PgArray]
           [java.sql Array]))

(defonce datasource (jdbc/get-datasource (:db-spec (load-config))))

(defn pgarray-to-vector [pgarray]
  (vec (.getArray pgarray)))

(defn transform-value [value]
  (cond
    (instance? PgArray value)
    (pgarray-to-vector value)

    (instance? Array value)
    (pgarray-to-vector value)

    (and (vector? value) (= (first value) 'inst))
    (java.util.Date/from (java.time.Instant/parse (second value)))

    (and (keyword? value) (namespace value))
    (keyword (name value))

    :else value))

(defn transform-map [m]
  (walk/postwalk
   (fn [x]
     (if (map? x)
       (into {} (map (fn [[k v]] [(transform-value k) (transform-value v)]) x))
       x))
   m))

(defn transform-result [result]
  (cond
    (map? result) (transform-map result)
    (vector? result) (mapv transform-map result)
    :else result))

(defn insert!
  [table key-map]
  (transform-result (sql/insert! datasource table key-map)))

(defn update!
  [table key-map where-params]
  (transform-map (sql/update! datasource table key-map where-params)))

(defn delete!
  [table where-params]
  (transform-result (sql/delete! datasource table where-params)))

(defn get-by-id!
  [table primary-key]
  (transform-result (sql/get-by-id datasource table primary-key)))

(defn find-by-keys!
  [table key-map]
  (transform-result (sql/find-by-keys datasource table key-map)))

(defn execute!
  [sql-params]
  (transform-result (jdbc/execute! datasource sql-params)))
