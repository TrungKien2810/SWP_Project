<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="row g-3">
    <div class="col-md-3">
      <div class="card">
        <div class="card-body">
          <div class="fw-semibold">Doanh thu hôm nay</div>
          <div class="fs-4">—</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card">
        <div class="card-body">
          <div class="fw-semibold">Đơn hàng mới</div>
          <div class="fs-4">—</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card">
        <div class="card-body">
          <div class="fw-semibold">Khách hàng mới</div>
          <div class="fs-4">—</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card">
        <div class="card-body">
          <div class="fw-semibold">Sắp hết hàng</div>
          <div class="fs-4">—</div>
        </div>
      </div>
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

<%@ include file="/admin/includes/footer.jspf" %>

