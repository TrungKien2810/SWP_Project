<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h5 class="mb-0">Quản lý khuyến mãi</h5>
    <div class="d-flex gap-2">
      <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin?action=discountAssign">
        Gán/Bỏ gán mã cho sản phẩm
      </a>
      <a class="btn btn-primary" href="${pageContext.request.contextPath}/discounts?action=new">Tạo mã mới</a>
    </div>
  </div>

  <c:if test="${not empty param.msg}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      ${param.msg}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>

  <c:choose>
    <c:when test="${empty discounts}">
      <div class="alert alert-info">Chưa có mã giảm giá. Hãy tạo mã đầu tiên.</div>
    </c:when>
    <c:otherwise>
      <!-- Chức năng gán/bỏ gán đã chuyển sang trang riêng: /admin?action=discountAssign -->

      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th>ID</th>
              <th>Code</th>
              <th>Tên</th>
              <th>Loại</th>
              <th>Giá trị</th>
              <th>Bắt đầu</th>
              <th>Kết thúc</th>
              <th>Trạng thái</th>
              <th class="text-end">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="d" items="${discounts}">
              <tr>
                <td>${d.discountId}</td>
                <td>${d.code}</td>
                <td>${d.name}</td>
                <td>${d.type}</td>
                <td>${d.value}</td>
                <td>${d.startDate}</td>
                <td>${d.endDate}</td>
                <td><c:out value="${d.active ? 'Đang kích hoạt' : 'Tắt'}"/></td>
                <td class="text-end">
                  <div class="d-inline-flex gap-2 align-items-center justify-content-end">
                    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/discounts?action=edit&id=${d.discountId}">Sửa</a>
                    <form action="${pageContext.request.contextPath}/discounts" method="post" style="margin:0;" onsubmit="return confirm('Xóa mã này?');">
                      <input type="hidden" name="action" value="delete" />
                      <input type="hidden" name="id" value="${d.discountId}" />
                      <button class="btn btn-sm btn-outline-danger" type="submit">Xóa</button>
                    </form>
                  </div>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<%@ include file="/admin/includes/footer.jspf" %>

