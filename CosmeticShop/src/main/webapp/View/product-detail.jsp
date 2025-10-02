<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Product p = (Product) request.getAttribute("product");
    String categoryName = (String) request.getAttribute("categoryName");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/product-detail.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <!-- header -->
    <div class="header">
        <div class="header_text"><p>THEO DÕI CHÚNG TÔI</p></div>
        <div class="header_social">
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/fb.png" alt="" ></a>
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ins.png" alt=""></a>
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/tt.png"alt=""></a>
            <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ytb.png" alt="" ></a>
        </div>
    </div>
    
    <!-- menu -->
    <div class="menu">
        <div class="menu_logo">
            <img src="${pageContext.request.contextPath}/IMG/logo.jpg" alt="" style="width: 230px;">
        </div>
        <div class="menu_list">
            <ul class="menu_list_item">
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/home.jsp">TRANG CHỦ</a></li>
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/vechungtoi.jsp">VỀ CHÚNG TÔI</a></li>
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/products">BỘ SƯU TẬP</a></li>
                <c:if test="${empty sessionScope.user}">
                    <li><a class="menu_list_link" href="${pageContext.request.contextPath}/signup">
                            ĐĂNG NHẬP & ĐĂNG KÝ
                        </a></li>
                </c:if>
                <c:if test="${not empty sessionScope.user && sessionScope.user.role == 'ADMIN'}">
                <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/products?action=manage">QUẢN LÝ SẢN PHẨM</a></li>
                </c:if>
                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/View/lienhe.jsp">LIÊN HỆ</a></li>
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
                <i class="fa-solid fa-cart-shopping"></i>
                <!-- Tài khoản -->
                <div class="account-menu">
                    <i class="fas fa-user-circle account-icon"></i>
                    <c:if test="${not empty sessionScope.user}">
                        <div class="account-dropdown">
                            <p class="welcome-text">Welcome, ${sessionScope.user.username}</p>
                            <a href="${pageContext.request.contextPath}/View/account.jsp">Account Setting</a>
                            <a href="${pageContext.request.contextPath}/cart">My Cart</a>
                            <a href="${pageContext.request.contextPath}/logout">Log Out</a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>    
    </div>
    <div class="container" style="margin-top: 20px;">
        <% if (p != null) { %>  
            <div class="product-image">
                <img src="${pageContext.request.contextPath}<%= p.getImageUrl() %>" alt="Hình sản phẩm"/>
            </div>
            <div class="product-details">
                <h2>Thông tin sản phẩm</h2>
                <p><b>ID:</b> <%= p.getProductId() %></p>
                <p><b>Tên:</b> <%= p.getName() %></p>
                <p><b>Giá:</b> 
                    <span class="price"><%= p.getPrice() %> VND</span>
                </p>
                <p><b>Số lượng:</b> <%= p.getStock() %></p>
                <p><b>Mô tả:</b> <%= p.getDescription() %></p>
                <p><b>Danh mục:</b> <%= categoryName != null ? categoryName : "Không xác định" %></p>
                <button class="btn">Mua ngay</button>
            </div>
            <div class="clear"></div>
        <% } else { %>
            <p>Không tìm thấy sản phẩm</p>
        <% } %>
    </div>
    <footer class="text-white py-4 w-100" style="background-color:#f76c85;">
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
                                            <li><a href="${pageContext.request.contextPath}/View/bosuutap.jsp" class="text-white text-decoration-none">Sức khỏe và làm đẹp</a></li>
                                            <li><a href="${pageContext.request.contextPath}/View/bosuutap.jsp" class="text-white text-decoration-none">Chăm sóc cơ thể</a></li>
                                            <li><a href="${pageContext.request.contextPath}/View/bosuutap.jsp" class="text-white text-decoration-none">Chăm sóc da mặt</a></li>
                                            <li><a href="${pageContext.request.contextPath}/View/bosuutap.jsp" class="text-white text-decoration-none">Chăm sóc tóc</a></li>
                                            <li><a href="${pageContext.request.contextPath}/View/bosuutap.jsp" class="text-white text-decoration-none">Clinic & Spa</a></li>
                                            <li><a href="${pageContext.request.contextPath}/View/bosuutap.jsp" class="text-white text-decoration-none">Trang điểm</a></li>
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
                                        <div class="d-flex info">
                                            <a href="" class="me-3"><img src="${pageContext.request.contextPath}/IMG/fbf.png" alt="Facebook" width="32"></a>
                                            <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/linkedin-54890.png" alt="instagram" width="32"></a>
                                            <a href="" class="me-3"><img src="${pageContext.request.contextPath}/IMG/tiktok-56510.png" alt="LinkedIn" width="32"></a>
                                            <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/youtube-11341.png" alt="YouTube" width="32"></a>
                                            <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/twitter.png" alt="Twitter" width="32"></a>
                                        </div>
                                        <div class="mt-2">
                                            <img src="${pageContext.request.contextPath}/IMG/bocongthuong.png" alt="Bộ Công Thương" width="120">
                                        </div>
                                    </div>
                                </div>
                                <hr class="border-white my-3">
                                <div class="text-center">
                                    <p class="mb-0">2023 Copyright PinkyCloud.vn - Sản phẩm chăm sóc da, Mỹ phẩm trang điểm, Mỹ phẩm chính hãng</p>
                                </div>
                            </div>
                        </footer>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</body>

</html>
