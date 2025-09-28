<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Product" %>
<%
    Product p = (Product) request.getAttribute("product");
%>
<html>
<head>
    <title>Chi tiết sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/product-detail.css">
</head>
<body>
    <div class="container">
        <% if (p != null) { %>
            <div class="product-image">
                <img src="<%= p.getImageUrl() %>" alt="Hình sản phẩm"/>
            </div>
            <div class="product-details">
                <h2>Thông tin sản phẩm</h2>
                <p><b>ID:</b> <%= p.getProductId() %></p>
                <p><b>Tên:</b> <%= p.getName() %></p>
                <p><b>Giá:</b> 
                    <span class="price"><%= p.getPrice() %> VND</span>
                    <span class="original-price">259,000đ</span>
                    <span class="discount">-38%</span>
                </p>
                <p><b>Số lượng:</b> <%= p.getStock() %></p>
                <p><b>Mô tả:</b> <%= p.getDescription() %></p>
                <p><b>Danh mục ID:</b> <%= p.getCategoryId() %></p>
                <button class="btn">Mua ngay</button>
            </div>
            <div class="clear"></div>
        <% } else { %>
            <p>Không tìm thấy sản phẩm</p>
        <% } %>
    </div>
</body>
</html>
