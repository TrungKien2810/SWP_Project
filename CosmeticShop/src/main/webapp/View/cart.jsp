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
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
</head>
<body>
<!-- ===== HEADER ===== -->
<div class="header">
    <div class="header_text"><p>THEO DÕI CHÚNG TÔI</p></div>
    <div class="header_social">
        <a href="#"><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/fb.png" alt=""></a>
        <a href="#"><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ins.png" alt=""></a>
        <a href="#"><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/tt.png" alt=""></a>
        <a href="#"><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ytb.png" alt=""></a>
    </div>
</div>

<!-- ===== MENU ===== -->
<div class="menu">
                            <div class="menu_logo">
                                <img src="${pageContext.request.contextPath}/IMG/logo.jpg" alt="" style="width: 230px;">
                            </div>
                            <div class="menu_list">
                                <ul class="menu_list_item">
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/home.jsp">TRANG CHỦ</a></li>
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/vechungtoi.jsp">VỀ CHÚNG TÔI</a></li>
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/products">BỘ SƯU TẬP</a></li>
                                        <c:if test="${empty sessionScope.user}">
                                        <li><a class="menu_list_link" href="${pageContext.request.contextPath}/signup">
                                                ĐĂNG NHẬP & ĐĂNG KÝ
                                            </a></li>
                                        </c:if>
                                        <c:if test="${not empty sessionScope.user && sessionScope.user.role == 'ADMIN'}">
                                        <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/products?action=manage">QUẢN LÝ SẢN PHẨM</a></li>
                                        </c:if>
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/lienhe.jsp">LIÊN HỆ</a></li>
                                </ul>
                                <div class="menu_search">
                                    <div class="menu_search_input">
                                        <input type="text" placeholder="Nhập từ khóa bạn cần tìm kiếm . . . ">
                                    </div>
                                    <div class="menu_search_icon">
                                        <a href=""><i class="fa-solid fa-magnifying-glass fa-xl" style="color: #f76c85;"></i></a>
                                    </div>
                                </div>
                                <div class="menu_search_cart">
                                    <i class="fa-solid fa-cart-shopping cart-icon"></i>
                                    <!-- Tài khoản -->
                                    <c:if test="${!empty sessionScope.user}">
                                        <div class="account-menu">
                                        <i class="fas fa-user-circle account-icon"></i>
                                        <c:if test="${not empty sessionScope.user}">
                                            <div class="account-dropdown">
                                                <p class="welcome-text">Welcome, ${sessionScope.user.username}</p>
                                                <a href="${pageContext.request.contextPath}/account-management">Quản lý tài khoản</a>
                                                <a href="${pageContext.request.contextPath}/cart">My Cart</a>
                                                <a href="${pageContext.request.contextPath}/logout">Log Out</a>
                                            </div>
                                        </c:if>
                                    </div>
                                    </c:if> 
                                </div>
                            </div>    
                        </div>

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
                    
                    totalPrice += p.getPrice() * p.getQuantity();
                    Product product;    
                    ProductDB pd = new ProductDB();
                    product = pd.getProductById(p.getProduct_id());
                    %>
                    <div class="cart-item d-flex align-items-center mb-4 p-3 border rounded shadow-sm">
                        <input type="checkbox" class="cart-item-checkbox me-2" checked
                               data-price="<%=p.getPrice()%>" data-quantity="<%=p.getQuantity()%>">
                        <img src="${pageContext.request.contextPath}<%=product.getImageUrl()%>"
                             alt="<%=product.getName()%>" class="me-3" style="width:100px; height:100px; object-fit:cover;">
                        <div class="item-info flex-grow-1">
                            <h5><%=product.getName()%></h5>
                            <p class="item-price"><%=p.getPrice()%>₫ x <span class="item-qty"><%=p.getQuantity()%></span></p>
                            <input type="number" value="<%=p.getQuantity()%>" min="1" class="form-control w-25 text-center quantity-input" data-product-id="<%=p.getProduct_id()%>">
                        </div>
                        <div class="item-total ms-3">
                            <p class="fw-bold item-total-text"><%=p.getPrice() * p.getQuantity()%>₫</p>
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
                        <span id="subtotalDisplay"><%=totalPrice%></span>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span>Phí vận chuyển:</span>
                        <span>Miễn phí</span>
                    </div>
                    <hr>

                    <div class="mb-3">
                        <form id="promoForm" class="d-flex" method="post" action="${pageContext.request.contextPath}/apply-promo">
                            <input type="text" name="promoCode" id="promoCodeInput" class="form-control me-2" placeholder="Nhập mã khuyến mãi" value="${sessionScope.appliedDiscountCode}">
                            <button type="submit" class="btn btn-outline-danger">Áp dụng</button>
                        </form>
                        <c:if test="${not empty sessionScope.appliedDiscountCode}">
                            <small class="text-success">Đã áp dụng: ${sessionScope.appliedDiscountCode} (-${sessionScope.appliedDiscountAmount})</small>
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
                            (totalPrice - appliedDiscount) > 0 ? (totalPrice - appliedDiscount) : 0
                        %></strong>
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
    document.querySelectorAll('.cart-item-checkbox').forEach(cb => cb.addEventListener('change', updateTotal));

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
</script>

