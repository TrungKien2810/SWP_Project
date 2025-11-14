/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.CartDB;
import DAO.ProductDB;
import Model.Cart;
import Model.CartItems;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Model.Product;
import Model.user;
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
                    if (removeProduct != null) {
                        cart.remove(removeProduct);
                    }
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
                        cartTotalSelected += entry.getKey().getDiscountedPrice() * entry.getValue();
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
//        processRequest(request, response);
        HttpSession session = request.getSession();
        CartDB cd = new CartDB();
        ProductDB pd = new ProductDB();
        List<CartItems> cartItems = new ArrayList<>();
        boolean isExist = false;
        int p_id = 0;
        int addQuantity = 1;
        String idRaw = request.getParameter("id");
        String qtyRaw = request.getParameter("quantity");
        String buyNow = request.getParameter("buyNow"); // Kiểm tra xem có phải "Mua ngay" không
        
        try {
            p_id = Integer.parseInt(idRaw);
        } catch (NumberFormatException e) {
            String error = e.getMessage();
            request.setAttribute("error", error);
            request.getRequestDispatcher("/View/collection.jsp").forward(request, response);
            return;
        }
        // Parse optional quantity, default 1
        try {
            if (qtyRaw != null && !qtyRaw.trim().isEmpty()) {
                addQuantity = Integer.parseInt(qtyRaw.trim());
            }
        } catch (NumberFormatException ignore) {
            addQuantity = 1;
        }
        if (addQuantity < 1) {
            addQuantity = 1;
        }
        if (session.getAttribute("user") != null) {
            user user = (user) session.getAttribute("user");
            Cart cart;
            
            cart = cd.getOrCreateCartByUserId(user.getUser_id());
            if (cart == null) {
                request.setAttribute("error", "Không thể tạo/tải giỏ hàng. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/products").forward(request, response);
                return;
            }
            // Kiểm tra sản phẩm hợp lệ
            Product product = pd.getProductById(p_id);
            if (product == null) {
                request.setAttribute("error", "Sản phẩm không tồn tại hoặc đã bị xóa.");
                request.getRequestDispatcher("/products").forward(request, response);
                return;
            }
            if (cd.getCartItemsByCartId(cart.getCart_id()) != null) {
                cartItems = cd.getCartItemsByCartId(cart.getCart_id());
            }
            //Giỏ hàng có sản phẩm
            if (!cartItems.isEmpty()) {
                for (CartItems c : cartItems) {
                    int quantity = c.getQuantity();
                    if (p_id != 0 && p_id == c.getProduct_id()) {
                        // Increase by requested quantity with simple stock clamp
                        int maxAvailable = product.getStock() > 0 ? product.getStock() : Integer.MAX_VALUE;
                        long requestedTotal = (long) quantity + (long) addQuantity;
                        if (requestedTotal > maxAvailable) {
                            quantity = maxAvailable;
                        } else {
                            quantity = (int) requestedTotal;
                        }
                        c.setQuantity(quantity);
                        cd.updateQuantityAddToCart(cart.getCart_id(), p_id, c.getQuantity());
                        isExist = true;
                    }
                }

            }
            if (isExist == false) {
                int createQty = addQuantity;
                int maxAvailable = product.getStock() > 0 ? product.getStock() : Integer.MAX_VALUE;
                if (createQty > maxAvailable) {
                    createQty = maxAvailable;
                }
                cd.addCartItems(cart.getCart_id(), p_id, createQty, product.getDiscountedPrice());
            }
            cartItems.clear();
            cartItems = cd.getCartItemsByCartId(cart.getCart_id());
            session.setAttribute("cartItems", cartItems);
            
            // Nếu là "Mua ngay", chuyển hướng đến giỏ hàng
            if ("true".equals(buyNow)) {
                session.setAttribute("cartSuccessMsg", "Đã thêm sản phẩm vào giỏ hàng!");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            
            // Nếu là "Thêm vào giỏ" thông thường, quay lại trang trước
            session.setAttribute("cartSuccessMsg", "Đã thêm vào giỏ hàng thành công!");
            String referer = request.getHeader("referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } else {
            // Guest user: store to cookie
            // Validate product exists
            Product product = pd.getProductById(p_id);
            if (product == null) {
                request.setAttribute("error", "Sản phẩm không tồn tại hoặc đã bị xóa.");
                request.getRequestDispatcher("/products").forward(request, response);
                return;
            }
            // Read cookie map, increment, write back
            java.util.Map<Integer, Integer> cookieCart = CartCookieUtil.readCartMap(request);
            int newQty = cookieCart.getOrDefault(p_id, 0) + addQuantity;
            cookieCart.put(p_id, newQty);
            CartCookieUtil.writeCartMap(response, cookieCart);
            
            // ✅ Cập nhật session cartItems từ cookie để đồng bộ
            List<CartItems> guestCartItems = new ArrayList<>();
            for (java.util.Map.Entry<Integer, Integer> e : cookieCart.entrySet()) {
                Product p = pd.getProductById(e.getKey());
                if (p == null) continue;
                guestCartItems.add(new CartItems(0, 0, p.getProductId(), e.getValue(), p.getDiscountedPrice()));
            }
            session.setAttribute("cartItems", guestCartItems);
            
            // Nếu là "Mua ngay", chuyển hướng đến giỏ hàng
            if ("true".equals(buyNow)) {
                session.setAttribute("cartSuccessMsg", "Đã thêm sản phẩm vào giỏ hàng!");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            
            // Nếu là "Thêm vào giỏ" thông thường, quay lại trang trước
            session.setAttribute("cartSuccessMsg", "Đã thêm vào giỏ hàng thành công!");
            String referer = request.getHeader("referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
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
