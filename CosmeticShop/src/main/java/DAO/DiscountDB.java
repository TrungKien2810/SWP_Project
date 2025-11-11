package DAO;

import Model.Discount;
import Model.UserDiscountAssign;
import Model.Notification;
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
        String sql = "SELECT discount_id, code, name, discount_type, discount_value, ISNULL(min_order_amount, 0) AS min_order_amount, max_discount_amount, is_active, start_date, end_date, " +
                     "description, usage_limit, condition_type, condition_value, condition_description, special_event, auto_assign_all, assign_date " +
                     "FROM Discounts ORDER BY discount_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.math.BigDecimal max = rs.getBigDecimal("max_discount_amount");
                Double maxVal = (max != null) ? max.doubleValue() : null;
                
                Discount discount = new Discount(
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
                
                // Set các trường mở rộng
                discount.setDescription(rs.getString("description"));
                discount.setUsageLimit(rs.getObject("usage_limit") != null ? rs.getInt("usage_limit") : null);
                discount.setConditionType(rs.getString("condition_type"));
                discount.setConditionValue(rs.getObject("condition_value") != null ? rs.getDouble("condition_value") : null);
                discount.setConditionDescription(rs.getString("condition_description"));
                discount.setSpecialEvent(rs.getObject("special_event") != null ? rs.getBoolean("special_event") : null);
                discount.setAutoAssignAll(rs.getObject("auto_assign_all") != null ? rs.getBoolean("auto_assign_all") : null);
                discount.setAssignDate(rs.getTimestamp("assign_date"));
                
                list.add(discount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Discount getById(int id) {
        String sql = "SELECT discount_id, code, name, discount_type, discount_value, ISNULL(min_order_amount, 0) AS min_order_amount, max_discount_amount, is_active, start_date, end_date, " +
                     "description, usage_limit, condition_type, condition_value, condition_description, special_event, auto_assign_all, assign_date " +
                     "FROM Discounts WHERE discount_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal max = rs.getBigDecimal("max_discount_amount");
                    Double maxVal = (max != null) ? max.doubleValue() : null;
                    
                    Discount discount = new Discount(
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
                    
                    // Set các trường mở rộng
                    discount.setDescription(rs.getString("description"));
                    discount.setUsageLimit(rs.getObject("usage_limit") != null ? rs.getInt("usage_limit") : null);
                    discount.setConditionType(rs.getString("condition_type"));
                    discount.setConditionValue(rs.getObject("condition_value") != null ? rs.getDouble("condition_value") : null);
                    discount.setConditionDescription(rs.getString("condition_description"));
                    discount.setSpecialEvent(rs.getObject("special_event") != null ? rs.getBoolean("special_event") : null);
                    discount.setAutoAssignAll(rs.getObject("auto_assign_all") != null ? rs.getBoolean("auto_assign_all") : null);
                    discount.setAssignDate(rs.getTimestamp("assign_date"));
                    
                    System.out.println("[DiscountDB] Loaded discount ID " + id + ": " + discount.getCode() + 
                        ", autoAssignAll=" + discount.getAutoAssignAll() + 
                        ", conditionType=" + discount.getConditionType() + 
                        ", conditionValue=" + discount.getConditionValue());
                    
                    return discount;
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
            boolean result = ps.executeUpdate() > 0;
            
            // Nếu tạo voucher với auto_assign_all = true và assign_date <= hiện tại, gán cho tất cả user
            if (result && autoAssignAll != null && autoAssignAll && 
                (assignDate == null || assignDate.getTime() <= System.currentTimeMillis())) {
                this.assignToAllUsers();
            }
            
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int id, String code, String name, String type, double value, Double minOrderAmount, Double maxDiscountAmount, java.sql.Timestamp start, java.sql.Timestamp end, boolean isActive,
                          String description, Integer usageLimit, String conditionType, Double conditionValue, String conditionDescription, Boolean specialEvent, Boolean autoAssignAll, java.sql.Timestamp assignDate) {
        try {
            // Lấy thông tin voucher cũ để so sánh
            Discount oldDiscount = getById(id);
            boolean wasAutoAssigned = (oldDiscount != null && 
                isAutoAssignedVoucher(oldDiscount.getAutoAssignAll(), oldDiscount.getConditionType()));
            
            // Cập nhật voucher
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
                boolean updateSuccess = ps.executeUpdate() > 0;
                
                if (updateSuccess) {
                    // Kiểm tra xem có chuyển từ AUTO về MANUAL không
                    boolean isNowManual = isManualVoucher(autoAssignAll, conditionType);
                    
                    if (wasAutoAssigned && isNowManual) {
                        // Xóa tất cả UserVouchers của voucher này khi chuyển về MANUAL
                        String deleteUserVouchers = "DELETE FROM UserVouchers WHERE discount_id = ?";
                        try (PreparedStatement deletePs = conn.prepareStatement(deleteUserVouchers)) {
                            deletePs.setInt(1, id);
                            int deletedCount = deletePs.executeUpdate();
                            System.out.println("[DiscountDB] Deleted " + deletedCount + " UserVouchers for discount ID " + id + " (converted to MANUAL)");
                        }
                    }
                }
                
                return updateSuccess;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try {
            // Xóa các UserVouchers liên quan trước
            String deleteUserVouchers = "DELETE FROM UserVouchers WHERE discount_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteUserVouchers)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            // Cập nhật Orders để set discount_id = NULL thay vì xóa
            String updateOrders = "UPDATE Orders SET discount_id = NULL WHERE discount_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateOrders)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            // Sau đó xóa Discount
            String sql = "DELETE FROM Discounts WHERE discount_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                boolean result = ps.executeUpdate() > 0;
                System.out.println("[DiscountDB] Delete discount ID " + id + ": " + (result ? "success" : "failed"));
                return result;
            }
        } catch (SQLException e) {
            System.out.println("[DiscountDB] Error deleting discount ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method để kiểm tra voucher có phải MANUAL không
    private boolean isManualVoucher(Boolean autoAssignAll, String conditionType) {
        return (autoAssignAll == null || !autoAssignAll) && 
               (conditionType == null || conditionType.trim().isEmpty());
    }
    
    // Helper method để kiểm tra voucher có được gán tự động không
    private boolean isAutoAssignedVoucher(Boolean autoAssignAll, String conditionType) {
        return Boolean.TRUE.equals(autoAssignAll) || 
               (conditionType != null && !conditionType.trim().isEmpty());
    }

    // ====== USER-ASSIGNED DISCOUNTS ======
    public java.util.List<UserDiscountAssign> listAssignedDiscountsForUser(int userId) {
        // Chỉ lấy voucher chưa sử dụng và user có quyền sử dụng
        String sql = "SELECT d.discount_id, d.code, d.name, d.discount_type, d.discount_value, " +
                     "ISNULL(d.min_order_amount,0) AS min_order_amount, d.max_discount_amount, d.start_date, d.end_date, " +
                     "CASE " +
                     "  WHEN uv.status = 'UNUSED' THEN 1 " + // Voucher được gán trực tiếp và chưa dùng
                     "  WHEN uv.user_id IS NULL AND d.auto_assign_all = 1 THEN 1 " + // Voucher tự động gán chưa có record
                     "  WHEN uv.user_id IS NULL AND d.condition_type IS NOT NULL AND d.condition_type != '' THEN " + 
                     "    CASE d.condition_type " +
                     "      WHEN 'ORDER_COUNT' THEN " +
                     "        CASE WHEN (SELECT COUNT(*) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value THEN 1 ELSE 0 END " +
                     "      WHEN 'TOTAL_SPENT' THEN " +
                     "        CASE WHEN (SELECT ISNULL(SUM(total_amount), 0) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value THEN 1 ELSE 0 END " +
                     "      WHEN 'FIRST_ORDER' THEN " +
                     "        CASE WHEN EXISTS(SELECT 1 FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') THEN 1 ELSE 0 END " +
                     "      ELSE 1 " +
                     "    END " +
                     "  ELSE 0 " +
                     "END AS remaining_uses " +
                     "FROM Discounts d " +
                     "LEFT JOIN UserVouchers uv ON d.discount_id = uv.discount_id AND uv.user_id = ? " +
                     "WHERE d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "AND (" +
                     "  (uv.user_id IS NOT NULL AND uv.status = 'UNUSED') " + // Voucher được gán trực tiếp và chưa dùng
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.auto_assign_all = 1 AND (d.assign_date IS NULL OR d.assign_date <= GETDATE())) " + // Voucher tự động gán cho tất cả
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.condition_type = 'ORDER_COUNT' AND (SELECT COUNT(*) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value) " +
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.condition_type = 'TOTAL_SPENT' AND (SELECT ISNULL(SUM(total_amount), 0) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value) " +
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.condition_type = 'FIRST_ORDER' AND EXISTS(SELECT 1 FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED')) " +
                     ") " +
                     "ORDER BY d.end_date ASC";
        java.util.List<UserDiscountAssign> list = new java.util.ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); // for ORDER_COUNT check
            ps.setInt(2, userId); // for TOTAL_SPENT check  
            ps.setInt(3, userId); // for FIRST_ORDER check
            ps.setInt(4, userId); // for LEFT JOIN UserVouchers
            ps.setInt(5, userId); // for ORDER_COUNT condition
            ps.setInt(6, userId); // for TOTAL_SPENT condition
            ps.setInt(7, userId); // for FIRST_ORDER condition
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
        System.out.println("[DiscountDB] Found " + list.size() + " vouchers for user " + userId);
        return list;
    }

    // Kiểm tra user có được phép sử dụng voucher này không
    public boolean canUserUseDiscount(int userId, int discountId) {
        String sql = "SELECT COUNT(*) FROM Discounts d " +
                     "LEFT JOIN UserVouchers uv ON d.discount_id = uv.discount_id AND uv.user_id = ? " +
                     "WHERE d.discount_id = ? AND d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "AND (" +
                     "  (d.condition_type IS NULL AND (d.auto_assign_all IS NULL OR d.auto_assign_all = 0)) " + // MANUAL voucher - ai cũng dùng được
                     "  OR " +
                     "  (uv.user_id IS NOT NULL AND uv.status = 'UNUSED') " + // Được gán trực tiếp và chưa dùng
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.auto_assign_all = 1 AND (d.assign_date IS NULL OR d.assign_date <= GETDATE())) " + // Auto-assign cho tất cả
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.condition_type = 'ORDER_COUNT' AND (SELECT COUNT(*) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value) " +
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.condition_type = 'TOTAL_SPENT' AND (SELECT ISNULL(SUM(total_amount), 0) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value) " +
                     "  OR " +
                     "  (uv.user_id IS NULL AND d.condition_type = 'FIRST_ORDER' AND EXISTS(SELECT 1 FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED')) " +
                     ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, discountId);
            ps.setInt(3, userId); // for ORDER_COUNT condition
            ps.setInt(4, userId); // for TOTAL_SPENT condition
            ps.setInt(5, userId); // for FIRST_ORDER condition
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean decrementAssignedDiscountUse(int userId, int discountId) {
        // Đánh dấu đã dùng một voucher được gán cho user (trạng thái USED)
        // Nếu voucher tự động gán chưa có trong UserVouchers, tạo record mới với status USED
        String sql = "MERGE UserVouchers AS target " +
                     "USING (SELECT ? AS user_id, ? AS discount_id) AS source " +
                     "ON target.user_id = source.user_id AND target.discount_id = source.discount_id " +
                     "WHEN MATCHED THEN " +
                     "  UPDATE SET status = 'USED', used_date = GETDATE() " +
                     "WHEN NOT MATCHED THEN " +
                     "  INSERT (user_id, discount_id, status, used_date) " +
                     "  VALUES (source.user_id, source.discount_id, 'USED', GETDATE());";
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
        System.out.println("[DEBUG] Checking vouchers eligible for user " + userId + ":");
        NotificationDB notificationDB = new NotificationDB();
        
        // Gán voucher AUTO_ALL
        String autoAllSql = "INSERT INTO UserVouchers(user_id, discount_id) " +
                     "SELECT ?, d.discount_id " +
                     "FROM Discounts d " +
                     "WHERE d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "AND d.auto_assign_all = 1 " +
                     "AND (d.assign_date IS NULL OR d.assign_date <= GETDATE()) " +
                     "AND NOT EXISTS (SELECT 1 FROM UserVouchers uv WHERE uv.user_id = ? AND uv.discount_id = d.discount_id)";
        try (PreparedStatement ps = conn.prepareStatement(autoAllSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            int assigned = ps.executeUpdate();
            System.out.println("[DEBUG] Assigned " + assigned + " AUTO_ALL vouchers to user " + userId);
            
            // Tạo thông báo cho user khi nhận mã giảm giá
            if (assigned > 0) {
                createDiscountNotifications(userId, "AUTO_ALL", notificationDB);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Gán voucher theo điều kiện ORDER_COUNT
        String orderCountSql = "INSERT INTO UserVouchers(user_id, discount_id) " +
                     "SELECT ?, d.discount_id " +
                     "FROM Discounts d " +
                     "WHERE d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "AND d.condition_type = 'ORDER_COUNT' " +
                     "AND (SELECT COUNT(*) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value " +
                     "AND NOT EXISTS (SELECT 1 FROM UserVouchers uv WHERE uv.user_id = ? AND uv.discount_id = d.discount_id)";
        try (PreparedStatement ps = conn.prepareStatement(orderCountSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            int assigned = ps.executeUpdate();
            System.out.println("[DEBUG] Assigned " + assigned + " ORDER_COUNT vouchers to user " + userId);
            
            // Tạo thông báo cho user khi nhận mã giảm giá
            if (assigned > 0) {
                createDiscountNotifications(userId, "ORDER_COUNT", notificationDB);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Gán voucher theo điều kiện TOTAL_SPENT
        String totalSpentSql = "INSERT INTO UserVouchers(user_id, discount_id) " +
                     "SELECT ?, d.discount_id " +
                     "FROM Discounts d " +
                     "WHERE d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "AND d.condition_type = 'TOTAL_SPENT' " +
                     "AND (SELECT ISNULL(SUM(total_amount), 0) FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') >= d.condition_value " +
                     "AND NOT EXISTS (SELECT 1 FROM UserVouchers uv WHERE uv.user_id = ? AND uv.discount_id = d.discount_id)";
        try (PreparedStatement ps = conn.prepareStatement(totalSpentSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            int assigned = ps.executeUpdate();
            System.out.println("[DEBUG] Assigned " + assigned + " TOTAL_SPENT vouchers to user " + userId);
            
            // Tạo thông báo cho user khi nhận mã giảm giá
            if (assigned > 0) {
                createDiscountNotifications(userId, "TOTAL_SPENT", notificationDB);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Gán voucher theo điều kiện FIRST_ORDER
        String firstOrderSql = "INSERT INTO UserVouchers(user_id, discount_id) " +
                     "SELECT ?, d.discount_id " +
                     "FROM Discounts d " +
                     "WHERE d.is_active = 1 " +
                     "AND d.start_date <= GETDATE() AND d.end_date >= GETDATE() " +
                     "AND d.condition_type = 'FIRST_ORDER' " +
                     "AND EXISTS (SELECT 1 FROM Orders WHERE user_id = ? AND order_status = 'COMPLETED') " +
                     "AND NOT EXISTS (SELECT 1 FROM UserVouchers uv WHERE uv.user_id = ? AND uv.discount_id = d.discount_id)";
        try (PreparedStatement ps = conn.prepareStatement(firstOrderSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            int assigned = ps.executeUpdate();
            System.out.println("[DEBUG] Assigned " + assigned + " FIRST_ORDER vouchers to user " + userId);
            
            // Tạo thông báo cho user khi nhận mã giảm giá
            if (assigned > 0) {
                createDiscountNotifications(userId, "FIRST_ORDER", notificationDB);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Helper method để tạo thông báo khi user nhận mã giảm giá
    private void createDiscountNotifications(int userId, String conditionType, NotificationDB notificationDB) {
        try {
            // Lấy thông tin các mã giảm giá vừa được gán
            String sql = "SELECT TOP 5 d.name, d.code, d.discount_type, d.discount_value " +
                         "FROM UserVouchers uv " +
                         "JOIN Discounts d ON uv.discount_id = d.discount_id " +
                         "WHERE uv.user_id = ? AND uv.status = 'UNUSED' " +
                         "ORDER BY uv.created_at DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    StringBuilder discountNames = new StringBuilder();
                    while (rs.next() && count < 3) {
                        if (count > 0) discountNames.append(", ");
                        discountNames.append(rs.getString("name"));
                        count++;
                    }
                    
                    if (count > 0) {
                        String title = "Bạn đã nhận mã giảm giá mới!";
                        String message;
                        if (count == 1) {
                            message = String.format("Bạn đã nhận được mã giảm giá '%s'. Hãy sử dụng ngay!", discountNames.toString());
                        } else {
                            message = String.format("Bạn đã nhận được %d mã giảm giá mới: %s. Hãy kiểm tra ngay!", count, discountNames.toString());
                        }
                        String linkUrl = "/my-promos";
                        
                        Notification notification = new Notification(userId, "DISCOUNT_ASSIGNED", title, message, linkUrl);
                        notificationDB.createNotification(notification);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Assign voucher to all users (for auto-assign vouchers)
    private void assignToAllUsers() {
        String sql = "INSERT INTO UserVouchers(user_id, discount_id) " +
                     "SELECT u.user_id, d.discount_id " +
                     "FROM Users u CROSS JOIN Discounts d " +
                     "WHERE d.is_active = 1 " +
                     "AND d.auto_assign_all = 1 " +
                     "AND (d.assign_date IS NULL OR d.assign_date <= GETDATE()) " +
                     "AND NOT EXISTS (SELECT 1 FROM UserVouchers uv WHERE uv.user_id = u.user_id AND uv.discount_id = d.discount_id)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


