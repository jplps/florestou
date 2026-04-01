(ns florestou.helpers-test
  (:require [clojure.test :refer [deftest is testing]]
            [florestou.helpers :refer [load-config]]))

(deftest load-config-test
  (testing "loads config with required keys"
    (let [config (load-config)]
      (is (map? config))
      (is (contains? config :db-spec))
      (is (contains? config :http))
      (is (string? (get-in config [:db-spec :host])))
      (is (number? (get-in config [:db-spec :port])))
      (is (number? (get-in config [:http :port]))))))

(deftest default-values-test
  (testing "default config values when env vars are absent"
    (let [config (load-config)]
      (is (= "localhost" (get-in config [:db-spec :host])))
      (is (= 5432 (get-in config [:db-spec :port])))
      (is (= 3000 (get-in config [:http :port]))))))
