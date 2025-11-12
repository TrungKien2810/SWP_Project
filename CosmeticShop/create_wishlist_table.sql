-- ===== BẢNG WISHLIST =====
CREATE TABLE Wishlist (
    wishlist_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
    UNIQUE (user_id, product_id) -- Mỗi user chỉ có thể thêm 1 sản phẩm vào wishlist 1 lần
);
GO

-- Index để tối ưu truy vấn
CREATE INDEX IX_Wishlist_User ON Wishlist(user_id);
CREATE INDEX IX_Wishlist_Product ON Wishlist(product_id);
GO

