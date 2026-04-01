(ns florestou.domains.product.postgres
  "PostgreSQL adapter implementing the ProductPort protocol."
  (:require [florestou.db.helpers :as dbh]
            [florestou.domains.product.port :refer [ProductPort]]
            [clojure.string :refer [join blank?]]))

(def all-products-sql
  "SELECT
       p.id,
       p.name,
       p.description,
       p.price,
       array_agg(DISTINCT cat.name) AS categories
   FROM products p
   LEFT JOIN product_categories pc ON p.id = pc.product_id
   LEFT JOIN categories cat ON pc.category_id = cat.id
   GROUP BY p.id
   ORDER BY p.id;")

(defn products-by-categories-sql
  "Build a parameterized SQL query filtering products by `n` category names."
  [n]
  (let [placeholders (join ", " (repeat n "?"))]
    (str "SELECT
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
          ORDER BY p.id;")))

(def categories-by-product-sql
  "SELECT c.* FROM categories c
   JOIN product_categories pc ON c.id = pc.category_id
   WHERE pc.product_id = ?")

(defrecord ProductRepository [db]
  ProductPort
  (get-product-by-id! [_ id]
    (dbh/get-by-id! (:conn db) :products id))

  (get-all-products! [_]
    (dbh/execute! (:conn db) [all-products-sql]))

  (get-products-by-categories! [_ categories]
    (let [categories (remove blank? categories)]
      (if (empty? categories)
        []
        (dbh/execute! (:conn db)
                      (into [(products-by-categories-sql (count categories))]
                            categories)))))

  (create-product! [_ data]
    (dbh/insert! (:conn db) :products (dissoc data :id)))

  (update-product-by-id! [_ id data]
    (let [result (dbh/update! (:conn db) :products (dissoc data :id) {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Product not found" {:id id})))))

  (delete-product-by-id! [_ id]
    (let [result (dbh/delete! (:conn db) :products {:id id})]
      (if (pos? (:update-count result))
        (assoc result :id id)
        (throw (ex-info "Product not found" {:id id})))))

  (create-product-category! [_ product-id category-id]
    (dbh/insert! (:conn db) :product_categories {:product_id product-id :category_id category-id}))

  (delete-product-category! [_ product-id category-id]
    (dbh/delete! (:conn db) :product_categories {:product_id product-id :category_id category-id}))

  (get-categories-by-product-id! [_ product-id]
    (dbh/execute! (:conn db) [categories-by-product-sql product-id])))

(defn new-repository
  "Create a ProductRepository component. Receives :db via Component injection."
  []
  (map->ProductRepository {}))
