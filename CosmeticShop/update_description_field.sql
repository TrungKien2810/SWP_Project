-- Cập nhật trường description để hỗ trợ mô tả nhiều mục
USE PinkyCloudDB;
GO

-- Cập nhật kích thước trường description từ NVARCHAR(1000) thành NVARCHAR(MAX)
ALTER TABLE Products 
ALTER COLUMN description NVARCHAR(MAX);
GO

PRINT 'Đã cập nhật trường description thành công!';
