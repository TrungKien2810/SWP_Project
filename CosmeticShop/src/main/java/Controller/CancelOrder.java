package Controller;

import DAO.OrderDB;
import DAO.ProductDB;
import DAO.DiscountDB;
import DAO.NotificationDB;
import Model.Order;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "CancelOrder", urlPatterns = {"/cancel-order"})
public class CancelOrder extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        user currentUser = (user) session.getAttribute("user");

        // Kiểm tra đăng nhập
        if (currentUser == null) {
            String error = "Vui lòng đăng nhập để thực hiện thao tác này.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/login?error=" + encodedError);
            return;
        }

        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            String error = "Không tìm thấy đơn hàng.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/my-orders?error=" + encodedError);
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDB orderDB = new OrderDB();
            Order order = orderDB.getFullOrderDetails(orderId);

            // Kiểm tra đơn hàng tồn tại
            if (order == null) {
                String error = "Không tìm thấy đơn hàng.";
                String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/my-orders?error=" + encodedError);
                return;
            }

            // Kiểm tra đơn hàng thuộc về user hiện tại
            if (order.getUserId() != currentUser.getUser_id()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền hủy đơn hàng này.");
                return;
            }

            // Kiểm tra trạng thái đơn hàng - chỉ cho phép hủy khi PENDING hoặc CONFIRMED
            String currentStatus = order.getOrderStatus();
            if (currentStatus == null || 
                "CANCELLED".equals(currentStatus) || 
                "COMPLETED".equals(currentStatus) ||
                "SHIPPING".equals(currentStatus)) {
                String error;
                if ("CANCELLED".equals(currentStatus)) {
                    error = "Đơn hàng này đã được hủy trước đó.";
                } else if ("COMPLETED".equals(currentStatus)) {
                    error = "Không thể hủy đơn hàng đã hoàn thành.";
                } else if ("SHIPPING".equals(currentStatus)) {
                    error = "Không thể hủy đơn hàng đang được vận chuyển. Vui lòng liên hệ với chúng tôi.";
                } else {
                    error = "Không thể hủy đơn hàng ở trạng thái này.";
                }
                String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/order-detail?orderId=" + orderId + "&error=" + encodedError);
                return;
            }

            // Hoàn lại tồn kho
            ProductDB productDB = new ProductDB();
            List<OrderDB.OrderItemQuantity> items = orderDB.getOrderItemQuantities(orderId);
            for (OrderDB.OrderItemQuantity item : items) {
                productDB.increaseStock(item.getProductId(), item.getQuantity());
            }

            // Hoàn lại voucher nếu đã sử dụng
            if (order.getDiscountId() != null && order.getDiscountAmount() > 0) {
                try {
                    DiscountDB discountDB = new DiscountDB();
                    discountDB.restoreUserVoucher(currentUser.getUser_id(), order.getDiscountId(), orderId);
                } catch (Exception e) {
                    // Log lỗi nhưng không ảnh hưởng đến việc hủy đơn
                    e.printStackTrace();
                }
            }

            // Cập nhật trạng thái đơn hàng thành CANCELLED
            boolean success = orderDB.updateOrderStatus(orderId, "CANCELLED");
            
            if (success) {
                // Tạo thông báo cho khách hàng
                try {
                    NotificationDB notificationDB = new NotificationDB();
                    String title = "Đơn hàng đã được hủy";
                    String message = "Đơn hàng #" + orderId + " của bạn đã được hủy thành công.";
                    String linkUrl = request.getContextPath() + "/order-detail?orderId=" + orderId;
                    notificationDB.createNotificationForUser(
                        currentUser.getUser_id(),
                        "ORDER_CANCELLED",
                        title,
                        message,
                        linkUrl
                    );
                } catch (Exception e) {
                    // Log lỗi nhưng không ảnh hưởng đến việc hủy đơn
                    e.printStackTrace();
                }

                String msg = "Đơn hàng #" + orderId + " đã được hủy thành công.";
                String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/order-detail?orderId=" + orderId + "&msg=" + encodedMsg);
            } else {
                String error = "Có lỗi xảy ra khi hủy đơn hàng. Vui lòng thử lại.";
                String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/order-detail?orderId=" + orderId + "&error=" + encodedError);
            }

        } catch (NumberFormatException e) {
            String error = "Mã đơn hàng không hợp lệ.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/my-orders?error=" + encodedError);
        } catch (Exception e) {
            e.printStackTrace();
            String error = "Có lỗi xảy ra khi xử lý yêu cầu.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/my-orders?error=" + encodedError);
        }
    }
}

