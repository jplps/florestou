(ns florestou.db.migrations
  (:require [florestou.db.helpers :refer [execute!]]))

(defn create-products-table []
  (execute! ["CREATE TABLE IF NOT EXISTS products (
                     id SERIAL PRIMARY KEY,
                     name VARCHAR(255) NOT NULL,
                     description TEXT,
                     price DECIMAL(10, 2) NOT NULL,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                   )"]))

(defn create-categories-table []
  (execute! ["CREATE TABLE IF NOT EXISTS categories (
                     id SERIAL PRIMARY KEY,
                     name VARCHAR(255) NOT NULL,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                   )"]))

(defn create-product-categories-table []
  (execute! ["CREATE TABLE IF NOT EXISTS product_categories (
                     product_id INTEGER REFERENCES products(id),
                     category_id INTEGER REFERENCES categories(id),
                     PRIMARY KEY (product_id, category_id)
                   )"]))

(defn run-migrations []
  (create-products-table)
  (create-categories-table)
  (create-product-categories-table))
