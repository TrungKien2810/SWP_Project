package DAO;

import Model.Discount;
import Model.UserDiscountAssign;
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

    public boolean create(String code, String name, String type, double value, Double minOrderAmount, Double maxDiscountAmount, java.sql.Timestamp start, java.sql.Timestamp end, boolean isActive,
                          String description, Integer usageLimit, String conditionType, Double conditionValue, String conditionDescription, Boolean specialEvent, Boolean autoAssignAll, java.sql.Timestamp assignDate) {
        String sql = "INSERT INTO Discounts (code, name, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, is_active, description, usage_limit, condition_type, condition_value, condition_description, special_event, auto_assign_all, assign_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            if (description == null) ps.setNull(10, java.sql.Types.NVARCHAR); else ps.setString(10, description);
            if (usageLimit == null) ps.setNull(11, java.sql.Types.INTEGER); else ps.setInt(11, usageLimit);
            if (conditionType == null) ps.setNull(12, java.sql.Types.NVARCHAR); else ps.setString(12, conditionType);
            if (conditionValue == null) ps.setNull(13, java.sql.Types.DECIMAL); else ps.setDouble(13, conditionValue);
            if (conditionDescription == null) ps.setNull(14, java.sql.Types.NVARCHAR); else ps.setString(14, conditionDescription);
            if (specialEvent == null) ps.setNull(15, java.sql.Types.BIT); else ps.setBoolean(15, specialEvent);
            if (autoAssignAll == null) ps.setNull(16, java.sql.Types.BIT); else ps.setBoolean(16, autoAssignAll);
            if (assignDate == null) ps.setNull(17, java.sql.Types.TIMESTAMP); else ps.setTimestamp(17, assignDate);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int id, String code, String name, String type, double value, Double minOrderAmount, Double maxDiscountAmount, java.sql.Timestamp start, java.sql.Timestamp end, boolean isActive,
                          String description, Integer usageLimit, String conditionType, Double conditionValue, String conditionDescription, Boolean specialEvent, Boolean autoAssignAll, java.sql.Timestamp assignDate) {
        String sql = "UPDATE Discounts SET code=?, name=?, discount_type=?, discount_value=?, min_order_amount=?, max_discount_amount=?, start_date=?, end_date=?, is_active=?, description=?, usage_limit=?, condition_type=?, condition_value=?, condition_description=?, special_event=?, auto_assign_all=?, assign_date=? WHERE discount_id=?";
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
            if (description == null) ps.setNull(10, java.sql.Types.NVARCHAR); else ps.setString(10, description);
            if (usageLimit == null) ps.setNull(11, java.sql.Types.INTEGER); else ps.setInt(11, usageLimit);
            if (conditionType == null) ps.setNull(12, java.sql.Types.NVARCHAR); else ps.setString(12, conditionType);
            if (conditionValue == null) ps.setNull(13, java.sql.Types.DECIMAL); else ps.setDouble(13, conditionValue);
            if (conditionDescription == null) ps.setNull(14, java.sql.Types.NVARCHAR); else ps.setString(14, conditionDescription);
            if (specialEvent == null) ps.setNull(15, java.sql.Types.BIT); else ps.setBoolean(15, specialEvent);
            if (autoAssignAll == null) ps.setNull(16, java.sql.Types.BIT); else ps.setBoolean(16, autoAssignAll);
            if (assignDate == null) ps.setNull(17, java.sql.Types.TIMESTAMP); else ps.setTimestamp(17, assignDate);
            ps.setInt(18, id);
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

    // ====== USER-ASSIGNED DISCOUNTS ======
    public java.util.List<UserDiscountAssign> listAssignedDiscountsForUser(int userId) {
        // Dựa trên bảng UserVouchers trong ProjectDB.sql
        String sql = "SELECT d.discount_id, d.code, d.name, d.discount_type, d.discount_value, " +
                     "ISNULL(d.min_order_amount,0) AS min_order_amount, d.max_discount_amount, d.start_date, d.end_date, " +
                     "CASE WHEN uv.status = 'UNUSED' THEN 1 ELSE 0 END AS remaining_uses " +
                     "FROM UserVouchers uv JOIN Discounts d ON uv.discount_id = d.discount_id " +
                     "WHERE uv.user_id = ? AND uv.status = 'UNUSED' AND d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "ORDER BY d.end_date ASC";
        java.util.List<UserDiscountAssign> list = new java.util.ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.math.BigDecimal max = rs.getBigDecimal("max_discount_amount");
                    Double maxVal = (max != null) ? max.doubleValue() : null;
                    list.add(new UserDiscountAssign(
                        rs.getInt("discount_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("discount_type"),
                        rs.getDouble("discount_value"),
                        rs.getDouble("min_order_amount"),
                        maxVal,
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getInt("remaining_uses")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean decrementAssignedDiscountUse(int userId, int discountId) {
        // Đánh dấu đã dùng một voucher được gán cho user (trạng thái USED)
        String sql = "UPDATE UserVouchers SET status = 'USED', used_date = GETDATE() " +
                     "WHERE user_id = ? AND discount_id = ? AND status = 'UNUSED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, discountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Assign all due auto-assign discounts to a specific user, idempotent
    public void assignDueForUser(int userId) {
        String sql = "INSERT INTO UserVouchers(user_id, discount_id) " +
                     "SELECT ?, d.discount_id " +
                     "FROM Discounts d " +
                     "WHERE d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "AND d.auto_assign_all = 1 " +
                     "AND (d.assign_date IS NULL OR d.assign_date <= GETDATE()) " +
                     "AND NOT EXISTS (SELECT 1 FROM UserVouchers uv WHERE uv.user_id = ? AND uv.discount_id = d.discount_id)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


