<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/admin/includes/header.jspf" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/Css/reports.css">

<div class="container-fluid">
  <h5 class="mb-3"><i class="fa fa-users"></i> Quản lý khách hàng</h5>
  
  <!-- Filter & Search -->
  <div class="filter-section">
    <form method="GET" action="${pageContext.request.contextPath}/admin" id="filterForm">
      <input type="hidden" name="action" value="users">
      
      <div class="row">
        <div class="col-md-4 col-lg-3">
          <label class="form-label">Tìm kiếm</label>
          <input type="text" name="search" class="form-control" 
                 placeholder="Tên, Email hoặc SĐT..." value="${searchKeyword}">
        </div>
        
        <div class="col-md-3 col-lg-2">
          <label class="form-label">Vai trò</label>
          <select name="roleFilter" class="form-select" id="roleFilter">
            <option value="ALL" ${roleFilter == 'ALL' ? 'selected' : ''}>Tất cả</option>
            <option value="ADMIN" ${roleFilter == 'ADMIN' ? 'selected' : ''}>Admin</option>
            <option value="USER" ${roleFilter == 'USER' ? 'selected' : ''}>User</option>
          </select>
        </div>
        
        <div class="col-md-12 col-lg-4 filter-actions">
          <button type="submit" class="btn btn-primary">
            <i class="fa fa-search"></i> Tìm kiếm
          </button>
          <button type="button" class="btn btn-secondary" onclick="resetFilter()">
            <i class="fa fa-redo"></i> Đặt lại
          </button>
        </div>
      </div>
    </form>
  </div>
  
  <!-- User List Table -->
  <div class="card">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên khách hàng</th>
              <th>Email / SĐT</th>
              <th>Vai trò</th>
              <th>Ngày tham gia</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty users}">
                <tr>
                  <td colspan="6" class="text-center text-muted">
                    <i class="fa fa-users"></i> Chưa có khách hàng nào
                  </td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="user" items="${users}">
                  <tr>
                    <td><strong>#${user.user_id}</strong></td>
                    <td>
                      <div class="d-flex align-items-center">
                        <c:choose>
                          <c:when test="${not empty user.avatarUrl}">
                            <img src="${pageContext.request.contextPath}${user.avatarUrl}" alt="Avatar" 
                                 class="rounded-circle me-2" width="32" height="32" 
                                 onerror="this.src='${pageContext.request.contextPath}/IMG/default-avatar.png'">
                          </c:when>
                          <c:otherwise>
                            <div class="rounded-circle me-2 bg-secondary text-white d-flex align-items-center justify-content-center" 
                                 style="width: 32px; height: 32px; font-size: 0.875rem;">
                              ${fn:substring(user.username, 0, 1)}
                            </div>
                          </c:otherwise>
                        </c:choose>
                        <strong>${user.username}</strong>
                      </div>
                    </td>
                    <td>
                      <div>
                        <div><i class="fa fa-envelope text-muted me-1"></i> ${user.email}</div>
                        <c:if test="${not empty user.phone}">
                          <div class="text-muted small"><i class="fa fa-phone me-1"></i> ${user.phone}</div>
                        </c:if>
                      </div>
                    </td>
                    <td>
                      <select id="role-select-${user.user_id}" 
                              name="role-${user.user_id}"
                              class="form-select form-select-sm role-select" 
                              data-user-id="${user.user_id}"
                              onchange="updateRole(${user.user_id}, this.value)"
                              aria-label="Chọn vai trò">
                        <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>USER</option>
                        <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                      </select>
                    </td>
                    <td>
                      <c:if test="${not empty user.date_create}">
                        ${user.date_create}
                      </c:if>
                    </td>
                    <td>
                      <a href="${pageContext.request.contextPath}/admin?action=userDetail&id=${user.user_id}" 
                         class="btn btn-sm btn-outline-primary">
                        <i class="fa fa-eye"></i> Chi tiết
                      </a>
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

<script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
<script>
function updateRole(userId, newRole) {
  if (!confirm('Bạn có chắc muốn thay đổi vai trò của người dùng này?')) {
    // Reload để reset dropdown
    location.reload();
    return;
  }
  
  fetch('${pageContext.request.contextPath}/admin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: 'action=users&op=updateRole&userId=' + userId + '&role=' + newRole
  })
  .then(response => {
    if (response.redirected) {
      window.location.href = response.url;
    } else {
      location.reload();
    }
  })
  .catch(error => {
    console.error('Error:', error);
    alert('Có lỗi xảy ra khi cập nhật vai trò!');
    location.reload();
  });
}

function resetFilter() {
  window.location.href = '${pageContext.request.contextPath}/admin?action=users';
}
</script>

<%@ include file="/admin/includes/footer.jspf" %>
