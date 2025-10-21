package Controller;

import Util.VnPayConfig;
import DAO.OrderDB;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class VnPayReturnServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> fields = new HashMap<>();
        req.getParameterMap().forEach((k, v) -> { if (k != null && k.startsWith("vnp_")) fields.put(k, v[0]); });
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        // Một số cổng trả về các tham số không theo thứ tự; đảm bảo sort lại trước khi ký
        String signData = VnPayConfig.hashAllFields(fields);
        String signed = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, signData);

        String orderIdStr = req.getParameter("orderId");
        String rsp = req.getParameter("vnp_ResponseCode");
        boolean ok = signed != null && vnp_SecureHash != null && signed.equalsIgnoreCase(vnp_SecureHash);

        if (ok) {
            try {
                int orderId = Integer.parseInt(orderIdStr);
                new OrderDB().updatePaymentStatus(orderId, VnPayConfig.statusForResponseCode(rsp));
            } catch (Exception ignore) {}

            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute("cartItems");
                session.removeAttribute("cartId");
            }

            String msg = VnPayConfig.describeResponseCode(rsp);
            String target = req.getContextPath() + "/View/home.jsp?msg=" + java.net.URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8);
            resp.sendRedirect(target);
        } else {
            resp.sendRedirect(req.getContextPath() + "/View/checkout.jsp?error=payment");
        }
    }
}

