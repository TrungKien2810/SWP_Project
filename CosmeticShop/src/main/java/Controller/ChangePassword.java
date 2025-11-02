package Controller;

import DAO.UserDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "changePassword", urlPatterns = {"/change-password"})
public class ChangePassword extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/View/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (currentPassword == null || newPassword == null || confirmPassword == null ||
            currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            req.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            req.getRequestDispatcher("/View/change-password.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            req.getRequestDispatcher("/View/change-password.jsp").forward(req, resp);
            return;
        }

        Model.user user = (Model.user) session.getAttribute("user");
        UserDB ud = new UserDB();

        // Enforce Gmail-only accounts for changing password
        String userEmail = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();
        if (!userEmail.endsWith("@gmail.com")) {
            req.setAttribute("error", "Chỉ tài khoản đăng ký bằng Gmail mới được đổi mật khẩu tại đây");
            req.getRequestDispatcher("/View/change-password.jsp").forward(req, resp);
            return;
        }

        // Kiểm tra mật khẩu hiện tại
        if (!ud.login(user.getEmail(), currentPassword)) {
            req.setAttribute("error", "Mật khẩu hiện tại không đúng");
            req.getRequestDispatcher("/View/change-password.jsp").forward(req, resp);
            return;
        }

        // Cập nhật mật khẩu mới
        if (ud.updatePassword(user.getEmail(), newPassword)) {
            req.setAttribute("success", "Đổi mật khẩu thành công");
        } else {
            req.setAttribute("error", "Có lỗi xảy ra khi đổi mật khẩu");
        }

        req.getRequestDispatcher("/View/change-password.jsp").forward(req, resp);
    }
}

