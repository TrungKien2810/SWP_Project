package Controller;

import DAO.OrderDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "paymentCallback", urlPatterns = {"/payment-callback"})
public class PaymentCallback extends HttpServlet {

    private final OrderDB orderDB = new OrderDB();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String status = req.getParameter("status");
        String orderIdStr = req.getParameter("orderId");
        int orderId = -1;
        try { orderId = Integer.parseInt(orderIdStr); } catch (Exception ignore) {}

        if (orderId > 0 && "success".equalsIgnoreCase(status)) {
            orderDB.updatePaymentStatus(orderId, "PAID");
            // Clear session cart if any
            session.removeAttribute("cartItems");
            session.removeAttribute("cartId");
            String msg = "Thanh toán thành công!";
            String target = req.getContextPath() + "/View/home.jsp?msg=" + java.net.URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8);
            resp.sendRedirect(target);
            return;
        }

        String target = req.getContextPath() + "/checkout?error=payment";
        resp.sendRedirect(target);
    }
}


