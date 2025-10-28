package Controller;
import DAO.CartDB;
import DAO.OrderDB;
import DAO.ShippingAddressDB;
import DAO.ShippingMethodDB;
import DAO.ShippingMethodDB.ShippingMethod;
import DAO.DiscountDB;
import Model.Cart;
import Model.Order;
import Model.OrderDetail;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "checkout", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    private final CartDB cartDB = new CartDB();
    private final ShippingAddressDB addressDB = new ShippingAddressDB();
    private final ShippingMethodDB methodDB = new ShippingMethodDB();
    private final OrderDB orderDB = new OrderDB();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        user currentUser = (user) session.getAttribute("user");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Cart cart = cartDB.getOrCreateCartByUserId(currentUser.getUser_id());
        List<Model.CheckoutItem> items = cartDB.getSelectedCheckoutItems(cart.getCart_id());
        double itemsTotal = cartDB.calculateSelectedTotal(cart.getCart_id());
        
        // Lấy discount từ session (nếu có)
        Double appliedDiscountAmount = 0.0;
        Object discountObj = session.getAttribute("appliedDiscountAmount");
        if (discountObj instanceof Double) {
            appliedDiscountAmount = (Double) discountObj;
        } else if (discountObj != null) {
            try {
                appliedDiscountAmount = Double.parseDouble(discountObj.toString());
            } catch (NumberFormatException e) {
                appliedDiscountAmount = 0.0;
            }
        }
        
        // Tính tổng tiền sau khi áp dụng discount
        double finalTotal = Math.max(0, itemsTotal - appliedDiscountAmount);

        req.setAttribute("addresses", addressDB.getByUserId(currentUser.getUser_id()));
        req.setAttribute("methods", methodDB.getActiveMethods());
        req.setAttribute("items", items);
        req.setAttribute("itemsTotal", itemsTotal);
        req.setAttribute("appliedDiscountAmount", appliedDiscountAmount);
        req.setAttribute("finalTotal", finalTotal);
        req.getRequestDispatcher("/View/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        user currentUser = (user) session.getAttribute("user");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Cart cart = cartDB.getOrCreateCartByUserId(currentUser.getUser_id());
        List<Model.CheckoutItem> items = cartDB.getSelectedCheckoutItems(cart.getCart_id());
        if (items.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String addressIdStr = req.getParameter("shipping_address_id");
        if (addressIdStr == null || addressIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/checkout?error=no_address");
            return;
        }
        int addressId = Integer.parseInt(addressIdStr);
        int methodId = Integer.parseInt(req.getParameter("shipping_method_id"));
        String paymentMethod = req.getParameter("payment_method");
        // bank_code UI removed; always redirect to VNPAY when BANK is selected
        String notes = req.getParameter("notes");

        double itemsTotal = cartDB.calculateSelectedTotal(cart.getCart_id());
        
        // Lấy discount từ session (nếu có)
        Double appliedDiscountAmount = 0.0;
        String appliedDiscountCode = null;
        Object discountObj = session.getAttribute("appliedDiscountAmount");
        Object discountCodeObj = session.getAttribute("appliedDiscountCode");
        
        if (discountObj instanceof Double) {
            appliedDiscountAmount = (Double) discountObj;
        } else if (discountObj != null) {
            try {
                appliedDiscountAmount = Double.parseDouble(discountObj.toString());
            } catch (NumberFormatException e) {
                appliedDiscountAmount = 0.0;
            }
        }
        
        if (discountCodeObj instanceof String) {
            appliedDiscountCode = (String) discountCodeObj;
        }
        
        // Tính tổng tiền sau khi áp dụng discount
        double subtotalAfterDiscount = Math.max(0, itemsTotal - appliedDiscountAmount);
        
        ShippingMethod method = methodDB.getById(methodId);
        double shippingCost = method != null ? method.getCost() : 0.0;
        double totalAmount = subtotalAfterDiscount + shippingCost;

        Order order = new Order();
        order.setUserId(currentUser.getUser_id());
        order.setShippingAddressId(addressId);
        order.setShippingMethodId(methodId);
        order.setShippingCost(shippingCost);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("PENDING");
        order.setOrderStatus("PENDING");
        order.setNotes(notes);
        order.setTotalAmount(totalAmount);
        
        // Lưu thông tin discount vào order (nếu có)
        if (appliedDiscountCode != null && appliedDiscountAmount > 0) {
            // Tìm discount_id từ code để lưu vào order
            DiscountDB discountDB = new DiscountDB();
            Model.Discount discount = discountDB.validateAndGetDiscount(appliedDiscountCode);
            if (discount != null) {
                order.setDiscountId(discount.getDiscountId());
                order.setDiscountAmount(appliedDiscountAmount);
            }
        }

        List<OrderDetail> details = new ArrayList<>();
        for (Model.CheckoutItem ci : items) {
            OrderDetail d = new OrderDetail();
            d.setProductId(ci.getProductId());
            d.setQuantity(ci.getQuantity());
            d.setPrice(ci.getPrice());
            details.add(d);
        }

        // TRỪ KHO NGAY KHI TẠO ĐƠN HÀNG (PENDING)
        DAO.ProductDB productDB = new DAO.ProductDB();
        for (OrderDetail d : details) {
            boolean stockOk = productDB.decreaseStock(d.getProductId(), d.getQuantity());
            if (!stockOk) {
                resp.sendRedirect(req.getContextPath() + "/checkout?error=out_of_stock");
                return;
            }
        }
        
        Integer orderId = orderDB.createOrder(order);
        if (orderId == null) {
            // Nếu tạo đơn thất bại, phải hoàn lại kho
            for (OrderDetail d : details) {
                productDB.increaseStock(d.getProductId(), d.getQuantity());
            }
            resp.sendRedirect(req.getContextPath() + "/checkout?error=order");
            return;
        }
        boolean detailsOk = orderDB.addOrderDetails(orderId, details);
        if (!detailsOk) {
            // Nếu add details thất bại, phải hoàn lại kho
            for (OrderDetail d : details) {
                productDB.increaseStock(d.getProductId(), d.getQuantity());
            }
            resp.sendRedirect(req.getContextPath() + "/checkout?error=details");
            return;
        }
        
        // // Nếu có discount được áp dụng, đánh dấu đã sử dụng
        if (appliedDiscountCode != null && appliedDiscountAmount > 0) {
            DiscountDB discountDB = new DiscountDB();
            Model.Discount discount = discountDB.validateAndGetDiscount(appliedDiscountCode);
            if (discount != null) {
                discountDB.decrementAssignedDiscountUse(currentUser.getUser_id(), discount.getDiscountId());
            }
        }
        
        // Xóa discount khỏi session sau khi đặt hàng thành công
        session.removeAttribute("appliedDiscountCode");
        session.removeAttribute("appliedDiscountAmount");
        
        if ("BANK".equalsIgnoreCase(paymentMethod)) {
            // Lưu cartId vào session để xóa cart sau khi thanh toán thành công
            session.setAttribute("pendingCartId", cart.getCart_id());
            String createUrl = req.getContextPath() + "/payment/vnpay/create?orderId=" + orderId;
            resp.sendRedirect(createUrl);
            return;
        } else {
            orderDB.clearSelectedCartItems(cart.getCart_id());
            session.removeAttribute("cartItems");
            session.removeAttribute("cartId");
            String msg = "Đặt hàng thành công!";
            String target = req.getContextPath() + "/View/home.jsp?msg=" + java.net.URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8);
            resp.sendRedirect(target);
        }
    }
}