/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author LENOVO THINKPAD
 */
@WebServlet(name = "addToCart", urlPatterns = {"/addToCart"})
public class addToCart extends HttpServlet {

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
        HttpSession session = request.getSession();

        // Lấy giỏ hàng từ session
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        // Thao tác trên giỏ hàng: add/update/remove
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "update":
                    int updateId = Integer.parseInt(request.getParameter("id"));
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    for (Product p : cart.keySet()) {
                        if (p.getProductId() == updateId) {
                            cart.put(p, quantity);
                            break;
                        }
                    }
                    break;

                case "remove":
                    int removeId = Integer.parseInt(request.getParameter("id"));
                    Product removeProduct = null;
                    for (Product p : cart.keySet()) {
                        if (p.getProductId() == removeId) {
                            removeProduct = p;
                            break;
                        }
                    }
                    if (removeProduct != null) cart.remove(removeProduct);
                    break;
            }
        }

        // Lấy các sản phẩm được chọn để tính tổng
        String[] selectedIds = request.getParameterValues("selectedItems");
        double cartTotalSelected = 0;
        if (selectedIds != null) {
            for (String idStr : selectedIds) {
                int id = Integer.parseInt(idStr);
                for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                    if (entry.getKey().getProductId() == id) {
                        cartTotalSelected += entry.getKey().getPrice() * entry.getValue();
                    }
                }
            }
        }

        // Áp dụng mã giảm giá nếu có
        String promoCode = request.getParameter("promoCode");
        double discount = 0;
        if (promoCode != null && !promoCode.isEmpty()) {
            if (promoCode.equalsIgnoreCase("PINKY10")) {
                discount = cartTotalSelected * 0.1; // giảm 10%
            }
            // Thêm các mã khác ở đây
        }

        double shipping = (cartTotalSelected > 0) ? 30000 : 0;
        double total = cartTotalSelected + shipping - discount;

        // Gửi dữ liệu sang JSP
        request.setAttribute("cart", cart);
        request.setAttribute("cartTotalSelected", cartTotalSelected);
        request.setAttribute("shipping", shipping);
        request.setAttribute("discount", discount);
        request.setAttribute("total", total);
        request.setAttribute("selectedIds", selectedIds);
        request.setAttribute("promoCode", promoCode);
        request.getRequestDispatcher("View/cart.jsp").forward(request, response);
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
        processRequest(request, response);
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
