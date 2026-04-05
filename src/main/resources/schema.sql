-- =====================================================
-- VietTicket - Schema SQL (Tham khảo)
-- Hibernate sẽ tự tạo bảng với ddl-auto=update
-- File này dùng để tham khảo cấu trúc database
-- =====================================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS ticketdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ticketdb;

-- ===== Bảng roles =====
CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- ===== Bảng users =====
CREATE TABLE IF NOT EXISTS users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,         -- BCrypt hash
    email    VARCHAR(100) NOT NULL UNIQUE,
    enabled  TINYINT(1)   NOT NULL DEFAULT 1
);

-- ===== Bảng user_roles (quan hệ nhiều-nhiều) =====
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ===== Bảng flights (chuyến bay) =====
CREATE TABLE IF NOT EXISTS flights (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    flight_number    VARCHAR(20)    NOT NULL UNIQUE,
    airline          VARCHAR(100)   NOT NULL,
    origin           VARCHAR(100)   NOT NULL,
    destination      VARCHAR(100)   NOT NULL,
    departure_time   DATETIME       NOT NULL,
    arrival_time     DATETIME       NOT NULL,
    price            DECIMAL(12, 2) NOT NULL,
    available_seats  INT            NOT NULL DEFAULT 0,
    description      VARCHAR(500),
    status           VARCHAR(20)    NOT NULL DEFAULT 'AVAILABLE',
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== Bảng trains (chuyến tàu hỏa) =====
CREATE TABLE IF NOT EXISTS trains (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    train_number     VARCHAR(20)    NOT NULL UNIQUE,
    train_name       VARCHAR(100)   NOT NULL,
    origin           VARCHAR(100)   NOT NULL,
    destination      VARCHAR(100)   NOT NULL,
    departure_time   DATETIME       NOT NULL,
    arrival_time     DATETIME       NOT NULL,
    price            DECIMAL(12, 2) NOT NULL,
    available_seats  INT            NOT NULL DEFAULT 0,
    train_type       VARCHAR(50),   -- HARD_SEAT, SOFT_SEAT, SLEEPER_CABIN, AC_CABIN
    description      VARCHAR(500),
    status           VARCHAR(20)    NOT NULL DEFAULT 'AVAILABLE',
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== Bảng bookings (đặt vé) =====
CREATE TABLE IF NOT EXISTS bookings (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    booking_type     VARCHAR(10)    NOT NULL,  -- 'FLIGHT' hoặc 'TRAIN'
    flight_id        BIGINT,                   -- NULL nếu là vé tàu
    train_id         BIGINT,                   -- NULL nếu là vé máy bay
    passenger_name   VARCHAR(100)   NOT NULL,
    passenger_email  VARCHAR(100)   NOT NULL,
    passenger_phone  VARCHAR(15)    NOT NULL,
    seat_count       INT            NOT NULL DEFAULT 1,
    total_price      DECIMAL(12, 2) NOT NULL,
    status           VARCHAR(20)    NOT NULL DEFAULT 'CONFIRMED',
    booking_date     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id)  ON DELETE SET NULL,
    FOREIGN KEY (train_id)  REFERENCES trains(id)   ON DELETE SET NULL,

    INDEX idx_booking_user   (user_id),
    INDEX idx_booking_type   (booking_type),
    INDEX idx_booking_status (status),
    INDEX idx_booking_date   (booking_date)
);

-- =====================================================
-- Dữ liệu mẫu khởi tạo (DataInitializer.java làm thay)
-- Uncomment nếu muốn insert thủ công:
-- =====================================================

-- INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');
--
-- -- Password: admin123 (BCrypt)
-- INSERT INTO users (username, password, email, enabled)
-- VALUES ('admin', '$2a$12$xxx...', 'admin@ticketbooking.vn', 1);
--
-- INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
