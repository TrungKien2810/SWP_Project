-- ===== BẢNG THÔNG BÁO =====
CREATE TABLE Notifications (
    notification_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT, -- NULL nếu là thông báo cho tất cả admin
    notification_type NVARCHAR(50) NOT NULL, -- 'CUSTOMER_FEEDBACK', 'LOW_RATING', 'DISCOUNT_ASSIGNED', 'SPECIAL_EVENT'
    title NVARCHAR(200) NOT NULL,
    message NVARCHAR(1000) NOT NULL,
    is_read BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    link_url NVARCHAR(500), -- URL để điều hướng khi click vào thông báo
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);
GO

-- Index để tối ưu truy vấn
CREATE INDEX IX_Notifications_User_Read ON Notifications(user_id, is_read);
CREATE INDEX IX_Notifications_Created ON Notifications(created_at DESC);
GO

