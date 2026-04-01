(ns florestou.test-helpers
  (:require [com.stuartsierra.component :as component]
            [florestou.db.core :as db]
            [florestou.domains.product.postgres :as product-repo]
            [florestou.domains.category.postgres :as category-repo]
            [florestou.http.core :as http-server]
            [florestou.containers.postgres :refer [*test-spec*]]))

(defn test-system-map []
  (component/system-map
   :db (db/new-database *test-spec*)
   :product-service (component/using (product-repo/new-repository) [:db])
   :category-service (component/using (category-repo/new-repository) [:db])
   :http-server (component/using (http-server/new-server 3001) [:product-service :category-service])))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))
