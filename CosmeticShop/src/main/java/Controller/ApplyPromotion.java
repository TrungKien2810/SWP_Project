package Controller;

import DAO.CartDB;
import DAO.DiscountDB;
import Model.Discount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ApplyPromotion", urlPatterns = {"/apply-promo"})
public class ApplyPromotion extends HttpServlet {

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
            // Redirect về controller cart để load lại assignedDiscounts
            response.sendRedirect(request.getContextPath() + "/cart?msg=" + encodedMsg);
            return;
        }

        if (cartId == null) {
            String error = "Không tìm thấy giỏ hàng.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            // Redirect về controller cart để load lại assignedDiscounts
            response.sendRedirect(request.getContextPath() + "/cart?error=" + encodedError);
            return;
        }

        // Lấy thông tin user từ session
        Model.user currentUser = (Model.user) session.getAttribute("user");
        if (currentUser == null) {
            String error = "Vui lòng đăng nhập để sử dụng mã giảm giá.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            // Redirect về controller cart để load lại assignedDiscounts
            response.sendRedirect(request.getContextPath() + "/cart?error=" + encodedError);
            return;
        }

        DiscountDB discountDB = new DiscountDB();
        Discount discount = discountDB.validateAndGetDiscount(code);
        CartDB cartDB = new CartDB();
        double subtotal = cartDB.calculateCartTotal(cartId);

        if (discount == null) {
            session.removeAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountAmount");
            String error = "Mã giảm giá không hợp lệ hoặc đã hết hạn.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            // Redirect về controller cart để load lại assignedDiscounts
            response.sendRedirect(request.getContextPath() + "/cart?error=" + encodedError);
            return;
        }

        // Kiểm tra user có quyền sử dụng voucher này không
        if (!discountDB.canUserUseDiscount(currentUser.getUser_id(), discount.getDiscountId())) {
            session.removeAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountAmount");
            String error = "Bạn không có quyền sử dụng mã giảm giá này.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            // Redirect về controller cart để load lại assignedDiscounts
            response.sendRedirect(request.getContextPath() + "/cart?error=" + encodedError);
            return;
        }

        if (subtotal < discount.getMinOrderAmount()) {
            String error = "Đơn hàng chưa đạt tối thiểu " + String.format("%,.0f", discount.getMinOrderAmount()) + " ₫ để áp dụng mã giảm giá.";
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            // Redirect về controller cart để load lại assignedDiscounts
            response.sendRedirect(request.getContextPath() + "/cart?error=" + encodedError);
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
        // Redirect về controller cart để load lại assignedDiscounts
        response.sendRedirect(request.getContextPath() + "/cart?msg=" + encodedMsg);
    }
}


