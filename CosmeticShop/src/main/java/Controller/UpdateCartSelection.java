package Controller;

import DAO.CartDB;
import Model.Cart;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "updateCartSelection", urlPatterns = {"/cart/update-selection"})
public class UpdateCartSelection extends HttpServlet {

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
        String isSelectedStr = req.getParameter("isSelected");
        
        if (productIdStr == null || isSelectedStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int productId;
        boolean isSelected;
        try {
            productId = Integer.parseInt(productIdStr);
            isSelected = Boolean.parseBoolean(isSelectedStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Cart cart = cartDB.getOrCreateCartByUserId(currentUser.getUser_id());
        if (cart == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        boolean ok = cartDB.updateSelectionStatus(cart.getCart_id(), productId, isSelected);

        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print("{\"ok\":" + ok + "}");
        }
    }
}

