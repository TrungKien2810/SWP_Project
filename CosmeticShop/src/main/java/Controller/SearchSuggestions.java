package Controller;

import DAO.ProductDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "SearchSuggestions", urlPatterns = {"/search-suggestions"})
public class SearchSuggestions extends HttpServlet {
    private ProductDB productDB;

    private ProductDB db() {
        if (productDB == null) {
            productDB = new ProductDB();
        }
        return productDB;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = request.getParameter("q");
        int limit = 8;
        try {
            String limitParam = request.getParameter("limit");
            if (limitParam != null && !limitParam.isEmpty()) {
                int parsed = Integer.parseInt(limitParam);
                if (parsed >= 1 && parsed <= 20) {
                    limit = parsed;
                }
            }
        } catch (NumberFormatException ignored) {}

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        // Empty or too short query → return empty list
        if (q == null || q.trim().length() < 1) {
            try (PrintWriter out = response.getWriter()) {
                out.write("[]");
            }
            return;
        }

        List<ProductDB.Suggestion> suggestions = db().suggestProducts(q.trim(), limit);

        // Build minimal JSON manually to avoid adding JSON libs
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < suggestions.size(); i++) {
            ProductDB.Suggestion s = suggestions.get(i);
            String nameEscaped = s.getName()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");
            String imageEscaped = (s.getImageUrl() == null ? "" : s.getImageUrl()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\""));
            String productUrl = request.getContextPath() + "/product-detail?id=" + s.getProductId();
            String productUrlEscaped = productUrl.replace("\\", "\\\\").replace("\"", "\\\"");
            String priceFormatted = String.format(java.util.Locale.US, "%.0f", s.getPrice());
            String originalPriceFormatted = String.format(java.util.Locale.US, "%.0f", s.getOriginalPrice());
            boolean hasDiscount = s.isDiscounted();
            json.append("{")
                .append("\"id\":").append(s.getProductId()).append(",")
                .append("\"name\":\"").append(nameEscaped).append("\",")
                .append("\"price\":").append(priceFormatted).append(",")
                .append("\"originalPrice\":").append(originalPriceFormatted).append(",")
                .append("\"hasDiscount\":").append(hasDiscount).append(",")
                .append("\"imageUrl\":\"").append(imageEscaped).append("\",")
                .append("\"url\":\"").append(productUrlEscaped).append("\"")
                .append("}");
            if (i < suggestions.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        try (PrintWriter out = response.getWriter()) {
            out.write(json.toString());
        }
    }
}


