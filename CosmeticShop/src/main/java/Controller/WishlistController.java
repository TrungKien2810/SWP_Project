package Controller;

import DAO.WishlistDB;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "WishlistController", urlPatterns = {"/wishlist"})
public class WishlistController extends HttpServlet {

    private WishlistDB wishlistDB = new WishlistDB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Kiểm tra đăng nhập
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/View/log.jsp?msg=" + 
                java.net.URLEncoder.encode("Vui lòng đăng nhập để xem wishlist", "UTF-8"));
            return;
        }

        user user = (user) session.getAttribute("user");
        
        // Lấy danh sách wishlist
        request.setAttribute("wishlistItems", wishlistDB.getWishlistByUserId(user.getUser_id()));
        request.setAttribute("wishlistCount", wishlistDB.getWishlistCount(user.getUser_id()));
        
        request.getRequestDispatcher("/View/wishlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Kiểm tra đăng nhập
        if (session == null || session.getAttribute("user") == null) {
            sendJsonResponse(response, false, "Vui lòng đăng nhập", null);
            return;
        }

        user user = (user) session.getAttribute("user");
        String action = request.getParameter("action");
        
        if (action == null) {
            sendJsonResponse(response, false, "Thiếu tham số action", null);
            return;
        }

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            
            boolean success = false;
            String message = "";
            
            switch (action) {
                case "add":
                    if (wishlistDB.isInWishlist(user.getUser_id(), productId)) {
                        sendJsonResponse(response, false, "Sản phẩm đã có trong wishlist", null);
                        return;
                    }
                    success = wishlistDB.addToWishlist(user.getUser_id(), productId);
                    message = success ? "Đã thêm vào wishlist" : "Không thể thêm vào wishlist";
                    break;
                    
                case "remove":
                    success = wishlistDB.removeFromWishlist(user.getUser_id(), productId);
                    message = success ? "Đã xóa khỏi wishlist" : "Không thể xóa khỏi wishlist";
                    break;
                    
                case "toggle":
                    if (wishlistDB.isInWishlist(user.getUser_id(), productId)) {
                        success = wishlistDB.removeFromWishlist(user.getUser_id(), productId);
                        message = success ? "Đã xóa khỏi wishlist" : "Không thể xóa";
                    } else {
                        success = wishlistDB.addToWishlist(user.getUser_id(), productId);
                        message = success ? "Đã thêm vào wishlist" : "Không thể thêm";
                    }
                    break;
                    
                case "check":
                    boolean inWishlist = wishlistDB.isInWishlist(user.getUser_id(), productId);
                    Map<String, Object> data = new HashMap<>();
                    data.put("inWishlist", inWishlist);
                    sendJsonResponse(response, true, "", data);
                    return;
                    
                default:
                    sendJsonResponse(response, false, "Action không hợp lệ", null);
                    return;
            }
            
            int wishlistCount = wishlistDB.getWishlistCount(user.getUser_id());
            Map<String, Object> data = new HashMap<>();
            data.put("wishlistCount", wishlistCount);
            data.put("inWishlist", action.equals("add") || (action.equals("toggle") && success));
            
            sendJsonResponse(response, success, message, data);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Product ID không hợp lệ", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Có lỗi xảy ra: " + e.getMessage(), null);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, Map<String, Object> data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("success", success);
        jsonResponse.put("message", message);
        if (data != null) {
            jsonResponse.putAll(data);
        }
        
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}

