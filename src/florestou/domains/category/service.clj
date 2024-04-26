(ns florestou.domains.category.service
  (:require [florestou.db.helpers :refer [insert! get-by-id! execute! update! delete!]]))

(defprotocol CSProtocol
  (get-all-categories! [this])
  (create-category! [this data])
  (get-category-by-id! [this id])
  (update-category-by-id! [this id data])
  (delete-category-by-id! [this id]))

(defrecord CategoryService []
  CSProtocol
  (get-category-by-id! [_ id]
    (get-by-id! :categories id))

  (get-all-categories! [_]
    (execute! ["SELECT * FROM categories"]))

  (create-category! [_ data]
    (insert! :categories (dissoc data :id)))

  (update-category-by-id! [_ id data]
    (let [result (update! :categories (dissoc data :id) {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Category not found" {:id id})))))

  (delete-category-by-id! [_ id]
    (let [result (delete! :categories {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Category not found" {:id id}))))))

(defn new-service []
  (->CategoryService))
