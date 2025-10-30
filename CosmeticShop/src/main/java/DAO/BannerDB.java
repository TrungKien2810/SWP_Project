package DAO;

import Model.Banner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BannerDB {

    private Connection getConnection() {
        return new DBConnect().conn;
    }

    public List<Banner> listAll() {
        String sql = "SELECT banner_id, image_url, link_url, sort_order, is_active FROM dbo.Banners ORDER BY sort_order ASC, banner_id ASC";
        List<Banner> banners = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    banners.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banners;
    }

    public List<Banner> listActiveOrdered() {
        String sql = "SELECT banner_id, image_url, link_url, sort_order, is_active FROM dbo.Banners WHERE is_active = 1 ORDER BY sort_order ASC, banner_id ASC";
        List<Banner> banners = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    banners.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banners;
    }

    public void insert(String imagePath, String targetUrl, int displayOrder, boolean isActive) {
        String sql = "INSERT INTO dbo.Banners (title, image_url, link_url, sort_order, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "");
            ps.setString(2, imagePath);
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, targetUrl);
            }
            ps.setInt(4, displayOrder);
            ps.setBoolean(5, isActive);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int bannerId) {
        String sql = "DELETE FROM dbo.Banners WHERE banner_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bannerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Banner getById(int id) {
        String sql = "SELECT banner_id, image_url, link_url, sort_order, is_active FROM dbo.Banners WHERE banner_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateWithoutImage(int id, String targetUrl, int displayOrder, boolean isActive) {
        String sql = "UPDATE dbo.Banners SET link_url = ?, sort_order = ?, is_active = ?, updated_at = GETDATE() WHERE banner_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                ps.setNull(1, java.sql.Types.VARCHAR);
            } else {
                ps.setString(1, targetUrl);
            }
            ps.setInt(2, displayOrder);
            ps.setBoolean(3, isActive);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWithImage(int id, String imagePath, String targetUrl, int displayOrder, boolean isActive) {
        String sql = "UPDATE dbo.Banners SET image_url = ?, link_url = ?, sort_order = ?, is_active = ?, updated_at = GETDATE() WHERE banner_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, targetUrl);
            }
            ps.setInt(3, displayOrder);
            ps.setBoolean(4, isActive);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Banner mapRow(ResultSet rs) throws SQLException {
        Banner b = new Banner();
        b.setBannerId(rs.getInt("banner_id"));
        b.setImagePath(rs.getString("image_url"));
        b.setTargetUrl(rs.getString("link_url"));
        b.setDisplayOrder(rs.getInt("sort_order"));
        b.setActive(rs.getBoolean("is_active"));
        return b;
    }
}


