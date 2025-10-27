<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <h5 class="mb-3">Quản lý đơn hàng</h5>
  <div class="alert alert-secondary">Bảng đơn hàng sẽ được triển khai ở bước kế tiếp.</div>
  <table class="table table-striped">
    <thead>
      <tr>
        <th>ID</th>
        <th>Khách hàng</th>
        <th>Tổng tiền</th>
        <th>Thanh toán</th>
        <th>Trạng thái</th>
        <th>Hành động</th>
      </tr>
    </thead>
    <tbody>
      <tr><td colspan="6" class="text-center">Chưa có dữ liệu</td></tr>
    </tbody>
  </table>
  <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/View/manage-orders.jsp" style="display:none;">placeholder</a>
  
</div>

<%@ include file="/admin/includes/footer.jspf" %>

