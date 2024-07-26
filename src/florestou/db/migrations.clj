(ns florestou.db.migrations
  (:require [florestou.db.helpers :refer [execute!]]))

(defn insert-dummy []
  (execute!
   ["BEGIN;
     TRUNCATE TABLE products, categories, product_categories CASCADE;
     INSERT INTO products (id, name, description, price) VALUES
     (1, 'Hydrating Facial Cream', 'A moisturizing cream ideal for dry skin.', 29.99),
     (2, 'Sunscreen Lotion SPF 50', 'Broad spectrum protection for all skin types.', 19.99),
     (3, 'Anti-Aging Serum', 'Reduces fine lines and wrinkles for mature skin.', 49.99),
     (4, 'Gentle Cleansing Gel', 'A mild cleanser for sensitive skin.', 15.99),
     (5, 'Revitalizing Hair Mask', 'Deep conditioning treatment for dry and damaged hair.', 24.99);
     INSERT INTO categories (id, name) VALUES
     (1, 'Skincare'),
     (2, 'Sun Protection'),
     (3, 'Anti-Aging'),
     (4, 'Cleansers'),
     (5, 'Haircare'),
     (6, 'Cream'),
     (7, 'Lotion'),
     (8, 'Serum'),
     (9, 'Gel'),
     (10, 'Mask'),
     (11, 'Dry'),
     (12, 'Oily'),
     (13, 'Sensitive'),
     (14, 'Mature'),
     (15, 'Damaged'),
     (16, 'Adults'),
     (17, 'Teens'),
     (18, 'Seniors'),
     (19, 'All Ages');
     INSERT INTO product_categories (product_id, category_id) VALUES
     (1, 1),
     (1, 18),
     (1, 16),
     (2, 10),
     (2, 6),
     (3, 3),
     (4, 17),
     (4, 19),
     (5, 8),
     (5, 5);
     COMMIT;"]))

(defn run-migrations []
  (execute!
   ["BEGIN;
     CREATE TABLE IF NOT EXISTS products (
         id SERIAL PRIMARY KEY,
         name VARCHAR(255) NOT NULL,
         description TEXT,
         price DECIMAL(10, 2) NOT NULL,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
     );
     CREATE TABLE IF NOT EXISTS categories (
         id SERIAL PRIMARY KEY,
         name VARCHAR(255) NOT NULL,
         description TEXT,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
     );
     CREATE TABLE IF NOT EXISTS product_categories (
         product_id INTEGER NOT NULL,
         category_id INTEGER NOT NULL,
         PRIMARY KEY (product_id, category_id),
         FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
         FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
     );
     CREATE INDEX IF NOT EXISTS idx_product_categories_product_id ON product_categories(product_id);
     CREATE INDEX IF NOT EXISTS idx_product_categories_category_id ON product_categories(category_id);
     COMMIT;"])

  (insert-dummy))
