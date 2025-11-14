package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import Model.user;
import service.CartPromotionService;
import service.ServiceRegistry;
import service.dto.PromotionApplicationResult;

@WebServlet(name = "ApplyPromotion", urlPatterns = {"/apply-promo"})
public class ApplyPromotion extends HttpServlet {

    private CartPromotionService cartPromotionService;

    CartPromotionService cartPromotionService() {
        if (cartPromotionService == null) {
            cartPromotionService = ServiceRegistry.getCartPromotionService();
        }
        return cartPromotionService;
    }

    // Setter để test có thể inject mock
    void setCartPromotionService(CartPromotionService service) {
        this.cartPromotionService = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String code = request.getParameter("promoCode");
        String removeDiscount = request.getParameter("removeDiscount");
        Integer cartId = (Integer) session.getAttribute("cartId");

        // Xử lý xóa mã giảm giá
        if ("true".equals(removeDiscount)) {
            String removedCode = (String) session.getAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountAmount");
            
            // Lưu mã đã xóa vào session để có thể áp dụng lại
            if (removedCode != null && !removedCode.trim().isEmpty()) {
                session.setAttribute("lastRemovedDiscountCode", removedCode);
            }
            
            String msg = "Đã xóa mã giảm giá: " + (removedCode != null ? removedCode : "");
            String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/View/cart.jsp?msg=" + encodedMsg);
            return;
        }

        if (cartId == null) {
            String error = "Không tìm thấy giỏ hàng.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/View/cart.jsp?error=" + encodedError);
            return;
        }

        // Lấy thông tin user từ session
        user currentUser = (user) session.getAttribute("user");
        if (currentUser == null) {
            String error = "Vui lòng đăng nhập để sử dụng mã giảm giá.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/View/cart.jsp?error=" + encodedError);
            return;
        }

        PromotionApplicationResult result = cartPromotionService().applyPromotion(currentUser.getUser_id(), cartId, code);
        if (!result.isSuccess()) {
            session.removeAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountAmount");
            String error = result.getMessage();
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/View/cart.jsp?error=" + encodedError);
            return;
        }

        session.setAttribute("appliedDiscountCode", result.getDiscountCode());
        session.setAttribute("appliedDiscountAmount", result.getDiscountAmount());
        // Xóa lastRemovedDiscountCode khi áp dụng mã mới
        session.removeAttribute("lastRemovedDiscountCode");
        // KHÔNG trừ lượt dùng khi mới áp dụng trong giỏ hàng. Chỉ trừ khi đơn hàng hoàn tất.
        String msg = result.getMessage();
        String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/View/cart.jsp?msg=" + encodedMsg);
    }
}
