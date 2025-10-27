<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <h5 class="mb-3">Cài đặt hệ thống</h5>
  <div class="card" style="max-width:720px;">
    <div class="card-body">
      <form>
        <div class="mb-3">
          <label class="form-label">Tên cửa hàng</label>
          <input type="text" class="form-control" value="PinkyCloud" />
        </div>
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input type="email" class="form-control" placeholder="support@example.com" />
        </div>
        <div class="mb-3">
          <label class="form-label">Số điện thoại</label>
          <input type="text" class="form-control" />
        </div>
        <button class="btn btn-primary" disabled>Lưu (sắp có)</button>
      </form>
    </div>
  </div>
</div>

<%@ include file="/admin/includes/footer.jspf" %>

