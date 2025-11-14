<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/admin/includes/header.jspf" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/Css/contact-manager.css">

<div class="container-fluid">
  <h5 class="mb-3"><i class="fa fa-envelope"></i> Quản lý phản hồi khách hàng</h5>
  
  <!-- Thông báo -->
  <c:if test="${not empty param.msg}">
    <div class="alert alert-${param.msg.contains('thành công') ? 'success' : 'danger'} alert-dismissible fade show" role="alert">
      ${param.msg}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>
  
  <!-- Thống kê nhanh -->
  <div class="row mb-3">
    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h6 class="text-muted">Tổng số phản hồi</h6>
          <h3>${contactList.size()}</h3>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h6 class="text-muted">Chưa xử lý</h6>
          <h3>
            <c:set var="unprocessedCount" value="0" />
            <c:forEach var="c" items="${contactList}">
              <c:if test="${!c.status}">
                <c:set var="unprocessedCount" value="${unprocessedCount + 1}" />
              </c:if>
            </c:forEach>
            ${unprocessedCount}
          </h3>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h6 class="text-muted">Đã xử lý</h6>
          <h3>
            <c:set var="processedCount" value="0" />
            <c:forEach var="c" items="${contactList}">
              <c:if test="${c.status}">
                <c:set var="processedCount" value="${processedCount + 1}" />
              </c:if>
            </c:forEach>
            ${processedCount}
          </h3>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Bảng danh sách phản hồi -->
  <div class="card">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover">
          <thead class="table-light">
            <tr>
              <th>ID</th>
              <th>Tên</th>
              <th>Điện thoại</th>
              <th>Email</th>
              <th>Địa chỉ</th>
              <th>Chủ đề</th>
              <th>Nội dung</th>
              <th>Ngày gửi</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty contactList}">
                <tr>
                  <td colspan="10" class="text-center text-muted py-4">
                    <i class="fa fa-inbox fa-2x mb-2"></i><br>
                    Chưa có phản hồi nào
                  </td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="c" items="${contactList}">
                  <tr class="${!c.status ? 'table-warning' : ''}">
                    <td>${c.id}</td>
                    <td><strong>${c.name}</strong></td>
                    <td>${c.phone}</td>
                    <td>${c.email}</td>
                    <td>${c.address}</td>
                    <td>${c.subject}</td>
                    <td>
                      <span class="text-truncate d-inline-block" style="max-width: 200px;" title="${c.message}">
                        ${c.message}
                      </span>
                    </td>
                    <td>
                      <c:if test="${not empty c.created_at}">
                        <fmt:formatDate value="${c.created_at}" pattern="dd/MM/yyyy HH:mm" />
                      </c:if>
                    </td>
                    <td>
                      <form action="${pageContext.request.contextPath}/UpdateLienheStatusServlet" method="post" class="d-inline">
                        <input type="hidden" name="id" value="${c.id}">
                        <select name="status" class="form-select form-select-sm" style="width: auto; display: inline-block;" onchange="this.form.submit()">
                          <option value="false" ${!c.status ? 'selected' : ''}>Chưa xử lý</option>
                          <option value="true" ${c.status ? 'selected' : ''}>Đã xử lý</option>
                        </select>
                      </form>
                    </td>
                    <td>
                      <span class="badge bg-${c.status ? 'success' : 'warning'}">
                        ${c.status ? 'Đã xử lý' : 'Chưa xử lý'}
                      </span>
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

<%@ include file="/admin/includes/footer.jspf" %>

