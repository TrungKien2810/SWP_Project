package DAO;

import Model.Order;
import Model.OrderDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class OrderDB {
    DBConnect db = new DBConnect();
    Connection conn = db.conn;

    public Integer createOrder(Order order) {
        String sql = "insert into Orders (user_id, total_amount, shipping_address_id, shipping_method_id, shipping_cost, payment_method, payment_status, order_status, discount_id, discount_amount, notes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            if (order.getShippingAddressId() == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, order.getShippingAddressId());
            if (order.getShippingMethodId() == null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, order.getShippingMethodId());
            ps.setDouble(5, order.getShippingCost());
            ps.setString(6, order.getPaymentMethod());
            ps.setString(7, order.getPaymentStatus() == null ? "PENDING" : order.getPaymentStatus());
            ps.setString(8, order.getOrderStatus() == null ? "PENDING" : order.getOrderStatus());
            if (order.getDiscountId() == null) ps.setNull(9, java.sql.Types.INTEGER); else ps.setInt(9, order.getDiscountId());
            ps.setDouble(10, order.getDiscountAmount());
            ps.setString(11, order.getNotes());
            int affected = ps.executeUpdate();
            if (affected == 0) return null;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addOrderDetails(int orderId, List<OrderDetail> details) {
        String sql = "insert into OrderDetails (order_id, product_id, quantity, price) values (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderDetail d : details) {
                ps.setInt(1, orderId);
                ps.setInt(2, d.getProductId());
                ps.setInt(3, d.getQuantity());
                ps.setDouble(4, d.getPrice());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearCart(int cartId) {
        String sql = "delete from CartItems where cart_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearSelectedCartItems(int cartId) {
        String sql = "delete from CartItems where cart_id = ? and is_selected = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePaymentStatus(int orderId, String status) {
        String sql = "update Orders set payment_status = ? where order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


