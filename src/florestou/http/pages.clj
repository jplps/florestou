(ns florestou.http.pages
  (:require [hiccup.page :as page]
            [hiccup.core :as h]))

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
    }
    main { min-height: 60vh; }
    main * { border-radius: 2px; }
    main, header, footer { padding: 0.5rem; }
    h1 { font-size: 116%; }
    h2 { font-size: 112%; }
    h3 { font-size: 108%; }
    ol, ul { list-style: none; }
    a {
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
  (page/html5
   {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title "Florestou"]
    [:style reset-css]
    [:script {:src "https://unpkg.com/htmx.org@1.7.0"}]]
   [:body
    [:header
     [:a {:href "/"}
      [:img {:src "/path/to/logo.png" :alt "Florestou Logo"}]]
     [:nav
      [:ul
       [:li [:a {:href "/products"} "Products"]]]]]

    main

    [:footer
     [:ul
      [:li [:a {:href "mailto:info@florestou.com.br"} "info@florestou.com.br"]]
      [:li [:a {:href "tel:+5548991679817"} "+55 (48) 991679817"]]]]]))

(def index
  (page-template
   [:main
    [:h1 "Florestou"]
    [:p "Discover our products."]

    [:p "Products: Soaps, Shampoos, Creams, Gels, Lotions, Butters, Deodorants, Functional Oils"]
    [:p "Categories: Face, Body, Hair"]
    [:p "Characteristics: Oily, Dry, Mist, Liquid, Solid, Gel, Paste"]
    [:p "Target Demographic: Children, Adults"]]))

(def not-found
  (page-template
   [:main
    [:h1 "404"]
    [:p "Not found"]]))

(defn products []
  (page-template
   [:main
    [:h1 "Products"]
    [:div {:id "products-list"
           :hx-get "http://localhost:3000/products/list"
           :hx-trigger "load"
           :hx-target "this"
           :hx-swap "outerHTML"}]]))

(defn product-item [{:keys [name description price]}]
  [:a {:href "/" :style "display: block; background-color: #f1f1f1; padding: 0.5rem;"}
   [:h2 name]
   [:p description]
   [:span price]])

(defn product-list-items [products]
  (h/html
   [:div {:style "display: flex; flex-direction: column; gap: 0.25rem; padding: 0.5rem 0;"}
    (for [product products]
      (product-item product))]))
