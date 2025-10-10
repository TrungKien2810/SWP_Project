<%@page import="Model.Product"%>
<%@page import="DAO.ProductDB"%>
<%@page import="Model.CartItems"%>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
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
        <img src="${pageContext.request.contextPath}/IMG/logo.jpg" alt="" style="width:230px;">
    </div>
    <div class="menu_list">
        <ul class="menu_list_item">
            <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/home.jsp">TRANG CHỦ</a></li>
            <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/vechungtoi.jsp">VỀ CHÚNG TÔI</a></li>
            <li><a class="menu_list_link" href="${pageContext.request.contextPath}/products">BỘ SƯU TẬP</a></li>
            <c:if test="${empty sessionScope.user}">
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/signup">ĐĂNG NHẬP & ĐĂNG KÝ</a></li>
            </c:if>
            <c:if test="${not empty sessionScope.user && sessionScope.user.role == 'ADMIN'}">
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/products?action=manage">QUẢN LÝ SẢN PHẨM</a></li>
            </c:if>
            <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/lienhe.jsp">LIÊN HỆ</a></li>
        </ul>
    </div>
</div>

<%
    List<CartItems> cartItems = new ArrayList<CartItems>();
    if(session.getAttribute("cartItems") != null){
    cartItems = (List<CartItems>)session.getAttribute("cartItems");
    }
    Double totalPrice = 0.0;
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
                    <div class="cart-item d-flex align-items-center mb-4 p-3 border rounded shadow-sm">
                        <input type="checkbox" class="cart-item-checkbox me-2" checked
                               data-price="<%=p.getPrice()%>" data-quantity="<%=p.getQuantity()%>">
                        <img src="${pageContext.request.contextPath}<%=product.getImageUrl()%>"
                             alt="<%=product.getName()%>" class="me-3" style="width:100px; height:100px; object-fit:cover;">
                        <div class="item-info flex-grow-1">
                            <h5><%=product.getName()%></h5>
                            <p class="item-price"><%=p.getPrice()%>₫ x <span class="item-qty"><%=p.getQuantity()%></span></p>
                            <input type="number" value="<%=p.getQuantity()%>" min="1" class="form-control w-25 text-center quantity-input">
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
                        <form id="promoForm" class="d-flex">
                            <input type="text" id="promoCodeInput" class="form-control me-2" placeholder="Nhập mã khuyến mãi">
                            <button type="submit" class="btn btn-outline-danger">Áp dụng</button>
                        </form>
                        <small id="promoDisplay" class="text-success" style="display:none;"></small>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="fw-bold">Tổng thanh toán:</span>
                        <strong style="color:#f76c85;" id="totalDisplay"><%=totalPrice%></strong>
                    </div>

                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-danger w-100 fw-bold">THANH TOÁN NGAY</a>
                </div>
            </div>
        <% } %>
    </div>
</div>

<!--<script>
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

    function updateTotal() {
        let subtotal = 0;
        document.querySelectorAll('.cart-item').forEach(item => {
            const price = parseInt(item.querySelector('.cart-item-checkbox').dataset.price);
            const qty = parseInt(item.querySelector('.quantity-input').value);
            item.querySelector('.cart-item-checkbox').dataset.quantity = qty;
            item.querySelector('.item-qty').textContent = qty;
            item.querySelector('.item-total-text').textContent = (price * qty).toLocaleString() + '₫';
            if (item.querySelector('.cart-item-checkbox').checked) {
                subtotal += price * qty;
            }
        });
        const subtotalDisplay = document.getElementById('subtotalDisplay');
        const totalDisplay = document.getElementById('totalDisplay');
        if(subtotalDisplay) subtotalDisplay.textContent = subtotal.toLocaleString() + '₫';
        if(totalDisplay) totalDisplay.textContent = subtotal.toLocaleString() + '₫';
    }

    // Event checkbox & quantity input
    document.querySelectorAll('.quantity-input').forEach(input => input.addEventListener('input', updateTotal));
    document.querySelectorAll('.cart-item-checkbox').forEach(cb => cb.addEventListener('change', updateTotal));

    // Event nút xóa
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.cart-item').remove();
            updateTotal();
            updateCartUI();
        });
    });

    updateTotal();

    // Mã giảm giá
    const promoForm = document.getElementById('promoForm');
    if(promoForm) {
        promoForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const code = document.getElementById('promoCodeInput').value.trim();
            let discount = 0;
            if (code === "GIAM10") discount = 10000;
            document.getElementById('promoDisplay').textContent = "Mã giảm giá áp dụng: -" + discount.toLocaleString() + "₫";
            document.getElementById('promoDisplay').style.display = 'inline';

            let subtotal = 0;
            document.querySelectorAll('.cart-item').forEach(item => {
                const price = parseInt(item.querySelector('.cart-item-checkbox').dataset.price);
                const qty = parseInt(item.querySelector('.quantity-input').value);
                if (item.querySelector('.cart-item-checkbox').checked) subtotal += price * qty;
            });
            document.getElementById('totalDisplay').textContent = (subtotal - discount).toLocaleString() + '₫';
        });
    }
</script>-->

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
