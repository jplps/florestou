(ns florestou.db.migrations-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.db.migrations :refer [run-migrations]]
            [florestou.db.helpers :refer [datasource query! insert!]]
            [clj-test-containers.core :as tc]))

(def db-container
  (tc/create {:image-name "postgres:13"
              :exposed-ports [5432]
              :env-vars {"POSTGRES_DB" "test_db"
                         "POSTGRES_USER" "test_user"
                         "POSTGRES_PASSWORD" "test_password"}
              :wait-for {:wait-strategy :query
                         :startup-timeout 60}}))

(defn db-test-spec [container]
  (let [port (get (:mapped-ports container) 5432)]
    {:dbtype "postgresql"
     :dbname "test_db"
     :host "localhost"
     :port port
     :user "test_user"
     :password "test_password"}))

(defn fixture [f]
  (let [container (tc/start! db-container)]
    (with-redefs [datasource (db-test-spec container)]
      (f))
    (tc/stop! container)))

(use-fixtures :once fixture)

(deftest migrations-test
  (testing "Insert sample data"
    (let [_ (run-migrations)
          _ (insert! :products {:name "Product" :description "Description" :price 9.99})
          _ (insert! :categories {:name "Category"})
          product-id (:id (first (query! ["SELECT id FROM products LIMIT 1"])))
          category-id (:id (first (query! ["SELECT id FROM categories LIMIT 1"])))
          _ (insert! :product_categories {:product_id product-id :category_id category-id})]
      (is (seq (query! ["SELECT * FROM products LIMIT 1"])))
      (is (seq (query! ["SELECT * FROM categories LIMIT 1"])))
      (is (seq (query! ["SELECT * FROM product_categories LIMIT 1"]))))))
