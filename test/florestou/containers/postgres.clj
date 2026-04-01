(ns florestou.containers.postgres
  (:require [next.jdbc :as jdbc]
            [clj-test-containers.core :as tc]))

(defn create-pg-container []
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
  (let [container (tc/start! (create-pg-container))
        test-spec (pg-test-spec container)
        test-datasource (jdbc/get-datasource test-spec)]
    (binding [*test-datasource* test-datasource
              *test-spec* test-spec]
      (try
        (f)
        (finally
          (tc/stop! container))))))

(defn clear-pg []
  (when *test-datasource*
    (doseq [table ["product_categories" "products" "categories"]]
      (try
        (jdbc/execute-one! *test-datasource* [(str "TRUNCATE TABLE " table " CASCADE")])
        (catch Exception _)))))

(defn clear-pg-fixture [f]
  (clear-pg)
  (f))
