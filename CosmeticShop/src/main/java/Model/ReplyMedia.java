package Model;

import java.time.LocalDateTime;

public class ReplyMedia {
    private int mediaId;
    private int replyId;
    private String mediaUrl;
    private String mediaType;
    private int mediaOrder;
    private LocalDateTime createdAt;
    
    public ReplyMedia() {
    }
    
    public ReplyMedia(int mediaId, int replyId, String mediaUrl, String mediaType, int mediaOrder, LocalDateTime createdAt) {
        this.mediaId = mediaId;
        this.replyId = replyId;
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
    
    public int getReplyId() {
        return replyId;
    }
    
    public void setReplyId(int replyId) {
        this.replyId = replyId;
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

