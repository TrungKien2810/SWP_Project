package Controller;

import DAO.OrderDB;
import DAO.ProductDB;
import DAO.NotificationDB;
import Model.Order;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "OrderManagement", urlPatterns = {"/admin/order-management"})
public class OrderManagement extends HttpServlet {
    
    private OrderDB orderDB;
    
    @Override
    public void init() {
        orderDB = new OrderDB();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String newStatus = request.getParameter("newStatus");
                
                // 1. Lấy trạng thái CŨ của đơn hàng
                String oldStatus = orderDB.getOrderStatus(orderId);
                
                // 2. Chỉ xử lý logic kho KHI CÓ THAY ĐỔI
                if (oldStatus != null && !oldStatus.equals(newStatus)) {
                    ProductDB productDB = new ProductDB();
                    
                    // Lấy danh sách sản phẩm trong đơn hàng
                    List<OrderDB.OrderItemQuantity> items = orderDB.getOrderItemQuantities(orderId);
                    
                    // Logic: HOÀN KHO khi hủy đơn (từ bất kỳ trạng thái nào ngoại trừ COMPLETED)
                    if (newStatus.equals("CANCELLED") && !oldStatus.equals("COMPLETED")) {
                        for (OrderDB.OrderItemQuantity item : items) {
                            productDB.increaseStock(item.getProductId(), item.getQuantity());
                        }
                    }
                    
                    // 3. Lấy thông tin đơn hàng để lấy userId
                    Order order = orderDB.getFullOrderDetails(orderId);
                    
                    // 4. Sau khi xử lý kho, mới cập nhật trạng thái đơn hàng
                    if (orderDB.updateOrderStatus(orderId, newStatus)) {
                        // Khi hoàn thành, tự động cập nhật payment_status = PAID
                        if ("COMPLETED".equals(newStatus)) {
                            orderDB.updatePaymentStatus(orderId, "PAID");
                        }
                        
                        // 5. Tạo thông báo cho khách hàng về trạng thái đơn hàng
                        if (order != null) {
                            try {
                                NotificationDB notificationDB = new NotificationDB();
                                String title = "";
                                String message = "";
                                String linkUrl = request.getContextPath() + "/order-detail?orderId=" + orderId;
                                
                                // Tạo thông báo phù hợp với từng trạng thái
                                switch (newStatus) {
                                    case "CONFIRMED":
                                        title = "Đơn hàng đã được xác nhận";
                                        message = String.format("Đơn hàng #%d của bạn đã được xác nhận. Chúng tôi sẽ chuẩn bị và gửi hàng sớm nhất có thể.", orderId);
                                        break;
                                    case "SHIPPING":
                                        title = "Đơn hàng đang được giao";
                                        message = String.format("Đơn hàng #%d của bạn đang được vận chuyển. Bạn sẽ nhận được hàng trong thời gian sớm nhất.", orderId);
                                        break;
                                    case "COMPLETED":
                                        title = "Đơn hàng đã hoàn thành";
                                        message = String.format("Đơn hàng #%d của bạn đã được giao thành công. Cảm ơn bạn đã mua sắm tại Pinky Cloud!", orderId);
                                        break;
                                    case "CANCELLED":
                                        title = "Đơn hàng đã bị hủy";
                                        message = String.format("Đơn hàng #%d của bạn đã bị hủy. Nếu bạn có thắc mắc, vui lòng liên hệ với chúng tôi.", orderId);
                                        break;
                                    default:
                                        title = "Cập nhật trạng thái đơn hàng";
                                        message = String.format("Trạng thái đơn hàng #%d của bạn đã được cập nhật.", orderId);
                                        break;
                                }
                                
                                // Tạo notification cho user
                                notificationDB.createNotificationForUser(
                                    order.getUserId(),
                                    "ORDER_STATUS_UPDATE",
                                    title,
                                    message,
                                    linkUrl
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Không làm gián đoạn flow nếu tạo thông báo thất bại
                            }
                        }
                        
                        response.getWriter().write("success");
                    } else {
                        response.getWriter().write("error");
                    }
                } else {
                    response.getWriter().write("no_change");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("error");
            }
        } else if ("updateTracking".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String trackingNumber = request.getParameter("trackingNumber");
                
                if (orderDB.updateTrackingNumber(orderId, trackingNumber)) {
                    response.getWriter().write("success");
                } else {
                    response.getWriter().write("error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("error");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}

