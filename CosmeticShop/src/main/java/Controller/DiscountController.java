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
                if (handleUpsert(request, response, false)) {
                    response.sendRedirect(request.getContextPath() + "/admin?action=discounts");
                }
                break;
            case "update":
                if (handleUpsert(request, response, true)) {
                    response.sendRedirect(request.getContextPath() + "/admin?action=discounts");
                }
                break;
            case "delete":
                try { db().delete(Integer.parseInt(request.getParameter("id"))); } catch (NumberFormatException ignored) {}
                response.sendRedirect(request.getContextPath() + "/admin?action=discounts");
                break;
            default:
                response.sendRedirect("discounts");
        }
    }

    private boolean handleUpsert(HttpServletRequest req, HttpServletResponse resp, boolean update) throws ServletException, IOException {
        String assignmentMode = req.getParameter("assignmentMode");
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String type = req.getParameter("type");
        int currentId = update ? parseInt(req.getParameter("id"), -1) : -1;
        String trimmedCode = (code != null) ? code.trim() : null;
        double value = parseDouble(req.getParameter("value"), 0);
        Double minOrder = parseNullableDouble(req.getParameter("minOrder"));
        Double maxDiscount = parseNullableDouble(req.getParameter("maxDiscount"));
        
        // Giới hạn giá trị để tránh arithmetic overflow
        if (value > 999999999) value = 999999999;
        if (minOrder != null && minOrder > 999999999) minOrder = 999999999.0;
        if (maxDiscount != null && maxDiscount > 999999999) maxDiscount = 999999999.0;
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
        // Giới hạn giá trị để tránh arithmetic overflow
        if (conditionValue != null && conditionValue > 999999999) conditionValue = 999999999.0;
        if ("FIRST_ORDER".equals(conditionType) || "SPECIAL_EVENT".equals(conditionType)) {
            conditionValue = null; // server-side guard
        }
        String conditionDescription = req.getParameter("conditionDescription");
        Boolean specialEvent = "on".equalsIgnoreCase(req.getParameter("specialEvent"));
        // Derive assignment mode if not provided (backward compatibility)
        if (assignmentMode == null || assignmentMode.isBlank()) {
            boolean autoParam = "on".equalsIgnoreCase(req.getParameter("autoAssignAll"));
            if (autoParam) assignmentMode = "AUTO_ALL";
            else if (conditionType != null) assignmentMode = "AUTO_CONDITION";
            else assignmentMode = "MANUAL";
        }
        boolean isAutoAll = "AUTO_ALL".equalsIgnoreCase(assignmentMode);
        boolean isAutoCondition = "AUTO_CONDITION".equalsIgnoreCase(assignmentMode);
        Boolean autoAssignAll = isAutoAll;
        Timestamp assignDate = parseTimestamp(req.getParameter("assignDate"));
        // Sanitize inconsistent fields based on assignment mode
        if (!isAutoCondition) {
            conditionType = null;
            conditionValue = null;
            conditionDescription = null;
        }
        Integer excludeId = update && currentId > 0 ? currentId : null;

        // Validation
        if (trimmedCode == null || trimmedCode.isEmpty()) {
            req.setAttribute("error", "Code không được để trống");
            forwardToForm(req, resp, update);
            return false;
        }
        if (db().existsByCode(trimmedCode, excludeId)) {
            req.setAttribute("error", "Mã giảm giá đã tồn tại");
            forwardToForm(req, resp, update);
            return false;
        }
        code = trimmedCode;
        if (name == null || name.trim().isEmpty()) {
            req.setAttribute("error", "Tên không được để trống");
            forwardToForm(req, resp, update);
            return false;
        }
        if (value <= 0) {
            req.setAttribute("error", "Giá trị phải lớn hơn 0");
            forwardToForm(req, resp, update);
            return false;
        }
        // Kiểm tra ngày tháng: luôn cần start & end; nếu AUTO_ALL, kiểm tra thêm assignDate <= end
        if (start == null || end == null) {
            req.setAttribute("error", "Ngày bắt đầu và kết thúc không được để trống");
            forwardToForm(req, resp, update);
            return false;
        }
        if (start.after(end)) {
            req.setAttribute("error", "Ngày bắt đầu không được sau ngày kết thúc");
            forwardToForm(req, resp, update);
            return false;
        }
        if (isAutoAll && assignDate != null && assignDate.after(end)) {
            req.setAttribute("error", "Ngày gán không được sau ngày kết thúc");
            forwardToForm(req, resp, update);
            return false;
        }

        // Xử lý ngày bắt đầu cho voucher tự động gán
        Timestamp finalStart = start;

        boolean success;
        if (update) {
            success = db().update(currentId, code, name, type, value, minOrder, maxDiscount, finalStart, end, active,
                    description, usageLimit, conditionType, conditionValue, conditionDescription, specialEvent, autoAssignAll, assignDate);
        } else {
            success = db().create(code, name, type, value, minOrder, maxDiscount, finalStart, end, active,
                    description, usageLimit, conditionType, conditionValue, conditionDescription, specialEvent, autoAssignAll, assignDate);
        }
        
        if (!success) {
            req.setAttribute("error", "Có lỗi xảy ra khi lưu dữ liệu");
            forwardToForm(req, resp, update);
            return false;
        }
        
        return true;
    }
    
    private void forwardToForm(HttpServletRequest req, HttpServletResponse resp, boolean update) throws ServletException, IOException {
        // Forward lại form với tất cả parameters để giữ lại giá trị đã nhập
        if (update) {
            try {
                int id = parseInt(req.getParameter("id"), -1);
                req.setAttribute("discount", db().getById(id));
            } catch (NumberFormatException ignored) {}
        }
        req.getRequestDispatcher("/View/discount-form.jsp").forward(req, resp);
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


