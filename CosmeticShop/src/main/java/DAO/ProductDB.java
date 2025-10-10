package DAO;

import Model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

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
}