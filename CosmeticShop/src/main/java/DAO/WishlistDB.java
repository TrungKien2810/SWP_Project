package DAO;

import Model.Wishlist;
import Model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn = db.conn;

    // Thêm sản phẩm vào wishlist
    public boolean addToWishlist(int userId, int productId) {
        String sql = "INSERT INTO Wishlist (user_id, product_id, created_at) VALUES (?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Nếu sản phẩm đã có trong wishlist (UNIQUE constraint), bỏ qua lỗi
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                return false; // Đã tồn tại
            }
            e.printStackTrace();
            return false;
        }
    }

    // Xóa sản phẩm khỏi wishlist
    public boolean removeFromWishlist(int userId, int productId) {
        String sql = "DELETE FROM Wishlist WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra sản phẩm có trong wishlist không
    public boolean isInWishlist(int userId, int productId) {
        String sql = "SELECT COUNT(*) as count FROM Wishlist WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả sản phẩm trong wishlist của user (có thông tin sản phẩm)
    public List<Wishlist> getWishlistByUserId(int userId) {
        List<Wishlist> wishlist = new ArrayList<>();
        String sql = "SELECT w.wishlist_id, w.user_id, w.product_id, w.created_at, " +
                     "p.product_id, p.name, p.price, p.stock, p.description, p.image_url " +
                     "FROM Wishlist w " +
                     "JOIN Products p ON w.product_id = p.product_id " +
                     "WHERE w.user_id = ? " +
                     "ORDER BY w.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Wishlist item = new Wishlist();
                    item.setWishlistId(rs.getInt("wishlist_id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setProductId(rs.getInt("product_id"));
                    if (rs.getTimestamp("created_at") != null) {
                        item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }

                    // Load thông tin sản phẩm
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        0 // categoryId, sẽ load sau nếu cần
                    );
                    item.setProduct(product);
                    wishlist.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishlist;
    }

    // Đếm số sản phẩm trong wishlist
    public int getWishlistCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM Wishlist WHERE user_id = ?";
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

    // Lấy danh sách user_id có sản phẩm trong wishlist (dùng để gửi thông báo khi sản phẩm được giảm giá)
    public List<Integer> getUserIdsWithProductInWishlist(int productId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT DISTINCT user_id FROM Wishlist WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }

    // Lấy danh sách user_id có bất kỳ sản phẩm nào trong danh sách productIds trong wishlist
    public List<Integer> getUserIdsWithProductsInWishlist(List<Integer> productIds) {
        List<Integer> userIds = new ArrayList<>();
        if (productIds == null || productIds.isEmpty()) {
            return userIds;
        }

        // Tạo placeholders cho IN clause
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < productIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sql = "SELECT DISTINCT user_id FROM Wishlist WHERE product_id IN (" + placeholders.toString() + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (Integer productId : productIds) {
                ps.setInt(index++, productId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }
}

