package Controller;

import DAO.lienheDAO;
import Model.lienhe;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "lienheServlet", urlPatterns = {"/lienheServlet"})
public class lienheServlet extends HttpServlet {

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

            // Tạo đối tượng liên hệ
            lienhe contact = new lienhe(name, phone, address, email, subject, message);

            // Gọi DAO để lưu vào DB
            lienheDAO dao = new lienheDAO();
            boolean result = dao.insertContact(contact);

            // Điều hướng kết quả
            if (result) {
                // ✅ Gửi thành công → điều hướng về lại trang liên hệ + thông báo
                // response.sendRedirect(request.getContextPath() + "/View/lienhe.jsp?msg=Gửi thành công! Cảm ơn bạn đã góp ý 💌");
                request.getRequestDispatcher("/View/lienhe.jsp?msg=Gửi thành công! Cảm ơn bạn đã góp ý 💌").forward(request, response);
            } else {
                // ❌ Gửi thất bại
                // response.sendRedirect(request.getContextPath() + "/View/lienhe.jsp?msg=Gửi thất bại. Vui lòng thử lại ❌");
                request.getRequestDispatcher("/View/lienhe.jsp?msg=Gửi thất bại. Vui lòng thử lại ❌").forward(request, response);

            }

        } catch (Exception e) {
            e.printStackTrace();
            // Luôn có phản hồi để tránh load mãi
            response.sendRedirect(request.getContextPath() + "/View/lienhe.jsp?msg=Lỗi máy chủ! Không gửi được phản hồi ⚠️");
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
        response.sendRedirect(request.getContextPath() + "/View/lienhe.jsp");
    }
}