<!-- ===== FOOTER ===== -->
<footer class="text-white py-4 w-100 mt-5" style="background-color:#f76c85;">
    <!-- footer giữ nguyên -->
    <div class="container-fluid text-center">
        <div class="row">
            <div class="col-md-3">
                <h5 class="fw-bold">PINKYCLOUD OFFICE</h5>
                <p>Địa chỉ: Số 31, đường Nguyễn Thị Minh Khai, Phường Quy Nhơn, Gia Lai</p>
                <p>Mail: <a href="mailto:pinkycloudvietnam@gmail.com" class="text-white">pinkycloudvietnam@gmail.com</a></p>
                <p>Website: <a href="${pageContext.request.contextPath}/View/home.jsp" class="text-white">www.pinkycloud.vn</a></p>
            </div>
            <div class="col-md-3">
                <h5 class="fw-bold">DANH MỤC</h5>
                <ul class="list-unstyled">
                    <li><a href="${pageContext.request.contextPath}/products" class="text-white text-decoration-none">Sức khỏe và làm đẹp</a></li>
                    <li><a href="${pageContext.request.contextPath}/products" class="text-white text-decoration-none">Chăm sóc cơ thể</a></li>
                    <li><a href="${pageContext.request.contextPath}/products" class="text-white text-decoration-none">Chăm sóc da mặt</a></li>
                    <li><a href="${pageContext.request.contextPath}/products" class="text-white text-decoration-none">Chăm sóc tóc</a></li>
                    <li><a href="${pageContext.request.contextPath}/products" class="text-white text-decoration-none">Clinic & Spa</a></li>
                    <li><a href="${pageContext.request.contextPath}/products" class="text-white text-decoration-none">Trang điểm</a></li>
                </ul>
            </div>
            <div class="col-md-3">
                <h5 class="fw-bold">CHÍNH SÁCH HỖ TRỢ</h5>
                <ul class="list-unstyled">
                    <li><a href="#" class="text-white text-decoration-none">Hỗ trợ đặt hàng</a></li>
                    <li><a href="#" class="text-white text-decoration-none">Chính sách trả hàng</a></li>
                    <li><a href="#" class="text-white text-decoration-none">Chính sách bảo hành</a></li>
                    <li><a href="#" class="text-white text-decoration-none">Chính sách người dùng</a></li>
                    <li><a href="#" class="text-white text-decoration-none">Chính sách mua hàng</a></li>
                </ul>
            </div>
            <div class="col-md-3">
                <h5 class="fw-bold">THEO DÕI CHÚNG TÔI</h5>
                <div class="d-flex justify-content-center">
                    <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/fbf.png" width="32"></a>
                    <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/linkedin-54890.png" width="32"></a>
                    <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/tiktok-56510.png" width="32"></a>
                    <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/youtube-11341.png" width="32"></a>
                </div>
            </div>
        </div>
        <hr class="border-white my-3">
        <div class="text-center">
            <p class="mb-0">2023 Copyright PinkyCloud.vn - Sản phẩm chăm sóc da, Mỹ phẩm chính hãng</p>
        </div>
    </div>
</footer>

<script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</body>
</html>