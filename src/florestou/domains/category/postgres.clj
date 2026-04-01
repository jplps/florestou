(ns florestou.domains.category.postgres
  "PostgreSQL adapter implementing the CategoryPort protocol."
  (:require [florestou.db.helpers :as dbh]
            [florestou.domains.category.port :refer [CategoryPort]]))

(def all-categories-sql
  "SELECT * FROM categories")

(defrecord CategoryRepository [db]
  CategoryPort
  (get-category-by-id! [_ id]
    (dbh/get-by-id! (:conn db) :categories id))

  (get-all-categories! [_]
    (dbh/execute! (:conn db) [all-categories-sql]))

  (create-category! [_ data]
    (dbh/insert! (:conn db) :categories (dissoc data :id)))

  (update-category-by-id! [_ id data]
    (let [result (dbh/update! (:conn db) :categories (dissoc data :id) {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Category not found" {:id id})))))

  (delete-category-by-id! [_ id]
    (let [result (dbh/delete! (:conn db) :categories {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Category not found" {:id id}))))))

(defn new-repository
  "Create a CategoryRepository component. Receives :db via Component injection."
  []
  (map->CategoryRepository {}))
