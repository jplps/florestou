(ns florestou.db.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [florestou.db.core :refer [new-database]]
            [com.stuartsierra.component :as component]))

(def db-test-spec {:dbtype "postgresql"
                   :dbname "test_db"
                   :host "localhost"
                   :port 5432
                   :user "test_user"
                   :password "test_password"})

(deftest database-lifecycle-test
  (testing "Database component lifecycle"
    (let [db (new-database db-test-spec)]
      (testing "Database starts successfully"
        (let [started-db (component/start db)]
          (is (instance? com.zaxxer.hikari.HikariDataSource (:conn started-db)))
          (is (= db-test-spec (:db-spec started-db)))))

      (testing "Database stops successfully"
        (let [started-db (component/start db)
              stopped-db (component/stop started-db)]
          (is (nil? (:conn stopped-db)))
          (is (nil? (:db-spec stopped-db))))))))

(deftest new-database-test
  (testing "Creating a new database"
    (let [db (new-database db-test-spec)]
      (is (instance? florestou.db.core.Database db))
      (is (= db-test-spec (:db-spec db)))
      (is (nil? (:conn db))))))
