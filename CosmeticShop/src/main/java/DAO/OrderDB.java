package DAO;

import Model.Order;
import Model.OrderDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import Model.OrderItemSummary;

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

    // Danh sách item của các đơn theo user
    public List<OrderItemSummary> listItemsByUserId(int userId) {
        String sql =
            "select o.order_id, p.product_id, p.name, p.image_url, od.quantity " +
            "from Orders o " +
            "join OrderDetails od on od.order_id = o.order_id " +
            "join Products p on p.product_id = od.product_id " +
            "where o.user_id = ? " +
            "order by o.order_date desc, od.order_detail_id asc";
        List<OrderItemSummary> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItemSummary it = new OrderItemSummary();
                    it.setOrderId(rs.getInt("order_id"));
                    it.setProductId(rs.getInt("product_id"));
                    it.setName(rs.getString("name"));
                    it.setImageUrl(rs.getString("image_url"));
                    it.setQuantity(rs.getInt("quantity"));
                    items.add(it);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
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

    // Danh sách đơn hàng của 1 user (mới nhất trước)
    public List<Order> listByUserId(int userId) {
        String sql =
            "select o.order_id, o.order_date, o.total_amount, o.payment_status, o.order_status, o.tracking_number, " +
            "p.name as first_product_name, " +
            "p.image_url as first_image " +
            "from Orders o " +
            "left join (select od1.order_id, min(od1.order_detail_id) as min_od_id from OrderDetails od1 group by od1.order_id) m on m.order_id = o.order_id " +
            "left join OrderDetails od on od.order_detail_id = m.min_od_id " +
            "left join Products p on p.product_id = od.product_id " +
            "where o.user_id = ? " +
            "order by o.order_date desc";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    // Using timestamp to LocalDateTime if needed
                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    if (ts != null) {
                        o.setOrderDate(ts.toLocalDateTime());
                    }
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setTrackingNumber(rs.getString("tracking_number"));
                    try { o.setFirstProductName(rs.getString("first_product_name")); } catch (SQLException ignore) {}
                    try { o.setFirstProductImageUrl(rs.getString("first_image")); } catch (SQLException ignore) {}
                    orders.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Lưu txnRef và số tiền để đối chiếu IPN
    public void attachVnpTxnRef(int orderId, String txnRef, long amount) {
        String sql = "update Orders set vnp_txn_ref = ?, vnp_amount = ? where order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txnRef);
            ps.setLong(2, amount);
            ps.setInt(3, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tìm đơn theo id
    public Order getById(int orderId) {
        String sql = "select top 1 order_id, total_amount, payment_status from Orders where order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    return o;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm đơn theo txnRef
    public Order findByVnpTxnRef(String txnRef) {
        String sql = "select top 1 order_id, total_amount, payment_status from Orders where vnp_txn_ref = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txnRef);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    return o;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


