(ns florestou.test-helpers
  (:require [com.stuartsierra.component :as component]
            [florestou.db.core :as db]
            [florestou.domains.product.service :as ps]
            [florestou.domains.category.service :as cs]
            [florestou.http.core :as hs]
            [florestou.containers.postgres :refer [*test-spec*]]))

(defn test-system-map []
  (component/system-map
   :db (db/new-database *test-spec*)
   :product-service (component/using (ps/new-service) [:db])
   :category-service (component/using (cs/new-service) [:db])
   :http-server (component/using (hs/new-server 3001) [:product-service :category-service])))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))
