<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Thanh toán ngân hàng</title>
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>
    <div class="container my-5" style="max-width:720px;">
        <h3 class="mb-3">Mô phỏng thanh toán ngân hàng</h3>
        <p>Đơn hàng: <strong>#${param.orderId}</strong></p>
        <p>Số tiền: <strong>
            <c:set var="amountValue" value="${param.amount}" />
            <c:choose>
                <c:when test="${not empty amountValue}">
                    <fmt:formatNumber value="${amountValue}" type="number" maxFractionDigits="0" /> ₫
                </c:when>
                <c:otherwise>0 ₫</c:otherwise>
            </c:choose>
        </strong></p>
        <div class="alert alert-info">Đây là trang mô phỏng. Nhấn nút bên dưới để hoàn tất thanh toán thành công.</div>
        <a class="btn btn-success" href="${pageContext.request.contextPath}/payment-callback?status=success&orderId=${param.orderId}">Thanh toán thành công</a>
        <a class="btn btn-outline-danger ms-2" href="${pageContext.request.contextPath}/payment-callback?status=failed&orderId=${param.orderId}">Hủy thanh toán</a>
    </div>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</body>
</html>


