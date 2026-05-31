USE inventorydb;

INSERT INTO inventory_items (product_id, product_name, available_quantity)
VALUES
    (1001, 'Java Cloud Camp Seat', 25),
    (1002, 'Spring Cloud Lab Access', 10)
ON DUPLICATE KEY UPDATE
    product_name = VALUES(product_name),
    available_quantity = VALUES(available_quantity);
