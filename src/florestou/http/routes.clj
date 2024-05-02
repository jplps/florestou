(ns florestou.http.routes
  (:require [ring.util.response :refer [content-type]]
            [florestou.http.pages :refer [index products product-list-items not-found]]))

(defn health-handler [_ _]
  (content-type {:body {:message "Healthy"}} "application/json"))

(defn index-handler [_ _]
  (content-type {:body index} "text/html"))

(defn products-handler [_ _]
  (content-type {:body (products)} "text/html"))

(defn products-list-handler [product-service _]
  (let [products (.get-all-products! product-service)]
    (content-type {:body (product-list-items products)} "text/html")))

(defn not-found-handler [_ _]
  (content-type {:body not-found} "text/html"))

(def api-routes
  {"/health" health-handler
   "/" index-handler
   "/products" products-handler
   "/products/list" products-list-handler})
