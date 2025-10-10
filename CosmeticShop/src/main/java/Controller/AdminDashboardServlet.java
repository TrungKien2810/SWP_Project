package Controller;

import DAO.ProductDB;
import DAO.UserDB;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin-dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        user currentUser = (user) session.getAttribute("user");
        
        // Kiểm tra quyền admin
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            request.setAttribute("error", "Bạn không có quyền truy cập trang này");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
            return;
        }
        
        try {
            // Lấy thống kê cơ bản
            ProductDB productDB = new ProductDB();
            UserDB userDB = new UserDB();
            
            // Đếm tổng số sản phẩm
            int totalProducts = productDB.getTotalProductsCount();
            
            // Lấy danh sách sản phẩm gần đây
            List<Model.Product> recentProducts = productDB.getAllProducts();
            if (recentProducts.size() > 5) {
                recentProducts = recentProducts.subList(0, 5);
            }
            
            // Set attributes cho JSP
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("recentProducts", recentProducts);
            request.setAttribute("currentUser", currentUser);
            
            // Forward to admin dashboard
            request.getRequestDispatcher("/View/admin-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải dashboard");
            request.getRequestDispatcher("/View/admin-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
