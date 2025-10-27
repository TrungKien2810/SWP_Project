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
                request.getRequestDispatcher("/admin/manage-orders.jsp").forward(request, response);
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


