<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Product p = (Product) request.getAttribute("product");
    String categoryName = (String) request.getAttribute("categoryName");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/product-detail.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <div class="container" style="margin-top: 20px;">
        <!-- Hiển thị thông báo -->
        <c:if test="${not empty requestScope.msg}">
            <div class="alert alert-success alert-dismissible fade show" role="alert" style="margin-bottom: 20px;">
                <i class="fas fa-check-circle me-2"></i>
                ${requestScope.msg}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <% if (p != null) { %>  
            <div class="product-container">
                <!-- Product Image Gallery -->
                <div class="product-gallery">
                    <div class="main-image-container">
                        <img id="mainImage" src="${pageContext.request.contextPath}<%= p.getImageUrl() %>" alt="Hình sản phẩm" class="main-image"/>
                        <div class="image-zoom-overlay" id="zoomOverlay">
                            <span class="close-zoom">&times;</span>
                            <img id="zoomedImage" src="" alt="Zoomed image" class="zoomed-image"/>
                        </div>
                    </div>
                    
                    <!-- Thumbnail Navigation -->
                    <div class="thumbnail-container">
                        <% if (p.getImageUrls() != null && !p.getImageUrls().isEmpty()) { %>
                            <% for (int i = 0; i < p.getImageUrls().size(); i++) { %>
                                <div class="thumbnail-item <%= i == 0 ? "active" : "" %>" onclick="changeMainImage('<%= p.getImageUrls().get(i) %>', this)">
                                    <img src="${pageContext.request.contextPath}<%= p.getImageUrls().get(i) %>" alt="Thumbnail <%= i + 1 %>" class="thumbnail-image"/>
                                </div>
                            <% } %>
                        <% } else { %>
                            <div class="thumbnail-item active" onclick="changeMainImage('<%= p.getImageUrl() %>', this)">
                                <img src="${pageContext.request.contextPath}<%= p.getImageUrl() %>" alt="Thumbnail" class="thumbnail-image"/>
                            </div>
                        <% } %>
                    </div>
                </div>
                
                <!-- Product Details -->
                <div class="product-details">
                    <h2><%= p.getName() %></h2>
                    <div class="price-section">
                        <span class="price"><%= String.format("%,.0f", p.getPrice()) %> VND</span>
                    </div>
                    <div class="product-info">
                        <p><b>Số lượng:</b> <%= p.getStock() %> sản phẩm có sẵn</p>
                        <p><b>Danh mục:</b> <%= categoryName != null ? categoryName : "Không xác định" %></p>
                    </div>
                    
                    <!-- Nút thêm vào giỏ hàng + số lượng -->
                    <div class="action-buttons mb-4">
                        <form action="${pageContext.request.contextPath}/addToCart" method="get" class="d-flex align-items-center gap-2">
                            <input type="hidden" name="id" value="<%=p.getProductId()%>"/>
                            <div class="qty-control" role="group" aria-label="Số lượng">
                                <button type="button" id="qtyMinus" class="qty-btn" aria-label="Giảm" onclick="changeQty(-1)" <%= p.getStock() <= 0 ? "disabled" : "" %>>
                                    <span aria-hidden="true">−</span>
                                </button>
                                <input type="text" class="qty-input" name="quantity" id="qtyInput"
                                       value="1" inputmode="numeric" pattern="[0-9]*"
                                       aria-live="assertive" aria-valuenow="1" aria-valuemin="1" aria-valuemax="<%=p.getStock()%>" role="spinbutton"
                                       data-min="1" data-max="<%=p.getStock()%>" <%= p.getStock() <= 0 ? "disabled" : "" %> />
                                <button type="button" id="qtyPlus" class="qty-btn" aria-label="Tăng" onclick="changeQty(1)" <%= p.getStock() <= 0 ? "disabled" : "" %>>
                                    <span aria-hidden="true">+</span>
                                </button>
                            </div>
                            <button type="submit" class="btn btn-primary add-to-cart" <%= p.getStock() <= 0 ? "disabled" : "" %>>
                                <i class="fas fa-shopping-cart me-2"></i>
                                Thêm vào giỏ hàng
                            </button>
                        </form>
                        <small class="qty-hint <%= p.getStock() <= 5 ? "low" : "ok" %>">
                            <i class="fas fa-info-circle"></i>
                            <%= p.getStock() > 0 ? ("Chọn số lượng (tối đa " + p.getStock() + ")") : "Hết hàng" %>
                        </small>
                    </div>
                    
                    <div class="description-section">
                        <h4>Mô tả sản phẩm</h4>
                        <div class="description-content">
                            <%= p.getDescription() != null ? p.getDescription().replace("\n", "<br>") : "Chưa có mô tả" %>
                        </div>
                    </div>
                </div>
            </div>
        <% } else { %>
            <div class="no-product">
                <h3>Không tìm thấy sản phẩm</h3>
                <p>Sản phẩm bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
            </div>
        <% } %>
    </div>
    <%@ include file="/View/includes/footer.jspf" %>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    
    <script>
        // Gallery functionality
        function changeMainImage(imageUrl, thumbnailElement) {
            // Update main image
            const mainImage = document.getElementById('mainImage');
            mainImage.src = '${pageContext.request.contextPath}' + imageUrl;
            
            // Update active thumbnail
            document.querySelectorAll('.thumbnail-item').forEach(item => {
                item.classList.remove('active');
            });
            thumbnailElement.classList.add('active');
        }
        
        // Image zoom functionality
        document.addEventListener('DOMContentLoaded', function() {
            const mainImage = document.getElementById('mainImage');
            const zoomOverlay = document.getElementById('zoomOverlay');
            const zoomedImage = document.getElementById('zoomedImage');
            const closeZoom = document.querySelector('.close-zoom');
            
            // Click to zoom
            mainImage.addEventListener('click', function() {
                zoomedImage.src = this.src;
                zoomOverlay.style.display = 'flex';
                document.body.style.overflow = 'hidden';
            });
            
            // Close zoom
            closeZoom.addEventListener('click', function() {
                zoomOverlay.style.display = 'none';
                document.body.style.overflow = 'auto';
            });
            
            // Close zoom when clicking overlay
            zoomOverlay.addEventListener('click', function(e) {
                if (e.target === zoomOverlay) {
                    zoomOverlay.style.display = 'none';
                    document.body.style.overflow = 'auto';
                }
            });
            
            // Close zoom with Escape key
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape' && zoomOverlay.style.display === 'flex') {
                    zoomOverlay.style.display = 'none';
                    document.body.style.overflow = 'auto';
                }
            });
        });
        
        // Add to cart functionality
        function changeQty(delta) {
            const input = document.getElementById('qtyInput');
            if (!input) return;
            const min = parseInt(input.getAttribute('data-min') || input.min || '1', 10);
            const max = parseInt(input.getAttribute('data-max') || input.max || '999999', 10);
            let val = parseInt(input.value || '1', 10);
            if (isNaN(val)) val = 1;
            val += delta;
            if (val < min) val = min;
            if (val > max) val = max;
            input.value = val;
            input.setAttribute('aria-valuenow', String(val));
            // toggle disabled state of minus button for UX
            const minus = document.getElementById('qtyMinus');
            const plus = document.getElementById('qtyPlus');
            if (minus) minus.disabled = (val <= min);
            if (plus) plus.disabled = (val >= max);
        }

        // Initialize minus/plus states on load
        document.addEventListener('DOMContentLoaded', function() {
            changeQty(0);
        });
        
        // Buy now functionality
        document.querySelector('.buy-now')?.addEventListener('click', function() {
            // TODO: Implement buy now
            alert('Chức năng mua ngay đang được phát triển!');
        });
    </script>
</body>

</html>
