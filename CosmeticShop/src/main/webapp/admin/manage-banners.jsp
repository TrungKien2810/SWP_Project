<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-3 gap-2">
    <h5 class="mb-0">Quản lý banner</h5>
  </div>

  <div class="card mb-4">
    <div class="card-body">
      <h6 class="card-title"><c:choose><c:when test="${not empty editBanner}">Cập nhật banner</c:when><c:otherwise>Thêm banner mới</c:otherwise></c:choose></h6>
      <form action="${pageContext.request.contextPath}/admin?action=banners" method="post" enctype="multipart/form-data" class="row g-3">
        <c:if test="${not empty editBanner}">
          <input type="hidden" name="id" value="${editBanner.bannerId}" />
        </c:if>
        <div class="col-md-4">
          <label class="form-label">Chọn ảnh</label>
          <input type="file" name="bannerImage" accept="image/*" class="form-control" />
          <div class="form-text">Nên chọn ảnh đúng tỷ lệ thiết kế. <strong>Kích thước tối ưu: 1920x600, 1600x550, 1200x400px cho banner slideshow</strong>.</div>
          <c:if test="${not empty editBanner.imagePath}">
            <div class="form-text">Để trống nếu không thay ảnh. Ảnh hiện tại:</div>
            <img src="${pageContext.request.contextPath}${editBanner.imagePath}" style="width:140px;height:80px;object-fit:cover;border-radius:8px;border:1px solid #eee;"/>
          </c:if>
        </div>
        <div class="col-md-4">
          <label class="form-label">Link đích (tùy chọn)</label>
          <input type="text" name="targetUrl" value="${not empty editBanner ? editBanner.targetUrl : ''}" placeholder="" class="form-control" />
          <div class="form-text">Nhập URL hoặc đường dẫn tương đối trong site.</div>
        </div>
        <div class="col-md-2">
          <label class="form-label">Thứ tự</label>
          <input type="number" name="displayOrder" value="${not empty editBanner ? editBanner.displayOrder : 0}" class="form-control" />
        </div>
        <div class="col-md-2 d-flex align-items-end">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" name="isActive" id="isActive" ${empty editBanner ? 'checked' : (editBanner.active ? 'checked' : '')}>
            <label class="form-check-label" for="isActive">Kích hoạt</label>
          </div>
        </div>
        <div class="col-12">
          <button class="btn btn-primary" type="submit"><c:choose><c:when test="${not empty editBanner}">Cập nhật</c:when><c:otherwise>Lưu banner</c:otherwise></c:choose></button>
          <c:if test="${not empty editBanner}">
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin?action=banners">Hủy</a>
          </c:if>
        </div>
      </form>
    </div>
  </div>

  <c:choose>
    <c:when test="${empty banners}">
      <div class="alert alert-warning">Chưa có banner nào.</div>
    </c:when>
    <c:otherwise>
      <div class="card">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th>Ảnh</th>
                <th>image path</th>
                <th>target url</th>
                <th>display order</th>
                <th>active</th>
                <th class="text-end">Hành động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="b" items="${banners}">
                <tr>
                  <td style="width:100px;">
                    <c:if test="${not empty b.imagePath}">
                      <img src="${pageContext.request.contextPath}${b.imagePath}" alt="thumb" style="width:84px;height:56px;object-fit:cover;border-radius:8px;"/>
                    </c:if>
                  </td>
                  <td><code>${b.imagePath}</code></td>
                  <td>
                    <c:choose>
                      <c:when test="${not empty b.targetUrl}">
                        <c:choose>
                          <c:when test="${fn:startsWith(b.targetUrl, 'http://') || fn:startsWith(b.targetUrl, 'https://')}">
                            <a href="${b.targetUrl}" target="_blank">${b.targetUrl}</a>
                          </c:when>
                          <c:otherwise>
                            <a href="${pageContext.request.contextPath}${b.targetUrl}" target="_blank">${b.targetUrl}</a>
                          </c:otherwise>
                        </c:choose>
                      </c:when>
                      <c:otherwise><span class="text-muted">—</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td>${b.displayOrder}</td>
                  <td>
                    <span class="badge ${b.active ? 'bg-success' : 'bg-secondary'}">${b.active ? 'On' : 'Off'}</span>
                  </td>
                  <td class="text-end">
                    <div class="d-inline-flex gap-2 align-items-center justify-content-end">
                      <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin?action=banners&op=edit&id=${b.bannerId}">Sửa</a>
                      <form action="${pageContext.request.contextPath}/admin?action=banners" method="post" style="margin:0;display:inline;" onsubmit="return confirm('Xóa banner này?');">
                        <input type="hidden" name="op" value="delete" />
                        <input type="hidden" name="id" value="${b.bannerId}" />
                        <button class="btn btn-sm btn-outline-danger" type="submit">Xóa</button>
                      </form>
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

<%@ include file="/admin/includes/footer.jspf" %>


