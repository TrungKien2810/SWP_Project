<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/global-mobile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/dndk.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/font/fontawesome-free-6.7.2-web/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/register.css">
    <title>PinkyCloud - Đăng Ký</title>
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <!-- Phần Đăng Ký -->
    <div class="register-section">
        <div class="toast" id="notificationToast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header">
                <strong class="me-auto">Thông báo</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body"></div>
        </div>

        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h1>Tạo tài khoản</h1>
                    <div class="social-icons">
                        <a href="#"><i class="bi bi-facebook"></i></a>
                        <a href="#"><i class="bi bi-twitter"></i></a>
                        <a href="#"><i class="bi bi-instagram"></i></a>
                        <a href="#"><i class="bi bi-youtube"></i></a>
                    </div>
                </div>
                <div class="col-md-6">
                    <h2>Đăng ký</h2>
                    <form action="${pageContext.request.contextPath}/signup" method="post">
                        <div class="mb-3">
                            <label for="username" class="form-label">Tên đăng nhập</label>
                            <input type="text" class="form-control" name="username" id="username" placeholder="Tên đăng nhập">
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Địa chỉ email</label>
                            <input type="email" class="form-control" name="email" id="email" placeholder="Chỉ chấp nhận Gmail (@gmail.com)" pattern="^[A-Za-z0-9._%+-]+@gmail\.com$" required>
                            <div class="form-text">Vui lòng nhập địa chỉ Gmail hợp lệ (ví dụ: ten@gmail.com)</div>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Mật khẩu</label>
                            <input type="password" class="form-control" name="password" id="password" placeholder="Mật khẩu" required>
                        </div>
                        <div class="mb-3">
                            <label for="confirm-password" class="form-label">Nhập lại mật khẩu</label>
                            <input type="password" class="form-control" name="confirm-password" id="confirm-password" placeholder="Nhập lại mật khẩu" required>
                        </div>
                        <div class="row">
                            <div class="col-6">
                                <button type="submit" class="btn btn-signup">Đăng ký ngay!</button>
                            </div>
                            <div class="col-6">
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-login">Đăng nhập</a>
                            </div>
                        </div>
                    </form>
                    <p class="terms mt-3">Bằng cách nhấn vào "Đăng ký", bạn đã đồng ý với <a href="#">Điều khoản dịch vụ</a> | <a href="#">Chính sách bảo mật</a></p>
                    <a class="d-block mt-2" href="${pageContext.request.contextPath}/View/forgot-password.jsp">Quên mật khẩu?</a>
                    <%
                        if (request.getAttribute("error") != null) {
                            String error = (String) request.getAttribute("error");
                    %>
                        <div class="alert alert-danger mt-3" role="alert">
                            <%=error%>
                        </div>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
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
                        <li><a href="${pageContext.request.contextPath}/View/collection.jsp" class="text-white text-decoration-none">Sức khỏe và làm đẹp</a></li>
                        <li><a href="${pageContext.request.contextPath}/View/collection.jsp" class="text-white text-decoration-none">Chăm sóc cơ thể</a></li>
                        <li><a href="${pageContext.request.contextPath}/View/collection.jsp" class="text-white text-decoration-none">Chăm sóc da mặt</a></li>
                        <li><a href="${pageContext.request.contextPath}/View/collection.jsp" class="text-white text-decoration-none">Chăm sóc tóc</a></li>
                        <li><a href="${pageContext.request.contextPath}/View/collection.jsp" class="text-white text-decoration-none">Clinic & Spa</a></li>
                        <li><a href="${pageContext.request.contextPath}/View/collection.jsp" class="text-white text-decoration-none">Trang điểm</a></li>
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
                        <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/fbf.png" alt="Facebook" width="32"></a>
                        <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/linkedin-54890.png" alt="instagram" width="32"></a>
                        <a href="#" class="me-3"><img src="${pageContext.request.contextPath}/IMG/tiktok-56510.png" alt="LinkedIn" width="32"></a>
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
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script>
        // Fallback: Ensure mobile menu works on this page
        (function() {
            function initMobileMenu() {
                var mobileMenuToggle = document.getElementById('mobileMenuToggle');
                var mobileNav = document.getElementById('mobileNav');
                var mobileMenuOverlay = document.getElementById('mobileMenuOverlay');
                var body = document.body;

                if (!mobileMenuToggle || !mobileNav || !mobileMenuOverlay) {
                    return;
                }

                // Check if already initialized by home.js
                if (mobileMenuToggle.hasAttribute('data-initialized')) {
                    return;
                }

                mobileMenuToggle.setAttribute('data-initialized', 'true');

                function openMobileMenu() {
                    mobileMenuToggle.classList.add('active');
                    mobileNav.classList.add('active');
                    mobileMenuOverlay.classList.add('active');
                    body.classList.add('mobile-menu-open');
                }

                function closeMobileMenu() {
                    mobileMenuToggle.classList.remove('active');
                    mobileNav.classList.remove('active');
                    mobileMenuOverlay.classList.remove('active');
                    body.classList.remove('mobile-menu-open');
                }

                mobileMenuToggle.addEventListener('click', function(e) {
                    e.stopPropagation();
                    e.preventDefault();
                    if (mobileNav.classList.contains('active')) {
                        closeMobileMenu();
                    } else {
                        openMobileMenu();
                    }
                }, true);

                mobileMenuOverlay.addEventListener('click', function() {
                    closeMobileMenu();
                });

                var mobileNavLinks = mobileNav.querySelectorAll('.mobile-nav-links a');
                mobileNavLinks.forEach(function(link) {
                    link.addEventListener('click', function() {
                        closeMobileMenu();
                    });
                });

                // Add scroll effect
                window.addEventListener('scroll', function() {
                    if (window.scrollY > 50) {
                        mobileMenuToggle.classList.add('scrolled');
                    } else {
                        mobileMenuToggle.classList.remove('scrolled');
                    }
                }, { passive: true });
            }

            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', initMobileMenu);
            } else {
                initMobileMenu();
            }
        })();
    </script>
</body>
</html>
