<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h5><i class="fa fa-shopping-cart"></i> Chi tiết đơn hàng</h5>
    <div>
      <a href="${pageContext.request.contextPath}/admin?action=orders" class="btn btn-secondary me-2">
        <i class="fa fa-arrow-left"></i> Quay lại quản lý đơn hàng
      </a>
      <a href="${pageContext.request.contextPath}/admin?action=users" class="btn btn-outline-secondary">
        <i class="fa fa-users"></i> Quản lý khách hàng
      </a>
    </div>
  </div>

  <div class="card">
    <div class="card-body">
      <%@ include file="/admin/order-detail.jsp" %>
    </div>
  </div>
</div>

<%@ include file="/admin/includes/footer.jspf" %>
