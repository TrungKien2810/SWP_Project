<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="row g-3">
    <div class="col-md-3">
      <div class="card shadow-sm">
        <div class="card-body">
          <div class="fw-semibold">Doanh thu hôm nay</div>
          <div class="fs-4 text-success">${String.format("%,.0f", doanhThuHomNay)} đ</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card shadow-sm">
        <div class="card-body">
          <div class="fw-semibold">Đơn hàng mới</div>
          <div class="fs-4">${soDonMoi}</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card shadow-sm">
        <div class="card-body">
          <div class="fw-semibold">Khách hàng mới</div>
          <div class="fs-4">${soKhachMoi}</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card shadow-sm">
        <div class="card-body">
          <div class="fw-semibold">Sắp hết hàng</div>
          <div class="fs-4 text-danger">${soSPHetHang}</div>
        </div>
      </div>
    </div>
  </div>

  <div class="card mt-4">
    <div class="card-body">
      <div class="fw-semibold mb-2">Doanh thu 7 ngày qua</div>
      <canvas id="chartDoanhThu7Ngay" height="80"></canvas>
    </div>
  </div>

  <div class="card mt-4">
    <div class="card-body">
      <div class="fw-semibold mb-2">Lối tắt</div>
      <a class="btn btn-primary me-2" href="${pageContext.request.contextPath}/admin?action=orders">Đơn hàng</a>
      <a class="btn btn-outline-primary me-2" href="${pageContext.request.contextPath}/admin?action=products">Sản phẩm</a>
      <a class="btn btn-outline-primary me-2" href="${pageContext.request.contextPath}/admin?action=discounts">Khuyến mãi</a>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
const ctx = document.getElementById('chartDoanhThu7Ngay').getContext('2d');
const labels = [
<c:forEach var="l" items="${nhan7Ngay}" varStatus="s">"${l}"<c:if test="${!s.last}">, </c:if></c:forEach>
];
const data7d = [
<c:forEach var="v" items="${doanhThu7Ngay}" varStatus="s">${v}<c:if test="${!s.last}">, </c:if></c:forEach>
];
new Chart(ctx, {
    type: 'line',
    data: {
        labels: labels,
        datasets: [{
            label: "Doanh thu (VNĐ)",
            data: data7d,
            fill: true,
            borderColor: '#f76c85',
            backgroundColor: 'rgba(247,108,133,0.10)',
            tension: 0.3
        }]
    },
    options: {
        plugins: {
            legend: {display: false}
        },
        scales: {
            y: {beginAtZero: true}
        }
    }
});
</script>

<%@ include file="/admin/includes/footer.jspf" %>

