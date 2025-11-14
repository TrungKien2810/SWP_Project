<%@page import="Model.Product"%>
<%@page import="DAO.ProductDB"%>
<%@page import="Model.CartItems"%>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Giỏ hàng của bạn - PinkyCloud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/toast-notification.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/cart.css?v=2.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
          crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body 
    <c:if test="${not empty sessionScope.cartSuccessMsg}">data-success-msg="${sessionScope.cartSuccessMsg}"</c:if>
    <c:if test="${not empty sessionScope.cartErrorMsg}">data-error-msg="${sessionScope.cartErrorMsg}"</c:if>
>
<c:remove var="cartSuccessMsg" scope="session" />
<c:remove var="cartErrorMsg" scope="session" />
<%@ include file="/View/includes/header.jspf" %>
<%@ include file="/View/includes/mobile-search.jspf" %>

<%
    List<CartItems> cartItems = new ArrayList<CartItems>();
    if(session.getAttribute("cartItems") != null){
    cartItems = (List<CartItems>)session.getAttribute("cartItems");
    }
    Double totalPrice = 0.0;
    // Lưu cartId vào session nếu có để apply promo từ server
    if (cartItems != null && !cartItems.isEmpty()) {
        // best-effort: lấy cartId từ phần tử đầu, nếu controller có set khác thì bỏ qua
        session.setAttribute("cartId", cartItems.get(0).getCart_id());
    }
    // Lấy giảm giá đã áp dụng từ server (nếu có)
    Double appliedDiscount = 0.0;
    Object ad = session.getAttribute("appliedDiscountAmount");
    if (ad instanceof Double) {
        appliedDiscount = (Double) ad;
    } else if (ad != null) {
        try { appliedDiscount = Double.parseDouble(ad.toString()); } catch (Exception ignore) {}
    }
%>

