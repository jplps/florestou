(ns florestou.domains.product.service-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [florestou.domains.product.port :as product]
            [florestou.domains.category.port :as category]
            [florestou.containers.postgres :refer [pg-fixture clear-pg-fixture]]))

(use-fixtures :once pg-fixture)
(use-fixtures :each clear-pg-fixture)

(deftest product-crud-service
  (with-system [system (test-system-map)]
    (let [service (:product-service system)]
      (testing "creating and retrieving a product"
        (let [created-product (product/create-product! service {:name "Test Product" :price 9.99})
              retrieved-product (product/get-product-by-id! service (:id created-product))]
          (is (some? (:id created-product)))
          (is (= (:name retrieved-product) (:name created-product)))))

      (testing "retrieving all products"
        (let [test-product-1 (product/create-product! service {:name "Test Product 1" :price 9.99})
              test-product-2 (product/create-product! service {:name "Test Product 2" :price 9.99})
              all-products (product/get-all-products! service)]
          (is (some #(= (:name test-product-1) (:name %)) all-products))
          (is (some #(= (:name test-product-2) (:name %)) all-products))))

      (testing "updating a product"
        (let [created-product (product/create-product! service {:name "Test Product" :price 9.99})
              updated-product-data {:name "Updated Product"}
              _ (product/update-product-by-id! service (:id created-product) updated-product-data)
              retrieved-product (product/get-product-by-id! service (:id created-product))]
          (is (not= (:name created-product) (:name retrieved-product)))
          (is (= (:name updated-product-data) (:name retrieved-product)))))

      (testing "deleting a product"
        (let [created-product (product/create-product! service {:name "Test Product" :price 9.99})
              _ (product/delete-product-by-id! service (:id created-product))
              retrieved-product (product/get-product-by-id! service (:id created-product))]
          (is (nil? retrieved-product))))

      (testing "retrieving a non-existent product"
        (is (nil? (product/get-product-by-id! service 999))))

      (testing "updating a non-existent product"
        (is (thrown-with-msg? clojure.lang.ExceptionInfo
                              #"Product not found"
                              (product/update-product-by-id! service 999 {:name "Updated Product"}))))

      (testing "deleting a non-existent product"
        (is (thrown-with-msg? clojure.lang.ExceptionInfo
                              #"Product not found"
                              (product/delete-product-by-id! service 999)))))))

(deftest product-category-crud-test
  (with-system [system (test-system-map)]
    (let [product-service (:product-service system)
          category-service (:category-service system)]
      (testing "creating a product-category association"
        (let [product-id (:id (product/create-product! product-service {:name "Test Product" :price 9.99}))
              category-id (:id (category/create-category! category-service {:name "Test Category"}))
              _ (product/create-product-category! product-service product-id category-id)
              categories (product/get-categories-by-product-id! product-service product-id)]
          (is (= 1 (count categories)))))

      (testing "deleting a product-category association"
        (let [product-id (:id (product/create-product! product-service {:name "Test Product" :price 9.99}))
              category-id (:id (category/create-category! category-service {:name "Test Category"}))
              _ (product/create-product-category! product-service product-id category-id)
              _ (product/delete-product-category! product-service product-id category-id)
              categories (product/get-categories-by-product-id! product-service product-id)]
          (is (empty? categories))))

      (testing "getting categories by product ID"
        (let [product-id (:id (product/create-product! product-service {:name "Test Product" :price 9.99}))
              category-ids [(:id (category/create-category! category-service {:name "Test Category 0"}))
                            (:id (category/create-category! category-service {:name "Test Category 1"}))
                            (:id (category/create-category! category-service {:name "Test Category 2"}))]
              _ (doseq [category-id category-ids]
                  (product/create-product-category! product-service product-id category-id))
              categories (product/get-categories-by-product-id! product-service product-id)]
          (is (= (count category-ids) (count categories))))))))

(deftest get-products-by-categories-test
  (with-system [system (test-system-map)]
    (let [product-service (:product-service system)
          category-service (:category-service system)
          skincare-id (:id (category/create-category! category-service {:name "Skincare"}))
          haircare-id (:id (category/create-category! category-service {:name "Haircare"}))
          suncare-id (:id (category/create-category! category-service {:name "Sun Protection"}))
          cream-id (:id (product/create-product! product-service {:name "Face Cream" :price 25.00}))
          shampoo-id (:id (product/create-product! product-service {:name "Shampoo" :price 12.00}))
          sunscreen-id (:id (product/create-product! product-service {:name "Sunscreen" :price 18.00}))]
      (product/create-product-category! product-service cream-id skincare-id)
      (product/create-product-category! product-service shampoo-id haircare-id)
      (product/create-product-category! product-service sunscreen-id suncare-id)
      (product/create-product-category! product-service sunscreen-id skincare-id)

      (testing "filter by single category"
        (let [results (product/get-products-by-categories! product-service ["Haircare"])]
          (is (= 1 (count results)))
          (is (= "Shampoo" (:name (first results))))))

      (testing "filter by multiple categories"
        (let [results (product/get-products-by-categories! product-service ["Skincare" "Haircare"])]
          (is (= 3 (count results)))))

      (testing "product in queried category includes all its categories"
        (let [results (product/get-products-by-categories! product-service ["Sun Protection"])
              sunscreen (first results)]
          (is (= 1 (count results)))
          (is (= "Sunscreen" (:name sunscreen)))
          (is (= 2 (count (:categories sunscreen))))))

      (testing "non-existent category returns empty"
        (is (empty? (product/get-products-by-categories! product-service ["Nonexistent"]))))

      (testing "empty categories list returns empty"
        (is (empty? (product/get-products-by-categories! product-service []))))

      (testing "blank strings in categories are ignored"
        (is (empty? (product/get-products-by-categories! product-service ["" " "])))))))
