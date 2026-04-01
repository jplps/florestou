(ns florestou.domains.category.port
  "Port protocol defining category domain operations.")

(defprotocol CategoryPort
  "Operations for managing product categories."
  (get-all-categories! [this] "Retrieve all categories.")
  (create-category! [this data] "Create a category from the given data map.")
  (get-category-by-id! [this id] "Retrieve a single category by its primary key.")
  (update-category-by-id! [this id data] "Update a category. Throws if not found.")
  (delete-category-by-id! [this id] "Delete a category. Throws if not found."))