<div class="cart-page container mt-5">
    <h2 class="text-center mb-4">GIỎ HÀNG CỦA BẠN</h2>

    <div id="cartContent">
        <% if(cartItems.isEmpty()) { %>
            <div class="text-center my-5">
                <h4>Giỏ hàng của bạn trống</h4>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Thêm sản phẩm</a>
            </div>
        <% } else { %>
            <div class="cart-content row">
                <!-- DANH SÁCH SẢN PHẨM -->
                <div class="cart-items col-md-8">
                    <% for(CartItems p : cartItems) { %>
                    <%
                    
                    totalPrice += p.getPrice() * p.getQuantity();
                    Product product;    
                    ProductDB pd = new ProductDB();
                    product = pd.getProductById(p.getProduct_id());
                    %>
                    <div class="cart-item">
                        <input type="checkbox" class="cart-item-checkbox" checked
                               data-price="<%=p.getPrice()%>" data-quantity="<%=p.getQuantity()%>">
                        <img src="${pageContext.request.contextPath}<%=product.getImageUrl()%>"
                             alt="<%=product.getName()%>">
                        <div class="item-info">
                            <h5><%=product.getName()%></h5>
                            <p class="item-price"><%=String.format("%,.0f", p.getPrice())%>₫ x <span class="item-qty"><%=p.getQuantity()%></span></p>
                            <input type="number" value="<%=p.getQuantity()%>" min="1" max="<%=product.getStock()%>" class="form-control quantity-input" data-product-id="<%=p.getProduct_id()%>" data-stock="<%=product.getStock()%>">
                        </div>
                        <div class="item-total">
                            <p class="item-total-text"><%=String.format("%,.0f", p.getPrice() * p.getQuantity())%>₫</p>
                            <a href="${pageContext.request.contextPath}/removeFromCart?productId=<%=p.getProduct_id()%>">
                                <button class="btn delete-btn"><i class="fas fa-trash-alt"></i> Xóa</button>
                            </a>
                        </div>
                    </div>
                    <% } %>
                </div>

                <!-- CART SUMMARY -->
                <div class="col-md-4 cart-summary">
                    <h4>Tổng cộng</h4>
                    <div class="d-flex justify-content-between mb-2">
                        <span>Tạm tính:</span>
                        <span id="subtotalDisplay"><%=String.format("%,.0f", totalPrice)%>₫</span>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span>Phí vận chuyển:</span>
                        <span>Miễn phí</span>
                    </div>
                    <hr>

                    <div class="mb-3">
                        <form id="promoForm" method="post" action="${pageContext.request.contextPath}/apply-promo">
                            <select class="form-select" id="promoSelect" onchange="onSelectPromo(this)">
                                <option value="">-- Chọn mã của bạn (nếu có) --</option>
                                <c:forEach var="d" items="${requestScope.assignedDiscounts}">
                                    <option value="${d.code}" ${sessionScope.appliedDiscountCode eq d.code ? 'selected' : ''}>${d.code}</option>
                                </c:forEach>
                                <!-- Thêm mã MANUAL đã áp dụng vào dropdown nếu không có trong assignedDiscounts -->
                                <c:if test="${not empty sessionScope.appliedDiscountCode}">
                                    <c:set var="foundInAssigned" value="false" />
                                    <c:forEach var="d" items="${requestScope.assignedDiscounts}">
                                        <c:if test="${sessionScope.appliedDiscountCode eq d.code}">
                                            <c:set var="foundInAssigned" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${!foundInAssigned}">
                                        <option value="${sessionScope.appliedDiscountCode}" selected>${sessionScope.appliedDiscountCode}</option>
                                    </c:if>
                                </c:if>
                                <!-- Thêm mã đã xóa gần đây để có thể áp dụng lại -->
                                <c:if test="${not empty sessionScope.lastRemovedDiscountCode && empty sessionScope.appliedDiscountCode}">
                                    <c:set var="foundInAssigned" value="false" />
                                    <c:forEach var="d" items="${requestScope.assignedDiscounts}">
                                        <c:if test="${sessionScope.lastRemovedDiscountCode eq d.code}">
                                            <c:set var="foundInAssigned" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${!foundInAssigned}">
                                        <option value="${sessionScope.lastRemovedDiscountCode}">${sessionScope.lastRemovedDiscountCode}</option>
                                    </c:if>
                                </c:if>
                            </select>
                            <input type="text" name="promoCode" id="promoCodeInput" class="form-control" placeholder="Nhập mã khuyến mãi" value="${sessionScope.appliedDiscountCode}">
                            <div class="promo-buttons">
                                <button type="submit" class="btn btn-outline-danger">Áp dụng</button>
                                <c:if test="${not empty sessionScope.appliedDiscountCode}">
                                    <button type="button" class="btn btn-outline-secondary" onclick="removeDiscount()">Xóa mã</button>
                                </c:if>
                            </div>
                        </form>
                        <div class="mt-2 d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/my-promos"><i class="fas fa-ticket-alt"></i> My Vouchers</a>
                            <a href="${pageContext.request.contextPath}/products"><i class="fas fa-shopping-bag"></i> Tiếp tục mua sắm</a>
                        </div>
                        <c:if test="${not empty sessionScope.appliedDiscountCode}">
                            <small class="text-success"><i class="fas fa-check-circle"></i> Đã áp dụng: ${sessionScope.appliedDiscountCode} (-<fmt:formatNumber value="${sessionScope.appliedDiscountAmount}" type="number" maxFractionDigits="0" /> ₫)</small>
                        </c:if>
                    </div>

                    <div class="d-flex justify-content-between mb-2">
                        <span>Giảm giá:</span>
                        <span id="discountDisplay" data-discount-amount="<%= appliedDiscount != null ? appliedDiscount : 0.0 %>">
                            <c:choose>
                                <c:when test="${not empty sessionScope.appliedDiscountAmount}">
                                    -<fmt:formatNumber value="${sessionScope.appliedDiscountAmount}" type="number" maxFractionDigits="0" /> ₫
                                </c:when>
                                <c:otherwise>0 ₫</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="fw-bold">Tổng thanh toán:</span>
                        <span id="totalDisplay"><%=
                            String.format("%,.0f", (totalPrice - appliedDiscount) > 0 ? (totalPrice - appliedDiscount) : 0)
                        %>₫</span>
                    </div>

                    <a href="${pageContext.request.contextPath}/checkout" id="checkoutBtn" class="btn btn-danger w-100"><i class="fas fa-credit-card"></i> THANH TOÁN NGAY</a>
                </div>
            </div>
        <% } %>
    </div>
</div>

