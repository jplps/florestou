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
     (5, 'Revitalizing Hair Mask', 'Deep conditioning treatment for dry and damaged hair.', 24.99),
     (6, 'Exfoliating Scrub', 'Removes dead skin cells for a smoother complexion.', 12.99),
     (7, 'Nourishing Night Cream', 'Replenishes moisture while you sleep.', 34.99),
     (8, 'Brightening Eye Cream', 'Reduces dark circles and puffiness.', 22.99),
     (9, 'Vitamin C Serum', 'Brightens skin and reduces signs of aging.', 39.99),
     (10, 'Clarifying Toner', 'Balances skin pH and removes impurities.', 14.99),
     (11, 'Deep Cleansing Mask', 'Detoxifies and purifies the skin.', 27.99),
     (12, 'Lip Balm SPF 15', 'Protects and moisturizes lips.', 5.99),
     (13, 'Hand Cream', 'Intensive care for dry and cracked hands.', 9.99),
     (14, 'Foot Cream', 'Softens and repairs rough feet.', 11.99),
     (15, 'Body Lotion', 'Hydrates and nourishes the skin.', 18.99),
     (16, 'After Shave Balm', 'Soothes and moisturizes post-shave skin.', 13.99),
     (17, 'Beard Oil', 'Conditions and softens beard hair.', 19.99),
     (18, 'Shampoo', 'Cleanses and revitalizes hair.', 10.99),
     (19, 'Conditioner', 'Moisturizes and detangles hair.', 10.99),
     (20, 'Anti-Dandruff Shampoo', 'Reduces dandruff and soothes scalp.', 12.99);
     
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
     (19, 'All Ages'),
     (20, 'Men');
     
     INSERT INTO product_categories (product_id, category_id) VALUES
     (1, 1),
     (1, 11),
     (1, 6),
     (2, 2),
     (2, 7),
     (3, 3),
     (3, 14),
     (3, 8),
     (4, 4),
     (4, 13),
     (5, 5),
     (5, 15),
     (6, 1),
     (6, 4),
     (7, 1),
     (7, 6),
     (8, 1),
     (8, 6),
     (9, 1),
     (9, 8),
     (10, 4),
     (10, 12),
     (11, 1),
     (11, 10),
     (12, 2),
     (12, 7),
     (13, 1),
     (13, 6),
     (14, 1),
     (14, 6),
     (15, 1),
     (15, 7),
     (16, 20),
     (16, 6),
     (17, 20),
     (17, 5),
     (18, 5),
     (18, 19),
     (19, 5),
     (19, 19),
     (20, 5),
     (20, 15);
     
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
