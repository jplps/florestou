(ns florestou.http.pages.product
  "Product page components: cards, list, and showcase page."
  (:require [hiccup.core :refer [html]]
            [florestou.http.pages.common :refer [page-template]]))

(defn label
  "Render a clickable category tag that toggles the corresponding filter."
  [category-name]
  [:a {:class "category-tag border inline-block text-xs text-muted-light cursor-pointer"
       :style "padding: 0.125rem 0.375rem;"
       :onclick (str "selectCategory('" category-name "')")}
   category-name])

(defn product-item
  "Render a single product card with name, categories, description, and price."
  [{:keys [name description price categories]}]
  [:div {:class "product-card flex-col gap-sm items-start border p-md bg-card max-w-card"}
   [:h3 {:class "bold"} name]
   [:div {:class "flex gap-xs flex-wrap"}
    (for [category categories]
      (label category))]
   [:p {:class "flex-1 lh-relaxed text-sm text-muted line-clamp-3"} description]
   [:span {:class "text-lg bold"} (str "R$ " price)]])

(defn product-list-items
  "Render an HTMX-swappable product grid."
  [products]
  (html
   [:div {:id "products-list"}
    [:div {:class "flex flex-wrap gap-xs justify-center"}
     (for [product products]
       (product-item product))]]))

(defn products
  "Render the full products showcase page with HTMX lazy-loading placeholders."
  []
  (page-template
   [:main {:class "flex-col gap-lg p-md"}
    [:div {:id "categories-list"
           :hx-get "/categories/list"
           :hx-trigger "load"
           :hx-target "this"
           :hx-swap "outerHTML"}]
    [:div {:id "products-list"
           :hx-get "/products/list"
           :hx-trigger "load"
           :hx-target "this"
           :hx-swap "outerHTML"}]]))
