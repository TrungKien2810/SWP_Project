<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <title>Xác nhận đơn hàng</title>
</head>
<body>
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


