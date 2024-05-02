(ns florestou.http.core
  (:require [com.stuartsierra.component :as component]
            [florestou.http.routes :refer [api-routes not-found-handler]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [content-type]]))

#_(defn route-handler [request]
    (let [uri (:uri request)
          handler (get api-routes uri not-found-handler)]
      (try
        (handler request)
        (catch Exception e
          (content-type {:body {:message (.getMessage e)}} "application/json")))))

(defn route-handler [product-service request]
  (let [uri (:uri request)
        handler (get api-routes uri not-found-handler)]
    (try
      (handler product-service request)
      (catch Exception e
        (content-type {:body {:message (.getMessage e)}} "application/json")))))

(defn inject-product-service [product-service handler]
  (fn [request]
    (handler product-service request)))

(defrecord HttpServer [port product-service]
  component/Lifecycle
  (start [this]
    (if (:server this)
      this
      (let [middleware (-> (inject-product-service product-service route-handler)
                           wrap-keyword-params
                           wrap-params
                           wrap-json-response)
            server (run-jetty middleware {:port port :join? false})]
        (assoc this :server server))))

  (stop [this]
    (when-let [server (:server this)]
      (.stop server))
    (assoc this :server nil)))

(defn new-server [port]
  (map->HttpServer {:port port}))
