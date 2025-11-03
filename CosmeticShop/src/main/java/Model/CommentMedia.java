package Model;

import java.time.LocalDateTime;

public class CommentMedia {
    private int mediaId;
    private int commentId;
    private String mediaUrl;
    private String mediaType;
    private int mediaOrder;
    private LocalDateTime createdAt;
    
    public CommentMedia() {
    }
    
    public CommentMedia(int mediaId, int commentId, String mediaUrl, String mediaType, int mediaOrder, LocalDateTime createdAt) {
        this.mediaId = mediaId;
        this.commentId = commentId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.mediaOrder = mediaOrder;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getMediaId() {
        return mediaId;
    }
    
    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }
    
    public int getCommentId() {
        return commentId;
    }
    
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    
    public String getMediaUrl() {
        return mediaUrl;
    }
    
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
    
    public String getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    public int getMediaOrder() {
        return mediaOrder;
    }
    
    public void setMediaOrder(int mediaOrder) {
        this.mediaOrder = mediaOrder;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

