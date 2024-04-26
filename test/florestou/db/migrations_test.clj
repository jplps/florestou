(ns florestou.db.migrations-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.containers.postgres :refer [db-fixture  *test-datasource*]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(use-fixtures :once db-fixture)

(deftest migration-test
  (testing "insert sample data"
    (let [_ (jdbc/execute! *test-datasource*
                           ["CREATE TABLE IF NOT EXISTS samples (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                            )"])
          _ (sql/insert! *test-datasource* :samples {:name "sample name"})
          generic-doc (jdbc/execute! *test-datasource* ["SELECT * FROM samples LIMIT 1"])]
      (is (seq generic-doc))
      (is (= 1 (get-in (first generic-doc) [:samples/id]))))))
