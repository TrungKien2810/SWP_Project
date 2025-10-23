package Controller;

import DAO.DiscountDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet(name = "DiscountController", urlPatterns = {"/discounts", "/my-promos"})
public class DiscountController extends HttpServlet {

    private DiscountDB db;

    private DiscountDB db() {
        if (db == null) db = new DiscountDB();
        return db;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if ("/my-promos".equals(servletPath)) {
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                request.setAttribute("error", "Vui lòng đăng nhập để xem mã của bạn.");
                request.getRequestDispatcher("/View/log.jsp").forward(request, response);
                return;
            }
            Model.user u = (Model.user) session.getAttribute("user");
            // Auto-assign due discounts for this user then load
            db().assignDueForUser(u.getUser_id());
            request.setAttribute("assignedDiscounts", db().listAssignedDiscountsForUser(u.getUser_id()));
            request.getRequestDispatcher("/View/my-discounts.jsp").forward(request, response);
            return;
        }
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

        // Extended fields
        String description = req.getParameter("description");
        Integer usageLimit = null;
        try { String s = req.getParameter("usageLimit"); if (s != null && !s.isBlank()) usageLimit = Integer.parseInt(s.trim()); } catch (Exception ignored) {}
        String conditionType = req.getParameter("conditionType");
        if (conditionType != null) {
            conditionType = conditionType.trim();
            if (conditionType.isEmpty()) {
                conditionType = null;
            } else if (!("TOTAL_SPENT".equals(conditionType) || "ORDER_COUNT".equals(conditionType) ||
                    "FIRST_ORDER".equals(conditionType) || "SPECIAL_EVENT".equals(conditionType))) {
                conditionType = null;
            }
        }
        Double conditionValue = parseNullableDouble(req.getParameter("conditionValue"));
        if ("FIRST_ORDER".equals(conditionType) || "SPECIAL_EVENT".equals(conditionType)) {
            conditionValue = null; // server-side guard
        }
        String conditionDescription = req.getParameter("conditionDescription");
        Boolean specialEvent = "on".equalsIgnoreCase(req.getParameter("specialEvent"));
        Boolean autoAssignAll = "on".equalsIgnoreCase(req.getParameter("autoAssignAll"));
        Timestamp assignDate = parseTimestamp(req.getParameter("assignDate"));

        if (update) {
            int id = parseInt(req.getParameter("id"), -1);
            db().update(id, code, name, type, value, minOrder, maxDiscount, start, end, active,
                    description, usageLimit, conditionType, conditionValue, conditionDescription, specialEvent, autoAssignAll, assignDate);
        } else {
            db().create(code, name, type, value, minOrder, maxDiscount, start, end, active,
                    description, usageLimit, conditionType, conditionValue, conditionDescription, specialEvent, autoAssignAll, assignDate);
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


