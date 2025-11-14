package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
<<<<<<< HEAD
import service.CartPromotionService;
import service.ServiceRegistry;
import service.dto.PromotionApplicationResult;
=======
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
>>>>>>> d8900ec43558aec996adf8d5b9b9955f1b384c86

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
        Model.user currentUser = (Model.user) session.getAttribute("user");
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
<<<<<<< HEAD
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            return;
        }

        session.setAttribute("appliedDiscountCode", result.getDiscountCode());
        session.setAttribute("appliedDiscountAmount", result.getDiscountAmount());
        // Xóa lastRemovedDiscountCode khi áp dụng mã mới
        session.removeAttribute("lastRemovedDiscountCode");
        // KHÔNG trừ lượt dùng khi mới áp dụng trong giỏ hàng. Chỉ trừ khi đơn hàng hoàn tất.
        request.setAttribute("msg", result.getMessage());
        request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
=======
            String error = "Mã giảm giá không hợp lệ hoặc đã hết hạn.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/View/cart.jsp?error=" + encodedError);
            return;
        }

        // Kiểm tra user có quyền sử dụng voucher này không
        if (!discountDB.canUserUseDiscount(currentUser.getUser_id(), discount.getDiscountId())) {
            session.removeAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountAmount");
            String error = "Bạn không có quyền sử dụng mã giảm giá này.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/View/cart.jsp?error=" + encodedError);
            return;
        }

        if (subtotal < discount.getMinOrderAmount()) {
            String error = "Đơn hàng chưa đạt tối thiểu " + String.format("%,.0f", discount.getMinOrderAmount()) + " ₫ để áp dụng mã giảm giá.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/View/cart.jsp?error=" + encodedError);
            return;
        }

        double discountAmount;
        if ("PERCENTAGE".equalsIgnoreCase(discount.getType())) {
            discountAmount = subtotal * (discount.getValue() / 100.0);
            if (discount.getMaxDiscountAmount() != null) {
                discountAmount = Math.min(discountAmount, discount.getMaxDiscountAmount());
            }
        } else { // FIXED_AMOUNT
            discountAmount = discount.getValue();
        }
        if (discountAmount < 0) discountAmount = 0;
        if (discountAmount > subtotal) discountAmount = subtotal;

        session.setAttribute("appliedDiscountCode", discount.getCode());
        session.setAttribute("appliedDiscountAmount", discountAmount);
        // Xóa lastRemovedDiscountCode khi áp dụng mã mới
        session.removeAttribute("lastRemovedDiscountCode");
        // KHÔNG trừ lượt dùng khi mới áp dụng trong giỏ hàng. Chỉ trừ khi đơn hàng hoàn tất.
        String msg = "✓ Áp dụng mã giảm giá '" + discount.getCode() + "' thành công! Giảm " + String.format("%,.0f", discountAmount) + " ₫";
        String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/View/cart.jsp?msg=" + encodedMsg);
>>>>>>> d8900ec43558aec996adf8d5b9b9955f1b384c86
    }
}


