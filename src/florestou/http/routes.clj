(ns florestou.http.routes
  (:require [ring.util.response :as response]))

(defn health-check [_]
  (response/status 200))

(def api-routes
  ["/health" {:get health-check}])
