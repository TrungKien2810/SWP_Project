-- =====================================================================
-- Rollback script: Khôi phục dữ liệu từ ProductCategories về Products.category_id
-- CHỈ CHẠY KHI CẦN ROLLBACK (nếu có vấn đề)
-- =====================================================================

-- Cảnh báo
PRINT 'CẢNH BÁO: Script này sẽ khôi phục Products.category_id từ ProductCategories.';
PRINT 'Nếu một sản phẩm có nhiều categories, chỉ category đầu tiên (theo created_at) sẽ được giữ lại.';
PRINT 'Bạn có chắc muốn tiếp tục? (Chạy script này có thể mất dữ liệu nếu sản phẩm có nhiều categories)';
GO

-- Khôi phục category_id cho Products (lấy category đầu tiên theo created_at)
UPDATE Products
SET category_id = (
    SELECT TOP 1 category_id 
    FROM ProductCategories pc 
    WHERE pc.product_id = Products.product_id 
    ORDER BY pc.created_at ASC
)
WHERE EXISTS (
    SELECT 1 FROM ProductCategories pc 
    WHERE pc.product_id = Products.product_id
);

DECLARE @RestoredCount INT;
SELECT @RestoredCount = @@ROWCOUNT;
PRINT 'Đã khôi phục category_id cho ' + CAST(@RestoredCount AS VARCHAR(10)) + ' sản phẩm.';
GO

-- (Tùy chọn) Xóa bảng ProductCategories sau khi rollback
-- UNCOMMENT DÒNG DƯỚI NẾU MUỐN XÓA BẢNG:
-- DROP TABLE IF EXISTS ProductCategories;
-- PRINT 'Đã xóa bảng ProductCategories.';

