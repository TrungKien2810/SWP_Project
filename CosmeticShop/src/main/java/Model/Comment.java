package Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Comment {
    private int commentId;
    private int productId;
    private int userId;
    private String commentText;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private String avatarUrl;
    private List<CommentMedia> mediaList;
    private List<CommentReply> replies;
    
    public Comment() {
        this.mediaList = new ArrayList<>();
        this.replies = new ArrayList<>();
    }
    
    public Comment(int commentId, int productId, int userId, String commentText, int rating, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.productId = productId;
        this.userId = userId;
        this.commentText = commentText;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    
    public int getCommentId() {
        return commentId;
    }
    
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getCommentText() {
        return commentText;
    }
    
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
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
    
    public List<CommentMedia> getMediaList() {
        return mediaList;
    }
    
    public void setMediaList(List<CommentMedia> mediaList) {
        this.mediaList = mediaList;
    }
    
    public List<CommentReply> getReplies() {
        return replies;
    }
    
    public void setReplies(List<CommentReply> replies) {
        this.replies = replies;
    }
}

