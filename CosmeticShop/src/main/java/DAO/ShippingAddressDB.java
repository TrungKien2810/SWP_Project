package DAO;

import Model.ShippingAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShippingAddressDB {
    DBConnect db = new DBConnect();
    Connection conn = db.conn;

    public List<ShippingAddress> getByUserId(int userId) {
        List<ShippingAddress> list = new ArrayList<>();
        String sql = "select address_id, user_id, full_name, phone, address, city, district, ward, is_default, created_at from ShippingAddresses where user_id = ? order by is_default desc, created_at desc";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ShippingAddress sa = mapRow(rs);
                list.add(sa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ShippingAddress getById(int addressId, int userId) {
        String sql = "select address_id, user_id, full_name, phone, address, city, district, ward, is_default, created_at from ShippingAddresses where address_id = ? and user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ShippingAddress getById(int addressId) {
        String sql = "select address_id, user_id, full_name, phone, address, city, district, ward, is_default, created_at from ShippingAddresses where address_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(ShippingAddress sa) {
        // If set default, unset others first
        if (sa.isDefault()) {
            unsetDefault(sa.getUserId());
        }
        String sql = "insert into ShippingAddresses (user_id, full_name, phone, address, city, district, ward, is_default) values (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sa.getUserId());
            ps.setString(2, sa.getFullName());
            ps.setString(3, sa.getPhone());
            ps.setString(4, sa.getAddress());
            ps.setString(5, sa.getCity());
            ps.setString(6, sa.getDistrict());
            ps.setString(7, sa.getWard());
            ps.setBoolean(8, sa.isDefault());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(ShippingAddress sa) {
        if (sa.isDefault()) {
            unsetDefault(sa.getUserId());
        }
        String sql = "update ShippingAddresses set full_name = ?, phone = ?, address = ?, city = ?, district = ?, ward = ?, is_default = ? where address_id = ? and user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sa.getFullName());
            ps.setString(2, sa.getPhone());
            ps.setString(3, sa.getAddress());
            ps.setString(4, sa.getCity());
            ps.setString(5, sa.getDistrict());
            ps.setString(6, sa.getWard());
            ps.setBoolean(7, sa.isDefault());
            ps.setInt(8, sa.getAddressId());
            ps.setInt(9, sa.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int addressId, int userId) {
        String sql = "delete from ShippingAddresses where address_id = ? and user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setDefault(int addressId, int userId) {
        unsetDefault(userId);
        String sql = "update ShippingAddresses set is_default = 1 where address_id = ? and user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void unsetDefault(int userId) {
        String sql = "update ShippingAddresses set is_default = 0 where user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ShippingAddress mapRow(ResultSet rs) throws SQLException {
        ShippingAddress sa = new ShippingAddress();
        sa.setAddressId(rs.getInt("address_id"));
        sa.setUserId(rs.getInt("user_id"));
        sa.setFullName(rs.getString("full_name"));
        sa.setPhone(rs.getString("phone"));
        sa.setAddress(rs.getString("address"));
        sa.setCity(rs.getString("city"));
        sa.setDistrict(rs.getString("district"));
        sa.setWard(rs.getString("ward"));
        sa.setDefault(rs.getBoolean("is_default"));
        return sa;
    }
}


