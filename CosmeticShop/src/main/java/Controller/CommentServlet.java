package Controller;

import DAO.CommentDB;
import Model.Comment;
import Model.CommentMedia;
import Model.CommentReply;
import Model.ReplyMedia;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CommentServlet", urlPatterns = {"/addComment", "/addReply", "/deleteComment", "/deleteReply"})
@MultipartConfig
public class CommentServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "C:\\CosmeticShop\\uploads";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Vui lòng đăng nhập để bình luận");
            return;
        }
        
        user currentUser = (user) session.getAttribute("user");
        
        try {
            String action = request.getParameter("action");
            CommentDB commentDB = new CommentDB();

            // Handle reply actions
            if ("addReply".equalsIgnoreCase(action)) {
                handleAddReply(request, response, currentUser, commentDB);
                return;
            }
            
            if ("deleteReply".equalsIgnoreCase(action)) {
                handleDeleteReply(request, response, currentUser, commentDB);
                return;
            }

            // Handle comment delete action
            if ("delete".equalsIgnoreCase(action)) {
                String commentIdStr = request.getParameter("commentId");
                int commentId = Integer.parseInt(commentIdStr);
                boolean deleted = commentDB.deleteCommentByUser(commentId, currentUser.getUser_id());
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(deleted ? "ok" : "forbidden");
                return;
            }

            // Get form parameters
            int productId = Integer.parseInt(request.getParameter("productId"));
            String commentText = request.getParameter("commentText");
            int rating = Integer.parseInt(request.getParameter("rating"));
            
            // Validate rating
            if (rating < 1 || rating > 5) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Đánh giá phải từ 1 đến 5 sao");
                return;
            }
            
            // Check if user has purchased product with COMPLETED status
            boolean hasPurchased = commentDB.hasUserPurchasedProduct(currentUser.getUser_id(), productId);
            if (!hasPurchased) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn cần mua sản phẩm và nhận hàng thành công trước khi có thể đánh giá");
                return;
            }
            
            // Create comment
            Comment comment = new Comment();
            comment.setProductId(productId);
            comment.setUserId(currentUser.getUser_id());
            comment.setCommentText(commentText);
            comment.setRating(rating);
            
            // Add comment to database
            int commentId = commentDB.addComment(comment);
            
            if (commentId <= 0) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể thêm bình luận");
                return;
            }
            
            // Handle media uploads
            try {
                int mediaOrder = 1;
                // Handle multiple files with the same name attribute
                List<Part> fileParts = new ArrayList<>();
                for (Part part : request.getParts()) {
                    if (part.getName() != null && part.getName().equals("mediaFiles")) {
                        if (part.getSize() > 0) {
                            fileParts.add(part);
                        }
                    }
                }
                
                // Process all files
                for (Part filePart : fileParts) {
                    String mediaUrl = handleMediaUpload(filePart);
                    if (mediaUrl != null && !mediaUrl.isEmpty()) {
                        CommentMedia media = new CommentMedia();
                        media.setCommentId(commentId);
                        media.setMediaUrl(mediaUrl);
                        
                        // Determine media type from file extension
                        String contentType = filePart.getContentType();
                        String mediaType = "image"; // default
                        if (contentType != null && contentType.startsWith("video/")) {
                            mediaType = "video";
                        }
                        media.setMediaType(mediaType);
                        media.setMediaOrder(mediaOrder++);
                        
                        commentDB.addCommentMedia(media);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing media files: " + e.getMessage());
                // Continue even if media upload fails
            }
            
            // Redirect back to product detail page
            response.sendRedirect(request.getContextPath() + "/product-detail?id=" + productId);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra");
        }
    }
    
    private String handleMediaUpload(Part mediaPart) throws IOException {
        String submittedFileName = Paths.get(mediaPart.getSubmittedFileName()).getFileName().toString();
        String ext = "";
        int dot = submittedFileName.lastIndexOf('.');
        if (dot >= 0) {
            ext = submittedFileName.substring(dot);
        }
        String newFileName = "comment_media_" + UUID.randomUUID().toString() + ext;
        
        // Create upload directory if not exists
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Save file
        File dest = new File(dir, newFileName);
        try {
            mediaPart.write(dest.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving media file: " + e.getMessage());
            return null;
        }
        
        // Return relative URL
        String contextRelativeUrl = "/uploads/" + newFileName;
        return contextRelativeUrl.replace("\\", "/");
    }
    
    private void handleAddReply(HttpServletRequest request, HttpServletResponse response, 
                                user currentUser, CommentDB commentDB) 
            throws IOException {
        try {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            String replyText = request.getParameter("replyText");
            
            if (replyText == null || replyText.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nội dung trả lời không được để trống");
                return;
            }
            
            CommentReply reply = new CommentReply();
            reply.setCommentId(commentId);
            reply.setUserId(currentUser.getUser_id());
            reply.setReplyText(replyText.trim());
            
            int replyId = commentDB.addReply(reply);
            if (replyId <= 0) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể thêm trả lời");
                return;
            }
            
            // Handle media uploads
            try {
                int mediaOrder = 1;
                List<Part> fileParts = new ArrayList<>();
                for (Part part : request.getParts()) {
                    if (part.getName() != null && part.getName().equals("mediaFiles")) {
                        if (part.getSize() > 0) {
                            fileParts.add(part);
                        }
                    }
                }
                
                // Process all files
                for (Part filePart : fileParts) {
                    String mediaUrl = handleMediaUpload(filePart);
                    if (mediaUrl != null && !mediaUrl.isEmpty()) {
                        ReplyMedia media = new ReplyMedia();
                        media.setReplyId(replyId);
                        media.setMediaUrl(mediaUrl);
                        
                        // Determine media type from file extension
                        String contentType = filePart.getContentType();
                        String mediaType = "image"; // default
                        if (contentType != null && contentType.startsWith("video/")) {
                            mediaType = "video";
                        }
                        media.setMediaType(mediaType);
                        media.setMediaOrder(mediaOrder++);
                        
                        commentDB.addReplyMedia(media);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing reply media files: " + e.getMessage());
                // Continue even if media upload fails
            }
            
            // Get productId from comment to redirect properly
            // Note: This requires a method to get productId from commentId
            // For now, redirecting back to the page will work
            response.sendRedirect(request.getHeader("Referer"));
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra");
        }
    }
    
    private void handleDeleteReply(HttpServletRequest request, HttpServletResponse response,
                                   user currentUser, CommentDB commentDB)
            throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            int replyId = Integer.parseInt(request.getParameter("replyId"));
            boolean deleted = commentDB.deleteReplyByUser(replyId, currentUser.getUser_id());
            out.print(deleted ? "ok" : "forbidden");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("error");
        }
    }
}

