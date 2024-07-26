(ns florestou.http.routes
  (:require [ring.util.response :refer [content-type]]
            [florestou.http.pages :refer [index products product-list-items category-list-items not-found]]
            [clojure.string :refer [split]]))

(defn health-handler [_ _ _]
  (content-type {:body {:message "Healthy"}} "application/json"))

(defn index-handler [_ _ _]
  (content-type {:body index} "text/html"))

(defn products-handler [_ _ _]
  (content-type {:body (products)} "text/html"))

(defn products-list-handler [product-service _ request]
  (let [categories-param (get (:query-params request) "categories")
        products (if (nil? categories-param)
                   (.get-all-products! product-service)
                   (let [categories (vec (split (str categories-param) #","))]
                     (.get-products-by-categories! product-service categories)))]
    (content-type {:body (product-list-items products)} "text/html")))

(defn categories-list-handler [_ category-service _]
  (let [categories (.get-all-categories! category-service)]
    (content-type {:body (category-list-items categories)} "text/html")))

(defn not-found-handler [_ _ _]
  (content-type {:body not-found} "text/html"))

(def api-routes
  {"/health" health-handler
   "/" index-handler
   "/products" products-handler
   "/products/list" products-list-handler
   "/categories/list" categories-list-handler})
