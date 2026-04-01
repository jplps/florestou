(ns florestou.http.routes
  "HTTP route definitions and handler functions."
  (:require [ring.util.response :refer [content-type]]
            [florestou.http.pages.home :refer [index not-found]]
            [florestou.http.pages.product :refer [products product-list-items]]
            [florestou.http.pages.category :refer [category-list-items]]
            [clojure.string :refer [split blank?]]))

(defn health-handler
  "Return a JSON health check response."
  [_ _ _]
  (content-type {:status 200 :body {:message "Healthy"}} "application/json"))

(defn index-handler
  "Serve the homepage."
  [_ _ _]
  (content-type {:status 200 :body index} "text/html"))

(defn products-handler
  "Serve the products showcase page."
  [_ _ _]
  (content-type {:status 200 :body (products)} "text/html"))

(defn products-list-handler
  "Return product cards, optionally filtered by a comma-separated categories query param."
  [product-service _ request]
  (let [categories-param (get (:query-params request) "categories")
        products (if (or (nil? categories-param) (blank? categories-param))
                   (.get-all-products! product-service)
                   (let [categories (vec (split categories-param #","))]
                     (.get-products-by-categories! product-service categories)))]
    (content-type {:status 200 :body (product-list-items products)} "text/html")))

(defn categories-list-handler
  "Return category filter checkboxes."
  [_ category-service _]
  (let [categories (.get-all-categories! category-service)]
    (content-type {:status 200 :body (category-list-items categories)} "text/html")))

(defn not-found-handler
  "Return a 404 page."
  [_ _ _]
  (content-type {:status 404 :body not-found} "text/html"))

(def api-routes
  {"/health" health-handler
   "/" index-handler
   "/products" products-handler
   "/products/list" products-list-handler
   "/categories/list" categories-list-handler})
