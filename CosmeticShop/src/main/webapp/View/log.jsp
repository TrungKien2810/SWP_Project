<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/dndk.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/font/fontawesome-free-6.7.2-web/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/log.css">
        <title>PinkyCloud - Đăng Nhập</title>
    </head>
    <body>
        <%@ include file="/View/includes/header.jspf" %>

                        <!-- Phần Đăng Nhập -->
                        <div class="login-section">
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
                                        <h1>Chào mừng trở lại</h1>
                                        <div class="social-icons">
                                            <a href="#"><i class="bi bi-facebook"></i></a>
                                            <a href="#"><i class="bi bi-twitter"></i></a>
                                            <a href="#"><i class="bi bi-instagram"></i></a>
                                            <a href="#"><i class="bi bi-youtube"></i></a>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <%
                                            Cookie[] cookies = request.getCookies();
                                            String savedEmail = "";
                                            String savedPass = "";
                                            if (cookies != null) {
                                                for (Cookie c : cookies) {
                                                    if ("email".equals(c.getName())) {
                                                        savedEmail = c.getValue();
                                                    }
                                                    if ("password".equals(c.getName())) {
                                                        savedPass = c.getValue();
                                                    }
                                                }
                                            }
                                        %>
                                        <h2>Đăng nhập</h2>
                                        <form action ="${pageContext.request.contextPath}/login" method="post">
                                            <div class="mb-3">
                                                <label for="email" class="form-label">Địa chỉ email</label>
                                                <input type="text" class="form-control" name="email" id="email" placeholder="Địa chỉ email" value="<%=savedEmail%>">
                                            </div>
                                            <div class="mb-3">
                                                <label for="password" class="form-label">Mật khẩu</label>
                                                <input type="password" class="form-control" name="password" id="password" placeholder="Mật khẩu" value="<%=savedPass%>">
                                            </div>

                                            <div class="mb-3 form-check">
                                                <input type="checkbox" class="form-check-input" name="remember" id="remember">
                                                <label class="form-check-label" for="remember">Nhớ mật khẩu</label>
                                            </div>
                                            <div class="row">
                                                <div class="col-6">
                                                    <button type="submit" class="btn btn-signin">Đăng nhập ngay!</button>
                                                </div>
                                                <div class="col-6">
                                                    <a href="${pageContext.request.contextPath}/signup" class="btn btn-signup">Đăng ký</a>
                                                </div>
                                            </div>
                                        </form>
                                        <p class="terms mt-3">Bằng cách nhấn vào "Đăng nhập ngay!", bạn đã đồng ý với <a href="#">Điều khoản dịch vụ</a> | <a href="#">Chính sách bảo mật</a></p>
                                        <a class="d-block mt-2" href="${pageContext.request.contextPath}/View/forgot-password.jsp">Quên mật khẩu?</a>
                                        <%
                                            if (request.getAttribute("error") != null) {
                                                String error = (String) request.getAttribute("error");
                                        %>
                                        <h5 style="color: red"><%=error%></h5>
                                        <%
                                            }
                                        %>
                                        <%
    String msg = request.getParameter("msg");
    if (msg == null) {
        msg = (String) request.getAttribute("msg");
    }
    if (msg != null) {
%>
<h5 style="color: green"><%=msg%></h5>
<%
    }
%>

                                    </div>
                                </div>
                            </div>

                            <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="successModalLabel">Đăng ký thành công</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            Chúc mừng! Bạn đã đăng nhập thành công. Bạn sẽ được chuyển hướng về trang đăng nhập trong giây lát${pageContext.request.contextPath}.
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                        </div>
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
                                        <p>Website: <a href="${pageContext.request.contextPath}/HTML/home.html" class="text-white">www.pinkycloud.vn</a></p>
                                    </div>
                                    <div class="col-md-3">
                                        <h5 class="fw-bold">DANH MỤC</h5>
                                        <ul class="list-unstyled">
                                            <li><a href="${pageContext.request.contextPath}/HTML/bosuutap.html" class="text-white text-decoration-none">Sức khỏe và làm đẹp</a></li>
                                            <li><a href="${pageContext.request.contextPath}/HTML/bosuutap.html" class="text-white text-decoration-none">Chăm sóc cơ thể</a></li>
                                            <li><a href="${pageContext.request.contextPath}/HTML/bosuutap.html" class="text-white text-decoration-none">Chăm sóc da mặt</a></li>
                                            <li><a href="${pageContext.request.contextPath}/HTML/bosuutap.html" class="text-white text-decoration-none">Chăm sóc tóc</a></li>
                                            <li><a href="${pageContext.request.contextPath}/HTML/bosuutap.html" class="text-white text-decoration-none">Clinic & Spa</a></li>
                                            <li><a href="${pageContext.request.contextPath}/HTML/bosuutap.html" class="text-white text-decoration-none">Trang điểm</a></li>
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
                        <script src="${pageContext.request.contextPath}/Js/home.js"></script>
                        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
                        <script src="${pageContext.request.contextPath}/JS/login.js"></script>
                        </body>
                        </html>
