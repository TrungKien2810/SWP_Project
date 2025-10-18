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
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-…" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <title>Pinky Cloud</title>
    </head>
    <body>
        <%@ include file="/View/includes/header.jspf" %>

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
                        <%@ include file="/View/includes/footer.jspf" %>
                        <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
                        <script src="${pageContext.request.contextPath}/Js/home.js"></script>
                    
                        </body>
                        </html>