<script>
    // Khởi tạo giá trị discount từ server khi trang load
    let serverDiscount = 0;
    (function() {
        const discountEl = document.getElementById('discountDisplay');
        if (discountEl) {
            const discountAmount = parseFloat(discountEl.getAttribute('data-discount-amount') || '0');
            serverDiscount = isNaN(discountAmount) ? 0 : discountAmount;
        }
    })();
    
    function updateCartUI() {
        const cartItems = document.querySelectorAll('.cart-item');
        const cartContentDiv = document.getElementById('cartContent');

        if (cartItems.length === 0) {
            cartContentDiv.innerHTML = `
                <div class="text-center my-5">
                    <h4>Giỏ hàng của bạn đang trống</h4>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Thêm sản phẩm mới</a>
                </div>`;
        }
    }

    function getServerDiscount() {
        // Trả về giá trị discount đã được lưu từ server
        return serverDiscount || 0;
    }
    
    // Hàm để cập nhật giá trị discount khi có thay đổi từ server (sau khi áp dụng/xóa mã)
    function updateServerDiscount(newDiscount) {
        serverDiscount = newDiscount || 0;
    }

    function hasSelectedItems() {
        return Array.from(document.querySelectorAll('.cart-item-checkbox')).some(cb => cb.checked);
    }

    function toggleCheckoutButton() {
        const checkoutBtn = document.getElementById('checkoutBtn');
        if (!checkoutBtn) return;
        const hasItem = hasSelectedItems();
        checkoutBtn.classList.toggle('disabled', !hasItem);
        checkoutBtn.setAttribute('aria-disabled', !hasItem);
    }

    function updateTotal() {
        let subtotal = 0;
        document.querySelectorAll('.cart-item').forEach(item => {
            const checkbox = item.querySelector('.cart-item-checkbox');
            const price = parseFloat(checkbox.dataset.price);
            const qty = parseInt(item.querySelector('.quantity-input').value);
            
            // Cập nhật data-quantity khi thay đổi số lượng
            checkbox.dataset.quantity = qty;
            item.querySelector('.item-qty').textContent = qty;
            item.querySelector('.item-total-text').textContent = (price * qty).toLocaleString() + '₫';
            
            // Chỉ tính vào tổng nếu checkbox được tick
            if (checkbox.checked) {
                subtotal += price * qty;
            }
        });
        
        const subtotalDisplay = document.getElementById('subtotalDisplay');
        const totalDisplay = document.getElementById('totalDisplay');
        if (subtotalDisplay) subtotalDisplay.textContent = subtotal.toLocaleString() + '₫';
        const discount = getServerDiscount();
        const total = Math.max(0, subtotal - discount);
        if (totalDisplay) totalDisplay.textContent = total.toLocaleString() + '₫';
        toggleCheckoutButton();
    }

    // Event checkbox & quantity input
    document.querySelectorAll('.quantity-input').forEach(input => {
        var oldValue = parseInt(input.value); // Lưu giá trị cũ
        
        input.addEventListener('input', updateTotal);
        input.addEventListener('focus', function() {
            oldValue = parseInt(this.value); // Cập nhật giá trị cũ khi focus
        });
        input.addEventListener('change', function(){
            var qty = parseInt(this.value);
            if (!qty || qty < 1) { 
                qty = 1; 
                this.value = 1; 
                oldValue = 1;
            }
            var productId = this.getAttribute('data-product-id');
            fetch('${pageContext.request.contextPath}/cart/update-quantity', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'productId=' + encodeURIComponent(productId) + '&quantity=' + encodeURIComponent(qty)
            }).then(r => r.json()).then(function(data){
                if (data.ok === false && data.error) {
                    // Hiển thị thông báo lỗi
                    if (typeof showErrorToast === 'function') {
                        showErrorToast(data.error);
                    } else {
                        alert(data.error);
                    }
                    // Khôi phục giá trị cũ
                    input.value = oldValue;
                    updateTotal();
                } else {
                    // Cập nhật giá trị cũ thành công
                    oldValue = qty;
                    // server refreshed session cart; totals already updated client-side
                }
            }).catch(function(err){ 
                console.error('Error updating cart quantity:', err);
                // Khôi phục giá trị cũ nếu có lỗi network
                input.value = oldValue;
                updateTotal();
            });
        });
    });
    document.querySelectorAll('.cart-item-checkbox').forEach(cb => {
        cb.addEventListener('change', function() {
            updateTotal();
            // Cập nhật trạng thái trong database
            const productId = this.closest('.cart-item').querySelector('.quantity-input').getAttribute('data-product-id');
            fetch('${pageContext.request.contextPath}/cart/update-selection', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'productId=' + encodeURIComponent(productId) + '&isSelected=' + this.checked
            }).then(r => r.json()).then(function(){
                // Status updated in database
            }).catch(function(){ /* ignore */ });
        });
    });

    // Event nút xóa
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.cart-item').remove();
            updateTotal();
            updateCartUI();
        });
    });

    // Khởi tạo tính tổng ban đầu (đảm bảo discount đã được load)
    // Đảm bảo serverDiscount được khởi tạo trước khi tính tổng
    if (typeof serverDiscount === 'undefined') {
        const discountEl = document.getElementById('discountDisplay');
        if (discountEl) {
            const discountAmount = parseFloat(discountEl.getAttribute('data-discount-amount') || '0');
            serverDiscount = isNaN(discountAmount) ? 0 : discountAmount;
        } else {
            serverDiscount = 0;
        }
    }
    updateTotal();
    toggleCheckoutButton();

    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', function(e) {
            if (!hasSelectedItems()) {
                e.preventDefault();
                if (typeof showToast === 'function') {
                    showToast('Vui lòng chọn ít nhất một sản phẩm để thanh toán.', 'error', 3500);
                } else {
                    alert('Vui lòng chọn ít nhất một sản phẩm để thanh toán.');
                }
            }
        });
    }

    // Mã giảm giá: xử lý phía server (không cập nhật client-side tại đây)
    function onSelectPromo(sel){
        var code = sel && sel.value ? sel.value : '';
        var input = document.getElementById('promoCodeInput');
        if(input){ input.value = code; }
        
        // Xóa lastRemovedDiscountCode khi chọn mã mới
        if(code && code.trim() !== '') {
            // Có thể thêm logic để clear lastRemovedDiscountCode nếu cần
        }
    }
    
    // Xóa mã giảm giá
    function removeDiscount(){
        var form = document.getElementById('promoForm');
        var input = document.getElementById('promoCodeInput');
        var select = document.getElementById('promoSelect');
        
        if(input) input.value = '';
        if(select) select.selectedIndex = 0;
        
        // Submit form với mã rỗng để xóa mã đã áp dụng
        if(form) {
            var hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'removeDiscount';
            hiddenInput.value = 'true';
            form.appendChild(hiddenInput);
            form.submit();
        }
    }

    // Prefill và tự áp dụng mã khi điều hướng từ trang "My Vouchers"
    document.addEventListener('DOMContentLoaded', function(){
        try {
            // Đồng bộ dropdown với input khi trang load
            var input = document.getElementById('promoCodeInput');
            var select = document.getElementById('promoSelect');
            if (input && select && input.value) {
                // Tìm option có value trùng với input
                for (var i = 0; i < select.options.length; i++) {
                    if (select.options[i].value === input.value) {
                        select.selectedIndex = i;
                        break;
                    }
                }
            }
            
            var prefill = localStorage.getItem('promoCodePrefill');
            if (prefill) {
                localStorage.removeItem('promoCodePrefill');
                var form = document.getElementById('promoForm');
                if (input) input.value = prefill;
                if (form && input && input.value) {
                    form.submit();
                }
            }
        } catch(e) {}
    });
