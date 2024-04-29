(ns florestou.domains.product.service-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [florestou.domains.product.service :as ps]
            [florestou.domains.category.service :as cs]
            [florestou.containers.postgres :refer [pg-fixture clear-pg]]))

(use-fixtures :once pg-fixture)

(deftest product-crud-service
  (with-system [system (test-system-map)]
    (let [service (:product-service system)]
      (testing "creating and retrieving a product"
        (let [_ (clear-pg)
              created-product (ps/create-product! service {:name "Test Product" :price 9.99})
              retrieved-product (ps/get-product-by-id! service (:id created-product))]
          (is (some? (:id created-product)))
          (is (= (:name retrieved-product) (:name created-product)))))

      (testing "retrieving all products"
        (let [_ (clear-pg)
              test-product-1 (ps/create-product! service {:name "Test Product 1" :price 9.99})
              test-product-2 (ps/create-product! service {:name "Test Product 2" :price 9.99})
              all-products (ps/get-all-products! service)]
          (is (= 2 (count all-products)))
          (is (some #(= (:name test-product-1) (:name %)) all-products))
          (is (some #(= (:name test-product-2) (:name %)) all-products))))

      (testing "updating a product"
        (let [_ (clear-pg)
              created-product (ps/create-product! service {:name "Test Product" :price 9.99})
              updated-product-data {:name "Updated Product"}
              _ (ps/update-product-by-id! service (:id created-product) updated-product-data)
              retrieved-product (ps/get-product-by-id! service (:id created-product))]
          (is (not= (:name created-product) (:name retrieved-product)))
          (is (= (:name updated-product-data) (:name retrieved-product)))))

      (testing "deleting a product"
        (let [_ (clear-pg)
              created-product (ps/create-product! service {:name "Test Product" :price 9.99})
              _ (ps/delete-product-by-id! service (:id created-product))
              retrieved-product (ps/get-product-by-id! service (:id created-product))]
          (is (nil? retrieved-product))))

      (testing "retrieving a non-existent product"
        (let [_ (clear-pg)
              non-existent-id 999]
          (is (nil? (ps/get-product-by-id! service non-existent-id)))))

      (testing "updating a non-existent product"
        (let [_ (clear-pg)
              non-existent-id 999
              updated-product-data {:name "Updated Product"}]
          (is (thrown-with-msg? clojure.lang.ExceptionInfo
                                #"Product not found"
                                (ps/update-product-by-id! service non-existent-id updated-product-data)))))

      (testing "deleting a non-existent product"
        (let [_ (clear-pg)
              non-existent-id 999]
          (is (thrown-with-msg? clojure.lang.ExceptionInfo
                                #"Product not found"
                                (ps/delete-product-by-id! service non-existent-id))))))))

(deftest product-category-crud-test
  (with-system [system (test-system-map)]
    (let [product-service (:product-service system)
          category-service (:category-service system)]
      (testing "creating a product-category association"
        (let [_ (clear-pg)
              product-id (:id (ps/create-product! product-service {:name "Test Product" :price 9.99}))
              category-id (:id (cs/create-category! category-service {:name "Test Category"}))
              _ (ps/create-product-category! product-service product-id category-id)
              categories (ps/get-categories-by-product-id! product-service product-id)]
          (is (= 1 (count categories))))

        (testing "deleting a product-category association"
          (let [_ (clear-pg)
                product-id (:id (ps/create-product! product-service {:name "Test Product" :price 9.99}))
                category-id (:id (cs/create-category! category-service {:name "Test Category"}))
                _ (ps/create-product-category! product-service product-id category-id)
                _ (ps/delete-product-category! product-service product-id category-id)
                categories (ps/get-categories-by-product-id! product-service product-id)]
            (is (empty? categories))))

        (testing "getting categories by product ID"
          (let [_ (clear-pg)
                product-id (:id (ps/create-product! product-service {:name "Test Product" :price 9.99}))
                category-ids [(:id (cs/create-category! category-service {:name "Test Category 0"}))
                              (:id (cs/create-category! category-service {:name "Test Category 1"}))
                              (:id (cs/create-category! category-service {:name "Test Category 2"}))]
                _ (doseq [category-id category-ids]
                    (ps/create-product-category! product-service product-id category-id))
                categories (ps/get-categories-by-product-id! product-service product-id)]
            (is (= (count category-ids) (count categories)))))))))
