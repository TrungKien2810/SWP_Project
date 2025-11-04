<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/log.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <title>PinkyCloud - Quên mật khẩu</title>
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <!-- Phần Quên mật khẩu -->
    <div class="login-section">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-body">
                            <h2 class="text-center mb-4">Quên mật khẩu</h2>
                            <p class="text-center text-muted mb-4">Địa chỉ Gmail đã đăng ký để nhận link khôi phục mật khẩu</p>
                            
                            <form method="post" action="${pageContext.request.contextPath}/password/reset/request">
                                <div class="mb-3">
                                    <label for="email" class="form-label">Địa chỉ email</label>
                                    <c:choose>
    <c:when test="${not empty sessionScope.user and not empty sessionScope.user.email}">
        <input type="email"
               class="form-control"
               name="email"
               id="email"
               value="${sessionScope.user.email}"
               readonly
               required
               pattern="^[A-Za-z0-9._%+-]+@gmail\\.com$"
               placeholder="Nhập Gmail đã đăng ký">
    </c:when>

    <c:otherwise>
        <input type="email"
               class="form-control"
               name="email"
               id="email"
               placeholder="Nhập Gmail đã đăng ký"
               required
               pattern="^[A-Za-z0-9._%+-]+@gmail\\.com$">
    </c:otherwise>
</c:choose>
                                    <div class="form-text">Vui lòng nhập địa chỉ Gmail đã đăng ký (ví dụ: ten@gmail.com)</div>

                                </div>
                                
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Gửi link khôi phục</button>
                                </div>
                            </form>
                            
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">Quay lại đăng nhập</a>
                            </div>
                            
                            <%
                                String msg = request.getParameter("msg");
                                if (msg != null) {
                            %>
                                <div class="alert alert-success mt-3" role="alert">
                                    <%= msg %>
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
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>



