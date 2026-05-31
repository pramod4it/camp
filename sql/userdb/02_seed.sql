USE userdb;

INSERT INTO users (id, name, email)
VALUES (1, 'Pramod Singh', 'pramod@example.com')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    email = VALUES(email);
