<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
    <title>PinkyCloud - Quên mật khẩu</title>
</head>
<body
    <c:if test="${not empty param.msg}">data-success-msg="${param.msg}"</c:if>
>
    <%@ include file="/View/includes/header.jspf" %>

    <main class="login-section" style="padding-top: 40px;">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-body">
                            <h2 class="text-center mb-4">Quên mật khẩu</h2>
                            <p class="text-center text-muted mb-4">Địa chỉ Gmail đã đăng ký để nhận link khôi phục mật khẩu</p>
                            
                            <form method="post" action="${pageContext.request.contextPath}/password/reset/request">
                                <div class="mb-3">
                                    <label for="email" class="form-label">Địa chỉ email</label>
                                    <c:choose>
    <c:when test="${not empty sessionScope.user and not empty sessionScope.user.email}">
        <input type="email"
               class="form-control"
               name="email"
               id="email"
               value="${sessionScope.user.email}"
               readonly
               required
               pattern="^[A-Za-z0-9._%+-]+@gmail\\.com$"
               placeholder="Nhập Gmail đã đăng ký">
    </c:when>

    <c:otherwise>
        <input type="email"
               class="form-control"
               name="email"
               id="email"
               placeholder="Nhập Gmail đã đăng ký"
               required
               pattern="^[A-Za-z0-9._%+-]+@gmail\\.com$">
    </c:otherwise>
</c:choose>
                                    <div class="form-text">Vui lòng nhập địa chỉ Gmail đã đăng ký (ví dụ: ten@gmail.com)</div>

                                </div>
                                
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Gửi link khôi phục</button>
                                </div>
                            </form>
                            
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">Quay lại đăng nhập</a>
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



