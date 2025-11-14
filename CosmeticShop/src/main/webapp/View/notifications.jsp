<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trung tâm thông báo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .notification-page-wrapper { max-width: 960px; margin: 0 auto; }
        .notification-page-header { display:flex; flex-wrap:wrap; align-items:center; justify-content:space-between; gap:12px; margin-bottom:24px; }
        .notification-page-header h3 { color:#f76c85; margin-bottom:4px; }
        .notification-page-list { display:flex; flex-direction:column; gap:12px; }
        .notification-card { border:1px solid #f0f0f0; border-left:5px solid transparent; border-radius:12px; padding:18px; background:#fff; box-shadow:0 2px 6px rgba(0,0,0,0.05); transition:transform 0.2s ease, border-color 0.2s ease; }
        .notification-card:hover { transform:translateY(-1px); border-color:#f76c85; }
        .notification-card.unread { border-left-color:#f76c85; background:#fff8f9; }
        .notification-card .notification-title { font-size:1.05rem; font-weight:600; margin-bottom:6px; color:#333; }
        .notification-card .notification-meta { display:flex; flex-wrap:wrap; gap:16px; font-size:0.9rem; color:#777; margin-top:12px; }
        .notification-card .notification-actions { margin-top:16px; display:flex; flex-wrap:wrap; gap:8px; }
        .notification-card .notification-message { color:#555; margin:0; white-space:pre-line; }
        .notification-card .tag { display:inline-flex; align-items:center; gap:6px; padding:4px 10px; border-radius:999px; background:#f1f3f5; font-size:0.8rem; font-weight:600; text-transform:uppercase; color:#495057; letter-spacing:0.5px; }
        .notification-badge-icon { color:#f76c85; }
        .notification-link { color:#f76c85 !important; transition:color 0.2s ease; }
        .notification-link:hover { color:#d85a6f !important; text-decoration:underline !important; }
        .notification-empty { display:none; }
        .notification-empty.show { display:block; }
        @media (max-width: 576px) {
            .notification-card { padding:16px; }
            .notification-page-header { flex-direction:column; align-items:flex-start; }
            .notification-card .notification-actions { flex-direction:column; align-items:stretch; }
        }
    </style>
</head>
<body data-context-path="${pageContext.request.contextPath}">
    <%@ include file="includes/header.jspf" %>
    <div class="container my-4 notification-page-wrapper">
        <div class="notification-page-header">
            <div>
                <h3>Trung tâm thông báo</h3>
                <p class="text-muted mb-0">Theo dõi và quản lý tất cả thông báo của bạn trong một nơi.</p>
            </div>
            <div class="d-flex gap-2">
                <button id="notificationPageMarkAllRead" class="btn btn-outline-primary btn-sm">
                    <i class="fa-regular fa-envelope-open"></i> Đánh dấu tất cả đã đọc
                </button>
                <button id="notificationPageRefresh" class="btn btn-outline-secondary btn-sm">
                    <i class="fa-solid fa-rotate-right"></i> Tải lại
                </button>
            </div>
        </div>
        <div id="notificationPageLoader" class="alert alert-light border">Đang tải dữ liệu...</div>
        <div id="notificationPageEmpty" class="alert alert-light border notification-empty">
            <i class="fa-regular fa-bell-slash me-2"></i> Bạn chưa có thông báo nào.
        </div>
        <div id="notificationPageList" class="notification-page-list"></div>
    </div>
    <%@ include file="includes/footer.jspf" %>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/notifications-page.js"></script>
</body>
</html>


