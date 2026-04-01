(ns florestou.http.core-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.string :as str]
            [florestou.containers.postgres :refer [pg-fixture clear-pg-fixture]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [florestou.domains.product.port :as product]
            [florestou.domains.category.port :as category]
            [clj-http.client :as http-client]))

(use-fixtures :once pg-fixture)
(use-fixtures :each clear-pg-fixture)

(def base-url "http://localhost:3001")

(deftest healthcheck-test
  (with-system [system (test-system-map)]
    (testing "healthcheck returns 200 with JSON"
      (let [response (http-client/get (str base-url "/health"))]
        (is (= 200 (:status response)))
        (is (= "{\"message\":\"Healthy\"}" (:body response)))))))

(deftest index-test
  (with-system [system (test-system-map)]
    (testing "index page returns 200 with HTML"
      (let [response (http-client/get (str base-url "/"))]
        (is (= 200 (:status response)))
        (is (str/includes? (:body response) "Florestou"))
        (is (str/includes? (:body response) "Discover our products."))))))

(deftest products-page-test
  (with-system [system (test-system-map)]
    (testing "products page returns 200 with HTMX attributes"
      (let [response (http-client/get (str base-url "/products"))]
        (is (= 200 (:status response)))
        (is (str/includes? (:body response) "hx-get"))
        (is (str/includes? (:body response) "/products/list"))
        (is (str/includes? (:body response) "/categories/list"))))))

(deftest not-found-test
  (with-system [system (test-system-map)]
    (testing "unknown route returns 404"
      (let [response (http-client/get (str base-url "/nonexistent")
                                      {:throw-exceptions false})]
        (is (= 404 (:status response)))
        (is (str/includes? (:body response) "404 - Not found"))))))

(deftest products-list-test
  (with-system [system (test-system-map)]
    (let [product-service (:product-service system)]

      (testing "empty product list returns 200"
        (let [response (http-client/get (str base-url "/products/list"))]
          (is (= 200 (:status response)))))

      (testing "product list contains created products"
        (product/create-product! product-service {:name "Aloe Vera Gel" :price 14.99})
        (product/create-product! product-service {:name "Rose Water Toner" :price 9.99})
        (let [response (http-client/get (str base-url "/products/list"))]
          (is (= 200 (:status response)))
          (is (str/includes? (:body response) "Aloe Vera Gel"))
          (is (str/includes? (:body response) "Rose Water Toner")))))))

(deftest categories-list-test
  (with-system [system (test-system-map)]
    (let [category-service (:category-service system)]

      (testing "empty category list returns 200"
        (let [response (http-client/get (str base-url "/categories/list"))]
          (is (= 200 (:status response)))))

      (testing "category list contains created categories"
        (category/create-category! category-service {:name "Moisturizers"})
        (category/create-category! category-service {:name "Cleansers"})
        (let [response (http-client/get (str base-url "/categories/list"))]
          (is (= 200 (:status response)))
          (is (str/includes? (:body response) "Moisturizers"))
          (is (str/includes? (:body response) "Cleansers")))))))

(deftest products-list-filter-by-category-test
  (with-system [system (test-system-map)]
    (let [product-service (:product-service system)
          category-service (:category-service system)
          moisturizer-id (:id (category/create-category! category-service {:name "Moisturizer"}))
          cleanser-id (:id (category/create-category! category-service {:name "Cleanser"}))
          serum-id (:id (category/create-category! category-service {:name "Serum"}))
          cream (:id (product/create-product! product-service {:name "Night Cream" :price 29.99}))
          gel (:id (product/create-product! product-service {:name "Cleansing Gel" :price 12.99}))
          serum (:id (product/create-product! product-service {:name "Vitamin C Serum" :price 39.99}))]
      (product/create-product-category! product-service cream moisturizer-id)
      (product/create-product-category! product-service gel cleanser-id)
      (product/create-product-category! product-service serum serum-id)

      (testing "filter by single category"
        (let [response (http-client/get (str base-url "/products/list?categories=Moisturizer"))]
          (is (= 200 (:status response)))
          (is (str/includes? (:body response) "Night Cream"))
          (is (not (str/includes? (:body response) "Cleansing Gel")))
          (is (not (str/includes? (:body response) "Vitamin C Serum")))))

      (testing "filter by multiple categories"
        (let [response (http-client/get (str base-url "/products/list?categories=Moisturizer,Serum"))]
          (is (= 200 (:status response)))
          (is (str/includes? (:body response) "Night Cream"))
          (is (str/includes? (:body response) "Vitamin C Serum"))
          (is (not (str/includes? (:body response) "Cleansing Gel")))))

      (testing "filter by non-existent category returns empty"
        (let [response (http-client/get (str base-url "/products/list?categories=NonExistent"))]
          (is (= 200 (:status response)))
          (is (not (str/includes? (:body response) "Night Cream")))))

      (testing "blank categories param returns all products"
        (let [response (http-client/get (str base-url "/products/list?categories="))]
          (is (= 200 (:status response)))
          (is (str/includes? (:body response) "Night Cream"))
          (is (str/includes? (:body response) "Cleansing Gel"))
          (is (str/includes? (:body response) "Vitamin C Serum")))))))
