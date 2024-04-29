(ns florestou.http.core
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

(defn health-handler [_]
  (response {:message "Healthy"}))

(defn not-found-handler [_]
  (response {:message "Not Found"}))

(defn exception-handling [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (response {:status "Internal Server Error"
                   :body {:message (.getMessage e)}})))))

(def api-routes
  {"/health" health-handler})

(defn route-handler [request]
  (let [uri (:uri request)
        handler (get api-routes uri not-found-handler)]
    (handler request)))

(def middleware
  (-> route-handler
      exception-handling
      wrap-keyword-params
      wrap-params
      wrap-json-response))

(defrecord HttpServer [port]
  component/Lifecycle
  (start [this]
    (if (:server this)
      this
      (let [server (run-jetty middleware {:port port :join? false})]
        (assoc this :server server))))

  (stop [this]
    (when-let [server (:server this)]
      (.stop server))
    (assoc this :server nil)))

(defn new-server [port]
  (map->HttpServer {:port port}))
