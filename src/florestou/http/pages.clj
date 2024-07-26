(ns florestou.http.pages
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]))

(def company "Florestou")
(def email "info@florestou.com.br")
(def phone "+5548991679817")

(def reset-css
  "html, body, div, span, applet, object, iframe,
    h1, h2, h3, h4, h5, h6, p, blockquote, pre,
    a, abbr, acronym, address, big, cite, code,
    del, dfn, em, img, ins, kbd, q, s, samp,
    small, strike, strong, sub, sup, tt, var,
    b, u, i, center,
    dl, dt, dd, ol, ul, li,
    fieldset, form, label, legend,
    table, caption, tbody, tfoot, thead, tr, th, td,
    article, aside, canvas, details, embed, 
    figure, figcaption, footer, header, hgroup, 
    menu, nav, output, ruby, section, summary,
    time, mark, audio, video {
      margin: 0;
      padding: 0;
      border: 0;
      font-size: 100%;
      font: inherit;
      vertical-align: baseline;
    }
    article, aside, details, figcaption, figure, 
    footer, header, hgroup, menu, nav, section {
      display: block;
    }
    body {
      line-height: 1;
      max-width: 100vw;
      min-height: 100vh;
      background-color: #e3d4bb;
    }
    main { min-height: 60vh; }
    main * { border-radius: 2px; }
    main, header, footer { padding: 0.5rem; }
    h1 { font-size: 116%; }
    h2 { font-size: 112%; }
    h3 { font-size: 108%; }
    ol, ul { list-style: none; }
    a {
      cursor: pointer;
      color: inherit;
      text-decoration: none;
    }
    blockquote, q { quotes: none; }
    blockquote:before, blockquote:after,
    q:before, q:after {
      content: '';
      content: none;
    }
    table {
      border-collapse: collapse;
      border-spacing: 0;
    }")

(defn page-template [main]
  (html5
   {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title company]
    [:style reset-css]
    [:script {:src "https://unpkg.com/htmx.org@1.7.0"}]]
   [:body
    [:header
     [:a {:href "/"}
      [:img {:src "/assets/img/logo.png" :alt (str company " logo")}]]
     [:nav
      [:ul
       [:li [:a {:href "/products"} "Products"]]]]]

    main

    [:footer
     [:ul
      [:li [:a {:href (str "mailto:" email)} email]]
      [:li [:a {:href (str "tel:" phone)} phone]]]]]))

(def index
  (page-template
   [:main
    [:p "Discover our products."]]))

(def not-found
  (page-template
   [:main
    [:p "404 - Not found"]]))

(defn products []
  (page-template
   [:main
    [:div {:id "categories-list"
           :hx-get "http://localhost:3000/categories/list"
           :hx-trigger "load"
           :hx-target "this"
           :hx-swap "outterHTML"}]
    [:div {:id "products-list"
           :hx-get "http://localhost:3000/products/list"
           :hx-trigger "load"
           :hx-target "this"
           :hx-swap "outterHTML"}]]))

(defn label [label]
  [:a {:style "border: 1px solid #a1a1a1; padding: 0.25rem 0.5rem;"
       :hx-get (str "http://localhost:3000/products/list?categories=" label)
       :hx-trigger "click"
       :hx-target "#products-list"
       :hx-swap "outterHTML"}
   label])

(defn product-item [{:keys [name description price categories]}]
  [:div {:style "display: flex; flex-direction: column; gap:0.5rem; align-items: start; border: 1px solid #c1c1c1; padding: 0.5rem;"}
   [:a {:href "/"} name]
   [:p description]
   [:span price]
   [:div {:style "display: flex; gap: 0.5rem; flex-wrap: wrap;"}
    (for [category categories]
      (label category))]])

(defn product-list-items [products]
  (html
   [:div {:style "display: flex; flex-direction: column; gap: 0.25rem; padding: 0.5rem 0;"}
    (for [product products]
      (product-item product))]))

(defn category-list-items [categories]
  (html
   [:div
    [:div {:style "display: flex; flex-wrap: wrap; gap: 0.25rem; padding: 0.5rem 0;"
           :id "category-form"
           :hx-target "#products-list"}
     (for [category categories]
       (let [category-name (:name category)]
         [:div {:style "border: 1px solid #c1c1c1; padding: 0.25rem 0.5rem;"}
          [:input {:style "display: none;"
                   :type "checkbox"
                   :name "categories"
                   :value category-name
                   :id category-name}]
          [:label {:for category-name} category-name]]))]
    [:script "" "
document.addEventListener('click', function(e) {
    if (e.target.matches('#category-form input[type=\"checkbox\"]')) {
        let form = document.getElementById('category-form');
        let selectedCategories = Array.from(form.querySelectorAll('input[type=\"checkbox\"]:checked')).map(checkbox => checkbox.value);
        let queryString = selectedCategories.length > 0 ? '?categories=' + selectedCategories.join(',') : '';
        form.setAttribute('hx-get', `http://localhost:3000/products/list${queryString}`);
        htmx.ajax('GET', form.getAttribute(\"hx-get\"), form.getAttribute(\"hx-target\"));
        if (e.target.checked) {
            e.target.parentElement.style.backgroundColor = '#c1c1c1';
        } else {
            e.target.parentElement.style.backgroundColor = 'unset';
        }
    }
});
  " ""]]))