</script>

<%@ include file="/View/includes/footer.jspf" %>

<script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/Js/home.js"></script>
<script src="${pageContext.request.contextPath}/Js/toast-notification.js"></script>
<script>
    // Hiển thị toast notification từ URL parameter
    window.addEventListener('load', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const msg = urlParams.get('msg');
        const error = urlParams.get('error');
        
        if (msg) {
            try {
                const decodedMsg = decodeURIComponent(msg);
                setTimeout(function() {
                    if (typeof showToast === 'function') {
                        showToast(decodedMsg, 'success', 4000);
                    }
                }, 200);
            } catch (e) {
                console.error('Error decoding message:', e);
            }
        }
        
        if (error) {
            try {
                const decodedError = decodeURIComponent(error);
                setTimeout(function() {
                    if (typeof showToast === 'function') {
                        showToast(decodedError, 'error', 4000);
                    }
                }, 200);
            } catch (e) {
                console.error('Error decoding error:', e);
            }
        }
    });
    
    // Mobile cart summary expand/collapse
    if (window.innerWidth <= 768) {
        document.addEventListener('DOMContentLoaded', function() {
            const cartSummary = document.querySelector('.cart-summary');
            if (cartSummary) {
                // Start collapsed
                cartSummary.classList.add('collapsed');
                
                // Click on summary header or before element to toggle
                const toggleSummary = function(e) {
                    // Don't toggle if clicking on interactive elements
                    if (e.target.tagName === 'BUTTON' || 
                        e.target.tagName === 'INPUT' || 
                        e.target.tagName === 'SELECT' ||
                        e.target.tagName === 'A' ||
                        e.target.closest('button') ||
                        e.target.closest('input') ||
                        e.target.closest('select') ||
                        e.target.closest('a') ||
                        e.target.closest('#checkoutBtn')) {
                        return;
                    }
                    
                    // Toggle collapsed class
                    cartSummary.classList.toggle('collapsed');
                };
                
                // Click on summary to expand/collapse
                cartSummary.addEventListener('click', toggleSummary);
                
                // Click on ::before element (drag handle)
                cartSummary.style.cursor = 'pointer';
            }
        });
    }
</script>
</body>
</html>