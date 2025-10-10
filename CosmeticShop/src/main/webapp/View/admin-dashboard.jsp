<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Pinky Cloud</title>
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
        
        .admin-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            transition: transform 0.3s ease;
            border-left: 4px solid;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
        }
        
        .stat-card.users {
            border-left-color: #f76c85;
        }
        
        .stat-card.products {
            border-left-color: #28a745;
        }
        
        .stat-card.orders {
            border-left-color: #ffc107;
        }
        
        .stat-card.revenue {
            border-left-color: #dc3545;
        }
        
        .stat-icon {
            font-size: 2.5rem;
            margin-bottom: 15px;
        }
        
        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .stat-label {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .admin-quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .quick-action-card {
            background: white;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            transition: all 0.3s ease;
            cursor: pointer;
            border: 2px solid transparent;
        }
        
        .quick-action-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
            border-color: #f76c85;
        }
        
        .quick-action-icon {
            font-size: 2rem;
            margin-bottom: 15px;
            color: #f76c85;
        }
        
        .admin-charts {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 30px;
        }
        
        .chart-title {
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #333;
            border-bottom: 2px solid #f76c85;
            padding-bottom: 10px;
        }
        
        .admin-activity {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }
        
        .activity-item {
            display: flex;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #f1f3f4;
        }
        
        .activity-item:last-child {
            border-bottom: none;
        }
        
        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 1.2rem;
        }
        
        .activity-icon.new-user {
            background-color: #e3f2fd;
            color: #1976d2;
        }
        
        .activity-icon.new-product {
            background-color: #e8f5e8;
            color: #388e3c;
        }
        
        .activity-icon.order {
            background-color: #fff3e0;
            color: #f57c00;
        }
        
        .btn-admin {
            background: linear-gradient(135deg, #f76c85 0%, #ff9a9e 100%);
            border: none;
            color: white;
            padding: 10px 20px;
            border-radius: 25px;
            transition: all 0.3s ease;
        }
        
        .btn-admin:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(247, 108, 133, 0.4);
            color: white;
        }
        
        .dashboard-header {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .stats-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            border-left: 4px solid;
        }
        
        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        
        .stats-card.users {
            border-left-color: #28a745;
        }
        
        .stats-card.products {
            border-left-color: #007bff;
        }
        
        .stats-card.orders {
            border-left-color: #ffc107;
        }
        
        .stats-card.revenue {
            border-left-color: #dc3545;
        }
        
        .stats-icon {
            font-size: 2.5rem;
            margin-bottom: 15px;
        }
        
        .stats-number {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .stats-label {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .chart-container {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 30px;
        }
        
        .recent-activity {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }
        
        .activity-item {
            display: flex;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #f1f3f4;
        }
        
        .activity-item:last-child {
            border-bottom: none;
        }
        
        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 1.2rem;
        }
        
        .activity-icon.new-user {
            background-color: #e3f2fd;
            color: #1976d2;
        }
        
        .activity-icon.new-product {
            background-color: #e8f5e8;
            color: #388e3c;
        }
        
        .activity-icon.order {
            background-color: #fff3e0;
            color: #f57c00;
        }
        
        .btn-admin {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 10px 20px;
            border-radius: 25px;
            transition: all 0.3s ease;
        }
        
        .btn-admin:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }
        
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .quick-action-card {
            background: white;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            transition: all 0.3s ease;
            cursor: pointer;
        }
        
        .quick-action-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        
        .quick-action-icon {
            font-size: 2rem;
            margin-bottom: 15px;
            color: #667eea;
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
                                    <input type="text" placeholder="Tìm kiếm trong admin...">
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
        <!-- Admin Header -->
        <div class="admin-header">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h2><i class="fas fa-crown" style="color: #f76c85;"></i> Admin Dashboard</h2>
                    <p class="text-muted mb-0">Chào mừng trở lại, Admin!</p>
                </div>
                <div class="col-md-6 text-end">
                    <c:if test="${not empty sessionScope.user}">
                        <span class="badge" style="background: linear-gradient(135deg, #f76c85 0%, #ff9a9e 100%); font-size: 1rem; padding: 10px 15px;">
                            <i class="fas fa-user-shield"></i> ${sessionScope.user.username}
                        </span>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="admin-quick-actions">
            <div class="quick-action-card" onclick="location.href='${pageContext.request.contextPath}/products?action=new'">
                <div class="quick-action-icon">
                    <i class="fas fa-plus-circle"></i>
                </div>
                <h5>Thêm sản phẩm</h5>
                <p class="text-muted">Tạo sản phẩm mới</p>
            </div>
            <div class="quick-action-card" onclick="location.href='${pageContext.request.contextPath}/products?action=manage'">
                <div class="quick-action-icon">
                    <i class="fas fa-edit"></i>
                </div>
                <h5>Quản lý sản phẩm</h5>
                <p class="text-muted">Chỉnh sửa sản phẩm</p>
            </div>
            <div class="quick-action-card" onclick="location.href='#users'">
                <div class="quick-action-icon">
                    <i class="fas fa-user-plus"></i>
                </div>
                <h5>Quản lý người dùng</h5>
                <p class="text-muted">Xem danh sách user</p>
            </div>
            <div class="quick-action-card" onclick="location.href='#reports'">
                <div class="quick-action-icon">
                    <i class="fas fa-chart-line"></i>
                </div>
                <h5>Báo cáo thống kê</h5>
                <p class="text-muted">Xem báo cáo chi tiết</p>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="admin-stats">
            <div class="stat-card users">
                <div class="d-flex align-items-center">
                    <div class="stat-icon" style="color: #f76c85;">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="ms-3">
                        <div class="stat-number" style="color: #f76c85;">1,234</div>
                        <div class="stat-label">Tổng người dùng</div>
                    </div>
                </div>
            </div>
            <div class="stat-card products">
                <div class="d-flex align-items-center">
                    <div class="stat-icon" style="color: #28a745;">
                        <i class="fas fa-box"></i>
                    </div>
                    <div class="ms-3">
                        <div class="stat-number" style="color: #28a745;">567</div>
                        <div class="stat-label">Sản phẩm</div>
                    </div>
                </div>
            </div>
            <div class="stat-card orders">
                <div class="d-flex align-items-center">
                    <div class="stat-icon" style="color: #ffc107;">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="ms-3">
                        <div class="stat-number" style="color: #ffc107;">89</div>
                        <div class="stat-label">Đơn hàng hôm nay</div>
                    </div>
                </div>
            </div>
            <div class="stat-card revenue">
                <div class="d-flex align-items-center">
                    <div class="stat-icon" style="color: #dc3545;">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="ms-3">
                        <div class="stat-number" style="color: #dc3545;">12.5M</div>
                        <div class="stat-label">Doanh thu tháng</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts and Recent Activity -->
        <div class="row">
            <div class="col-lg-8 mb-4">
                <div class="admin-charts">
                    <div class="chart-title">
                        <i class="fas fa-chart-line"></i> Biểu đồ doanh thu
                    </div>
                    <div class="text-center py-5">
                        <i class="fas fa-chart-area fa-3x text-muted mb-3" style="color: #f76c85;"></i>
                        <p class="text-muted">Biểu đồ sẽ được tích hợp sau</p>
                        <button class="btn btn-admin">Xem chi tiết</button>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 mb-4">
                <div class="admin-activity">
                    <div class="chart-title">
                        <i class="fas fa-clock"></i> Hoạt động gần đây
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon new-user">
                            <i class="fas fa-user-plus"></i>
                        </div>
                        <div>
                            <div class="fw-bold">Người dùng mới</div>
                            <div class="text-muted small">Nguyễn Văn A đã đăng ký</div>
                            <div class="text-muted small">2 phút trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon new-product">
                            <i class="fas fa-box"></i>
                        </div>
                        <div>
                            <div class="fw-bold">Sản phẩm mới</div>
                            <div class="text-muted small">Son môi MAC Ruby Woo</div>
                            <div class="text-muted small">15 phút trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon order">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div>
                            <div class="fw-bold">Đơn hàng mới</div>
                            <div class="text-muted small">Đơn hàng #12345</div>
                            <div class="text-muted small">1 giờ trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon new-user">
                            <i class="fas fa-user-edit"></i>
                        </div>
                        <div>
                            <div class="fw-bold">Cập nhật thông tin</div>
                            <div class="text-muted small">Trần Thị B đã cập nhật profile</div>
                            <div class="text-muted small">2 giờ trước</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Additional Info Cards -->
        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="admin-charts">
                    <div class="chart-title">
                        <i class="fas fa-star"></i> Sản phẩm bán chạy
                    </div>
                    <div class="list-group list-group-flush">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="mb-1">Son môi MAC Ruby Woo</h6>
                                <small class="text-muted">Đã bán: 45 sản phẩm</small>
                            </div>
                            <span class="badge bg-primary rounded-pill">#1</span>
                        </div>
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="mb-1">Kem nền Fenty Beauty</h6>
                                <small class="text-muted">Đã bán: 32 sản phẩm</small>
                            </div>
                            <span class="badge bg-secondary rounded-pill">#2</span>
                        </div>
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="mb-1">Mascara Maybelline</h6>
                                <small class="text-muted">Đã bán: 28 sản phẩm</small>
                            </div>
                            <span class="badge bg-success rounded-pill">#3</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-6 mb-4">
                <div class="admin-charts">
                    <div class="chart-title">
                        <i class="fas fa-bell"></i> Thông báo hệ thống
                    </div>
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Hệ thống đang hoạt động bình thường
                    </div>
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle"></i> 5 sản phẩm sắp hết hàng
                    </div>
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i> Backup dữ liệu thành công
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script>
        // Auto-refresh stats (optional)
        setInterval(function() {
            // You can add AJAX calls here to refresh stats
            console.log('Refreshing admin stats...');
        }, 30000); // Refresh every 30 seconds
        
        // Admin specific functionality
        document.addEventListener('DOMContentLoaded', function() {
            console.log('Admin Dashboard loaded');
        });
    </script>
</body>
</html>
