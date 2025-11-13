<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/auth.css">
    <title>PinkyCloud - Đổi mật khẩu</title>
</head>
<body
    <c:if test="${not empty requestScope.success}">data-success-msg="${requestScope.success}"</c:if>
    <c:if test="${not empty requestScope.error}">data-error-msg="${requestScope.error}"</c:if>
>
    <%@ include file="/View/includes/header.jspf" %>

    <main class="login-section" style="padding-top: 40px;">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-body">
                            <h2 class="text-center mb-4">Đổi mật khẩu</h2>
                            <p class="text-center text-muted mb-4">Chỉ hỗ trợ đổi mật khẩu cho tài khoản Gmail (@gmail.com)</p>
                            
                            <form method="post" action="${pageContext.request.contextPath}/change-password">
                                <div class="mb-3">
                                    <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
                                    <input type="password" class="form-control" name="currentPassword" id="currentPassword" placeholder="Nhập mật khẩu hiện tại" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                                    <input type="password" class="form-control" name="newPassword" id="newPassword" placeholder="Nhập mật khẩu mới" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                                    <input type="password" class="form-control" name="confirmPassword" id="confirmPassword" placeholder="Nhập lại mật khẩu mới" required>
                                </div>
                                
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                                </div>
                            </form>
                            
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/account-management" class="text-decoration-none">Quay lại quản lý tài khoản</a>
                            </div>
                            
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/View/includes/footer.jspf" %>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
</body>
</html>

