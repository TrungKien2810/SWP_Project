<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h5 class="mb-0">Gán/Bỏ gán mã cho sản phẩm</h5>
    <div class="d-flex gap-2">
      <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin?action=discounts">Quay về quản lý mã</a>
      <a class="btn btn-primary" href="${pageContext.request.contextPath}/discounts?action=new">Tạo mã mới</a>
    </div>
  </div>

  <c:if test="${not empty param.msg}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      ${param.msg}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>

  <div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
      <strong>Gán mã giảm giá</strong>
      <small class="text-muted">Chọn một trong các cách dưới đây</small>
    </div>
    <div class="card-body">
      <ul class="nav nav-tabs" id="assignTabs" role="tablist">
        <li class="nav-item" role="presentation">
          <button class="nav-link active" id="by-products-tab" data-bs-toggle="tab" data-bs-target="#by-products" type="button" role="tab">Theo sản phẩm</button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="by-category-tab" data-bs-toggle="tab" data-bs-target="#by-category" type="button" role="tab">Theo danh mục</button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="by-price-tab" data-bs-toggle="tab" data-bs-target="#by-price" type="button" role="tab">Theo mức giá</button>
        </li>
      </ul>
      <div class="tab-content p-3 border border-top-0 rounded-bottom">
        <!-- By Products -->
        <div class="tab-pane fade show active" id="by-products" role="tabpanel">
          <form action="${pageContext.request.contextPath}/admin" method="post" class="row g-3">
            <input type="hidden" name="action" value="discounts" />
            <input type="hidden" name="op" value="assignProducts" />
            <div class="col-md-4">
              <label class="form-label">Chọn mã giảm giá</label>
              <select class="form-select" name="discountId" required>
                <option value="" disabled selected>-- Chọn mã --</option>
                <c:forEach var="d" items="${discounts}">
                  <option value="${d.discountId}">${d.code} - ${d.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-8">
              <label class="form-label">Chọn sản phẩm</label>
              <div class="border rounded" style="max-height: 340px; overflow-y: auto; padding: 8px;">
                <div class="list-group list-group-flush">
                  <c:forEach var="p" items="${allProducts}">
                    <label class="list-group-item d-flex align-items-center gap-3" style="cursor:pointer;">
                      <input class="form-check-input me-2" type="checkbox" name="productIds" value="${p.productId}" />
                      <img src="${pageContext.request.contextPath}${p.imageUrl}" alt="${p.name}"
                           style="width:48px;height:48px;object-fit:cover;border-radius:8px;"
                           onerror="this.src='${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png'">
                      <span class="flex-grow-1" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;" title="${p.name}">
                        ${p.productId} - ${p.name}
                      </span>
                    </label>
                  </c:forEach>
                </div>
              </div>
              <div class="form-text">Danh sách có thể cuộn khi dài.</div>
            </div>
            <div class="col-12 d-flex gap-2">
              <button type="submit" class="btn btn-success"><i class="fa fa-check"></i> Gán mã</button>
              <button type="reset" class="btn btn-outline-secondary">Làm mới</button>
            </div>
          </form>
          <hr/>
          <form action="${pageContext.request.contextPath}/admin" method="post" class="row g-3">
            <input type="hidden" name="action" value="discounts" />
            <input type="hidden" name="op" value="unassignProducts" />
            <div class="col-md-4">
              <label class="form-label">Bỏ gán mã giảm giá</label>
              <select class="form-select" name="discountId" required>
                <option value="" disabled selected>-- Chọn mã --</option>
                <c:forEach var="d" items="${discounts}">
                  <option value="${d.discountId}">${d.code} - ${d.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-8">
              <label class="form-label">Chọn sản phẩm để bỏ gán</label>
              <div class="border rounded" style="max-height: 240px; overflow-y: auto; padding: 8px;">
                <div class="list-group list-group-flush">
                  <c:forEach var="p" items="${allProducts}">
                    <label class="list-group-item d-flex align-items-center gap-3" style="cursor:pointer;">
                      <input class="form-check-input me-2" type="checkbox" name="productIds" value="${p.productId}" />
                      <img src="${pageContext.request.contextPath}${p.imageUrl}" alt="${p.name}"
                           style="width:40px;height:40px;object-fit:cover;border-radius:8px;"
                           onerror="this.src='${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png'">
                      <span class="flex-grow-1" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;" title="${p.name}">
                        ${p.productId} - ${p.name}
                      </span>
                    </label>
                  </c:forEach>
                </div>
              </div>
            </div>
            <div class="col-12 d-flex gap-2">
              <button type="submit" class="btn btn-outline-danger"><i class="fa fa-unlink"></i> Bỏ gán mã</button>
            </div>
          </form>
          <div class="my-3"></div>
          <form action="${pageContext.request.contextPath}/admin" method="post" class="row g-3">
            <input type="hidden" name="action" value="discounts" />
            <input type="hidden" name="op" value="unassignAllForProducts" />
            <div class="col-md-12">
              <label class="form-label">Xóa tất cả các mã đang gán cho các sản phẩm đã chọn</label>
              <div class="border rounded" style="max-height: 240px; overflow-y: auto; padding: 8px;">
                <div class="list-group list-group-flush">
                  <c:forEach var="p" items="${allProducts}">
                    <label class="list-group-item d-flex align-items-center gap-3" style="cursor:pointer;">
                      <input class="form-check-input me-2" type="checkbox" name="productIds" value="${p.productId}" />
                      <img src="${pageContext.request.contextPath}${p.imageUrl}" alt="${p.name}"
                           style="width:40px;height:40px;object-fit:cover;border-radius:8px;"
                           onerror="this.src='${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png'">
                      <span class="flex-grow-1" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;" title="${p.name}">
                        ${p.productId} - ${p.name}
                      </span>
                    </label>
                  </c:forEach>
                </div>
              </div>
            </div>
            <div class="col-12 d-flex gap-2">
              <button type="submit" class="btn btn-danger"><i class="fa fa-trash"></i> Xóa tất cả mã của sản phẩm</button>
            </div>
          </form>
        </div>
        <!-- By Category -->
        <div class="tab-pane fade" id="by-category" role="tabpanel">
          <form action="${pageContext.request.contextPath}/admin" method="post" class="row g-3">
            <input type="hidden" name="action" value="discounts" />
            <input type="hidden" name="op" value="assignByCategory" />
            <div class="col-md-4">
              <label class="form-label">Chọn mã giảm giá</label>
              <select class="form-select" name="discountId" required>
                <option value="" disabled selected>-- Chọn mã --</option>
                <c:forEach var="d" items="${discounts}">
                  <option value="${d.discountId}">${d.code} - ${d.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-8">
              <label class="form-label">Danh mục áp dụng</label>
              <select class="form-select" name="categoryName" required>
                <option value="" disabled selected>-- Chọn danh mục --</option>
                <c:forEach var="cname" items="${allCategories}">
                  <option value="${cname}">${cname}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-12 d-flex gap-2">
              <button type="submit" class="btn btn-success"><i class="fa fa-check"></i> Gán mã theo danh mục</button>
            </div>
          </form>
          <hr/>
          <form action="${pageContext.request.contextPath}/admin" method="post" class="row g-3">
            <input type="hidden" name="action" value="discounts" />
            <input type="hidden" name="op" value="unassignByCategory" />
            <div class="col-md-4">
              <label class="form-label">Bỏ gán mã (theo danh mục)</label>
              <select class="form-select" name="discountId" required>
                <option value="" disabled selected>-- Chọn mã --</option>
                <c:forEach var="d" items="${discounts}">
                  <option value="${d.discountId}">${d.code} - ${d.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-8">
              <label class="form-label">Danh mục</label>
              <select class="form-select" name="categoryName" required>
                <option value="" disabled selected>-- Chọn danh mục --</option>
                <c:forEach var="cname" items="${allCategories}">
                  <option value="${cname}">${cname}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-12 d-flex gap-2">
              <button type="submit" class="btn btn-outline-danger"><i class="fa fa-unlink"></i> Bỏ gán theo danh mục</button>
            </div>
          </form>
        </div>
        <!-- By Price -->
        <div class="tab-pane fade" id="by-price" role="tabpanel">
          <form action="${pageContext.request.contextPath}/admin" method="post" class="row g-3">
            <input type="hidden" name="action" value="discounts" />
            <input type="hidden" name="op" value="assignByPrice" />
            <div class="col-md-4">
              <label class="form-label">Chọn mã giảm giá</label>
              <select class="form-select" name="discountId" required>
                <option value="" disabled selected>-- Chọn mã --</option>
                <c:forEach var="d" items="${discounts}">
                  <option value="${d.discountId}">${d.code} - ${d.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-8">
              <div class="row g-2">
                <div class="col-md-6">
                  <label class="form-label">Giá từ (VNĐ)</label>
                  <input type="number" class="form-control" name="minPrice" min="0" step="1000" placeholder="VD: 100000">
                </div>
                <div class="col-md-6">
                  <label class="form-label">Đến (VNĐ)</label>
                  <input type="number" class="form-control" name="maxPrice" min="0" step="1000" placeholder="VD: 500000">
                </div>
              </div>
              <div class="form-text">Để trống 1 đầu mút nếu muốn áp dụng một phía.</div>
            </div>
            <div class="col-12 d-flex gap-2">
              <button type="submit" class="btn btn-success"><i class="fa fa-check"></i> Gán mã theo mức giá</button>
            </div>
          </form>
          <hr/>
          <form action="${pageContext.request.contextPath}/admin" method="post" class="row g-3">
            <input type="hidden" name="action" value="discounts" />
            <input type="hidden" name="op" value="unassignByPrice" />
            <div class="col-md-4">
              <label class="form-label">Bỏ gán mã (theo mức giá)</label>
              <select class="form-select" name="discountId" required>
                <option value="" disabled selected>-- Chọn mã --</option>
                <c:forEach var="d" items="${discounts}">
                  <option value="${d.discountId}">${d.code} - ${d.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-8">
              <div class="row g-2">
                <div class="col-md-6">
                  <label class="form-label">Giá từ (VNĐ)</label>
                  <input type="number" class="form-control" name="minPrice" min="0" step="1000" placeholder="VD: 100000">
                </div>
                <div class="col-md-6">
                  <label class="form-label">Đến (VNĐ)</label>
                  <input type="number" class="form-control" name="maxPrice" min="0" step="1000" placeholder="VD: 500000">
                </div>
              </div>
            </div>
            <div class="col-12 d-flex gap-2">
              <button type="submit" class="btn btn-outline-danger"><i class="fa fa-unlink"></i> Bỏ gán theo mức giá</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="/admin/includes/footer.jspf" %>


