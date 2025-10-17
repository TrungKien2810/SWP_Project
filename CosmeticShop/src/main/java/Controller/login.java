/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.CartDB;
import DAO.UserDB;
import Model.Cart;
import Model.CartItems;
import Model.user;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        UserDB ud = new UserDB();
        CartDB cd = new CartDB();

        if (remember != null) {
            Cookie cEmail = new Cookie("email", email);
            Cookie cPass = new Cookie("password", password);
            cEmail.setMaxAge(7 * 24 * 60 * 60);
            cPass.setMaxAge(7 * 24 * 60 * 60);

            cEmail.setHttpOnly(true);
            cPass.setHttpOnly(true);

            response.addCookie(cEmail);
            response.addCookie(cPass);
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            request.setAttribute("error", "Sai cú pháp email");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
            return;
        }
        if (email.equals("") || password.equals("")) {
            request.setAttribute("error", "Không thể để trống thông tin");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
            return;

        }
        if (ud.getUserByEmail(email) == null) {
            request.setAttribute("error", "Tài khoản không tồn tại, bạn đã đăng ký chưa?");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
            return;

        }
        if (ud.login(email, password)) {
            user user = ud.getUserByEmail(email);
            session.setAttribute("user", user);
            Cart cart = cd.getCartByUserId(user.getUser_id());
            if(cart == null){
                cd.addNewCart(user.getUser_id());
                cart = cd.getCartByUserId(user.getUser_id());       
            }
            List<CartItems> cartItems = new ArrayList<>();
            if (!cd.getCartItemsByCartId(cart.getCart_id()).isEmpty()) {
                cartItems = cd.getCartItemsByCartId(cart.getCart_id());
                session.setAttribute("cartItems", cartItems);
            }
            request.getRequestDispatcher("/View/home.jsp").forward(request, response);
            return;

        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.getRequestDispatcher("/View/log.jsp").forward(request, response);
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
