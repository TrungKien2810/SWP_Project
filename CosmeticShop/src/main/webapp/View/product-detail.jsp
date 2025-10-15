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
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
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

    <div class="container" style="margin-top: 20px;">
        <!-- Hiển thị thông báo -->
        <c:if test="${not empty requestScope.msg}">
            <div class="alert alert-success alert-dismissible fade show" role="alert" style="margin-bottom: 20px;">
                <i class="fas fa-check-circle me-2"></i>
                ${requestScope.msg}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <% if (p != null) { %>  
            <div class="product-container">
                <!-- Product Image Gallery -->
                <div class="product-gallery">
                    <div class="main-image-container">
                        <img id="mainImage" src="${pageContext.request.contextPath}<%= p.getImageUrl() %>" alt="Hình sản phẩm" class="main-image"/>
                        <div class="image-zoom-overlay" id="zoomOverlay">
                            <span class="close-zoom">&times;</span>
                            <img id="zoomedImage" src="" alt="Zoomed image" class="zoomed-image"/>
                        </div>
                    </div>
                    
                    <!-- Thumbnail Navigation -->
                    <div class="thumbnail-container">
                        <% if (p.getImageUrls() != null && !p.getImageUrls().isEmpty()) { %>
                            <% for (int i = 0; i < p.getImageUrls().size(); i++) { %>
                                <div class="thumbnail-item <%= i == 0 ? "active" : "" %>" onclick="changeMainImage('<%= p.getImageUrls().get(i) %>', this)">
                                    <img src="${pageContext.request.contextPath}<%= p.getImageUrls().get(i) %>" alt="Thumbnail <%= i + 1 %>" class="thumbnail-image"/>
                                </div>
                            <% } %>
                        <% } else { %>
                            <div class="thumbnail-item active" onclick="changeMainImage('<%= p.getImageUrl() %>', this)">
                                <img src="${pageContext.request.contextPath}<%= p.getImageUrl() %>" alt="Thumbnail" class="thumbnail-image"/>
                            </div>
                        <% } %>
                    </div>
                </div>
                
                <!-- Product Details -->
                <div class="product-details">
                    <h2><%= p.getName() %></h2>
                    <div class="price-section">
                        <span class="price"><%= String.format("%.0f", p.getPrice()) %> VND</span>
                    </div>
                    <div class="product-info">
                        <p><b>Số lượng:</b> <%= p.getStock() %> sản phẩm có sẵn</p>
                        <p><b>Danh mục:</b> <%= categoryName != null ? categoryName : "Không xác định" %></p>
                    </div>
                    
                    <!-- Nút thêm vào giỏ hàng -->
                    <div class="action-buttons mb-4">
                        <a href="${pageContext.request.contextPath}/addToCart?id=<%=p.getProductId()%>"> 
                        <button class="btn btn-primary add-to-cart">
                            <i class="fas fa-shopping-cart me-2"></i>
                            Thêm vào giỏ hàng
                        </button>
                        </a>
                    </div>
                    
                    <div class="description-section">
                        <h4>Mô tả sản phẩm</h4>
                        <div class="description-content">
                            <%= p.getDescription() != null ? p.getDescription().replace("\n", "<br>") : "Chưa có mô tả" %>
                        </div>
                    </div>
                </div>
            </div>
        <% } else { %>
            <div class="no-product">
                <h3>Không tìm thấy sản phẩm</h3>
                <p>Sản phẩm bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
            </div>
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
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    
    <script>
        // Gallery functionality
        function changeMainImage(imageUrl, thumbnailElement) {
            // Update main image
            const mainImage = document.getElementById('mainImage');
            mainImage.src = '${pageContext.request.contextPath}' + imageUrl;
            
            // Update active thumbnail
            document.querySelectorAll('.thumbnail-item').forEach(item => {
                item.classList.remove('active');
            });
            thumbnailElement.classList.add('active');
        }
        
        // Image zoom functionality
        document.addEventListener('DOMContentLoaded', function() {
            const mainImage = document.getElementById('mainImage');
            const zoomOverlay = document.getElementById('zoomOverlay');
            const zoomedImage = document.getElementById('zoomedImage');
            const closeZoom = document.querySelector('.close-zoom');
            
            // Click to zoom
            mainImage.addEventListener('click', function() {
                zoomedImage.src = this.src;
                zoomOverlay.style.display = 'flex';
                document.body.style.overflow = 'hidden';
            });
            
            // Close zoom
            closeZoom.addEventListener('click', function() {
                zoomOverlay.style.display = 'none';
                document.body.style.overflow = 'auto';
            });
            
            // Close zoom when clicking overlay
            zoomOverlay.addEventListener('click', function(e) {
                if (e.target === zoomOverlay) {
                    zoomOverlay.style.display = 'none';
                    document.body.style.overflow = 'auto';
                }
            });
            
            // Close zoom with Escape key
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape' && zoomOverlay.style.display === 'flex') {
                    zoomOverlay.style.display = 'none';
                    document.body.style.overflow = 'auto';
                }
            });
        });
        
        // Add to cart functionality
        document.querySelector('.add-to-cart')?.addEventListener('click', function() {
            // TODO: Implement add to cart
            alert('Sản phẩm đã được thêm vào giỏ hàng!');
        });
        
        // Buy now functionality
        document.querySelector('.buy-now')?.addEventListener('click', function() {
            // TODO: Implement buy now
            alert('Chức năng mua ngay đang được phát triển!');
        });
    </script>
</body>

</html>
