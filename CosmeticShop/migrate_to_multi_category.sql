-- =====================================================================
-- COSMETIC SHOP - MIGRATION TO MULTI-CATEGORY SYSTEM
-- Script tổng hợp: Chuyển từ 1 category → nhiều categories
-- Chạy script này trong SQL Server Management Studio (SSMS)
-- =====================================================================

PRINT '========================================';
PRINT 'Bắt đầu migration: 1 Category → Nhiều Categories';
PRINT '========================================';
GO

-- =====================================================================
-- BƯỚC 1: Tạo bảng trung gian ProductCategories
-- =====================================================================
PRINT '';
PRINT 'BƯỚC 1: Tạo bảng ProductCategories...';

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ProductCategories]') AND type = 'U')
BEGIN
    CREATE TABLE ProductCategories (
        product_id INT NOT NULL,
        category_id INT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        PRIMARY KEY (product_id, category_id),
        FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
        FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE
    );
    
    -- Index để tối ưu query
    CREATE INDEX IX_ProductCategories_Category ON ProductCategories(category_id);
    CREATE INDEX IX_ProductCategories_Product ON ProductCategories(product_id);
    
    PRINT '✅ Bảng ProductCategories đã được tạo thành công!';
    PRINT '   - Primary Key: (product_id, category_id)';
    PRINT '   - Foreign Keys: Products, Categories';
    PRINT '   - Indexes: IX_ProductCategories_Category, IX_ProductCategories_Product';
END
ELSE
BEGIN
    PRINT '⚠️ Bảng ProductCategories đã tồn tại. Bỏ qua bước tạo bảng.';
END
GO

-- =====================================================================
-- BƯỚC 2: Migration dữ liệu từ Products.category_id → ProductCategories
-- =====================================================================
PRINT '';
PRINT 'BƯỚC 2: Migration dữ liệu từ Products.category_id → ProductCategories...';

-- Kiểm tra xem đã có dữ liệu trong ProductCategories chưa
DECLARE @ExistingCount INT;
SELECT @ExistingCount = COUNT(*) FROM ProductCategories;

IF @ExistingCount > 0
BEGIN
    PRINT '⚠️ Bảng ProductCategories đã có ' + CAST(@ExistingCount AS VARCHAR(10)) + ' bản ghi.';
    PRINT '   Bạn có muốn tiếp tục migration? (Có thể tạo duplicate)';
    PRINT '   Để an toàn, script sẽ chỉ migrate những sản phẩm chưa có trong ProductCategories.';
END

-- Migrate dữ liệu từ Products.category_id → ProductCategories
-- Chỉ migrate những sản phẩm có category_id hợp lệ và chưa tồn tại trong ProductCategories
INSERT INTO ProductCategories (product_id, category_id)
SELECT DISTINCT p.product_id, p.category_id 
FROM Products p 
WHERE p.category_id IS NOT NULL 
  AND p.category_id > 0
  AND EXISTS (SELECT 1 FROM Categories c WHERE c.category_id = p.category_id)
  AND NOT EXISTS (
      SELECT 1 FROM ProductCategories pc 
      WHERE pc.product_id = p.product_id AND pc.category_id = p.category_id
  );

DECLARE @MigratedCount INT;
SELECT @MigratedCount = @@ROWCOUNT;
PRINT '✅ Đã migrate ' + CAST(@MigratedCount AS VARCHAR(10)) + ' sản phẩm vào bảng ProductCategories.';
GO

-- =====================================================================
-- BƯỚC 3: Kiểm tra và báo cáo kết quả
-- =====================================================================
PRINT '';
PRINT 'BƯỚC 3: Kiểm tra kết quả migration...';

-- Đếm tổng số sản phẩm có category
DECLARE @TotalProductsWithCategory INT;
SELECT @TotalProductsWithCategory = COUNT(DISTINCT product_id) 
FROM ProductCategories;

DECLARE @TotalProducts INT;
SELECT @TotalProducts = COUNT(*) FROM Products;

DECLARE @ProductsWithoutCategory INT;
SELECT @ProductsWithoutCategory = COUNT(*) 
FROM Products p
WHERE NOT EXISTS (
    SELECT 1 FROM ProductCategories pc WHERE pc.product_id = p.product_id
);

