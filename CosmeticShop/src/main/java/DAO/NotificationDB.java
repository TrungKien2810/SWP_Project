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
        // Giữ phương thức cũ cho tương thích ngược: chỉ lấy theo user
        return getNotificationsByUserId(userId, false);
    }

    // Lấy tất cả thông báo, có thể bao gồm thông báo chung cho admin
    public List<Notification> getNotificationsByUserId(int userId, boolean includeAdminGlobal) {
        List<Notification> notifications = new ArrayList<>();
        String sql;
        if (includeAdminGlobal) {
            sql = "SELECT * FROM Notifications WHERE (user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING'))) " +
                  "ORDER BY is_read ASC, created_at DESC";
        } else {
            sql = "SELECT * FROM Notifications WHERE user_id = ? ORDER BY is_read ASC, created_at DESC";
        }
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
        return getUnreadNotificationsByUserId(userId, false);
    }

    // Lấy thông báo chưa đọc, có thể bao gồm thông báo chung cho admin
    public List<Notification> getUnreadNotificationsByUserId(int userId, boolean includeAdminGlobal) {
        List<Notification> notifications = new ArrayList<>();
        String sql;
        if (includeAdminGlobal) {
            sql = "SELECT * FROM Notifications WHERE (user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING'))) " +
                  "AND (is_read = 0 OR is_read IS NULL) ORDER BY created_at DESC";
        } else {
            sql = "SELECT * FROM Notifications WHERE user_id = ? AND (is_read = 0 OR is_read IS NULL) ORDER BY created_at DESC";
        }
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
        return getUnreadCount(userId, false);
    }

    // Đếm số thông báo chưa đọc, có thể bao gồm thông báo chung cho admin
    public int getUnreadCount(int userId, boolean includeAdminGlobal) {
        String sql;
        if (includeAdminGlobal) {
            sql = "SELECT COUNT(*) as count FROM Notifications " +
                  "WHERE (user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING'))) " +
                  "AND (is_read = 0 OR is_read IS NULL)";
        } else {
            sql = "SELECT COUNT(*) as count FROM Notifications WHERE user_id = ? AND (is_read = 0 OR is_read IS NULL)";
        }
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

    // Đánh dấu thông báo là đã đọc CHỈ KHI thuộc về user (không áp dụng cho thông báo global của admin)
    public boolean markAsReadForUser(int notificationId, int userId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE notification_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đánh dấu tất cả thông báo của user là đã đọc
    public boolean markAllAsRead(int userId) {
        return markAllAsRead(userId, false);
    }

    // Đánh dấu tất cả thông báo là đã đọc, có thể bao gồm thông báo chung cho admin
    public boolean markAllAsRead(int userId, boolean includeAdminGlobal) {
        String sql;
        if (includeAdminGlobal) {
            // Đánh dấu tất cả notifications của user AND các global notifications cho admin
            sql = "UPDATE Notifications SET is_read = 1 " +
                  "WHERE (user_id = ? OR (user_id IS NULL AND notification_type IN ('CUSTOMER_FEEDBACK', 'LOW_RATING'))) " +
                  "AND (is_read = 0 OR is_read IS NULL)";
        } else {
            // Chỉ đánh dấu notifications của user (không global)
            sql = "UPDATE Notifications SET is_read = 1 " +
                  "WHERE user_id = ? AND (is_read = 0 OR is_read IS NULL)";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            // Debug log để kiểm tra
            System.out.println("[DEBUG] markAllAsRead - userId: " + userId + ", includeAdminGlobal: " + includeAdminGlobal);
            System.out.println("[DEBUG] markAllAsRead - SQL: " + sql);
            int rowsUpdated = ps.executeUpdate();
            System.out.println("[DEBUG] markAllAsRead - rows updated: " + rowsUpdated);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] markAllAsRead failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Xóa thông báo theo quyền sở hữu
    public boolean deleteNotificationForUser(int notificationId, int userId, boolean allowAdminGlobal) {
        String sql;
        if (allowAdminGlobal) {
            sql = "DELETE FROM Notifications WHERE notification_id = ? AND (user_id = ? OR user_id IS NULL)";
        } else {
            sql = "DELETE FROM Notifications WHERE notification_id = ? AND user_id = ?";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
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

    // Tạo thông báo cho 1 user cụ thể
    public int createNotificationForUser(int userId, String notificationType, String title, String message, String linkUrl) {
        Notification notification = new Notification(userId, notificationType, title, message, linkUrl);
        return createNotification(notification);
    }

    // Tạo thông báo cho tất cả tài khoản ADMIN (mỗi admin một bản ghi riêng)
    public int[] createNotificationsForAdmins(String notificationType, String title, String message, String linkUrl) {
        DAO.UserDB userDB = new DAO.UserDB();
        java.util.List<Integer> adminIds = userDB.getAdminUserIds();
        if (adminIds == null || adminIds.isEmpty()) {
            return new int[0];
        }
        int[] createdIds = new int[adminIds.size()];
        int idx = 0;
        for (Integer adminId : adminIds) {
            try {
                int newId = createNotificationForUser(adminId, notificationType, title, message, linkUrl);
                createdIds[idx++] = newId;
            } catch (Exception e) {
                e.printStackTrace();
                createdIds[idx++] = -1;
            }
        }
        return createdIds;
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

