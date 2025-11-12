package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProductDiscountDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn = db.conn;

    // Tạo mapping bảng DiscountProducts(discount_id, product_id)
    // Gán một mã giảm giá cho danh sách sản phẩm (bỏ qua trùng lặp)
    public int assignDiscountToProducts(int discountId, List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) return 0;
        String sql = "IF NOT EXISTS (SELECT 1 FROM DiscountProducts WHERE discount_id = ? AND product_id = ?) " +
                     "INSERT INTO DiscountProducts(discount_id, product_id, assigned_at) VALUES(?, ?, GETDATE())";
        int affected = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer pid : productIds) {
                if (pid == null || pid <= 0) continue;
                ps.setInt(1, discountId);
                ps.setInt(2, pid);
                ps.setInt(3, discountId);
                ps.setInt(4, pid);
                affected += ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affected;
    }

    // Bỏ gán mã giảm giá khỏi danh sách sản phẩm
    public int unassignDiscountFromProducts(int discountId, List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) return 0;
        int affected = 0;
        String sql = "DELETE FROM DiscountProducts WHERE discount_id = ? AND product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer pid : productIds) {
                if (pid == null || pid <= 0) continue;
                ps.setInt(1, discountId);
                ps.setInt(2, pid);
                affected += ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affected;
    }

    // Xóa tất cả mã giảm giá hiện có của danh sách sản phẩm
    public int unassignAllDiscountsFromProducts(List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) return 0;
        int affected = 0;
        String sql = "DELETE FROM DiscountProducts WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer pid : productIds) {
                if (pid == null || pid <= 0) continue;
                ps.setInt(1, pid);
                affected += ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affected;
    }
}


