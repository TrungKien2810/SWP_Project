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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <title>PinkyCloud - Account Management</title>
    <style>
        .account-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
        }
        .account-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(247, 108, 133, 0.1);
            padding: 25px;
            margin-bottom: 20px;
        }
        .account-header {
            text-align: center;
            margin-bottom: 20px;
        }
        .account-header h2 {
            color: #f76c85;
            margin-bottom: 5px;
            font-size: 1.8rem;
            font-weight: 700;
        }
        .account-header p {
            color: #666;
            font-size: 0.9rem;
            margin: 0;
        }
        .account-info {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            padding: 15px 20px;
            background: linear-gradient(135deg, #fff5f7 0%, #ffffff 100%);
            border-radius: 10px;
            border: 2px solid #fbd0da;
        }
        .account-avatar {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: #f76c85;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.5rem;
            margin-right: 15px;
            flex-shrink: 0;
        }
        .account-details h4 {
            color: #333;
            margin-bottom: 3px;
            font-size: 1.1rem;
        }
        .account-details p {
            color: #666;
            margin: 0;
            font-size: 0.85rem;
            line-height: 1.4;
        }
        .account-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
            gap: 12px;
            margin-top: 20px;
        }
        .action-card {
            background: white;
            border: 2px solid #fbd0da;
            border-radius: 10px;
            padding: 15px 10px;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        .action-card:hover {
            border-color: #f76c85;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(247, 108, 133, 0.2);
            background: #fff5f7;
        }
        .action-icon {
            font-size: 2rem;
            color: #f76c85;
            margin-bottom: 8px;
        }
        .action-title {
            font-size: 0.95rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 0;
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

    <main class="account-container" style="margin-top: 30px;">
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
                    <div class="action-title">Chỉnh sửa</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/change-password'">
                    <div class="action-icon">
                        <i class="fas fa-key"></i>
                    </div>
                    <div class="action-title">Đổi mật khẩu</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/shipping-address'">
                    <div class="action-icon">
                        <i class="fas fa-location-dot"></i>
                    </div>
                    <div class="action-title">Địa chỉ</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/wishlist'">
                    <div class="action-icon">
                        <i class="fas fa-heart"></i>
                    </div>
                    <div class="action-title">Yêu thích</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/my-orders'">
                    <div class="action-icon">
                        <i class="fas fa-receipt"></i>
                    </div>
                    <div class="action-title">Đơn hàng</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/cart'">
                    <div class="action-icon">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="action-title">Giỏ hàng</div>
                </div>

                <div class="action-card" onclick="location.href='${pageContext.request.contextPath}/logout'">
                    <div class="action-icon">
                        <i class="fas fa-sign-out-alt"></i>
                    </div>
                    <div class="action-title">Đăng xuất</div>
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
    </main>

    <%@ include file="includes/footer.jspf" %>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script src="${pageContext.request.contextPath}/Js/search-suggest.js"></script>
    <script src="${pageContext.request.contextPath}/Js/notification.js"></script>
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

