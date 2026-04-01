(ns florestou.http.pages.category
  "Category filter component with checkbox toggles and sync logic."
  (:require [hiccup.core :refer [html]]))

(defn category-list-items
  "Render an HTMX-swappable category filter pill cloud with checkboxes and client-side sync JS."
  [categories]
  (html
   [:div {:id "categories-list"}
    [:div {:class "flex flex-wrap gap-xs justify-center"
           :id "category-form"
           :hx-target "#products-list"}
     (for [category categories]
       (let [category-name (:name category)]
         [:label {:class "filter-pill border"
                  :for category-name}
          [:input {:class "hidden"
                   :type "checkbox"
                   :name "categories"
                   :value category-name
                   :id category-name}]
          category-name]))]
    [:script "
function syncFilters() {
    var form = document.getElementById('category-form');
    var selected = Array.from(form.querySelectorAll('input[type=\"checkbox\"]:checked')).map(function(cb) { return cb.value; });
    var qs = selected.length > 0 ? '?categories=' + selected.join(',') : '';
    htmx.ajax('GET', '/products/list' + qs, '#products-list');
    form.querySelectorAll('.filter-pill').forEach(function(pill) {
        var cb = pill.querySelector('input[type=\"checkbox\"]');
        if (cb.checked) {
            pill.classList.add('active');
        } else {
            pill.classList.remove('active');
        }
    });
}

function selectCategory(name) {
    var cb = document.getElementById(name);
    if (cb) {
        cb.checked = !cb.checked;
        syncFilters();
    }
}

document.addEventListener('change', function(e) {
    if (e.target.matches('#category-form input[type=\"checkbox\"]')) {
        syncFilters();
    }
});
"]]))
