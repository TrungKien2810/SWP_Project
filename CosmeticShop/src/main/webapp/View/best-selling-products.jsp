<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sản Phẩm Bán Chạy Nhất - PinkyCloud</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" 
          integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA=="
          crossorigin="anonymous" referrerpolicy="no-referrer">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/collection.css">
</head>

<body>
    <%@ include file="/View/includes/header.jspf" %>
    
    <main class="container my-5">
        <div class="text-center mb-5">
            <h2 class="fw-bold" style="color: #f76c85; font-family: 'Times New Roman', Times, serif;">
                <i class="fas fa-fire text-danger"></i> SẢN PHẨM BÁN CHẠY NHẤT
            </h2>
            <p class="text-muted">Những sản phẩm được khách hàng yêu thích và mua nhiều nhất</p>
        </div>

        <!-- Thông tin kết quả -->
        <div class="results-header mb-4">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="mb-1">
                        <i class="fas fa-chart-line me-2"></i>Tổng số sản phẩm
                    </h5>
                    <small class="text-muted">
                        <c:choose>
                            <c:when test="${empty bestSellingProducts}">
                                Không có sản phẩm nào
                            </c:when>
                            <c:otherwise>
                                Hiển thị ${(currentPage-1)*pageSize + 1}-${(currentPage-1)*pageSize + bestSellingProducts.size()} 
                                trong tổng số ${totalProducts} sản phẩm
                            </c:otherwise>
                        </c:choose>
                    </small>
                </div>
            </div>
        </div>

        <!-- Danh sách sản phẩm -->
        <c:choose>
            <c:when test="${empty bestSellingProducts}">
                <div class="alert alert-info text-center">
                    <i class="fas fa-info-circle fa-2x mb-3"></i>
                    <h5>Chưa có sản phẩm bán chạy nào</h5>
                    <p class="mb-0">Vui lòng quay lại sau hoặc xem các sản phẩm khác.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                        <i class="fas fa-home me-2"></i>Về trang chủ
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid mb-5">
                    <c:forEach var="product" items="${bestSellingProducts}">
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
                                            -<fmt:formatNumber value="${product.activeDiscount.value}" type="number" maxFractionDigits="0"/> ₫
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                            
                            <!-- Badge Bán chạy -->
                            <div style="position: absolute; top: 10px; right: 10px; background: linear-gradient(135deg, #ff6b6b, #f76c85); color: white; padding: 6px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; z-index: 10; box-shadow: 0 3px 10px rgba(255, 107, 107, 0.5); animation: badgePulse 2s ease-in-out infinite;">
                                <i class="fas fa-fire"></i> Bán chạy
                            </div>
                            
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
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Phân trang -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Product pagination">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage - 1}&pageSize=${pageSize}">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            </li>
                            
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}&pageSize=${pageSize}">${i}</a>
                                    </li>
                                </c:if>
                                <c:if test="${i == currentPage - 3 || i == currentPage + 3}">
                                    <li class="page-item disabled">
                                        <span class="page-link">...</span>
                                    </li>
                                </c:if>
                            </c:forEach>
                            
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage + 1}&pageSize=${pageSize}">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
        
        <!-- Các trang sản phẩm khác -->
        <div class="row mt-5">
            <div class="col-md-4 mb-3">
                <div class="card border-warning h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-star text-warning fa-3x mb-3"></i>
                        <h5 class="card-title">Sản Phẩm Nổi Bật</h5>
                        <p class="card-text">Xem các sản phẩm được yêu thích nhất</p>
                        <a href="${pageContext.request.contextPath}/featured-products" class="btn btn-warning">
                            Xem ngay <i class="fas fa-arrow-right ms-2"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-3">
                <div class="card border-success h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-tags text-success fa-3x mb-3"></i>
                        <h5 class="card-title">Sản Phẩm Khuyến Mại</h5>
                        <p class="card-text">Xem các sản phẩm đang có giá ưu đãi</p>
                        <a href="${pageContext.request.contextPath}/promotional-products" class="btn btn-success">
                            Xem ngay <i class="fas fa-arrow-right ms-2"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-3">
                <div class="card border-primary h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-th text-primary fa-3x mb-3"></i>
                        <h5 class="card-title">Tất Cả Sản Phẩm</h5>
                        <p class="card-text">Khám phá toàn bộ bộ sưu tập của chúng tôi</p>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                            Khám phá <i class="fas fa-arrow-right ms-2"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/View/includes/footer.jspf" %>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
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

