/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.CartDB;
import Model.Cart;
import Model.CartItems;
import Model.CartItems;
import Model.user;
import DAO.ProductDB;
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
import Util.CartCookieUtil;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "removeFromCart", urlPatterns = {"/removeFromCart"})
public class removeFromCart extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet removeFromCart</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet removeFromCart at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        user user = (user) session.getAttribute("user");
        String rawId = request.getParameter("productId");
        int p_id = 0;
        try{
            p_id = Integer.parseInt(rawId);
        }catch(NumberFormatException e){
            e.printStackTrace();
        }
        if (user != null) {
            CartDB cd = new CartDB();
            Cart cart = cd.getCartByUserId(user.getUser_id());
            List<CartItems> cartItems = new ArrayList<>();
            if (cart != null) {
                cd.removeFromCart(cart.getCart_id(), p_id);
                cartItems = cd.getCartItemsByCartId(cart.getCart_id());
            }
            session.setAttribute("cartItems", cartItems);
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
        } else {
            // Guest: remove from cookie
            java.util.Map<Integer, Integer> cookieCart = CartCookieUtil.readCartMap(request);
            cookieCart.remove(p_id);
            CartCookieUtil.writeCartMap(response, cookieCart);
            // rebuild session cartItems
            ProductDB pd = new ProductDB();
            List<CartItems> cartItems = new ArrayList<>();
            for (java.util.Map.Entry<Integer, Integer> e : cookieCart.entrySet()) {
                Product p = pd.getProductById(e.getKey());
                if (p == null) continue;
                cartItems.add(new CartItems(0, 0, p.getProductId(), e.getValue(), p.getDiscountedPrice()));
            }
            session.setAttribute("cartItems", cartItems);
            request.getRequestDispatcher("/View/cart.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
