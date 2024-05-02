(ns florestou.db.migrations
  (:require [florestou.db.helpers :refer [execute!]]))

(defn insert-dummy []
  (execute! ["BEGIN;

TRUNCATE TABLE products, categories, forms, skin_hair_types, demographics, product_categories, product_attributes CASCADE;

INSERT INTO products (id, name, description, price) VALUES
(1, 'Hydrating Facial Cream', 'A moisturizing cream ideal for dry skin.', 29.99),
(2, 'Sunscreen Lotion SPF 50', 'Broad spectrum protection for all skin types.', 19.99),
(3, 'Anti-Aging Serum', 'Reduces fine lines and wrinkles for mature skin.', 49.99),
(4, 'Gentle Cleansing Gel', 'A mild cleanser for sensitive skin.', 15.99),
(5, 'Revitalizing Hair Mask', 'Deep conditioning treatment for dry and damaged hair.', 24.99);

INSERT INTO categories (id, name, description) VALUES
(1, 'Skincare', 'Products dedicated to improving and maintaining skin health.'),
(2, 'Sun Protection', 'Products that offer protection against the harmful effects of the sun.'),
(3, 'Anti-Aging', 'Products designed to reduce the signs of aging.'),
(4, 'Cleansers', 'Products for cleansing the skin and removing impurities.'),
(5, 'Haircare', 'Products focused on hair health and maintenance.');

INSERT INTO forms (id, name) VALUES
(1, 'Cream'),
(2, 'Lotion'),
(3, 'Serum'),
(4, 'Gel'),
(5, 'Mask');

INSERT INTO skin_hair_types (id, name) VALUES
(1, 'Dry'),
(2, 'Oily'),
(3, 'Sensitive'),
(4, 'Mature'),
(5, 'Damaged');

INSERT INTO demographics (id, name) VALUES
(1, 'Adults'),
(2, 'Teens'),
(3, 'Seniors'),
(4, 'All Ages');

INSERT INTO product_categories (product_id, category_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

INSERT INTO product_attributes (product_id, form_id, skin_hair_type_id, demographic_id) VALUES
(1, 1, 1, 1),
(2, 2, 5, 4),
(3, 3, 4, 3),
(4, 4, 3, 1),
(5, 5, 5, 1);

COMMIT;"]))

(defn run-migrations []
  (execute! ["BEGIN;

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

CREATE TABLE IF NOT EXISTS forms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS skin_hair_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS demographics (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
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

CREATE TABLE IF NOT EXISTS product_attributes (
    product_id INTEGER NOT NULL,
    form_id INTEGER,
    skin_hair_type_id INTEGER,
    demographic_id INTEGER,
    PRIMARY KEY (product_id, form_id, skin_hair_type_id, demographic_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE SET NULL,
    FOREIGN KEY (skin_hair_type_id) REFERENCES skin_hair_types(id) ON DELETE SET NULL,
    FOREIGN KEY (demographic_id) REFERENCES demographics(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_product_categories_product_id ON product_categories(product_id);
CREATE INDEX IF NOT EXISTS idx_product_categories_category_id ON product_categories(category_id);

CREATE INDEX IF NOT EXISTS idx_product_attributes_product_id ON product_attributes(product_id);
CREATE INDEX IF NOT EXISTS idx_product_attributes_form_id ON product_attributes(form_id);
CREATE INDEX IF NOT EXISTS idx_product_attributes_skin_hair_type_id ON product_attributes(skin_hair_type_id);
CREATE INDEX IF NOT EXISTS idx_product_attributes_demographic_id ON product_attributes(demographic_id);

COMMIT;"])

  (insert-dummy))
