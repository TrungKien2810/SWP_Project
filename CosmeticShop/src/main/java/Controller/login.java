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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "login", urlPatterns = {"/login"})
public class login extends HttpServlet {

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
            out.println("<title>Servlet login</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet login at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("/View/log.jsp").forward(request, response);
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
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        UserDB ud = new UserDB();
        if (remember != null) {
                Cookie cEmail = new Cookie("email", email);
                Cookie cPass = new Cookie("password", password);
                // Thời gian sống 7 ngày (tính bằng giây)
                cEmail.setMaxAge(7 * 24 * 60 * 60);
                cPass.setMaxAge(7 * 24 * 60 * 60);
                // Tùy chọn bảo mật
                cEmail.setHttpOnly(true);
                cPass.setHttpOnly(true);

                response.addCookie(cEmail);
                response.addCookie(cPass);
            }
        if (!email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            request.setAttribute("error", "Email must be a valid Gmail address");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
        }
        if(email.equals("") || password.equals("")){
            request.setAttribute("error", "Không thể để trống thông tin");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
        }
        if (ud.getUserByEmail(email) == null) {
            request.setAttribute("error", "account is not exist, you aren't sign up?");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
        }
        if (ud.login(email, password)) {
            session.setAttribute("user", ud.getUserByEmail(email));
            request.getRequestDispatcher("/View/home.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Email or password is invalid, please try again");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
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
