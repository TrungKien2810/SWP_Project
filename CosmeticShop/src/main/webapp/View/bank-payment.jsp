<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <title>Thanh toán ngân hàng</title>
</head>
<body>
    <div class="container my-5" style="max-width:720px;">
        <h3 class="mb-3">Mô phỏng thanh toán ngân hàng</h3>
        <p>Đơn hàng: <strong>#${param.orderId}</strong></p>
        <p>Số tiền: <strong>${param.amount}</strong> VND</p>
        <div class="alert alert-info">Đây là trang mô phỏng. Nhấn nút bên dưới để hoàn tất thanh toán thành công.</div>
        <a class="btn btn-success" href="${pageContext.request.contextPath}/payment-callback?status=success&orderId=${param.orderId}">Thanh toán thành công</a>
        <a class="btn btn-outline-danger ms-2" href="${pageContext.request.contextPath}/payment-callback?status=failed&orderId=${param.orderId}">Hủy thanh toán</a>
    </div>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</body>
</html>


