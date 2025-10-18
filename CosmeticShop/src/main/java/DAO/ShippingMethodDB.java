package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShippingMethodDB {
    DBConnect db = new DBConnect();
    Connection conn = db.conn;

    public static class ShippingMethod {
        private int methodId;
        private String name;
        private String description;
        private double cost;
        private int estimatedDays;
        private boolean active;

        public int getMethodId() { return methodId; }
        public void setMethodId(int methodId) { this.methodId = methodId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }
        public int getEstimatedDays() { return estimatedDays; }
        public void setEstimatedDays(int estimatedDays) { this.estimatedDays = estimatedDays; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    private ShippingMethod mapRow(ResultSet rs) throws SQLException {
        ShippingMethod sm = new ShippingMethod();
        sm.setMethodId(rs.getInt("method_id"));
        sm.setName(rs.getString("name"));
        sm.setDescription(rs.getString("description"));
        sm.setCost(rs.getDouble("cost"));
        sm.setEstimatedDays(rs.getInt("estimated_days"));
        sm.setActive(rs.getBoolean("is_active"));
        return sm;
    }

    public List<ShippingMethod> getActiveMethods() {
        String sql = "select method_id, name, description, cost, estimated_days, is_active from ShippingMethods where is_active = 1 order by cost";
        List<ShippingMethod> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ShippingMethod getById(int methodId) {
        String sql = "select method_id, name, description, cost, estimated_days, is_active from ShippingMethods where method_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, methodId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


