package DAO;

import Model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn = db.conn;

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        return c;
    }

    public List<Category> listAll() {
        String sql = "SELECT category_id, name, description FROM Categories ORDER BY name";
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Category getById(int id) {
        String sql = "SELECT category_id, name, description FROM Categories WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(String name, String description) {
        String sql = "INSERT INTO Categories(name, description) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            if (description == null || description.isBlank()) ps.setNull(2, java.sql.Types.NVARCHAR); else ps.setString(2, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int id, String name, String description) {
        String sql = "UPDATE Categories SET name = ?, description = ? WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            if (description == null || description.isBlank()) ps.setNull(2, java.sql.Types.NVARCHAR); else ps.setString(2, description);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        // Consider FK constraints from Products; if exists, prevent delete or handle cascade at DB.
        String sql = "DELETE FROM Categories WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


