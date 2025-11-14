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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/checkout.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <title>Thanh toán - PinkyCloud</title>
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <main class="container-sm my-4">
        <h2 class="checkout-title"><i class="fas fa-shopping-cart"></i> Thanh toán</h2>
        
        <c:if test="${param.error == 'no_address'}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> Cần thêm địa chỉ giao hàng trước khi đặt hàng.
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/checkout">
            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="card">
                        <h5><i class="fas fa-map-marker-alt"></i> Địa chỉ giao hàng</h5>
                        <c:choose>
                            <c:when test="${empty addresses}">
                                <div class="empty-address">
                                    <p><i class="fas fa-inbox fa-2x mb-2"></i><br>Bạn chưa có địa chỉ. <a href="${pageContext.request.contextPath}/shipping-address?return_to=checkout"><i class="fas fa-plus-circle"></i> Thêm địa chỉ</a></p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="address-select-container">
                                    <select class="form-select" name="shipping_address_id" required>
                                    <c:forEach var="a" items="${addresses}">
                                        <option value="${a.addressId}" ${a['default'] ? 'selected' : ''}>
                                            ${a.fullName} - ${a.phone} | ${a.address}, ${a.ward}, ${a.district}, ${a.city}
                                        </option>
                                    </c:forEach>
                                    </select>
                                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/shipping-address?return_to=checkout">
                                        <i class="fas fa-plus"></i> Thêm mới
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="card">
                        <h5><i class="fas fa-truck"></i> Phương thức giao hàng</h5>
                        <c:forEach var="m" items="${methods}" varStatus="st">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="shipping_method_id" id="m_${m.methodId}" value="${m.methodId}" data-cost="${m.cost}" ${st.first ? 'checked' : ''} required>
                                <label class="form-check-label" for="m_${m.methodId}">
                                    <i class="fas fa-shipping-fast"></i> ${m.name} - <span class="highlight"><fmt:formatNumber value="${m.cost}" type="number" maxFractionDigits="0" /> ₫</span> 
                                    <small class="text-muted">(dự kiến ${m.estimatedDays} ngày)</small>
                                </label>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="card">
                        <h5><i class="fas fa-credit-card"></i> Phương thức thanh toán</h5>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="payment_method" id="cod" value="COD" checked>
                            <label class="form-check-label" for="cod">
                                <i class="fas fa-money-bill-wave"></i> Thanh toán khi nhận hàng (COD)
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="payment_method" id="vnpay" value="BANK">
                            <label class="form-check-label" for="vnpay">
                                <i class="fas fa-university"></i> Thanh toán qua VNPAY
                            </label>
                        </div>
                        <div class="form-text">
                            <i class="fas fa-info-circle"></i> Chọn VNPAY để chuyển đến cổng thanh toán trực tuyến.
                        </div>
                    </div>

                    <div class="card">
                        <h5><i class="fas fa-sticky-note"></i> Ghi chú</h5>
                        <textarea class="form-control" name="notes" rows="3" placeholder="Nhập ghi chú cho đơn hàng của bạn (tuỳ chọn)..."></textarea>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="card order-summary-card">
                        <h5><i class="fas fa-receipt"></i> Đơn hàng của bạn</h5>
                        <div class="list-group mb-3">
                            <c:forEach var="it" items="${items}">
                                <div class="list-group-item d-flex align-items-center justify-content-between">
                                    <div class="d-flex align-items-center">
                                        <img src="${pageContext.request.contextPath}/${it.imageUrl}" alt="${it.productName}">
                                        <div>
                                            <div class="fw-semibold">${it.productName}</div>
                                            <div class="text-muted"><i class="fas fa-box"></i> SL: ${it.quantity}</div>
                                        </div>
                                    </div>
                                    <div class="text-end">
                                        <strong><fmt:formatNumber value="${it.price * it.quantity}" pattern="#,##0" />₫</strong>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <div class="price-line">
                            <span><i class="fas fa-calculator"></i> Tạm tính:</span>
                            <strong><fmt:formatNumber value="${itemsTotal}" pattern="#,##0" />₫</strong>
                        </div>
                        
                        <c:if test="${appliedDiscountAmount > 0}">
                            <div class="price-line text-success">
                                <span><i class="fas fa-tag"></i> Giảm giá:</span>
                                <strong>-<fmt:formatNumber value="${appliedDiscountAmount}" pattern="#,##0" />₫</strong>
                            </div>
                        </c:if>
                        
                        <div class="price-line">
                            <span><i class="fas fa-truck"></i> Phí vận chuyển:</span>
                            <strong id="shippingCost">0₫</strong>
                        </div>
                        
                        <hr>
                        
                        <div class="price-line total-price-line">
                            <span>Tổng cộng:</span>
                            <span id="grandTotal"><fmt:formatNumber value="${finalTotal}" pattern="#,##0" />₫</span>
                        </div>
                        
                        <div>
                            <button type="submit" class="btn btn-primary btn-place-order">
                                <i class="fas fa-check-circle"></i> Đặt hàng ngay
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </main>

    <%@ include file="/View/includes/footer.jspf" %>
    <script>
        (function(){
            var itemsTotal = Number('${itemsTotal}'.replace(/,/g, '')) || 0;
            var appliedDiscountAmount = Number('${appliedDiscountAmount}'.replace(/,/g, '')) || 0;
            var finalTotal = Number('${finalTotal}'.replace(/,/g, '')) || 0;
            
            // Function để format số tiền với dấu chấm phân cách
            function formatCurrency(amount) {
                return Math.round(amount).toLocaleString('vi-VN') + '₫';
            }
            
            function updateTotals(){
                var checked = document.querySelector('input[name="shipping_method_id"]:checked');
                var cost = checked ? Number(checked.getAttribute('data-cost')) : 0;
                if (isNaN(cost)) cost = 0;
                document.getElementById('shippingCost').textContent = formatCurrency(cost);
                
                // Tính tổng cuối cùng = (itemsTotal - discount) + shipping
                var grandTotal = Math.max(0, itemsTotal - appliedDiscountAmount) + cost;
                document.getElementById('grandTotal').textContent = formatCurrency(grandTotal);
            }
            document.addEventListener('change', function(e){
                if (e.target && e.target.name === 'shipping_method_id') updateTotals();
            });
            document.addEventListener('DOMContentLoaded', function(){
                var radios = document.querySelectorAll('input[name="shipping_method_id"]');
                var anyChecked = document.querySelector('input[name="shipping_method_id"]:checked');
                if (!anyChecked && radios.length > 0){
                    radios[0].checked = true;
                }
                updateTotals();
                // No bank list UI; VNPAY used when BANK is selected
            });
        })();
    </script>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
</body>
</html>






