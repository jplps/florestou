(ns florestou.helpers
  (:require [clojure.edn :as edn])
  (:gen-class))

(defn load-config []
  (edn/read-string (slurp "resources/config.edn")))
