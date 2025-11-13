<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <title>Lịch sử mua hàng</title>
    <style>
        .status-badge { padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 600; }
        .status-PENDING { background: #fff3cd; color: #856404; }
        .status-CONFIRMED { background: #e2f0d9; color: #2e7d32; }
        .status-PROCESSING { background: #d1ecf1; color: #0c5460; }
        .status-SHIPPING { background: #cfe2ff; color: #084298; }
        .status-DELIVERED { background: #d4edda; color: #155724; }
        .status-COMPLETED { background: #d4edda; color: #155724; }
        .status-CANCELLED { background: #f8d7da; color: #721c24; }
        .status-RETURNED { background: #fbe7c6; color: #8a6d3b; }
        .status-FAILED { background: #f8d7da; color: #842029; }
        .pay-PAID { background:#d4edda; color:#155724; }
        .pay-PENDING { background:#fff3cd; color:#856404; }
        .pay-FAILED { background:#f8d7da; color:#842029; }
        .pay-REFUNDED { background:#e2e3e5; color:#41464b; }
        .order-products { display:flex; gap:16px; align-items:flex-start; }
        .order-thumb-list { display:flex; flex-wrap:wrap; gap:8px; max-width:150px; }
        .order-thumb-list img { flex:0 0 auto; }
        .order-thumb { width:50px; height:50px; object-fit:cover; border-radius:8px; }
        .order-items-list { display:flex; flex-direction:column; gap:6px; }
        .order-items-list div { line-height:1.3; }
    </style>
</head>
<body>
    <%@ include file="includes/header.jspf" %>
    <div class="container my-4" style="max-width: 1000px;">
        <h3 class="mb-3" style="color:#f76c85">Lịch sử mua hàng</h3>
        <c:choose>
            <c:when test="${empty orderRows}">
                <div class="alert alert-light border">Bạn chưa có đơn hàng nào.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Sản phẩm</th>
                                <th>Ngày đặt</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái đơn</th>
                                <th>Thanh toán</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${orderRows}">
                                <tr>
                                    <td>#${r.orderId}</td>
                                    <td>
                                        <div class="order-products">
                                            <div class="order-thumb-list">
                                                <c:choose>
                                                    <c:when test="${empty r.images}">
                                                        <c:url var="imgSrc" value="/IMG/placeholder.png"/>
                                                        <img src="${imgSrc}" alt="thumb" class="order-thumb">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach var="img" items="${r.images}">
                                                            <c:set var="imgRel" value="${img}"/>
                                                            <c:if test="${not fn:startsWith(imgRel, '/')}" >
                                                                <c:set var="imgRel" value="/${imgRel}"/>
                                                            </c:if>
                                                            <c:url var="imgSrc" value="${imgRel}"/>
                                                            <img src="${imgSrc}" alt="thumb" class="order-thumb">
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="order-items-list">
                                                <c:forEach var="nq" items="${r.nameQtys}">
                                                    <div>${nq.name} <span style="color:#888;font-size:0.9em">x${nq.quantity}</span></div>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </td>
                                    <td>${r.orderDateStr}</td>
                                    <td><fmt:formatNumber value="${r.totalAmount}" type="number" maxFractionDigits="0"/> đ</td>
                                    <td><span class="status-badge status-${r.orderStatus}">${r.orderStatus}</span></td>
                                    <td><span class="status-badge pay-${r.paymentStatus}">${r.paymentStatus}</span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/order-detail?orderId=${r.orderId}" 
                                           class="btn btn-sm btn-outline-primary">
                                            <i class="fas fa-eye"></i> Chi tiết
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</body>
</html>


