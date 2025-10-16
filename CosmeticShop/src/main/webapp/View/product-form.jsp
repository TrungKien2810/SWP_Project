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

        <form action="${pageContext.request.contextPath}/products" method="post" enctype="multipart/form-data">
            
            <%-- Phần logic ẩn: 
                 - Nếu là 'sửa' (product không rỗng), gửi action là "update" và id của sản phẩm.
                 - Nếu là 'thêm mới' (product rỗng), gửi action là "insert".
            --%>
            <c:if test="${product != null}">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="id" value="${product.productId}" />
                <input type="hidden" name="currentImageUrl" value="${product.imageUrl}" />
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
                <label for="imageFile" class="form-label">Hình ảnh sản phẩm chính</label>
                <input type="file" class="form-control" name="imageFile" id="imageFile" accept="image/*">
                <c:if test="${product != null && product.imageUrl != null && product.imageUrl != ''}">
                    <small class="text-muted">Hình hiện tại: ${product.imageUrl}</small>
                </c:if>
            </div>

            <!-- Các hình ảnh phụ -->
            <div class="mb-3">
                <label class="form-label">Các hình ảnh phụ</label>
                <div id="additionalImagesContainer">
                    <c:choose>
                        <c:when test="${not empty existingImages}">
                            <!-- Hiển thị ảnh phụ hiện có khi chỉnh sửa -->
                            <c:forEach var="image" items="${existingImages}" varStatus="loop">
                                <div class="existing-image-item mb-3 p-3 border rounded">
                                    <div class="row align-items-center">
                                        <div class="col-md-2">
                                            <img src="${pageContext.request.contextPath}${image.imageUrl}" 
                                                 alt="Ảnh phụ ${loop.index + 1}" 
                                                 class="img-thumbnail" 
                                                 style="max-width: 80px; max-height: 80px;">
                                        </div>
                                        <div class="col-md-8">
                                            <small class="text-muted d-block">Ảnh hiện tại: ${image.imageUrl}</small>
                                            <small class="text-muted">Thứ tự: ${image.imageOrder}</small>
                                        </div>
                                        <div class="col-md-2">
                                            <div class="form-check">
                                                <input class="form-check-input" 
                                                       type="checkbox" 
                                                       name="deleteImageIds" 
                                                       value="${image.imageId}" 
                                                       id="deleteImage${image.imageId}">
                                                <label class="form-check-label text-danger" for="deleteImage${image.imageId}">
                                                    <i class="fas fa-trash"></i> Xóa
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                    </c:choose>
                    
                    <!-- Phần thêm ảnh phụ mới -->
                    <c:if test="${product != null}">
                        <div class="mt-3">
                            <h6 class="text-primary">Thêm ảnh phụ mới:</h6>
                        </div>
                    </c:if>
                    
                    <c:choose>
                        <c:when test="${product == null}">
                            <!-- Hiển thị ảnh phụ mặc định khi thêm mới -->
                            <div class="additional-image-item mb-2">
                                <div class="row">
                                    <div class="col-md-10">
                                        <input type="file" class="form-control" name="additionalImageFiles[]" accept="image/*">
                                    </div>
                                    <div class="col-md-2">
                                        <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeImageItem(this)">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                    </c:choose>
                </div>
                <button type="button" class="btn btn-outline-primary btn-sm" onclick="addImageItem()">
                    <i class="fas fa-plus"></i> Thêm ảnh phụ mới
                </button>
                <small class="text-muted d-block mt-1">
                    <c:choose>
                        <c:when test="${product != null}">
                            Chọn checkbox để xóa ảnh phụ hiện có, hoặc upload ảnh mới để thêm vào
                        </c:when>
                        <c:otherwise>
                            Upload ảnh phụ (thứ tự tự động theo thời gian upload)
                        </c:otherwise>
                    </c:choose>
                </small>
            </div>

            <div class="mb-3">
                <label for="categoryName" class="form-label">Danh mục</label>
                <select class="form-control" name="categoryName" id="categoryName" required>
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category}" 
                                <c:if test="${category == currentCategoryName}">selected</c:if>>
                            ${category}
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${product != null}">
                    <input type="hidden" name="currentCategoryId" value="${product.categoryId}" />
                </c:if>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary me-md-2">Hủy</a>
                <button type="submit" class="btn" style="background-color: #f76c85; color: white;">Lưu lại</button>
            </div>
        </form>
    </div>

    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <script>
        let imageItemCount = 1;
        
        function addImageItem() {
            imageItemCount++;
            const container = document.getElementById('additionalImagesContainer');
            const newItem = document.createElement('div');
            newItem.className = 'additional-image-item mb-2';
            newItem.innerHTML = `
                <div class="row">
                    <div class="col-md-10">
                        <input type="file" class="form-control" name="additionalImageFiles[]" accept="image/*">
                    </div>
                    <div class="col-md-2">
                        <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeImageItem(this)">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            `;
            container.appendChild(newItem);
        }
        
        function removeImageItem(button) {
            const container = document.getElementById('additionalImagesContainer');
            if (container.children.length > 1) {
                button.closest('.additional-image-item').remove();
            } else {
                alert('Phải có ít nhất một ảnh phụ!');
            }
        }
        
        // Load existing additional images when editing
        document.addEventListener('DOMContentLoaded', function() {
            // Update imageItemCount based on existing images
            const existingItems = document.querySelectorAll('#additionalImagesContainer .additional-image-item');
            if (existingItems.length > 0) {
                imageItemCount = existingItems.length;
            }
        });
    </script>
    
    <style>
        .additional-image-item {
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 10px;
            background-color: #f8f9fa;
            transition: all 0.3s ease;
        }
        
        .additional-image-item:hover {
            border-color: #f76c85;
            background-color: #fff;
        }
        
        .additional-image-item .btn {
            width: 100%;
        }
        
        .additional-image-item input[type="text"] {
            border: 2px solid #e9ecef;
            transition: border-color 0.3s ease;
        }
        
        .additional-image-item input[type="text"]:focus {
            border-color: #f76c85;
            box-shadow: 0 0 0 0.2rem rgba(247, 108, 133, 0.25);
        }
        
        .additional-image-item input[type="number"] {
            border: 2px solid #e9ecef;
            transition: border-color 0.3s ease;
        }
        
        .additional-image-item input[type="number"]:focus {
            border-color: #f76c85;
            box-shadow: 0 0 0 0.2rem rgba(247, 108, 133, 0.25);
        }
    </style>
</body>
</html>