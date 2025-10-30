-- ===== BẢNG NGƯỜI DÙNG =====
CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone NVARCHAR(20),
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    reset_token NVARCHAR(255),
    reset_token_expiry DATETIME,
    date_create DATETIME DEFAULT GETDATE()
);
GO

-- ===== BẢNG DANH MỤC =====
CREATE TABLE Categories (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500)
);
GO

-- ===== BẢNG SẢN PHẨM =====
CREATE TABLE Products (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    description NVARCHAR(MAX),
    image_url NVARCHAR(500),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);
GO

-- ===== BẢNG HÌNH ẢNH SẢN PHẨM =====
CREATE TABLE ProductImages (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    image_url NVARCHAR(500) NOT NULL,
    image_order INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
);
GO

-- ===== BẢNG ĐỊA CHỈ GIAO HÀNG =====
CREATE TABLE ShippingAddresses (
    address_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    phone NVARCHAR(20) NOT NULL,
    address NVARCHAR(500) NOT NULL,
    city NVARCHAR(100) NOT NULL,
    district NVARCHAR(100) NOT NULL,
    ward NVARCHAR(100) NOT NULL,
    is_default BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);
GO

-- ===== BẢNG PHƯƠNG THỨC GIAO HÀNG =====
CREATE TABLE ShippingMethods (
    method_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    cost DECIMAL(10,2) DEFAULT 0,
    estimated_days INT DEFAULT 1,
    is_active BIT DEFAULT 1
);
GO

-- ===== BẢNG GIẢM GIÁ/VOUCHER =====
CREATE TABLE Discounts (
    discount_id INT IDENTITY(1,1) PRIMARY KEY,
    code NVARCHAR(50) NOT NULL UNIQUE,
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(500),
    discount_type NVARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0,
    max_discount_amount DECIMAL(10,2),
    usage_limit INT,
    used_count INT DEFAULT 0,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BIT DEFAULT 1,
    
    -- Điều kiện nhận voucher
    condition_type NVARCHAR(20) CHECK (condition_type IN ('TOTAL_SPENT', 'ORDER_COUNT', 'FIRST_ORDER', 'SPECIAL_EVENT')),
    condition_value DECIMAL(10,2),
    condition_description NVARCHAR(500),
    
    -- Voucher đặc biệt (Black Friday, etc.)
special_event BIT DEFAULT 0,
    auto_assign_all BIT DEFAULT 0,
    assign_date DATETIME,
    
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ===== BẢNG ĐƠN HÀNG =====
CREATE TABLE Orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    order_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_address_id INT,
    shipping_method_id INT,
    shipping_cost DECIMAL(10,2) DEFAULT 0,
    payment_method NVARCHAR(50),
    payment_status NVARCHAR(20) DEFAULT 'PENDING',
    order_status NVARCHAR(20) DEFAULT 'PENDING',
    tracking_number NVARCHAR(100),
    discount_id INT,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    notes NVARCHAR(1000),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (shipping_address_id) REFERENCES ShippingAddresses(address_id),
    FOREIGN KEY (shipping_method_id) REFERENCES ShippingMethods(method_id),
    FOREIGN KEY (discount_id) REFERENCES Discounts(discount_id)
);
GO

-- ===== BẢNG CHI TIẾT ĐƠN HÀNG =====
CREATE TABLE OrderDetails (
    order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);
GO

