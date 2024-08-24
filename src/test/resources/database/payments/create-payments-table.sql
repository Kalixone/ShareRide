CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(255),
    type VARCHAR(255),
    rental_id BIGINT,
    session_url VARCHAR(512),
    session_id VARCHAR(255),
    amount_to_pay DECIMAL(10, 2)
);
