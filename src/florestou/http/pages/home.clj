(ns florestou.http.pages.home
  (:require [florestou.http.pages.common :refer [page-template]]))

(def index
  (page-template
   [:main
    [:p "Discover our products."]]))

(def not-found
  (page-template
   [:main
    [:p "404 - Not found"]]))
