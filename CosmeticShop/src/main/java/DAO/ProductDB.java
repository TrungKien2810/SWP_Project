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
}