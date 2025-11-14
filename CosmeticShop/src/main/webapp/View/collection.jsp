<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>PinkyCloud - Bộ sưu tập</title>

                <!-- Preconnect để tối ưu tải font và CDN -->
                <link rel="preconnect" href="https://cdnjs.cloudflare.com">
                <link rel="dns-prefetch" href="https://cdnjs.cloudflare.com">
                
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css?v=2.1">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" 
                      integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA=="
                      crossorigin="anonymous" referrerpolicy="no-referrer">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/collection.css?v=2.1">
                
            </head>

            <body 
                <c:if test="${not empty sessionScope.cartSuccessMsg}">data-success-msg="${sessionScope.cartSuccessMsg}"</c:if>
                <c:if test="${not empty sessionScope.cartErrorMsg}">data-error-msg="${sessionScope.cartErrorMsg}"</c:if>
            >
              <c:remove var="cartSuccessMsg" scope="session" />
              <c:remove var="cartErrorMsg" scope="session" />
              <%@ include file="/View/includes/header.jspf" %>
              <%@ include file="/View/includes/mobile-search.jspf" %>
                    <main class="container-fluid my-5">
                        
                        <div class="text-center mb-4">
                            <h2 class="fw-bold" style="color: #f76c85; font-family: 'Times New Roman', Times, serif;">BỘ
                                SƯU TẬP SẢN PHẨM</h2>
                            <p>Khám phá những sản phẩm làm đẹp tốt nhất dành cho bạn.</p>
                        </div>

                        <div class="row">
                            <!-- Sidebar Filter - Bên trái -->
                            <div class="col-lg-3 col-md-4 mb-4">
                                <div class="filter-sidebar">
                                    <div class="filter-header">
                                        <h5 class="mb-3">
                                            <i class="fas fa-filter me-2"></i>Bộ lọc sản phẩm
                                        </h5>
                                    </div>

                                    <form method="GET" action="${pageContext.request.contextPath}/products"
                                        class="filter-form">
                                        <!-- Danh mục -->
                                        <div class="filter-section mb-3">
                                            <label for="category" class="form-label">
                                                <i class="fas fa-tags me-1"></i>Danh mục
                                            </label>
                                            <select class="form-select form-select-sm" id="category" name="category">
                                                <option value="">Tất cả</option>
                                                <c:forEach var="category" items="${categories}">
                                                    <option value="${category}" ${selectedCategory==category ? 'selected' : '' }>
                                                        ${category}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <!-- Khoảng giá -->
                                        <div class="filter-section mb-3">
                                            <label for="fixedPriceRange" class="form-label">
                                                <i class="fas fa-money-bill-wave me-1"></i>Khoảng giá
                                            </label>
                                            <select class="form-select form-select-sm" id="fixedPriceRange" name="fixedPriceRange">
                                                <option value="">Tất cả</option>
                                                <option value="under-100k" ${selectedFixedPriceRange=='under-100k' ? 'selected' : '' }>Dưới 100k</option>
                                                <option value="100k-300k" ${selectedFixedPriceRange=='100k-300k' ? 'selected' : '' }>100k - 300k</option>
                                                <option value="300k-500k" ${selectedFixedPriceRange=='300k-500k' ? 'selected' : '' }>300k - 500k</option>
                                                <option value="500k-1m" ${selectedFixedPriceRange=='500k-1m' ? 'selected' : '' }>500k - 1tr</option>
                                                <option value="over-1m" ${selectedFixedPriceRange=='over-1m' ? 'selected' : '' }>Trên 1tr</option>
                                            </select>
                                        </div>

                                        <!-- Giá tùy chỉnh -->
                                        <div class="filter-section mb-3">
                                            <label class="form-label">
                                                <i class="fas fa-sliders-h me-1"></i>Giá tùy chỉnh
                                            </label>
                                            <div class="row g-2">
                                                <div class="col-6">
                                                    <input type="number" class="form-control form-control-sm" id="minPrice"
                                                        name="minPrice" placeholder="Từ" min="0" value="${minPrice}">
                                                </div>
                                                <div class="col-6">
                                                    <input type="number" class="form-control form-control-sm" id="maxPrice"
                                                        name="maxPrice" placeholder="Đến" min="0" value="${maxPrice}">
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Sắp xếp -->
                                        <div class="filter-section mb-3">
                                            <label for="sortBy" class="form-label">
                                                <i class="fas fa-sort me-1"></i>Sắp xếp
                                            </label>
                                            <select class="form-select form-select-sm" id="sortBy" name="sortBy">
                                                <option value="">Mặc định</option>
                                                <option value="price-asc" ${selectedSortBy=='price-asc' ? 'selected' : '' }>Giá tăng</option>
                                                <option value="price-desc" ${selectedSortBy=='price-desc' ? 'selected' : '' }>Giá giảm</option>
                                                <option value="name-asc" ${selectedSortBy=='name-asc' ? 'selected' : '' }>Tên A-Z</option>
                                                <option value="name-desc" ${selectedSortBy=='name-desc' ? 'selected' : '' }>Tên Z-A</option>
                                                <option value="newest" ${selectedSortBy=='newest' ? 'selected' : '' }>Mới nhất</option>
                                                <option value="oldest" ${selectedSortBy=='oldest' ? 'selected' : '' }>Cũ nhất</option>
                                            </select>
                                        </div>

                                        <!-- Nút lọc -->
                                        <div class="filter-actions">
                                            <button type="submit" class="btn btn-primary btn-sm w-100 mb-2">
                                                <i class="fas fa-check me-1"></i>Áp dụng
                                            </button>
                                            <a href="${pageContext.request.contextPath}/products"
                                                class="btn btn-outline-secondary btn-sm w-100">
                                                <i class="fas fa-redo me-1"></i>Đặt lại
                                            </a>
                                        </div>

                                        <!-- Thông tin bộ lọc hiện tại -->
                                        <c:if
                                            test="${not empty searchTerm || not empty selectedCategory || not empty selectedFixedPriceRange || not empty minPrice || not empty maxPrice || not empty selectedSortBy}">
                                            <div class="current-filters mt-4">
                                                <h6 class="text-muted mb-2">
                                                    <i class="fas fa-info-circle me-1"></i>Bộ lọc đang áp dụng:
                                                </h6>
                                                <div class="d-flex flex-wrap gap-1">
                                                    <c:if test="${not empty searchTerm}">
                                                        <span class="badge bg-primary">Tìm: "${searchTerm}"</span>
                                                    </c:if>
                                                    <c:if test="${not empty selectedCategory}">
                                                        <span class="badge bg-info">${selectedCategory}</span>
                                                    </c:if>
                                                    <c:if test="${not empty selectedFixedPriceRange}">
                                                        <span class="badge bg-success">Khoảng giá</span>
                                                    </c:if>
                                                    <c:if test="${not empty minPrice || not empty maxPrice}">
                                                        <span class="badge bg-warning">Giá tùy chỉnh</span>
                                                    </c:if>
                                                    <c:if test="${not empty selectedSortBy}">
                                                        <span class="badge bg-secondary">
                                                            <c:choose>
                                                                <c:when test="${selectedSortBy == 'price-asc'}">💰
                                                                    Thấp→Cao</c:when>
                                                                <c:when test="${selectedSortBy == 'price-desc'}">💰
                                                                    Cao→Thấp</c:when>
                                                                <c:when test="${selectedSortBy == 'name-asc'}">📝 A→Z
                                                                </c:when>
                                                                <c:when test="${selectedSortBy == 'name-desc'}">📝 Z→A
                                                                </c:when>
                                                                <c:when test="${selectedSortBy == 'newest'}">🆕 Mới
                                                                </c:when>
                                                                <c:when test="${selectedSortBy == 'oldest'}">📅 Cũ
                                                                </c:when>
                                                            </c:choose>
                                                        </span>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:if>
                                    </form>
                                </div>
                            </div>

                            <!-- Sản phẩm - Bên phải -->
                            <div class="col-lg-9 col-md-8">
                                <!-- Thông tin kết quả -->
                                <div class="results-header mb-4">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <h5 class="mb-1">Kết quả tìm kiếm</h5>
                                            <small class="text-muted">
                                                <c:choose>
                                                    <c:when test="${empty productList}">
                                                        Không tìm thấy sản phẩm nào
                                                    </c:when>
                                                    <c:otherwise>
                                                        Hiển thị ${(currentPage-1)*pageSize +
                                                        1}-${(currentPage-1)*pageSize + productList.size()} trong tổng
                                                        số ${totalProducts} sản phẩm
                                                    </c:otherwise>
                                                </c:choose>
                                            </small>
                                        </div>
                                        <div class="view-options">
                                            <div class="btn-group" role="group">
                                                <button type="button" class="btn btn-outline-secondary active"
                                                    id="gridView">
                                                    <i class="fas fa-th"></i>
                                                </button>
                                                <button type="button" class="btn btn-outline-secondary" id="listView">
                                                    <i class="fas fa-list"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <%-- Lưới hiển thị danh sách sản phẩm --%>
                                    <c:choose>
                                        <c:when test="${empty productList}">
                                            <div class="no-results">
                                                <i class="fas fa-search"></i>
                                                <h4>Không tìm thấy sản phẩm nào</h4>
                                                <p>Hãy thử tìm kiếm với từ khóa khác hoặc điều chỉnh bộ lọc của bạn.</p>
                                                <a href="${pageContext.request.contextPath}/products"
                                                    class="btn btn-primary mt-3">
                                                     Xem tất cả sản phẩm
                                                </a>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="product-grid" id="productGrid">
                                                <c:forEach var="product" items="${productList}">
                                                    <div class="product-card"
                                                        onclick="window.location.href='${pageContext.request.contextPath}/product-detail?id=${product.productId}'"
                                                        style="cursor: pointer;">
                                                        <c:if test="${product.discountActive}">
                                                            <div class="discount-flag">
                                                                <c:choose>
                                                                    <c:when test="${product.activeDiscount.type == 'PERCENTAGE'}">
                                                                        -<fmt:formatNumber value="${product.activeDiscount.value}" maxFractionDigits="0" />%
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        -<fmt:formatNumber value="${product.activeDiscount.value}" type="number" maxFractionDigits="0" /> ₫
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </c:if>
                                                        <c:choose>
                                                            <c:when test="${not empty product.imageUrl and product.imageUrl != ''}">
                                                                <img src="${pageContext.request.contextPath}${product.imageUrl}"
                                                                    alt="${fn:escapeXml(product.name)}" 
                                                                    loading="lazy"
                                                                    decoding="async"
                                                                    onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/IMG/logo.png';"
                                                                    style="background-color: #f8f9fa;">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${pageContext.request.contextPath}/IMG/logo.png"
                                                                    alt="${fn:escapeXml(product.name)}" 
                                                                    loading="lazy"
                                                                    decoding="async"
                                                                    style="background-color: #f8f9fa;">
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div class="product-card-body">
                                                            <h5>
                                                                <c:out value="${product.name}" />
                                                            </h5>
                                                            <c:choose>
                                                                <c:when test="${product.discountActive}">
                                                                    <c:if test="${not empty product.activeDiscount.endDate}">
                                                                        <div class="sale-countdown-container mb-2" 
                                                                             data-end-time="${product.activeDiscount.endDate.time}"
                                                                             data-start-time="${product.activeDiscount.startDate.time}">
                                                                            <div class="countdown-label">
                                                                                <i class="far fa-clock"></i>
                                                                                <span class="countdown-text">Đang tải...</span>
                                                                            </div>
                                                                            <div class="countdown-progress-bar">
                                                                                <div class="countdown-progress-fill"></div>
                                                                            </div>
                                                                        </div>
                                                                    </c:if>
                                                                    <div class="price-wrapper">
                                                                        <span class="price-old">
                                                                            <fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="0" /> ₫
                                                                        </span>
                                                                        <span class="price-new">
                                                                            <fmt:formatNumber value="${product.discountedPrice}" type="number" maxFractionDigits="0" /> ₫
                                                                        </span>
                                                                        <c:if test="${product.discountAmount > 0}">
                                                                            <span class="price-save">
                                                                                Tiết kiệm <fmt:formatNumber value="${product.discountAmount}" type="number" maxFractionDigits="0" /> ₫
                                                                            </span>
                                                                        </c:if>
                                                                    </div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <p class="price">
                                                                        <fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="0" /> ₫
                                                                    </p>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <div class="action-buttons">
                                                                <a href="${pageContext.request.contextPath}/addToCart?id=${product.productId}&buyNow=true"
                                                                    class="btn btn-sm btn-buy-now"
                                                                    onclick="event.stopPropagation();">
                                                                    <i class="fas fa-shopping-bag"></i> Mua ngay
                                                                </a>
                                                                <c:if test="${not empty sessionScope.user}">
                                                                    <button type="button" 
                                                                            class="btn btn-sm ${wishlistProductIds.contains(product.productId) ? 'btn-danger' : 'btn-outline-danger'}" 
                                                                            onclick="event.stopPropagation(); toggleWishlist(${product.productId}, this);"
                                                                            data-product-id="${product.productId}"
                                                                            title="${wishlistProductIds.contains(product.productId) ? 'Xóa khỏi wishlist' : 'Thêm vào wishlist'}">
                                                                        <i class="${wishlistProductIds.contains(product.productId) ? 'fas' : 'far'} fa-heart"></i>
                                                                    </button>
                                                                </c:if>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>

                                    <!-- Phân trang -->
                                    <c:if test="${totalPages > 1}">
                                        <nav aria-label="Phân trang sản phẩm" class="mt-5">
                                            <ul class="pagination justify-content-center">
                                                <!-- Nút Trang đầu -->
                                                <c:if test="${currentPage > 1}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                            href="${pageContext.request.contextPath}/products?page=1&pageSize=${pageSize}&search=${searchTerm}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&fixedPriceRange=${selectedFixedPriceRange}&sortBy=${selectedSortBy}">
                                                            <i class="fas fa-angle-double-left"></i>
                                                        </a>
                                                    </li>
                                                </c:if>

                                                <!-- Nút Trang trước -->
                                                <c:if test="${currentPage > 1}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                            href="${pageContext.request.contextPath}/products?page=${currentPage-1}&pageSize=${pageSize}&search=${searchTerm}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&fixedPriceRange=${selectedFixedPriceRange}&sortBy=${selectedSortBy}">
                                                            <i class="fas fa-angle-left"></i>
                                                        </a>
                                                    </li>
                                                </c:if>

                                                <!-- Hiển thị các trang -->
                                                <c:set var="startPage" value="${currentPage - 2}" />
                                                <c:set var="endPage" value="${currentPage + 2}" />

                                                <c:if test="${startPage < 1}">
                                                    <c:set var="startPage" value="1" />
                                                    <c:set var="endPage" value="5" />
                                                </c:if>

                                                <c:if test="${endPage > totalPages}">
                                                    <c:set var="endPage" value="${totalPages}" />
                                                    <c:if test="${endPage - 4 > 0}">
                                                        <c:set var="startPage" value="${endPage - 4}" />
                                                    </c:if>
                                                </c:if>

                                                <c:forEach var="i" begin="${startPage}" end="${endPage}">
                                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                        <a class="page-link"
                                                            href="${pageContext.request.contextPath}/products?page=${i}&pageSize=${pageSize}&search=${searchTerm}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&fixedPriceRange=${selectedFixedPriceRange}&sortBy=${selectedSortBy}">
                                                            ${i}
                                                        </a>
                                                    </li>
                                                </c:forEach>

                                                <!-- Nút Trang sau -->
                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                            href="${pageContext.request.contextPath}/products?page=${currentPage+1}&pageSize=${pageSize}&search=${searchTerm}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&fixedPriceRange=${selectedFixedPriceRange}&sortBy=${selectedSortBy}">
                                                            <i class="fas fa-angle-right"></i>
                                                        </a>
                                                    </li>
                                                </c:if>

                                                <!-- Nút Trang cuối -->
                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                            href="${pageContext.request.contextPath}/products?page=${totalPages}&pageSize=${pageSize}&search=${searchTerm}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&fixedPriceRange=${selectedFixedPriceRange}&sortBy=${selectedSortBy}">
                                                            <i class="fas fa-angle-double-right"></i>
                                                        </a>
                                                    </li>
                                                </c:if>
                                            </ul>

                                            <!-- Thông tin trang hiện tại + nhập số trang -->
                                            <div class="text-center mt-3">
                                                <form method="get" action="${pageContext.request.contextPath}/products"
                                                    class="d-inline-flex align-items-center">
                                                    <input type="hidden" name="search" value="${searchTerm}">
                                                    <input type="hidden" name="category" value="${selectedCategory}">
                                                    <input type="hidden" name="minPrice" value="${minPrice}">
                                                    <input type="hidden" name="maxPrice" value="${maxPrice}">
                                                    <input type="hidden" name="fixedPriceRange"
                                                        value="${selectedFixedPriceRange}">
                                                    <input type="hidden" name="sortBy" value="${selectedSortBy}">
                                                    <input type="hidden" name="pageSize" value="${pageSize}">
                                                    <div class="d-inline-block align-middle"
                                                        style="display: flex; align-items: center;">
                                                        <small class="text-muted"
                                                            style="margin-right:8px;">Trang</small>
                                                        <input type="number" name="page" min="1" max="${totalPages}"
                                                            value="${currentPage}" class="form-control"
                                                            style="width:90px; display:inline-block;">
                                                        <small class="text-muted" style="margin-left:8px;">/
                                                            ${totalPages}</small>
                                                        <button type="submit" class="btn btn-outline-secondary btn-sm"
                                                            style="margin-left:8px;">Đi</button>
                                                    </div>

                                                    <!-- <small class="text-muted" style="margin-right:8px;">Trang</small>
                                                <input type="number" name="page" min="1" max="${totalPages}" value="${currentPage}" class="form-control" style="width:90px; display:inline-block;">
                                                <small class="text-muted" style="margin-left:8px;">/ ${totalPages} </small>
                                                <button type="submit" class="btn btn-outline-secondary btn-sm" style="margin-left:8px;">Đi</button> -->
                                                </form>
                                            </div>
                                        </nav>
                                    </c:if>
                            </div>
                        </div>
                    </main>

                    <%@ include file="/View/includes/footer.jspf" %>

                    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
                    <script src="${pageContext.request.contextPath}/Js/home.js"></script>

                    <!-- JavaScript cho tìm kiếm và lọc sản phẩm -->
                    <script>
                        document.addEventListener('DOMContentLoaded', function () {
                            // Lấy các elements
                            const searchInput = document.getElementById('search');
                            const categorySelect = document.getElementById('category');
                            const minPriceInput = document.getElementById('minPrice');
                            const maxPriceInput = document.getElementById('maxPrice');
                            const fixedPriceRangeSelect = document.getElementById('fixedPriceRange');
                            const sortBySelect = document.getElementById('sortBy');
                            const filterForm = document.querySelector('.filter-form');

                            // Xử lý khi chọn lọc giá cố định
                            if (fixedPriceRangeSelect) {
                                fixedPriceRangeSelect.addEventListener('change', function () {
                                    if (this.value) {
                                        // Clear giá tùy chỉnh khi chọn giá cố định
                                        if (minPriceInput) minPriceInput.value = '';
                                        if (maxPriceInput) maxPriceInput.value = '';
                                    }
                                });
                            }

                            // Xử lý khi nhập giá tùy chỉnh
                            if (minPriceInput) {
                                minPriceInput.addEventListener('change', function () {
                                    if (this.value || (maxPriceInput && maxPriceInput.value)) {
                                        // Clear giá cố định khi nhập giá tùy chỉnh
                                        if (fixedPriceRangeSelect) fixedPriceRangeSelect.value = '';
                                    }
                                });
                            }

                            if (maxPriceInput) {
                                maxPriceInput.addEventListener('change', function () {
                                    if (this.value || (minPriceInput && minPriceInput.value)) {
                                        // Clear giá cố định khi nhập giá tùy chỉnh
                                        if (fixedPriceRangeSelect) fixedPriceRangeSelect.value = '';
                                    }
                                });
                            }




                            // Grid/List view toggle
                            const gridViewBtn = document.getElementById('gridView');
                            const listViewBtn = document.getElementById('listView');
                            const productGrid = document.getElementById('productGrid');

                            if (gridViewBtn && listViewBtn && productGrid) {
                                gridViewBtn.addEventListener('click', function () {
                                    productGrid.classList.remove('list-view');
                                    gridViewBtn.classList.add('active');
                                    listViewBtn.classList.remove('active');
                                });

                                listViewBtn.addEventListener('click', function () {
                                    productGrid.classList.add('list-view');
                                    listViewBtn.classList.add('active');
                                    gridViewBtn.classList.remove('active');
                                });
                            }

                            // Enter key để submit form
                            const formInputs = [searchInput, categorySelect, minPriceInput, maxPriceInput, fixedPriceRangeSelect, sortBySelect];
                            formInputs.forEach(input => {
                                if (input) {
                                    input.addEventListener('keypress', function (e) {
                                        if (e.key === 'Enter') {
                                            e.preventDefault();
                                            if (filterForm) filterForm.submit();
                                        }
                                    });
                                }
                            });
                        });
                        
                        // Wishlist toggle function cho danh sách sản phẩm (phải ở global scope để onclick có thể gọi)
                        function toggleWishlist(productId, buttonElement) {
                            if (!buttonElement) {
                                console.error('Button element not found');
                                return;
                            }
                            
                            // Prevent multiple clicks
                            if (buttonElement.disabled) {
                                console.log('Button is already processing, ignoring click');
                                return;
                            }
                            
                            const icon = buttonElement.querySelector('i');
                            
                            const params = new URLSearchParams();
                            params.append('action', 'toggle');
                            params.append('productId', productId.toString());
                            
                            // Debug: Log request contents
                            console.log('Sending wishlist request:', {
                                action: 'toggle',
                                productId: productId
                            });
                            
                            // Disable button during request
                            buttonElement.disabled = true;
                            buttonElement.style.opacity = '0.6';
                            buttonElement.style.cursor = 'not-allowed';
                            
                            fetch('${pageContext.request.contextPath}/wishlist', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/x-www-form-urlencoded',
                                },
                                body: params.toString()
                            })
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('HTTP error! status: ' + response.status);
                                }
                                return response.text().then(text => {
                                    console.log('Raw response:', text);
                                    try {
                                        return JSON.parse(text);
                                    } catch (e) {
                                        console.error('Response is not JSON:', text);
                                        throw new Error('Invalid JSON response');
                                    }
                                });
                            })
                            .then(data => {
                                console.log('Wishlist response:', data);
                                if (data.success) {
                                    // Toggle button state
                                    if (data.inWishlist) {
                                        buttonElement.classList.remove('btn-outline-danger');
                                        buttonElement.classList.add('btn-danger');
                                        if (icon) {
                                            icon.classList.remove('far');
                                            icon.classList.add('fas');
                                        }
                                        buttonElement.title = 'Xóa khỏi wishlist';
                                    } else {
                                        buttonElement.classList.remove('btn-danger');
                                        buttonElement.classList.add('btn-outline-danger');
                                        if (icon) {
                                            icon.classList.remove('fas');
                                            icon.classList.add('far');
                                        }
                                        buttonElement.title = 'Thêm vào wishlist';
                                    }
                                    console.log('Wishlist updated successfully. inWishlist:', data.inWishlist);
                                } else {
                                    console.error('Wishlist update failed:', data.message);
                                    alert(data.message || 'Có lỗi xảy ra');
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                alert('Có lỗi xảy ra khi cập nhật wishlist: ' + error.message);
                            })
                            .finally(() => {
                                buttonElement.disabled = false;
                                buttonElement.style.opacity = '1';
                                buttonElement.style.cursor = 'pointer';
                            });
                        }
                    </script>
                    <script>
                        // ===== COUNTDOWN PROGRESS BAR CHO SALE =====
                        function initSaleCountdown() {
                            const countdowns = document.querySelectorAll('.sale-countdown-container');
                            if (countdowns.length === 0) return;
                            
                            function updateCountdowns() {
                                countdowns.forEach(function(elem) {
                                    const endTime = parseInt(elem.dataset.endTime);
                                    const startTime = parseInt(elem.dataset.startTime);
                                    const now = new Date().getTime();
                                    const distance = endTime - now;
                                    
                                    const textElem = elem.querySelector('.countdown-text');
                                    const progressFill = elem.querySelector('.countdown-progress-fill');
                                    if (!textElem || !progressFill) return;
                                    
                                    if (distance < 0) {
                                        textElem.innerHTML = '<strong>Hết hạn</strong>';
                                        progressFill.style.width = '0%';
                                        progressFill.style.background = '#999';
                                        elem.classList.add('sale-ended');
                                        return;
                                    }
                                    
                                    const totalDuration = endTime - startTime;
                                    const percentRemaining = (distance / totalDuration) * 100;
                                    progressFill.style.width = Math.max(0, Math.min(100, percentRemaining)) + '%';
                                    
                                    if (percentRemaining > 50) {
                                        progressFill.style.background = 'linear-gradient(90deg, #4caf50, #8bc34a)';
                                    } else if (percentRemaining > 20) {
                                        progressFill.style.background = 'linear-gradient(90deg, #ff9800, #ffc107)';
                                    } else {
                                        progressFill.style.background = 'linear-gradient(90deg, #f44336, #ff5722)';
                                    }
                                    
                                    const days = Math.floor(distance / (1000 * 60 * 60 * 24));
                                    const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                                    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                                    const seconds = Math.floor((distance % (1000 * 60)) / 1000);
                                    
                                    let displayText = 'Còn ';
                                    if (days > 0) {
                                        displayText += days + 'd ' + hours + 'h';
                                    } else if (hours > 0) {
                                        displayText += hours + 'h ' + minutes + 'm';
                                    } else {
                                        displayText += minutes + 'm ' + seconds + 's';
                                    }
                                    textElem.textContent = displayText;
                                });
                            }
                            
                            updateCountdowns();
                            setInterval(updateCountdowns, 1000);
                        }
                        
                        if (document.readyState === 'loading') {
                            document.addEventListener('DOMContentLoaded', initSaleCountdown);
                        } else {
                            initSaleCountdown();
                        }
                    </script>
            </body>

            </html>