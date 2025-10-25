/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.CartItems;
import Model.user;
import DAO.CartDB;
import DAO.ProductDB;
import Model.Cart;
import Model.Product;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import DAO.DiscountDB;
import Util.CartCookieUtil;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "cart", urlPatterns = { "/cart" })
public class cart extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet cart</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet cart at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        user user = (user) session.getAttribute("user");
        List<CartItems> cartItems = new ArrayList<>();
        Object CartItems = session.getAttribute("cartItems");
        if (CartItems != null) {
            cartItems = (List<CartItems>) CartItems;
            // Lấy danh sách voucher được gán cho user để hiển thị select
            if (session.getAttribute("user") != null) {
                Model.user u = (Model.user) session.getAttribute("user");
                // Auto-assign due discounts before loading list
                new DiscountDB().assignDueForUser(u.getUser_id());
                request.setAttribute("assignedDiscounts",
                        new DiscountDB().listAssignedDiscountsForUser(u.getUser_id()));
            }
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
        } else {
            if (session.getAttribute("user") != null) {
                Model.user u = (Model.user) session.getAttribute("user");
                new DiscountDB().assignDueForUser(u.getUser_id());
                request.setAttribute("assignedDiscounts",
                        new DiscountDB().listAssignedDiscountsForUser(u.getUser_id()));
            }
            if (user != null) {
                CartDB cd = new CartDB();
                Cart cart = cd.getCartByUserId(user.getUser_id());
                if (cart != null) {
                    cartItems = cd.getCartItemsByCartId(cart.getCart_id());
                }
                session.setAttribute("cartItems", cartItems);
                request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            } else {
                // Guest: build from cookie
                ProductDB pd = new ProductDB();
                java.util.Map<Integer, Integer> cookieCart = CartCookieUtil.readCartMap(request);
                for (java.util.Map.Entry<Integer, Integer> e : cookieCart.entrySet()) {
                    Product p = pd.getProductById(e.getKey());
                    if (p == null)
                        continue;
                    CartItems ci = new CartItems(0, 0, p.getProductId(), e.getValue(), p.getPrice());
                    cartItems.add(ci);
                }
                session.setAttribute("cartItems", cartItems);
                request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
            }
        }
    }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
