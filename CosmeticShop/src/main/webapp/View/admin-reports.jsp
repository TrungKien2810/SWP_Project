<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo cáo thống kê - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        
        .chart-container {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 30px;
        }
        
        .stats-grid {
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
        
        .stat-card.revenue {
            border-left-color: #28a745;
        }
        
        .stat-card.orders {
            border-left-color: #007bff;
        }
        
        .stat-card.users {
            border-left-color: #ffc107;
        }
        
        .stat-card.products {
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
        
        .stat-change {
            font-size: 0.8rem;
            margin-top: 5px;
        }
        
        .stat-change.positive {
            color: #28a745;
        }
        
        .stat-change.negative {
            color: #dc3545;
        }
        
        .chart-title {
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #333;
        }
        
        .chart-wrapper {
            position: relative;
            height: 300px;
        }
        
        .filter-section {
            background: white;
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 30px;
        }
        
        .filter-row {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }
        
        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        
        .filter-group label {
            font-weight: 600;
            color: #333;
            font-size: 0.9rem;
        }
        
        .btn-filter {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 10px 20px;
            border-radius: 25px;
            transition: all 0.3s ease;
        }
        
        .btn-filter:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }
        
        .table-container {
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
            
            .filter-row {
                flex-direction: column;
                align-items: stretch;
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
                    <h2><i class="fas fa-chart-bar"></i> Báo cáo thống kê</h2>
                    <p class="text-muted mb-0">Phân tích dữ liệu và hiệu suất kinh doanh</p>
                </div>
                <div class="col-md-6 text-end">
                    <button class="btn btn-success me-2">
                        <i class="fas fa-download"></i> Xuất báo cáo
                    </button>
                    <button class="btn btn-primary">
                        <i class="fas fa-refresh"></i> Làm mới
                    </button>
                </div>
            </div>
        </div>

        <!-- Filter Section -->
        <div class="filter-section">
            <h5 class="mb-3"><i class="fas fa-filter"></i> Bộ lọc báo cáo</h5>
            <div class="filter-row">
                <div class="filter-group">
                    <label>Khoảng thời gian</label>
                    <select class="form-select">
                        <option value="today">Hôm nay</option>
                        <option value="week">Tuần này</option>
                        <option value="month" selected>Tháng này</option>
                        <option value="quarter">Quý này</option>
                        <option value="year">Năm nay</option>
                        <option value="custom">Tùy chỉnh</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label>Từ ngày</label>
                    <input type="date" class="form-control" value="2024-12-01">
                </div>
                <div class="filter-group">
                    <label>Đến ngày</label>
                    <input type="date" class="form-control" value="2024-12-31">
                </div>
                <div class="filter-group">
                    <label>Loại báo cáo</label>
                    <select class="form-select">
                        <option value="all">Tất cả</option>
                        <option value="revenue">Doanh thu</option>
                        <option value="orders">Đơn hàng</option>
                        <option value="users">Người dùng</option>
                        <option value="products">Sản phẩm</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label>&nbsp;</label>
                    <button class="btn btn-filter">
                        <i class="fas fa-search"></i> Áp dụng
                    </button>
                </div>
            </div>
        </div>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card revenue">
                <div class="stat-icon text-success">
                    <i class="fas fa-dollar-sign"></i>
                </div>
                <div class="stat-number text-success">25.5M VNĐ</div>
                <div class="stat-label">Tổng doanh thu</div>
                <div class="stat-change positive">
                    <i class="fas fa-arrow-up"></i> +12.5% so với tháng trước
                </div>
            </div>
            <div class="stat-card orders">
                <div class="stat-icon text-primary">
                    <i class="fas fa-shopping-cart"></i>
                </div>
                <div class="stat-number text-primary">1,234</div>
                <div class="stat-label">Tổng đơn hàng</div>
                <div class="stat-change positive">
                    <i class="fas fa-arrow-up"></i> +8.3% so với tháng trước
                </div>
            </div>
            <div class="stat-card users">
                <div class="stat-icon text-warning">
                    <i class="fas fa-users"></i>
                </div>
                <div class="stat-number text-warning">567</div>
                <div class="stat-label">Người dùng mới</div>
                <div class="stat-change positive">
                    <i class="fas fa-arrow-up"></i> +15.2% so với tháng trước
                </div>
            </div>
            <div class="stat-card products">
                <div class="stat-icon text-danger">
                    <i class="fas fa-box"></i>
                </div>
                <div class="stat-number text-danger">89</div>
                <div class="stat-label">Sản phẩm bán chạy</div>
                <div class="stat-change negative">
                    <i class="fas fa-arrow-down"></i> -2.1% so với tháng trước
                </div>
            </div>
        </div>

        <!-- Charts Row -->
        <div class="row">
            <div class="col-lg-8 mb-4">
                <div class="chart-container">
                    <div class="chart-title">
                        <i class="fas fa-chart-line"></i> Biểu đồ doanh thu theo thời gian
                    </div>
                    <div class="chart-wrapper">
                        <canvas id="revenueChart"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 mb-4">
                <div class="chart-container">
                    <div class="chart-title">
                        <i class="fas fa-chart-pie"></i> Phân bố đơn hàng theo trạng thái
                    </div>
                    <div class="chart-wrapper">
                        <canvas id="orderStatusChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="chart-container">
                    <div class="chart-title">
                        <i class="fas fa-chart-bar"></i> Top 10 sản phẩm bán chạy
                    </div>
                    <div class="chart-wrapper">
                        <canvas id="topProductsChart"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-lg-6 mb-4">
                <div class="chart-container">
                    <div class="chart-title">
                        <i class="fas fa-chart-area"></i> Tăng trưởng người dùng
                    </div>
                    <div class="chart-wrapper">
                        <canvas id="userGrowthChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Top Products Table -->
        <div class="table-container">
            <div class="p-3">
                <h5 class="mb-3"><i class="fas fa-trophy"></i> Sản phẩm bán chạy nhất</h5>
            </div>
            <table class="table table-hover mb-0">
                <thead>
                    <tr>
                        <th>Hạng</th>
                        <th>Sản phẩm</th>
                        <th>Danh mục</th>
                        <th>Số lượng bán</th>
                        <th>Doanh thu</th>
                        <th>Tỷ lệ</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><span class="badge bg-warning fs-6">#1</span></td>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/anh1.png" 
                                     alt="Product" class="me-3" style="width: 40px; height: 40px; object-fit: cover; border-radius: 5px;">
                                <div>
                                    <div class="fw-bold">Son môi MAC Ruby Woo</div>
                                    <small class="text-muted">SKU: MAC001</small>
                                </div>
                            </div>
                        </td>
                        <td><span class="badge bg-primary">Môi</span></td>
                        <td class="fw-bold">245</td>
                        <td class="text-success fw-bold">110.25M VNĐ</td>
                        <td>
                            <div class="progress" style="height: 8px;">
                                <div class="progress-bar bg-success" style="width: 95%"></div>
                            </div>
                            <small class="text-muted">95%</small>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="badge bg-secondary fs-6">#2</span></td>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/anh2.png" 
                                     alt="Product" class="me-3" style="width: 40px; height: 40px; object-fit: cover; border-radius: 5px;">
                                <div>
                                    <div class="fw-bold">Kem nền Fenty Beauty</div>
                                    <small class="text-muted">SKU: FNT001</small>
                                </div>
                            </div>
                        </td>
                        <td><span class="badge bg-info">Mặt</span></td>
                        <td class="fw-bold">189</td>
                        <td class="text-success fw-bold">122.85M VNĐ</td>
                        <td>
                            <div class="progress" style="height: 8px;">
                                <div class="progress-bar bg-info" style="width: 78%"></div>
                            </div>
                            <small class="text-muted">78%</small>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="badge bg-success fs-6">#3</span></td>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/IMG/anh3.png" 
                                     alt="Product" class="me-3" style="width: 40px; height: 40px; object-fit: cover; border-radius: 5px;">
                                <div>
                                    <div class="fw-bold">Mascara Maybelline</div>
                                    <small class="text-muted">SKU: MAY001</small>
                                </div>
                            </div>
                        </td>
                        <td><span class="badge bg-warning">Mắt</span></td>
                        <td class="fw-bold">156</td>
                        <td class="text-success fw-bold">28.08M VNĐ</td>
                        <td>
                            <div class="progress" style="height: 8px;">
                                <div class="progress-bar bg-warning" style="width: 65%"></div>
                            </div>
                            <small class="text-muted">65%</small>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script>
        // Revenue Chart
        const revenueCtx = document.getElementById('revenueChart').getContext('2d');
        new Chart(revenueCtx, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'],
                datasets: [{
                    label: 'Doanh thu (triệu VNĐ)',
                    data: [12, 19, 3, 5, 2, 3, 15, 18, 22, 25, 28, 30],
                    borderColor: '#667eea',
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        // Order Status Chart
        const orderStatusCtx = document.getElementById('orderStatusChart').getContext('2d');
        new Chart(orderStatusCtx, {
            type: 'doughnut',
            data: {
                labels: ['Hoàn thành', 'Đang xử lý', 'Đã hủy', 'Chờ xác nhận'],
                datasets: [{
                    data: [65, 20, 10, 5],
                    backgroundColor: ['#28a745', '#ffc107', '#dc3545', '#17a2b8']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });

        // Top Products Chart
        const topProductsCtx = document.getElementById('topProductsChart').getContext('2d');
        new Chart(topProductsCtx, {
            type: 'bar',
            data: {
                labels: ['MAC Ruby Woo', 'Fenty Beauty', 'Maybelline', 'L\'Oreal', 'Revlon'],
                datasets: [{
                    label: 'Số lượng bán',
                    data: [245, 189, 156, 134, 98],
                    backgroundColor: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        // User Growth Chart
        const userGrowthCtx = document.getElementById('userGrowthChart').getContext('2d');
        new Chart(userGrowthCtx, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'],
                datasets: [{
                    label: 'Người dùng mới',
                    data: [45, 52, 38, 67, 89, 95, 78, 102, 115, 128, 145, 167],
                    borderColor: '#28a745',
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        // Filter functionality
        document.querySelector('.btn-filter').addEventListener('click', function() {
            alert('Chức năng lọc sẽ được phát triển');
        });

        // Export report
        document.querySelector('.btn-success').addEventListener('click', function() {
            alert('Chức năng xuất báo cáo sẽ được phát triển');
        });

        // Refresh data
        document.querySelector('.btn-primary').addEventListener('click', function() {
            location.reload();
        });
    </script>
</body>
</html>
