-- Repair path: some databases were baselined at v1 (Flyway skipped V1) while only `products`
-- existed from Hibernate, so `app_users` was never created. IF NOT EXISTS keeps this safe when v1 already ran.
CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_app_users_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
