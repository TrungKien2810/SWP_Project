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
        c.setImageUrl(rs.getString("image_url"));
        return c;
    }

    public List<Category> listAll() {
        String sql = "SELECT category_id, name, description, image_url FROM Categories ORDER BY name";
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Category getById(int id) {
        String sql = "SELECT category_id, name, description, image_url FROM Categories WHERE category_id = ?";
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

    public boolean create(String name, String description, String imageUrl) {
        String normalizedName = (name != null) ? name.trim() : null;
        if (normalizedName == null || normalizedName.isEmpty()) {
            return false;
        }
        if (existsByName(normalizedName)) {
            return false;
        }
        String sql = "INSERT INTO Categories(name, description, image_url) VALUES(?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedName);
            if (description == null || description.isBlank()) ps.setNull(2, java.sql.Types.NVARCHAR); else ps.setString(2, description);
            if (imageUrl == null || imageUrl.isBlank()) ps.setNull(3, java.sql.Types.NVARCHAR); else ps.setString(3, imageUrl);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int id, String name, String description, String imageUrl) {
        String normalizedName = (name != null) ? name.trim() : null;
        if (normalizedName == null || normalizedName.isEmpty()) {
            return false;
        }
        if (existsByNameExcludingId(normalizedName, id)) {
            return false;
        }
        String sql = "UPDATE Categories SET name = ?, description = ?, image_url = ? WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedName);
            if (description == null || description.isBlank()) ps.setNull(2, java.sql.Types.NVARCHAR); else ps.setString(2, description);
            if (imageUrl == null || imageUrl.isBlank()) ps.setNull(3, java.sql.Types.NVARCHAR); else ps.setString(3, imageUrl);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateWithoutImage(int id, String name, String description) {
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

    public List<Integer> getCategoryIdsByName(String name) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT category_id FROM Categories WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("category_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public boolean existsByName(String name) {
        return existsByNameInternal(name, null);
    }

    public boolean existsByNameExcludingId(String name, int excludeId) {
        return existsByNameInternal(name, excludeId);
    }

    private boolean existsByNameInternal(String name, Integer excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder("SELECT 1 FROM Categories WHERE LOWER(name) = LOWER(?)");
        if (excludeId != null) {
            sql.append(" AND category_id <> ?");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, name.trim());
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


