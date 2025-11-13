package Controller;

import DAO.NotificationDB;
import Model.Notification;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "NotificationController", urlPatterns = {"/notifications"})
public class NotificationController extends HttpServlet {

    private NotificationDB notificationDB = new NotificationDB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Vui lòng đăng nhập");
            return;
        }

        user currentUser = (user) session.getAttribute("user");
        String action = request.getParameter("action");

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();

        try {
            boolean isAdmin = "ADMIN".equalsIgnoreCase(String.valueOf(currentUser.getRole()));
            if ("count".equals(action)) {
                // Lấy số lượng thông báo chưa đọc
                // Không tính thông báo global cho admin vào badge để tránh can nhiễu
                int unreadCount = notificationDB.getUnreadCount(currentUser.getUser_id(), false);
                out.print("{\"count\":" + unreadCount + "}");
            } else if ("list".equals(action)) {
                // Lấy danh sách thông báo
                List<Notification> notifications = notificationDB.getNotificationsByUserId(currentUser.getUser_id(), isAdmin);
                out.print(notificationsToJson(notifications, true));
            } else if ("unread".equals(action)) {
                // Lấy danh sách thông báo chưa đọc
                // Không trả về thông báo global trong danh sách chưa đọc để tránh hiển thị sai badge
                List<Notification> notifications = notificationDB.getUnreadNotificationsByUserId(currentUser.getUser_id(), false);
                out.print(notificationsToJson(notifications, false));
            } else {
                // Mặc định trả về danh sách
                List<Notification> notifications = notificationDB.getNotificationsByUserId(currentUser.getUser_id(), isAdmin);
                out.print(notificationsToJson(notifications, true));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Vui lòng đăng nhập");
            return;
        }

        user currentUser = (user) session.getAttribute("user");
        String action = request.getParameter("action");

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();

        try {
            boolean isAdmin = "ADMIN".equalsIgnoreCase(String.valueOf(currentUser.getRole()));
            if ("markRead".equals(action)) {
                // Đánh dấu một thông báo là đã đọc
                String notificationIdStr = request.getParameter("notificationId");
                if (notificationIdStr != null) {
                    int notificationId = Integer.parseInt(notificationIdStr);
                    // Chỉ cho phép đánh dấu đã đọc nếu là thông báo thuộc về user
                    boolean success = notificationDB.markAsReadForUser(notificationId, currentUser.getUser_id());
                    int unreadCount = notificationDB.getUnreadCount(currentUser.getUser_id(), false);
                    if (!success && unreadCount == 0) {
                        success = true;
                    }
                    JSONObject json = new JSONObject();
                    json.put("success", success);
                    json.put("notificationId", notificationId);
                    json.put("unreadCount", unreadCount);
                    out.print(json.toString());
                } else {
                    out.print("{\"success\":false,\"error\":\"Missing notificationId\"}");
                }
            } else if ("delete".equals(action)) {
                String notificationIdStr = request.getParameter("notificationId");
                if (notificationIdStr != null) {
                    int notificationId = Integer.parseInt(notificationIdStr);
                    boolean allowAdminGlobal = "ADMIN".equalsIgnoreCase(currentUser.getRole());
                    boolean success = notificationDB.deleteNotificationForUser(notificationId, currentUser.getUser_id(), allowAdminGlobal);
                    int unreadCount = notificationDB.getUnreadCount(currentUser.getUser_id(), false);

                    JSONObject json = new JSONObject();
                    json.put("success", success);
                    json.put("notificationId", notificationId);
                    json.put("unreadCount", unreadCount);
                    out.print(json.toString());
                } else {
                    out.print("{\"success\":false,\"error\":\"Missing notificationId\"}");
                }
            } else if ("markAllRead".equals(action)) {
                // Đánh dấu tất cả thông báo là đã đọc
                // Không đánh dấu read cho thông báo global (admin) để tránh ảnh hưởng tới người khác
                int userId = currentUser.getUser_id();
                System.out.println("[DEBUG] markAllRead action - userId: " + userId);
                
                boolean success = notificationDB.markAllAsRead(userId, false);
                int unreadCount = notificationDB.getUnreadCount(userId, false);
                
                // Nếu không có rows updated nhưng unreadCount = 0, coi như thành công
                if (!success && unreadCount == 0) {
                    success = true;
                    System.out.println("[DEBUG] markAllRead - treated as success (no unread left)");
                }
                
                System.out.println("[DEBUG] markAllRead result - success: " + success + ", unreadCount: " + unreadCount);
                
                // Lấy danh sách thông báo sau khi update để trả về
                List<Notification> notifications = notificationDB.getNotificationsByUserId(userId, false);
                
                JSONObject json = new JSONObject();
                json.put("success", success);
                json.put("unreadCount", unreadCount);
                json.put("notificationCount", notifications.size());
                // Parse the notifications JSON string to JSONArray
                String notificationsJson = notificationsToJson(notifications, true);
                json.put("notifications", new JSONArray(notificationsJson));
                out.print(json.toString());
            } else {
                out.print("{\"success\":false,\"error\":\"Invalid action\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }
    
    // Helper method to convert notifications to JSON
    private String notificationsToJson(List<Notification> notifications, boolean forceGlobalAsRead) {
        JSONArray jsonArray = new JSONArray();
        for (Notification notif : notifications) {
            JSONObject json = new JSONObject();
            json.put("notificationId", notif.getNotificationId());
            if (notif.getUserId() != null) {
                json.put("userId", notif.getUserId());
            } else {
                json.put("userId", JSONObject.NULL);
            }
            json.put("notificationType", notif.getNotificationType());
            json.put("title", notif.getTitle());
            json.put("message", notif.getMessage());
            // Với thông báo global (userId == null), luôn hiển thị như đã đọc để không ảnh hưởng UI/badge
            if (forceGlobalAsRead && notif.getUserId() == null) {
                json.put("read", true);
            } else {
                json.put("read", notif.isRead());
            }
            if (notif.getCreatedAt() != null) {
                json.put("createdAt", notif.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                json.put("createdAt", JSONObject.NULL);
            }
            if (notif.getLinkUrl() != null) {
                json.put("linkUrl", notif.getLinkUrl());
            } else {
                json.put("linkUrl", JSONObject.NULL);
            }
            jsonArray.put(json);
        }
        return jsonArray.toString();
    }
}

