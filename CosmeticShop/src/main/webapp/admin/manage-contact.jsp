<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/admin/includes/header.jspf" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/Css/contact-manager.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<div class="container-fluid contact-manager-wrapper">
  <div class="page-header">
    <h2><i class="fas fa-envelope-open-text"></i> Quản lý phản hồi khách hàng</h2>
    <p class="text-muted">Theo dõi và xử lý các phản hồi từ khách hàng</p>
  </div>
  
  <!-- Thông báo -->
  <c:if test="${not empty param.msg}">
    <div class="alert alert-${param.msg.contains('thành công') ? 'success' : 'danger'} alert-dismissible fade show" role="alert">
      <i class="fas fa-${param.msg.contains('thành công') ? 'check-circle' : 'exclamation-circle'}"></i>
      ${param.msg}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>
  
  <!-- Tính toán thống kê trước -->
  <c:set var="totalCount" value="${contactList.size()}" />
  <c:set var="unprocessedCount" value="0" />
  <c:set var="processedCount" value="0" />
  <c:forEach var="c" items="${contactList}">
    <c:if test="${!c.status}">
      <c:set var="unprocessedCount" value="${unprocessedCount + 1}" />
    </c:if>
    <c:if test="${c.status}">
      <c:set var="processedCount" value="${processedCount + 1}" />
    </c:if>
  </c:forEach>
  
  <!-- Thống kê nhanh -->
  <div class="row stats-row mb-4">
    <div class="col-md-4 mb-3">
      <div class="stat-card stat-total">
        <div class="stat-icon">
          <i class="fas fa-envelope"></i>
        </div>
        <div class="stat-content">
          <h6>Tổng số phản hồi</h6>
          <h2>${totalCount}</h2>
        </div>
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <div class="stat-card stat-pending">
        <div class="stat-icon">
          <i class="fas fa-clock"></i>
        </div>
        <div class="stat-content">
          <h6>Chưa xử lý</h6>
          <h2>${unprocessedCount}</h2>
        </div>
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <div class="stat-card stat-completed">
        <div class="stat-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
          <h6>Đã xử lý</h6>
          <h2>${processedCount}</h2>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Bộ lọc và tìm kiếm -->
  <div class="filter-section mb-4">
    <div class="row align-items-center">
      <div class="col-md-5 mb-2">
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input type="text" id="searchInput" class="form-control" placeholder="Tìm kiếm theo tên, email, số điện thoại..." onkeypress="if(event.key==='Enter') filterTable()">
        </div>
      </div>
      <div class="col-md-2 mb-2">
        <button class="btn btn-primary w-100" onclick="filterTable()">
          <i class="fas fa-search"></i> Tìm kiếm
        </button>
      </div>
      <div class="col-md-3 mb-2">
        <select id="statusFilter" class="form-select" onchange="filterTable()">
          <option value="">Tất cả trạng thái</option>
          <option value="pending">Chưa xử lý</option>
          <option value="completed">Đã xử lý</option>
        </select>
      </div>
      <div class="col-md-2 mb-2">
        <button class="btn btn-outline-secondary w-100" onclick="resetFilters()">
          <i class="fas fa-redo"></i> Reset
        </button>
      </div>
    </div>
  </div>
  
  <!-- Bảng danh sách phản hồi -->
  <div class="contact-table-card">
    <div class="card-header">
      <h5><i class="fas fa-list"></i> Danh sách phản hồi</h5>
    </div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table contact-table" id="contactTable">
          <thead>
            <tr>
              <th>#</th>
              <th><i class="fas fa-user"></i> Khách hàng</th>
              <th><i class="fas fa-phone"></i> Liên hệ</th>
              <th><i class="fas fa-tag"></i> Chủ đề</th>
              <th><i class="fas fa-comment-dots"></i> Nội dung</th>
              <th><i class="fas fa-calendar"></i> Ngày gửi</th>
              <th><i class="fas fa-toggle-on"></i> Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty contactList}">
                <tr>
                  <td colspan="7" class="text-center empty-state py-5">
                    <i class="fas fa-inbox fa-3x mb-3 text-muted"></i>
                    <h5 class="text-muted">Chưa có phản hồi nào</h5>
                    <p class="text-muted">Các phản hồi từ khách hàng sẽ hiển thị ở đây</p>
                  </td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="c" items="${contactList}" varStatus="loop">
                  <tr class="contact-row ${!c.status ? 'unprocessed' : 'processed'}" 
                      data-status="${c.status ? 'completed' : 'pending'}"
                      data-id="${c.id}"
                      data-name="<c:out value='${c.name}'/>"
                      data-email="${c.email}"
                      data-phone="${c.phone}"
                      data-address="<c:out value='${c.address}'/>"
                      data-subject="<c:out value='${c.subject}'/>"
                      data-message="<c:out value='${c.message}'/>"
                      data-date="<fmt:formatDate value='${c.created_at}' pattern='dd/MM/yyyy HH:mm' />"
                      data-contact-status="${c.status}">
                    <td class="id-cell">${c.id}</td>
                    <td class="customer-cell">
                      <div class="customer-info">
                        <div class="customer-avatar">
                          <i class="fas fa-user"></i>
                        </div>
                        <div>
                          <strong class="customer-name">${c.name}</strong>
                          <small class="d-block text-muted customer-address">
                            <i class="fas fa-map-marker-alt"></i> ${c.address}
                          </small>
                        </div>
                      </div>
                    </td>
                    <td class="contact-cell">
                      <div class="contact-info">
                        <div class="mb-1">
                          <i class="fas fa-envelope text-primary"></i>
                          <a href="mailto:${c.email}" class="contact-link">${c.email}</a>
                        </div>
                        <div>
                          <i class="fas fa-phone text-success"></i>
                          <a href="tel:${c.phone}" class="contact-link">${c.phone}</a>
                        </div>
                      </div>
                    </td>
                    <td class="subject-cell">
                      <span class="subject-badge">
                        <i class="fas fa-bookmark"></i> ${c.subject}
                      </span>
                    </td>
                    <td class="message-cell">
                      <div class="message-preview">
                        ${c.message.length() > 50 ? c.message.substring(0, 50).concat('...') : c.message}
                      </div>
                      <button class="btn btn-sm btn-link view-detail-btn" 
                              onclick="showDetailModalFromRow(this)">
                        <i class="fas fa-eye"></i> Xem chi tiết
                      </button>
                    </td>
                    <td class="date-cell">
                      <c:if test="${not empty c.created_at}">
                        <div class="date-info">
                          <i class="far fa-calendar-alt"></i>
                          <fmt:formatDate value="${c.created_at}" pattern="dd/MM/yyyy" />
                          <small class="d-block text-muted">
                            <i class="far fa-clock"></i>
                            <fmt:formatDate value="${c.created_at}" pattern="HH:mm" />
                          </small>
                        </div>
                      </c:if>
                    </td>
                    <td class="status-cell">
                      <form action="${pageContext.request.contextPath}/UpdateLienheStatusServlet" method="post" class="status-form">
                        <input type="hidden" name="id" value="${c.id}">
                        <select name="status" class="status-select ${c.status ? 'status-completed' : 'status-pending'}" 
                                onchange="confirmStatusChange(this)">
                          <option value="false" ${!c.status ? 'selected' : ''}>
                            <i class="fas fa-clock"></i> Chưa xử lý
                          </option>
                          <option value="true" ${c.status ? 'selected' : ''}>
                            <i class="fas fa-check"></i> Đã xử lý
                          </option>
                        </select>
                      </form>
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

