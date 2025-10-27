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

    // Lấy tất cả đơn hàng (cho admin)
    public List<Order> getAllOrders() {
        String sql =
            "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.payment_method, " +
            "o.payment_status, o.order_status, o.shipping_cost, o.tracking_number, " +
            "u.full_name as customer_name, u.email as customer_email " +
            "FROM Orders o " +
            "JOIN Users u ON o.user_id = u.user_id " +
            "ORDER BY o.order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    if (ts != null) o.setOrderDate(ts.toLocalDateTime());
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setShippingCost(rs.getDouble("shipping_cost"));
                    o.setTrackingNumber(rs.getString("tracking_number"));
                    orders.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Lấy đơn hàng theo trạng thái
    public List<Order> getOrdersByStatus(String status) {
        String sql =
            "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.payment_method, " +
            "o.payment_status, o.order_status, o.shipping_cost, o.tracking_number, " +
            "u.full_name as customer_name, u.email as customer_email " +
            "FROM Orders o " +
            "JOIN Users u ON o.user_id = u.user_id " +
            "WHERE o.order_status = ? " +
            "ORDER BY o.order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    if (ts != null) o.setOrderDate(ts.toLocalDateTime());
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setShippingCost(rs.getDouble("shipping_cost"));
                    o.setTrackingNumber(rs.getString("tracking_number"));
                    orders.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Lấy chi tiết đầy đủ của 1 đơn hàng
    public Order getFullOrderDetails(int orderId) {
        String sql =
            "SELECT o.*, u.full_name as customer_name, u.email as customer_email, u.phone as customer_phone " +
            "FROM Orders o " +
            "JOIN Users u ON o.user_id = u.user_id " +
            "WHERE o.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    if (ts != null) o.setOrderDate(ts.toLocalDateTime());
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setShippingAddressId(rs.getInt("shipping_address_id"));
                    o.setShippingMethodId(rs.getInt("shipping_method_id"));
                    o.setShippingCost(rs.getDouble("shipping_cost"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setTrackingNumber(rs.getString("tracking_number"));
                    if (!rs.wasNull()) o.setDiscountId(rs.getInt("discount_id"));
                    o.setDiscountAmount(rs.getDouble("discount_amount"));
                    o.setNotes(rs.getString("notes"));
                    return o;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy chi tiết sản phẩm trong đơn hàng
    public List<OrderDetailInfo> getOrderDetailItems(int orderId) {
        String sql =
            "SELECT od.*, p.name as product_name, p.image_url " +
            "FROM OrderDetails od " +
            "JOIN Products p ON od.product_id = p.product_id " +
            "WHERE od.order_id = ? " +
            "ORDER BY od.order_detail_id";
        List<OrderDetailInfo> details = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetailInfo d = new OrderDetailInfo();
                    d.setOrderDetailId(rs.getInt("order_detail_id"));
                    d.setOrderId(rs.getInt("order_id"));
                    d.setProductId(rs.getInt("product_id"));
                    d.setQuantity(rs.getInt("quantity"));
                    d.setPrice(rs.getDouble("price"));
                    d.setProductName(rs.getString("product_name"));
                    d.setProductImageUrl(rs.getString("image_url"));
                    details.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    // Cập nhật trạng thái đơn hàng
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET order_status = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật số tracking
    public boolean updateTrackingNumber(int orderId, String trackingNumber) {
        String sql = "UPDATE Orders SET tracking_number = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingNumber);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy trạng thái hiện tại của đơn hàng
    public String getOrderStatus(int orderId) {
        String sql = "SELECT order_status FROM Orders WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("order_status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy danh sách sản phẩm với số lượng trong đơn hàng (cho việc trừ/hoàn kho)
    public List<OrderItemQuantity> getOrderItemQuantities(int orderId) {
        String sql = "SELECT product_id, quantity FROM OrderDetails WHERE order_id = ?";
        List<OrderItemQuantity> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItemQuantity item = new OrderItemQuantity();
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // Inner class để lưu thông tin product và quantity
    public static class OrderItemQuantity {
        private int productId;
        private int quantity;
        
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    // Lấy đơn hàng theo ngày
    public List<Order> getOrdersByDate(java.sql.Date date) {
        String sql =
            "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.payment_method, " +
            "o.payment_status, o.order_status, o.shipping_cost, o.tracking_number, " +
            "u.full_name as customer_name, u.email as customer_email " +
            "FROM Orders o " +
            "JOIN Users u ON o.user_id = u.user_id " +
            "WHERE CAST(o.order_date AS DATE) = ? " +
            "ORDER BY o.order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    if (ts != null) o.setOrderDate(ts.toLocalDateTime());
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setShippingCost(rs.getDouble("shipping_cost"));
                    o.setTrackingNumber(rs.getString("tracking_number"));
                    orders.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Lấy đơn hàng theo khoảng ngày
    public List<Order> getOrdersByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        String sql =
            "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.payment_method, " +
            "o.payment_status, o.order_status, o.shipping_cost, o.tracking_number, " +
            "u.full_name as customer_name, u.email as customer_email " +
            "FROM Orders o " +
            "JOIN Users u ON o.user_id = u.user_id " +
            "WHERE CAST(o.order_date AS DATE) BETWEEN ? AND ? " +
            "ORDER BY o.order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    if (ts != null) o.setOrderDate(ts.toLocalDateTime());
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setShippingCost(rs.getDouble("shipping_cost"));
                    o.setTrackingNumber(rs.getString("tracking_number"));
                    orders.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Inner class để lưu thông tin chi tiết đơn hàng
    public static class OrderDetailInfo {
        private int orderDetailId;
        private int orderId;
        private int productId;
        private int quantity;
        private double price;
        private String productName;
        private String productImageUrl;

        public int getOrderDetailId() { return orderDetailId; }
        public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }
        public int getOrderId() { return orderId; }
        public void setOrderId(int orderId) { this.orderId = orderId; }
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductImageUrl() { return productImageUrl; }
        public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    }
}


