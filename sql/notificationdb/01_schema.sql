CREATE DATABASE IF NOT EXISTS notificationdb;
USE notificationdb;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_notifications_order_id (order_id),
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_created_at (created_at)
);
