(ns florestou.domains.product.port
  "Port protocol defining product domain operations.")

(defprotocol ProductPort
  "Operations for managing products and their category associations."
  (get-all-products! [this] "Retrieve all products with aggregated category names.")
  (get-products-by-categories! [this categories] "Retrieve products belonging to any of the given category names.")
  (create-product! [this data] "Create a product from the given data map.")
  (get-product-by-id! [this id] "Retrieve a single product by its primary key.")
  (update-product-by-id! [this id data] "Update a product. Throws if not found.")
  (delete-product-by-id! [this id] "Delete a product. Throws if not found.")
  (create-product-category! [this product-id category-id] "Associate a product with a category.")
  (delete-product-category! [this product-id category-id] "Remove a product-category association.")
  (get-categories-by-product-id! [this product-id] "Retrieve all categories for a given product."))