<!-- Modal xem chi tiết -->
<div class="modal fade" id="detailModal" tabindex="-1" aria-labelledby="detailModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="detailModalLabel">
          <i class="fas fa-info-circle"></i> Chi tiết phản hồi #<span id="modalId"></span>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="detail-section">
          <h6><i class="fas fa-user"></i> Thông tin khách hàng</h6>
          <div class="row">
            <div class="col-md-6">
              <p><strong>Họ tên:</strong> <span id="modalName"></span></p>
              <p><strong>Email:</strong> <a href="#" id="modalEmail"></a></p>
            </div>
            <div class="col-md-6">
              <p><strong>Số điện thoại:</strong> <a href="#" id="modalPhone"></a></p>
              <p><strong>Địa chỉ:</strong> <span id="modalAddress"></span></p>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h6><i class="fas fa-bookmark"></i> Chủ đề</h6>
          <p id="modalSubject" class="subject-text"></p>
        </div>
        
        <div class="detail-section">
          <h6><i class="fas fa-comment-dots"></i> Nội dung phản hồi</h6>
          <div class="message-content" id="modalMessage"></div>
        </div>
        
        <div class="detail-section">
          <div class="row">
            <div class="col-md-6">
              <p><strong><i class="far fa-calendar"></i> Ngày gửi:</strong> <span id="modalDate"></span></p>
            </div>
            <div class="col-md-6">
              <p><strong><i class="fas fa-info"></i> Trạng thái:</strong> <span id="modalStatus"></span></p>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          <i class="fas fa-times"></i> Đóng
        </button>
      </div>
    </div>
  </div>
