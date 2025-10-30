package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Model.lienhe;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class lienheDAO {

    public boolean insertContact(lienhe contact) {
        String sql = "INSERT INTO lienhe (name, phone, address, email, subject, message) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            // ✅ Dùng DBConnect hiện tại của bạn
            DBConnect db = new DBConnect();
            conn = db.conn;

            if (conn == null) {
                System.out.println("❌ Kết nối SQL thất bại (conn = null)");
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, contact.getName());
            ps.setString(2, contact.getPhone());
            ps.setString(3, contact.getAddress());
            ps.setString(4, contact.getEmail());
            ps.setString(5, contact.getSubject());
            ps.setString(6, contact.getMessage());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Dữ liệu liên hệ đã được lưu vào SQL (" + rows + " dòng)");
                return true;
            } else {
                System.out.println("⚠️ Không có dòng nào được chèn vào SQL");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("❌ Lỗi SQL khi thêm liên hệ: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null && !conn.isClosed()) {
                    conn.close(); // ✅ đóng kết nối để servlet không bị treo
                    System.out.println("🔒 Đã đóng kết nối SQL Server.");
                }
            } catch (SQLException e) {
                System.out.println("⚠️ Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }

    public List<lienhe> getAllContacts() {
        List<lienhe> list = new ArrayList<>();
        String sql = "SELECT id, name, phone, address, email, subject, message, status FROM lienhe ORDER BY id DESC";
        try {
            DBConnect db = new DBConnect();
            Connection conn = db.conn;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lienhe c = new lienhe(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("subject"),
                        rs.getString("message"),
                        rs.getBoolean("status"));
                list.add(c);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi lấy danh sách liên hệ: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int id, boolean status) {
        String sql = "UPDATE lienhe SET status = ? WHERE id = ?";
        try {
            DBConnect db = new DBConnect();
            Connection conn = db.conn;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, status);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            conn.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi cập nhật trạng thái liên hệ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
