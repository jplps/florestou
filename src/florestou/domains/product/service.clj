(ns florestou.domains.product.service
  (:require [florestou.db.helpers :refer [get-by-id! execute! insert! update! delete!]]
            [clojure.string :refer [join]]))

(defprotocol PSProtocol
  (get-all-products! [this])
  (get-products-by-categories! [this categories])
  (create-product! [this data])
  (get-product-by-id! [this id])
  (update-product-by-id! [this id data])
  (delete-product-by-id! [this id])
  (create-product-category! [this product-id category-id])
  (delete-product-category! [this product-id category-id]))

(defrecord ProductRepository []
  PSProtocol
  (get-product-by-id! [_ id]
    (get-by-id! :products id))

  (get-all-products! [_]
    (execute!
     ["SELECT 
           p.id,
           p.name,
           p.description,
           p.price,
           array_agg(DISTINCT cat.name) AS categories
       FROM products p
       LEFT JOIN product_categories pc ON p.id = pc.product_id
       LEFT JOIN categories cat ON pc.category_id = cat.id
       GROUP BY p.id
       ORDER BY p.id;"]))

  (get-products-by-categories! [_ categories]
    (let [placeholders (join ", " (repeat (count categories) "?"))
          sql-query (str "SELECT
                            p.id,
                            p.name,
                            p.description,
                            p.price,
                            array_agg(DISTINCT cat.name) AS categories
                          FROM products p
                          LEFT JOIN product_categories pc ON p.id = pc.product_id
                          LEFT JOIN categories cat ON pc.category_id = cat.id
                          WHERE 
                            p.id IN (
                              SELECT DISTINCT p.id
                              FROM products p
                              JOIN product_categories pc ON p.id = pc.product_id
                              JOIN categories cat ON pc.category_id = cat.id
                              WHERE cat.name IN (" placeholders ")
                            )
                          GROUP BY p.id
                          ORDER BY p.id;")
          query-params (into [sql-query] categories)]
      (execute! query-params)))

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

  (delete-product-category! [_ product-id category-id]
    (delete! :product_categories {:product_id product-id :category_id category-id})))

(defn new-service []
  (->ProductRepository))
