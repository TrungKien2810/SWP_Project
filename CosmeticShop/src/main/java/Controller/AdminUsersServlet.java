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
            System.out.println("AdminUsersServlet: Starting doGet");
            UserDB userDB = new UserDB();
            System.out.println("AdminUsersServlet: UserDB created successfully");
            
            // Lấy tham số tìm kiếm, lọc và pagination
            String searchTerm = request.getParameter("search");
            String roleFilter = request.getParameter("role");
            String pageParam = request.getParameter("page");
            
            int page = 1;
            int pageSize = 10; // 10 người dùng mỗi trang
            
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            
            System.out.println("AdminUsersServlet: searchTerm=" + searchTerm + ", roleFilter=" + roleFilter + ", page=" + page);
            
            List<user> users;
            int totalUsers;
            int totalPages;
            
            // Xử lý tìm kiếm và lọc với pagination
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                System.out.println("AdminUsersServlet: Searching users with term: " + searchTerm);
                users = userDB.searchUsersWithPaging(searchTerm, page, pageSize);
                totalUsers = userDB.getSearchUsersCount(searchTerm);
            } else if (roleFilter != null && !roleFilter.trim().isEmpty() && !roleFilter.equals("all")) {
                System.out.println("AdminUsersServlet: Filtering users by role: " + roleFilter);
                users = userDB.getUsersByRoleWithPaging(roleFilter, page, pageSize);
                totalUsers = userDB.getUsersCountByRoleForPaging(roleFilter);
            } else {
                System.out.println("AdminUsersServlet: Getting all users with pagination");
                users = userDB.getUsersWithPaging(page, pageSize);
                totalUsers = userDB.getTotalUsersCount();
            }
            
            // Tính tổng số trang
            totalPages = (int) Math.ceil((double) totalUsers / pageSize);
            if (totalPages == 0) totalPages = 1;
            
            System.out.println("AdminUsersServlet: Found " + (users != null ? users.size() : 0) + " users, total=" + totalUsers + ", totalPages=" + totalPages);
            
            // Lấy thống kê tổng quan (không phụ thuộc vào pagination)
            int totalUsersStats = userDB.getTotalUsersCount();
            int adminUsers = userDB.getUsersCountByRole("ADMIN");
            int userCount = userDB.getUsersCountByRole("USER");
            int newUsers = userDB.getNewUsersThisMonth();
            
            System.out.println("AdminUsersServlet: Stats - total=" + totalUsersStats + ", admin=" + adminUsers + ", user=" + userCount + ", new=" + newUsers);
            
            // Set attributes
            request.setAttribute("users", users);
            request.setAttribute("totalUsers", totalUsersStats);
            request.setAttribute("activeUsers", userCount);
            request.setAttribute("adminUsers", adminUsers);
            request.setAttribute("newUsers", newUsers);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("selectedRole", roleFilter);
            
            // Pagination attributes
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalUsersInCurrentView", totalUsers);
            
            System.out.println("AdminUsersServlet: Forwarding to admin-users.jsp");
            // Forward to admin users page
            request.getRequestDispatcher("/View/admin-users.jsp").forward(request, response);
            System.out.println("AdminUsersServlet: Forward completed");
            
        } catch (Exception e) {
            System.err.println("AdminUsersServlet: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            
            // Kiểm tra xem response đã được commit chưa
            if (response.isCommitted()) {
                System.err.println("AdminUsersServlet: Response already committed, cannot forward");
                return;
            }
            
            request.setAttribute("error", "Có lỗi xảy ra khi tải danh sách người dùng: " + e.getMessage());
            try {
                request.getRequestDispatcher("/View/admin-users.jsp").forward(request, response);
            } catch (Exception ex) {
                System.err.println("AdminUsersServlet: Error forwarding to error page: " + ex.getMessage());
                ex.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống");
            }
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
                case "updateUser":
                    updateUser(request, response);
                    break;
                case "createUser":
                    createUser(request, response);
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
        String userIdStr = request.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            request.setAttribute("error", "ID người dùng không hợp lệ");
            response.sendRedirect("admin-users");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            user currentUser = (user) request.getSession().getAttribute("user");
            
            // Kiểm tra không được xóa chính mình
            if (currentUser.getUser_id() == userId) {
                request.setAttribute("error", "Không thể xóa chính mình");
            } else {
                UserDB userDB = new UserDB();
                if (userDB.deleteUser(userId)) {
                    request.setAttribute("success", "Xóa người dùng thành công");
                } else {
                    request.setAttribute("error", "Không thể xóa người dùng");
                }
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID người dùng không hợp lệ");
        }
        
        response.sendRedirect("admin-users");
    }
    
    private void updateUserRole(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String newRole = request.getParameter("newRole");
        
        if (userIdStr == null || userIdStr.trim().isEmpty() || newRole == null || newRole.trim().isEmpty()) {
            request.setAttribute("error", "Thông tin không hợp lệ");
            response.sendRedirect("admin-users");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            user currentUser = (user) request.getSession().getAttribute("user");
            
            // Kiểm tra không được thay đổi role của chính mình
            if (currentUser.getUser_id() == userId) {
                request.setAttribute("error", "Không thể thay đổi vai trò của chính mình");
            } else {
                UserDB userDB = new UserDB();
                if (userDB.updateUserRole(userId, newRole)) {
                    request.setAttribute("success", "Cập nhật vai trò thành công");
                } else {
                    request.setAttribute("error", "Không thể cập nhật vai trò");
                }
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID người dùng không hợp lệ");
        }
        
        response.sendRedirect("admin-users");
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        
        if (userIdStr == null || userIdStr.trim().isEmpty() || 
            fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Thông tin không đầy đủ");
            response.sendRedirect("admin-users");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            UserDB userDB = new UserDB();
            
            // Kiểm tra email có trùng với user khác không
            user existingUser = userDB.getUserByEmail(email);
            if (existingUser != null && existingUser.getUser_id() != userId) {
                request.setAttribute("error", "Email đã được sử dụng bởi người dùng khác");
                response.sendRedirect("admin-users");
                return;
            }
            
            // Tạo user object để cập nhật
            user userToUpdate = new user();
            userToUpdate.setUser_id(userId);
            userToUpdate.setUsername(fullName);
            userToUpdate.setEmail(email);
            userToUpdate.setPhone(phone != null ? phone : "");
            userToUpdate.setRole(role != null ? role : "USER");
            
            if (userDB.updateUser(userToUpdate)) {
                request.setAttribute("success", "Cập nhật thông tin người dùng thành công");
            } else {
                request.setAttribute("error", "Không thể cập nhật thông tin người dùng");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID người dùng không hợp lệ");
        }
        
        response.sendRedirect("admin-users");
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");
        
        // Validation
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin bắt buộc");
            response.sendRedirect("admin-users");
            return;
        }
        
        // Kiểm tra email format
        if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            request.setAttribute("error", "Email phải có định dạng Gmail (xxx@gmail.com)");
            response.sendRedirect("admin-users");
            return;
        }
        
        // Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            response.sendRedirect("admin-users");
            return;
        }
        
        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
            response.sendRedirect("admin-users");
            return;
        }
        
        try {
            UserDB userDB = new UserDB();
            
            // Kiểm tra email đã tồn tại
            if (userDB.emailExists(email)) {
                request.setAttribute("error", "Email đã được sử dụng bởi người dùng khác");
                response.sendRedirect("admin-users");
                return;
            }
            
            // Tạo người dùng mới
            if (userDB.createUser(username, email, phone, password, role)) {
                request.setAttribute("success", "Tạo người dùng mới thành công");
            } else {
                request.setAttribute("error", "Không thể tạo người dùng mới");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tạo người dùng: " + e.getMessage());
        }
        
        response.sendRedirect("admin-users");
    }
}
