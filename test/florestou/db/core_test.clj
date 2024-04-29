(ns florestou.db.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [florestou.db.core :refer [new-database]]
            [florestou.containers.postgres :refer [pg-fixture *test-spec*]]
            [com.stuartsierra.component :as component]))

(use-fixtures :once pg-fixture)

(deftest database-lifecycle-test
  (testing "database component lifecycle"
    (let [db (new-database *test-spec*)]
      (testing "Database starts successfully"
        (let [started-db (component/start db)]
          (is (instance? com.zaxxer.hikari.HikariDataSource (:conn started-db)))
          (is (= *test-spec* (:db-spec started-db)))))

      (testing "database stops successfully"
        (let [started-db (component/start db)
              stopped-db (component/stop started-db)]
          (is (nil? (:conn stopped-db)))
          (is (nil? (:db-spec stopped-db))))))))

(deftest new-database-test
  (testing "creating a new database"
    (let [db (new-database *test-spec*)]
      (is (instance? florestou.db.core.Database db))
      (is (= *test-spec* (:db-spec db)))
      (is (nil? (:conn db))))))
