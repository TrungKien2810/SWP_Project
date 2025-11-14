<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/admin/includes/header.jspf" %>

<style>
.category-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  max-width: 250px;
}

.category-badges .badge {
  font-size: 11px;
  padding: 4px 8px;
  font-weight: 500;
  white-space: nowrap;
}

.category-badges .badge.bg-info {
  cursor: help;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  transition: all 0.2s;
}

.category-badges .badge.bg-info:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}
</style>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-3 gap-2">
    <h5 class="mb-0">Quản lý sản phẩm</h5>
    <form action="${pageContext.request.contextPath}/admin" method="get" class="d-flex" style="max-width:420px;flex:1;">
      <input type="hidden" name="action" value="products" />
      <input type="text" name="search" value="${search}" class="form-control" placeholder="Tìm theo tên hoặc mô tả..." />
      <button class="btn btn-outline-primary ms-2" type="submit">Tìm</button>
    </form>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/products?action=new">Thêm sản phẩm</a>
  </div>

  <c:choose>
    <c:when test="${empty productList}">
      <div class="alert alert-warning">Chưa có sản phẩm. Hãy tạo sản phẩm đầu tiên.</div>
    </c:when>
    <c:otherwise>
      <div class="card">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th>Ảnh</th>
                <th>Tên sản phẩm</th>
                <th>Danh mục</th>
                <th class="text-end">Giá</th>
                <th class="text-end">Tồn kho</th>
                <th class="text-end">Hành động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="p" items="${productList}">
                <tr>
                  <td style="width:72px;">
                    <c:choose>
                      <c:when test="${not empty p.imageUrl}">
                        <img src="${pageContext.request.contextPath}${p.imageUrl}" alt="thumb" style="width:56px;height:56px;object-fit:cover;border-radius:8px;"/>
                      </c:when>
                      <c:otherwise>
                        <div style="width:56px;height:56px;background:#f1f1f1;border-radius:8px;display:flex;align-items:center;justify-content:center;color:#aaa;">—</div>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <div class="fw-semibold">${p.name}</div>
                    <div class="text-muted small" style="max-width:520px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${p.description}</div>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${not empty productCategoryMap[p.productId]}">
                        <div class="category-badges">
                          <c:set var="categories" value="${productCategoryMap[p.productId]}" />
                          <c:set var="categoryCount" value="${categories.size()}" />
                          
                          <c:choose>
                            <c:when test="${categoryCount <= 2}">
                              <!-- Hiển thị tất cả nếu <= 2 danh mục -->
                              <c:forEach var="catName" items="${categories}">
                                <span class="badge bg-secondary me-1 mb-1">${catName}</span>
                              </c:forEach>
                            </c:when>
                            <c:otherwise>
                              <!-- Hiển thị 2 danh mục đầu + badge số lượng còn lại -->
                              <c:forEach var="catName" items="${categories}" begin="0" end="1">
                                <span class="badge bg-secondary me-1 mb-1">${catName}</span>
                              </c:forEach>
                              <span class="badge bg-info me-1 mb-1" 
                                    data-bs-toggle="tooltip" 
                                    data-bs-placement="top"
                                    data-bs-html="true"
                                    title="<c:forEach var='catName' items='${categories}' varStatus='status'>${catName}<c:if test='${!status.last}'>, </c:if></c:forEach>">
                                +${categoryCount - 2}
                              </span>
                            </c:otherwise>
                          </c:choose>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted small">(Chưa phân loại)</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-end"><strong style="color:#f76c85;">${String.format("%,.0f", p.price)} ₫</strong></td>
                  <td class="text-end">${p.stock}</td>
                  <td class="text-end">
                    <div class="d-inline-flex gap-2 align-items-center justify-content-end">
                      <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/products?action=edit&id=${p.productId}">Sửa</a>
                      <a class="btn btn-sm btn-outline-danger" href="${pageContext.request.contextPath}/products?action=delete&id=${p.productId}" onclick="return confirm('Xóa sản phẩm này?');">Xóa</a>
                    </div>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<script>
// Khởi tạo Bootstrap tooltips
document.addEventListener('DOMContentLoaded', function() {
  var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
  var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl);
  });
});
</script>

<%@ include file="/admin/includes/footer.jspf" %>

