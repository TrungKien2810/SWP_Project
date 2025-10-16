-- Cập nhật database hiện tại để hỗ trợ gallery ảnh
USE PinkyCloudDB;
GO

-- Thêm các field còn thiếu vào bảng Products
ALTER TABLE Products 
ADD description NVARCHAR(MAX),
    image_url NVARCHAR(500);
GO

-- Tạo bảng ProductImages để lưu trữ nhiều ảnh cho mỗi sản phẩm
CREATE TABLE ProductImages (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    image_url NVARCHAR(500) NOT NULL,
    image_order INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
);
GO


