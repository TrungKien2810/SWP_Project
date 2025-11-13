package Model;

import java.time.LocalDateTime;

public class Notification {
    private int notificationId;
    private Integer userId; // null nếu là thông báo cho tất cả admin
    private String notificationType; // 'CUSTOMER_FEEDBACK', 'LOW_RATING', 'DISCOUNT_ASSIGNED', 'SPECIAL_EVENT'
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String linkUrl;

    public Notification() {
    }

    public Notification(Integer userId, String notificationType, String title, String message, String linkUrl) {
        this.userId = userId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.linkUrl = linkUrl;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(int notificationId, Integer userId, String notificationType, String title, 
                       String message, boolean isRead, LocalDateTime createdAt, String linkUrl) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.linkUrl = linkUrl;
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}

