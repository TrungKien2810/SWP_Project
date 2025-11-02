package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.CommentDB;
import DAO.ProductDB;
import Model.Product;
import Model.Comment;
import Model.user;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "productdetail", urlPatterns = {"/product-detail"})
public class productdetail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id_raw = request.getParameter("id");
        try {
            int id = Integer.parseInt(id_raw);

            ProductDB dao = new ProductDB();
            Product product = dao.getProductById(id);

            if (product != null) {
                // Lấy tên danh mục theo category_id
                String categoryName = dao.getCategoryNameById(product.getCategoryId());
                
                // Lấy bình luận và thống kê
                CommentDB commentDB = new CommentDB();
                List<Comment> comments;
                double avgRating = commentDB.getAverageRating(id);
                int commentCount = commentDB.getCommentCount(id);
                
                // Get rating filter parameter
                String ratingFilterStr = request.getParameter("rating");
                Integer ratingFilter = null;
                try {
                    if (ratingFilterStr != null && !ratingFilterStr.isEmpty()) {
                        int rating = Integer.parseInt(ratingFilterStr);
                        if (rating >= 1 && rating <= 5) {
                            ratingFilter = rating;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Invalid rating parameter, ignore
                }
                
                // Kiểm tra xem user đã mua sản phẩm chưa (và order có status COMPLETED)
                boolean canComment = false;
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("user") != null) {
                    user currentUser = (user) session.getAttribute("user");
                    canComment = commentDB.hasUserPurchasedProduct(currentUser.getUser_id(), id);
                    // Ưu tiên hiển thị bình luận của user lên đầu và lọc theo rating nếu có
                    if (ratingFilter != null) {
                        comments = commentDB.getCommentsByProductIdAndRatingPrioritizeUser(id, ratingFilter, currentUser.getUser_id());
                    } else {
                        comments = commentDB.getCommentsByProductIdPrioritizeUser(id, currentUser.getUser_id());
                    }
                } else {
                    if (ratingFilter != null) {
                        comments = commentDB.getCommentsByProductIdAndRating(id, ratingFilter);
                    } else {
                        comments = commentDB.getCommentsByProductId(id);
                    }
                }
                
                // Get rating distribution
                int rating5Count = commentDB.getRatingCount(id, 5);
                int rating4Count = commentDB.getRatingCount(id, 4);
                int rating3Count = commentDB.getRatingCount(id, 3);
                int rating2Count = commentDB.getRatingCount(id, 2);
                int rating1Count = commentDB.getRatingCount(id, 1);
                
                request.setAttribute("product", product);
                request.setAttribute("categoryName", categoryName);
                request.setAttribute("comments", comments);
                request.setAttribute("avgRating", avgRating);
                request.setAttribute("commentCount", commentCount);
                request.setAttribute("canComment", canComment);
                request.setAttribute("ratingFilter", ratingFilter);
                request.setAttribute("rating5Count", rating5Count);
                request.setAttribute("rating4Count", rating4Count);
                request.setAttribute("rating3Count", rating3Count);
                request.setAttribute("rating2Count", rating2Count);
                request.setAttribute("rating1Count", rating1Count);
                request.getRequestDispatcher("/View/product-detail.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sản phẩm");// lỗi product=null
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ");// bắt lỗi id k là số
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra");//Bắt các lỗi kết nối DB, lỗi query.
        }
    }
}
