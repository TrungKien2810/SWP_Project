<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/admin/includes/header.jspf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/Css/reports.css">
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<div class="container-fluid">
  <h4 class="mb-4"><i class="fa fa-chart-line"></i> Báo Cáo & Thống Kê</h4>

  <!-- BỘ LỌC -->
  <div class="filter-section">
    <form method="GET" action="${pageContext.request.contextPath}/admin" id="filterForm">
      <input type="hidden" name="action" value="reports">
      <div class="row">
        <div class="col-md-3 col-lg-2">
          <label class="form-label">Chọn nhanh</label>
          <select name="dateQuickFilter" class="form-select" id="dateQuickFilter">
            <option value="">-- Chọn --</option>
            <option value="all" ${dateQuickFilter == 'all' ? 'selected' : ''}>Tất cả</option>
            <option value="today" ${dateQuickFilter == 'today' ? 'selected' : ''}>Hôm nay</option>
            <option value="last7days" ${dateQuickFilter == 'last7days' ? 'selected' : ''}>7 ngày qua</option>
            <option value="thisMonth" ${dateQuickFilter == 'thisMonth' ? 'selected' : ''}>Tháng này</option>
            <option value="thisYear" ${dateQuickFilter == 'thisYear' ? 'selected' : ''}>Năm nay</option>
          </select>
        </div>
        <div class="col-md-3 col-lg-2">
          <label class="form-label">Từ ngày</label>
          <input type="date" name="startDate" class="form-control" value="${startDate}" id="startDate">
        </div>
        <div class="col-md-3 col-lg-2">
          <label class="form-label">Đến ngày</label>
          <input type="date" name="endDate" class="form-control" value="${endDate}" id="endDate">
        </div>
        <div class="col-md-3 col-lg-2">
          <label class="form-label">Trạng thái</label>
          <select name="orderStatus" class="form-select">
            <option value="ALL" ${orderStatusFilter == 'ALL' ? 'selected' : ''}>Tất cả</option>
            <option value="COMPLETED" ${orderStatusFilter == 'COMPLETED' ? 'selected' : ''}>Đã hoàn thành</option>
            <option value="SHIPPING" ${orderStatusFilter == 'SHIPPING' ? 'selected' : ''}>Đang giao</option>
            <option value="CONFIRMED" ${orderStatusFilter == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
            <option value="PENDING" ${orderStatusFilter == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
            <option value="CANCELLED" ${orderStatusFilter == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
          </select>
        </div>
        <div class="col-md-12 col-lg-4 filter-actions">
          <button type="submit" class="btn btn-primary"><i class="fa fa-search"></i> Lọc dữ liệu</button>
          <button type="button" class="btn btn-secondary" onclick="resetFilters()"><i class="fa fa-redo"></i> Đặt lại</button>
        </div>
      </div>
    </form>
  </div>

  <c:set var="data" value="${reportData}" />
  <c:set var="hasFilter" value="${hasDateFilter}" />

  <c:if test="${not hasFilter}">
    <div class="alert alert-info mb-3">
      <i class="fa fa-info-circle"></i> <strong>Chưa chọn bộ lọc!</strong> Vui lòng chọn khoảng thời gian để xem báo cáo.
    </div>
  </c:if>

  <!-- THỐNG KÊ TỔNG QUAN -->
  <div class="row">
    <div class="col-md-3">
      <div class="stat-card">
        <div class="stat-label">Tổng Doanh Thu</div>
        <div class="stat-value">
          <c:choose>
            <c:when test="${not empty data.totalRevenue}">
              ${String.format("%,.0f", data.totalRevenue)} đ
            </c:when>
            <c:otherwise>0 đ</c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card blue">
        <div class="stat-label">Tổng Số Đơn Hàng</div>
        <div class="stat-value">${data.totalOrders != null ? data.totalOrders : 0}</div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card green">
        <div class="stat-label">Giá Trị Đơn Trung Bình (AOV)</div>
        <div class="stat-value">
          <c:choose>
            <c:when test="${not empty data.averageOrderValue}">
              ${String.format("%,.0f", data.averageOrderValue)} đ
            </c:when>
            <c:otherwise>0 đ</c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card orange">
        <div class="stat-label">Tổng Giảm Giá</div>
        <div class="stat-value">
          <c:choose>
            <c:when test="${not empty data.totalDiscount}">
              ${String.format("%,.0f", data.totalDiscount)} đ
            </c:when>
            <c:otherwise>0 đ</c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>

  <!-- BIỂU ĐỒ DOANH THU THEO NGÀY -->
  <div class="report-card">
    <h5 class="mb-3"><i class="fa fa-chart-area"></i> Biểu Đồ Doanh Thu Theo Ngày</h5>
    <div class="chart-container">
      <canvas id="salesChart"></canvas>
    </div>
  </div>

  <div class="row">
    <!-- TOP SẢN PHẨM BÁN CHẠY (THEO SỐ LƯỢNG) -->
    <div class="col-md-6">
      <div class="report-card">
        <h5 class="mb-3"><i class="fa fa-box"></i> Top Sản Phẩm Bán Chạy (Theo Số Lượng)</h5>
        <div class="chart-container" style="height: 300px;">
          <canvas id="topProductsQuantityChart"></canvas>
        </div>
      </div>
    </div>

    <!-- TOP SẢN PHẨM DOANH THU CAO -->
    <div class="col-md-6">
      <div class="report-card">
        <h5 class="mb-3"><i class="fa fa-dollar-sign"></i> Top Sản Phẩm Doanh Thu Cao</h5>
        <div class="chart-container" style="height: 300px;">
          <canvas id="topProductsRevenueChart"></canvas>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <!-- BÁO CÁO TỒN KHO -->
    <div class="col-md-6">
      <div class="report-card">
        <h5 class="mb-3"><i class="fa fa-exclamation-triangle"></i> Sản Phẩm Sắp Hết Hàng</h5>
        <div class="table-responsive">
          <table class="table table-sm table-hover">
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên Sản Phẩm</th>
                <th>Tồn Kho</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="product" items="${data.lowStockProducts}">
                <tr>
                  <td>${product.productId}</td>
                  <td>${product.name}</td>
                  <td><span class="badge bg-danger">${product.stock}</span></td>
                </tr>
              </c:forEach>
              <c:if test="${empty data.lowStockProducts}">
                <tr><td colspan="3" class="text-center text-muted">Không có sản phẩm nào sắp hết hàng</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- SẢN PHẨM TỒN KHO LÂU -->
    <div class="col-md-6">
      <div class="report-card">
        <h5 class="mb-3"><i class="fa fa-clock"></i> Sản Phẩm Tồn Kho Lâu (90+ ngày không bán)</h5>
        <div class="table-responsive">
          <table class="table table-sm table-hover">
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên Sản Phẩm</th>
                <th>Tồn Kho</th>
                <th>Lần bán cuối</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="product" items="${data.slowMovingProducts}">
                <tr>
                  <td>${product.productId}</td>
                  <td>${product.name}</td>
                  <td><span class="badge bg-warning">${product.stock}</span></td>
                  <td class="text-muted">${product.lastSoldDate}</td>
                </tr>
              </c:forEach>
              <c:if test="${empty data.slowMovingProducts}">
                <tr><td colspan="4" class="text-center text-muted">Không có sản phẩm tồn kho lâu</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <!-- TOP KHÁCH HÀNG -->
  <div class="report-card">
    <h5 class="mb-3"><i class="fa fa-users"></i> Top 10 Khách Hàng Thân Thiết</h5>
    <div class="table-responsive">
      <table class="table table-hover">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên</th>
            <th>Email</th>
            <th>Tổng Chi Tiêu</th>
            <th>Số Đơn</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="customer" items="${data.topCustomers}" varStatus="status">
            <tr>
              <td>${status.index + 1}</td>
              <td><strong>${customer.fullName}</strong></td>
              <td>${customer.email}</td>
              <td><strong class="text-success">${String.format("%,.0f", customer.totalSpent)} đ</strong></td>
              <td><span class="badge bg-primary">${customer.orderCount}</span></td>
            </tr>
          </c:forEach>
          <c:if test="${empty data.topCustomers}">
            <tr><td colspan="5" class="text-center text-muted">Chưa có dữ liệu</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>

  <div class="row">
    <!-- KHÁCH HÀNG MỚI VS QUAY LẠI -->
    <div class="col-md-6">
      <div class="report-card">
        <h5 class="mb-3"><i class="fa fa-chart-pie"></i> Khách Hàng Mới vs Quay Lại</h5>
        <div class="chart-container" style="height: 300px;">
          <canvas id="customerPieChart"></canvas>
        </div>
      </div>
    </div>

    <!-- TOP MÃ GIẢM GIÁ -->
    <div class="col-md-6">
      <div class="report-card">
        <h5 class="mb-3"><i class="fa fa-ticket"></i> Top Mã Giảm Giá</h5>
        <div class="table-responsive">
          <table class="table table-sm table-hover">
            <thead>
              <tr>
                <th>Mã</th>
                <th>Tên</th>
                <th>Số Lượt Dùng</th>
                <th>Tổng Giảm Giá</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="discount" items="${data.topDiscounts}">
                <tr>
                  <td><strong class="text-primary">${discount.code}</strong></td>
                  <td>${discount.name}</td>
                  <td><span class="badge bg-info">${discount.usageCount}</span></td>
                  <td class="text-success">${String.format("%,.0f", discount.totalDiscount)} đ</td>
                </tr>
              </c:forEach>
              <c:if test="${empty data.topDiscounts}">
                <tr><td colspan="4" class="text-center text-muted">Chưa có mã giảm giá nào được sử dụng</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  // Xử lý bộ lọc
  // Khi chọn date range thủ công, xóa quick filter
  document.getElementById('startDate').addEventListener('change', function() {
    document.getElementById('dateQuickFilter').value = '';
  });
  
  document.getElementById('endDate').addEventListener('change', function() {
    document.getElementById('dateQuickFilter').value = '';
  });
  
  function resetFilters() {
    window.location.href = '${pageContext.request.contextPath}/admin?action=reports';
  }

  // Dữ liệu từ server - Doanh thu theo ngày
  <c:choose>
    <c:when test="${not empty data.salesByDay}">
      const salesByDayLabels = [
        <c:forEach var="entry" items="${data.salesByDay}" varStatus="s">
          "${entry.key}"<c:if test="${!s.last}">, </c:if>
        </c:forEach>
      ];
      const salesByDayData = [
        <c:forEach var="entry" items="${data.salesByDay}" varStatus="s">
          ${entry.value}<c:if test="${!s.last}">, </c:if>
        </c:forEach>
      ];
    </c:when>
    <c:otherwise>
      const salesByDayLabels = [];
      const salesByDayData = [];
    </c:otherwise>
  </c:choose>

  // Top sản phẩm theo số lượng
  const topProductsQuantityNames = [
    <c:forEach var="product" items="${data.topProductsByQuantity}" varStatus="s">
      "${product.name}"<c:if test="${!s.last}">, </c:if>
    </c:forEach>
  ];
  const topProductsQuantityData = [
    <c:forEach var="product" items="${data.topProductsByQuantity}" varStatus="s">
      ${product.totalQuantity}<c:if test="${!s.last}">, </c:if>
    </c:forEach>
  ];

  // Top sản phẩm theo doanh thu
  const topProductsRevenueNames = [
    <c:forEach var="product" items="${data.topProductsByRevenue}" varStatus="s">
      "${product.name}"<c:if test="${!s.last}">, </c:if>
    </c:forEach>
  ];
  const topProductsRevenueData = [
    <c:forEach var="product" items="${data.topProductsByRevenue}" varStatus="s">
      ${product.totalRevenue}<c:if test="${!s.last}">, </c:if>
    </c:forEach>
  ];

0
  // Khách hàng mới vs quay lại
  <c:set var="newVsReturning" value="${data.newVsReturningCustomers}" />
  <c:choose>
    <c:when test="${not empty newVsReturning}">
      <c:set var="newCust" value="${newVsReturning.newCustomers}" />
      <c:set var="retCust" value="${newVsReturning.returningCustomers}" />
    </c:when>
    <c:otherwise>
      <c:set var="newCust" value="0" />
      <c:set var="retCust" value="0" />
    </c:otherwise>
  </c:choose>
  const newCustomers = ${newCust};
  const returningCustomers = ${retCust};

  // Biểu đồ doanh thu theo ngày (Line Chart)
  const salesCtx = document.getElementById('salesChart').getContext('2d');
  
  // Đảm bảo arrays luôn có giá trị
  const salesLabels = (typeof salesByDayLabels !== 'undefined' && salesByDayLabels && salesByDayLabels.length > 0) 
    ? salesByDayLabels.map(date => {
        if (!date) return '';
        try {
          const d = new Date(date);
          if (isNaN(d.getTime())) return date;
          return d.getDate() + '/' + (d.getMonth() + 1);
        } catch(e) {
          return date;
        }
      })
    : [];
  const salesData = (typeof salesByDayData !== 'undefined' && salesByDayData && salesByDayData.length > 0) ? salesByDayData : [];
  
  <c:choose>
    <c:when test="${hasFilter == true}">
      <c:set var="noDataMsg" value="Không có dữ liệu trong khoảng thời gian đã chọn" />
    </c:when>
    <c:otherwise>
      <c:set var="noDataMsg" value="Chưa chọn bộ lọc. Vui lòng chọn khoảng thời gian để xem báo cáo." />
    </c:otherwise>
  </c:choose>
  const noDataText = '${noDataMsg}';
  
  // Luôn tạo chart
  const salesChart = new Chart(salesCtx, {
    type: 'line',
    data: {
      labels: salesLabels.length > 0 ? salesLabels : ['Chưa có dữ liệu'],
      datasets: [{
        label: 'Doanh Thu (VNĐ)',
        data: salesData.length > 0 ? salesData : [0],
        borderColor: '#f76c85',
        backgroundColor: 'rgba(247,108,133,0.1)',
        tension: 0.4,
        fill: true,
        pointRadius: salesData.length > 0 ? 4 : 0,
        pointHoverRadius: salesData.length > 0 ? 6 : 0
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true
        },
        tooltip: {
          enabled: salesData.length > 0,
          callbacks: {
            label: function(context) {
              if (salesData.length === 0) return '';
              return 'Doanh thu: ' + new Intl.NumberFormat('vi-VN').format(context.parsed.y) + ' ₫';
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              if (salesData.length === 0) return '';
              return new Intl.NumberFormat('vi-VN').format(value);
            }
          }
        }
      },
      animation: {
        duration: salesData.length > 0 ? 1000 : 0
      },
      onHover: undefined,
      onClick: undefined
    }
  });
  
  // Hiển thị thông báo nếu không có dữ liệu - đợi chart render xong
  if (salesData.length === 0 || !salesLabels || salesLabels.length === 0) {
    setTimeout(function() {
      const chartArea = salesChart.chartArea;
      if (chartArea) {
        const ctx = salesChart.ctx;
        ctx.save();
        ctx.clearRect(0, 0, salesChart.width, salesChart.height);
        salesChart.draw();
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillStyle = '#999';
        ctx.font = '16px Arial';
        const centerX = (chartArea.left + chartArea.right) / 2;
        const centerY = (chartArea.top + chartArea.bottom) / 2;
        ctx.fillText(noDataText, centerX, centerY);
        ctx.restore();
      }
    }, 100);
  }

  // Top sản phẩm theo số lượng (Bar Chart - Horizontal)
  const topProductsQuantityCtx = document.getElementById('topProductsQuantityChart').getContext('2d');
  if (topProductsQuantityNames.length > 0) {
    new Chart(topProductsQuantityCtx, {
      type: 'bar',
      data: {
        labels: topProductsQuantityNames,
        datasets: [{
          label: 'Số Lượng Bán',
          data: topProductsQuantityData,
          backgroundColor: 'rgba(78,115,223,0.8)',
          borderColor: '#4e73df',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        indexAxis: 'y',
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          x: {
            beginAtZero: true
          }
        }
      }
    });
  }

  // Top sản phẩm theo doanh thu (Bar Chart - Horizontal)
  const topProductsRevenueCtx = document.getElementById('topProductsRevenueChart').getContext('2d');
  if (topProductsRevenueNames.length > 0) {
    new Chart(topProductsRevenueCtx, {
      type: 'bar',
      data: {
        labels: topProductsRevenueNames,
        datasets: [{
          label: 'Doanh Thu (VNĐ)',
          data: topProductsRevenueData,
          backgroundColor: 'rgba(28,200,138,0.8)',
          borderColor: '#1cc88a',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        indexAxis: 'y',
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: function(context) {
                return 'Doanh thu: ' + new Intl.NumberFormat('vi-VN').format(context.parsed.x) + ' ₫';
              }
            }
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            ticks: {
              callback: function(value) {
                return new Intl.NumberFormat('vi-VN').format(value);
              }
            }
          }
        }
      }
    });
  }

  // Khách hàng mới vs quay lại (Pie Chart)
  const customerPieCtx = document.getElementById('customerPieChart').getContext('2d');
  new Chart(customerPieCtx, {
    type: 'pie',
    data: {
      labels: ['Khách Hàng Mới', 'Khách Hàng Quay Lại'],
      datasets: [{
        data: [newCustomers, returningCustomers],
        backgroundColor: ['rgba(247,108,133,0.8)', 'rgba(78,115,223,0.8)'],
        borderWidth: 2,
        borderColor: '#fff'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom'
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || '';
              const value = context.parsed || 0;
              const total = context.dataset.data.reduce((a, b) => a + b, 0);
              const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
              return label + ': ' + value + ' (' + percentage + '%)';
            }
          }
        }
      }
    }
  });
</script>

<%@ include file="/admin/includes/footer.jspf" %>
