<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${empty order}">
        <div class="alert alert-danger">Không tìm thấy đơn hàng</div>
    </c:when>
    <c:otherwise>

<div class="row">
    <div class="col-md-6">
        <h6><i class="fa fa-user"></i> Thông tin khách hàng</h6>
        <div class="card mb-3">
            <div class="card-body">
                <p><strong>Tên:</strong> ${customer.username}</p>
                <p><strong>Email:</strong> ${customer.email}</p>
                <c:if test="${not empty customer.phone}">
                    <p><strong>Điện thoại:</strong> ${customer.phone}</p>
                </c:if>
            </div>
        </div>
    </div>
    
    <div class="col-md-6">
        <h6><i class="fa fa-shopping-cart"></i> Thông tin đơn hàng</h6>
        <div class="card mb-3">
            <div class="card-body">
                <p><strong>Mã đơn hàng:</strong> #${order.orderId}</p>
                <p><strong>Ngày đặt:</strong> ${order.orderDate}</p>
                <p><strong>Thanh toán:</strong> 
                    <c:choose>
                        <c:when test="${order.paymentMethod == 'COD'}">Tiền mặt</c:when>
                        <c:when test="${order.paymentMethod == 'BANK'}">Chuyển khoản</c:when>
                        <c:otherwise>${order.paymentMethod}</c:otherwise>
                    </c:choose>
                </p>
                <p><strong>Trạng thái:</strong> 
                    <span class="badge bg-primary">${order.orderStatus}</span>
                </p>
                <p><strong>Thanh toán:</strong> 
                    <c:choose>
                        <c:when test="${order.paymentStatus == 'PAID'}">
                            <span class="badge bg-success">Đã thanh toán</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-warning">Chưa thanh toán</span>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>
    </div>
</div>

<c:if test="${not empty shippingAddress}">
    <h6><i class="fa fa-map-marker-alt"></i> Địa chỉ giao hàng</h6>
    <div class="card mb-3">
        <div class="card-body">
            <p><strong>Người nhận:</strong> ${shippingAddress.fullName}</p>
            <p><strong>Điện thoại:</strong> ${shippingAddress.phone}</p>
            <p><strong>Địa chỉ:</strong> ${shippingAddress.address}</p>
            <p><strong>Phường/Xã:</strong> ${shippingAddress.ward}</p>
            <p><strong>Quận/Huyện:</strong> ${shippingAddress.district}</p>
            <p><strong>Tỉnh/Thành phố:</strong> ${shippingAddress.city}</p>
        </div>
    </div>
</c:if>

<c:if test="${not empty shippingMethod}">
    <h6><i class="fa fa-truck"></i> Phương thức vận chuyển</h6>
    <div class="card mb-3">
        <div class="card-body">
            <p><strong>Tên:</strong> ${shippingMethod.name}</p>
            <p><strong>Phí vận chuyển:</strong> ${String.format("%,.0f", shippingMethod.cost)} ₫</p>
            <c:if test="${not empty shippingMethod.estimatedDays}">
                <p><strong>Thời gian giao hàng:</strong> ${shippingMethod.estimatedDays} ngày</p>
            </c:if>
        </div>
    </div>
</c:if>

<h6><i class="fa fa-box"></i> Sản phẩm</h6>
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
                        <img src="${pageContext.request.contextPath}${item.productImageUrl}" 
                             alt="${item.productName}" 
                             style="width:50px; height:50px; object-fit:cover;" class="img-thumbnail">
                    </td>
                    <td>${item.productName}</td>
                    <td>${item.quantity}</td>
                    <td>${String.format("%,.0f", item.price)} ₫</td>
                    <td>${String.format("%,.0f", item.quantity * item.price)} ₫</td>
                </tr>
            </c:forEach>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="4" class="text-end"><strong>Tổng tiền sản phẩm:</strong></td>
                <td><strong>${String.format("%,.0f", order.totalAmount - order.shippingCost + order.discountAmount)} ₫</strong></td>
            </tr>
            <c:if test="${not empty order.discountCode && order.discountAmount > 0}">
                <tr>
                    <td colspan="4" class="text-end"><strong>Giảm giá (${order.discountCode}):</strong></td>
                    <td><strong style="color:#198754;">-${String.format("%,.0f", order.discountAmount)} ₫</strong></td>
                </tr>
            </c:if>
            <c:if test="${not empty shippingMethod}">
                <tr>
                    <td colspan="4" class="text-end"><strong>Phí vận chuyển:</strong></td>
                    <td><strong>${String.format("%,.0f", order.shippingCost)} ₫</strong></td>
                </tr>
            </c:if>
            <tr style="background-color:#fdf1f4;">
                <td colspan="4" class="text-end"><strong>TỔNG CỘNG:</strong></td>
                <td><strong style="color:#f76c85; font-size:1.2em;">${String.format("%,.0f", order.totalAmount)} ₫</strong></td>
            </tr>
        </tfoot>
    </table>
</div>

    </c:otherwise>
</c:choose>
