package Util;

import java.util.List;

import DAO.NotificationDB;
import DAO.ProductDB;
import DAO.UserDB;
import DAO.WishlistDB;
import Model.Notification;
import Model.Product;
import Model.user;
import jakarta.servlet.ServletContext;

public class WishlistNotificationUtil {
    
    /**
     * Gửi thông báo và email cho các user có sản phẩm trong wishlist khi sản phẩm được giảm giá
     * @param productIds Danh sách product IDs được giảm giá
     * @param discountId ID của discount được áp dụng
     * @param context ServletContext để lấy email config (có thể null nếu không cần gửi email)
     */
    public static void notifyUsersAboutWishlistDiscounts(List<Integer> productIds, int discountId, ServletContext context) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        
        WishlistDB wishlistDB = new WishlistDB();
        NotificationDB notificationDB = new NotificationDB();
        ProductDB productDB = new ProductDB();
        UserDB userDB = new UserDB();
        
        // Lấy danh sách user có sản phẩm trong wishlist
        List<Integer> userIds = wishlistDB.getUserIdsWithProductsInWishlist(productIds);
        
        if (userIds.isEmpty()) {
            System.out.println("[WishlistNotificationUtil] Không có user nào có sản phẩm trong wishlist");
            return;
        }
        
        System.out.println("[WishlistNotificationUtil] Tìm thấy " + userIds.size() + " user có sản phẩm trong wishlist");
        
        // Lấy thông tin discount để hiển thị trong thông báo
        DAO.DiscountDB discountDB = new DAO.DiscountDB();
        Model.Discount discount = discountDB.getById(discountId);
        
        String discountInfo = "";
        if (discount != null) {
            if ("PERCENTAGE".equalsIgnoreCase(discount.getType())) {
                discountInfo = String.format("giảm %.0f%%", discount.getValue());
            } else {
                discountInfo = String.format("giảm %,.0f VND", discount.getValue());
            }
        }
        
