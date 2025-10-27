package Controller;

import DAO.OrderDB;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "OrderManagementServlet", urlPatterns = {"/admin/order-management"})
public class OrderManagementServlet extends HttpServlet {
    
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
                    DAO.ProductDB productDB = new DAO.ProductDB();
                    
                    // Lấy danh sách sản phẩm trong đơn hàng
                    List<DAO.OrderDB.OrderItemQuantity> items = orderDB.getOrderItemQuantities(orderId);
                    
                    // Logic: HOÀN KHO khi hủy đơn (từ bất kỳ trạng thái nào ngoại trừ COMPLETED)
                    if (newStatus.equals("CANCELLED") && !oldStatus.equals("COMPLETED")) {
                        for (DAO.OrderDB.OrderItemQuantity item : items) {
                            productDB.increaseStock(item.getProductId(), item.getQuantity());
                        }
                    }
                    
                    // 5. Sau khi xử lý kho, mới cập nhật trạng thái đơn hàng
                    if (orderDB.updateOrderStatus(orderId, newStatus)) {
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

