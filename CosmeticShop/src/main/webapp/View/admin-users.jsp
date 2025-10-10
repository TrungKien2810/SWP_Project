<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý người dùng - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        /* Admin specific styles */
        .admin-content {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .admin-header {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            margin-bottom: 30px;
            border-left: 5px solid #f76c85;
        }
        
        .sidebar .logo {
            padding: 20px;
            text-align: center;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        
        .sidebar .logo h4 {
            color: white;
            margin: 0;
            font-weight: 600;
        }
        
        .sidebar .nav-link {
            color: rgba(255,255,255,0.8);
            padding: 15px 20px;
            border: none;
            text-decoration: none;
            display: block;
            transition: all 0.3s ease;
        }
        
        .sidebar .nav-link:hover {
            background-color: rgba(255,255,255,0.1);
            color: white;
            transform: translateX(5px);
        }
        
        .sidebar .nav-link.active {
            background-color: rgba(255,255,255,0.2);
            color: white;
        }
        
        .main-content {
            margin-left: 250px;
            padding: 20px;
        }
        
        .page-header {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .users-table {
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        
        .table thead th {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            font-weight: 600;
        }
        
        .table tbody tr {
            transition: all 0.3s ease;
        }
        
        .table tbody tr:hover {
            background-color: #f8f9fa;
            transform: scale(1.01);
        }
        
        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
        }
        
        .role-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
        }
        
        .role-admin {
            background-color: #dc3545;
            color: white;
        }
        
        .role-user {
            background-color: #28a745;
            color: white;
        }
        
        .action-buttons {
            display: flex;
            gap: 5px;
        }
        
        .btn-action {
            padding: 5px 10px;
            border: none;
            border-radius: 5px;
            font-size: 0.8rem;
            transition: all 0.3s ease;
        }
        
        .btn-edit {
            background-color: #007bff;
            color: white;
        }
        
        .btn-edit:hover {
            background-color: #0056b3;
            transform: scale(1.05);
        }
        
        .btn-delete {
            background-color: #dc3545;
            color: white;
        }
        
        .btn-delete:hover {
            background-color: #c82333;
            transform: scale(1.05);
        }
        
        .search-box {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        
        .stats-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            transition: transform 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
        }
        
        .stat-icon {
            font-size: 2rem;
            margin-bottom: 10px;
        }
        
        .stat-number {
            font-size: 1.5rem;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .stat-label {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        @media (max-width: 768px) {
            .sidebar {
                transform: translateX(-100%);
                transition: transform 0.3s ease;
            }
            
            .sidebar.show {
                transform: translateX(0);
            }
            
            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
    <!-- header -->
    <div class="header">
        <div class="header_text"><p>ADMIN PANEL</p></div>
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
                                        <li class="dropdown">
                                            <a class="menu_list_link dropdown-toggle" href="#" id="adminDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                                <i class="fas fa-crown me-1"></i>ADMIN DASHBOARD
                                            </a>
                                            <ul class="dropdown-menu" aria-labelledby="adminDropdown">
                                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin-dashboard">
                                                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                                                </a></li>
                                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/products?action=manage">
                                                    <i class="fas fa-box me-2"></i>Quản lý sản phẩm
                                                </a></li>
                                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin-users">
                                                    <i class="fas fa-users me-2"></i>Quản lý người dùng
                                                </a></li>
                                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/View/admin-reports.jsp">
                                                    <i class="fas fa-chart-line me-2"></i>Báo cáo
                                                </a></li>
                                            </ul>
                                        </li>
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
                                    <i class="fa-solid fa-cart-shopping cart-icon"></i>
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

    <!-- Admin Content -->
    <div class="admin-content">
        <!-- Header -->
        <div class="page-header">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h2><i class="fas fa-users"></i> Quản lý người dùng</h2>
                    <p class="text-muted mb-0">
                        Quản lý tài khoản người dùng và phân quyền
                        <c:if test="${totalPages > 1}">
                            | Trang ${currentPage}/${totalPages}
                        </c:if>
                    </p>
                </div>
                <div class="col-md-6 text-end">
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createUserModal">
                        <i class="fas fa-plus"></i> Thêm người dùng
                    </button>
                </div>
            </div>
        </div>

        <!-- Stats Cards -->
        <div class="stats-cards">
            <div class="stat-card">
                <div class="stat-icon text-primary">
                    <i class="fas fa-users"></i>
                </div>
                <div class="stat-number text-primary">${totalUsers}</div>
                <div class="stat-label">Tổng người dùng</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon text-success">
                    <i class="fas fa-user-check"></i>
                </div>
                <div class="stat-number text-success">${activeUsers}</div>
                <div class="stat-label">Người dùng hoạt động</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon text-warning">
                    <i class="fas fa-user-shield"></i>
                </div>
                <div class="stat-number text-warning">${adminUsers}</div>
                <div class="stat-label">Quản trị viên</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon text-info">
                    <i class="fas fa-user-plus"></i>
                </div>
                <div class="stat-number text-info">${newUsers}</div>
                <div class="stat-label">Người dùng mới (tháng)</div>
            </div>
        </div>

        <!-- Search Box -->
        <div class="search-box">
            <form method="GET" action="${pageContext.request.contextPath}/admin-users">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="fas fa-search"></i>
                        </span>
                            <input type="text" class="form-control" name="search" 
                                   placeholder="Tìm kiếm người dùng..." value="${searchTerm}">
                    </div>
                </div>
                <div class="col-md-6 text-end">
                    <div class="btn-group" role="group">
                            <a href="${pageContext.request.contextPath}/admin-users" 
                               class="btn btn-outline-primary ${empty selectedRole || selectedRole == 'all' ? 'active' : ''}">Tất cả</a>
                            <a href="${pageContext.request.contextPath}/admin-users?role=ADMIN" 
                               class="btn btn-outline-primary ${selectedRole == 'ADMIN' ? 'active' : ''}">Admin</a>
                            <a href="${pageContext.request.contextPath}/admin-users?role=USER" 
                               class="btn btn-outline-primary ${selectedRole == 'USER' ? 'active' : ''}">User</a>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <!-- Users Table -->
        <div class="users-table">
            <table class="table table-hover mb-0">
                <thead>
                    <tr>
                        <th>Người dùng</th>
                        <th>Email</th>
                        <th>Vai trò</th>
                        <th>Ngày tạo</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty users}">
                            <tr>
                                <td colspan="6" class="text-center py-4">
                                    <i class="fas fa-users fa-3x text-muted mb-3"></i>
                                    <h5 class="text-muted">Không có người dùng nào</h5>
                                    <p class="text-muted">Hãy thử tìm kiếm với từ khóa khác hoặc thay đổi bộ lọc</p>
                        </td>
                    </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="user" items="${users}">
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/default-avatar.png" 
                                     alt="Avatar" class="user-avatar me-3">
                                <div>
                                                <div class="fw-bold">${user.username}</div>
                                                <small class="text-muted">ID: #${user.user_id}</small>
                                </div>
                            </div>
                        </td>
                                    <td>${user.email}</td>
                        <td>
                                        <span class="role-badge ${user.role == 'ADMIN' ? 'role-admin' : 'role-user'}">
                                            ${user.role}
                                        </span>
                        </td>
                        <td>
                                        <span class="date-display" data-date="${user.date_create}">
                                            <c:out value="${user.date_create}"/>
                                        </span>
                        </td>
                                    <td>
                                        <span class="badge bg-success">Hoạt động</span>
                                    </td>
                        <td>
                            <div class="action-buttons">
                                            <button class="btn-action btn-edit" title="Chỉnh sửa" 
                                                    onclick="editUser(${user.user_id}, '${user.username}', '${user.email}', '${user.phone}', '${user.role}')">
                                    <i class="fas fa-edit"></i>
                                </button>
                                            <c:if test="${user.user_id != sessionScope.user.user_id}">
                                                <button class="btn-action btn-delete" title="Xóa" 
                                                        onclick="deleteUser(${user.user_id}, '${user.username}')">
                                    <i class="fas fa-trash"></i>
                                </button>
                                            </c:if>
                            </div>
                        </td>
                    </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation" class="mt-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-muted">
                        Hiển thị ${(currentPage - 1) * pageSize + 1} - ${currentPage * pageSize > totalUsersInCurrentView ? totalUsersInCurrentView : currentPage * pageSize} 
                        trong tổng số ${totalUsersInCurrentView} người dùng
                    </div>
                    <ul class="pagination mb-0">
                        <!-- Previous button -->
                        <c:choose>
                            <c:when test="${currentPage > 1}">
                                <li class="page-item">
                                    <a class="page-link" href="${pageContext.request.contextPath}/admin-users?page=${currentPage - 1}&search=${searchTerm}&role=${selectedRole}">
                                        <i class="fas fa-chevron-left"></i> Trước
                                    </a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="page-item disabled">
                                    <span class="page-link">
                                        <i class="fas fa-chevron-left"></i> Trước
                                    </span>
                                </li>
                            </c:otherwise>
                        </c:choose>
                        
                        <!-- Page numbers -->
                        <c:set var="startPage" value="${currentPage - 2}"/>
                        <c:set var="endPage" value="${currentPage + 2}"/>
                        
                        <c:if test="${startPage < 1}">
                            <c:set var="startPage" value="1"/>
                        </c:if>
                        <c:if test="${endPage > totalPages}">
                            <c:set var="endPage" value="${totalPages}"/>
                        </c:if>
                        
                        <c:if test="${startPage > 1}">
                            <li class="page-item">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin-users?page=1&search=${searchTerm}&role=${selectedRole}">1</a>
                            </li>
                            <c:if test="${startPage > 2}">
                <li class="page-item disabled">
                                    <span class="page-link">...</span>
                </li>
                            </c:if>
                        </c:if>
                        
                        <c:forEach var="i" begin="${startPage}" end="${endPage}">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                <li class="page-item active">
                                        <span class="page-link">${i}</span>
                </li>
                                </c:when>
                                <c:otherwise>
                <li class="page-item">
                                        <a class="page-link" href="${pageContext.request.contextPath}/admin-users?page=${i}&search=${searchTerm}&role=${selectedRole}">${i}</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        
                        <c:if test="${endPage < totalPages}">
                            <c:if test="${endPage < totalPages - 1}">
                                <li class="page-item disabled">
                                    <span class="page-link">...</span>
                </li>
                            </c:if>
                <li class="page-item">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin-users?page=${totalPages}&search=${searchTerm}&role=${selectedRole}">${totalPages}</a>
                </li>
                        </c:if>
                        
                        <!-- Next button -->
                        <c:choose>
                            <c:when test="${currentPage < totalPages}">
                <li class="page-item">
                                    <a class="page-link" href="${pageContext.request.contextPath}/admin-users?page=${currentPage + 1}&search=${searchTerm}&role=${selectedRole}">
                                        Sau <i class="fas fa-chevron-right"></i>
                                    </a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="page-item disabled">
                                    <span class="page-link">
                                        Sau <i class="fas fa-chevron-right"></i>
                                    </span>
                </li>
                            </c:otherwise>
                        </c:choose>
            </ul>
                </div>
        </nav>
        </c:if>
    </div>

    <!-- Edit User Modal -->
    <div class="modal fade" id="editUserModal" tabindex="-1" aria-labelledby="editUserModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editUserModalLabel">Chỉnh sửa người dùng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="POST" action="${pageContext.request.contextPath}/admin-users">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="updateUser">
                        <input type="hidden" name="userId" id="editUserId">
                        
                        <div class="mb-3">
                            <label for="editFullName" class="form-label">Họ và tên</label>
                            <input type="text" class="form-control" id="editFullName" name="fullName" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="editEmail" class="form-label">Email</label>
                            <input type="email" class="form-control" id="editEmail" name="email" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="editPhone" class="form-label">Số điện thoại</label>
                            <input type="text" class="form-control" id="editPhone" name="phone">
                        </div>
                        
                        <div class="mb-3">
                            <label for="editRole" class="form-label">Vai trò</label>
                            <select class="form-select" id="editRole" name="role" required>
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Cập nhật</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteUserModal" tabindex="-1" aria-labelledby="deleteUserModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteUserModalLabel">Xác nhận xóa</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Bạn có chắc chắn muốn xóa người dùng <strong id="deleteUserName"></strong>?</p>
                    <p class="text-danger"><small>Hành động này không thể hoàn tác!</small></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <form method="POST" action="${pageContext.request.contextPath}/admin-users" style="display: inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="userId" id="deleteUserId">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Create User Modal -->
    <div class="modal fade" id="createUserModal" tabindex="-1" aria-labelledby="createUserModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="createUserModalLabel">Thêm người dùng mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="POST" action="${pageContext.request.contextPath}/admin-users" id="createUserForm">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="createUser">
                        
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="createUsername" class="form-label">Họ và tên <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="createUsername" name="username" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="createEmail" class="form-label">Email <span class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="createEmail" name="email" 
                                           placeholder="example@gmail.com" pattern="[a-zA-Z0-9._%+-]+@gmail\.com" required>
                                    <div class="form-text">Chỉ chấp nhận email Gmail</div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="createPhone" class="form-label">Số điện thoại</label>
                                    <input type="text" class="form-control" id="createPhone" name="phone" 
                                           placeholder="0123456789">
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="createRole" class="form-label">Vai trò <span class="text-danger">*</span></label>
                                    <select class="form-select" id="createRole" name="role" required>
                                        <option value="USER">USER</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="createPassword" class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" id="createPassword" name="password" 
                                           minlength="6" required>
                                    <div class="form-text">Tối thiểu 6 ký tự</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="createConfirmPassword" class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" id="createConfirmPassword" name="confirmPassword" 
                                           minlength="6" required>
                                </div>
                            </div>
                        </div>
                        
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle"></i>
                            <strong>Lưu ý:</strong> Người dùng mới sẽ có thể đăng nhập ngay sau khi được tạo.
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Tạo người dùng
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script>
        // Edit user function
        function editUser(userId, username, email, phone, role) {
            document.getElementById('editUserId').value = userId;
            document.getElementById('editFullName').value = username;
            document.getElementById('editEmail').value = email;
            document.getElementById('editPhone').value = phone || '';
            document.getElementById('editRole').value = role;
            
            const editModal = new bootstrap.Modal(document.getElementById('editUserModal'));
            editModal.show();
        }
        
        // Delete user function
        function deleteUser(userId, username) {
            document.getElementById('deleteUserId').value = userId;
            document.getElementById('deleteUserName').textContent = username;
            
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
            deleteModal.show();
        }
        
        // Format dates
        document.querySelectorAll('.date-display').forEach(function(element) {
            const dateStr = element.getAttribute('data-date');
            if (dateStr) {
                try {
                    // Parse LocalDateTime string and format it
                    const date = new Date(dateStr);
                    const day = String(date.getDate()).padStart(2, '0');
                    const month = String(date.getMonth() + 1).padStart(2, '0');
                    const year = date.getFullYear();
                    element.textContent = day + '/' + month + '/' + year;
                } catch (e) {
                    // If parsing fails, keep original text
                    console.log('Date parsing failed for:', dateStr);
                }
            }
        });
        
        // Auto-submit search form on input
        document.querySelector('input[name="search"]').addEventListener('input', function(e) {
            if (e.target.value.length > 2 || e.target.value.length === 0) {
                e.target.form.submit();
            }
        });
        
        // Create user form validation
        document.getElementById('createUserForm').addEventListener('submit', function(e) {
            const username = document.getElementById('createUsername').value.trim();
            const email = document.getElementById('createEmail').value.trim();
            const password = document.getElementById('createPassword').value;
            const confirmPassword = document.getElementById('createConfirmPassword').value;
            
            // Clear previous errors
            document.querySelectorAll('.form-control').forEach(input => {
                input.classList.remove('is-invalid');
            });
            document.querySelectorAll('.invalid-feedback').forEach(feedback => {
                feedback.remove();
            });
            
            let hasError = false;
            
            // Validate username
            if (username.length < 2) {
                showFieldError('createUsername', 'Họ tên phải có ít nhất 2 ký tự');
                hasError = true;
            }
            
            // Validate email
            const emailPattern = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
            if (!emailPattern.test(email)) {
                showFieldError('createEmail', 'Email phải có định dạng Gmail (xxx@gmail.com)');
                hasError = true;
            }
            
            // Validate password
            if (password.length < 6) {
                showFieldError('createPassword', 'Mật khẩu phải có ít nhất 6 ký tự');
                hasError = true;
            }
            
            // Validate confirm password
            if (password !== confirmPassword) {
                showFieldError('createConfirmPassword', 'Mật khẩu xác nhận không khớp');
                hasError = true;
            }
            
            if (hasError) {
                e.preventDefault();
                return false;
            }
        });
        
        // Real-time password confirmation validation
        document.getElementById('createConfirmPassword').addEventListener('input', function() {
            const password = document.getElementById('createPassword').value;
            const confirmPassword = this.value;
            
            if (confirmPassword && password !== confirmPassword) {
                this.classList.add('is-invalid');
                if (!this.nextElementSibling || !this.nextElementSibling.classList.contains('invalid-feedback')) {
                    const feedback = document.createElement('div');
                    feedback.className = 'invalid-feedback';
                    feedback.textContent = 'Mật khẩu xác nhận không khớp';
                    this.parentNode.appendChild(feedback);
                }
            } else {
                this.classList.remove('is-invalid');
                const feedback = this.parentNode.querySelector('.invalid-feedback');
                if (feedback) {
                    feedback.remove();
                }
            }
        });
        
        function showFieldError(fieldId, message) {
            const field = document.getElementById(fieldId);
            field.classList.add('is-invalid');
            
            const feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            feedback.textContent = message;
            field.parentNode.appendChild(feedback);
        }
        
        // Show success/error messages
        <c:if test="${not empty success}">
            showAlert('<c:out value="${success}" escapeXml="true"/>', 'success');
        </c:if>
        
        <c:if test="${not empty error}">
            showAlert('<c:out value="${error}" escapeXml="true"/>', 'danger');
        </c:if>
        
        function showAlert(message, type) {
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-' + type + ' alert-dismissible fade show position-fixed';
            alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
            alertDiv.innerHTML = 
                message +
                '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';
            document.body.appendChild(alertDiv);
            
            // Auto remove after 5 seconds
            setTimeout(function() {
                if (alertDiv.parentNode) {
                    alertDiv.parentNode.removeChild(alertDiv);
                }
            }, 5000);
        }
    </script>
</body>
</html>
