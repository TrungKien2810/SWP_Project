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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .wishlist-page {
            padding: 40px 0;
            min-height: 70vh;
            background: linear-gradient(135deg, #fff5f7 0%, #ffffff 100%);
        }
        
        .wishlist-header {
            text-align: center;
            margin-bottom: 40px;
            padding: 30px 0;
            background: white;
            border-radius: 16px;
            box-shadow: 0 4px 15px rgba(247, 108, 133, 0.1);
        }
        
        .wishlist-header h2 {
            color: #f76c85;
            font-weight: 800;
            font-size: 2.5rem;
            margin-bottom: 10px;
            font-family: 'Times New Roman', Times, serif;
        }
        
        .wishlist-header p {
            color: #666;
            font-size: 1.1rem;
        }
        
        .wishlist-item {
            border: 2px solid #fbd0da;
            border-radius: 16px;
            padding: 25px;
            margin-bottom: 25px;
            background: white;
            box-shadow: 0 4px 12px rgba(247, 108, 133, 0.08);
            transition: all 0.3s ease;
        }
        
        .wishlist-item:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(247, 108, 133, 0.15);
            border-color: #f76c85;
        }
        
        .wishlist-item img {
            width: 180px;
            height: 180px;
            object-fit: cover;
            border-radius: 12px;
            border: 2px solid #fbd0da;
        }
        
        .wishlist-item-info {
            flex: 1;
            padding-left: 30px;
        }
        
        .wishlist-item-info h5 {
            margin-bottom: 15px;
            color: #333;
            font-size: 1.3rem;
            font-weight: 700;
        }
        
        .wishlist-item-info h5 a {
            color: #333;
            text-decoration: none;
            transition: color 0.3s;
        }
        
        .wishlist-item-info h5 a:hover {
            color: #f76c85;
        }
        
        .price-section {
            margin: 15px 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .price-original {
            text-decoration: line-through;
            color: #999;
            font-size: 16px;
        }
        
        .price-discounted {
            color: #f76c85;
            font-size: 24px;
            font-weight: bold;
        }
        
        .price {
            color: #f76c85;
            font-size: 24px;
            font-weight: bold;
        }
        
        .price-badge {
            background: linear-gradient(135deg, #f76c85, #ff8fa3);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 600;
        }
        
        .wishlist-actions {
            display: flex;
            gap: 12px;
            margin-top: 20px;
            flex-wrap: wrap;
        }
        
        .btn-wishlist-cart {
            background: linear-gradient(135deg, #f76c85, #ff8fa3);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            text-decoration: none;
        }
        
        .btn-wishlist-cart:hover {
            background: linear-gradient(135deg, #ff8fa3, #f76c85);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(247, 108, 133, 0.3);
            color: white;
        }
        
        .btn-wishlist-remove {
            background: white;
            color: #f44336;
            border: 2px solid #f44336;
            padding: 12px 24px;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        
        .btn-wishlist-remove:hover {
            background: #f44336;
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
        }
        
        .empty-wishlist {
            text-align: center;
            padding: 80px 20px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 4px 15px rgba(247, 108, 133, 0.1);
        }
        
        .empty-wishlist i {
            font-size: 100px;
            color: #fbd0da;
            margin-bottom: 30px;
        }
        
        .empty-wishlist h3 {
            color: #333;
            margin-bottom: 15px;
            font-weight: 700;
            font-size: 1.8rem;
        }
        
        .empty-wishlist p {
            color: #666;
            font-size: 1.1rem;
            margin-bottom: 30px;
        }
        
        .empty-wishlist .btn {
            background: linear-gradient(135deg, #f76c85, #ff8fa3);
            border: none;
            padding: 14px 32px;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 8px;
            transition: all 0.3s;
        }
        
        .empty-wishlist .btn:hover {
            background: linear-gradient(135deg, #ff8fa3, #f76c85);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(247, 108, 133, 0.3);
        }
        
        .wishlist-count {
            display: inline-block;
            background: #fff5f7;
            color: #f76c85;
            padding: 8px 20px;
            border-radius: 20px;
            font-weight: 600;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<%@ include file="/View/includes/header.jspf" %>

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

