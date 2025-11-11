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
        PrintWriter out = response.getWriter();

        try {
            if ("count".equals(action)) {
                // Lấy số lượng thông báo chưa đọc
                int unreadCount = notificationDB.getUnreadCount(currentUser.getUser_id());
                out.print("{\"count\":" + unreadCount + "}");
            } else if ("list".equals(action)) {
                // Lấy danh sách thông báo
                List<Notification> notifications = notificationDB.getNotificationsByUserId(currentUser.getUser_id());
                out.print(notificationsToJson(notifications));
            } else if ("unread".equals(action)) {
                // Lấy danh sách thông báo chưa đọc
                List<Notification> notifications = notificationDB.getUnreadNotificationsByUserId(currentUser.getUser_id());
                out.print(notificationsToJson(notifications));
            } else {
                // Mặc định trả về danh sách
                List<Notification> notifications = notificationDB.getNotificationsByUserId(currentUser.getUser_id());
                out.print(notificationsToJson(notifications));
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
        PrintWriter out = response.getWriter();

        try {
            if ("markRead".equals(action)) {
                // Đánh dấu một thông báo là đã đọc
                String notificationIdStr = request.getParameter("notificationId");
                if (notificationIdStr != null) {
                    int notificationId = Integer.parseInt(notificationIdStr);
                    boolean success = notificationDB.markAsRead(notificationId);
                    out.print("{\"success\":" + success + "}");
                } else {
                    out.print("{\"success\":false,\"error\":\"Missing notificationId\"}");
                }
            } else if ("markAllRead".equals(action)) {
                // Đánh dấu tất cả thông báo là đã đọc
                boolean success = notificationDB.markAllAsRead(currentUser.getUser_id());
                out.print("{\"success\":" + success + "}");
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
    private String notificationsToJson(List<Notification> notifications) {
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
            json.put("read", notif.isRead());
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

