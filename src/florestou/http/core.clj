(ns florestou.http.core
  "HTTP server component with Ring/Jetty."
  (:require [com.stuartsierra.component :as component]
            [florestou.http.routes :refer [api-routes not-found-handler]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defn route-handler
  "Dispatch a request to the matching route handler, returning 500 on unhandled exceptions."
  [product-service category-service request]
  (let [uri (:uri request)
        handler (get api-routes uri not-found-handler)]
    (try
      (handler product-service category-service request)
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body {:message (.getMessage e)}}))))

(defn inject-services
  "Create a Ring handler that injects service dependencies into route-handler."
  [product-service category-service]
  (fn [request]
    (route-handler product-service category-service request)))

(defrecord HttpServer [port product-service category-service]
  component/Lifecycle
  (start [this]
    (if (:server this)
      this
      (let [middleware (-> (inject-services product-service category-service)
                           wrap-keyword-params
                           wrap-params
                           wrap-json-response)
            server (run-jetty middleware {:port port :join? false})]
        (assoc this :server server))))

  (stop [this]
    (when-let [server (:server this)]
      (.stop server))
    (assoc this :server nil)))

(defn new-server
  "Create an HttpServer component for the given port."
  [port]
  (map->HttpServer {:port port}))
