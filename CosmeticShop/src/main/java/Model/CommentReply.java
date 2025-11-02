package Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class CommentReply {
    private int replyId;
    private int commentId;
    private int userId;
    private String replyText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username; // For display purposes
    private String avatarUrl; // For display purposes
    private List<ReplyMedia> mediaList;
    
    public CommentReply() {
        this.mediaList = new ArrayList<>();
    }
    
    public CommentReply(int replyId, int commentId, int userId, String replyText, 
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.replyId = replyId;
        this.commentId = commentId;
        this.userId = userId;
        this.replyText = replyText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    
    public int getReplyId() {
        return replyId;
    }
    
    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }
    
    public int getCommentId() {
        return commentId;
    }
    
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getReplyText() {
        return replyText;
    }
    
    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public List<ReplyMedia> getMediaList() {
        return mediaList;
    }
    
    public void setMediaList(List<ReplyMedia> mediaList) {
        this.mediaList = mediaList;
    }
}

