(ns florestou.domains.category.service-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [florestou.domains.category.port :as category]
            [florestou.containers.postgres :refer [pg-fixture clear-pg-fixture]]))

(use-fixtures :once pg-fixture)
(use-fixtures :each clear-pg-fixture)

(deftest category-crud-service
  (with-system [system (test-system-map)]
    (let [service (:category-service system)]
      (testing "creating and retrieving a category"
        (let [test-category {:name "Test Category"}
              created-category (category/create-category! service test-category)
              retrieved-category (category/get-category-by-id! service (:id created-category))]
          (is (some? (:id created-category)))
          (is (= (:name retrieved-category) (:name created-category)))))

      (testing "retrieving all categories"
        (let [test-category-1 (category/create-category! service {:name "Test Category 1"})
              test-category-2 (category/create-category! service {:name "Test Category 2"})
              all-categories (category/get-all-categories! service)]
          (is (some #(= (:name test-category-1) (:name %)) all-categories))
          (is (some #(= (:name test-category-2) (:name %)) all-categories))))

      (testing "updating a category"
        (let [test-category {:name "Test Category"}
              created-category (category/create-category! service test-category)
              updated-category-data {:name "Updated Category"}
              _ (category/update-category-by-id! service (:id created-category) updated-category-data)
              retrieved-category (category/get-category-by-id! service (:id created-category))]
          (is (not= (:name created-category) (:name retrieved-category)))
          (is (= (:name updated-category-data) (:name retrieved-category)))))

      (testing "deleting a category"
        (let [test-category {:name "Test Category"}
              created-category (category/create-category! service test-category)
              _ (category/delete-category-by-id! service (:id created-category))
              retrieved-category (category/get-category-by-id! service (:id created-category))]
          (is (nil? retrieved-category))))

      (testing "retrieving a non-existent category"
        (is (nil? (category/get-category-by-id! service 999))))

      (testing "updating a non-existent category"
        (is (thrown-with-msg? clojure.lang.ExceptionInfo
                              #"Category not found"
                              (category/update-category-by-id! service 999 {:name "Updated Category"}))))

      (testing "deleting a non-existent category"
        (is (thrown-with-msg? clojure.lang.ExceptionInfo
                              #"Category not found"
                              (category/delete-category-by-id! service 999)))))))
