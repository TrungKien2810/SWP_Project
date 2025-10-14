<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
        <link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
      integrity="sha512-…"
      crossorigin="anonymous" referrerpolicy="no-referrer" />
        <script src="${pageContext.request.contextPath}/Js/home.js"></script>
        <title>Pinky Cloud</title>
    </head>
    <body>
        <!-- header -->
        <div class="header">
            <div class="header_text"><p>THEO DÕI CHÚNG TÔI</p></div>
            <div class="header_social">
                <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/fb.png" alt="" ></a>
                <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ins.png" alt=""></a>
                <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/tt.png"alt=""><a>
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

                        <!-- MODAL LOGIN -->
                        <!-- Modal Đăng Nhập -->
                        <!--<div class="modal" id="loginModal">
                            <div class="modal-content">
                                <span class="close">&times;</span>
                                <h2>Đăng nhập</h2>
                                <form>
                                    <div class="input-group">
                                        <label for="email">Email</label>
                                        <input type="email" id="email" placeholder="Nhập email của bạn" required>
                                    </div>
                                    <div class="input-group">
                                        <label for="password">Mật khẩu</label>
                                        <input type="password" id="password" placeholder="Nhập mật khẩu" required>
                                    </div>
                                    <button type="submit">Đăng nhập</button>
                                </form>
                                <p>Chưa có tài khoản? <a href="#" id="showRegister">Đăng ký ngay</a></p>
                            </div>
                        </div> -->

<!-- Modal Đăng Ký -->
<div class="modal" id="registerModal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>Đăng ký</h2>
        <form>
            <div class="input-group">
                <label for="fullname">Họ và tên</label>
                <input type="text" id="fullname" placeholder="Nhập họ và tên" required>
            </div>
            <div class="input-group">
                <label for="emailReg">Email</label>
                <input type="email" id="emailReg" placeholder="Nhập email của bạn" required>
            </div>
            <div class="input-group">
                <label for="passwordReg">Mật khẩu</label>
                <input type="password" id="passwordReg" placeholder="Tạo mật khẩu" required>
            </div>
            <button type="submit">Đăng ký</button>
        </form>
        <p>Đã có tài khoản? <a href="#" id="showLogin">Đăng nhập</a></p>
    </div>