-- ===== BẢNG VOUCHER CỦA NGƯỜI DÙNG =====
CREATE TABLE UserVouchers (
    user_voucher_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    discount_id INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'UNUSED' CHECK (status IN ('UNUSED', 'USED', 'EXPIRED')),
    assigned_date DATETIME DEFAULT GETDATE(),
    used_date DATETIME,
    order_id INT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (discount_id) REFERENCES Discounts(discount_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    UNIQUE(user_id, discount_id)
);
GO

-- ===== BẢNG GIỎ HÀNG =====
CREATE TABLE Carts (
    cart_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);
GO

-- ===== BẢNG CHI TIẾT GIỎ HÀNG =====
CREATE TABLE CartItems (
    cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    is_selected BIT DEFAULT 1,
    added_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (cart_id) REFERENCES Carts(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
    UNIQUE(cart_id, product_id)
);
GO

-- ===== INDEXES =====
CREATE INDEX IX_Users_Email ON Users(email);
CREATE INDEX IX_Users_Username ON Users(username);
CREATE INDEX IX_Products_Category ON Products(category_id);
CREATE INDEX IX_Products_Name ON Products(name);
CREATE INDEX IX_Orders_User ON Orders(user_id);
CREATE INDEX IX_Orders_Date ON Orders(order_date);
CREATE INDEX IX_OrderDetails_Order ON OrderDetails(order_id);
CREATE INDEX IX_OrderDetails_Product ON OrderDetails(product_id);
CREATE INDEX IX_Discounts_Code ON Discounts(code);
CREATE INDEX IX_Discounts_Active ON Discounts(is_active, start_date, end_date);
CREATE INDEX IX_UserVouchers_User ON UserVouchers(user_id);
CREATE INDEX IX_UserVouchers_Status ON UserVouchers(status);
CREATE INDEX IX_CartItems_Cart ON CartItems(cart_id);
CREATE INDEX IX_CartItems_Selected ON CartItems(is_selected);
CREATE INDEX IX_ShippingAddresses_User ON ShippingAddresses(user_id);
GO

-- ===== STORED PROCEDURES =====

-- ===== PROCEDURE KIỂM TRA VÀ GÁN VOUCHER TỰ ĐỘNG =====
CREATE PROCEDURE sp_CheckAndAssignVouchers
    @user_id INT
AS
BEGIN
    -- Kiểm tra điều kiện tổng tiền đã chi
    INSERT INTO UserVouchers (user_id, discount_id)
    SELECT @user_id, d.discount_id
    FROM Discounts d
    WHERE d.condition_type = 'TOTAL_SPENT'
    AND d.is_active = 1
    AND d.start_date <= GETDATE()
    AND d.end_date >= GETDATE()
    AND NOT EXISTS (
        SELECT 1 FROM UserVouchers uv 
        WHERE uv.user_id = @user_id AND uv.discount_id = d.discount_id
    )
    AND (
        SELECT ISNULL(SUM(total_amount), 0) 
        FROM Orders 
        WHERE user_id = @user_id AND order_status = 'COMPLETED'
    ) >= d.condition_value;

    -- Kiểm tra điều kiện số đơn hàng
    INSERT INTO UserVouchers (user_id, discount_id)
    SELECT @user_id, d.discount_id
    FROM Discounts d
    WHERE d.condition_type = 'ORDER_COUNT'
    AND d.is_active = 1
    AND d.start_date <= GETDATE()
    AND d.end_date >= GETDATE()
    AND NOT EXISTS (
        SELECT 1 FROM UserVouchers uv 
        WHERE uv.user_id = @user_id AND uv.discount_id = d.discount_id
    )
    AND (
        SELECT COUNT(*) 
        FROM Orders 
        WHERE user_id = @user_id AND order_status = 'COMPLETED'
    ) >= d.condition_value;

    -- Kiểm tra đơn hàng đầu tiên
    INSERT INTO UserVouchers (user_id, discount_id)
    SELECT @user_id, d.discount_id
    FROM Discounts d
    WHERE d.condition_type = 'FIRST_ORDER'
    AND d.is_active = 1
    AND d.start_date <= GETDATE()
    AND d.end_date >= GETDATE()
    AND NOT EXISTS (
        SELECT 1 FROM UserVouchers uv 
        WHERE uv.user_id = @user_id AND uv.discount_id = d.discount_id
    )
    AND EXISTS (
        SELECT 1 FROM Orders 
        WHERE user_id = @user_id AND order_status = 'COMPLETED'
    );
END
GO

-- ===== PROCEDURE GÁN VOUCHER ĐẶC BIỆT CHO TẤT CẢ USER =====
CREATE PROCEDURE sp_AssignSpecialEventVouchers
    @discount_id INT
AS
BEGIN
    INSERT INTO UserVouchers (user_id, discount_id)
    SELECT u.user_id, @discount_id
    FROM Users u
    WHERE NOT EXISTS (
        SELECT 1 FROM UserVouchers uv
WHERE uv.user_id = u.user_id AND uv.discount_id = @discount_id
    );
END
GO

-- ===== PROCEDURE CẬP NHẬT VOUCHER HẾT HẠN =====
CREATE PROCEDURE sp_UpdateExpiredVouchers
AS
BEGIN
    UPDATE UserVouchers 
    SET status = 'EXPIRED'
    WHERE status = 'UNUSED'
    AND discount_id IN (
        SELECT discount_id FROM Discounts 
        WHERE end_date < GETDATE()
    );
END
GO

-- ===== PROCEDURE TÍNH TỔNG GIỎ HÀNG =====
CREATE PROCEDURE sp_CalculateCartTotal
    @cart_id INT,
    @total_amount DECIMAL(10,2) OUTPUT
AS
BEGIN
    SELECT @total_amount = ISNULL(SUM(ci.price * ci.quantity), 0)
    FROM CartItems ci
    WHERE ci.cart_id = @cart_id 
    AND ci.is_selected = 1;
END
GO

-- ===== TRIGGERS =====

-- ===== TRIGGER TỰ ĐỘNG GÁN VOUCHER KHI TẠO ĐƠN HÀNG =====
CREATE TRIGGER tr_OrderCreated_AssignVouchers
ON Orders
AFTER INSERT
AS
BEGIN
    DECLARE @user_id INT;
    SELECT @user_id = user_id FROM inserted;
    
    EXEC sp_CheckAndAssignVouchers @user_id;
END
GO

-- ===== TRIGGER CẬP NHẬT SỐ LƯỢNG VOUCHER ĐÃ SỬ DỤNG =====
CREATE TRIGGER tr_UserVoucherUsed_UpdateDiscount
ON UserVouchers
AFTER UPDATE
AS
BEGIN
    IF UPDATE(status) AND EXISTS(SELECT 1 FROM inserted WHERE status = 'USED')
    BEGIN
        UPDATE d
        SET used_count = used_count + 1
        FROM Discounts d
        INNER JOIN inserted i ON d.discount_id = i.discount_id
        WHERE i.status = 'USED';
    END
END
GO

-- ===== TRIGGER CẬP NHẬT THỜI GIAN GIỎ HÀNG =====
CREATE TRIGGER tr_CartItemsUpdated_UpdateCartTime
ON CartItems
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    DECLARE @cart_id INT;
    
    SELECT @cart_id = cart_id FROM inserted;
    IF @cart_id IS NULL
        SELECT @cart_id = cart_id FROM deleted;
    
    UPDATE Carts 
    SET updated_at = GETDATE()
    WHERE cart_id = @cart_id;
END
GO

-- ===== TRIGGER KIỂM TRA STOCK KHI THÊM VÀO GIỎ HÀNG =====
CREATE TRIGGER tr_CartItemsInsert_CheckStock
ON CartItems
AFTER INSERT
AS
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM inserted i
        INNER JOIN Products p ON i.product_id = p.product_id
        WHERE i.quantity > p.stock
    )
    BEGIN
        RAISERROR('Số lượng sản phẩm trong giỏ hàng vượt quá tồn kho', 16, 1);
        ROLLBACK TRANSACTION;
    END
END
GO