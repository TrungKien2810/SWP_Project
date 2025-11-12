package DAO;

import Model.Discount;
import Model.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProductDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn;

    public ProductDB() {
        this.conn = db.conn;
    }

    // Lấy tất cả sản phẩm (Read)
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productList.add(createProductWithImages(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Gợi ý tìm kiếm: trả về danh sách sản phẩm rút gọn theo tên
    public List<Suggestion> suggestProducts(String term, int limit) {
        List<Suggestion> list = new ArrayList<>();
        if (term == null || term.trim().isEmpty()) return list;
        int safeLimit = Math.max(1, Math.min(limit, 20));
        String sql = "SELECT TOP " + safeLimit + " product_id, name, price, image_url " +
                     "FROM Products " +
                     "WHERE name LIKE ? OR description LIKE ? " +
                     "ORDER BY CASE WHEN name LIKE ? THEN 0 ELSE 1 END, product_id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + term + "%";
            String starts = term + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, starts);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Suggestion(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getString("image_url")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Kiểu dữ liệu rút gọn cho gợi ý
    public static class Suggestion {
        private final int productId;
        private final String name;
        private final double price;
        private final String imageUrl;

        public Suggestion(int productId, String name, double price, String imageUrl) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
        }

        public int getProductId() { return productId; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }
    }

    // Lấy sản phẩm theo ID (Read)
    public Product getProductById(int id) {
        String sql = "SELECT * FROM Products WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createProductWithImages(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy danh sách ảnh bổ sung của sản phẩm
    public List<String> getProductImages(int productId) {
        List<String> images = new ArrayList<>();
        String sql = "SELECT image_url FROM ProductImages WHERE product_id = ? ORDER BY image_order";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    images.add(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }
    
    // Helper method để tạo Product object với ảnh bổ sung
    private Product createProductWithImages(ResultSet rs) throws SQLException {
        Product product = new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("description"),
                rs.getString("image_url"),
                rs.getInt("category_id")
        );
        // Lấy danh sách ảnh bổ sung
        List<String> additionalImages = getProductImages(rs.getInt("product_id"));
        for (String imageUrl : additionalImages) {
            product.addImageUrl(imageUrl);
        }
        Discount discount = fetchActiveDiscount(rs.getInt("product_id"));
        product.setActiveDiscount(discount);
        return product;
    }

    // Tra cứu category_id theo tên danh mục
    public Integer getCategoryIdByName(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String sql = "SELECT category_id FROM Categories WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Discount fetchActiveDiscount(int productId) {
        String sql = "SELECT TOP 1 d.*, dp.assigned_at, p.price FROM DiscountProducts dp " +
                     "JOIN Discounts d ON dp.discount_id = d.discount_id " +
                     "JOIN Products p ON p.product_id = dp.product_id " +
                     "WHERE dp.product_id = ? AND d.is_active = 1 " +
                     "AND (d.start_date IS NULL OR d.start_date <= GETDATE()) " +
                     "AND (d.end_date IS NULL OR d.end_date >= GETDATE()) " +
                     "ORDER BY " +
                     // 1) ưu tiên mức giảm thực tế lớn nhất (đã tính cap nếu là %)
                     "CASE WHEN d.discount_type = 'PERCENTAGE' THEN " +
                     "       CASE WHEN d.max_discount_amount IS NOT NULL " +
                     "             THEN CASE WHEN (p.price * d.discount_value / 100.0) > d.max_discount_amount " +
                     "                        THEN d.max_discount_amount " +
                     "                        ELSE (p.price * d.discount_value / 100.0) END " +
                     "             ELSE (p.price * d.discount_value / 100.0) END " +
                     "     ELSE d.discount_value END DESC, " +
                     // 2) nếu bằng nhau, ưu tiên mã còn hạn lâu hơn
                     "DATEDIFF(SECOND, GETDATE(), d.end_date) DESC, " +
                     // 3) cuối cùng, ưu tiên mã mới gán hơn
                     "dp.assigned_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Discount discount = new Discount();
                    discount.setDiscountId(rs.getInt("discount_id"));
                    discount.setCode(rs.getString("code"));
                    discount.setName(rs.getString("name"));
                    discount.setType(rs.getString("discount_type"));
                    discount.setValue(rs.getDouble("discount_value"));
                    BigDecimal minOrder = rs.getBigDecimal("min_order_amount");
                    discount.setMinOrderAmount(minOrder != null ? minOrder.doubleValue() : 0);
                    BigDecimal maxDiscount = rs.getBigDecimal("max_discount_amount");
                    discount.setMaxDiscountAmount(maxDiscount != null ? maxDiscount.doubleValue() : null);
                    discount.setActive(rs.getBoolean("is_active"));
                    Timestamp startDate = rs.getTimestamp("start_date");
                    Timestamp endDate = rs.getTimestamp("end_date");
                    discount.setStartDate(startDate);
                    discount.setEndDate(endDate);
                    discount.setDescription(rs.getString("description"));
                    Integer usageLimit = rs.getObject("usage_limit") != null ? rs.getInt("usage_limit") : null;
                    Integer usedCount = rs.getObject("used_count") != null ? rs.getInt("used_count") : null;
                    discount.setUsageLimit(usageLimit);
                    discount.setUsedCount(usedCount);
                    discount.setConditionType(rs.getString("condition_type"));
                    BigDecimal conditionValue = rs.getBigDecimal("condition_value");
                    discount.setConditionValue(conditionValue != null ? conditionValue.doubleValue() : null);
                    discount.setConditionDescription(rs.getString("condition_description"));
                    discount.setSpecialEvent(rs.getObject("special_event") != null ? rs.getBoolean("special_event") : null);
                    discount.setAutoAssignAll(rs.getObject("auto_assign_all") != null ? rs.getBoolean("auto_assign_all") : null);
                    discount.setAssignDate(rs.getTimestamp("assign_date"));
                    return discount;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả danh mục
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM Categories ORDER BY name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Lấy danh sách product_id theo tên danh mục
    public List<Integer> getProductIdsByCategoryName(String categoryName) {
        List<Integer> ids = new ArrayList<>();
        if (categoryName == null || categoryName.trim().isEmpty()) return ids;
        String sql = "SELECT p.product_id FROM Products p JOIN Categories c ON p.category_id = c.category_id WHERE c.name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    // Lấy danh sách product_id theo khoảng giá
    public List<Integer> getProductIdsByPriceRange(double minPrice, double maxPrice) {
        List<Integer> ids = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT product_id FROM Products WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (minPrice > 0) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice < Double.MAX_VALUE) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    // Lấy tên danh mục theo ID
    public String getCategoryNameById(int categoryId) {
        String sql = "SELECT name FROM Categories WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm sản phẩm mới (Create)
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO Products (name, price, stock, description, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getImageUrl());
            if (product.getCategoryId() <= 0) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, product.getCategoryId());
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy ID của sản phẩm vừa được thêm
    public int getLastInsertedProductId() {
        String sql = "SELECT TOP 1 product_id FROM Products ORDER BY product_id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Cập nhật sản phẩm (Update)
    public boolean updateProduct(Product product) {
        String sql = "UPDATE Products SET name = ?, price = ?, stock = ?, description = ?, image_url = ?, category_id = ? WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getImageUrl());
            if (product.getCategoryId() <= 0) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, product.getCategoryId());
            }
            stmt.setInt(7, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa sản phẩm (Delete)
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM Products WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm sản phẩm theo tên
    public List<Product> searchProducts(String searchTerm) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE name LIKE ? OR description LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Lọc sản phẩm theo danh mục
    public List<Product> getProductsByCategory(String categoryName) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT p.* FROM Products p JOIN Categories c ON p.category_id = c.category_id WHERE c.name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Lọc sản phẩm theo khoảng giá
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE price >= ? AND price <= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Lọc sản phẩm theo giá cố định
    public List<Product> getProductsByFixedPriceRange(String priceRange) {
        List<Product> productList = new ArrayList<>();
        String sql = "";
        
        switch (priceRange) {
            case "under-100k":
                sql = "SELECT * FROM Products WHERE price < 100000 ORDER BY price";
                break;
            case "100k-300k":
                sql = "SELECT * FROM Products WHERE price >= 100000 AND price <= 300000 ORDER BY price";
                break;
            case "300k-500k":
                sql = "SELECT * FROM Products WHERE price > 300000 AND price <= 500000 ORDER BY price";
                break;
            case "500k-1m":
                sql = "SELECT * FROM Products WHERE price > 500000 AND price <= 1000000 ORDER BY price";
                break;
            case "over-1m":
                sql = "SELECT * FROM Products WHERE price > 1000000 ORDER BY price";
                break;
            default:
                return getAllProducts();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Tìm kiếm và lọc kết hợp (cập nhật để hỗ trợ lọc giá cố định và sắp xếp)
    public List<Product> searchAndFilterProducts(String searchTerm, String categoryName, double minPrice, double maxPrice, String fixedPriceRange, String sortBy) {
        List<Product> productList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.* FROM Products p JOIN Categories c ON p.category_id = c.category_id WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
            String searchPattern = "%" + searchTerm + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        
        if (categoryName != null && !categoryName.trim().isEmpty() && !categoryName.equals("all")) {
            sql.append(" AND c.name = ?");
            parameters.add(categoryName);
        }
        
        // Xử lý lọc giá cố định
        if (fixedPriceRange != null && !fixedPriceRange.trim().isEmpty() && !fixedPriceRange.equals("all")) {
            switch (fixedPriceRange) {
                case "under-100k":
                    sql.append(" AND p.price < ?");
                    parameters.add(100000.0);
                    break;
                case "100k-300k":
                    sql.append(" AND p.price >= ? AND p.price <= ?");
                    parameters.add(100000.0);
                    parameters.add(300000.0);
                    break;
                case "300k-500k":
                    sql.append(" AND p.price > ? AND p.price <= ?");
                    parameters.add(300000.0);
                    parameters.add(500000.0);
                    break;
                case "500k-1m":
                    sql.append(" AND p.price > ? AND p.price <= ?");
                    parameters.add(500000.0);
                    parameters.add(1000000.0);
                    break;
                case "over-1m":
                    sql.append(" AND p.price > ?");
                    parameters.add(1000000.0);
                    break;
            }
        } else {
            // Chỉ áp dụng lọc giá tùy chỉnh nếu không có lọc giá cố định
            if (minPrice >= 0) {
                sql.append(" AND p.price >= ?");
                parameters.add(minPrice);
            }
            
            if (maxPrice > 0) {
                sql.append(" AND p.price <= ?");
                parameters.add(maxPrice);
            }
        }
        
        // Xử lý sắp xếp
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy) {
                case "price-asc":
                    sql.append(" ORDER BY p.price ASC");
                    break;
                case "price-desc":
                    sql.append(" ORDER BY p.price DESC");
                    break;
                case "name-asc":
                    sql.append(" ORDER BY p.name ASC");
                    break;
                case "name-desc":
                    sql.append(" ORDER BY p.name DESC");
                    break;
                case "newest":
                    sql.append(" ORDER BY p.product_id DESC");
                    break;
                case "oldest":
                    sql.append(" ORDER BY p.product_id ASC");
                    break;
                default:
                    sql.append(" ORDER BY p.name ASC");
                    break;
            }
        } else {
            sql.append(" ORDER BY p.name ASC");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Phân trang: lấy tất cả sản phẩm với OFFSET/FETCH (SQL Server 2012+)
    public List<Product> getAllProductsWithPaging(int page, int pageSize) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM Products ORDER BY product_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, (page - 1) * pageSize);
            stmt.setInt(2, pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Lấy sản phẩm nổi bật (featured products) - top 8 sản phẩm mới nhất
    public List<Product> getFeaturedProducts(int limit) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM Products ORDER BY product_id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Lấy sản phẩm bán chạy nhất (best selling products) - top sản phẩm theo số lượng bán
    public List<Product> getBestSellingProducts(int limit) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT TOP (?) p.* " +
                     "FROM Products p " +
                     "INNER JOIN OrderDetails od ON p.product_id = od.product_id " +
                     "INNER JOIN Orders o ON od.order_id = o.order_id " +
                     "WHERE o.payment_status = 'PAID' " +
                     "GROUP BY p.product_id, p.name, p.price, p.stock, p.description, p.image_url, p.category_id " +
                     "ORDER BY SUM(od.quantity) DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Lấy sản phẩm khuyến mại (demo) - fix cứng các sản phẩm có product_id chẵn
    public List<Product> getPromotionalProducts(int limit) {
        List<Product> productList = new ArrayList<>();
        int safeLimit = Math.max(1, Math.min(limit, 50));
        String sql = "SELECT TOP " + safeLimit + " * FROM Products " +
                     "WHERE stock > 0 AND product_id % 2 = 0 " +
                     "ORDER BY price ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (productList.size() < safeLimit) {
            int remaining = safeLimit - productList.size();
            String fallbackSql = "SELECT TOP " + remaining + " * FROM Products " +
                                 "WHERE stock > 0 AND product_id NOT IN ( " +
                                 "    SELECT TOP " + safeLimit + " product_id FROM Products " +
                                 "    WHERE stock > 0 AND product_id % 2 = 0 ORDER BY price ASC " +
                                 ") ORDER BY price ASC";
            try (PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        productList.add(createProductWithImages(rs));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }

    // Phân trang: tìm kiếm + lọc với OFFSET/FETCH
    public List<Product> searchAndFilterProductsWithPaging(String searchTerm, String categoryName,
            double minPrice, double maxPrice, String fixedPriceRange, String sortBy,
            int page, int pageSize) {
        List<Product> productList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.* FROM Products p JOIN Categories c ON p.category_id = c.category_id WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
            String searchPattern = "%" + searchTerm + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        if (categoryName != null && !categoryName.trim().isEmpty() && !categoryName.equals("all")) {
            sql.append(" AND c.name = ?");
            parameters.add(categoryName);
        }

        if (fixedPriceRange != null && !fixedPriceRange.trim().isEmpty() && !fixedPriceRange.equals("all")) {
            switch (fixedPriceRange) {
                case "under-100k":
                    sql.append(" AND p.price < ?");
                    parameters.add(100000.0);
                    break;
                case "100k-300k":
                    sql.append(" AND p.price >= ? AND p.price <= ?");
                    parameters.add(100000.0);
                    parameters.add(300000.0);
                    break;
                case "300k-500k":
                    sql.append(" AND p.price > ? AND p.price <= ?");
                    parameters.add(300000.0);
                    parameters.add(500000.0);
                    break;
                case "500k-1m":
                    sql.append(" AND p.price > ? AND p.price <= ?");
                    parameters.add(500000.0);
                    parameters.add(1000000.0);
                    break;
                case "over-1m":
                    sql.append(" AND p.price > ?");
                    parameters.add(1000000.0);
                    break;
            }
        } else {
            if (minPrice >= 0) {
                sql.append(" AND p.price >= ?");
                parameters.add(minPrice);
            }
            if (maxPrice > 0) {
                sql.append(" AND p.price <= ?");
                parameters.add(maxPrice);
            }
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy) {
                case "price-asc":
                    sql.append(" ORDER BY p.price ASC");
                    break;
                case "price-desc":
                    sql.append(" ORDER BY p.price DESC");
                    break;
                case "name-asc":
                    sql.append(" ORDER BY p.name ASC");
                    break;
                case "name-desc":
                    sql.append(" ORDER BY p.name DESC");
                    break;
                case "newest":
                    sql.append(" ORDER BY p.product_id DESC");
                    break;
                case "oldest":
                    sql.append(" ORDER BY p.product_id ASC");
                    break;
                default:
                    sql.append(" ORDER BY p.name ASC");
                    break;
            }
        } else {
            sql.append(" ORDER BY p.name ASC");
        }

        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        parameters.add((page - 1) * pageSize);
        parameters.add(pageSize);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(createProductWithImages(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public int getTotalProductsCount() {
        String sql = "SELECT COUNT(*) FROM Products";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFilteredProductsCount(String searchTerm, String categoryName,
            double minPrice, double maxPrice, String fixedPriceRange) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products p JOIN Categories c ON p.category_id = c.category_id WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
            String searchPattern = "%" + searchTerm + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        if (categoryName != null && !categoryName.trim().isEmpty() && !categoryName.equals("all")) {
            sql.append(" AND c.name = ?");
            parameters.add(categoryName);
        }

        if (fixedPriceRange != null && !fixedPriceRange.trim().isEmpty() && !fixedPriceRange.equals("all")) {
            switch (fixedPriceRange) {
                case "under-100k":
                    sql.append(" AND p.price < ?");
                    parameters.add(100000.0);
                    break;
                case "100k-300k":
                    sql.append(" AND p.price >= ? AND p.price <= ?");
                    parameters.add(100000.0);
                    parameters.add(300000.0);
                    break;
                case "300k-500k":
                    sql.append(" AND p.price > ? AND p.price <= ?");
                    parameters.add(300000.0);
                    parameters.add(500000.0);
                    break;
                case "500k-1m":
                    sql.append(" AND p.price > ? AND p.price <= ?");
                    parameters.add(500000.0);
                    parameters.add(1000000.0);
                    break;
                case "over-1m":
                    sql.append(" AND p.price > ?");
                    parameters.add(1000000.0);
                    break;
            }
        } else {
            if (minPrice >= 0) {
                sql.append(" AND p.price >= ?");
                parameters.add(minPrice);
            }
            if (maxPrice > 0) {
                sql.append(" AND p.price <= ?");
                parameters.add(maxPrice);
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ========== QUẢN LÝ ẢNH SẢN PHẨM ==========
    
    // Thêm ảnh cho sản phẩm
    public boolean addProductImage(int productId, String imageUrl, int imageOrder) {
        String sql = "INSERT INTO ProductImages (product_id, image_url, image_order) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setString(2, imageUrl);
            stmt.setInt(3, imageOrder);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật ảnh sản phẩm
    public boolean updateProductImage(int imageId, String imageUrl, int imageOrder) {
        String sql = "UPDATE ProductImages SET image_url = ?, image_order = ? WHERE image_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, imageUrl);
            stmt.setInt(2, imageOrder);
            stmt.setInt(3, imageId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa ảnh sản phẩm
    public boolean deleteProductImage(int imageId) {
        String sql = "DELETE FROM ProductImages WHERE image_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, imageId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật thứ tự ảnh
    public boolean updateImageOrder(int imageId, int newOrder) {
        String sql = "UPDATE ProductImages SET image_order = ? WHERE image_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newOrder);
            stmt.setInt(2, imageId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tất cả ảnh của một sản phẩm với thông tin chi tiết
    public List<ImageInfo> getProductImagesWithDetails(int productId) {
        List<ImageInfo> images = new ArrayList<>();
        String sql = "SELECT image_id, image_url, image_order FROM ProductImages WHERE product_id = ? ORDER BY image_order";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    images.add(new ImageInfo(
                        rs.getInt("image_id"),
                        rs.getString("image_url"),
                        rs.getInt("image_order")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }
    
    // Inner class để lưu thông tin ảnh
    public static class ImageInfo {
        private int imageId;
        private String imageUrl;
        private int imageOrder;
        
        public ImageInfo(int imageId, String imageUrl, int imageOrder) {
            this.imageId = imageId;
            this.imageUrl = imageUrl;
            this.imageOrder = imageOrder;
        }
        
        // Getters
        public int getImageId() { return imageId; }
        public String getImageUrl() { return imageUrl; }
        public int getImageOrder() { return imageOrder; }
        
        // Setters
        public void setImageId(int imageId) { this.imageId = imageId; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public void setImageOrder(int imageOrder) { this.imageOrder = imageOrder; }
    }
    
    // Giảm số lượng tồn kho
    public boolean decreaseStock(int productId, int quantity) {
        String sql = "UPDATE Products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity); // Đảm bảo còn đủ hàng
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Tăng số lượng tồn kho (hoàn kho)
    public boolean increaseStock(int productId, int quantity) {
        String sql = "UPDATE Products SET stock = stock + ? WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}