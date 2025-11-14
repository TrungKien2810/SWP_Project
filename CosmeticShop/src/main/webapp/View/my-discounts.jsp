<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Vouchers</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
    
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <div class="container my-4" style="max-width: 1000px; min-height: 60vh;">
        <div class="card shadow-sm">
            <div class="card-header d-flex justify-content-between align-items-center" style="background-color:#fdf1f4; border-color:#fbd0da;">
                <h5 class="mb-0" style="color:#f76c85;">My Vouchers</h5>
                <a class="btn btn-outline-primary" style="border-color:#f76c85; color:#f76c85;" href="${pageContext.request.contextPath}/cart">
                    <i class="fa-solid fa-cart-shopping"></i> Đi tới giỏ hàng
                </a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty assignedDiscounts}">
                        <div class="text-center py-5">
                            <i class="fa-solid fa-ticket-simple fa-2xl mb-3" style="color:#f76c85;"></i>
                            <h6 class="mb-2">Bạn chưa có mã nào</h6>
                            <p class="text-muted mb-0">Đặt hàng và đạt điều kiện để nhận mã ưu đãi dành riêng cho bạn.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Code</th>
                                        <th>Tên</th>
                                        <th>Loại</th>
                                        <th>Giá trị</th>
                                        <th>Điều kiện</th>
                                        <th>Hạn dùng</th>
                                        <th>Còn</th>
                                        <th class="text-end">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="d" items="${assignedDiscounts}">
                                        <tr>
                                            <td><span class="fw-bold">${d.code}</span></td>
                                            <td>${d.name}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${d.type == 'PERCENTAGE'}">
                                                        <span class="badge bg-info text-dark">PERCENTAGE</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">FIXED_AMOUNT</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${d.type == 'PERCENTAGE'}">${d.value}%</c:when>
                                                    <c:otherwise><fmt:formatNumber value="${d.value}" type="number" maxFractionDigits="0" /> ₫</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:if test="${d.minOrderAmount > 0}">ĐH tối thiểu: <fmt:formatNumber value="${d.minOrderAmount}" type="number" maxFractionDigits="0" /> ₫</c:if>
                                                <c:if test="${d.maxDiscountAmount != null}"><br/>Giảm tối đa: <fmt:formatNumber value="${d.maxDiscountAmount}" type="number" maxFractionDigits="0" /> ₫</c:if>
                                            </td>
                                            <td><small>${d.startDate} → ${d.endDate}</small></td>
                                            <td><span class="badge bg-success">${d.remainingUses}</span></td>
                                            <td class="text-end">
                                                <div class="d-inline-flex gap-2">
                                                    <button class="btn btn-sm btn-outline-primary" onclick="copyCode('${d.code}')">Sao chép</button>
                                                    <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/cart" onclick="return applyAndGo('${d.code}')">Áp dụng</a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <%@ include file="/View/includes/footer.jspf" %>

    <script>
        function copyCode(code) {
            navigator.clipboard.writeText(code).then(() => {
                alert('Đã sao chép mã: ' + code);
            });
        }
        function applyAndGo(code) {
            try { localStorage.setItem('promoCodePrefill', code); } catch (e) {}
            return true;
        }
    </script>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
</body>
</html>

