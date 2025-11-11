package DAO;

import Model.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn;

    public NotificationDB() {
        this.conn = db.conn;
    }

    // Tạo thông báo mới
    public int createNotification(Notification notification) {
        String sql = "INSERT INTO Notifications (user_id, notification_type, title, message, link_url, is_read, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (notification.getUserId() != null) {
                ps.setInt(1, notification.getUserId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, notification.getNotificationType());
            ps.setString(3, notification.getTitle());
            ps.setString(4, notification.getMessage());
            if (notification.getLinkUrl() != null) {
                ps.setString(5, notification.getLinkUrl());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }
            ps.setBoolean(6, notification.isRead());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Lấy tất cả thông báo của một user (chưa đọc trước, sau đó đã đọc)
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING')) " +
                     "ORDER BY is_read ASC, created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    // Lấy thông báo chưa đọc của một user
    public List<Notification> getUnreadNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE (user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING'))) " +
                     "AND is_read = 0 ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    // Đếm số thông báo chưa đọc
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM Notifications " +
                     "WHERE (user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING'))) " +
                     "AND is_read = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Đánh dấu thông báo là đã đọc
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE notification_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đánh dấu tất cả thông báo của user là đã đọc
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE Notifications SET is_read = 1 " +
                     "WHERE (user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING'))) " +
                     "AND is_read = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa thông báo
    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM Notifications WHERE notification_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tạo thông báo cho tất cả admin (user_id = NULL)
    public int createAdminNotification(String notificationType, String title, String message, String linkUrl) {
        Notification notification = new Notification(null, notificationType, title, message, linkUrl);
        return createNotification(notification);
    }

    // Helper method để map ResultSet sang Notification
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        int userId = rs.getInt("user_id");
        notification.setUserId(rs.wasNull() ? null : userId);
        notification.setNotificationType(rs.getString("notification_type"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        if (rs.getTimestamp("created_at") != null) {
            notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        String linkUrl = rs.getString("link_url");
        notification.setLinkUrl(rs.wasNull() ? null : linkUrl);
        return notification;
    }
}

