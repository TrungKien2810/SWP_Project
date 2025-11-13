package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletContext;

public class ProductDiscountDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn = db.conn;

    // Tạo mapping bảng DiscountProducts(discount_id, product_id)
    // Gán một mã giảm giá cho danh sách sản phẩm (bỏ qua trùng lặp)
    public int assignDiscountToProducts(int discountId, List<Integer> productIds) {
        return assignDiscountToProducts(discountId, productIds, null);
    }
    
    // Overload với ServletContext để gửi thông báo
    public int assignDiscountToProducts(int discountId, List<Integer> productIds, ServletContext context) {
        if (productIds == null || productIds.isEmpty()) return 0;
        String sql = "IF NOT EXISTS (SELECT 1 FROM DiscountProducts WHERE discount_id = ? AND product_id = ?) " +
                     "INSERT INTO DiscountProducts(discount_id, product_id, assigned_at) VALUES(?, ?, GETDATE())";
        int affected = 0;
        java.util.List<Integer> successfullyAssigned = new java.util.ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer pid : productIds) {
                if (pid == null || pid <= 0) continue;
                ps.setInt(1, discountId);
                ps.setInt(2, pid);
                ps.setInt(3, discountId);
                ps.setInt(4, pid);
                int result = ps.executeUpdate();
                if (result > 0) {
                    affected++;
                    successfullyAssigned.add(pid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Gửi thông báo cho user có sản phẩm trong wishlist
        if (affected > 0 && !successfullyAssigned.isEmpty() && context != null) {
            try {
                Util.WishlistNotificationUtil.notifyUsersAboutWishlistDiscounts(successfullyAssigned, discountId, context);
            } catch (Exception e) {
                System.err.println("[ProductDiscountDB] Lỗi khi gửi thông báo wishlist: " + e.getMessage());
                e.printStackTrace();
                // Không throw exception để không ảnh hưởng đến việc gán discount
            }
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

    // Xóa tất cả mã giảm giá khỏi tất cả sản phẩm
    public int unassignAllDiscountsFromAllProducts() {
        String sql = "DELETE FROM DiscountProducts";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Xóa tất cả mã giảm giá khỏi sản phẩm theo danh sách category IDs
    public int unassignAllDiscountsFromProductsByCategoryIds(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return 0;
        // Lọc category IDs hợp lệ
        java.util.List<Integer> validCategoryIds = new java.util.ArrayList<>();
        for (Integer cid : categoryIds) {
            if (cid != null && cid > 0) {
                validCategoryIds.add(cid);
            }
        }
        if (validCategoryIds.isEmpty()) return 0;
        
        // Tạo placeholders cho IN clause
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < validCategoryIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        
        String sql = "DELETE FROM DiscountProducts WHERE product_id IN (" +
                     "SELECT DISTINCT product_id FROM ProductCategories WHERE category_id IN (" +
                     placeholders.toString() + "))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (Integer cid : validCategoryIds) {
                ps.setInt(index++, cid);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Xóa tất cả mã giảm giá khỏi sản phẩm theo khoảng giá
    public int unassignAllDiscountsFromProductsByPriceRange(double minPrice, double maxPrice) {
        String sql = "DELETE FROM DiscountProducts WHERE product_id IN (" +
                     "SELECT product_id FROM Products WHERE price >= ? AND price <= ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


