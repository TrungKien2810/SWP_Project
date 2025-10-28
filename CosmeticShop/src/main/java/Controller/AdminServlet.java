package Controller;

import DAO.DiscountDB;
import DAO.ProductDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    private DiscountDB discountDB;
    private ProductDB productDB;
    private DAO.CategoryDB categoryDB;

    private DiscountDB discountDb() {
        if (discountDB == null) discountDB = new DiscountDB();
        return discountDB;
    }

    private ProductDB productDb() {
        if (productDB == null) productDB = new ProductDB();
        return productDB;
    }

    private DAO.CategoryDB categoryDb() {
        if (categoryDB == null) categoryDB = new DAO.CategoryDB();
        return categoryDB;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) action = "dashboard";

        switch (action) {
            case "dashboard":
                request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
                break;
            case "products":
                String search = request.getParameter("search");
                java.util.List<Model.Product> list = (search != null && !search.isBlank())
                        ? productDb().searchProducts(search.trim())
                        : productDb().getAllProducts();
                java.util.Map<Integer, String> categoryNames = new java.util.HashMap<>();
                for (Model.Product p : list) {
                    int cid = p.getCategoryId();
                    if (cid > 0 && !categoryNames.containsKey(cid)) {
                        String name = productDb().getCategoryNameById(cid);
                        if (name != null) categoryNames.put(cid, name);
                    }
                }
                request.setAttribute("search", search);
                request.setAttribute("productList", list);
                request.setAttribute("categoryNames", categoryNames);
                request.getRequestDispatcher("/admin/manage-products.jsp").forward(request, response);
                break;
            case "orders":
                String statusFilter = request.getParameter("status");
                String dateFilter = request.getParameter("dateFilter");
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                
                DAO.OrderDB orderDB = new DAO.OrderDB();
                java.util.List<Model.Order> orderList;
                
                // Xử lý lọc theo ngày (ưu tiên filter ngày trước)
                if ("today".equals(dateFilter)) {
                    java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                    if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                        // Filter cả ngày VÀ status
                        java.util.List<Model.Order> dateFilteredOrders = orderDB.getOrdersByDate(today);
                        orderList = new java.util.ArrayList<>();
                        for (Model.Order order : dateFilteredOrders) {
                            if (order.getOrderStatus().equals(statusFilter)) {
                                orderList.add(order);
                            }
                        }
                    } else {
                        orderList = orderDB.getOrdersByDate(today);
                    }
                } else if ("dateRange".equals(dateFilter) && startDateStr != null && endDateStr != null) {
                    try {
                        java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
                        java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
                        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                            // Filter cả khoảng ngày VÀ status
                            java.util.List<Model.Order> dateFilteredOrders = orderDB.getOrdersByDateRange(startDate, endDate);
                            orderList = new java.util.ArrayList<>();
                            for (Model.Order order : dateFilteredOrders) {
                                if (order.getOrderStatus().equals(statusFilter)) {
                                    orderList.add(order);
                                }
                            }
                        } else {
                            orderList = orderDB.getOrdersByDateRange(startDate, endDate);
                        }
                    } catch (Exception e) {
                        orderList = orderDB.getAllOrders();
                    }
                } else if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                    orderList = orderDB.getOrdersByStatus(statusFilter);
                } else {
                    // Không filter gì - lấy tất cả
                    orderList = orderDB.getAllOrders();
                }
                
                // Get customer names for orders
                DAO.UserDB userDB = new DAO.UserDB();
                java.util.Map<Integer, String> customerNames = new java.util.HashMap<>();
                for (Model.Order order : orderList) {
                    Model.user customer = userDB.getUserById(order.getUserId());
                    if (customer != null) {
                        customerNames.put(order.getOrderId(), customer.getUsername());
                    }
                }
                
                request.setAttribute("orders", orderList);
                request.setAttribute("customerNames", customerNames);
                request.setAttribute("selectedStatus", statusFilter);
                request.setAttribute("selectedDateFilter", dateFilter);
                request.setAttribute("selectedStartDate", startDateStr);
                request.setAttribute("selectedEndDate", endDateStr);
                request.getRequestDispatcher("/admin/manage-orders.jsp").forward(request, response);
                break;
            case "orderDetail":
                String orderIdStr = request.getParameter("orderId");
                if (orderIdStr != null) {
                    try {
                        int orderId = Integer.parseInt(orderIdStr);
                        DAO.OrderDB odb = new DAO.OrderDB();
                        Model.Order order = odb.getFullOrderDetails(orderId);
                        
                        if (order != null) {
                            // Get customer info
                            DAO.UserDB udb = new DAO.UserDB();
                            Model.user customer = udb.getUserById(order.getUserId());
                            request.setAttribute("customer", customer);
                            
                            // Get shipping address
                            if (order.getShippingAddressId() != null) {
                                DAO.ShippingAddressDB addressDB = new DAO.ShippingAddressDB();
                                Model.ShippingAddress address = addressDB.getById(order.getShippingAddressId());
                                request.setAttribute("shippingAddress", address);
                            }
                            
                            // Get shipping method
                            if (order.getShippingMethodId() != null) {
                                DAO.ShippingMethodDB methodDB = new DAO.ShippingMethodDB();
                                DAO.ShippingMethodDB.ShippingMethod method = methodDB.getById(order.getShippingMethodId());
                                request.setAttribute("shippingMethod", method);
                            }
                            
                            // Get order items
                            java.util.List<DAO.OrderDB.OrderDetailInfo> items = odb.getOrderDetailItems(orderId);
                            request.setAttribute("orderItems", items);
                            
                            request.setAttribute("order", order);
                            request.getRequestDispatcher("/admin/order-detail.jsp").forward(request, response);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        // Invalid order ID
                    }
                }
                response.sendRedirect(request.getContextPath() + "/admin?action=orders");
                break;
            case "users":
                request.getRequestDispatcher("/admin/manage-users.jsp").forward(request, response);
                break;
            case "categories":
                request.setAttribute("categories", categoryDb().listAll());
                request.getRequestDispatcher("/admin/manage-categories.jsp").forward(request, response);
                break;
            case "discounts":
                request.setAttribute("discounts", discountDb().listAll());
                request.getRequestDispatcher("/admin/manage-discounts.jsp").forward(request, response);
                break;
            case "reports":
                request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
                break;
            case "settings":
                request.getRequestDispatcher("/admin/settings.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("categories".equals(action)) {
            String op = request.getParameter("op");
            if ("create".equals(op)) {
                String name = request.getParameter("name");
                String desc = request.getParameter("description");
                if (name != null && !name.isBlank()) {
                    categoryDb().create(name.trim(), (desc != null ? desc.trim() : null));
                }
                response.sendRedirect(request.getContextPath() + "/admin?action=categories");
                return;
            }
            if ("update".equals(op)) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("name");
                    String desc = request.getParameter("description");
                    if (name != null && !name.isBlank()) {
                        categoryDb().update(id, name.trim(), (desc != null ? desc.trim() : null));
                    }
                } catch (Exception ignored) {}
                response.sendRedirect(request.getContextPath() + "/admin?action=categories");
                return;
            }
            if ("delete".equals(op)) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    categoryDb().delete(id);
                } catch (Exception ignored) {}
                response.sendRedirect(request.getContextPath() + "/admin?action=categories");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
    }
}


