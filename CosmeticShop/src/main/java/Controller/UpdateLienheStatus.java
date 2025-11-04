/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.lienheDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
@WebServlet(name = "UpdateLienheStatus", urlPatterns = {"/UpdateLienheStatusServlet"})
public class UpdateLienheStatus extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String context = request.getContextPath();

        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("status");

        if (idParam == null || statusParam == null) {
            response.sendRedirect(context + "/lienheManagerServlet?msg=Thiếu tham số yêu cầu");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(context + "/lienheManagerServlet?msg=ID không hợp lệ");
            return;
        }

        // Hỗ trợ cả boolean và nhãn tiếng Việt từ dropdown
        boolean statusBool;
        String normalized = statusParam.trim().toLowerCase();
        if ("true".equals(normalized) || "1".equals(normalized) || "đã xử lý".equals(normalized) || "da xu ly".equals(normalized)) {
            statusBool = true;
        } else if ("false".equals(normalized) || "0".equals(normalized) || "chưa xử lý".equals(normalized) || "chua xu ly".equals(normalized)) {
            statusBool = false;
        } else {
            // Mặc định an toàn: coi như chưa xử lý
            statusBool = false;
        }

        lienheDAO dao = new lienheDAO();
        boolean updated = dao.updateStatus(id, statusBool);

//        String redirect = context + "/lienheManagerServlet";

//        String location = response.encodeRedirectURL(redirect + (updated ? "?msg=Cập nhật thành công" : "?msg=Cập nhật thất bại"));
if(updated){
    request.getRequestDispatcher("/lienheManagerServlet?msg=Cập nhật thành công").forward(request, response);
}
else{
        request.getRequestDispatcher("/lienheManagerServlet?msg=Cập nhật thất bại").forward(request, response);

}
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Cập nhật trạng thái phản hồi";
    }
}
