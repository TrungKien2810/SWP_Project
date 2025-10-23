package Controller;

import DAO.ShippingAddressDB;
import Model.ShippingAddress;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "shippingAddress", urlPatterns = {"/shipping-address"})
public class ShippingAddressServlet extends HttpServlet {

    private final ShippingAddressDB shippingAddressDB = new ShippingAddressDB();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        user currentUser = (user) session.getAttribute("user");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String returnTo = req.getParameter("return_to");
        if (action == null || action.isEmpty()) {
            List<ShippingAddress> addresses = shippingAddressDB.getByUserId(currentUser.getUser_id());
            req.setAttribute("addresses", addresses);
            if (returnTo != null) {
                req.setAttribute("return_to", returnTo);
            }
            req.getRequestDispatcher("/View/shipping-address.jsp").forward(req, resp);
            return;
        }

        if ("edit".equals(action)) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                ShippingAddress sa = shippingAddressDB.getById(id, currentUser.getUser_id());
                req.setAttribute("address", sa);
            }
            if (returnTo != null) {
                req.setAttribute("return_to", returnTo);
            }
            req.getRequestDispatcher("/View/shipping-address.jsp").forward(req, resp);
            return;
        }

        if ("delete".equals(action)) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean ok = shippingAddressDB.delete(id, currentUser.getUser_id());
                if (!ok) {
                    resp.sendRedirect(req.getContextPath() + "/shipping-address?error=cannot_delete");
                    return;
                }
            }
            resp.sendRedirect(req.getContextPath() + "/shipping-address");
            return;
        }

        if ("default".equals(action)) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                shippingAddressDB.setDefault(id, currentUser.getUser_id());
            }
            resp.sendRedirect(req.getContextPath() + "/shipping-address");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        user currentUser = (user) session.getAttribute("user");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String addressIdStr = req.getParameter("address_id");
        String returnTo = req.getParameter("return_to");
        String fullName = req.getParameter("full_name");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String ward = req.getParameter("ward");
        boolean isDefault = "on".equals(req.getParameter("is_default"));

        ShippingAddress sa = new ShippingAddress();
        sa.setUserId(currentUser.getUser_id());
        sa.setFullName(fullName);
        sa.setPhone(phone);
        sa.setAddress(address);
        sa.setCity(city);
        sa.setDistrict(district);
        sa.setWard(ward);
        sa.setDefault(isDefault);

        if (addressIdStr == null || addressIdStr.isEmpty()) {
            shippingAddressDB.create(sa);
        } else {
            sa.setAddressId(Integer.parseInt(addressIdStr));
            shippingAddressDB.update(sa);
        }

        if ("checkout".equalsIgnoreCase(returnTo)) {
            resp.sendRedirect(req.getContextPath() + "/checkout?address_added=true");
        } else {
            resp.sendRedirect(req.getContextPath() + "/shipping-address");
        }
    }
}


