<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Thanh toán</title>
    <style>
        .container-sm { max-width: 1000px; }
        .card { border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .highlight { color:#f76c85; }
    </style>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
</head>
<body>
    <!-- header -->
    <div class="header">
        <div class="header_text"><p>THEO DÕI CHÚNG TÔI</p></div>
        <div class="header_social">
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/fb.png" alt="" ></a>
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ins.png" alt=""></a>
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/tt.png" alt=""></a>
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ytb.png" alt="" ></a>
        </div>
    </div>

    <!-- menu -->
    <div class="menu">
        <div class="menu_logo">
            <a href="${pageContext.request.contextPath}/View/home.jsp"><img src="${pageContext.request.contextPath}/IMG/logo.jpg" alt="" style="width: 230px;"></a>
        </div>
        <div class="menu_list">
            <ul class="menu_list_item">
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/home.jsp">TRANG CHỦ</a></li>
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/vechungtoi.jsp">VỀ CHÚNG TÔI</a></li>
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/products">BỘ SƯU TẬP</a></li>
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/lienhe.jsp">LIÊN HỆ</a></li>
            </ul>
            <div class="menu_search">
                <div class="menu_search_input">
                    <input type="text" placeholder="Nhập từ khóa bạn cần tìm kiếm . . . ">
                </div>
                <div class="menu_search_icon">
                    <a href="${pageContext.request.contextPath}/cart"> <i class="fa-solid fa-cart-shopping cart-icon"></i></a>
                </div>
            </div>
            <div class="menu_search_cart">
                <a href="${pageContext.request.contextPath}/cart"> <i class="fa-solid fa-cart-shopping cart-icon"></i></a>
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

    <div class="container-sm my-4">
        <c:if test="${param.error == 'no_address'}">
            <div class="alert alert-danger">Cần thêm địa chỉ giao hàng trước khi đặt hàng.</div>
        </c:if>
        <h3 class="mb-3">Thanh toán</h3>

        <form method="post" action="${pageContext.request.contextPath}/checkout">
            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Địa chỉ giao hàng</h5>
                        <c:choose>
                            <c:when test="${empty addresses}">
                                <p>Bạn chưa có địa chỉ. <a href="${pageContext.request.contextPath}/shipping-address?return_to=checkout">Thêm địa chỉ</a></p>
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex gap-2">
                                    <select class="form-select" name="shipping_address_id" required>
                                    <c:forEach var="a" items="${addresses}">
                                        <option value="${a.addressId}" ${a['default'] ? 'selected' : ''}>
                                            ${a.fullName} - ${a.phone} | ${a.address}, ${a.ward}, ${a.district}, ${a.city}
                                        </option>
                                    </c:forEach>
                                    </select>
                                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/shipping-address?return_to=checkout">Thêm địa chỉ</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Phương thức giao hàng</h5>
                        <c:forEach var="m" items="${methods}" varStatus="st">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="shipping_method_id" id="m_${m.methodId}" value="${m.methodId}" data-cost="${m.cost}" ${st.first ? 'checked' : ''} required>
                                <label class="form-check-label" for="m_${m.methodId}">
                                    ${m.name} - <span class="highlight">${m.cost}</span> VND (dự kiến ${m.estimatedDays} ngày)
                                </label>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Phương thức thanh toán</h5>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="payment_method" id="cod" value="COD" checked>
                            <label class="form-check-label" for="cod">Thanh toán khi nhận hàng (COD)</label>
                        </div>
                        <div class="form-check mt-2">
                            <input class="form-check-input" type="radio" name="payment_method" id="vnpay" value="BANK">
                            <label class="form-check-label" for="vnpay">Thanh toán qua VNPAY</label>
                        </div>
                        <div class="form-text">Chọn VNPAY để chuyển đến cổng thanh toán VNPAY.</div>
                    </div>

                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Ghi chú</h5>
                        <textarea class="form-control" name="notes" rows="3" placeholder="Ghi chú cho đơn hàng..."></textarea>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="card p-3">
                        <h5 class="mb-3">Đơn hàng của bạn</h5>
                        <div class="list-group mb-3">
                            <c:forEach var="it" items="${items}">
                                <div class="list-group-item d-flex align-items-center justify-content-between">
                                    <div class="d-flex align-items-center">
                                        <img src="${pageContext.request.contextPath}/${it.imageUrl}" alt="${it.productName}" style="width:56px;height:56px;object-fit:cover;border-radius:6px;margin-right:10px;">
                                        <div>
                                            <div class="fw-semibold">${it.productName}</div>
                                            <div class="text-muted">SL: ${it.quantity}</div>
                                        </div>
                                    </div>
                                    <div>${it.price * it.quantity}</div>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>Tạm tính:</span>
                            <strong>${itemsTotal}</strong>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>Phí vận chuyển:</span>
                            <strong id="shippingCost">0</strong>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <span>Tổng cộng:</span>
                            <strong id="grandTotal">${itemsTotal}</strong>
                        </div>
                        <div class="mt-3">
                            <button class="btn btn-primary w-100" style="background-color:#f76c85;border-color:#f76c85;">Đặt hàng</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <script>
        (function(){
            var itemsTotal = Number('${itemsTotal}'.replace(',', '')) || 0;
            function updateTotals(){
                var checked = document.querySelector('input[name="shipping_method_id"]:checked');
                var cost = checked ? Number(checked.getAttribute('data-cost')) : 0;
                if (isNaN(cost)) cost = 0;
                document.getElementById('shippingCost').textContent = cost;
                document.getElementById('grandTotal').textContent = (itemsTotal + cost);
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
</body>
</html>


