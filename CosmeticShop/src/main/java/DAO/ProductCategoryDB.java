package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn = db.conn;

    // Lấy danh sách category IDs của một product
    public List<Integer> getCategoryIdsByProductId(int productId) {
        List<Integer> categoryIds = new ArrayList<>();
        String sql = "SELECT category_id FROM ProductCategories WHERE product_id = ? ORDER BY created_at ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categoryIds.add(rs.getInt("category_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryIds;
    }

    // Lấy danh sách category names của một product
    public List<String> getCategoryNamesByProductId(int productId) {
        List<String> categoryNames = new ArrayList<>();
        String sql = "SELECT c.name FROM ProductCategories pc " +
                     "JOIN Categories c ON pc.category_id = c.category_id " +
                     "WHERE pc.product_id = ? ORDER BY c.name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categoryNames.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryNames;
    }

    // Gán categories cho product (xóa cũ, thêm mới)
    public boolean assignCategoriesToProduct(int productId, List<Integer> categoryIds) {
        java.util.LinkedHashSet<Integer> uniqueCategoryIds = new java.util.LinkedHashSet<>();
        if (categoryIds != null) {
            for (Integer categoryId : categoryIds) {
                if (categoryId != null && categoryId > 0) {
                    uniqueCategoryIds.add(categoryId);
                }
            }
        }

        try {
            conn.setAutoCommit(false);
            
            // Xóa tất cả categories cũ
            String deleteSql = "DELETE FROM ProductCategories WHERE product_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, productId);
                ps.executeUpdate();
            }
            
            // Thêm categories mới
            if (!uniqueCategoryIds.isEmpty()) {
                String insertSql = "INSERT INTO ProductCategories (product_id, category_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    for (Integer categoryId : uniqueCategoryIds) {
                        ps.setInt(1, productId);
                        ps.setInt(2, categoryId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Lấy danh sách product IDs theo category name (OR logic - thuộc bất kỳ category nào)
    public List<Integer> getProductIdsByCategoryName(String categoryName) {
        List<Integer> productIds = new ArrayList<>();
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return productIds;
        }
        
        String sql = "SELECT DISTINCT pc.product_id FROM ProductCategories pc " +
                     "JOIN Categories c ON pc.category_id = c.category_id " +
                     "WHERE c.name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productIds.add(rs.getInt("product_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productIds;
    }

    // Lấy danh sách product IDs có TẤT CẢ các categories được chỉ định (AND logic)
    public List<Integer> getProductIdsByAllCategoryNames(List<String> categoryNames) {
        List<Integer> productIds = new ArrayList<>();
        if (categoryNames == null || categoryNames.isEmpty()) {
            return productIds;
        }
        
        // Filter empty category names
        List<String> validCategoryNames = new ArrayList<>();
        for (String catName : categoryNames) {
            if (catName != null && !catName.trim().isEmpty()) {
                validCategoryNames.add(catName.trim());
            }
        }
        
        if (validCategoryNames.isEmpty()) {
            return productIds;
        }
        
        // Query: Tìm sản phẩm có TẤT CẢ các categories
        // Sử dụng GROUP BY và HAVING COUNT để đảm bảo sản phẩm có đủ số lượng categories
        StringBuilder sql = new StringBuilder(
            "SELECT pc.product_id " +
            "FROM ProductCategories pc " +
            "JOIN Categories c ON pc.category_id = c.category_id " +
            "WHERE c.name IN ("
        );
        
        // Tạo placeholders cho IN clause
        for (int i = 0; i < validCategoryNames.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(") ");
        sql.append("GROUP BY pc.product_id ");
        sql.append("HAVING COUNT(DISTINCT c.name) = ?"); // Phải có đủ số lượng categories
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            // Set các category names
            for (String categoryName : validCategoryNames) {
                ps.setString(paramIndex++, categoryName);
            }
            // Set số lượng categories cần có
            ps.setInt(paramIndex, validCategoryNames.size());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productIds.add(rs.getInt("product_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productIds;
    }

    // Xóa tất cả categories của một product
    public boolean removeAllCategoriesFromProduct(int productId) {
        String sql = "DELETE FROM ProductCategories WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() >= 0; // >= 0 vì có thể không có category nào
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

