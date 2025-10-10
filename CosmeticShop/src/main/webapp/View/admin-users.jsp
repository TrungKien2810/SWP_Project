<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                                <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/admin-dashboard">ADMIN DASHBOARD</a></li>
                                <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/products?action=manage">QUẢN LÝ SẢN PHẨM</a></li>
                                <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/admin-users.jsp">QUẢN LÝ NGƯỜI DÙNG</a></li>
                                <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/admin-reports.jsp">BÁO CÁO</a></li>
                                <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/lienhe.jsp">LIÊN HỆ</a></li>
                            </ul>
                            <div class="menu_search">
                                <div class="menu_search_input">
                                    <input type="text" placeholder="Tìm kiếm người dùng...">
                                </div>
                                <div class="menu_search_icon">
                                    <a href=""><i class="fa-solid fa-magnifying-glass fa-xl" style="color: #f76c85;"></i></a>
                                </div>
                            </div>
                            <div class="menu_search_cart">
                                <i class="fa-solid fa-crown cart-icon" style="color: #f76c85;"></i>
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
                    <p class="text-muted mb-0">Quản lý tài khoản người dùng và phân quyền</p>
                </div>
                <div class="col-md-6 text-end">
                    <button class="btn btn-primary">
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
                <div class="stat-number text-primary">1,234</div>
                <div class="stat-label">Tổng người dùng</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon text-success">
                    <i class="fas fa-user-check"></i>
                </div>
                <div class="stat-number text-success">1,180</div>
                <div class="stat-label">Người dùng hoạt động</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon text-warning">
                    <i class="fas fa-user-shield"></i>
                </div>
                <div class="stat-number text-warning">5</div>
                <div class="stat-label">Quản trị viên</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon text-info">
                    <i class="fas fa-user-plus"></i>
                </div>
                <div class="stat-number text-info">54</div>
                <div class="stat-label">Người dùng mới (tháng)</div>
            </div>
        </div>

        <!-- Search Box -->
        <div class="search-box">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="fas fa-search"></i>
                        </span>
                        <input type="text" class="form-control" placeholder="Tìm kiếm người dùng...">
                    </div>
                </div>
                <div class="col-md-6 text-end">
                    <div class="btn-group" role="group">
                        <button type="button" class="btn btn-outline-primary active">Tất cả</button>
                        <button type="button" class="btn btn-outline-primary">Admin</button>
                        <button type="button" class="btn btn-outline-primary">User</button>
                    </div>
                </div>
            </div>
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
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/default-avatar.png" 
                                     alt="Avatar" class="user-avatar me-3">
                                <div>
                                    <div class="fw-bold">Nguyễn Văn A</div>
                                    <small class="text-muted">ID: #1001</small>
                                </div>
                            </div>
                        </td>
                        <td>nguyenvana@gmail.com</td>
                        <td>
                            <span class="role-badge role-user">USER</span>
                        </td>
                        <td>15/11/2024</td>
                        <td>
                            <span class="badge bg-success">Hoạt động</span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-action btn-edit" title="Chỉnh sửa">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn-action btn-delete" title="Xóa">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/default-avatar.png" 
                                     alt="Avatar" class="user-avatar me-3">
                                <div>
                                    <div class="fw-bold">Trần Thị B</div>
                                    <small class="text-muted">ID: #1002</small>
                                </div>
                            </div>
                        </td>
                        <td>tranthib@gmail.com</td>
                        <td>
                            <span class="role-badge role-user">USER</span>
                        </td>
                        <td>20/11/2024</td>
                        <td>
                            <span class="badge bg-success">Hoạt động</span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-action btn-edit" title="Chỉnh sửa">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn-action btn-delete" title="Xóa">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/default-avatar.png" 
                                     alt="Avatar" class="user-avatar me-3">
                                <div>
                                    <div class="fw-bold">Admin User</div>
                                    <small class="text-muted">ID: #1003</small>
                                </div>
                            </div>
                        </td>
                        <td>admin@pinkycloud.com</td>
                        <td>
                            <span class="role-badge role-admin">ADMIN</span>
                        </td>
                        <td>01/12/2024</td>
                        <td>
                            <span class="badge bg-success">Hoạt động</span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-action btn-edit" title="Chỉnh sửa">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn-action btn-delete" title="Xóa" disabled>
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/default-avatar.png" 
                                     alt="Avatar" class="user-avatar me-3">
                                <div>
                                    <div class="fw-bold">Lê Văn C</div>
                                    <small class="text-muted">ID: #1004</small>
                                </div>
                            </div>
                        </td>
                        <td>levanc@gmail.com</td>
                        <td>
                            <span class="role-badge role-user">USER</span>
                        </td>
                        <td>25/11/2024</td>
                        <td>
                            <span class="badge bg-warning">Tạm khóa</span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-action btn-edit" title="Chỉnh sửa">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn-action btn-delete" title="Xóa">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/default-avatar.png" 
                                     alt="Avatar" class="user-avatar me-3">
                                <div>
                                    <div class="fw-bold">Phạm Thị D</div>
                                    <small class="text-muted">ID: #1005</small>
                                </div>
                            </div>
                        </td>
                        <td>phamthid@gmail.com</td>
                        <td>
                            <span class="role-badge role-user">USER</span>
                        </td>
                        <td>30/11/2024</td>
                        <td>
                            <span class="badge bg-success">Hoạt động</span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-action btn-edit" title="Chỉnh sửa">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn-action btn-delete" title="Xóa">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">
                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1">Trước</a>
                </li>
                <li class="page-item active">
                    <a class="page-link" href="#">1</a>
                </li>
                <li class="page-item">
                    <a class="page-link" href="#">2</a>
                </li>
                <li class="page-item">
                    <a class="page-link" href="#">3</a>
                </li>
                <li class="page-item">
                    <a class="page-link" href="#">Sau</a>
                </li>
            </ul>
        </nav>
    </div>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script>
        // Search functionality
        document.querySelector('input[type="text"]').addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const rows = document.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                if (text.includes(searchTerm)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
        
        // Filter buttons
        document.querySelectorAll('.btn-group button').forEach(btn => {
            btn.addEventListener('click', function() {
                // Remove active class from all buttons
                document.querySelectorAll('.btn-group button').forEach(b => b.classList.remove('active'));
                // Add active class to clicked button
                this.classList.add('active');
                
                // Filter logic here
                const filter = this.textContent.trim();
                const rows = document.querySelectorAll('tbody tr');
                
                rows.forEach(row => {
                    if (filter === 'Tất cả') {
                        row.style.display = '';
                    } else {
                        const role = row.querySelector('.role-badge').textContent;
                        if (role === filter) {
                            row.style.display = '';
                        } else {
                            row.style.display = 'none';
                        }
                    }
                });
            });
        });
        
        // Action buttons
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', function() {
                alert('Chức năng chỉnh sửa sẽ được phát triển');
            });
        });
        
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', function() {
                if (confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
                    alert('Chức năng xóa sẽ được phát triển');
                }
            });
        });
    </script>
</body>
</html>
