package Controller;

import DAO.lienheDAO;
import DAO.NotificationDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "lienhe", urlPatterns = {"/lienheServlet"})
public class lienhe extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đảm bảo UTF-8 để nhận tiếng Việt

        try {
            // Lấy dữ liệu từ form
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String email = request.getParameter("email");
            String subject = request.getParameter("subject");
            String message = request.getParameter("message");

            // Kiểm tra và tạo user nếu email chưa tồn tại (để đảm bảo FOREIGN KEY constraint)
            DAO.UserDB userDB = new DAO.UserDB();
            Model.user existingUser = userDB.getUserByEmail(email);
            boolean isNewUser = (existingUser == null);
            
            if (isNewUser) {
                // Tự động tạo user tạm thời với email này
                // Sử dụng tên từ form làm username, password tạm thời (user có thể đổi sau)
                String tempPassword = "temp_" + System.currentTimeMillis(); // Password tạm thời
                boolean userCreated = userDB.signup(name, email, tempPassword);
                if (!userCreated) {
                    // Nếu tạo user thất bại (có thể do username trùng), thử với email làm username
                    String usernameFromEmail = email.split("@")[0]; // Lấy phần trước @
                    userCreated = userDB.signup(usernameFromEmail, email, tempPassword);
                }
                if (!userCreated) {
                    // Nếu vẫn thất bại, thông báo lỗi
                    String errorMsg = "Không thể tạo tài khoản. Vui lòng đăng ký tài khoản trước hoặc liên hệ trực tiếp qua hotline.";
                    String target = request.getContextPath() + "/View/contact.jsp?msg=" + java.net.URLEncoder.encode(errorMsg, "UTF-8");
                    response.sendRedirect(target);
                    return;
                }
            }

            // Tạo đối tượng liên hệ
            Model.lienhe contact = new Model.lienhe(name, phone, address, email, subject, message);

            // Gọi DAO để lưu vào DB
            lienheDAO dao = new lienheDAO();
            boolean result = dao.insertContact(contact);

            // Tạo thông báo cho admin khi có phản hồi khách hàng
            if (result) {
                try {
                    NotificationDB notificationDB = new NotificationDB();
                    String title = "Phản hồi mới từ khách hàng";
                    String notificationMessage = String.format("Khách hàng %s (%s) đã gửi phản hồi với chủ đề: %s", 
                        name, email, subject);
                    String linkUrl = request.getContextPath() + "/admin?action=contact";
                    notificationDB.createNotificationsForAdmins("CUSTOMER_FEEDBACK", title, notificationMessage, linkUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Không làm gián đoạn flow nếu tạo thông báo thất bại
                }
                // ✅ Gửi thành công → điều hướng về lại trang liên hệ + thông báo
                String successMsg = "Gửi thành công! Cảm ơn bạn đã góp ý 💌";
                if (isNewUser) {
                    successMsg += " (Tài khoản đã được tạo tự động với email của bạn. Bạn có thể đăng nhập và đổi mật khẩu sau.)";
                }
                String target = request.getContextPath() + "/View/contact.jsp?msg=" + java.net.URLEncoder.encode(successMsg, "UTF-8");
                response.sendRedirect(target);
            } else {
                // ❌ Gửi thất bại
                String target = request.getContextPath() + "/View/contact.jsp?msg=" + java.net.URLEncoder.encode("Gửi thất bại. Vui lòng thử lại sau ❌", "UTF-8");
                response.sendRedirect(target);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Luôn có phản hồi để tránh load mãi
            String errorMsg = "Lỗi máy chủ! Không gửi được phản hồi ⚠️";
            String target = request.getContextPath() + "/View/contact.jsp?msg=" + java.net.URLEncoder.encode(errorMsg, "UTF-8");
            response.sendRedirect(target);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chỉ nên cho GET hiển thị trang, không xử lý gửi
        response.sendRedirect(request.getContextPath() + "/View/contact.jsp");
    }
}
