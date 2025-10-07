<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Giỏ hàng của bạn - PinkyCloud</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/cart.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
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

        <!-- ===== CART CONTENT ===== -->
        <div class="cart-page container mt-5">
            <h2 class="text-center mb-4">GIỎ HÀNG CỦA BẠN</h2>

            <c:if test="${empty cartItems}">
                <div class="empty-cart text-center">
                    <i class="fa-solid fa-cart-shopping fa-3x mb-3" style="color:#f76c85;"></i>
                    <p>Giỏ hàng của bạn đang trống</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-danger mt-2">Tiếp tục mua sắm</a>
                </div>
            </c:if>

            <c:if test="${not empty cartItems}">
                <div class="cart-content row">
                    <!-- DANH SÁCH SẢN PHẨM -->
                    <div class="col-md-8 cart-items">
                        <c:forEach var="item" items="${cartItems}">
                            <div class="cart-item d-flex align-items-center mb-4 p-3 border rounded shadow-sm">
                                <img src="${pageContext.request.contextPath}/IMG/${item.image}" 
                                     alt="${item.name}" class="me-3" style="width:100px; height:100px; object-fit:cover;">
                                <div class="item-info flex-grow-1">
                                    <h5>${item.name}</h5>
                                    <p>${item.price}₫</p>
                                    <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center gap-2">
                                        <input type="hidden" name="action" value="update">
                                        <input type="hidden" name="id" value="${item.id}">
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" class="form-control w-25 text-center">
                                        <button type="submit" class="btn btn-light border"><i class="fa-solid fa-rotate"></i></button>
                                    </form>
                                    <a href="${pageContext.request.contextPath}/cart?action=remove&id=${item.id}" 
                                       class="text-danger small mt-2 d-inline-block">Xóa</a>
                                </div>
                                <div class="item-total ms-3">
                                    <p class="fw-bold">${item.price * item.quantity}₫</p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- TỔNG KẾT GIỎ HÀNG -->
                    <div class="col-md-4 cart-summary bg-light p-4 rounded shadow-sm">
                        <h4 class="fw-bold mb-3">Tổng cộng</h4>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tạm tính:</span>
                            <span>${cartTotal}₫</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Phí vận chuyển:</span>
                            <span>Miễn phí</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <span class="fw-bold">Tổng thanh toán:</span>
                            <strong style="color:#f76c85;">${cartTotal}₫</strong>
                        </div>
                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-danger w-100 fw-bold">THANH TOÁN NGAY</a>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- ===== FOOTER ===== -->
        <footer class="text-white py-4 w-100 mt-5" style="background-color:#f76c85;">
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
