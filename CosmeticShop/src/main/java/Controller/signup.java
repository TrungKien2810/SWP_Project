/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.UserDB;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "signup", urlPatterns = {"/signup"})
public class signup extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet signup</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet signup at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/View/register.jsp").forward(request, response);
        return;
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String cp = request.getParameter("confirm-password");
        UserDB ud = new UserDB();
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            session.setAttribute("signupErrorMsg", "Email phải là địa chỉ Gmail hợp lệ (@gmail.com)!");
            response.sendRedirect(request.getContextPath() + "/signup");
            return;
        }
        if(email.equals("") || password.equals("") || username.equals("") || cp.equals("")){
            session.setAttribute("signupErrorMsg", "Vui lòng điền đầy đủ tất cả các thông tin!");
            response.sendRedirect(request.getContextPath() + "/signup");
            return;
        }
        if (!cp.equals(password)) {
            session.setAttribute("signupErrorMsg", "Mật khẩu nhập lại không khớp!");
            response.sendRedirect(request.getContextPath() + "/signup");
            return;
        }
        if (ud.getUserByEmail(email) != null) {
            session.setAttribute("signupErrorMsg", "Email đã được sử dụng. Vui lòng chọn email khác!");
            response.sendRedirect(request.getContextPath() + "/signup");
            return;
        }
        if (ud.isFullNameTaken(username)) {
            session.setAttribute("signupErrorMsg", "Tên hiển thị đã được sử dụng. Vui lòng chọn tên khác!");
            response.sendRedirect(request.getContextPath() + "/signup");
            return;
        }
        if (ud.signup(username, email, password)) {
            session.setAttribute("user", ud.getUserByEmail(email));
            session.setAttribute("signupSuccessMsg", "Đăng ký thành công! Chào mừng " + username + " đến với PinkyCloud!");
            response.sendRedirect(request.getContextPath() + "/View/home.jsp");
            return;
        } else {
            session.setAttribute("signupErrorMsg", "Email đã được sử dụng. Vui lòng chọn email khác!");
            response.sendRedirect(request.getContextPath() + "/signup");
            return;
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
