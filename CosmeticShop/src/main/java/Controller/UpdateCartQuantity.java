package Controller;

import DAO.CartDB;
import Model.Cart;
import Model.CartItems;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "updateCartQuantity", urlPatterns = {"/cart/update-quantity"})
public class UpdateCartQuantity extends HttpServlet {

    private final CartDB cartDB = new CartDB();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        user currentUser = (user) session.getAttribute("user");
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String productIdStr = req.getParameter("productId");
        String quantityStr = req.getParameter("quantity");
        if (productIdStr == null || quantityStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int productId;
        int quantity;
        try {
            productId = Integer.parseInt(productIdStr);
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 1) quantity = 1;
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Cart cart = cartDB.getOrCreateCartByUserId(currentUser.getUser_id());
        if (cart == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        boolean ok = cartDB.updateQuantityAddToCart(cart.getCart_id(), productId, quantity);

        // Refresh session cart items after update & sync current prices (include discount)
        List<CartItems> cartItems = new ArrayList<>();
        cartItems = cartDB.getCartItemsByCartId(cart.getCart_id());
        try {
            DAO.ProductDB pd = new DAO.ProductDB();
            for (CartItems ci : cartItems) {
                Model.Product p = pd.getProductById(ci.getProduct_id());
                if (p != null) {
                    ci.setPrice(p.getDiscountedPrice());
                }
            }
        } catch (Exception ignore) {}
        session.setAttribute("cartItems", cartItems);

        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print("{\"ok\":" + ok + "}");
        }
    }
}


