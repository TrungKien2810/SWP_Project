package Controller;

import DAO.UserDB;
import Util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

@WebServlet(name = "passwordResetRequest", urlPatterns = {"/password/reset/request"})
public class PasswordResetRequestServlet extends HttpServlet {

    private static final String FROM_EMAIL = System.getenv().getOrDefault("MAIL_FROM", "yourgmail@gmail.com");
    private static final String APP_PASSWORD = System.getenv().getOrDefault("MAIL_APP_PASSWORD", "app_password_here");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is required");
            return;
        }

        // Enforce Gmail-only for password recovery
        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.endsWith("@gmail.com")) {
            resp.sendRedirect(req.getContextPath() + "/View/forgot-password.jsp?msg=Chỉ hỗ trợ khôi phục qua Gmail (@gmail.com)");
            return;
        }

        UserDB ud = new UserDB();
        if (ud.getUserByEmail(email) != null) {
            String token = generateToken();
            Timestamp expiry = Timestamp.from(Instant.now().plusSeconds(3600));
            ud.setResetToken(email, token, expiry);

            String baseUrl = req.getRequestURL().toString().replace("/password/reset/request", "/password/reset");
            String resetLink = baseUrl + "?token=" + token;
            String body = "<p>Nhấn vào link để đặt lại mật khẩu:</p><p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>";
            try {
                EmailUtil.send(email, "Đặt lại mật khẩu - Pinky Cloud", body, FROM_EMAIL, APP_PASSWORD);
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot send email");
                return;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/View/log.jsp?msg=If the Gmail exists, a reset link has been sent");
    }

    private String generateToken() {
        byte[] b = new byte[32];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}


