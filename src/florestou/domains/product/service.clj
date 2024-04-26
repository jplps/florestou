(ns florestou.domains.product.service
  (:require [florestou.db.helpers :refer [get-by-id! execute! insert! update! delete!]]))

(defprotocol ServiceProtocol
  (get-all-products! [this])
  (create-product! [this product])
  (get-product-by-id! [this id])
  (update-product-by-id! [this id product])
  (delete-product-by-id! [this id])
  (create-product-category! [this product-id category-id])
  (get-product-categories! [this product-id]))

(defrecord ProductRepository []
  ServiceProtocol
  (get-product-by-id! [_ id]
    (get-by-id! :products id))

  (get-all-products! [_]
    (execute! ["SELECT * FROM products"]))

  (create-product! [_ data]
    (insert! :products (dissoc data :id)))

  (update-product-by-id! [_ id data]
    (let [result (update! :products (dissoc data :id) {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Product not found" {:id id})))))

  (delete-product-by-id! [_ id]
    (let [result (delete! :products {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Product not found" {:id id})))))

  (create-product-category! [_ product-id category-id]
    (insert! :product_categories {:product_id product-id :category_id category-id}))

  (get-product-categories! [_ product-id]
    (execute! ["SELECT c.* FROM categories c
                JOIN product_categories pc ON c.id = pc.category_id
                WHERE pc.product_id = ?" product-id])))

(defn new-service []
  (->ProductRepository))
