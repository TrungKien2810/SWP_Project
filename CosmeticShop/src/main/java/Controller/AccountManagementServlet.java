package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import DAO.OrderDB;
import Model.Order;

@WebServlet(name = "accountManagement", urlPatterns = {"/account-management"})
public class AccountManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // Load order history for current user
        try {
            Model.user u = (Model.user) session.getAttribute("user");
            OrderDB orderDB = new OrderDB();
            List<Order> orders = orderDB.listByUserId(u.getUser_id());
            req.setAttribute("orders", orders);
        } catch (Exception e) {
            // Fail-safe: still render page even if orders fail to load
            e.printStackTrace();
        }
        req.getRequestDispatcher("/View/account-management.jsp").forward(req, resp);
    }
}

