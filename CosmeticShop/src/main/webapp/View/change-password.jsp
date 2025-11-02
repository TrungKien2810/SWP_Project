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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/log.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <!-- FONT AWESOME -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <title>PinkyCloud - Đổi mật khẩu</title>
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>
 

    <!-- Phần Đổi mật khẩu -->
    <div class="login-section">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-body">
                            <h2 class="text-center mb-4">Đổi mật khẩu</h2>
                            <p class="text-center text-muted mb-4">Chỉ hỗ trợ đổi mật khẩu cho tài khoản Gmail (@gmail.com)</p>
                            
                            <form method="post" action="${pageContext.request.contextPath}/change-password">
                                <div class="mb-3">
                                    <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
                                    <input type="password" class="form-control" name="currentPassword" id="currentPassword" placeholder="Nhập mật khẩu hiện tại" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                                    <input type="password" class="form-control" name="newPassword" id="newPassword" placeholder="Nhập mật khẩu mới" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                                    <input type="password" class="form-control" name="confirmPassword" id="confirmPassword" placeholder="Nhập lại mật khẩu mới" required>
                                </div>
                                
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                                </div>
                            </form>
                            
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/account-management" class="text-decoration-none">Quay lại quản lý tài khoản</a>
                            </div>
                            
                            <%
                                String error = (String) request.getAttribute("error");
                                String success = (String) request.getAttribute("success");
                                if (error != null) {
                            %>
                                <div class="alert alert-danger mt-3" role="alert">
                                    <%= error %>
                                </div>
                            <%
                                }
                                if (success != null) {
                            %>
                                <div class="alert alert-success mt-3" role="alert">
                                    <%= success %>
                                </div>
                            <%
                                }
                            %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <%@ include file="/View/includes/footer.jspf" %>
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
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>

