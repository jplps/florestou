(ns florestou.helpers
  "Application configuration loading with environment variable overrides."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:gen-class))

(defn load-config
  "Load config from resources/config.edn, overriding with environment variables
  DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, and HTTP_PORT when present."
  []
  (let [config (-> (io/resource "config.edn") slurp edn/read-string)]
    (when-not (and (:db-spec config) (:http config))
      (throw (ex-info "Invalid config: missing :db-spec or :http" {:config config})))
    (-> config
        (update :db-spec merge
                (cond-> {}
                  (System/getenv "DB_HOST")     (assoc :host (System/getenv "DB_HOST"))
                  (System/getenv "DB_PORT")     (assoc :port (parse-long (System/getenv "DB_PORT")))
                  (System/getenv "DB_NAME")     (assoc :dbname (System/getenv "DB_NAME"))
                  (System/getenv "DB_USER")     (assoc :user (System/getenv "DB_USER"))
                  (System/getenv "DB_PASSWORD") (assoc :password (System/getenv "DB_PASSWORD"))))
        (update-in [:http :port] #(or (some-> (System/getenv "HTTP_PORT") parse-long) %)))))
