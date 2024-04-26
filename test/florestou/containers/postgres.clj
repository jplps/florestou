(ns florestou.containers.postgres
  (:require [next.jdbc :as jdbc]
            [florestou.db.helpers :refer [datasource]]
            [clj-test-containers.core :as tc]))

(def db-container
  (tc/create
   {:image-name "postgres:13"
    :exposed-ports [5432]
    :env-vars {"POSTGRES_DB" "test_db"
               "POSTGRES_USER" "test_user"
               "POSTGRES_PASSWORD" "test_password"}
    :wait-for {:wait-strategy :query
               :startup-timeout 60}}))

(defn db-test-spec [container]
  (let [port (get (:mapped-ports container) 5432)]
    {:dbtype "postgresql"
     :dbname "test_db"
     :host "localhost"
     :port port
     :user "test_user"
     :password "test_password"}))

(def ^:dynamic *test-datasource* nil)
(def ^:dynamic *test-spec* nil)

(defn db-fixture [f]
  (let [container (tc/start! db-container)
        test-spec (db-test-spec container)
        test-datasource (jdbc/get-datasource test-spec)]
    (with-redefs [datasource test-datasource]
      (binding [*test-datasource* test-datasource
                *test-spec* test-spec]
        (f)))
    (tc/stop! container)))

(defn clear-db []
  (when (.getContainerId (:container db-container))
    (with-open [conn (jdbc/get-connection
                      (assoc
                       (db-test-spec nil)
                       :port (.getMappedPort (:container db-container) 5432)))]
      (doseq [table ["products" "categories" "product_categories"]]
        (jdbc/execute-one! conn [(format "TRUNCATE TABLE %s CASCADE" table)])))))

(defn clear-db-fixtures [f]
  (f)
  (clear-db))
