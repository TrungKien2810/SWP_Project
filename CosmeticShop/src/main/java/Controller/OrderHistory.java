package Controller;

import DAO.OrderDB;
import Model.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "orderHistory", urlPatterns = {"/my-orders"})
public class OrderHistory extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            Model.user u = (Model.user) session.getAttribute("user");
            OrderDB orderDB = new OrderDB();
            List<Order> orders = orderDB.listByUserId(u.getUser_id());
            // Fetch all items for these orders
            var items = orderDB.listItemsByUserId(u.getUser_id());
            Map<Integer, List<Model.OrderItemSummary>> orderIdToItems = items.stream().collect(Collectors.groupingBy(Model.OrderItemSummary::getOrderId));
            req.setAttribute("orders", orders);

            // Pre-format date/time for JSP (avoid LocalDateTime -> Date issues in JSTL)
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (Order o : orders) {
                Map<String, Object> r = new HashMap<>();
                r.put("orderId", o.getOrderId());
                String dateStr = o.getOrderDate() == null ? "-" : fmt.format(o.getOrderDate());
                r.put("orderDateStr", dateStr);
                r.put("totalAmount", o.getTotalAmount());
                r.put("orderStatus", o.getOrderStatus());
                r.put("paymentStatus", o.getPaymentStatus());
                var its = orderIdToItems.getOrDefault(o.getOrderId(), new ArrayList<>());
                // Names and quantities (pairs for display)
                List<Map<String, Object>> nameQtys = new ArrayList<>();
                for (var it : its) {
                    Map<String, Object> nq = new HashMap<>();
                    nq.put("name", it.getName());
                    nq.put("quantity", it.getQuantity());
                    nameQtys.add(nq);
                }
                if (nameQtys.isEmpty()) {
                    Map<String, Object> fallback = new HashMap<>();
                    fallback.put("name", o.getFirstProductName() != null ? o.getFirstProductName() : ("Đơn hàng #" + o.getOrderId()));
                    fallback.put("quantity", 0);
                    nameQtys.add(fallback);
                }
                r.put("nameQtys", nameQtys);
                // Images list
                List<String> thumbs = its.stream().map(Model.OrderItemSummary::getImageUrl).collect(Collectors.toList());
                if (thumbs.isEmpty() && o.getFirstProductImageUrl() != null) {
                    thumbs.add(o.getFirstProductImageUrl());
                }
                r.put("images", thumbs);
                rows.add(r);
            }
            req.setAttribute("orderRows", rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        req.getRequestDispatcher("/View/my-orders.jsp").forward(req, resp);
    }
}


