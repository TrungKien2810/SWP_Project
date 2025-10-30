-- Tạo bảng Banners để quản lý slideshow trang chủ
USE PinkyCloudDB;
GO

IF OBJECT_ID('dbo.Banners', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Banners (
        banner_id INT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(200) NOT NULL,
        image_url NVARCHAR(500) NOT NULL,
        link_url NVARCHAR(500) NULL,
        sort_order INT NOT NULL DEFAULT 0,
        is_active BIT NOT NULL DEFAULT 1,
        created_at DATETIME NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME NULL
    );

    CREATE INDEX IX_Banners_Active_Order ON dbo.Banners(is_active, sort_order, banner_id);
END
GO

PRINT 'Bảng Banners đã sẵn sàng.';


