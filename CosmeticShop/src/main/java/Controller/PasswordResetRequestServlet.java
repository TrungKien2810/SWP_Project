package Controller;

import DAO.UserDB;
import Model.user;
import Util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

@WebServlet(name = "passwordResetRequest", urlPatterns = {"/password/reset/request", "/test-servlet"})
public class PasswordResetRequestServlet extends HttpServlet {

    private String getFromEmail(HttpServletRequest req) {
        String fromEnv = System.getenv("MAIL_FROM");
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv;
        }
        return req.getServletContext().getInitParameter("MAIL_FROM");
    }
    
    private String getAppPassword(HttpServletRequest req) {
        String passwordEnv = System.getenv("MAIL_APP_PASSWORD");
        if (passwordEnv != null && !passwordEnv.isEmpty()) {
            return passwordEnv;
        }
        return req.getServletContext().getInitParameter("MAIL_APP_PASSWORD");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("PasswordResetRequestServlet doGet called");
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println("<h1>Password Reset Request Servlet is working!</h1>");
        resp.getWriter().println("<p>Current time: " + new java.util.Date() + "</p>");
        resp.getWriter().println("<p>Context path: " + req.getContextPath() + "</p>");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is required");
            return;
        }

        // Validate Gmail format only
        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            resp.sendRedirect(req.getContextPath() + "/View/forgot-password.jsp?msg=Chỉ hỗ trợ khôi phục qua Gmail (@gmail.com)");
            return;
        }

        UserDB ud = new UserDB();
        user existingUser = ud.getUserByEmail(email);
        if (existingUser != null) {
            String token = generateToken();
            Timestamp expiry = Timestamp.from(Instant.now().plusSeconds(3600));
            ud.setResetToken(email, token, expiry);

            String baseUrl = req.getRequestURL().toString().replace("/password/reset/request", "/password/reset");
            String resetLink = baseUrl + "?token=" + token;
            String body = "<p>Nhấn vào link để đặt lại mật khẩu:</p><p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>";
            try {
                String fromEmail = getFromEmail(req);
                String appPassword = getAppPassword(req);
                
                // Kiểm tra cấu hình email
                if (fromEmail == null || fromEmail.equals("yourgmail@gmail.com") || 
                    appPassword == null || appPassword.equals("your_16_character_app_password")) {
                    // Tạm thời bỏ qua gửi email, chỉ tạo token
                    String message = "Token đã được tạo. Vui lòng cấu hình email để gửi link khôi phục.";
                    resp.sendRedirect(req.getContextPath() + "/View/log.jsp?msg=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
                    return;
                }
                
                EmailUtil.send(email, "Đặt lại mật khẩu - Pinky Cloud", body, fromEmail, appPassword);
                String message = "Link khôi phục mật khẩu đã được gửi đến email của bạn";
                resp.sendRedirect(req.getContextPath() + "/View/log.jsp?msg=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace(); // Log lỗi để debug
                String errorMessage = "Lỗi gửi email. Vui lòng kiểm tra cấu hình email hoặc thử lại sau.";
                resp.sendRedirect(req.getContextPath() + "/View/forgot-password.jsp?msg=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
                return;
            }
        } else {
            // Không tiết lộ thông tin về việc email có tồn tại hay không để bảo mật
            String securityMessage = "Nếu email tồn tại trong hệ thống, link khôi phục đã được gửi";
            resp.sendRedirect(req.getContextPath() + "/View/log.jsp?msg=" + URLEncoder.encode(securityMessage, StandardCharsets.UTF_8));
        }
    }

    private String generateToken() {
        byte[] b = new byte[32];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}


