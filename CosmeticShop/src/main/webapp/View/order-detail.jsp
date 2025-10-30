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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Chi tiết đơn hàng</title>
    <style>
        .order-detail-container {
            max-width: 1000px;
            margin: 20px auto;
            padding: 20px;
        }
        .detail-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .detail-card h6 {
            color: #f76c85;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .back-btn {
            margin-bottom: 20px;
        }
        .badge {
            padding: 6px 12px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
        }
        .badge-primary { background: #0d6efd; color: white; }
        .badge-success { background: #198754; color: white; }
        .badge-warning { background: #ffc107; color: #212529; }
    </style>
</head>
<body>
    <%@ include file="includes/header.jspf" %>

    <div class="order-detail-container">
        <a href="${pageContext.request.contextPath}/my-orders" class="btn btn-outline-secondary back-btn">
            <i class="fas fa-arrow-left"></i> Quay lại
        </a>

        <c:choose>
            <c:when test="${empty order}">
                <div class="alert alert-danger">Không tìm thấy đơn hàng</div>
            </c:when>
            <c:otherwise>

                <div class="row">
                    <div class="col-md-6">
                        <div class="detail-card">
                            <h6><i class="fas fa-shopping-cart"></i> Thông tin đơn hàng</h6>
                            <p><strong>Mã đơn hàng:</strong> #${order.orderId}</p>
                            <p><strong>Ngày đặt:</strong> 
                                <c:choose>
                                    <c:when test="${not empty order.orderDate}">
                                        ${order.orderDate}
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong>Phương thức thanh toán:</strong> 
                                <c:choose>
                                    <c:when test="${order.paymentMethod == 'COD'}">Tiền mặt</c:when>
                                    <c:when test="${order.paymentMethod == 'BANK'}">Chuyển khoản</c:when>
                                    <c:otherwise>${order.paymentMethod}</c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong>Trạng thái đơn hàng:</strong> 
                                <span class="badge badge-primary">${order.orderStatus}</span>
                            </p>
                            <p><strong>Trạng thái thanh toán:</strong> 
                                <c:choose>
                                    <c:when test="${order.paymentStatus == 'PAID'}">
                                        <span class="badge badge-success">Đã thanh toán</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-warning">Chưa thanh toán</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <c:if test="${not empty order.trackingNumber}">
                                <p><strong>Số tracking:</strong> ${order.trackingNumber}</p>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="col-md-6">
                        <c:if test="${not empty shippingAddress}">
                            <div class="detail-card">
                                <h6><i class="fas fa-map-marker-alt"></i> Địa chỉ giao hàng</h6>
                                <p><strong>Người nhận:</strong> ${shippingAddress.fullName}</p>
                                <p><strong>Điện thoại:</strong> ${shippingAddress.phone}</p>
                                <p><strong>Địa chỉ:</strong> ${shippingAddress.address}</p>
                                <p><strong>Phường/Xã:</strong> ${shippingAddress.ward}</p>
                                <p><strong>Quận/Huyện:</strong> ${shippingAddress.district}</p>
                                <p><strong>Tỉnh/Thành phố:</strong> ${shippingAddress.city}</p>
                            </div>
                        </c:if>
                    </div>
                </div>

                <c:if test="${not empty shippingMethod}">
                    <div class="detail-card">
                        <h6><i class="fas fa-truck"></i> Phương thức vận chuyển</h6>
                        <p><strong>Tên:</strong> ${shippingMethod.name}</p>
                        <p><strong>Phí vận chuyển:</strong> 
                            <fmt:formatNumber value="${shippingMethod.cost}" type="number" maxFractionDigits="0"/> đ
                        </p>
                        <c:if test="${not empty shippingMethod.estimatedDays}">
                            <p><strong>Thời gian giao hàng:</strong> ${shippingMethod.estimatedDays} ngày</p>
                        </c:if>
                    </div>
                </c:if>

                <div class="detail-card">
                    <h6><i class="fas fa-box"></i> Sản phẩm</h6>
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>Hình ảnh</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Số lượng</th>
                                    <th>Giá</th>
                                    <th>Tổng</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${orderItems}">
                                    <tr>
                                        <td>
                                            <c:set var="imgUrl" value="${item.productImageUrl}"/>
                                            <c:if test="${not fn:startsWith(imgUrl, '/')}">
                                                <c:set var="imgUrl" value="/${imgUrl}"/>
                                            </c:if>
                                            <img src="${pageContext.request.contextPath}${imgUrl}" 
                                                 alt="${item.productName}" 
                                                 style="width:50px; height:50px; object-fit:cover;" class="img-thumbnail">
                                        </td>
                                        <td>${item.productName}</td>
                                        <td>${item.quantity}</td>
                                        <td><fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0"/> đ</td>
                                        <td><fmt:formatNumber value="${item.quantity * item.price}" type="number" maxFractionDigits="0"/> đ</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="4" class="text-end"><strong>Tổng tiền sản phẩm:</strong></td>
                                    <td><strong><fmt:formatNumber value="${order.totalAmount - order.shippingCost}" type="number" maxFractionDigits="0"/> đ</strong></td>
                                </tr>
                                <c:if test="${not empty shippingMethod}">
                                    <tr>
                                        <td colspan="4" class="text-end"><strong>Phí vận chuyển:</strong></td>
                                        <td><strong><fmt:formatNumber value="${order.shippingCost}" type="number" maxFractionDigits="0"/> đ</strong></td>
                                    </tr>
                                </c:if>
                                <tr style="background-color:#fdf1f4;">
                                    <td colspan="4" class="text-end"><strong>TỔNG CỘNG:</strong></td>
                                    <td><strong style="color:#f76c85; font-size:1.2em;">
                                        <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/> đ
                                    </strong></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>

            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="includes/footer.jspf" %>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</body>
</html>

