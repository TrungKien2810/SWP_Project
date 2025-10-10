package Controller;

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

@WebServlet(name = "AdminUsersServlet", urlPatterns = {"/admin-users"})
public class AdminUsersServlet extends HttpServlet {

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
            // Lấy danh sách người dùng (sẽ được phát triển sau)
            // UserDB userDB = new UserDB();
            // List<user> users = userDB.getAllUsers();
            
            // Tạm thời tạo dữ liệu mẫu
            request.setAttribute("totalUsers", 1234);
            request.setAttribute("activeUsers", 1180);
            request.setAttribute("adminUsers", 5);
            request.setAttribute("newUsers", 54);
            
            // Forward to admin users page
            request.getRequestDispatcher("/View/admin-users.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải danh sách người dùng");
            request.getRequestDispatcher("/View/admin-users.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        user currentUser = (user) session.getAttribute("user");
        
        // Kiểm tra quyền admin
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action) {
                case "delete":
                    deleteUser(request, response);
                    break;
                case "updateRole":
                    updateUserRole(request, response);
                    break;
                case "toggleStatus":
                    toggleUserStatus(request, response);
                    break;
                default:
                    response.sendRedirect("admin-users");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu");
            request.getRequestDispatcher("/View/admin-users.jsp").forward(request, response);
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Implement delete user functionality
        String userId = request.getParameter("userId");
        
        // Kiểm tra không được xóa chính mình
        user currentUser = (user) request.getSession().getAttribute("user");
        if (String.valueOf(currentUser.getUser_id()).equals(userId)) {
            request.setAttribute("error", "Không thể xóa chính mình");
        } else {
            // TODO: Xóa user từ database
            request.setAttribute("success", "Xóa người dùng thành công");
        }
        
        response.sendRedirect("admin-users");
    }
    
    private void updateUserRole(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Implement update user role functionality
        String userId = request.getParameter("userId");
        String newRole = request.getParameter("newRole");
        
        // TODO: Cập nhật role trong database
        request.setAttribute("success", "Cập nhật vai trò thành công");
        
        response.sendRedirect("admin-users");
    }
    
    private void toggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Implement toggle user status functionality
        String userId = request.getParameter("userId");
        
        // TODO: Thay đổi trạng thái user (active/inactive)
        request.setAttribute("success", "Cập nhật trạng thái thành công");
        
        response.sendRedirect("admin-users");
    }
}
