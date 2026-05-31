CREATE DATABASE IF NOT EXISTS inventorydb;
USE inventorydb;

CREATE TABLE IF NOT EXISTS inventory_items (
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    available_quantity INT NOT NULL,
    PRIMARY KEY (product_id)
);
