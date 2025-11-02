package Controller;

import DAO.UserDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "passwordReset", urlPatterns = {"/password/reset"})
public class PasswordReset extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token == null || token.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing token");
            return;
        }
        req.setAttribute("token", token);
        req.getRequestDispatcher("/View/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        if (token == null || password == null || !password.equals(confirm)) {
            req.setAttribute("error", "Mật khẩu không khớp");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/View/reset-password.jsp").forward(req, resp);
            return;
        }
        UserDB ud = new UserDB();
        if (ud.updatePasswordByToken(token, password)) {
            resp.sendRedirect(req.getContextPath() + "/View/log.jsp?msg=Password updated, please login");
        } else {
            req.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/View/reset-password.jsp").forward(req, resp);
        }
    }
}


