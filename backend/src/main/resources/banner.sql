CREATE TABLE banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128),
    product_id BIGINT NOT NULL,
    image_url VARCHAR(512) NOT NULL,
    link VARCHAR(512),
    sort_order INT NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    INDEX idx_enabled_sort (enabled, sort_order),
    FOREIGN KEY (product_id) REFERENCES product(id)
);
