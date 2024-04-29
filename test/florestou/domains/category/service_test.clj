(ns florestou.domains.category.service-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [florestou.domains.category.service :as cs]
            [florestou.containers.postgres :refer [pg-fixture clear-pg]]))

(use-fixtures :once pg-fixture)

(deftest category-crud-service
  (with-system [system (test-system-map)]
    (let [service (:category-service system)]
      (testing "creating and retrieving a category"
        (let [_ (clear-pg)
              test-category {:name "Test Category"}
              created-category (cs/create-category! service test-category)
              retrieved-category (cs/get-category-by-id! service (:id created-category))]
          (is (some? (:id created-category)))
          (is (= (:name retrieved-category) (:name created-category)))))

      (testing "retrieving all categories"
        (let [_ (clear-pg)
              test-category-1 (cs/create-category! service {:name "Test Category 1"})
              test-category-2 (cs/create-category! service {:name "Test Category 2"})
              all-categories (cs/get-all-categories! service)]
          (is (= 2 (count all-categories)))
          (is (some #(= (:name test-category-1) (:name %)) all-categories))
          (is (some #(= (:name test-category-2) (:name %)) all-categories))))

      (testing "updating a category"
        (let [_ (clear-pg)
              test-category {:name "Test Category"}
              created-category (cs/create-category! service test-category)
              updated-category-data {:name "Updated Category"}
              _ (cs/update-category-by-id! service (:id created-category) updated-category-data)
              retrieved-category (cs/get-category-by-id! service (:id created-category))]
          (is (not= (:name created-category) (:name retrieved-category)))
          (is (= (:name updated-category-data) (:name retrieved-category)))))

      (testing "deleting a category"
        (let [_ (clear-pg)
              test-category {:name "Test Category"}
              created-category (cs/create-category! service test-category)
              _ (cs/delete-category-by-id! service (:id created-category))
              retrieved-category (cs/get-category-by-id! service (:id created-category))]
          (is (nil? retrieved-category))))

      (testing "retrieving a non-existent category"
        (let [_ (clear-pg)
              non-existent-id 999]
          (is (nil? (cs/get-category-by-id! service non-existent-id)))))

      (testing "updating a non-existent category"
        (let [_ (clear-pg)
              non-existent-id 999
              updated-category-data {:name "Updated Category"}]
          (is (thrown-with-msg? clojure.lang.ExceptionInfo
                                #"Category not found"
                                (cs/update-category-by-id! service non-existent-id updated-category-data)))))

      (testing "deleting a non-existent category"
        (let [_ (clear-pg)
              non-existent-id 999]
          (is (thrown-with-msg? clojure.lang.ExceptionInfo
                                #"Category not found"
                                (cs/delete-category-by-id! service non-existent-id))))))))
