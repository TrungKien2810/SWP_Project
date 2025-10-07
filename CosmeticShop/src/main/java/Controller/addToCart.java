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

        // ✅ Lấy tham số từ request
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        String stockStr = request.getParameter("stock");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");
        String categoryIdStr = request.getParameter("categoryId");

        // ✅ Nếu thiếu tham số → báo lỗi nhẹ nhàng
        if (idStr == null || name == null || priceStr == null || imageUrl == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số sản phẩm khi thêm vào giỏ hàng!");
            return;
        }

        // ✅ Parse dữ liệu
        int id = Integer.parseInt(idStr);
        double price = Double.parseDouble(priceStr);
        int stock = (stockStr != null && !stockStr.isEmpty()) ? Integer.parseInt(stockStr) : 1;
        int categoryId = (categoryIdStr != null && !categoryIdStr.isEmpty()) ? Integer.parseInt(categoryIdStr) : 0;

        // ✅ Tạo đối tượng Product
        Product p = new Product(id, name, price, stock, description, imageUrl, categoryId);

        // ✅ Lấy giỏ hàng từ session
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // ✅ Kiểm tra nếu sản phẩm đã có → tăng số lượng
        boolean found = false;
        for (Product existing : cart.keySet()) {
            if (existing.getProductId() == p.getProductId()) {
                cart.put(existing, cart.get(existing) + 1);
                found = true;
                break;
            }
        }

        // ✅ Nếu chưa có sản phẩm trong giỏ
        if (!found) {
            cart.put(p, 1);
        }

        // ✅ Tính toán tổng tiền
        double subtotal = 0;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            subtotal += entry.getKey().getPrice() * entry.getValue();
        }
        double shipping = (subtotal > 0) ? 30000 : 0;
        double discount = 0;
        double total = subtotal + shipping - discount;

        // ✅ Lưu lại giỏ hàng vào session
        session.setAttribute("cart", cart);

        // ✅ Truyền dữ liệu sang JSP
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("shipping", shipping);
        request.setAttribute("discount", discount);
        request.setAttribute("total", total);
        request.setAttribute("cart", cart);

        // ✅ Chuyển sang trang giỏ hàng
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
