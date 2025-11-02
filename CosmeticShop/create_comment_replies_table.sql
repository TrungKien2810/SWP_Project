-- Create CommentReplies table for product comment replies
USE PinkyCloudDB;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'CommentReplies')
BEGIN
    CREATE TABLE CommentReplies (
        reply_id INT IDENTITY(1,1) PRIMARY KEY,
        comment_id INT NOT NULL,
        user_id INT NOT NULL,
        reply_text NVARCHAR(MAX) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (comment_id) REFERENCES Comments(comment_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES Users(user_id)
    );
    
    PRINT 'Table CommentReplies created successfully';
END
ELSE
BEGIN
    PRINT 'Table CommentReplies already exists';
END
GO

