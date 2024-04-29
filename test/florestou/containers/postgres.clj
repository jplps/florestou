(ns florestou.containers.postgres
  (:require [next.jdbc :as jdbc]
            [florestou.db.helpers :refer [datasource]]
            [clj-test-containers.core :as tc]))

(def pg-container
  (tc/create
   {:image-name "postgres:13"
    :exposed-ports [5432]
    :env-vars {"POSTGRES_DB" "test_db"
               "POSTGRES_USER" "test_user"
               "POSTGRES_PASSWORD" "test_password"}
    :wait-for {:wait-strategy :query
               :startup-timeout 60}}))

(defn pg-test-spec [container]
  (let [port (get (:mapped-ports container) 5432)]
    {:dbtype "postgresql"
     :dbname "test_db"
     :host "localhost"
     :port port
     :user "test_user"
     :password "test_password"}))

(def ^:dynamic *test-datasource* nil)
(def ^:dynamic *test-spec* nil)

(defn pg-fixture [f]
  (let [container (tc/start! pg-container)
        test-spec (pg-test-spec container)
        test-datasource (jdbc/get-datasource test-spec)]
    (with-redefs [datasource test-datasource]
      (binding [*test-datasource* test-datasource
                *test-spec* test-spec]
        (f)))
    (tc/stop! container)))

(defn clear-pg []
  (when (.getContainerId (:container pg-container))
    (with-open [conn (jdbc/get-connection
                      (assoc
                       (pg-test-spec nil)
                       :port (.getMappedPort (:container pg-container) 5432)))]
      (doseq [table ["products" "categories" "product_categories"]]
        (jdbc/execute-one! conn [(format "TRUNCATE TABLE %s CASCADE" table)])))))

(defn clear-pg-fixtures [f]
  (f)
  (clear-pg))
