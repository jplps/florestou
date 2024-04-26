(ns florestou.domains.product.service-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [florestou.domains.product.service :as ps]
            [florestou.containers.postgres :refer [db-fixture clear-db]]))

(use-fixtures :once db-fixture)

(deftest product-service
  (with-system [system (test-system-map)]
    (let [service (:product-service system)]
      (testing "creating and retrieving a product"
        (let [_ (clear-db)
              test-product {:name "Test Product" :price 9.99}
              created-product (ps/create-product! service test-product)
              retrieved-product (ps/get-product-by-id! service (:id created-product))]
          (is (some? (:id created-product)))
          (is (= (:name retrieved-product) (:name created-product)))))

      (testing "retrieving all products"
        (let [_ (clear-db)
              test-product-1 (ps/create-product! service {:name "Test Product 1" :price 9.99})
              test-product-2 (ps/create-product! service {:name "Test Product 2" :price 9.99})
              all-products (ps/get-all-products! service)]
          (is (= 2 (count all-products)))
          (is (some #(= (:name test-product-1) (:name %)) all-products))
          (is (some #(= (:name test-product-2) (:name %)) all-products))))

      (testing "updating a product"
        (let [_ (clear-db)
              test-product {:name "Test Product" :price 9.99}
              created-product (ps/create-product! service test-product)
              updated-product-data {:name "Updated Product"}
              _ (ps/update-product-by-id! service (:id created-product) updated-product-data)
              retrieved-product (ps/get-product-by-id! service (:id created-product))]
          (is (not= (:name created-product) (:name retrieved-product)))
          (is (= (:name updated-product-data) (:name retrieved-product)))))

      (testing "deleting a product"
        (let [_ (clear-db)
              test-product {:name "Test Product" :price 9.99}
              created-product (ps/create-product! service test-product)
              _ (ps/delete-product-by-id! service (:id created-product))
              retrieved-product (ps/get-product-by-id! service (:id created-product))]
          (is (nil? retrieved-product))))

      (testing "retrieving a non-existent product"
        (let [_ (clear-db)
              non-existent-id 999]
          (is (nil? (ps/get-product-by-id! service non-existent-id)))))

      (testing "updating a non-existent product"
        (let [_ (clear-db)
              non-existent-id 999
              updated-product-data {:name "Updated Product"}]
          (is (thrown-with-msg? clojure.lang.ExceptionInfo
                                #"Product not found"
                                (ps/update-product-by-id! service non-existent-id updated-product-data)))))

      (testing "deleting a non-existent product"
        (let [_ (clear-db)
              non-existent-id 999]
          (is (thrown-with-msg? clojure.lang.ExceptionInfo
                                #"Product not found"
                                (ps/delete-product-by-id! service non-existent-id))))))))