PRINT '';
PRINT '📊 THỐNG KÊ:';
PRINT '   - Tổng số sản phẩm: ' + CAST(@TotalProducts AS VARCHAR(10));
PRINT '   - Sản phẩm có category: ' + CAST(@TotalProductsWithCategory AS VARCHAR(10));
PRINT '   - Sản phẩm không có category: ' + CAST(@ProductsWithoutCategory AS VARCHAR(10));
PRINT '';

-- Hiển thị một số ví dụ sản phẩm có nhiều categories (nếu có)
PRINT '📋 MẪU DỮ LIỆU (Top 10 sản phẩm có nhiều categories nhất):';
SELECT TOP 10
    p.product_id,
    p.name AS product_name,
    COUNT(pc.category_id) AS category_count,
    STRING_AGG(c.name, ', ') AS categories
FROM Products p
JOIN ProductCategories pc ON p.product_id = pc.product_id
JOIN Categories c ON pc.category_id = c.category_id
GROUP BY p.product_id, p.name
HAVING COUNT(pc.category_id) > 0
ORDER BY category_count DESC, p.product_id;
GO

-- =====================================================================
-- BƯỚC 4: (TÙY CHỌN) Xóa cột category_id cũ trong Products
-- =====================================================================
PRINT '';
PRINT 'BƯỚC 4: Xóa cột category_id cũ trong Products...';
PRINT '⚠️ CẢNH BÁO: Bước này sẽ XÓA VĨNH VIỄN cột category_id trong bảng Products!';
PRINT '   Chỉ chạy bước này sau khi đã test kỹ và chắc chắn mọi thứ hoạt động tốt.';
PRINT '   Để an toàn, script này sẽ KHÔNG tự động xóa.';
PRINT '   Nếu muốn xóa, hãy uncomment các dòng dưới đây:';
PRINT '';
PRINT '-- Bước 4.1: Tìm và xóa Foreign Key constraint';
PRINT '-- DECLARE @FKName NVARCHAR(128);';
PRINT '-- SELECT @FKName = name FROM sys.foreign_keys WHERE parent_object_id = OBJECT_ID(''Products'') AND referenced_object_id = OBJECT_ID(''Categories'');';
PRINT '-- IF @FKName IS NOT NULL';
PRINT '-- BEGIN';
PRINT '--     DECLARE @DropFKSQL NVARCHAR(MAX) = ''ALTER TABLE Products DROP CONSTRAINT '' + QUOTENAME(@FKName);';
PRINT '--     EXEC sp_executesql @DropFKSQL;';
PRINT '--     PRINT ''✅ Đã xóa Foreign Key constraint: '' + @FKName;';
PRINT '-- END';
PRINT '-- ELSE';
PRINT '-- BEGIN';
PRINT '--     PRINT ''⚠️ Không tìm thấy Foreign Key constraint.'';';
PRINT '-- END';
PRINT '';
PRINT '-- Bước 4.2: Xóa cột category_id';
PRINT '-- IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(''Products'') AND name = ''category_id'')';
PRINT '-- BEGIN';
PRINT '--     ALTER TABLE Products DROP COLUMN category_id;';
PRINT '--     PRINT ''✅ Đã xóa cột category_id trong Products.'';';
PRINT '-- END';
PRINT '-- ELSE';
PRINT '-- BEGIN';
PRINT '--     PRINT ''⚠️ Cột category_id không tồn tại.'';';
PRINT '-- END';
GO

-- =====================================================================
-- HOÀN TẤT
-- =====================================================================
PRINT '';
PRINT '========================================';
PRINT '✅ MIGRATION HOÀN TẤT!';
PRINT '========================================';
PRINT '';
PRINT '📝 CÁC BƯỚC TIẾP THEO:';
PRINT '   1. Kiểm tra dữ liệu đã migrate đúng chưa';
PRINT '   2. Test ứng dụng với multi-category';
PRINT '   3. Sau khi chắc chắn, có thể xóa cột category_id cũ (xem BƯỚC 4)';
PRINT '';
PRINT '⚠️ LƯU Ý:';
PRINT '   - Cột category_id trong Products vẫn còn để backward compatibility';
PRINT '   - Code mới sẽ dùng bảng ProductCategories';
PRINT '   - Có thể rollback bằng cách restore từ ProductCategories về Products.category_id';
PRINT '';

