<%@page import="Model.Wishlist"%>
<%@page import="Model.Product"%>
<%@page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Wishlist của bạn - PinkyCloud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .wishlist-page {
            padding: 20px 0;
            min-height: 60vh;
        }
        .wishlist-item {
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            background: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .wishlist-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }
        .wishlist-item img {
            width: 150px;
            height: 150px;
            object-fit: cover;
            border-radius: 8px;
        }
        .wishlist-item-info {
            flex: 1;
            padding-left: 20px;
        }
        .wishlist-item-info h5 {
            margin-bottom: 10px;
            color: #333;
        }
        .price-section {
            margin: 10px 0;
        }
        .price-original {
            text-decoration: line-through;
            color: #999;
            font-size: 14px;
            margin-right: 10px;
        }
        .price-discounted {
            color: #e91e63;
            font-size: 18px;
            font-weight: bold;
        }
        .price {
            color: #333;
            font-size: 18px;
            font-weight: bold;
        }
        .price-badge {
            background: #e91e63;
            color: white;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
            margin-left: 10px;
        }
        .wishlist-actions {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }
        .btn-wishlist-remove {
            background: #f44336;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-wishlist-remove:hover {
            background: #d32f2f;
        }
        .empty-wishlist {
            text-align: center;
            padding: 60px 20px;
        }
        .empty-wishlist i {
            font-size: 80px;
            color: #ccc;
            margin-bottom: 20px;
        }
        .empty-wishlist h3 {
            color: #666;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<%@ include file="/View/includes/header.jspf" %>

<div class="container wishlist-page">
    <h2 class="text-center mb-4">
        <i class="fas fa-heart text-danger"></i> WISHLIST CỦA BẠN
    </h2>

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
                                    <span class="price-original"><%= String.format("%,.0f", originalPrice) %> VND</span>
                                    <span class="price-discounted"><%= String.format("%,.0f", discountedPrice) %> VND</span>
                                    <span class="price-badge">
                                        <% if (activeDiscount != null && "PERCENTAGE".equalsIgnoreCase(activeDiscount.getType())) { %>
                                            -<%= String.format("%.0f", activeDiscount.getValue()) %>%
                                        <% } else if (activeDiscount != null) { %>
                                            -<%= String.format("%,.0f", activeDiscount.getValue()) %> VND
                                        <% } %>
                                    </span>
                                <% } else { %>
                                    <span class="price"><%= String.format("%,.0f", originalPrice) %> VND</span>
                                <% } %>
                            </div>
                            <div class="wishlist-actions">
                                <a href="${pageContext.request.contextPath}/addToCart?id=<%= product.getProductId() %>&buyNow=true" 
                                   class="btn btn-primary">
                                    <i class="fas fa-shopping-cart me-2"></i>Thêm vào giỏ hàng
                                </a>
                                <button class="btn btn-wishlist-remove" onclick="removeFromWishlist(<%= product.getProductId() %>)">
                                    <i class="fas fa-trash me-2"></i>Xóa khỏi wishlist
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
<script>
function removeFromWishlist(productId) {
    if (!confirm('Bạn có chắc muốn xóa sản phẩm này khỏi wishlist?')) {
        return;
    }
    
    const formData = new FormData();
    formData.append('action', 'remove');
    formData.append('productId', productId);
    
    fetch('${pageContext.request.contextPath}/wishlist', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert(data.message || 'Có lỗi xảy ra');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi xóa sản phẩm');
    });
}
</script>
</body>
</html>

