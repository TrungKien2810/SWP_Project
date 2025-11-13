<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Product" %>
<%@ page import="Model.Comment" %>
<%@ page import="Model.CommentReply" %>
<%@ page import="Model.ReplyMedia" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    Product p = (Product) request.getAttribute("product");
    String categoryName = (String) request.getAttribute("categoryName");
    java.util.List<String> categoryNames = (java.util.List<String>) request.getAttribute("categoryNames");
    if (categoryNames == null) categoryNames = new java.util.ArrayList<>();
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
<body 
    <c:if test="${not empty sessionScope.cartSuccessMsg}">data-success-msg="${sessionScope.cartSuccessMsg}"</c:if>
    <c:if test="${not empty sessionScope.cartErrorMsg}">data-error-msg="${sessionScope.cartErrorMsg}"</c:if>
>
    <c:remove var="cartSuccessMsg" scope="session" />
    <c:remove var="cartErrorMsg" scope="session" />
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
                    <%
                        Model.Discount activeDiscount = p.getActiveDiscount();
                        double originalPrice = p.getPrice();
                        double discountedPrice = p.getDiscountedPrice();
                        boolean hasDiscount = p.isDiscountActive();
                    %>
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
                    <div class="product-info">
                        <p><b>Số lượng:</b> <%= p.getStock() %> sản phẩm có sẵn</p>
                        <p><b>Danh mục:</b> 
                            <c:choose>
                                <c:when test="${not empty categoryNames}">
                                    <c:forEach var="catName" items="${categoryNames}" varStatus="loop">
                                        ${catName}<c:if test="${!loop.last}">, </c:if>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Chưa phân loại</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
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
                            <c:if test="${not empty sessionScope.user}">
                                <button type="button" id="wishlistBtn" class="btn ${inWishlist ? 'btn-danger' : 'btn-outline-danger'}" onclick="toggleWishlist(<%=p.getProductId()%>, event); return false;" title="${inWishlist ? 'Xóa khỏi wishlist' : 'Thêm vào wishlist'}">
                                    <i class="${inWishlist ? 'fas' : 'far'} fa-heart" id="wishlistIcon"></i>
                                </button>
                            </c:if>
                        </form>
                        <small class="qty-hint <%= p.getStock() <= 5 ? "low" : "ok" %>">
                            <i class="fas fa-info-circle"></i>
                            <%= p.getStock() > 0 ? ("Chọn số lượng (tối đa " + p.getStock() + ")") : "Hết hàng" %>
                        </small>
                    </div>
                </div>
            </div>
            
            <!-- Product Description Section - Tách riêng để phân bố đều -->
            <div class="product-description-section">
                <div class="container">
                    <div class="row">
                        <div class="col-12">
                            <div class="description-section">
                                <h4 class="description-main-title">Mô tả sản phẩm</h4>
                                <div class="description-content">
                                    <%
                                        String description = p.getDescription();
                                        if (description != null && !description.trim().isEmpty()) {
                                            // Phân tích mô tả thành các mục
                                            String[] sections = description.split("------------------");
                                            for (int i = 0; i < sections.length; i++) {
                                                String section = sections[i].trim();
                                                if (!section.isEmpty()) {
                                                    String[] lines = section.split("\n", 2);
                                                    if (lines.length >= 2) {
                                                        String title = lines[0].trim();
                                                        String content = lines[1].trim();
                                                        if (!title.isEmpty() && !content.isEmpty()) {
                                    %>
                                            <div class="description-item">
                                                <h5 class="description-item-title"><%= title %></h5>
                                                <div class="description-item-content"><%= content.replace("\n", "<br>") %></div>
                                            </div>
                                    <%
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                    %>
                                        <div class="no-description">Chưa có mô tả</div>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Comments & Reviews Section -->
            <div class="comments-section">
                <div class="container">
                    <div class="row">
                        <div class="col-12">
                            <h4 class="comments-main-title">Đánh giá & Bình luận</h4>
                            
                            <!-- Rating Summary -->
                            <div class="rating-summary">
                                <div class="avg-rating">
                                    <div class="rating-score">
                                        <span class="score">${avgRating > 0 ? String.format("%.1f", avgRating) : "0.0"}</span>
                                        <div class="stars-display">
                                            <% double avgRating = (Double) request.getAttribute("avgRating"); %>
                                            <% for (int i = 1; i <= 5; i++) { %>
                                                <i class="fas fa-star <%= (i <= Math.round(avgRating)) ? "star-filled" : "star-empty" %>"></i>
                                            <% } %>
                                        </div>
                                    </div>
                                    <div class="comment-count">
                                        <span>${commentCount} đánh giá</span>
                                    </div>
                                </div>
                                
                                <!-- Rating Filter -->
                                <div class="rating-filter">
                                    <div class="filter-label">Lọc theo đánh giá:</div>
                                    <div class="filter-buttons">
                                        <a href="${pageContext.request.contextPath}/product-detail?id=<%= p.getProductId() %>" 
                                           class="filter-btn ${empty ratingFilter ? 'active' : ''}">
                                            Tất cả (${commentCount})
                                        </a>
                                        <a href="${pageContext.request.contextPath}/product-detail?id=<%= p.getProductId() %>&rating=5" 
                                           class="filter-btn ${ratingFilter == 5 ? 'active' : ''}">
                                            <span class="filter-stars"><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i></span> ${rating5Count}
                                        </a>
                                        <a href="${pageContext.request.contextPath}/product-detail?id=<%= p.getProductId() %>&rating=4" 
                                           class="filter-btn ${ratingFilter == 4 ? 'active' : ''}">
                                            <span class="filter-stars"><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="far fa-star"></i></span> ${rating4Count}
                                        </a>
                                        <a href="${pageContext.request.contextPath}/product-detail?id=<%= p.getProductId() %>&rating=3" 
                                           class="filter-btn ${ratingFilter == 3 ? 'active' : ''}">
                                            <span class="filter-stars"><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="far fa-star"></i><i class="far fa-star"></i></span> ${rating3Count}
                                        </a>
                                        <a href="${pageContext.request.contextPath}/product-detail?id=<%= p.getProductId() %>&rating=2" 
                                           class="filter-btn ${ratingFilter == 2 ? 'active' : ''}">
                                            <span class="filter-stars"><i class="fas fa-star star-filled"></i><i class="fas fa-star star-filled"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i></span> ${rating2Count}
                                        </a>
                                        <a href="${pageContext.request.contextPath}/product-detail?id=<%= p.getProductId() %>&rating=1" 
                                           class="filter-btn ${ratingFilter == 1 ? 'active' : ''}">
                                            <span class="filter-stars"><i class="fas fa-star star-filled"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i></span> ${rating1Count}
                                        </a>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Add Comment Form -->
                            <c:choose>
                                <c:when test="${empty sessionScope.user}">
                                    <div class="alert alert-info">
                                        <i class="fas fa-info-circle me-2"></i>
                                        Vui lòng <a href="${pageContext.request.contextPath}/signup">đăng nhập</a> để viết đánh giá
                                    </div>
                                </c:when>
                                <c:when test="${canComment == true}">
                                    <div class="add-comment-form">
                                        <h5>Viết đánh giá của bạn</h5>
                                        <form action="${pageContext.request.contextPath}/addComment" method="post" enctype="multipart/form-data" id="commentForm">
                                            <input type="hidden" name="productId" value="<%= p.getProductId() %>">
                                            
                                            <div class="rating-input mb-3">
                                                <label class="form-label">Đánh giá (sao)</label>
                                                <div class="star-rating">
                                                    <% for (int i = 1; i <= 5; i++) { %>
                                                        <i class="far fa-star star-rating-btn" data-rating="<%= i %>" onclick="selectRating(<%= i %>)"></i>
                                                    <% } %>
                                                </div>
                                                <input type="hidden" name="rating" id="ratingValue" value="0" required>
                                            </div>
                                            
                                            <div class="mb-3">
                                                <label for="commentText" class="form-label">Nội dung bình luận</label>
                                                <textarea class="form-control" name="commentText" id="commentText" rows="4" required placeholder="Chia sẻ cảm nhận của bạn về sản phẩm..."></textarea>
                                            </div>
                                            
                                            <div class="mb-3">
                                                <div class="custom-file-upload">
                                                    <input type="file" name="mediaFiles" id="mediaFiles" multiple accept="image/*,video/*" style="display: none;">
                                                    <label for="mediaFiles" class="file-upload-icon-btn" title="Thêm ảnh/video">
                                                        <i class="fas fa-image"></i>
                                                    </label>
                                                    <div id="mediaPreview" class="mt-3"></div>
                                                </div>
                                            </div>
                                            
                                            <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
                                        </form>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-warning">
                                        <i class="fas fa-shopping-bag me-2"></i>
                                        Bạn cần <strong>mua sản phẩm</strong> và <strong>nhận hàng thành công</strong> trước khi có thể đánh giá.
                                        <a href="${pageContext.request.contextPath}/View/home.jsp" class="alert-link ms-2">
                                            <i class="fas fa-arrow-right me-1"></i>Mua ngay
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            
                            <!-- Comments List -->
                            <div class="comments-list">
                                <c:choose>
                                    <c:when test="${not empty comments and fn:length(comments) > 0}">
                                        <c:forEach var="comment" items="${comments}">
                                            <div class="comment-item" data-comment-id="${comment.commentId}">
                                                <div class="comment-header">
                                                    <div class="commenter-info">
                                                        <c:choose>
                                                            <c:when test="${not empty comment.avatarUrl}">
                                                                <img src="${pageContext.request.contextPath}${comment.avatarUrl}" alt="Avatar" class="commenter-avatar">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="commenter-avatar-placeholder">
                                                                    <i class="fas fa-user"></i>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div class="commenter-details">
                                                            <div class="commenter-name">${comment.username}</div>
                                                            <div class="comment-rating">
                                                                <c:forEach var="star" begin="1" end="5">
                                                                    <i class="fas fa-star <c:if test='${star <= comment.rating}'>star-filled</c:if> <c:if test='${star > comment.rating}'>star-empty</c:if>"></i>
                                                                </c:forEach>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="comment-date">
                                                        <c:if test="${not empty comment.createdAt}">
                                                            <%
                                                                try {
                                                                    Comment commentObj = (Comment) pageContext.getAttribute("comment");
                                                                    if (commentObj != null && commentObj.getCreatedAt() != null) {
                                                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                                                                        String dateStr = commentObj.getCreatedAt().format(formatter);
                                                                        out.print(dateStr);
                                                                    } else {
                                                                        out.print("Vừa xong");
                                                                    }
                                                                } catch (Exception e) {
                                                                    out.print("Vừa xong");
                                                                }
                                                            %>
                                                        </c:if>
                                                        <c:if test="${empty comment.createdAt}">
                                                            Vừa xong
                                                        </c:if>
                                                    </div>
                                                    <c:if test="${not empty sessionScope.user && sessionScope.user.user_id == comment.userId}">
                                                        <button type="button" class="delete-comment-btn" title="Xóa" aria-label="Xóa" data-comment-id="${comment.commentId}">
                                                            <i class="fas fa-times"></i>
                                                        </button>
                                                    </c:if>
                                                </div>
                                                <div class="comment-body">
                                                    <p>${comment.commentText}</p>
                                                </div>
                                                <c:if test="${not empty comment.mediaList and fn:length(comment.mediaList) > 0}">
                                                    <div class="comment-media">
                                                        <c:forEach var="media" items="${comment.mediaList}">
                                                            <c:choose>
                                                                <c:when test="${media.mediaType == 'image'}">
                                                                    <img src="${pageContext.request.contextPath}${media.mediaUrl}" alt="Media" class="media-item" onclick="openMediaModal('${pageContext.request.contextPath}${media.mediaUrl}', 'image')">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <video controls class="media-item">
                                                                        <source src="${pageContext.request.contextPath}${media.mediaUrl}" type="video/mp4">
                                                                        Trình duyệt của bạn không hỗ trợ video.
                                                                    </video>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:forEach>
                                                    </div>
                                                </c:if>
                                                
                                                <!-- Reply Section -->
                                                <div class="comment-replies">
                                                    <!-- Reply button -->
                                                    <c:if test="${not empty sessionScope.user}">
                                                        <button class="btn-reply" onclick="toggleReplyForm(${comment.commentId})">
                                                            <i class="fas fa-reply"></i> Trả lời
                                                        </button>
                                                    </c:if>
                                                    
                                                    <!-- Reply form (hidden by default) -->
                                                    <div id="replyForm_${comment.commentId}" class="reply-form" style="display: none;">
                                                        <form action="${pageContext.request.contextPath}/addReply" method="post" enctype="multipart/form-data">
                                                            <input type="hidden" name="action" value="addReply">
                                                            <input type="hidden" name="commentId" value="${comment.commentId}">
                                                            <div class="mb-2">
                                                                <textarea class="form-control" name="replyText" rows="2" required placeholder="Viết trả lời của bạn..."></textarea>
                                                            </div>
                                                            <div class="mb-2">
                                                                <input type="file" name="mediaFiles" id="replyMediaFiles_${comment.commentId}" multiple accept="image/*,video/*" style="display: none;">
                                                                <label for="replyMediaFiles_${comment.commentId}" class="file-upload-icon-btn" title="Thêm ảnh/video">
                                                                    <i class="fas fa-image"></i>
                                                                </label>
                                                                <div id="replyMediaPreview_${comment.commentId}"></div>
                                                            </div>
                                                            <div class="reply-form-actions">
                                                                <button type="submit" class="btn btn-primary btn-sm">Gửi trả lời</button>
                                                                <button type="button" class="btn btn-secondary btn-sm" onclick="toggleReplyForm(${comment.commentId})">Hủy</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                    
                                                    <!-- Existing replies -->
                                                    <c:if test="${not empty comment.replies and fn:length(comment.replies) > 0}">
                                                        <div class="replies-list">
                                                            <c:forEach var="reply" items="${comment.replies}">
                                                                <div class="reply-item">
                                                                    <div class="reply-header">
                                                                        <div class="replyer-info">
                                                                            <c:choose>
                                                                                <c:when test="${not empty reply.avatarUrl}">
                                                                                    <img src="${pageContext.request.contextPath}${reply.avatarUrl}" alt="Avatar" class="replyer-avatar">
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <div class="replyer-avatar-placeholder">
                                                                                        <i class="fas fa-user"></i>
                                                                                    </div>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                            <div class="replyer-details">
                                                                                <div class="replyer-name">${reply.username}</div>
                                                                                <div class="reply-date">
                                                                                    <%
                                                                                        try {
                                                                                            CommentReply replyObj = (CommentReply) pageContext.getAttribute("reply");
                                                                                            if (replyObj != null && replyObj.getCreatedAt() != null) {
                                                                                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                                                                                                String dateStr = replyObj.getCreatedAt().format(formatter);
                                                                                                out.print(dateStr);
                                                                                            } else {
                                                                                                out.print("Vừa xong");
                                                                                            }
                                                                                        } catch (Exception e) {
                                                                                            out.print("Vừa xong");
                                                                                        }
                                                                                    %>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <c:if test="${not empty sessionScope.user && sessionScope.user.user_id == reply.userId}">
                                                                            <button type="button" class="delete-reply-btn" title="Xóa" aria-label="Xóa" data-reply-id="${reply.replyId}">
                                                                                <i class="fas fa-times"></i>
                                                                            </button>
                                                                        </c:if>
                                                                    </div>
                                                                    <div class="reply-body">
                                                                        <p>${reply.replyText}</p>
                                                                    </div>
                                                                    <c:if test="${not empty reply.mediaList and fn:length(reply.mediaList) > 0}">
                                                                        <div class="reply-media">
                                                                            <c:forEach var="media" items="${reply.mediaList}">
                                                                                <c:choose>
                                                                                    <c:when test="${media.mediaType == 'image'}">
                                                                                        <img src="${pageContext.request.contextPath}${media.mediaUrl}" alt="Media" class="reply-media-item" onclick="openMediaModal('${pageContext.request.contextPath}${media.mediaUrl}', 'image')">
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <video controls class="reply-media-item">
                                                                                            <source src="${pageContext.request.contextPath}${media.mediaUrl}" type="video/mp4">
                                                                                            Trình duyệt của bạn không hỗ trợ video.
                                                                                        </video>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </c:forEach>
                                                                        </div>
                                                                    </c:if>
                                                                </div>
                                                            </c:forEach>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-comments">
                                            <i class="fas fa-comment-dots"></i>
                                            <p>Chưa có đánh giá nào. Hãy là người đầu tiên đánh giá sản phẩm này!</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
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
    
    <!-- Media Modal -->
    <div id="mediaModal" class="media-modal" onclick="closeMediaModal()">
        <span class="media-modal-close">&times;</span>
        <div class="media-modal-content" onclick="event.stopPropagation()">
            <img id="modalImage" src="" alt="Modal Image">
            <video id="modalVideo" controls style="display: none;">
                <source src="" type="video/mp4">
            </video>
        </div>
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
        
        // Rating selection
        function selectRating(rating) {
            document.getElementById('ratingValue').value = rating;
            const stars = document.querySelectorAll('.star-rating-btn');
            stars.forEach((star, index) => {
                if (index < rating) {
                    star.classList.remove('far');
                    star.classList.add('fas');
                } else {
                    star.classList.remove('fas');
                    star.classList.add('far');
                }
            });
        }
        
        // Media preview and drag & drop
        const fileInput = document.getElementById('mediaFiles');
        const uploadBox = document.querySelector('.file-upload-box');
        
        // File change handler
        fileInput?.addEventListener('change', function(e) {
            previewFiles(e.target.files);
        });
        
        // Drag and drop handlers
        if (uploadBox) {
            // Prevent default drag behaviors
            ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
                uploadBox.addEventListener(eventName, preventDefaults, false);
                document.body.addEventListener(eventName, preventDefaults, false);
            });
            
            function preventDefaults(e) {
                e.preventDefault();
                e.stopPropagation();
            }
            
            // Highlight drop zone when item is dragged over it
            ['dragenter', 'dragover'].forEach(eventName => {
                uploadBox.addEventListener(eventName, highlight, false);
            });
            
            ['dragleave', 'drop'].forEach(eventName => {
                uploadBox.addEventListener(eventName, unhighlight, false);
            });
            
            function highlight(e) {
                uploadBox.classList.add('drag-over');
            }
            
            function unhighlight(e) {
                uploadBox.classList.remove('drag-over');
            }
            
            // Handle dropped files
            uploadBox.addEventListener('drop', handleDrop, false);
            
            function handleDrop(e) {
                const dt = e.dataTransfer;
                const files = dt.files;
                fileInput.files = files;
                previewFiles(files);
            }
        }
        
        function previewFiles(files) {
            const preview = document.getElementById('mediaPreview');
            preview.innerHTML = '';
            
            if (files.length > 0) {
                // Store file names and data URLs
                const fileDataList = [];
                
                for (let i = 0; i < files.length; i++) {
                    const file = files[i];
                    const reader = new FileReader();
                    
                    reader.onload = function(e) {
                        fileDataList.push({
                            file: file,
                            dataUrl: e.target.result,
                            index: i
                        });
                        
                        // When all files are loaded
                        if (fileDataList.length === files.length) {
                            fileDataList.forEach((item, idx) => {
                                const div = document.createElement('div');
                                div.setAttribute('data-file-index', idx);
                                div.style.position = 'relative';
                                div.style.display = 'inline-block';
                                div.style.margin = '5px';
                                
                                if (item.file.type.startsWith('image/')) {
                                    const img = document.createElement('img');
                                    img.src = item.dataUrl;
                                    img.style.width = '100px';
                                    img.style.height = '100px';
                                    img.style.objectFit = 'cover';
                                    img.style.border = '2px solid #ff69b4';
                                    img.style.borderRadius = '8px';
                                    div.appendChild(img);
                                } else if (item.file.type.startsWith('video/')) {
                                    const video = document.createElement('video');
                                    video.src = item.dataUrl;
                                    video.style.width = '100px';
                                    video.style.height = '100px';
                                    video.style.objectFit = 'cover';
                                    video.style.border = '2px solid #ff69b4';
                                    video.style.borderRadius = '8px';
                                    video.controls = true;
                                    div.appendChild(video);
                                }
                                
                                // Add remove button
                                const removeBtn = document.createElement('span');
                                removeBtn.innerHTML = '&times;';
                                removeBtn.style.position = 'absolute';
                                removeBtn.style.top = '-8px';
                                removeBtn.style.right = '-8px';
                                removeBtn.style.width = '24px';
                                removeBtn.style.height = '24px';
                                removeBtn.style.borderRadius = '50%';
                                removeBtn.style.background = '#e74c3c';
                                removeBtn.style.color = 'white';
                                removeBtn.style.cursor = 'pointer';
                                removeBtn.style.display = 'flex';
                                removeBtn.style.alignItems = 'center';
                                removeBtn.style.justifyContent = 'center';
                                removeBtn.style.fontSize = '18px';
                                removeBtn.style.fontWeight = 'bold';
                                removeBtn.onclick = function() {
                                    const fileIdx = parseInt(div.getAttribute('data-file-index'));
                                    div.remove();
                                    
                                    // Remove from file list
                                    const newFiles = Array.from(fileInput.files).filter((_, idx) => idx !== fileIdx);
                                    const dt = new DataTransfer();
                                    newFiles.forEach(f => dt.items.add(f));
                                    fileInput.files = dt.files;
                                    
                                    // Re-render preview
                                    previewFiles(newFiles);
                                };
                                div.appendChild(removeBtn);
                                
                                preview.appendChild(div);
                            });
                        }
                    };
                    
                    reader.readAsDataURL(file);
                }
                
                // Update upload box text
                if (uploadBox) {
                    uploadBox.querySelector('.file-upload-main').textContent = `Đã chọn ${files.length} file`;
                    uploadBox.querySelector('.file-upload-sub').textContent = 'Click để thay đổi';
                }
            } else {
                // Reset upload box text
                if (uploadBox) {
                    uploadBox.querySelector('.file-upload-main').textContent = 'Kéo thả ảnh/video vào đây';
                    uploadBox.querySelector('.file-upload-sub').textContent = 'hoặc click để chọn từ máy tính';
                }
            }
        }
        
        // Media modal
        function openMediaModal(url, type) {
            const modal = document.getElementById('mediaModal');
            const modalImage = document.getElementById('modalImage');
            const modalVideo = document.getElementById('modalVideo');
            
            if (type === 'image') {
                modalImage.src = url;
                modalImage.style.display = 'block';
                modalVideo.style.display = 'none';
            } else {
                modalVideo.src = url;
                modalImage.style.display = 'none';
                modalVideo.style.display = 'block';
            }
            
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
        }
        
        function closeMediaModal() {
            const modal = document.getElementById('mediaModal');
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
            
            const modalImage = document.getElementById('modalImage');
            const modalVideo = document.getElementById('modalVideo');
            modalImage.src = '';
            modalVideo.pause();
            modalVideo.src = '';
        }
        
        // Close modal on close button click
        document.querySelector('.media-modal-close')?.addEventListener('click', closeMediaModal);
        
        // Close modal on Escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                closeMediaModal();
            }
        });
        
        // Form validation
        document.getElementById('commentForm')?.addEventListener('submit', function(e) {
            const rating = document.getElementById('ratingValue').value;
            if (rating === '0') {
                e.preventDefault();
                alert('Vui lòng chọn đánh giá sao!');
                return false;
            }
        });

        // Delete comment via AJAX
        const ctx = '${pageContext.request.contextPath}';

        function updateCommentMeta() {
            const commentItems = document.querySelectorAll('.comment-item');
            const commentCountEl = document.querySelector('.comment-count span');
            if (commentCountEl) {
                const newCount = commentItems.length;
                commentCountEl.textContent = `${newCount} đánh giá`;
            }

            const totalFilterLink = document.querySelector('.rating-filter .filter-buttons a');
            if (totalFilterLink) {
                const newCount = commentItems.length;
                totalFilterLink.innerHTML = totalFilterLink.innerHTML.replace(/\(\d+\)/, `(${newCount})`);
            }

            const commentsListEl = document.querySelector('.comments-list');
            if (commentsListEl && commentItems.length === 0) {
                commentsListEl.innerHTML = '<div class="notification-empty">Chưa có bình luận nào.</div>';
            }
        }

        document.querySelectorAll('.delete-comment-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const button = this;
                if (button.dataset.deleting === 'true') return; // chặn double-click
                if (!confirm('Bạn có chắc muốn xóa bình luận này?')) return;
                const commentId = button.getAttribute('data-comment-id');
                const itemEl = button.closest('.comment-item');
                button.dataset.deleting = 'true';
                button.setAttribute('aria-busy', 'true');
                button.disabled = true;
                // Optimistic UI: remove ngay trên UI để không cần reload
                if (itemEl) {
                    itemEl.style.opacity = '0.5';
                    itemEl.style.pointerEvents = 'none';
                }
                fetch(ctx + '/addComment', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                    body: 'action=delete&commentId=' + encodeURIComponent(commentId),
                    cache: 'no-store'
                })
                .then(res => res.text().catch(() => ''))
                .then(text => {
                    const normalized = (text || '').trim().toLowerCase();
                    // Nếu server báo 'ok' hoặc 'forbidden'/'not_found' (đã xóa), xóa hoàn toàn khỏi UI
                    if (normalized === 'ok' || normalized === 'forbidden' || normalized === 'not_found') {
                        if (itemEl && itemEl.isConnected) {
                            itemEl.remove();
                        }
                        updateCommentMeta();
                        return;
                    }
                    // Nếu phản hồi không như mong đợi (HTML/redirect), fallback reload để đồng bộ UI
                    if (itemEl && itemEl.isConnected) {
                        itemEl.remove();
                        updateCommentMeta();
                    }
                    setTimeout(() => {
                        location.reload();
                    }, 50);
                })
                .catch(() => alert('Có lỗi xảy ra khi xóa.'))
                .finally(() => {
                    button.dataset.deleting = 'false';
                    button.removeAttribute('aria-busy');
                    button.disabled = false;
                });
            });
        });
        
        // Toggle reply form
        function toggleReplyForm(commentId) {
            const form = document.getElementById('replyForm_' + commentId);
            if (form) {
                form.style.display = form.style.display === 'none' ? 'block' : 'none';
            }
        }
        
        // Delete reply via AJAX
        document.querySelectorAll('.delete-reply-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                if (!confirm('Bạn có chắc muốn xóa trả lời này?')) return;
                const replyId = this.getAttribute('data-reply-id');
                fetch(ctx + '/deleteReply', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'action=deleteReply&replyId=' + encodeURIComponent(replyId)
                })
                .then(res => res.text())
                .then(text => {
                    if (text === 'ok') {
                        const item = this.closest('.reply-item');
                        if (item) item.remove();
                    } else {
                        alert('Không thể xóa trả lời.');
                    }
                })
                .catch(() => alert('Có lỗi xảy ra khi xóa.'));
            });
        });
        
        // Setup reply media preview
        function setupReplyMediaPreview(commentId) {
            const fileInput = document.getElementById('replyMediaFiles_' + commentId);
            const preview = document.getElementById('replyMediaPreview_' + commentId);
            
            if (fileInput && preview) {
                fileInput.addEventListener('change', function(e) {
                    previewReplyFiles(e.target.files, preview);
                });
            }
        }
        
        function previewReplyFiles(files, previewElement) {
            previewElement.innerHTML = '';
            
            if (files.length > 0) {
                const fileDataList = [];
                
                for (let i = 0; i < files.length; i++) {
                    const file = files[i];
                    const reader = new FileReader();
                    
                    reader.onload = function(e) {
                        fileDataList.push({
                            file: file,
                            dataUrl: e.target.result,
                            index: i
                        });
                        
                        if (fileDataList.length === files.length) {
                            fileDataList.forEach((item, idx) => {
                                const div = document.createElement('div');
                                div.setAttribute('data-file-index', idx);
                                div.style.position = 'relative';
                                div.style.display = 'inline-block';
                                div.style.margin = '5px';
                                
                                if (item.file.type.startsWith('image/')) {
                                    const img = document.createElement('img');
                                    img.src = item.dataUrl;
                                    img.style.width = '80px';
                                    img.style.height = '80px';
                                    img.style.objectFit = 'cover';
                                    img.style.border = '2px solid #ff69b4';
                                    img.style.borderRadius = '8px';
                                    div.appendChild(img);
                                } else if (item.file.type.startsWith('video/')) {
                                    const video = document.createElement('video');
                                    video.src = item.dataUrl;
                                    video.style.width = '80px';
                                    video.style.height = '80px';
                                    video.style.objectFit = 'cover';
                                    video.style.border = '2px solid #ff69b4';
                                    video.style.borderRadius = '8px';
                                    video.controls = true;
                                    div.appendChild(video);
                                }
                                
                                const removeBtn = document.createElement('span');
                                removeBtn.innerHTML = '&times;';
                                removeBtn.style.position = 'absolute';
                                removeBtn.style.top = '-5px';
                                removeBtn.style.right = '-5px';
                                removeBtn.style.width = '20px';
                                removeBtn.style.height = '20px';
                                removeBtn.style.borderRadius = '50%';
                                removeBtn.style.background = '#e74c3c';
                                removeBtn.style.color = 'white';
                                removeBtn.style.cursor = 'pointer';
                                removeBtn.style.display = 'flex';
                                removeBtn.style.alignItems = 'center';
                                removeBtn.style.justifyContent = 'center';
                                removeBtn.style.fontSize = '16px';
                                removeBtn.style.fontWeight = 'bold';
                                removeBtn.onclick = function() {
                                    div.remove();
                                };
                                div.appendChild(removeBtn);
                                
                                previewElement.appendChild(div);
                            });
                        }
                    };
                    
                    reader.readAsDataURL(file);
                }
            }
        }
        
        // Setup media preview for all reply forms
        document.querySelectorAll('[id^="replyForm_"]').forEach(replyForm => {
            const commentId = replyForm.id.replace('replyForm_', '');
            setupReplyMediaPreview(commentId);
        });
        
        // Wishlist toggle function
        function toggleWishlist(productId, event) {
            // Prevent default form submission if event is provided
            if (event) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            const btn = document.getElementById('wishlistBtn');
            const icon = document.getElementById('wishlistIcon');
            
            if (!btn) {
                console.error('Wishlist button not found');
                return false;
            }
            
            // Prevent multiple clicks
            if (btn.disabled) {
                console.log('Button is already processing, ignoring click');
                return false;
            }
            
            const params = new URLSearchParams();
            params.append('action', 'toggle');
            params.append('productId', productId.toString());
            
            // Debug: Log request contents
            console.log('Sending wishlist request:', {
                action: 'toggle',
                productId: productId
            });
            
            // Disable button during request
            btn.disabled = true;
            btn.style.opacity = '0.6';
            btn.style.cursor = 'not-allowed';
            
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
                        btn.classList.remove('btn-outline-danger');
                        btn.classList.add('btn-danger');
                        if (icon) {
                            icon.classList.remove('far');
                            icon.classList.add('fas');
                        }
                        btn.title = 'Xóa khỏi wishlist';
                    } else {
                        btn.classList.remove('btn-danger');
                        btn.classList.add('btn-outline-danger');
                        if (icon) {
                            icon.classList.remove('fas');
                            icon.classList.add('far');
                        }
                        btn.title = 'Thêm vào wishlist';
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
                btn.disabled = false;
                btn.style.opacity = '1';
                btn.style.cursor = 'pointer';
            });
        }
    </script>
</body>

</html>