</div>
                    
                        <div id="carouselExample" class="carousel slide mt-3" data-bs-ride="carousel" data-bs-interval="2000">
                            <div class="carousel-inner">
                                <div class="carousel-item active">
                                    <img src="${pageContext.request.contextPath}/IMG/hinhnen1.png"
                                         class="d-block w-100"
                                         alt="Slide 1"
                                         style="max-height: 550px; object-fit: cover;">
                                </div>
                                <div class="carousel-item">
                                    <img src="${pageContext.request.contextPath}/IMG/hinhnen2.png"
                                         class="d-block w-100"
                                         alt="Slide 2"
                                         style="max-height: 550px; object-fit: cover;">
                                </div>
                                <div class="carousel-item">
                                    <img src="${pageContext.request.contextPath}/IMG/hinhnen3.png"
                                         class="d-block w-100"
                                         alt="Slide 3"
                                         style="max-height: 550px; object-fit: cover;">
                                </div> 
                                <div class="carousel-item">
                                    <img src="${pageContext.request.contextPath}/IMG/hinhnen4.png"
                                         class="d-block w-100"
                                         alt="Slide 4"
                                         style="max-height: 550px; object-fit: cover;">
                                </div>
                            </div>

                            <!-- Nút điều hướng -->
                            <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample" data-bs-slide="prev">
                                <span class="carousel-control-prev-icon"></span>
                                <span class="visually-hidden">Previous</span>
                            </button>
                            <button class="carousel-control-next" type="button" data-bs-target="#carouselExample" data-bs-slide="next">
                                <span class="carousel-control-next-icon"></span>
                                <span class="visually-hidden">Next</span>
                            </button>
                        </div> 



                        <div class="text">
                            <h2>KHÁM PHÁ BỘ SƯU TẬP</h2>
                        </div>

                        <div class="container mt-4">
                            <div class="row g-5">
                                <!-- Hàng 1 -->
                                <div class="col-md-6">
                                    <div class="banner-item bg-lightblue">
                                        <img src="${pageContext.request.contextPath}/IMG/bst1.jpg" alt="">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="banner-item bg-lightpink">
                                        <img src="${pageContext.request.contextPath}/IMG/bst2.jpg" alt="">
                                    </div>
                                </div>

                                <!-- Hàng 2 -->
                                <%-- <div class="col-md-4">
                                    <div class="banner-item bg-lightgray">
                                      <a href="https://www.son3ce.com/"><img src="${pageContext.request.contextPath}/IMG/bst3.png" alt=""></a>  
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="banner-item bg-lightorange">
                                        <a href="https://www.carslan.com/?srsltid=AfmBOopZ7zVD_OR9MDgYT29w8ByUaNikvqYyjkU0g5t3OKv6xvlgfrfX">
                                            <img src="${pageContext.request.contextPath}/IMG/bst4.png" alt="">
                                        </a>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="banner-item bg-lightgray">
                                        <a href="https://www.judydoll.com/"> <img src="${pageContext.request.contextPath}/IMG/bst5.png" alt=""></a>
                                       
                                    </div>
                                </div>

        <!-- Hàng 3 -->
        <div class="col-md-4">
            <div class="banner-item bg-lightred">
                <a href="https://acnes.com.vn/">       
                     <img src="${pageContext.request.contextPath}/IMG/bst6.png" alt="">
                </a>
            </div>
        </div>
        <div class="col-md-4">
            <div class="banner-item bg-lightblue">
                <a href="https://anessa.vn/">                
                    <img src="${pageContext.request.contextPath}/IMG/bst7.png" alt="">
                </a>
            </div>
        </div>
        <div class="col-md-4">
            <div class="banner-item bg-lightyellow">
                <a href="https://www.bioderma.com.vn/">  
                    <img src="${pageContext.request.contextPath}/IMG/bst8.png" alt="">
                </a>
              
            </div>
        </div>   --%>
                            </div> 

                            <div class="container mt-4 text-center">
                                <h2 class="text-center text-uppercase fw-bold " style="color: #f76c85;">
                                    Chi Nhánh Phân Phối
                                </h2>
                                <div class="row mt-4">
                                    <!-- Cột chứa bản đồ -->
                                    <div class="col-md-6 map-container text-center">
                                        <img src="${pageContext.request.contextPath}/IMG/map.jpg" alt="Bản đồ phân phối">
                                    </div>
                                    <!-- Cột chứa thông tin chi nhánh -->
                                    <div class="col-md-6 branch-info">
                                        <h3>Trụ Sở Chính</h3>
                                        <p><strong>Cửa hàng mỹ phẩm PinkyCloud</strong></p>
                                        <p>📍 Số 31, đường Nguyễn Thị Minh Khai, Phường Quy Nhơn, Gia Lai</p>
                                        <p>📧 pinkycloudvietnam@gmail.com</p>
                                        <p>🌍 <a href="${pageContext.request.contextPath}/View/home.jsp" target="_blank" style="color: #24e454; text-decoration: none;" >pinkycloud.vn</a></p>

                                        <h3>Hệ Thống Phòng Kinh Doanh</h3>
                                        <h5>TOCEPO THỊ NẠI</h5>
                                        <p>📍 224 Đống Đa, Thị Nải, Quy Nhơn, Bình Định, Việt Nam</p>
                                        <p>📞 0888.004.444 - 0888.885.884 (Zalo)</p>

                                        <h5>Quán Nhậu Aking 2</h5>
                                        <p>📍153-155 Bùi Xuân Phái, Trần Hưng Đạo, Quy Nhơn, Bình Định, Việt Nam</p>
                                        <p>📞 0833.55.4444 (Zalo)</p>

                                        <h3>Nhà Phân Phối KEEPFLY</h3>
                                        <h5>🏠 Bình Dương</h5>
                                        <p>📍 1/5 KP. Bình Quới A, P. Bình Chuẩn, TP. Thuận An</p>

                                        <h5>🏠 TP. Nha Trang</h5>
                                        <p>📍 17/11/1 đường 52, Vĩnh Phước, Tp Nha Trang</p>

                                        <h5>🏠 Kiên Giang</h5>
                                        <p>📍 151 Quang Trung, Vĩnh Thạnh, Rạch Giá, Kiên Giang</p>

                                        <h3>PINKYCLOUD SHOWROOM</h3>
                                        <p>📍 15-17 Tô Hiệu, Tân Phú, TP. Hồ Chí Minh</p>
                                        <p>📍 56 Ngô Gia Tự, Tây Sơn, Bình Định</p>
                                    </div>
                                </div>
                            </div>
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