</div>

<script>
// Tìm kiếm và lọc (chỉ khi nhấn nút hoặc Enter)
function filterTable() {
  const searchValue = document.getElementById('searchInput').value.toLowerCase().trim();
  const statusValue = document.getElementById('statusFilter').value;
  const rows = document.querySelectorAll('.contact-row');
  
  let visibleCount = 0;
  rows.forEach(row => {
    const text = row.textContent.toLowerCase();
    const status = row.getAttribute('data-status');
    
    const matchSearch = !searchValue || text.includes(searchValue);
    const matchStatus = !statusValue || status === statusValue;
    
    if (matchSearch && matchStatus) {
      row.style.display = '';
      visibleCount++;
    } else {
      row.style.display = 'none';
    }
  });
  
  // Hiển thị thông báo nếu không tìm thấy
  updateSearchResult(visibleCount);
}

function updateSearchResult(count) {
  const tbody = document.querySelector('.contact-table tbody');
  let resultRow = document.getElementById('searchResultRow');
  
  // Xóa thông báo cũ nếu có
  if (resultRow) {
    resultRow.remove();
  }
  
  // Nếu không có kết quả và đang có filter
  const searchValue = document.getElementById('searchInput').value.trim();
  const statusValue = document.getElementById('statusFilter').value;
  
  if (count === 0 && (searchValue || statusValue)) {
    const emptyRow = document.createElement('tr');
    emptyRow.id = 'searchResultRow';
    emptyRow.innerHTML = `
      <td colspan="7" class="text-center py-4">
        <i class="fas fa-search fa-2x mb-2 text-muted"></i>
        <h6 class="text-muted">Không tìm thấy kết quả phù hợp</h6>
        <p class="text-muted small">Thử thay đổi từ khóa tìm kiếm hoặc bộ lọc</p>
      </td>
    `;
    tbody.appendChild(emptyRow);
  }
}

function resetFilters() {
  document.getElementById('searchInput').value = '';
  document.getElementById('statusFilter').value = '';
  
  // Hiển thị tất cả các rows
  const rows = document.querySelectorAll('.contact-row');
  rows.forEach(row => {
    row.style.display = '';
  });
  
  // Xóa thông báo kết quả tìm kiếm
  const resultRow = document.getElementById('searchResultRow');
  if (resultRow) {
    resultRow.remove();
  }
}

// Xác nhận thay đổi trạng thái
function confirmStatusChange(select) {
  if (confirm('Bạn có chắc muốn thay đổi trạng thái phản hồi này?')) {
    select.closest('form').submit();
  } else {
    select.selectedIndex = select.value === 'true' ? 1 : 0;
  }
}

// Hiển thị modal chi tiết từ nút trong row
function showDetailModalFromRow(button) {
  const row = button.closest('.contact-row');
  const id = row.dataset.id;
  const name = row.dataset.name;
  const email = row.dataset.email;
  const phone = row.dataset.phone;
  const address = row.dataset.address;
  const subject = row.dataset.subject;
  const message = row.dataset.message;
  const date = row.dataset.date;
  const status = row.dataset.contactStatus === 'true';
  
  document.getElementById('modalId').textContent = id;
  document.getElementById('modalName').textContent = name;
  document.getElementById('modalEmail').textContent = email;
  document.getElementById('modalEmail').href = 'mailto:' + email;
  document.getElementById('modalPhone').textContent = phone;
  document.getElementById('modalPhone').href = 'tel:' + phone;
  document.getElementById('modalAddress').textContent = address;
  document.getElementById('modalSubject').textContent = subject;
  document.getElementById('modalMessage').textContent = message;
  document.getElementById('modalDate').textContent = date;
  
  const statusBadge = status 
    ? '<span class="badge bg-success"><i class="fas fa-check-circle"></i> Đã xử lý</span>'
    : '<span class="badge bg-warning"><i class="fas fa-clock"></i> Chưa xử lý</span>';
  document.getElementById('modalStatus').innerHTML = statusBadge;
  
  new bootstrap.Modal(document.getElementById('detailModal')).show();
}
</script>

<%@ include file="/admin/includes/footer.jspf" %>


