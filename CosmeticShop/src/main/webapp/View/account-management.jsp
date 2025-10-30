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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-…" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <title>PinkyCloud - Account Management</title>
    <style>
        .account-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .account-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-bottom: 20px;
        }
        .account-header {
            text-align: center;
            margin-bottom: 30px;
        }
        .account-header h2 {
            color: #f76c85;
            margin-bottom: 10px;
        }
        .account-info {
            display: flex;
            align-items: center;
            margin-bottom: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 10px;
        }
        .account-avatar {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: #f76c85;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 2rem;
            margin-right: 20px;
        }
        .account-details h4 {
            color: #333;
            margin-bottom: 5px;
        }
        .account-details p {
            color: #666;
            margin: 0;
        }
        .account-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }
        .action-card {
            background: white;
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        .action-card:hover {
            border-color: #f76c85;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(247, 108, 133, 0.2);
        }
        .action-icon {
            font-size: 2.5rem;
            color: #f76c85;
            margin-bottom: 15px;
        }
        .action-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
        }
        .action-description {
            color: #666;
            font-size: 0.9rem;
        }
        /* Theme alignment */
        .btn-primary {
            background-color: #f76c85;
            border-color: #f76c85;
        }
        .btn-primary:hover {
            background-color: #e85c76;
            border-color: #e85c76;
        }
        .modal-header {
            background-color: #f76c85;
            color: #fff;
        }
        .modal-header .btn-close {
            filter: invert(1) grayscale(100%);
            opacity: 0.9;
        }
        .account-avatar:hover img#avatarDisplay {
            box-shadow: 0 0 0 3px rgba(247,108,133,0.35);
            transition: box-shadow 0.2s ease;
        }
    </style>
</head>
<body>
    <%@ include file="includes/header.jspf" %>
    <!-- Account Management -->
    <div class="account-container">
        <div class="account-card">
            <div class="account-header">
                <h2>Account Management</h2>
                <p>Quản lý thông tin và cài đặt tài khoản của bạn</p>
            </div>

            <div class="account-info">
                <div class="account-avatar" style="overflow:hidden; position:relative; cursor:pointer;" onclick="openEditProfileModal();">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user.avatarUrl}">
                            <img id="avatarDisplay" src="${pageContext.request.contextPath}${sessionScope.user.avatarUrl}" alt="Avatar" style="width:100%;height:100%;object-fit:cover;border-radius:50%;"/>
                        </c:when>
                        <c:otherwise>
                            <img id="avatarDisplay" src="${pageContext.request.contextPath}/IMG/default-avatar.png" alt="Avatar" style="width:100%;height:100%;object-fit:cover;border-radius:50%;"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="account-details flex-grow-1">
                    <h4>${sessionScope.user.username}</h4>
                    <p>${sessionScope.user.email}</p>
                    <p>Vai trò: ${sessionScope.user.role}</p>
                    
                </div>
            </div>

            <div class="account-actions">
                <div class="action-card" onclick="openEditProfileModal();">
                    <div class="action-icon">
                        <i class="fas fa-user-pen"></i>
                    </div>
                    <div class="action-title">Chỉnh sửa tài khoản</div>
                    <div class="action-description">Đổi tên hiển thị và ảnh đại diện</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/change-password'">
                    <div class="action-icon">
                        <i class="fas fa-key"></i>
                    </div>
                    <div class="action-title">Đổi mật khẩu</div>
                    <div class="action-description">Thay đổi mật khẩu tài khoản của bạn</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/cart'">
                    <div class="action-icon">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="action-title">Giỏ hàng</div>
                    <div class="action-description">Xem và quản lý giỏ hàng của bạn</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/shipping-address'">
                    <div class="action-icon">
                        <i class="fas fa-location-dot"></i>
                    </div>
                    <div class="action-title">Địa chỉ giao hàng</div>
                    <div class="action-description">Quản lý địa chỉ nhận hàng của bạn</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/View/forgot-password.jsp'">
                    <div class="action-icon">
                        <i class="fas fa-lock"></i>
                    </div>
                    <div class="action-title">Quên mật khẩu</div>
                    <div class="action-description">Khôi phục mật khẩu qua email</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/my-orders'">
                    <div class="action-icon">
                        <i class="fas fa-receipt"></i>
                    </div>
                    <div class="action-title">Lịch sử mua hàng</div>
                    <div class="action-description">Xem các đơn hàng và trạng thái thanh toán</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/logout'">
                    <div class="action-icon">
                        <i class="fas fa-sign-out-alt"></i>
                    </div>
                    <div class="action-title">Đăng xuất</div>
                    <div class="action-description">Thoát khỏi tài khoản hiện tại</div>
                </div>
            </div>

            
            <!-- Modal ẩn để chỉnh sửa tài khoản -->
            <div class="modal fade" id="editProfileModal" tabindex="-1" aria-hidden="true">
              <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title">Chỉnh sửa tài khoản</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <form action="${pageContext.request.contextPath}/account/avatar" method="post" enctype="multipart/form-data">
                    <div class="modal-body">
                      <div class="mb-3">
                        <label class="form-label">Tên hiển thị</label>
                        <input class="form-control" type="text" name="username" value="${sessionScope.user.username}" required>
                      </div>
                      <div class="mb-2">Ảnh đại diện</div>
                      <div class="d-flex align-items-center gap-3 mb-2">
                        <img id="avatarPreview" src="${pageContext.request.contextPath}${empty sessionScope.user.avatarUrl ? '/IMG/default-avatar.png' : sessionScope.user.avatarUrl}" alt="Preview" style="width:72px;height:72px;border-radius:50%;object-fit:cover;">
                        <label class="btn btn-outline-secondary mb-0">
                          <i class="fas fa-image me-1"></i> Chọn ảnh
                          <input id="avatarFileModal" type="file" name="avatar" accept="image/*" onchange="previewAvatar(event)" style="display:none;">
                        </label>
                      </div>
                      
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                      <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i>Lưu thay đổi</button>
                    </div>
                  </form>
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
    function previewAvatar(e){
        const file = e.target.files && e.target.files[0];
        if(!file) return;
        const url = URL.createObjectURL(file);
        const img = document.getElementById('avatarPreview');
        if(img) img.src = url;
    }
    function previewAvatarInline(e){
        const file = e.target.files && e.target.files[0];
        if(!file) return;
        const url = URL.createObjectURL(file);
        const img = document.getElementById('avatarDisplay');
        if(img) img.src = url;
    }
    function openEditProfileModal(){
        try { new bootstrap.Modal(document.getElementById('editProfileModal')).show(); } catch(e) {}
    }
    </script>

</body>
</html>

