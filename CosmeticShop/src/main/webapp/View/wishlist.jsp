<%@page import="Model.Wishlist"%>
<%@page import="Model.Product"%>
<%@page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Wishlist của bạn - PinkyCloud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/collection.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/wishlist.css?v=2.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
          crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<%@ include file="/View/includes/header.jspf" %>
<%@ include file="/View/includes/mobile-search.jspf" %>

<div class="container wishlist-page">
    <div class="wishlist-header">
        <h2>
            <i class="fas fa-heart"></i> WISHLIST CỦA BẠN
        </h2>
        <p>Lưu những sản phẩm yêu thích để không bỏ lỡ ưu đãi!</p>
        <%
            List<Wishlist> wishlistCount = (List<Wishlist>) request.getAttribute("wishlistItems");
            int count = (wishlistCount != null) ? wishlistCount.size() : 0;
            if (count > 0) {
        %>
            <span class="wishlist-count">
                <i class="fas fa-heart me-2"></i><%= count %> sản phẩm
            </span>
        <% } %>
    </div>

    <c:if test="${not empty requestScope.msg}">
        <div class="alert alert-success alert-dismissible fade show" role="alert" style="margin-bottom: 20px;">
            <i class="fas fa-check-circle me-2"></i>
            ${requestScope.msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <%
        List<Wishlist> wishlistItems = (List<Wishlist>) request.getAttribute("wishlistItems");
        if (wishlistItems == null) {
            wishlistItems = new ArrayList<>();
        }
    %>

    <div id="wishlistContent">
        <% if (wishlistItems.isEmpty()) { %>
            <div class="empty-wishlist">
                <i class="fas fa-heart"></i>
                <h3>Wishlist của bạn trống</h3>
                <p class="text-muted">Hãy thêm sản phẩm yêu thích vào wishlist để không bỏ lỡ những ưu đãi hấp dẫn!</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">
                    <i class="fas fa-shopping-bag me-2"></i>Tiếp tục mua sắm
                </a>
            </div>
        <% } else { %>
            <div class="row">
                <% for (Wishlist item : wishlistItems) { 
                    Product product = item.getProduct();
                    if (product == null) continue;
                    
                    Model.Discount activeDiscount = product.getActiveDiscount();
                    double originalPrice = product.getPrice();
                    double discountedPrice = product.getDiscountedPrice();
                    boolean hasDiscount = product.isDiscountActive();
                %>
                <div class="col-md-12">
                    <div class="wishlist-item d-flex">
                        <a href="${pageContext.request.contextPath}/product-detail?id=<%= product.getProductId() %>">
                            <img src="${pageContext.request.contextPath}<%= product.getImageUrl() %>" 
                                 alt="<%= product.getName() %>">
                        </a>
                        <div class="wishlist-item-info">
                            <h5>
                                <a href="${pageContext.request.contextPath}/product-detail?id=<%= product.getProductId() %>" 
                                   style="text-decoration: none; color: #333;">
                                    <%= product.getName() %>
                                </a>
                            </h5>
                            <div class="price-section">
                                <% if (hasDiscount) { %>
                                    <span class="price-original"><%= String.format("%,.0f", originalPrice) %> ₫</span>
                                    <span class="price-discounted"><%= String.format("%,.0f", discountedPrice) %> ₫</span>
                                    <span class="price-badge">
                                        <% if (activeDiscount != null && "PERCENTAGE".equalsIgnoreCase(activeDiscount.getType())) { %>
                                            -<%= String.format("%.0f", activeDiscount.getValue()) %>%
                                        <% } else if (activeDiscount != null) { %>
                                            -<%= String.format("%,.0f", activeDiscount.getValue()) %> ₫
                                        <% } %>
                                    </span>
                                <% } else { %>
                                    <span class="price"><%= String.format("%,.0f", originalPrice) %> ₫</span>
                                <% } %>
                            </div>
                            <div class="wishlist-actions">
                                <a href="${pageContext.request.contextPath}/addToCart?id=<%= product.getProductId() %>&buyNow=true" 
                                   class="btn-wishlist-cart">
                                    <i class="fas fa-shopping-cart"></i>Thêm vào giỏ hàng
                                </a>
                                <button class="btn-wishlist-remove" onclick="removeFromWishlist(<%= product.getProductId() %>)">
                                    <i class="fas fa-trash"></i>Xóa
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        <% } %>
    </div>
</div>

<%@ include file="/View/includes/footer.jspf" %>

<script src="${pageContext.request.contextPath}/Css/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/Js/home.js"></script>
<script src="${pageContext.request.contextPath}/Js/search-suggest.js"></script>
<script src="${pageContext.request.contextPath}/Js/notification.js"></script>
<script>
function removeFromWishlist(productId) {
    if (!confirm('Bạn có chắc muốn xóa sản phẩm này khỏi wishlist?')) {
        return;
    }
    
    const params = new URLSearchParams();
    params.append('action', 'remove');
    params.append('productId', productId.toString());
    
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
            try {
                return JSON.parse(text);
            } catch (e) {
                console.error('Response is not JSON:', text);
                throw new Error('Invalid JSON response');
            }
        });
    })
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert(data.message || 'Có lỗi xảy ra');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi xóa sản phẩm: ' + error.message);
    });
}
</script>
</body>
</html>

