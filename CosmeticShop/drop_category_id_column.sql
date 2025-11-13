-- =====================================================================
-- Script xóa cột category_id cũ trong bảng Products
-- CHỈ CHẠY SAU KHI ĐÃ TEST KỸ VÀ CHẮC CHẮN MỌI THỨ HOẠT ĐỘNG TỐT!
-- 
-- ⚠️ CẢNH BÁO: Script này sẽ XÓA VĨNH VIỄN cột category_id!
--    Đảm bảo bạn đã:
--    1. Test kỹ ứng dụng với multi-category
--    2. Backup database
--    3. Chắc chắn không cần rollback
-- =====================================================================

-- =====================================================================
-- BƯỚC 1: Tìm và xóa tất cả Index phụ thuộc vào cột category_id
-- Cần xóa index trước khi xóa cột
-- Xóa tất cả index có chứa cột category_id (có thể có nhiều index)
-- =====================================================================
DECLARE @IndexName NVARCHAR(128);
DECLARE @DropIndexSQL NVARCHAR(MAX);

DECLARE index_cursor CURSOR FOR
SELECT DISTINCT i.name
FROM sys.indexes i
INNER JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
INNER JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
WHERE i.object_id = OBJECT_ID('Products')
  AND c.name = 'category_id'
  AND i.name IS NOT NULL
  AND i.is_primary_key = 0; -- Không xóa primary key

OPEN index_cursor;
FETCH NEXT FROM index_cursor INTO @IndexName;

WHILE @@FETCH_STATUS = 0
BEGIN
    SET @DropIndexSQL = 'DROP INDEX ' + QUOTENAME(@IndexName) + ' ON Products';
    EXEC sp_executesql @DropIndexSQL;
    FETCH NEXT FROM index_cursor INTO @IndexName;
END

CLOSE index_cursor;
DEALLOCATE index_cursor;
GO

-- =====================================================================
-- BƯỚC 2: Tìm và xóa Foreign Key constraint
-- Script sẽ tự động tìm tên foreign key constraint và xóa nó
-- =====================================================================
DECLARE @FKName NVARCHAR(128);
SELECT @FKName = name 
FROM sys.foreign_keys 
WHERE parent_object_id = OBJECT_ID('Products') 
  AND referenced_object_id = OBJECT_ID('Categories');

IF @FKName IS NOT NULL
BEGIN
    DECLARE @DropFKSQL NVARCHAR(MAX) = 'ALTER TABLE Products DROP CONSTRAINT ' + QUOTENAME(@FKName);
    EXEC sp_executesql @DropFKSQL;
END
GO

-- =====================================================================
-- BƯỚC 3: Xóa cột category_id
-- Kiểm tra xem cột có tồn tại không trước khi xóa
-- =====================================================================
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Products') AND name = 'category_id')
BEGIN
    ALTER TABLE Products DROP COLUMN category_id;
END
GO

-- =====================================================================
-- HOÀN TẤT
-- 
-- LƯU Ý:
--   - Cột category_id đã bị xóa vĩnh viễn
--   - Tất cả dữ liệu category hiện được lưu trong bảng ProductCategories
--   - Nếu cần rollback, phải restore từ backup database
-- =====================================================================

