package Controller;

import DAO.DiscountDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet(name = "DiscountController", urlPatterns = {"/discounts"})
public class DiscountController extends HttpServlet {

    private DiscountDB db;

    private DiscountDB db() {
        if (db == null) db = new DiscountDB();
        return db;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";
        switch (action) {
            case "new":
                request.getRequestDispatcher("/View/discount-form.jsp").forward(request, response);
                break;
            case "edit":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("discount", db().getById(id));
                } catch (NumberFormatException ignored) {}
                request.getRequestDispatcher("/View/discount-form.jsp").forward(request, response);
                break;
            default:
                request.setAttribute("discounts", db().listAll());
                request.getRequestDispatcher("/View/discount-manager.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("discounts");
            return;
        }

        switch (action) {
            case "create":
                handleUpsert(request, false);
                response.sendRedirect("discounts");
                break;
            case "update":
                handleUpsert(request, true);
                response.sendRedirect("discounts");
                break;
            case "delete":
                try { db().delete(Integer.parseInt(request.getParameter("id"))); } catch (NumberFormatException ignored) {}
                response.sendRedirect("discounts");
                break;
            default:
                response.sendRedirect("discounts");
        }
    }

    private void handleUpsert(HttpServletRequest req, boolean update) {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String type = req.getParameter("type");
        double value = parseDouble(req.getParameter("value"), 0);
        Double minOrder = parseNullableDouble(req.getParameter("minOrder"));
        Double maxDiscount = parseNullableDouble(req.getParameter("maxDiscount"));
        boolean active = "on".equalsIgnoreCase(req.getParameter("active"));
        Timestamp start = parseTimestamp(req.getParameter("start"));
        Timestamp end = parseTimestamp(req.getParameter("end"));

        if (update) {
            int id = parseInt(req.getParameter("id"), -1);
            db().update(id, code, name, type, value, minOrder, maxDiscount, start, end, active);
        } else {
            db().create(code, name, type, value, minOrder, maxDiscount, start, end, active);
        }
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    private double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }
    private Double parseNullableDouble(String s) {
        try { if (s == null || s.isBlank()) return null; return Double.parseDouble(s); } catch (Exception e) { return null; }
    }
    private Timestamp parseTimestamp(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            String norm = s.trim().replace('T', ' ');
            if (norm.length() == 16) {
                norm += ":00"; // add seconds
            }
            return Timestamp.valueOf(norm);
        } catch (Exception e) {
            return null;
        }
    }
}


