(ns florestou.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [florestou.helpers :refer [load-config]]
            [florestou.core :refer [base-system system-map]]))

(deftest test-load-config
  (testing "load-config loads the configuration from config.edn"
    (let [config (load-config)]
      (is (map? config))
      (is (contains? config :db-spec)))))

(deftest test-system-map
  (let [config (load-config)]
    (testing "system-map returns a valid system map"
      (let [system (system-map config)]
        (is (instance? com.stuartsierra.component.SystemMap system))
        (is (contains? system :db))))))

(deftest test-base-system
  (testing "base-system returns a valid system map"
    (let [system (base-system)]
      (is (instance? com.stuartsierra.component.SystemMap system))
      (is (contains? system :db)))))
