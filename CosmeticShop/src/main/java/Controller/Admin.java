package Controller;

import DAO.DiscountDB;
import DAO.ProductDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "Admin", urlPatterns = {"/admin"})
@MultipartConfig
public class Admin extends HttpServlet {

    private DiscountDB discountDB;
    private ProductDB productDB;
    private DAO.CategoryDB categoryDB;
    private DAO.BannerDB bannerDB;

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

    private DAO.BannerDB bannerDb() {
        if (bannerDB == null) bannerDB = new DAO.BannerDB();
        return bannerDB;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) action = "dashboard";

        switch (action) {
            case "dashboard":
                // Dữ liệu thật cho dashboard
                request.setAttribute("doanhThuHomNay", getTodayRevenue());
                request.setAttribute("soDonMoi", getTodayNewOrders());
                request.setAttribute("soKhachMoi", getTodayNewUsers());
                request.setAttribute("soSPHetHang", getLowStockCount(5));
                request.setAttribute("nhan7Ngay", getLast7DayLabels());
                request.setAttribute("doanhThu7Ngay", getLast7DaysRevenue());
                request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
                break;
            case "products":
                String search = request.getParameter("search");
                java.util.List<Model.Product> list = (search != null && !search.isBlank())
                        ? productDb().searchProducts(search.trim())
                        : productDb().getAllProducts();
                // Load category names cho từng product (nhiều categories)
                java.util.Map<Integer, java.util.List<String>> productCategoryMap = new java.util.HashMap<>();
                DAO.ProductCategoryDB pcDB = new DAO.ProductCategoryDB();
                for (Model.Product p : list) {
                    java.util.List<String> catNames = pcDB.getCategoryNamesByProductId(p.getProductId());
                    productCategoryMap.put(p.getProductId(), catNames);
                }
                request.setAttribute("search", search);
                request.setAttribute("productList", list);
                request.setAttribute("productCategoryMap", productCategoryMap);
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
                            // Check if fullPage parameter is set (for direct page view, not modal)
                            String fullPage = request.getParameter("fullPage");
                            if (fullPage != null && "true".equals(fullPage)) {
                                // Forward to full page with header/footer
                                request.getRequestDispatcher("/admin/order-detail-page.jsp").forward(request, response);
                            } else {
                                // Forward to partial JSP (for modal in manage-orders.jsp)
                                request.getRequestDispatcher("/admin/order-detail.jsp").forward(request, response);
                            }
                            return;
                        }
                    } catch (NumberFormatException e) {
                        // Invalid order ID
                    }
                }
                response.sendRedirect(request.getContextPath() + "/admin?action=orders");
                break;
            case "users":
                DAO.UserDB userDBGet = new DAO.UserDB();
                String searchKeyword = request.getParameter("search");
                String roleFilter = request.getParameter("roleFilter");
                
                java.util.List<Model.user> userList;
                
                // Xử lý tìm kiếm và lọc
                if (searchKeyword != null && !searchKeyword.isBlank()) {
                    userList = userDBGet.searchUsers(searchKeyword.trim());
                    // Áp dụng filter role sau khi search
                    if (roleFilter != null && !roleFilter.isBlank() && !"ALL".equals(roleFilter)) {
                        userList.removeIf(u -> !u.getRole().equals(roleFilter));
                    }
                } else if (roleFilter != null && !roleFilter.isBlank() && !"ALL".equals(roleFilter)) {
                    userList = userDBGet.getUsersByRole(roleFilter);
                } else {
                    userList = userDBGet.getAllUsers();
                }
                
                request.setAttribute("users", userList);
                request.setAttribute("searchKeyword", searchKeyword != null ? searchKeyword : "");
                request.setAttribute("roleFilter", roleFilter != null ? roleFilter : "ALL");
                request.getRequestDispatcher("/admin/manage-users.jsp").forward(request, response);
                break;
            case "userDetail":
                String userIdStr = request.getParameter("id");
                if (userIdStr != null && !userIdStr.isBlank()) {
                    try {
                        int userId = Integer.parseInt(userIdStr);
                        DAO.UserDB userDBDetail = new DAO.UserDB();
                        Model.user userDetail = userDBDetail.getUserById(userId);
                        
                        if (userDetail != null) {
                            java.util.List<Model.Order> userOrders = new java.util.ArrayList<>();
                            java.util.List<Model.ShippingAddress> userAddresses = new java.util.ArrayList<>();
                            java.util.List<Model.UserDiscountAssign> userVouchers = new java.util.ArrayList<>();
                            
                            // Lấy danh sách đơn hàng của user
                            try {
                                DAO.OrderDB orderDBDetail = new DAO.OrderDB();
                                userOrders = orderDBDetail.listByUserId(userId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Tiếp tục với danh sách rỗng
                            }
                            
                            // Lấy danh sách địa chỉ giao hàng
                            try {
                                DAO.ShippingAddressDB addressDB = new DAO.ShippingAddressDB();
                                userAddresses = addressDB.getByUserId(userId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Tiếp tục với danh sách rỗng
                            }
                            
                            // Lấy danh sách voucher của user
                            try {
                                DAO.DiscountDB discountDB = new DAO.DiscountDB();
                                userVouchers = discountDB.listAssignedDiscountsForUser(userId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Tiếp tục với danh sách rỗng
                            }
                            
                            request.setAttribute("userDetail", userDetail);
                            request.setAttribute("userOrders", userOrders);
                            request.setAttribute("userAddresses", userAddresses);
                            request.setAttribute("userVouchers", userVouchers);
                            request.getRequestDispatcher("/admin/user-detail.jsp").forward(request, response);
                            return;
                        } else {
                            response.sendRedirect(request.getContextPath() + "/admin?action=users");
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        response.sendRedirect(request.getContextPath() + "/admin?action=users");
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.sendRedirect(request.getContextPath() + "/admin?action=users");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin?action=users");
                }
                break;
            case "categories":
                request.setAttribute("categories", categoryDb().listAll());
                request.getRequestDispatcher("/admin/manage-categories.jsp").forward(request, response);
                break;
            case "discounts":
                request.setAttribute("discounts", discountDb().listAll());
                // Cung cấp danh sách sản phẩm để gán mã giảm giá hàng loạt
                try {
                    request.setAttribute("allProducts", productDb().getAllProducts());
                    request.setAttribute("allCategories", productDb().getAllCategories());
                } catch (Exception ignored) {}
                request.getRequestDispatcher("/admin/manage-discounts.jsp").forward(request, response);
                break;
            case "discountAssign":
                request.setAttribute("discounts", discountDb().listAll());
                try {
                    request.setAttribute("allProducts", productDb().getAllProducts());
                    request.setAttribute("allCategories", productDb().getAllCategories());
                } catch (Exception ignored) {}
                request.getRequestDispatcher("/admin/assign-discounts.jsp").forward(request, response);
                break;
            case "banners":
                String opView = request.getParameter("op");
                if ("edit".equals(opView)) {
                    try {
                        int id = Integer.parseInt(request.getParameter("id"));
                        Model.Banner banner = bannerDb().getById(id);
                        request.setAttribute("editBanner", banner);
                    } catch (Exception ignored) {}
                }
                request.setAttribute("banners", bannerDb().listAll());
                request.getRequestDispatcher("/admin/manage-banners.jsp").forward(request, response);
                break;
            case "reports":
                // Xử lý bộ lọc
                String reportStartDateStr = request.getParameter("startDate");
                String reportEndDateStr = request.getParameter("endDate");
                String orderStatusFilter = request.getParameter("orderStatus");
                String dateQuickFilter = request.getParameter("dateQuickFilter");
                
                java.sql.Date startDate = null;
                java.sql.Date endDate = null;
                boolean hasDateFilter = false;
                
                // Xử lý date quick filter (Tất cả, Hôm nay, 7 ngày qua, Tháng này, Năm nay)
                if (dateQuickFilter != null && !dateQuickFilter.isBlank()) {
                    java.time.LocalDate today = java.time.LocalDate.now();
                    switch (dateQuickFilter) {
                        case "all":
                            // Lấy dữ liệu từ ngày đầu tiên có đơn hàng đến hiện tại
                            // Set một ngày xa (10 năm trước) để lấy toàn bộ dữ liệu
                            startDate = java.sql.Date.valueOf(today.minusYears(10));
                            endDate = java.sql.Date.valueOf(today);
                            hasDateFilter = true;
                            break;
                        case "today":
                            startDate = endDate = java.sql.Date.valueOf(today);
                            hasDateFilter = true;
                            break;
                        case "last7days":
                            startDate = java.sql.Date.valueOf(today.minusDays(6));
                            endDate = java.sql.Date.valueOf(today);
                            hasDateFilter = true;
                            break;
                        case "thisMonth":
                            startDate = java.sql.Date.valueOf(today.withDayOfMonth(1));
                            endDate = java.sql.Date.valueOf(today);
                            hasDateFilter = true;
                            break;
                        case "thisYear":
                            startDate = java.sql.Date.valueOf(today.withDayOfYear(1));
                            endDate = java.sql.Date.valueOf(today);
                            hasDateFilter = true;
                            break;
                    }
                } else if (reportStartDateStr != null && !reportStartDateStr.isBlank() && 
                           reportEndDateStr != null && !reportEndDateStr.isBlank()) {
                    try {
                        startDate = java.sql.Date.valueOf(reportStartDateStr);
                        endDate = java.sql.Date.valueOf(reportEndDateStr);
                        hasDateFilter = true;
                    } catch (Exception e) {
                        // Không set mặc định nếu parse lỗi
                    }
                }
                
                // Mặc định filter theo "DELIVERED" hoặc "COMPLETED" nếu không chọn
                if (orderStatusFilter == null || orderStatusFilter.isBlank() || "ALL".equals(orderStatusFilter)) {
                    orderStatusFilter = null;
                }
                
                // Lấy dữ liệu báo cáo - chỉ khi có date filter
                java.util.Map<String, Object> reportData = null;
                if (hasDateFilter && startDate != null && endDate != null) {
                    reportData = getReportData(startDate, endDate, orderStatusFilter);
                } else {
                    // Tạo empty report data nếu không có filter
                    reportData = new java.util.HashMap<>();
                    reportData.put("totalRevenue", 0.0);
                    reportData.put("totalOrders", 0);
                    reportData.put("averageOrderValue", 0.0);
                    reportData.put("totalDiscount", 0.0);
                    reportData.put("salesByDay", new java.util.LinkedHashMap<>());
                    reportData.put("topProductsByQuantity", new java.util.ArrayList<>());
                    reportData.put("topProductsByRevenue", new java.util.ArrayList<>());
                    reportData.put("lowStockProducts", getLowStockProducts(5));
                    reportData.put("slowMovingProducts", getSlowMovingProducts(90));
                    reportData.put("topCustomers", new java.util.ArrayList<>());
                    reportData.put("newVsReturningCustomers", new java.util.HashMap<>());
                    reportData.put("topDiscounts", new java.util.ArrayList<>());
                }
                
                request.setAttribute("reportData", reportData);
                request.setAttribute("startDate", reportStartDateStr != null ? reportStartDateStr : "");
                request.setAttribute("endDate", reportEndDateStr != null ? reportEndDateStr : "");
                request.setAttribute("orderStatusFilter", orderStatusFilter != null ? orderStatusFilter : "ALL");
                request.setAttribute("dateQuickFilter", dateQuickFilter);
                request.setAttribute("hasDateFilter", hasDateFilter);
                
                request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("banners".equals(action)) {
            String op = request.getParameter("op");
            if ("delete".equals(op)) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    bannerDb().delete(id);
                } catch (Exception ignored) {}
                response.sendRedirect(request.getContextPath() + "/admin?action=banners");
                return;
            }

            String idStr = request.getParameter("id");
            Part imagePart = request.getPart("bannerImage");
            String targetUrl = request.getParameter("targetUrl");
            String displayOrderStr = request.getParameter("displayOrder");
            String isActiveStr = request.getParameter("isActive");

            int displayOrder = 0;
            try { displayOrder = Integer.parseInt(displayOrderStr); } catch (Exception ignored) {}
            boolean isActive = (isActiveStr != null);

            if (idStr != null && !idStr.isBlank()) {
                // update
                int id = Integer.parseInt(idStr);
                if (imagePart != null && imagePart.getSize() > 0) {
                    String imagePath = saveBannerImage(imagePart);
                    bannerDb().updateWithImage(id, imagePath, targetUrl, displayOrder, isActive);
                } else {
                    bannerDb().updateWithoutImage(id, targetUrl, displayOrder, isActive);
                }
            } else {
                // create
                if (imagePart != null && imagePart.getSize() > 0) {
                    String imagePath = saveBannerImage(imagePart);
                    bannerDb().insert(imagePath, targetUrl, displayOrder, isActive);
                }
            }

            response.sendRedirect(request.getContextPath() + "/admin?action=banners");
            return;
        }
        if ("users".equals(action)) {
            String op = request.getParameter("op");
            DAO.UserDB userDBPost = new DAO.UserDB();
            
            if ("updateRole".equals(op)) {
                try {
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    String newRole = request.getParameter("role");
                    if (newRole != null && (newRole.equals("USER") || newRole.equals("ADMIN"))) {
                        userDBPost.updateRole(userId, newRole);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.sendRedirect(request.getContextPath() + "/admin?action=users");
                return;
            }
        }
        if ("discounts".equals(action)) {
            String op = request.getParameter("op");
            if ("assignProducts".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    
                    // Kiểm tra mã giảm giá có còn hiệu lực không
                    String validationError = null;
                    try {
                        validationError = discountDb().validateDiscountForAssignment(discountId);
                    } catch (Exception validationEx) {
                        validationError = "Lỗi kiểm tra mã giảm giá: " + validationEx.getMessage();
                        validationEx.printStackTrace();
                    }
                    
                    if (validationError != null) {
                        String msg = java.net.URLEncoder.encode(validationError, "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    String[] productIdsRaw = request.getParameterValues("productIds");
                    java.util.List<Integer> productIds = new java.util.ArrayList<>();
                    if (productIdsRaw != null) {
                        for (String pid : productIdsRaw) {
                            try { productIds.add(Integer.parseInt(pid)); } catch (Exception ignored) {}
                        }
                    }
                    if (productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào để gán (vui lòng chọn ít nhất một sản phẩm)", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().assignDiscountToProducts(discountId, productIds);
                    if (affected < 0) {
                        String msg = java.net.URLEncoder.encode("Lỗi khi gán mã giảm giá", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    String msg = java.net.URLEncoder.encode("Gán mã thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = java.net.URLEncoder.encode("Gán mã thất bại: " + e.getMessage(), "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                }
            } else if ("unassignProducts".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    String[] productIdsRaw = request.getParameterValues("productIds");
                    java.util.List<Integer> productIds = new java.util.ArrayList<>();
                    if (productIdsRaw != null) {
                        for (String pid : productIdsRaw) {
                            try { productIds.add(Integer.parseInt(pid)); } catch (Exception ignored) {}
                        }
                    }
                    if (productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào để bỏ gán (vui lòng chọn ít nhất một sản phẩm)", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().unassignDiscountFromProducts(discountId, productIds);
                    String msg = java.net.URLEncoder.encode("Bỏ gán mã thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Bỏ gán mã thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            } else if ("assignByCategory".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    
                    // Kiểm tra mã giảm giá có còn hiệu lực không
                    String validationError = null;
                    try {
                        validationError = discountDb().validateDiscountForAssignment(discountId);
                    } catch (Exception validationEx) {
                        validationError = "Lỗi kiểm tra mã giảm giá: " + validationEx.getMessage();
                        validationEx.printStackTrace();
                    }
                    
                    if (validationError != null) {
                        String msg = java.net.URLEncoder.encode(validationError, "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    // Nhận nhiều categoryNames
                    String[] categoryNames = request.getParameterValues("categoryNames");
                    java.util.List<Integer> productIds = new java.util.ArrayList<>();
                    
                    if (categoryNames != null && categoryNames.length > 0) {
                        // Chuyển array thành List và filter empty
                        java.util.List<String> categoryNameList = new java.util.ArrayList<>();
                        for (String catName : categoryNames) {
                            if (catName != null && !catName.trim().isEmpty()) {
                                categoryNameList.add(catName.trim());
                            }
                        }
                        
                        // Lấy sản phẩm có TẤT CẢ các categories (AND logic)
                        if (!categoryNameList.isEmpty()) {
                            productIds = new DAO.ProductCategoryDB().getProductIdsByAllCategoryNames(categoryNameList);
                        }
                    }
                    
                    if (productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào để gán (không có sản phẩm nào có tất cả các danh mục đã chọn)", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().assignDiscountToProducts(discountId, productIds);
                    String msg = java.net.URLEncoder.encode("Gán theo danh mục thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Gán theo danh mục thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            } else if ("unassignByCategory".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    // Nhận nhiều categoryNames
                    String[] categoryNames = request.getParameterValues("categoryNames");
                    java.util.List<Integer> productIds = new java.util.ArrayList<>();
                    
                    if (categoryNames != null && categoryNames.length > 0) {
                        // Chuyển array thành List và filter empty
                        java.util.List<String> categoryNameList = new java.util.ArrayList<>();
                        for (String catName : categoryNames) {
                            if (catName != null && !catName.trim().isEmpty()) {
                                categoryNameList.add(catName.trim());
                            }
                        }
                        
                        // Lấy sản phẩm có TẤT CẢ các categories (AND logic)
                        if (!categoryNameList.isEmpty()) {
                            productIds = new DAO.ProductCategoryDB().getProductIdsByAllCategoryNames(categoryNameList);
                        }
                    }
                    
                    if (productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào để bỏ gán (không có sản phẩm nào có tất cả các danh mục đã chọn)", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().unassignDiscountFromProducts(discountId, productIds);
                    String msg = java.net.URLEncoder.encode("Bỏ gán theo danh mục thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Bỏ gán theo danh mục thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            } else if ("assignByPrice".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    
                    // Kiểm tra mã giảm giá có còn hiệu lực không
                    String validationError = null;
                    try {
                        validationError = discountDb().validateDiscountForAssignment(discountId);
                    } catch (Exception validationEx) {
                        validationError = "Lỗi kiểm tra mã giảm giá: " + validationEx.getMessage();
                        validationEx.printStackTrace();
                    }
                    
                    if (validationError != null) {
                        String msg = java.net.URLEncoder.encode(validationError, "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    double minPrice = 0;
                    double maxPrice = Double.MAX_VALUE;
                    try { String s = request.getParameter("minPrice"); if (s != null && !s.isBlank()) minPrice = Double.parseDouble(s); } catch (Exception ignored) {}
                    try { String s = request.getParameter("maxPrice"); if (s != null && !s.isBlank()) maxPrice = Double.parseDouble(s); } catch (Exception ignored) {}
                    java.util.List<Integer> productIds = productDb().getProductIdsByPriceRange(minPrice, maxPrice);
                    if (productIds == null || productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào để gán (không có sản phẩm nào trong khoảng giá đã chọn)", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().assignDiscountToProducts(discountId, productIds);
                    String msg = java.net.URLEncoder.encode("Gán theo mức giá thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Gán theo mức giá thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            } else if ("unassignByPrice".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    double minPrice = 0;
                    double maxPrice = Double.MAX_VALUE;
                    try { String s = request.getParameter("minPrice"); if (s != null && !s.isBlank()) minPrice = Double.parseDouble(s); } catch (Exception ignored) {}
                    try { String s = request.getParameter("maxPrice"); if (s != null && !s.isBlank()) maxPrice = Double.parseDouble(s); } catch (Exception ignored) {}
                    java.util.List<Integer> productIds = productDb().getProductIdsByPriceRange(minPrice, maxPrice);
                    if (productIds == null || productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào để bỏ gán (không có sản phẩm nào trong khoảng giá đã chọn)", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().unassignDiscountFromProducts(discountId, productIds);
                    String msg = java.net.URLEncoder.encode("Bỏ gán theo mức giá thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Bỏ gán theo mức giá thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            } else if ("unassignAllForProducts".equals(op)) {
                try {
                    String[] productIdsRaw = request.getParameterValues("productIds");
                    java.util.List<Integer> productIds = new java.util.ArrayList<>();
                    if (productIdsRaw != null) {
                        for (String pid : productIdsRaw) {
                            try { productIds.add(Integer.parseInt(pid)); } catch (Exception ignored) {}
                        }
                    }
                    if (productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào để xóa mã (vui lòng chọn ít nhất một sản phẩm)", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().unassignAllDiscountsFromProducts(productIds);
                    String msg = java.net.URLEncoder.encode("Đã xóa tất cả mã của " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Xóa tất cả mã thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            } else if ("assignAllProducts".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    
                    // Kiểm tra mã giảm giá có còn hiệu lực không
                    String validationError = null;
                    try {
                        validationError = discountDb().validateDiscountForAssignment(discountId);
                    } catch (Exception validationEx) {
                        validationError = "Lỗi kiểm tra mã giảm giá: " + validationEx.getMessage();
                        validationEx.printStackTrace();
                    }
                    
                    if (validationError != null) {
                        String msg = java.net.URLEncoder.encode(validationError, "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    // Lấy tất cả product IDs
                    java.util.List<Integer> productIds = productDb().getAllProductIds();
                    
                    if (productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào trong hệ thống", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().assignDiscountToProducts(discountId, productIds);
                    String msg = java.net.URLEncoder.encode("Gán mã cho tất cả sản phẩm thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Gán mã cho tất cả sản phẩm thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            } else if ("unassignAllProducts".equals(op)) {
                try {
                    int discountId = Integer.parseInt(request.getParameter("discountId"));
                    // Lấy tất cả product IDs
                    java.util.List<Integer> productIds = productDb().getAllProductIds();
                    
                    if (productIds.isEmpty()) {
                        String msg = java.net.URLEncoder.encode("Không có sản phẩm nào trong hệ thống", "UTF-8");
                        response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                        return;
                    }
                    
                    int affected = new DAO.ProductDiscountDB().unassignDiscountFromProducts(discountId, productIds);
                    String msg = java.net.URLEncoder.encode("Bỏ gán mã khỏi tất cả sản phẩm thành công: " + affected + " sản phẩm", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = java.net.URLEncoder.encode("Bỏ gán mã khỏi tất cả sản phẩm thất bại", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/admin?action=discountAssign&msg=" + msg);
                return;
            }
        }
        if ("categories".equals(action)) {
            String op = request.getParameter("op");
            if ("create".equals(op)) {
                String name = request.getParameter("name");
                String desc = request.getParameter("description");
                Part imagePart = request.getPart("categoryImage");
                String imageUrl = null;
                if (imagePart != null && imagePart.getSize() > 0) {
                    imageUrl = saveCategoryImage(imagePart);
                }
                if (name != null && !name.isBlank()) {
                    categoryDb().create(name.trim(), (desc != null ? desc.trim() : null), imageUrl);
                }
                response.sendRedirect(request.getContextPath() + "/admin?action=categories");
                return;
            }
            if ("update".equals(op)) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("name");
                    String desc = request.getParameter("description");
                    Part imagePart = request.getPart("categoryImage");
                    if (name != null && !name.isBlank()) {
                        if (imagePart != null && imagePart.getSize() > 0) {
                            String imageUrl = saveCategoryImage(imagePart);
                            categoryDb().update(id, name.trim(), (desc != null ? desc.trim() : null), imageUrl);
                        } else {
                            categoryDb().updateWithoutImage(id, name.trim(), (desc != null ? desc.trim() : null));
                        }
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

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            for (String cd : contentDisp.split(";")) {
                String s = cd.trim();
                if (s.startsWith("filename")) {
                    String name = s.substring(s.indexOf('=') + 1).trim().replace("\"", "");
                    return new java.io.File(name).getName();
                }
            }
        }
        return "file";
    }

    private String saveBannerImage(Part imagePart) throws IOException {
        String submittedFileName = getFileName(imagePart);
        String safeName = System.currentTimeMillis() + "_" + submittedFileName.replaceAll("[^a-zA-Z0-9.\\-_]", "_");

        final String UPLOAD_BASE = "C:\\CosmeticShop\\uploads";
        final String BANNERS_DIR = "banners";

        File dir = new File(UPLOAD_BASE, BANNERS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File saved = new File(dir, safeName);
        try (InputStream in = imagePart.getInputStream(); FileOutputStream out = new FileOutputStream(saved)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
        return "/uploads/" + BANNERS_DIR + "/" + safeName;
    }

    private String saveCategoryImage(Part imagePart) throws IOException {
        String submittedFileName = getFileName(imagePart);
        String safeName = System.currentTimeMillis() + "_" + submittedFileName.replaceAll("[^a-zA-Z0-9.\\-_]", "_");

        final String UPLOAD_BASE = "C:\\CosmeticShop\\uploads";
        final String CATEGORIES_DIR = "categories";

        File dir = new File(UPLOAD_BASE, CATEGORIES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File saved = new File(dir, safeName);
        try (InputStream in = imagePart.getInputStream(); FileOutputStream out = new FileOutputStream(saved)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
        return "/uploads/" + CATEGORIES_DIR + "/" + safeName;
    }

    private double getTodayRevenue() {
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = "SELECT SUM(total_amount) FROM Orders WHERE CAST(order_date AS DATE) = CAST(GETDATE() AS DATE) AND payment_status = 'PAID'";
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql); java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    private int getTodayNewOrders() {
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = "SELECT COUNT(*) FROM Orders WHERE CAST(order_date AS DATE) = CAST(GETDATE() AS DATE) AND (order_status = 'PENDING' OR order_status IS NULL)";
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql); java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int getTodayNewUsers() {
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = "SELECT COUNT(*) FROM Users WHERE CAST(date_create AS DATE) = CAST(GETDATE() AS DATE)";
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql); java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int getLowStockCount(int threshold) {
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = "SELECT COUNT(*) FROM Products WHERE stock <= ?";
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, threshold);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private String[] getLast7DayLabels() {
        java.time.LocalDate today = java.time.LocalDate.now();
        String[] labels = new String[7];
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate d = today.minusDays(i);
            // Nhãn T2..CN
            java.time.DayOfWeek dow = d.getDayOfWeek();
            String label;
            switch (dow) {
                case MONDAY: label = "T2"; break;
                case TUESDAY: label = "T3"; break;
                case WEDNESDAY: label = "T4"; break;
                case THURSDAY: label = "T5"; break;
                case FRIDAY: label = "T6"; break;
                case SATURDAY: label = "T7"; break;
                default: label = "CN";
            }
            labels[6 - i] = label;
        }
        return labels;
    }

    private int[] getLast7DaysRevenue() {
        int[] revenue = new int[7];
        java.util.Map<java.time.LocalDate, Double> map = new java.util.HashMap<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = "SELECT CAST(order_date AS DATE) d, SUM(total_amount) s FROM Orders WHERE payment_status = 'PAID' AND order_date >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE)) GROUP BY CAST(order_date AS DATE)";
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql); java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate(1);
                    double s = rs.getDouble(2);
                    map.put(d.toLocalDate(), s);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate d = today.minusDays(i);
            double val = map.getOrDefault(d, 0.0);
            revenue[6 - i] = (int)Math.round(val);
        }
        return revenue;
    }
    
    // ========== PHƯƠNG THỨC BÁO CÁO ==========
    
    private java.util.Map<String, Object> getReportData(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        
        // Sales Report - Tổng quan
        data.put("totalRevenue", getTotalRevenue(startDate, endDate, orderStatusFilter));
        data.put("totalOrders", getTotalOrders(startDate, endDate, orderStatusFilter));
        data.put("averageOrderValue", getAverageOrderValue(startDate, endDate, orderStatusFilter));
        data.put("totalDiscount", getTotalDiscount(startDate, endDate, orderStatusFilter));
        
        // Sales by Day - Doanh thu theo ngày
        data.put("salesByDay", getSalesByDay(startDate, endDate, orderStatusFilter));
        
        // Top Products - Top sản phẩm bán chạy
        data.put("topProductsByQuantity", getTopProductsByQuantity(startDate, endDate, orderStatusFilter, 10));
        data.put("topProductsByRevenue", getTopProductsByRevenue(startDate, endDate, orderStatusFilter, 10));
        
        // Inventory Report - Báo cáo tồn kho
        data.put("lowStockProducts", getLowStockProducts(5));
        data.put("slowMovingProducts", getSlowMovingProducts(90));
        
        // Customer Report - Báo cáo khách hàng
        data.put("topCustomers", getTopCustomers(startDate, endDate, orderStatusFilter, 10));
        data.put("newVsReturningCustomers", getNewVsReturningCustomers(startDate, endDate, orderStatusFilter));
        
        // Discount Report - Báo cáo khuyến mãi
        data.put("topDiscounts", getTopDiscounts(startDate, endDate, 10));
        
        return data;
    }
    
    private double getTotalRevenue(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter) {
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder("SELECT SUM(total_amount) FROM Orders WHERE CAST(order_date AS DATE) BETWEEN ? AND ? AND payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND order_status = ?");
            }
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }
    
    private int getTotalOrders(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter) {
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Orders WHERE CAST(order_date AS DATE) BETWEEN ? AND ? AND payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND order_status = ?");
            }
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    
    private double getAverageOrderValue(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter) {
        int totalOrders = getTotalOrders(startDate, endDate, orderStatusFilter);
        if (totalOrders == 0) return 0.0;
        return getTotalRevenue(startDate, endDate, orderStatusFilter) / totalOrders;
    }
    
    private double getTotalDiscount(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter) {
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder("SELECT SUM(discount_amount) FROM Orders WHERE CAST(order_date AS DATE) BETWEEN ? AND ? AND payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND order_status = ?");
            }
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }
    
    // Doanh thu theo ngày - trả về Map<Date, Revenue> để vẽ biểu đồ
    private java.util.Map<String, Double> getSalesByDay(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter) {
        java.util.Map<String, Double> sales = new java.util.LinkedHashMap<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder("SELECT CAST(order_date AS DATE) as order_date, SUM(total_amount) as revenue FROM Orders WHERE CAST(order_date AS DATE) BETWEEN ? AND ? AND payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND order_status = ?");
            }
            sql.append(" GROUP BY CAST(order_date AS DATE) ORDER BY order_date");
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String dateStr = rs.getDate("order_date").toString();
                        double revenue = rs.getDouble("revenue");
                        sales.put(dateStr, revenue);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return sales;
    }
    
    // Top sản phẩm theo số lượng
    private java.util.List<java.util.Map<String, Object>> getTopProductsByQuantity(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter, int limit) {
        java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder(
                "SELECT TOP " + limit + " p.product_id, p.name, SUM(od.quantity) as total_quantity " +
                "FROM OrderDetails od " +
                "JOIN Orders o ON od.order_id = o.order_id " +
                "JOIN Products p ON od.product_id = p.product_id " +
                "WHERE CAST(o.order_date AS DATE) BETWEEN ? AND ? AND o.payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND o.order_status = ?");
            }
            sql.append(" GROUP BY p.product_id, p.name ORDER BY total_quantity DESC");
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, Object> product = new java.util.HashMap<>();
                        product.put("productId", rs.getInt("product_id"));
                        product.put("name", rs.getString("name"));
                        product.put("totalQuantity", rs.getInt("total_quantity"));
                        products.add(product);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return products;
    }
    
    // Top sản phẩm theo doanh thu
    private java.util.List<java.util.Map<String, Object>> getTopProductsByRevenue(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter, int limit) {
        java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder(
                "SELECT TOP " + limit + " p.product_id, p.name, SUM(od.quantity * od.price) as total_revenue " +
                "FROM OrderDetails od " +
                "JOIN Orders o ON od.order_id = o.order_id " +
                "JOIN Products p ON od.product_id = p.product_id " +
                "WHERE CAST(o.order_date AS DATE) BETWEEN ? AND ? AND o.payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND o.order_status = ?");
            }
            sql.append(" GROUP BY p.product_id, p.name ORDER BY total_revenue DESC");
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, Object> product = new java.util.HashMap<>();
                        product.put("productId", rs.getInt("product_id"));
                        product.put("name", rs.getString("name"));
                        product.put("totalRevenue", rs.getDouble("total_revenue"));
                        products.add(product);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return products;
    }
    
    // Sản phẩm sắp hết hàng
    private java.util.List<java.util.Map<String, Object>> getLowStockProducts(int threshold) {
        java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = "SELECT product_id, name, stock FROM Products WHERE stock <= ? ORDER BY stock ASC";
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, threshold);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, Object> product = new java.util.HashMap<>();
                        product.put("productId", rs.getInt("product_id"));
                        product.put("name", rs.getString("name"));
                        product.put("stock", rs.getInt("stock"));
                        products.add(product);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return products;
    }
    
    // Sản phẩm tồn kho lâu (không bán được trong X ngày)
    private java.util.List<java.util.Map<String, Object>> getSlowMovingProducts(int days) {
        java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = 
                "SELECT p.product_id, p.name, p.stock, MAX(o.order_date) as last_sold_date " +
                "FROM Products p " +
                "LEFT JOIN OrderDetails od ON p.product_id = od.product_id " +
                "LEFT JOIN Orders o ON od.order_id = o.order_id AND o.payment_status = 'PAID' " +
                "GROUP BY p.product_id, p.name, p.stock " +
                "HAVING MAX(o.order_date) IS NULL OR MAX(o.order_date) < DATEADD(DAY, -" + days + ", GETDATE()) " +
                "ORDER BY p.stock DESC";
            try (java.sql.Statement stmt = c.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    java.util.Map<String, Object> product = new java.util.HashMap<>();
                    product.put("productId", rs.getInt("product_id"));
                    product.put("name", rs.getString("name"));
                    product.put("stock", rs.getInt("stock"));
                    java.sql.Timestamp lastSold = rs.getTimestamp("last_sold_date");
                    product.put("lastSoldDate", lastSold != null ? lastSold.toString() : "Chưa bán");
                    products.add(product);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return products;
    }
    
    // Top khách hàng
    private java.util.List<java.util.Map<String, Object>> getTopCustomers(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter, int limit) {
        java.util.List<java.util.Map<String, Object>> customers = new java.util.ArrayList<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder(
                "SELECT TOP " + limit + " u.user_id, u.full_name, u.email, SUM(o.total_amount) as total_spent, COUNT(o.order_id) as order_count " +
                "FROM Users u " +
                "JOIN Orders o ON u.user_id = o.user_id " +
                "WHERE CAST(o.order_date AS DATE) BETWEEN ? AND ? AND o.payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND o.order_status = ?");
            }
            sql.append(" GROUP BY u.user_id, u.full_name, u.email ORDER BY total_spent DESC");
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, Object> customer = new java.util.HashMap<>();
                        customer.put("userId", rs.getInt("user_id"));
                        customer.put("fullName", rs.getString("full_name"));
                        customer.put("email", rs.getString("email"));
                        customer.put("totalSpent", rs.getDouble("total_spent"));
                        customer.put("orderCount", rs.getInt("order_count"));
                        customers.add(customer);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return customers;
    }
    
    // Tỷ lệ khách hàng mới vs quay lại
    private java.util.Map<String, Integer> getNewVsReturningCustomers(java.sql.Date startDate, java.sql.Date endDate, String orderStatusFilter) {
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            StringBuilder sql = new StringBuilder(
                "SELECT " +
                "  SUM(CASE WHEN first_order.first_order_date = CAST(o.order_date AS DATE) THEN 1 ELSE 0 END) as new_customers, " +
                "  SUM(CASE WHEN first_order.first_order_date < CAST(o.order_date AS DATE) THEN 1 ELSE 0 END) as returning_customers " +
                "FROM Orders o " +
                "JOIN (SELECT user_id, MIN(CAST(order_date AS DATE)) as first_order_date FROM Orders WHERE payment_status = 'PAID' GROUP BY user_id) first_order " +
                "  ON o.user_id = first_order.user_id " +
                "WHERE CAST(o.order_date AS DATE) BETWEEN ? AND ? AND o.payment_status = 'PAID'");
            if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                sql.append(" AND o.order_status = ?");
            }
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql.toString())) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                if (orderStatusFilter != null && !orderStatusFilter.isBlank()) {
                    ps.setString(3, orderStatusFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.put("newCustomers", rs.getInt("new_customers"));
                        result.put("returningCustomers", rs.getInt("returning_customers"));
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        if (result.isEmpty()) {
            result.put("newCustomers", 0);
            result.put("returningCustomers", 0);
        }
        return result;
    }
    
    // Top mã giảm giá
    private java.util.List<java.util.Map<String, Object>> getTopDiscounts(java.sql.Date startDate, java.sql.Date endDate, int limit) {
        java.util.List<java.util.Map<String, Object>> discounts = new java.util.ArrayList<>();
        try {
            java.sql.Connection c = new DAO.DBConnect().conn;
            String sql = 
                "SELECT TOP " + limit + " d.discount_id, d.code, d.name, COUNT(o.order_id) as usage_count, SUM(o.discount_amount) as total_discount " +
                "FROM Discounts d " +
                "JOIN Orders o ON d.discount_id = o.discount_id " +
                "WHERE CAST(o.order_date AS DATE) BETWEEN ? AND ? AND o.payment_status = 'PAID' " +
                "GROUP BY d.discount_id, d.code, d.name " +
                "ORDER BY usage_count DESC";
            try (java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setDate(1, startDate);
                ps.setDate(2, endDate);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.util.Map<String, Object> discount = new java.util.HashMap<>();
                        discount.put("discountId", rs.getInt("discount_id"));
                        discount.put("code", rs.getString("code"));
                        discount.put("name", rs.getString("name"));
                        discount.put("usageCount", rs.getInt("usage_count"));
                        discount.put("totalDiscount", rs.getDouble("total_discount"));
                        discounts.add(discount);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return discounts;
    }
}


