<%@page import="Model.user"%>
<%@page import="DAO.BannerDB, DAO.CategoryDB, DAO.ProductDB, java.util.List, Model.Banner, Model.Category, Model.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/collection.css">
        <title>Pinky Cloud - Trang Chủ</title>
    </head>
    <body>
        <%@ include file="/View/includes/header.jspf" %>

        <%
            // Load banners
            if (request.getAttribute("bannerList") == null) {
                BannerDB bannerDB = new BannerDB();
                request.setAttribute("bannerList", bannerDB.listActiveOrdered());
            }
            
            // Load categories
            if (request.getAttribute("categoryList") == null) {
                CategoryDB categoryDB = new CategoryDB();
                request.setAttribute("categoryList", categoryDB.listAll());
            }
            
            // Load featured products (top 8 sản phẩm mới nhất)
            if (request.getAttribute("featuredProducts") == null) {
                ProductDB productDB = new ProductDB();
                request.setAttribute("featuredProducts", productDB.getFeaturedProducts(8));
            }
            
            // Load best selling products (top 8 sản phẩm bán chạy nhất)
            if (request.getAttribute("bestSellingProducts") == null) {
                ProductDB productDB = new ProductDB();
                request.setAttribute("bestSellingProducts", productDB.getBestSellingProducts(8));
            }
        %>

        <c:if test="${not empty param.msg}">
            <div class="container mt-3">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${param.msg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </div>
        </c:if>

        <!-- Modal Đăng Ký -->
        <div class="modal" id="registerModal">
            <div class="modal-content">
                <span class="close">&times;</span>
                <h2>Đăng ký</h2>
                <form>
                    <div class="input-group">
                        <label for="fullname">Họ và tên</label>
                        <input type="text" id="fullname" placeholder="Nhập họ và tên" required>
                    </div>
                    <div class="input-group">
                        <label for="emailReg">Email</label>
                        <input type="email" id="emailReg" placeholder="Nhập email của bạn" required>
                    </div>
                    <div class="input-group">
                        <label for="passwordReg">Mật khẩu</label>
                        <input type="password" id="passwordReg" placeholder="Tạo mật khẩu" required>
                    </div>
                    <button type="submit">Đăng ký</button>
                </form>
                <p>Đã có tài khoản? <a href="#" id="showLogin">Đăng nhập</a></p>
            </div>
        </div>
                    
        <!-- ========== BANNER CAROUSEL ========== -->
        <div id="carouselExample" class="carousel slide mt-3" data-bs-ride="carousel" data-bs-interval="3000">
            <div class="carousel-inner">
                <c:if test="${empty bannerList}">
                    <div class="carousel-item active">
                        <img src="${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png"
                             class="d-block w-100"
                             alt="Banner"
                             style="max-height: 550px; object-fit: contain;">
                    </div>
                </c:if>
                <c:forEach var="banner" items="${bannerList}" varStatus="loop">
                    <div class="carousel-item ${loop.index == 0 ? 'active' : ''}">
                        <c:choose>
                            <c:when test="${not empty banner.targetUrl}">
                                <c:choose>
                                    <c:when test="${fn:startsWith(banner.targetUrl, 'http://') || fn:startsWith(banner.targetUrl, 'https://')}">
                                        <a href="${banner.targetUrl}" target="_blank" rel="noopener noreferrer">
                                            <img src="${pageContext.request.contextPath}${banner.imagePath}"
                                                 class="d-block w-100"
                                                 alt="Banner"
                                                 style="max-height: 550px; object-fit: contain;">
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}${banner.targetUrl}">
                                            <img src="${pageContext.request.contextPath}${banner.imagePath}"
                                                 class="d-block w-100"
                                                 alt="Banner"
                                                 style="max-height: 550px; object-fit: contain;">
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}${banner.imagePath}"
                                     class="d-block w-100"
                                     alt="Banner"
                                     style="max-height: 550px; object-fit: contain;">
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>

            <!-- Nút điều hướng -->
            <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample" data-bs-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carouselExample" data-bs-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Next</span>
            </button>
        </div>

        <!-- ========== KHỐI DANH MỤC SẢN PHẨM ========== -->
        <div class="container mt-5 mb-4">
            <div class="text-center mb-4">
                <h2 class="text" style="color: #f76c85; font-weight: 800; font-family: 'Times New Roman', Times, serif;">
                    DANH MỤC SẢN PHẨM
                </h2>
                <p class="text-muted">Khám phá các sản phẩm làm đẹp chất lượng cao</p>
            </div>
            
            <c:choose>
                <c:when test="${empty categoryList}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle"></i> Chưa có danh mục sản phẩm nào.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="category-carousel-wrapper">
                        <button type="button" class="category-carousel-btn category-carousel-btn-prev" id="categoryPrevBtn" aria-label="Previous">
                            <i class="fas fa-chevron-left"></i>
                        </button>
                        <div class="category-carousel-container" id="categoryCarousel">
                            <div class="category-carousel-track" id="categoryTrack">
                                <c:forEach var="category" items="${categoryList}" varStatus="loop">
                                    <c:url var="categoryUrl" value="/products">
                                        <c:param name="category" value="${category.name}"/>
                                    </c:url>
                                    <a href="${categoryUrl}" class="category-card-home text-decoration-none">
                                        <div class="category-icon-home">
                                            <c:choose>
                                                <c:when test="${not empty category.imageUrl}">
                                                    <img src="${pageContext.request.contextPath}${category.imageUrl}" 
                                                         alt="${fn:escapeXml(category.name)}" 
                                                         class="category-image" 
                                                         onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                                                    <div class="category-fallback-icon" style="display: none;">
                                                        <c:set var="catName" value="${fn:toLowerCase(fn:trim(category.name))}" />
                                                        <c:choose>
                                                            <c:when test="${fn:contains(catName, 'son') || fn:contains(catName, 'môi')}">
                                                                <i class="fas fa-lips"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'kem') || fn:contains(catName, 'dưỡng')}">
                                                                <i class="fas fa-spa"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'rửa') || fn:contains(catName, 'sữa')}">
                                                                <i class="fas fa-pump-soap"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'mặt nạ') || fn:contains(catName, 'mask')}">
                                                                <i class="fas fa-mask"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'phấn') || fn:contains(catName, 'nền')}">
                                                                <i class="fas fa-palette"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'tóc') || fn:contains(catName, 'gội')}">
                                                                <i class="fas fa-cut"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'nước hoa') || fn:contains(catName, 'perfume')}">
                                                                <i class="fas fa-spray-can"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'phụ kiện') || fn:contains(catName, 'accessory')}">
                                                                <i class="fas fa-brush"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'mắt') || fn:contains(catName, 'eye')}">
                                                                <i class="fas fa-eye"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'serum')}">
                                                                <i class="fas fa-flask"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'tẩy') || fn:contains(catName, 'trang')}">
                                                                <i class="fas fa-hand-sparkles"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'nắng') || fn:contains(catName, 'sun')}">
                                                                <i class="fas fa-sun"></i>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fas fa-cube"></i>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="category-fallback-icon">
                                                        <c:set var="catName" value="${fn:toLowerCase(fn:trim(category.name))}" />
                                                        <c:choose>
                                                            <c:when test="${fn:contains(catName, 'son') || fn:contains(catName, 'môi')}">
                                                                <i class="fas fa-lips"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'kem') || fn:contains(catName, 'dưỡng')}">
                                                                <i class="fas fa-spa"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'rửa') || fn:contains(catName, 'sữa')}">
                                                                <i class="fas fa-pump-soap"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'mặt nạ') || fn:contains(catName, 'mask')}">
                                                                <i class="fas fa-mask"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'phấn') || fn:contains(catName, 'nền')}">
                                                                <i class="fas fa-palette"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'tóc') || fn:contains(catName, 'gội')}">
                                                                <i class="fas fa-cut"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'nước hoa') || fn:contains(catName, 'perfume')}">
                                                                <i class="fas fa-spray-can"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'phụ kiện') || fn:contains(catName, 'accessory')}">
                                                                <i class="fas fa-brush"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'mắt') || fn:contains(catName, 'eye')}">
                                                                <i class="fas fa-eye"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'serum')}">
                                                                <i class="fas fa-flask"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'tẩy') || fn:contains(catName, 'trang')}">
                                                                <i class="fas fa-hand-sparkles"></i>
                                                            </c:when>
                                                            <c:when test="${fn:contains(catName, 'nắng') || fn:contains(catName, 'sun')}">
                                                                <i class="fas fa-sun"></i>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fas fa-cube"></i>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <h6 class="category-name-home mt-2 mb-0">${fn:escapeXml(category.name)}</h6>
                                    </a>
                                </c:forEach>
                            </div>
                        </div>
                        <button type="button" class="category-carousel-btn category-carousel-btn-next" id="categoryNextBtn" aria-label="Next">
                            <i class="fas fa-chevron-right"></i>
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- ========== KHỐI SẢN PHẨM NỔI BẬT ========== -->
        <div class="container mt-5 mb-5">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="text mb-2" style="color: #f76c85; font-weight: 800; font-family: 'Times New Roman', Times, serif;">
                        <i class="fas fa-star text-warning"></i> SẢN PHẨM NỔI BẬT
                    </h2>
                    <p class="text-muted mb-0">Những sản phẩm được yêu thích nhất</p>
                </div>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary d-none d-md-block">
                    Khám phá <i class="fas fa-arrow-right ms-1"></i>
                </a>
            </div>
            
            <c:choose>
                <c:when test="${empty featuredProducts}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle"></i> Chưa có sản phẩm nào. Vui lòng quay lại sau.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="product-grid">
                        <c:forEach var="product" items="${featuredProducts}">
                            <div class="product-card"
                                 onclick="window.location.href='${pageContext.request.contextPath}/product-detail?id=${product.productId}'"
                                 style="cursor: pointer;">
                                <c:if test="${product.discountActive}">
                                    <div class="discount-flag">
                                        <c:choose>
                                            <c:when test="${product.activeDiscount.type == 'PERCENTAGE'}">
                                                -<fmt:formatNumber value="${product.activeDiscount.value}" maxFractionDigits="0"/>%
                                            </c:when>
                                            <c:otherwise>
                                                -<fmt:formatNumber value="${product.activeDiscount.value}" type="number" maxFractionDigits="0"/> VNĐ
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
                                <c:choose>
                                    <c:when test="${not empty product.imageUrl}">
                                        <img src="${pageContext.request.contextPath}${product.imageUrl}"
                                             alt="${fn:escapeXml(product.name)}"
                                             loading="lazy"
                                             onerror="this.src='${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png'">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png"
                                             alt="${fn:escapeXml(product.name)}"
                                             loading="lazy">
                                    </c:otherwise>
                                </c:choose>
                                <div class="product-card-body">
                                    <h5>${fn:escapeXml(product.name)}</h5>
                                    <c:choose>
                                        <c:when test="${product.discountActive}">
                                            <div class="price-wrapper">
                                                <span class="price-old">
                                                    <fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="0" /> VNĐ
                                                </span>
                                                <span class="price-new">
                                                    <fmt:formatNumber value="${product.discountedPrice}" type="number" maxFractionDigits="0" /> VNĐ
                                                </span>
                                                <c:if test="${product.discountAmount > 0}">
                                                    <span class="price-save">
                                                        Tiết kiệm <fmt:formatNumber value="${product.discountAmount}" type="number" maxFractionDigits="0" /> VNĐ
                                                    </span>
                                                </c:if>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="price">
                                                <fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="0" /> VNĐ
                                            </p>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/addToCart?id=${product.productId}"
                                           class="btn btn-sm btn-buy-now"
                                           onclick="event.stopPropagation();">
                                            <i class="fas fa-shopping-bag"></i> Mua ngay
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    
                    <!-- Nút xem tất cả cho mobile -->
                    <div class="text-center mt-4 d-md-none">
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">
                            Xem tất cả sản phẩm <i class="fas fa-arrow-right ms-2"></i>
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- ========== KHỐI SẢN PHẨM BÁN CHẠY NHẤT ========== -->
        <div class="best-selling-section">
            <div class="container mt-5 mb-5">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2 class="text mb-2" style="color: #f76c85; font-weight: 800; font-family: 'Times New Roman', Times, serif;">
                            <i class="fas fa-fire text-danger"></i> SẢN PHẨM BÁN CHẠY NHẤT
                        </h2>
                        <p class="text-muted mb-0">Những sản phẩm được khách hàng yêu thích nhất</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary d-none d-md-block">
                        Khám phá <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            
                <c:choose>
                    <c:when test="${empty bestSellingProducts}">
                        <div class="alert alert-info text-center">
                            <i class="fas fa-info-circle"></i> Chưa có sản phẩm bán chạy nào. Vui lòng quay lại sau.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="product-grid">
                            <c:forEach var="product" items="${bestSellingProducts}">
                                <div class="product-card"
                                     onclick="window.location.href='${pageContext.request.contextPath}/product-detail?id=${product.productId}'"
                                     style="cursor: pointer;">
                                    <div class="best-selling-badge">
                                        <i class="fas fa-fire"></i> Bán chạy
                                    </div>
                                    <c:if test="${product.discountActive}">
                                        <div class="discount-flag" style="left: auto; right: 12px; top: 52px;">
                                            <c:choose>
                                                <c:when test="${product.activeDiscount.type == 'PERCENTAGE'}">
                                                    -<fmt:formatNumber value="${product.activeDiscount.value}" maxFractionDigits="0"/>%
                                                </c:when>
                                                <c:otherwise>
                                                    -<fmt:formatNumber value="${product.activeDiscount.value}" type="number" maxFractionDigits="0" /> VNĐ
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${not empty product.imageUrl}">
                                            <img src="${pageContext.request.contextPath}${product.imageUrl}"
                                                 alt="${fn:escapeXml(product.name)}"
                                                 loading="lazy"
                                                 onerror="this.src='${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png'">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/IMG/hinhnen-placeholder.png"
                                                 alt="${fn:escapeXml(product.name)}"
                                                 loading="lazy">
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="product-card-body">
                                        <h5>${fn:escapeXml(product.name)}</h5>
                                        <c:choose>
                                            <c:when test="${product.discountActive}">
                                                <div class="price-wrapper">
                                                    <span class="price-old">
                                                        <fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="0" /> VNĐ
                                                    </span>
                                                    <span class="price-new">
                                                        <fmt:formatNumber value="${product.discountedPrice}" type="number" maxFractionDigits="0" /> VNĐ
                                                    </span>
                                                    <c:if test="${product.discountAmount > 0}">
                                                        <span class="price-save">
                                                            Tiết kiệm <fmt:formatNumber value="${product.discountAmount}" type="number" maxFractionDigits="0" /> VNĐ
                                                        </span>
                                                    </c:if>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <p class="price">
                                                    <fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="0" /> VNĐ
                                                </p>
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/addToCart?id=${product.productId}"
                                               class="btn btn-sm btn-buy-now"
                                               onclick="event.stopPropagation();">
                                                <i class="fas fa-shopping-bag"></i> Mua ngay
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Nút xem tất cả cho mobile -->
                        <div class="text-center mt-4 d-md-none">
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">
                                Xem tất cả sản phẩm <i class="fas fa-arrow-right ms-2"></i>
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- ========== KHỐI CHI NHÁNH PHÂN PHỐI ========== -->
        <div class="container mt-5 mb-5">
            <div class="text-center mb-4">
                <h2 class="text-uppercase fw-bold" style="color: #f76c85;">
                    Chi Nhánh Phân Phối
                </h2>
            </div>
            <div class="row mt-4">
                <!-- Cột chứa bản đồ -->
                <div class="col-md-6 map-container text-center mb-4 mb-md-0">
                    <img src="${pageContext.request.contextPath}/IMG/map.jpg" 
                         alt="Bản đồ phân phối" 
                         class="img-fluid rounded shadow">
                </div>
                <!-- Cột chứa thông tin chi nhánh -->
                <div class="col-md-6 branch-info">
                    <h3>Trụ Sở Chính</h3>
                    <p><strong>Cửa hàng mỹ phẩm PinkyCloud</strong></p>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> Số 31, đường Nguyễn Thị Minh Khai, Phường Quy Nhơn, Gia Lai</p>
                    <p><i class="fas fa-envelope text-info"></i> pinkycloudvietnam@gmail.com</p>
                    <p><i class="fas fa-globe text-success"></i> <a href="${pageContext.request.contextPath}/View/home.jsp" target="_blank" style="color: #24e454; text-decoration: none;">pinkycloud.vn</a></p>

                    <h3 class="mt-4">Hệ Thống Phòng Kinh Doanh</h3>
                    <h5>TOCEPO THỊ NẠI</h5>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> 224 Đống Đa, Thị Nải, Quy Nhơn, Bình Định, Việt Nam</p>
                    <p><i class="fas fa-phone text-primary"></i> 0888.004.444 - 0888.885.884 (Zalo)</p>

                    <h5>Quán Nhậu Aking 2</h5>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> 153-155 Bùi Xuân Phái, Trần Hưng Đạo, Quy Nhơn, Bình Định, Việt Nam</p>
                    <p><i class="fas fa-phone text-primary"></i> 0833.55.4444 (Zalo)</p>

                    <h3 class="mt-4">Nhà Phân Phối KEEPFLY</h3>
                    <h5><i class="fas fa-home"></i> Bình Dương</h5>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> 1/5 KP. Bình Quới A, P. Bình Chuẩn, TP. Thuận An</p>

                    <h5><i class="fas fa-home"></i> TP. Nha Trang</h5>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> 17/11/1 đường 52, Vĩnh Phước, Tp Nha Trang</p>

                    <h5><i class="fas fa-home"></i> Kiên Giang</h5>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> 151 Quang Trung, Vĩnh Thạnh, Rạch Giá, Kiên Giang</p>

                    <h3 class="mt-4">PINKYCLOUD SHOWROOM</h3>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> 15-17 Tô Hiệu, Tân Phú, TP. Hồ Chí Minh</p>
                    <p><i class="fas fa-map-marker-alt text-danger"></i> 56 Ngô Gia Tự, Tây Sơn, Bình Định</p>
                </div>
            </div>
        </div>

        <%@ include file="/View/includes/footer.jspf" %>
        <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    </body>
</html>
