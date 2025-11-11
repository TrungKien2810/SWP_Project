/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.user;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ADMIN
 */
public class UserDB {

    DBConnect db = new DBConnect();
    Connection conn = db.conn;

    public user getUserByEmail(String email) {
        String sql = "select user_id, full_name as username, email, phone, password, role, date_create, avatar_url from Users where email = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user u = new user(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("date_create").toLocalDateTime()
                );
                try { u.setAvatarUrl(rs.getString("avatar_url")); } catch (Exception ignore) {}
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    public user getUserById(int userId) {
        String sql = "select user_id, full_name as username, email, phone, password, role, date_create, avatar_url from Users where user_id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user u = new user(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("date_create").toLocalDateTime()
                );
                try { u.setAvatarUrl(rs.getString("avatar_url")); } catch (Exception ignore) {}
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean signup(String username, String email, String password) {
        String sql = "insert into Users(full_name, email, password, role) values (?, ?, ?, ?)";
        try {
            user check = getUserByEmail(email);
            if (check != null) {
                return false;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, "USER"); // Mặc định role là "USER"
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String email, String password) {
        user check = getUserByEmail(email);
        if (check == null) {
            return false;
        } else {
            String sql = "select password from Users where email = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("password").equals(password);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }


    public boolean setResetToken(String email, String token, java.sql.Timestamp expiresAt) {
        String sql = "update Users set reset_token = ?, reset_token_expiry = ? where email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            ps.setTimestamp(2, expiresAt);
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public user getUserByResetToken(String token) {
        String sql = "select user_id, full_name as username, email, phone, password, role, date_create, avatar_url from Users where reset_token = ? and reset_token_expiry > GETDATE()";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user u = new user(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("date_create").toLocalDateTime()
                );
                try { u.setAvatarUrl(rs.getString("avatar_url")); } catch (Exception ignore) {}
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePasswordByToken(String token, String newPassword) {
        String sql = "update Users set password = ?, reset_token = NULL, reset_token_expiry = NULL where reset_token = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setString(2, token);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "update Users set password = ? where email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAvatarUrl(int userId, String avatarUrl) {
        String sql = "update Users set avatar_url = ? where user_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, avatarUrl);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFullName(int userId, String fullName) {
        String sql = "update Users set full_name = ? where user_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fullName);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tất cả users
    public java.util.List<user> getAllUsers() {
        java.util.List<user> users = new java.util.ArrayList<>();
        String sql = "SELECT user_id, full_name as username, email, phone, password, role, date_create, avatar_url FROM Users ORDER BY date_create DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user u = new user(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("date_create").toLocalDateTime()
                );
                try { u.setAvatarUrl(rs.getString("avatar_url")); } catch (Exception ignore) {}
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // Tìm kiếm users theo tên, email, phone
    public java.util.List<user> searchUsers(String keyword) {
        java.util.List<user> users = new java.util.ArrayList<>();
        String sql = "SELECT user_id, full_name as username, email, phone, password, role, date_create, avatar_url " +
                     "FROM Users " +
                     "WHERE full_name LIKE ? OR email LIKE ? OR phone LIKE ? " +
                     "ORDER BY date_create DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user u = new user(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("date_create").toLocalDateTime()
                );
                try { u.setAvatarUrl(rs.getString("avatar_url")); } catch (Exception ignore) {}
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // Lấy users theo role
    public java.util.List<user> getUsersByRole(String role) {
        java.util.List<user> users = new java.util.ArrayList<>();
        String sql = "SELECT user_id, full_name as username, email, phone, password, role, date_create, avatar_url " +
                     "FROM Users WHERE role = ? ORDER BY date_create DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user u = new user(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("date_create").toLocalDateTime()
                );
                try { u.setAvatarUrl(rs.getString("avatar_url")); } catch (Exception ignore) {}
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Lấy danh sách user_id của tất cả tài khoản ADMIN
    public java.util.List<Integer> getAdminUserIds() {
        java.util.List<Integer> adminIds = new java.util.ArrayList<>();
        String sql = "SELECT user_id FROM Users WHERE role = 'ADMIN'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                adminIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adminIds;
    }
    
    // Cập nhật role của user
    public boolean updateRole(int userId, String newRole) {
        String sql = "UPDATE Users SET role = ? WHERE user_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