        // Gửi thông báo cho từng user
        for (Integer userId : userIds) {
            try {
                // Lấy danh sách sản phẩm trong wishlist của user này
                List<Model.Wishlist> userWishlist = wishlistDB.getWishlistByUserId(userId);
                
                // Tìm các sản phẩm được giảm giá trong wishlist của user
                List<Product> discountedProducts = new java.util.ArrayList<>();
                for (Model.Wishlist item : userWishlist) {
                    if (productIds.contains(item.getProductId())) {
                        Product product = productDB.getProductById(item.getProductId());
                        if (product != null) {
                            discountedProducts.add(product);
                        }
                    }
                }
                
                if (discountedProducts.isEmpty()) {
                    continue;
                }
                
                // Tạo thông báo
                String title = "Sản phẩm trong wishlist đang được giảm giá!";
                String message;
                String linkUrl;
                
                if (discountedProducts.size() == 1) {
                    Product product = discountedProducts.get(0);
                    message = String.format("Sản phẩm '%s' trong wishlist của bạn đang được %s. Hãy nhanh tay mua ngay!", 
                        product.getName(), discountInfo);
                    linkUrl = "/product-detail?id=" + product.getProductId();
                } else {
                    message = String.format("Có %d sản phẩm trong wishlist của bạn đang được %s. Hãy kiểm tra ngay!", 
                        discountedProducts.size(), discountInfo);
                    linkUrl = "/wishlist";
                }
                
                // Tạo thông báo trong hệ thống
                Notification notification = new Notification(
                    userId, 
                    "WISHLIST_DISCOUNT", 
                    title, 
                    message, 
                    linkUrl
                );
                notificationDB.createNotification(notification);
                
                // Gửi email nếu có context và email config
                if (context != null) {
                    sendEmailNotification(userId, discountedProducts, discountInfo, context);
                }
                
            } catch (Exception e) {
                System.err.println("[WishlistNotificationUtil] Lỗi khi gửi thông báo cho user " + userId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Gửi email thông báo cho user về sản phẩm trong wishlist được giảm giá
     */
    private static void sendEmailNotification(int userId, List<Product> discountedProducts, String discountInfo, ServletContext context) {
        try {
            UserDB userDB = new UserDB();
            user user = userDB.getUserById(userId);
            
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                return;
            }
            
            String fromEmail = getFromEmail(context);
            String appPassword = getAppPassword(context);
            
            // Kiểm tra cấu hình email (giống như PasswordResetRequest)
            if (fromEmail == null || fromEmail.equals("yourgmail@gmail.com") || 
                appPassword == null || appPassword.equals("your_16_character_app_password") ||
                appPassword.equals("your_app_password_here") || appPassword.equals("app_password_here")) {
                System.out.println("[WishlistNotificationUtil] Email chưa được cấu hình, bỏ qua gửi email");
                return;
            }
            
            String subject = "Sản phẩm trong wishlist đang được giảm giá!";
            String body = createEmailTemplate(user.getUsername(), discountedProducts, discountInfo);
            
            EmailUtil.send(user.getEmail(), subject, body, fromEmail, appPassword);
            System.out.println("[WishlistNotificationUtil] Đã gửi email thông báo cho user " + userId);
            
        } catch (Exception e) {
            System.err.println("[WishlistNotificationUtil] Lỗi khi gửi email cho user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo template email thông báo giảm giá
     */
    private static String createEmailTemplate(String userName, List<Product> products, String discountInfo) {
        String nameToShow = (userName != null ? userName : "bạn");
        
        StringBuilder productsHtml = new StringBuilder();
        for (Product product : products) {
            productsHtml.append("<div style=\"border: 1px solid #e0e0e0; border-radius: 8px; padding: 15px; margin: 15px 0; background: #fff;\">");
            productsHtml.append("<h3 style=\"margin: 0 0 10px 0; color: #333;\">").append(escapeHtml(product.getName())).append("</h3>");
            productsHtml.append("<p style=\"margin: 5px 0; color: #e91e63; font-size: 18px; font-weight: bold;\">");
            productsHtml.append(String.format("%,.0f VND", product.getDiscountedPrice()));
            if (product.isDiscountActive()) {
                productsHtml.append(" <span style=\"text-decoration: line-through; color: #999; font-size: 14px;\">");
                productsHtml.append(String.format("%,.0f VND", product.getPrice()));
                productsHtml.append("</span>");
            }
            productsHtml.append("</p>");
            productsHtml.append("</div>");
        }
        
        return "<!DOCTYPE html>\n" +
            "<html lang=\"vi\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Sản phẩm trong wishlist đang được giảm giá!</title>\n" +
            "    <style>\n" +
            "        body { margin: 0; padding: 0; font-family: 'Arial', sans-serif; background-color: #f9f9f9; color: #333; }\n" +
            "        .email-container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }\n" +
            "        .header { background: linear-gradient(135deg, #f76c85, #ff8fa3); padding: 30px 20px; text-align: center; }\n" +
            "        .content { padding: 40px 30px; line-height: 1.6; }\n" +
            "        .button { display: inline-block; background: #f76c85; color: white !important; padding: 15px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; margin: 25px 0; text-align: center; }\n" +
            "        .footer { background: #f76c85; color: white; padding: 30px 20px; text-align: center; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"email-container\">\n" +
            "        <div class=\"header\">\n" +
            "            <h1 style=\"color: white; margin: 10px 0 5px 0; font-size: 28px;\">PINKY CLOUD</h1>\n" +
            "            <p style=\"color: white; margin: 0; opacity: 0.9;\">Sản phẩm trong wishlist đang được giảm giá!</p>\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            <h2 style=\"color: #f76c85; margin-top: 0;\">Xin chào " + nameToShow + "!</h2>\n" +
            "            <p>Chúng tôi có tin vui cho bạn! Các sản phẩm trong wishlist của bạn đang được <strong>" + discountInfo + "</strong>.</p>\n" +
            "            <p>Hãy nhanh tay mua ngay trước khi hết hàng!</p>\n" +
            productsHtml.toString() +
            "            <div style=\"text-align: center; margin: 30px 0;\">\n" +
            "                <p style=\"color: #666; margin-bottom: 15px;\">Hãy truy cập website để xem chi tiết và đặt hàng ngay!</p>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"footer\">\n" +
            "            <p style=\"margin: 0;\">© 2024 Pinky Cloud. Tất cả quyền được bảo lưu.</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    private static String getFromEmail(ServletContext context) {
        if (context == null) return null;
        String fromEnv = System.getenv("MAIL_FROM");
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv;
        }
        return context.getInitParameter("MAIL_FROM");
    }
    
    private static String getAppPassword(ServletContext context) {
        if (context == null) return null;
        String passwordEnv = System.getenv("MAIL_APP_PASSWORD");
        if (passwordEnv != null && !passwordEnv.isEmpty()) {
            return passwordEnv;
        }
        return context.getInitParameter("MAIL_APP_PASSWORD");
    }
    
    /**
     * Escape HTML để tránh XSS và lỗi hiển thị
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

