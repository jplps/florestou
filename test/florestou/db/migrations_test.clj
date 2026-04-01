(ns florestou.db.migrations-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.containers.postgres :refer [pg-fixture *test-datasource*]]
            [florestou.db.migrations :refer [run-migrations]]
            [next.jdbc :as jdbc]))

(use-fixtures :once pg-fixture)

(deftest migration-idempotency-test
  (testing "running migrations twice does not error"
    (run-migrations *test-datasource*)
    (run-migrations *test-datasource*)
    (let [tables (jdbc/execute! *test-datasource*
                                ["SELECT table_name FROM information_schema.tables
                                  WHERE table_schema = 'public'
                                  ORDER BY table_name"])]
      (is (= #{"categories" "product_categories" "products"}
             (set (map :tables/table_name tables)))))))

(deftest seed-idempotency-test
  (testing "seeding twice does not duplicate data"
    (run-migrations *test-datasource* true)
    (run-migrations *test-datasource* true)
    (let [products (jdbc/execute! *test-datasource* ["SELECT count(*) AS cnt FROM products"])
          categories (jdbc/execute! *test-datasource* ["SELECT count(*) AS cnt FROM categories"])]
      (is (= 20 (:cnt (first products))))
      (is (= 20 (:cnt (first categories)))))))

(deftest seed-data-test
  (testing "seed data populates products, categories, and associations"
    (run-migrations *test-datasource* true)
    (let [assocs (jdbc/execute! *test-datasource* ["SELECT count(*) AS cnt FROM product_categories"])]
      (is (pos? (:cnt (first assocs)))))))
