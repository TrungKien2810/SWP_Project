package DAO;

import Model.Discount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscountDB {

    private final DBConnect db = new DBConnect();
    private final Connection conn = db.conn;

    public Discount validateAndGetDiscount(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        String sql = "SELECT discount_id, code, name, discount_type, discount_value, ISNULL(min_order_amount, 0) AS min_order_amount, max_discount_amount " +
                     "FROM Discounts WHERE code = ? AND is_active = 1 AND start_date <= GETDATE() AND end_date >= GETDATE() " +
                     "AND (usage_limit IS NULL OR used_count < usage_limit)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal max = rs.getBigDecimal("max_discount_amount");
                    Double maxVal = (max != null) ? max.doubleValue() : null;
                    return new Discount(
                        rs.getInt("discount_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("discount_type"),
                        rs.getDouble("discount_value"),
                        rs.getDouble("min_order_amount"),
                        maxVal,
                        Boolean.TRUE,
                        null,
                        null
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====== ADMIN CRUD ======
    public java.util.List<Discount> listAll() {
        java.util.List<Discount> list = new java.util.ArrayList<>();
        String sql = "SELECT discount_id, code, name, discount_type, discount_value, ISNULL(min_order_amount, 0) AS min_order_amount, max_discount_amount, is_active, start_date, end_date FROM Discounts ORDER BY discount_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.math.BigDecimal max = rs.getBigDecimal("max_discount_amount");
                Double maxVal = (max != null) ? max.doubleValue() : null;
                list.add(new Discount(
                    rs.getInt("discount_id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("discount_type"),
                    rs.getDouble("discount_value"),
                    rs.getDouble("min_order_amount"),
                    maxVal,
                    (Boolean) rs.getObject("is_active"),
                    rs.getTimestamp("start_date"),
                    rs.getTimestamp("end_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Discount getById(int id) {
        String sql = "SELECT discount_id, code, name, discount_type, discount_value, ISNULL(min_order_amount, 0) AS min_order_amount, max_discount_amount, is_active, start_date, end_date FROM Discounts WHERE discount_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal max = rs.getBigDecimal("max_discount_amount");
                    Double maxVal = (max != null) ? max.doubleValue() : null;
                    return new Discount(
                        rs.getInt("discount_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("discount_type"),
                        rs.getDouble("discount_value"),
                        rs.getDouble("min_order_amount"),
                        maxVal,
                        (Boolean) rs.getObject("is_active"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(String code, String name, String type, double value, Double minOrderAmount, Double maxDiscountAmount, java.sql.Timestamp start, java.sql.Timestamp end, boolean isActive) {
        String sql = "INSERT INTO Discounts (code, name, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, is_active) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setDouble(4, value);
            if (minOrderAmount == null) ps.setNull(5, java.sql.Types.DECIMAL); else ps.setDouble(5, minOrderAmount);
            if (maxDiscountAmount == null) ps.setNull(6, java.sql.Types.DECIMAL); else ps.setDouble(6, maxDiscountAmount);
            ps.setTimestamp(7, start);
            ps.setTimestamp(8, end);
            ps.setBoolean(9, isActive);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int id, String code, String name, String type, double value, Double minOrderAmount, Double maxDiscountAmount, java.sql.Timestamp start, java.sql.Timestamp end, boolean isActive) {
        String sql = "UPDATE Discounts SET code=?, name=?, discount_type=?, discount_value=?, min_order_amount=?, max_discount_amount=?, start_date=?, end_date=?, is_active=? WHERE discount_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setDouble(4, value);
            if (minOrderAmount == null) ps.setNull(5, java.sql.Types.DECIMAL); else ps.setDouble(5, minOrderAmount);
            if (maxDiscountAmount == null) ps.setNull(6, java.sql.Types.DECIMAL); else ps.setDouble(6, maxDiscountAmount);
            ps.setTimestamp(7, start);
            ps.setTimestamp(8, end);
            ps.setBoolean(9, isActive);
            ps.setInt(10, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Discounts WHERE discount_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


