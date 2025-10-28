package Controller;

import DAO.OrderDB;
import DAO.ShippingAddressDB;
import DAO.ShippingMethodDB;
import Model.Order;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "orderDetail", urlPatterns = {"/order-detail"})
public class OrderDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        user u = (user) session.getAttribute("user");

        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/my-orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDB orderDB = new OrderDB();
            Order order = orderDB.getFullOrderDetails(orderId);

            if (order == null) {
                req.setAttribute("error", "Không tìm thấy đơn hàng");
                req.getRequestDispatcher("/View/my-orders.jsp").forward(req, resp);
                return;
            }

            // Verify that this order belongs to the current user
            if (order.getUserId() != u.getUser_id()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Get shipping address
            if (order.getShippingAddressId() != null) {
                ShippingAddressDB addressDB = new ShippingAddressDB();
                var address = addressDB.getById(order.getShippingAddressId());
                req.setAttribute("shippingAddress", address);
            }

            // Get shipping method
            if (order.getShippingMethodId() != null) {
                ShippingMethodDB methodDB = new ShippingMethodDB();
                var method = methodDB.getById(order.getShippingMethodId());
                req.setAttribute("shippingMethod", method);
            }

            // Get order items
            List<OrderDB.OrderDetailInfo> items = orderDB.getOrderDetailItems(orderId);
            req.setAttribute("orderItems", items);
            req.setAttribute("order", order);

            req.getRequestDispatcher("/View/order-detail.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/my-orders");
        }
    }
}

