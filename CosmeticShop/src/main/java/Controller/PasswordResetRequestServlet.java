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
import jakarta.servlet.http.HttpSession;
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
            // String body = "<p>Nhấn vào link để đặt lại mật khẩu:</p><p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>";
            String body = createEmailTemplate(resetLink, existingUser.getUsername());
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
                HttpSession session = req.getSession();
                user user = (user) session.getAttribute("user");
                if(user==null){
                    EmailUtil.send(email, "Đặt lại mật khẩu - Pinky Cloud", body, fromEmail, appPassword);
                String message = "Link khôi phục mật khẩu đã được gửi đến email của bạn";
                resp.sendRedirect(req.getContextPath() + "/View/log.jsp?msg=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
                } else {
                EmailUtil.send(email, "Đặt lại mật khẩu - Pinky Cloud", body, fromEmail, appPassword);
                String message = "Link khôi phục mật khẩu đã được gửi đến email của bạn";
                resp.sendRedirect(req.getContextPath() + "/View/home.jsp?msg=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
                }
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

private String createEmailTemplate(String resetLink, String userName) {
    String nameToShow = (userName != null ? userName : "bạn");
    
    String template = "<!DOCTYPE html>\n" +
        "<html lang=\"vi\">\n" +
        "<head>\n" +
        "    <meta charset=\"UTF-8\">\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "    <title>Khôi phục mật khẩu - Pinky Cloud</title>\n" +
        "    <style>\n" +
        "        body { margin: 0; padding: 0; font-family: 'Arial', sans-serif; background-color: #f9f9f9; color: #333; }\n" +
        "        .email-container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }\n" +
        "        .header { background: linear-gradient(135deg, #f76c85, #ff8fa3); padding: 30px 20px; text-align: center; }\n" +
        "        .logo { max-width: 200px; height: auto; margin-bottom: 15px; }\n" +
        "        .content { padding: 40px 30px; line-height: 1.6; }\n" +
        "        .button { display: inline-block; background: #f76c85; color: white !important; padding: 15px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; margin: 25px 0; text-align: center; }\n" +
        "        .button:hover { background: #e55a74; }\n" +
        "        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 25px 0; border-radius: 4px; }\n" +
        "        .footer { background: #f76c85; color: white; padding: 30px 20px; text-align: center; }\n" +
        "        .footer-links a { color: white; text-decoration: none; margin: 0 15px; font-size: 14px; }\n" +
        "        .social-icon { width: 32px; height: 32px; vertical-align: middle; }\n" +
        "        .branch-info { background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; font-size: 14px; }\n" +
        "    </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "    <div class=\"email-container\">\n" +
        "        <!-- Header với Logo -->\n" +
        "        <div class=\"header\">\n" +
        "            <h1 style=\"color: white; margin: 10px 0 5px 0; font-size: 28px;\">PINKY CLOUD</h1>\n" +
        "            <p style=\"color: white; margin: 0; opacity: 0.9;\">Khôi phục mật khẩu của bạn</p>\n" +
        "        </div>\n" +
        "        \n" +
        "        <!-- Nội dung chính -->\n" +
        "        <div class=\"content\">\n" +
        "            <h2 style=\"color: #f76c85; margin-top: 0;\">Xin chào " + nameToShow + "</h2>\n" +
        "            \n" +
        "            <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản <strong>Pinky Cloud</strong> của bạn.</p>\n" +
        "            \n" +
        "            <p>Để đặt lại mật khẩu, vui lòng nhấn vào nút bên dưới:</p>\n" +
        "            \n" +
        "            <div style=\"text-align: center;\">\n" +
        "                <a href=\"" + resetLink + "\" class=\"button\" style=\"display: inline-block; background: #f76c85; color: white; padding: 15px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; margin: 25px 0;\">\n" +
        "                    ĐẶT LẠI MẬT KHẨU\n" +
        "                </a>\n" +
        "            </div>\n" +
        "            \n" +
        "            <p>Hoặc sao chép và dán đường link sau vào trình duyệt:</p>\n" +
        "            <div style=\"background: #f8f9fa; padding: 15px; border-radius: 5px; word-break: break-all;\">\n" +
        "                <code style=\"color: #f76c85; font-size: 12px;\">" + resetLink + "</code>\n" +
        "            </div>\n" +
        "            \n" +
        "            <!-- Cảnh báo bảo mật -->\n" +
        "            <div class=\"warning\">\n" +
        "                <strong>Lưu ý quan trọng:</strong>\n" +
        "                <ul style=\"margin: 10px 0; padding-left: 20px;\">\n" +
        "                    <li>Liên kết chỉ có hiệu lực trong <strong>1 giờ</strong></li>\n" +
        "                    <li>Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này</li>\n" +
        "                    <li>Liên hệ hỗ trợ ngay nếu bạn nghi ngờ có hoạt động đáng ngờ</li>\n" +
        "                </ul>\n" +
        "            </div>\n" +
        "            \n" +
        "            <!-- Thông tin liên hệ -->\n" +
        "            <div class=\"branch-info\">\n" +
        "                <h4 style=\"color: #f76c85; margin-top: 0;\">Hỗ trợ khách hàng</h4>\n" +
        "                <p><strong>Trụ sở chính:</strong> Số 31, đường Nguyễn Thị Minh Khai, Phường Quy Nhơn, Gia Lai</p>\n" +
        "                <p><strong>Email:</strong> pinkycloudvietnam@gmail.com</p>\n" +
        "                <p><strong>Website:</strong> www.pinkycloud.vn</p>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        \n" +
        "        <!-- Footer -->\n" +
        "        <div class=\"footer\">\n" +
        "            <div class=\"footer-links\">\n" +
        "                <a href=\"https://yourwebsite.com/View/vechungtoi.jsp\">VỀ CHÚNG TÔI</a>\n" +
        "                <a href=\"https://yourwebsite.com/products\">BỘ SƯU TẬP</a>\n" +
        "                <a href=\"https://yourwebsite.com/View/lienhe.jsp\">LIÊN HỆ</a>\n" +
        "            </div>\n" +
        "            \n" +
        "            <div style=\"margin-top: 20px; font-size: 12px; opacity: 0.9;\">\n" +
        "                <p style=\"margin: 5px 0;\">© 2024 Pinky Cloud. All rights reserved.</p>\n" +
        "                <p style=\"margin: 5px 0;\">Sản phẩm chăm sóc da, Mỹ phẩm trang điểm, Mỹ phẩm chính hãng</p>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</body>\n" +
        "</html>";
    
    return template;
}


}


