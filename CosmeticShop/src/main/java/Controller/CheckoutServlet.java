package Controller;

import DAO.CartDB;
import DAO.OrderDB;
import DAO.ShippingAddressDB;
import DAO.ShippingMethodDB;
import DAO.ShippingMethodDB.ShippingMethod;
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

        req.setAttribute("addresses", addressDB.getByUserId(currentUser.getUser_id()));
        req.setAttribute("methods", methodDB.getActiveMethods());
        req.setAttribute("items", items);
        req.setAttribute("itemsTotal", itemsTotal);
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

        int addressId = Integer.parseInt(req.getParameter("shipping_address_id"));
        int methodId = Integer.parseInt(req.getParameter("shipping_method_id"));
        String paymentMethod = req.getParameter("payment_method");
        // bank_code UI removed; always redirect to VNPAY when BANK is selected
        String notes = req.getParameter("notes");

        double itemsTotal = cartDB.calculateSelectedTotal(cart.getCart_id());
        ShippingMethod method = methodDB.getById(methodId);
        double shippingCost = method != null ? method.getCost() : 0.0;
        double totalAmount = itemsTotal + shippingCost;

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

        List<OrderDetail> details = new ArrayList<>();
        for (Model.CheckoutItem ci : items) {
            OrderDetail d = new OrderDetail();
            d.setProductId(ci.getProductId());
            d.setQuantity(ci.getQuantity());
            d.setPrice(ci.getPrice());
            details.add(d);
        }

        Integer orderId = orderDB.createOrder(order);
        if (orderId == null) {
            resp.sendRedirect(req.getContextPath() + "/checkout?error=order");
            return;
        }
        boolean detailsOk = orderDB.addOrderDetails(orderId, details);
        if (!detailsOk) {
            resp.sendRedirect(req.getContextPath() + "/checkout?error=details");
            return;
        }
        if ("BANK".equalsIgnoreCase(paymentMethod)) {
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


