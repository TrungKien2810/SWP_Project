-- Create ReplyMedia table for reply media attachments
USE PinkyCloudDB;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ReplyMedia')
BEGIN
    CREATE TABLE ReplyMedia (
        media_id INT IDENTITY(1,1) PRIMARY KEY,
        reply_id INT NOT NULL,
        media_url NVARCHAR(500) NOT NULL,
        media_type NVARCHAR(20) NOT NULL CHECK (media_type IN ('image', 'video')),
        media_order INT DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (reply_id) REFERENCES CommentReplies(reply_id) ON DELETE CASCADE
    );
    
    PRINT 'Table ReplyMedia created successfully';
END
ELSE
BEGIN
    PRINT 'Table ReplyMedia already exists';
END
GO

