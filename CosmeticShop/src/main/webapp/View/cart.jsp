<%@page import="Model.Product"%>
<%@page import="DAO.ProductDB"%>
<%@page import="Model.CartItems"%>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Giỏ hàng của bạn - PinkyCloud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/cart.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
          crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<%@ include file="/View/includes/header.jspf" %>

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
    <!-- Hiển thị thông báo -->
    <c:if test="${not empty requestScope.msg}">
        <div class="alert alert-success alert-dismissible fade show" role="alert" style="margin-bottom: 20px;">
            <i class="fas fa-check-circle me-2"></i>
            ${requestScope.msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    
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
                    Product product;    
                    ProductDB pd = new ProductDB();
                    product = pd.getProductById(p.getProduct_id());
                    
                    // Chỉ tính vào tổng nếu item được chọn
                    if (p.isIs_selected()) {
                        totalPrice += p.getPrice() * p.getQuantity();
                    }
                    %>
                    <div class="cart-item d-flex align-items-center mb-4 p-3 border rounded shadow-sm">
                        <input type="checkbox" class="cart-item-checkbox me-2" <%=p.isIs_selected() ? "checked" : ""%>
                               data-price="<%=p.getPrice()%>" data-quantity="<%=p.getQuantity()%>">
                        <img src="${pageContext.request.contextPath}<%=product.getImageUrl()%>"
                             alt="<%=product.getName()%>" class="me-3" style="width:100px; height:100px; object-fit:cover;">
                        <div class="item-info flex-grow-1">
                            <h5><%=product.getName()%></h5>
                            <p class="item-price"><%=p.getPrice()%>₫ x <span class="item-qty"><%=p.getQuantity()%></span></p>
                            <input type="number" value="<%=p.getQuantity()%>" min="1" class="form-control w-25 text-center quantity-input" data-product-id="<%=p.getProduct_id()%>">
                        </div>
                        <div class="item-total ms-3">
                            <p class="fw-bold item-total-text"><%=String.format("%,.0f", p.getPrice() * p.getQuantity())%>₫</p>
                            <a href="${pageContext.request.contextPath}/removeFromCart?productId=<%=p.getProduct_id()%>">
                            <button class="btn btn-sm btn-outline-danger mt-2 delete-btn">Xóa</button>
                            </a>
                        </div>
                    </div>
                    <% } %>
                </div>

                <!-- CART SUMMARY -->
                <div class="col-md-4 cart-summary bg-light p-4 rounded shadow-sm">
                    <h4 class="fw-bold mb-3">Tổng cộng</h4>
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
                        <form id="promoForm" class="d-flex" method="post" action="${pageContext.request.contextPath}/apply-promo">
                            <select class="form-select me-2" id="promoSelect" onchange="onSelectPromo(this)">
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
                            <input type="text" name="promoCode" id="promoCodeInput" class="form-control me-2" placeholder="Nhập mã khuyến mãi" value="${sessionScope.appliedDiscountCode}">
                            <button type="submit" class="btn btn-outline-danger me-2">Áp dụng</button>
                            <c:if test="${not empty sessionScope.appliedDiscountCode}">
                                <button type="button" class="btn btn-outline-secondary" onclick="removeDiscount()">Xóa mã</button>
                            </c:if>
                        </form>
                        <div class="mt-2 d-flex justify-content-between">
                            <a class="small" href="${pageContext.request.contextPath}/my-promos">My Vouchers</a>
                            <a class="small" href="${pageContext.request.contextPath}/products">Tiếp tục mua sắm</a>
                        </div>
                        <c:if test="${not empty sessionScope.appliedDiscountCode}">
                            <small class="text-success">Đã áp dụng: ${sessionScope.appliedDiscountCode} (-${sessionScope.appliedDiscountAmount})</small>
                        </c:if>
                        <c:if test="${not empty requestScope.msg}">
                            <small class="text-success">${requestScope.msg}</small>
                        </c:if>
                        <c:if test="${not empty requestScope.error}">
                            <small class="text-danger">${requestScope.error}</small>
                        </c:if>
                    </div>

                    <div class="d-flex justify-content-between mb-2">
                        <span>Giảm giá:</span>
                        <span id="discountDisplay">${sessionScope.appliedDiscountAmount != null ? sessionScope.appliedDiscountAmount : 0}</span>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="fw-bold">Tổng thanh toán:</span>
                        <strong style="color:#f76c85;" id="totalDisplay"><%=
                            String.format("%,.0f", (totalPrice - appliedDiscount) > 0 ? (totalPrice - appliedDiscount) : 0)
                        %>₫</strong>
                    </div>

                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-danger w-100 fw-bold">THANH TOÁN NGAY</a>
                </div>
            </div>
        <% } %>
    </div>
</div>

<script>
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
        const el = document.getElementById('discountDisplay');
        if (!el) return 0;
        const raw = (el.textContent || '0').toString();
        const num = Number(raw.replace(/[^0-9.\-]/g, ''));
        return isNaN(num) ? 0 : num;
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
    }

    // Event checkbox & quantity input
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('input', updateTotal);
        input.addEventListener('change', function(){
            var qty = parseInt(this.value);
            if (!qty || qty < 1) { qty = 1; this.value = 1; }
            var productId = this.getAttribute('data-product-id');
            fetch('${pageContext.request.contextPath}/cart/update-quantity', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'productId=' + encodeURIComponent(productId) + '&quantity=' + encodeURIComponent(qty)
            }).then(r => r.json()).then(function(){
                // server refreshed session cart; totals already updated client-side
            }).catch(function(){ /* ignore */ });
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

    // Khởi tạo tính tổng ban đầu
    updateTotal();

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
</body>
</html>