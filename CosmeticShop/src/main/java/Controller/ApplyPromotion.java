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
            
            request.setAttribute("msg", "Đã xóa mã giảm giá: " + (removedCode != null ? removedCode : ""));
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            return;
        }

        if (cartId == null) {
            request.setAttribute("error", "Không tìm thấy giỏ hàng.");
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            return;
        }

        // Lấy thông tin user từ session
        Model.user currentUser = (Model.user) session.getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Vui lòng đăng nhập để sử dụng mã giảm giá.");
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            return;
        }

        DiscountDB discountDB = new DiscountDB();
        Discount discount = discountDB.validateAndGetDiscount(code);
        CartDB cartDB = new CartDB();
        double subtotal = cartDB.calculateCartTotal(cartId);

        if (discount == null) {
            session.removeAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountAmount");
            request.setAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            return;
        }

        // Kiểm tra user có quyền sử dụng voucher này không
        if (!discountDB.canUserUseDiscount(currentUser.getUser_id(), discount.getDiscountId())) {
            session.removeAttribute("appliedDiscountCode");
            session.removeAttribute("appliedDiscountAmount");
            request.setAttribute("error", "Bạn không có quyền sử dụng mã giảm giá này.");
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            return;
        }

        if (subtotal < discount.getMinOrderAmount()) {
            request.setAttribute("error", "Đơn hàng chưa đạt tối thiểu để áp dụng mã giảm giá.");
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
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
        request.setAttribute("msg", "Áp dụng mã thành công: " + discount.getCode());
        request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
    }
}


