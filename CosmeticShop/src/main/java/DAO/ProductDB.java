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
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getInt("category_id")
                );
                productList.add(product);
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
                    return new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getInt("category_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getInt("category_id")
                    );
                    productList.add(product);
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
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getInt("category_id")
                    );
                    productList.add(product);
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
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getInt("category_id")
                    );
                    productList.add(product);
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
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getInt("category_id")
                    );
                    productList.add(product);
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
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getInt("category_id")
                    );
                    productList.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

}