package Controller;

import DAO.ProductDB;
import Model.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "BestSellingProducts", urlPatterns = {"/best-selling-products"})
public class BestSellingProducts extends HttpServlet {
    
    private ProductDB productDB;

    @Override
    public void init() {
        productDB = new ProductDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy tham số phân trang
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        
        int page = 1;
        int pageSize = 20;
        
        if (pageStr != null && !pageStr.isEmpty()) {
            try { 
                page = Integer.parseInt(pageStr); 
                if (page < 1) page = 1; 
            } catch (NumberFormatException ignored) {}
        }
        
        if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeStr);
                if (pageSize < 1) pageSize = 20;
                if (pageSize > 100) pageSize = 100;
            } catch (NumberFormatException ignored) {}
        }
        
        // Lấy tất cả sản phẩm bán chạy nhất
        List<Product> allBestSellingProducts = productDB.getBestSellingProducts(1000);
        int totalProducts = allBestSellingProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        
        // Phân trang
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalProducts);
        List<Product> bestSellingProducts = allBestSellingProducts.subList(startIndex, endIndex);
        
        // Lấy danh sách product IDs trong wishlist của user (nếu đã đăng nhập)
        java.util.Set<Integer> wishlistProductIds = new java.util.HashSet<>();
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            Model.user user = (Model.user) session.getAttribute("user");
            DAO.WishlistDB wishlistDB = new DAO.WishlistDB();
            java.util.List<Model.Wishlist> wishlistItems = wishlistDB.getWishlistByUserId(user.getUser_id());
            for (Model.Wishlist item : wishlistItems) {
                wishlistProductIds.add(item.getProductId());
            }
        }
        
        request.setAttribute("bestSellingProducts", bestSellingProducts);
        request.setAttribute("wishlistProductIds", wishlistProductIds);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalPages", totalPages);
        
        request.getRequestDispatcher("/View/best-selling-products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

