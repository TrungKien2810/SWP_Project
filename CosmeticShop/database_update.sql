-- Add columns to support Google Sign-In and password reset (idempotent checks recommended in production)
ALTER TABLE Users ADD google_id VARCHAR(64) NULL;
ALTER TABLE Users ADD reset_token VARCHAR(128) NULL;
ALTER TABLE Users ADD reset_token_expiry DATETIME NULL;
-- Script để thêm cột role vào bảng Users
-- Chạy script này trong database để cập nhật cấu trúc bảng

-- Thêm cột role vào bảng Users
ALTER TABLE Users ADD COLUMN role VARCHAR(20) DEFAULT 'USER';

-- Cập nhật role cho các user hiện có (mặc định là 'USER')
UPDATE Users SET role = 'USER' WHERE role IS NULL;

-- Tạo một admin user mẫu (thay đổi email và password theo nhu cầu)
-- INSERT INTO Users (full_name, email, password, role) 
-- VALUES ('Admin', 'admin@pinkycloud.com', 'admin123', 'ADMIN');

-- Hoặc cập nhật một user hiện có thành admin
-- UPDATE Users SET role = 'ADMIN' WHERE email = 'your-admin-email@gmail.com';
