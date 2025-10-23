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

@WebServlet(name = "ApplyPromotionServlet", urlPatterns = {"/apply-promo"})
public class ApplyPromotionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String code = request.getParameter("promoCode");
        Integer cartId = (Integer) session.getAttribute("cartId");

        if (cartId == null) {
            request.setAttribute("error", "Không tìm thấy giỏ hàng.");
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
        // KHÔNG trừ lượt dùng khi mới áp dụng trong giỏ hàng. Chỉ trừ khi đơn hàng hoàn tất.
        request.setAttribute("msg", "Áp dụng mã thành công: " + discount.getCode());
        request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
    }
}


