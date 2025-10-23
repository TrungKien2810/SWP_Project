package Controller;

import DAO.lienheDAO;
import Model.lienhe;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/lienheServlet")
public class lienheServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        lienhe contact = new lienhe(name, phone, address, email, subject, message);
        lienheDAO dao = new lienheDAO();

        boolean result = dao.insertContact(contact);
        if (result) {
            response.sendRedirect("View/lienhe.jsp?msg=Gửi thành công! Cảm ơn bạn đã góp ý 💌");
        } else {
            response.sendRedirect("View/lienhe.jsp?msg=Gửi thất bại. Vui lòng thử lại ❌");
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
        processRequest(request, response);
    }
}
