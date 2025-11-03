<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Xác nhận đơn hàng</title>
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>
    <div class="container my-5" style="max-width: 720px;">
        <div class="text-center">
            <h3>Cảm ơn bạn đã đặt hàng!</h3>
            <p>Mã đơn hàng của bạn: <strong>#${param.orderId}</strong></p>
            <a class="btn btn-primary mt-3" style="background-color:#f76c85;border-color:#f76c85;" href="${pageContext.request.contextPath}/View/home.jsp">Về trang chủ</a>
        </div>  
    </div>
</body>
<script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</html>


