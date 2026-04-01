(ns florestou.http.pages.common
  "Shared CSS, site info, and page layout template."
  (:require [hiccup.page :refer [html5]]))

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
    blockquote, q { quotes: none; }
    blockquote:before, blockquote:after,
    q:before, q:after {
      content: '';
      content: none;
    }
    table {
      border-collapse: collapse;
      border-spacing: 0;
    }
    ol, ul { list-style: none; }
    a {
      cursor: pointer;
      color: inherit;
      text-decoration: none;
    }")

(def base-css
  "body {
      font-family: sans-serif;
      font-size: 1rem;
      line-height: 1;
      max-width: 100vw;
      min-height: 100vh;
      background-color: #ede4d4;
      color: #2c2416;
    }
    main { min-height: 60vh; }
    main * { border-radius: 0.5rem; }
    main, header, footer { padding: 0.5rem; }
    h1 { font-size: 116%; }
    h2 { font-size: 112%; }
    h3 { font-size: 108%; }")

(def component-css
  ".flex { display: flex; }
    .flex-col { display: flex; flex-direction: column; }
    .flex-wrap { flex-wrap: wrap; }
    .gap-xs { gap: 0.25rem; }
    .gap-sm { gap: 0.5rem; }
    .gap-md { gap: 1rem; }
    .gap-lg { gap: 2rem; }
    .items-center { align-items: center; }
    .items-start { align-items: start; }
    .justify-between { justify-content: space-between; }
    .border { border: 1px solid #96815e; }
    .p-sm { padding: 0.5rem; }
    .p-md { padding: 0.75rem; }
    .m-sm { margin: 0.5rem; }
    .bold { font-weight: bold; }
    .text-sm { font-size: 0.875rem; }
    .text-xs { font-size: 0.75rem; }
    .text-lg { font-size: 1.25rem; }
    .text-muted { color: #5c4f3d; }
    .text-muted-light { color: #7a6b56; }
    .lh-relaxed { line-height: 1.4; }
    .line-clamp-3 {
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      min-height: 2.8em;
    }
    .bg-card { background: #f0e6d6; }
    .max-w-card { max-width: 20rem; }
    .flex-1 { flex: 1; }
    .inline-block { display: inline-block; }
    .hidden { display: none; }
    .cursor-pointer { cursor: pointer; }
    .product-card {
      transition: transform 0.15s, box-shadow 0.15s;
    }
    .product-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.12);
    }
    .category-tag {
      transition: background-color 0.15s, color 0.15s;
      border-radius: 1rem;
    }
    .category-tag:hover {
      background-color: #5c4f3d;
      color: #faf6f0;
    }
    .justify-center { justify-content: center; }
    .max-w-tablet { max-width: 700px; margin-left: auto; margin-right: auto; }
    .filter-pill {
      transition: background-color 0.15s, color 0.15s;
      border-radius: 1rem;
      cursor: pointer;
      font-size: 0.875rem;
      padding: 0.25rem 0.75rem;
    }
    .filter-pill:hover {
      background-color: #5c4f3d;
      color: #faf6f0;
    }
    .filter-pill.active {
      background-color: #5c4f3d;
      color: #faf6f0;
    }")

(def styles (str reset-css "\n" base-css "\n" component-css))

(defn page-template
  "Wrap content in the site HTML5 shell with head, header, and footer."
  [main]
  (html5
   {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title company]
    [:style styles]
    [:script {:src "https://unpkg.com/htmx.org@1.7.0"}]]
   [:body
    [:header {:class "flex items-center justify-between border p-sm m-sm"}
     [:a {:href "/"}
      [:img {:src "/assets/img/logo.png" :alt (str company " logo")}]]
     [:nav
      [:ul {:class "flex gap-sm"}
       [:li [:a {:href "/"} "About"]]
       [:li [:hr]]
       [:li [:a {:href "/products"} "Products"]]]]]

    main

    [:footer {:class "border p-sm m-sm"}
     [:ul
      [:li [:a {:href (str "mailto:" email)} email]]
      [:li [:a {:href (str "tel:" phone)} phone]]]]]))
