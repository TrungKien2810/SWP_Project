<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h5 class="mb-0">Quản lý danh mục</h5>
  </div>

  <c:if test="${not empty param.msg}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      ${param.msg}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>
  <c:if test="${not empty param.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      ${param.error}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>

  <div class="card mb-3" style="max-width:720px;">
    <div class="card-body">
      <form action="${pageContext.request.contextPath}/admin" method="post" enctype="multipart/form-data" class="row g-2 align-items-end">
        <input type="hidden" name="action" value="categories" />
        <input type="hidden" name="op" value="create" />
        <div class="col-md-4">
          <label class="form-label">Tên danh mục</label>
          <input type="text" name="name" class="form-control" required />
        </div>
        <div class="col-md-3">
          <label class="form-label">Mô tả</label>
          <input type="text" name="description" class="form-control" />
        </div>
        <div class="col-md-3">
          <label class="form-label">Ảnh danh mục</label>
          <input type="file" name="categoryImage" accept="image/*" class="form-control" />
        </div>
        <div class="col-md-2">
          <button class="btn btn-primary w-100" type="submit">Thêm</button>
        </div>
      </form>
    </div>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <c:choose>
        <c:when test="${empty categories}">
          <div class="p-4 text-center text-muted">Chưa có danh mục</div>
        </c:when>
        <c:otherwise>
          <div class="table-responsive">
            <table class="table align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th style="width:60px;">ID</th>
                  <th style="width:80px;">Ảnh</th>
                  <th>Tên danh mục</th>
                  <th>Mô tả</th>
                  <th style="width:150px;">Ảnh mới</th>
                  <th class="text-end" style="width:150px;">Hành động</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="c" items="${categories}">
                  <tr>
                    <td>${c.categoryId}</td>
                    <td>
                      <c:choose>
                        <c:when test="${not empty c.imageUrl}">
                          <img src="${pageContext.request.contextPath}${c.imageUrl}" 
                               style="width:50px;height:50px;object-fit:cover;border-radius:8px;border:1px solid #eee;" 
                               alt="${c.name}" />
                        </c:when>
                        <c:otherwise>
                          <div style="width:50px;height:50px;background:#f0f0f0;border-radius:8px;display:flex;align-items:center;justify-content:center;color:#999;font-size:12px;">No img</div>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <input type="text" class="form-control form-control-sm" value="${c.name}" form="update-${c.categoryId}" name="name" required />
                    </td>
                    <td>
                      <input type="text" class="form-control form-control-sm" value="${c.description}" form="update-${c.categoryId}" name="description" />
                    </td>
                    <td>
                      <form id="update-${c.categoryId}" action="${pageContext.request.contextPath}/admin" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="categories" />
                        <input type="hidden" name="op" value="update" />
                        <input type="hidden" name="id" value="${c.categoryId}" />
                        <input type="file" name="categoryImage" accept="image/*" class="form-control form-control-sm" form="update-${c.categoryId}" />
                        <small class="text-muted" style="font-size:11px;">Để trống nếu không thay ảnh</small>
                      </form>
                    </td>
                    <td class="text-end">
                      <div class="d-inline-flex gap-2">
                        <button class="btn btn-sm btn-outline-secondary" type="submit" form="update-${c.categoryId}">Lưu</button>
                        <form action="${pageContext.request.contextPath}/admin" method="post" onsubmit="return confirm('Xóa danh mục này?');" style="display:inline;">
                          <input type="hidden" name="action" value="categories" />
                          <input type="hidden" name="op" value="delete" />
                          <input type="hidden" name="id" value="${c.categoryId}" />
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
  </div>
</div>

<%@ include file="/admin/includes/footer.jspf" %>

