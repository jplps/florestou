(ns florestou.core-test
  {:clj-kondo/config '{:linters {:unresolved-symbol {:exclude [system]}}}}
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [florestou.helpers :refer [load-config]]
            [florestou.test-helpers :refer [with-system test-system-map]]
            [florestou.containers.postgres :refer [db-fixture]]
            [florestou.core :refer [base-system system-map]]))

(use-fixtures :once db-fixture)

(deftest test-load-config
  (testing "load-config loads the configuration from config.edn"
    (let [config (load-config)]
      (is (map? config))
      (is (contains? config :db-spec))
      (is (contains? config :http)))))

(deftest test-valid-system-map
  (let [config (load-config)]
    (testing "system-map returns a valid system map"
      (let [system (system-map config)]
        (is (instance? com.stuartsierra.component.SystemMap system))
        (is (contains? system :db))
        (is (contains? system :category-service))
        (is (contains? system :product-service))))))

(deftest test-base-system
  (testing "base-system returns a valid system map"
    (let [system (base-system)]
      (is (instance? com.stuartsierra.component.SystemMap system))
      (is (contains? system :db))
      (is (contains? system :category-service))
      (is (contains? system :product-service)))))

(deftest full-system-map
  (with-system [system (test-system-map)]
    (testing "instantiate the full system"
      (is (instance? com.stuartsierra.component.SystemMap system))
      (is (contains? system :db))
      (is (satisfies? component/Lifecycle (:db system)))
      (is (contains? system :category-service))
      (is (satisfies? component/Lifecycle (:category-service system)))
      (is (contains? system :product-service))
      (is (satisfies? component/Lifecycle (:product-service system))))))
