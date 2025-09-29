<%-- Thêm isELIgnored="false" để đảm bảo các thẻ JSTL hoạt động chính xác --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Sản phẩm - PinkyCloud</title>
    
    <%-- Sử dụng Bootstrap để giao diện trông đẹp hơn --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    
    <style>
        body {
            background-color: #f9f9f9;
        }
        .form-container {
            max-width: 700px;
            margin: 50px auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 15px;
            box-shadow: 0 6px 20px rgba(0,0,0,0.08);
        }
        .form-container h2 {
            color: #f76c85;
            margin-bottom: 25px;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container form-container">
        <h2 class="text-center">
            <%-- Tiêu đề sẽ tự động thay đổi tùy theo hành động là Sửa hay Thêm mới --%>
            <c:if test="${product != null}">
                Chỉnh sửa Thông tin Sản phẩm
            </c:if>
            <c:if test="${product == null}">
                Thêm Sản phẩm Mới
            </c:if>
        </h2>

        <form action="${pageContext.request.contextPath}/products" method="post">
            
            <%-- Phần logic ẩn: 
                 - Nếu là 'sửa' (product không rỗng), gửi action là "update" và id của sản phẩm.
                 - Nếu là 'thêm mới' (product rỗng), gửi action là "insert".
            --%>
            <c:if test="${product != null}">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="id" value="${product.productId}" />
            </c:if>
            <c:if test="${product == null}">
                <input type="hidden" name="action" value="insert" />
            </c:if>

            <div class="mb-3">
                <label for="name" class="form-label">Tên Sản phẩm</label>
                <input type="text" class="form-control" name="name" id="name" value="${product.name}" required>
            </div>

            <div class="mb-3">
                <label for="price" class="form-label">Giá (VNĐ)</label>
                <input type="number" step="1000" class="form-control" name="price" id="price" value="${product.price}" required>
            </div>

            <div class="mb-3">
                <label for="stock" class="form-label">Số lượng trong kho</label>
                <input type="number" class="form-control" name="stock" id="stock" value="${product.stock}" required>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Mô tả</label>
                <textarea class="form-control" name="description" id="description" rows="4">${product.description}</textarea>
            </div>

            <div class="mb-3">
                <label for="imageUrl" class="form-label">Đường dẫn Hình ảnh (VD: /IMG/anh1.png)</label>
                <input type="text" class="form-control" name="imageUrl" id="imageUrl" value="${product.imageUrl}" required>
            </div>

            <div class="mb-3">
                <label for="categoryId" class="form-label">ID Danh mục</label>
                <input type="number" class="form-control" name="categoryId" id="categoryId" value="${product.categoryId}" required>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary me-md-2">Hủy</a>
                <button type="submit" class="btn" style="background-color: #f76c85; color: white;">Lưu lại</button>
            </div>
        </form>
    </div>
</body>
</html>