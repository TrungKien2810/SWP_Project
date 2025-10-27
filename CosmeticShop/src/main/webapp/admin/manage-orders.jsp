<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <h5 class="mb-3">Quản lý đơn hàng</h5>
  
  <!-- Filter by Status & Date -->
  <div class="mb-3">
    <form method="get" action="${pageContext.request.contextPath}/admin" class="row g-2 align-items-end">
      <input type="hidden" name="action" value="orders">
      
      <div class="col-auto">
        <label class="form-label mb-0">Lọc theo trạng thái:</label>
        <select name="status" class="form-select form-select-sm" style="width:200px;" onchange="this.form.submit()">
          <option value="">Tất cả</option>
          <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
          <option value="CONFIRMED" ${selectedStatus == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
          <option value="SHIPPING" ${selectedStatus == 'SHIPPING' ? 'selected' : ''}>Đang giao</option>
          <option value="COMPLETED" ${selectedStatus == 'COMPLETED' ? 'selected' : ''}>Đã hoàn thành</option>
          <option value="CANCELLED" ${selectedStatus == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
        </select>
      </div>
      
      <div class="col-auto">
        <label class="form-label mb-0">Lọc theo ngày:</label>
        <select name="dateFilter" class="form-select form-select-sm" style="width:150px;" onchange="toggleDateRange(this.value)">
          <option value="">Tất cả</option>
          <option value="today" ${selectedDateFilter == 'today' ? 'selected' : ''}>Hôm nay</option>
          <option value="dateRange" ${selectedDateFilter == 'dateRange' ? 'selected' : ''}>Khoảng ngày</option>
        </select>
      </div>
      
      <div class="col-auto" id="dateRangeContainer" style="display:${selectedDateFilter == 'dateRange' ? 'block' : 'none'};">
        <div class="d-flex gap-2 align-items-center">
          <input type="date" name="startDate" class="form-control form-control-sm" value="${selectedStartDate}" style="width:150px;">
          <span class="text-muted">đến</span>
          <input type="date" name="endDate" class="form-control form-control-sm" value="${selectedEndDate}" style="width:150px;">
          <button type="submit" class="btn btn-sm btn-primary">Lọc</button>
          <button type="button" class="btn btn-sm btn-secondary" onclick="resetFilter()">Xóa</button>
        </div>
      </div>
    </form>
  </div>
  
  <div class="card">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>Ngày đặt</th>
              <th>Khách hàng</th>
              <th>Tổng tiền</th>
              <th>Phương thức</th>
              <th>Thanh toán</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty orders}">
                <tr>
                  <td colspan="8" class="text-center">Chưa có đơn hàng nào</td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="order" items="${orders}">
                  <tr>
                    <td><strong>#${order.orderId}</strong></td>
                    <td>
                      <c:if test="${not empty order.orderDate}">
                        ${order.orderDate}
                      </c:if>
                    </td>
                    <td>${customerNames[order.orderId]}</td>
                    <td>
                      <strong style="color:#f76c85;">
                        ${String.format("%,.0f", order.totalAmount)} đ
                      </strong>
                    </td>
                    <td>
                      <span class="badge bg-secondary">${order.paymentMethod}</span>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${order.paymentStatus == 'PAID'}">
                          <span class="badge bg-success">Đã thanh toán</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge bg-warning">Chưa thanh toán</span>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${order.orderStatus == 'CANCELLED'}">
                          <span class="badge bg-danger">Đã hủy</span>
                          <br>
                        </c:when>
                        <c:otherwise>
                          <select class="form-select form-select-sm status-select" 
                                  data-order-id="${order.orderId}"
                                  onchange="updateOrderStatus(${order.orderId}, this.value)">
                            <option value="PENDING" ${order.orderStatus == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
                            <option value="CONFIRMED" ${order.orderStatus == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
                            <option value="SHIPPING" ${order.orderStatus == 'SHIPPING' ? 'selected' : ''}>Đang giao</option>
                            <option value="COMPLETED" ${order.orderStatus == 'COMPLETED' ? 'selected' : ''}>Đã hoàn thành</option>
                            <option value="CANCELLED" ${order.orderStatus == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                          </select>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <button class="btn btn-sm btn-outline-primary" 
                              onclick="viewOrderDetail(${order.orderId})">
                        <i class="fa fa-eye"></i> Chi tiết
                      </button>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<!-- Modal for Order Details -->
<div class="modal fade" id="orderDetailModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Chi tiết đơn hàng #<span id="modalOrderId"></span></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body" id="orderDetailContent">
        <div class="text-center"><i class="fa fa-spinner fa-spin"></i> Đang tải...</div>
      </div>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
<script>
function updateOrderStatus(orderId, newStatus) {
  if (!confirm('Bạn có chắc muốn cập nhật trạng thái đơn hàng?')) {
    // Reload to reset dropdown
    location.reload();
    return;
  }
  
  fetch('${pageContext.request.contextPath}/admin/order-management', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: 'action=updateStatus&orderId=' + orderId + '&newStatus=' + newStatus
  })
  .then(response => response.text())
  .then(result => {
    if (result === 'success') {
      alert('Cập nhật trạng thái thành công!');
      location.reload();
    } else if (result === 'out_of_stock') {
      alert('Không đủ tồn kho! Không thể xác nhận đơn hàng.');
      location.reload();
    } else if (result === 'no_change') {
      // Không làm gì, trạng thái không đổi
      location.reload();
    } else {
      alert('Có lỗi xảy ra khi cập nhật trạng thái!');
      location.reload();
    }
  })
  .catch(error => {
    console.error('Error:', error);
    alert('Có lỗi xảy ra!');
    location.reload();
  });
}

function updateTrackingNumber(orderId, trackingNumber) {
  if (!trackingNumber || trackingNumber.trim() === '') {
    return;
  }
  
  fetch('${pageContext.request.contextPath}/admin/order-management', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: 'action=updateTracking&orderId=' + orderId + '&trackingNumber=' + encodeURIComponent(trackingNumber)
  })
  .then(response => response.text())
  .then(result => {
    if (result === 'success') {
      console.log('Tracking number updated successfully');
    } else {
      console.error('Failed to update tracking number');
    }
  })
  .catch(error => {
    console.error('Error:', error);
  });
}

function viewOrderDetail(orderId) {
  const modal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
  document.getElementById('modalOrderId').textContent = orderId;
  document.getElementById('orderDetailContent').innerHTML = '<div class="text-center"><i class="fa fa-spinner fa-spin"></i> Đang tải...</div>';
  modal.show();
  
  // Load order details
  fetch('${pageContext.request.contextPath}/admin?action=orderDetail&orderId=' + orderId)
    .then(response => response.text())
    .then(html => {
      document.getElementById('orderDetailContent').innerHTML = html;
    })
    .catch(error => {
      console.error('Error:', error);
      document.getElementById('orderDetailContent').innerHTML = '<div class="alert alert-danger">Không thể tải chi tiết đơn hàng</div>';
    });
}

function toggleDateRange(value) {
  const container = document.getElementById('dateRangeContainer');
  if (value === 'dateRange') {
    container.style.display = 'block';
  } else {
    container.style.display = 'none';
    // Nếu chọn "Hôm nay" hoặc "Tất cả", submit form ngay
    if (value === 'today') {
      document.querySelector('form').submit();
    }
  }
}

function resetFilter() {
  window.location.href = '${pageContext.request.contextPath}/admin?action=orders';
}
</script>

<%@ include file="/admin/includes/footer.jspf" %>
