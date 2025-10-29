-- Add columns to support Google Sign-In and password reset (optional)
IF COL_LENGTH('Users', 'google_id') IS NULL ALTER TABLE Users ADD google_id VARCHAR(64) NULL;
IF COL_LENGTH('Users', 'reset_token') IS NULL ALTER TABLE Users ADD reset_token VARCHAR(128) NULL;
IF COL_LENGTH('Users', 'reset_token_expiry') IS NULL ALTER TABLE Users ADD reset_token_expiry DATETIME NULL;
IF COL_LENGTH('Users', 'avatar_url') IS NULL ALTER TABLE Users ADD avatar_url NVARCHAR(255) NULL;

-- TEST DATA: create a user, an order > 100000, and a conditional discount, then assign via proc
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'testuser@example.com')
BEGIN
    INSERT INTO Users(full_name, email, phone, password, role)
    VALUES (N'Test User', 'testuser@example.com', '0900000000', 'hashedpassword', 'USER');
END

DECLARE @uid INT = (SELECT TOP 1 user_id FROM Users WHERE email='testuser@example.com');

IF NOT EXISTS (SELECT 1 FROM Discounts WHERE code = 'SPENT100K')
BEGIN
    INSERT INTO Discounts(code, name, discount_type, discount_value, min_order_amount, max_discount_amount,
        usage_limit, start_date, end_date, is_active, condition_type, condition_value, condition_description, special_event)
    VALUES ('SPENT100K', N'Ưu đãi khách chi tiêu 100k', 'PERCENTAGE', 10, 0, 50000, NULL,
        DATEADD(DAY,-1,GETDATE()), DATEADD(DAY,30,GETDATE()), 1, 'TOTAL_SPENT', 100000, N'Chi tiêu tích lũy >= 100000', 0);
END

INSERT INTO Orders(user_id, order_date, total_amount, payment_status, order_status)
VALUES (@uid, GETDATE(), 150000, 'PAID', 'COMPLETED');

EXEC sp_CheckAndAssignVouchers @user_id = @uid;
