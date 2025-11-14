<%-- Thêm isELIgnored="false" để đảm bảo các thẻ JSTL hoạt động chính xác --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Sản phẩm - PinkyCloud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-…" crossorigin="anonymous" referrerpolicy="no-referrer" />
    
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
    <%@ include file="/View/includes/header.jspf" %>
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
                <label for="price" class="form-label">Giá (₫)</label>
                <input type="number" step="1000" class="form-control" name="price" id="price" value="${product.price}" required>
            </div>

            <div class="mb-3">
                <label for="stock" class="form-label">Số lượng trong kho</label>
                <input type="number" class="form-control" name="stock" id="stock" value="${product.stock}" required>
            </div>

            <!-- Mô tả sản phẩm nhiều mục -->
            <div class="mb-4">
                <label class="form-label" style="color: #f76c85; font-weight: 600; font-size: 16px; margin-bottom: 15px;">
                    <i class="fas fa-edit me-2"></i>Mô tả sản phẩm
                </label>
                <div id="descriptionSectionsContainer">
                    <c:choose>
                        <c:when test="${product != null && not empty productDescriptionSections}">
                            <!-- Hiển thị các mục mô tả hiện có khi chỉnh sửa -->
                            <c:forEach var="section" items="${productDescriptionSections}" varStatus="loop">
                                <div class="description-section-item">
                                    <div class="row">
                                        <div class="col-md-10">
                                            <label class="form-label">Tên mục ${loop.index + 1}</label>
                                            <input type="text" class="form-control" name="descriptionSectionTitles[]" 
                                                   value="${section.title}" placeholder="Ví dụ: Thành phần, Cách sử dụng, Lưu ý...">
                                            
                                            <label class="form-label mt-3">Nội dung mục ${loop.index + 1}</label>
                                            <textarea class="form-control" name="descriptionSectionContents[]" 
                                                      placeholder="Nhập nội dung chi tiết...">${section.content}</textarea>
                                        </div>
                                        <div class="col-md-2 d-flex justify-content-center align-items-start">
                                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeDescriptionSection(this)">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <!-- Hiển thị mục mô tả mặc định khi thêm mới -->
                            <div class="description-section-item">
                                <div class="row">
                                    <div class="col-md-10">
                                        <label class="form-label">Tên mục 1</label>
                                        <input type="text" class="form-control" name="descriptionSectionTitles[]" 
                                               placeholder="Ví dụ: Thành phần, Cách sử dụng, Lưu ý...">
                                        
                                        <label class="form-label mt-3">Nội dung mục 1</label>
                                        <textarea class="form-control" name="descriptionSectionContents[]" 
                                                  placeholder="Nhập nội dung chi tiết..."></textarea>
                                    </div>
                                    <div class="col-md-2 d-flex justify-content-center align-items-start">
                                        <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeDescriptionSection(this)">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="text-center mt-3">
                    <button type="button" class="btn btn-outline-primary" onclick="addDescriptionSection()">
                        <i class="fas fa-plus me-2"></i>Thêm mục mô tả mới
                    </button>
                    <small class="text-muted d-block mt-2">
                        <i class="fas fa-info-circle me-1"></i>
                        Thêm các mục mô tả khác nhau cho sản phẩm (ví dụ: Thành phần, Cách sử dụng, Lưu ý, v.v.)
                    </small>
                </div>
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
                <label for="categoryIds" class="form-label">Danh mục <span class="text-muted">(có thể chọn nhiều)</span></label>
                <select class="form-control" name="categoryIds" id="categoryIds" multiple size="5" style="min-height: 120px;">
                    <c:forEach var="category" items="${categories}">
                        <c:set var="isSelected" value="false" />
                        <c:if test="${product != null && not empty currentCategoryIds}">
                            <c:forEach var="catId" items="${currentCategoryIds}">
                                <c:if test="${catId == category.categoryId}">
                                    <c:set var="isSelected" value="true" />
                                </c:if>
                            </c:forEach>
                        </c:if>
                        <option value="${category.categoryId}" <c:if test="${isSelected}">selected</c:if>>
                            ${category.name}
                        </option>
                    </c:forEach>
                </select>
                <small class="form-text text-muted">
                    <i class="fas fa-info-circle"></i> Giữ <kbd>Ctrl</kbd> (Windows) hoặc <kbd>Cmd</kbd> (Mac) để chọn nhiều danh mục
                </small>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/admin?action=products" class="btn btn-secondary me-md-2">Hủy</a>
                <button type="submit" class="btn" style="background-color: #f76c85; color: white;">Lưu lại</button>
            </div>
        </form>
    </div>

    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <script>
        let imageItemCount = 1;
        let descriptionSectionCount = 1;
        
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
        
        function addDescriptionSection() {
            const addButton = document.querySelector('button[onclick="addDescriptionSection()"]');
            addButton.classList.add('loading');
            
            setTimeout(() => {
                descriptionSectionCount++;
                const container = document.getElementById('descriptionSectionsContainer');
                const newItem = document.createElement('div');
                newItem.className = 'description-section-item';
                newItem.innerHTML = `
                    <div class="row">
                        <div class="col-md-10">
                            <label class="form-label">Tên mục ${descriptionSectionCount}</label>
                            <input type="text" class="form-control" name="descriptionSectionTitles[]" 
                                   placeholder="Ví dụ: Thành phần, Cách sử dụng, Lưu ý...">
                            
                            <label class="form-label mt-3">Nội dung mục ${descriptionSectionCount}</label>
                            <textarea class="form-control" name="descriptionSectionContents[]" 
                                      placeholder="Nhập nội dung chi tiết..."></textarea>
                        </div>
                        <div class="col-md-2 d-flex justify-content-center align-items-start">
                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeDescriptionSection(this)">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                `;
                container.appendChild(newItem);
                
                // Focus vào input đầu tiên của mục mới
                const newInput = newItem.querySelector('input[type="text"]');
                if (newInput) {
                    newInput.focus();
                }
                
                // Auto-resize cho textarea mới
                const newTextarea = newItem.querySelector('textarea');
                if (newTextarea) {
                    autoResizeTextarea(newTextarea);
                    newTextarea.addEventListener('input', function() {
                        autoResizeTextarea(this);
                    });
                }
                
                addButton.classList.remove('loading');
            }, 300);
        }
        
        function removeDescriptionSection(button) {
            const container = document.getElementById('descriptionSectionsContainer');
            if (container.children.length > 1) {
                button.closest('.description-section-item').remove();
            } else {
                alert('Phải có ít nhất một mục mô tả!');
            }
        }
        
        // Auto-resize textarea function
        function autoResizeTextarea(textarea) {
            textarea.style.height = 'auto';
            textarea.style.height = textarea.scrollHeight + 'px';
        }
        
        // Load existing additional images when editing
        document.addEventListener('DOMContentLoaded', function() {
            // Update imageItemCount based on existing images
            const existingItems = document.querySelectorAll('#additionalImagesContainer .additional-image-item');
            if (existingItems.length > 0) {
                imageItemCount = existingItems.length;
            }
            
            // Update descriptionSectionCount based on existing sections
            const existingDescriptionItems = document.querySelectorAll('#descriptionSectionsContainer .description-section-item');
            if (existingDescriptionItems.length > 0) {
                descriptionSectionCount = existingDescriptionItems.length;
            }
            
            // Auto-resize existing textareas
            const textareas = document.querySelectorAll('.description-section-item textarea');
            textareas.forEach(textarea => {
                autoResizeTextarea(textarea);
                textarea.addEventListener('input', function() {
                    autoResizeTextarea(this);
                });
            });
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
        
        .description-section-item {
            border: 2px solid #f0f0f0;
            border-radius: 12px;
            padding: 20px;
            background: linear-gradient(135deg, #fff 0%, #fafafa 100%);
            transition: all 0.3s ease;
            position: relative;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }
        
        .description-section-item:hover {
            border-color: #f76c85;
            background: linear-gradient(135deg, #fff 0%, #fff5f7 100%);
            box-shadow: 0 4px 20px rgba(247, 108, 133, 0.15);
            transform: translateY(-2px);
        }
        
        .description-section-item::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #f76c85, #ff9eb5, #f76c85);
            border-radius: 12px 12px 0 0;
        }
        
        .description-section-item .row {
            margin: 0;
        }
        
        .description-section-item .col-md-10 {
            padding: 0 10px;
        }
        
        .description-section-item .col-md-2 {
            padding: 0 10px;
            display: flex;
            justify-content: center;
            align-items: flex-start;
            padding-top: 30px;
        }
        
        .description-section-item .form-label {
            color: #f76c85 !important;
            font-weight: 600 !important;
            font-size: 14px !important;
            margin-bottom: 8px !important;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .description-section-item input[type="text"] {
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 12px 15px;
            font-size: 14px;
            transition: all 0.3s ease;
            background: #fff;
            box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
        }
        
        .description-section-item input[type="text"]:focus {
            border-color: #f76c85;
            box-shadow: 0 0 0 3px rgba(247, 108, 133, 0.1), inset 0 1px 3px rgba(0, 0, 0, 0.05);
            outline: none;
        }
        
        .description-section-item textarea {
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 12px 15px;
            font-size: 14px;
            transition: all 0.3s ease;
            background: #fff;
            box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
            resize: none;
            min-height: 80px;
            height: auto;
            overflow-y: hidden;
        }
        
        .description-section-item textarea:focus {
            border-color: #f76c85;
            box-shadow: 0 0 0 3px rgba(247, 108, 133, 0.1), inset 0 1px 3px rgba(0, 0, 0, 0.05);
            outline: none;
        }
        
        .description-section-item .btn {
            width: 100%;
            height: 40px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 5px;
        }
        
        .description-section-item .btn-outline-danger {
            border-color: #dc3545;
            color: #dc3545;
            background: #fff;
        }
        
        .description-section-item .btn-outline-danger:hover {
            background: #dc3545;
            color: #fff;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
        }
        
        .description-section-item .btn-outline-primary {
            background: linear-gradient(135deg, #f76c85, #ff9eb5);
            border: none;
            color: #fff;
            font-weight: 600;
            padding: 10px 20px;
            border-radius: 25px;
            transition: all 0.3s ease;
            box-shadow: 0 2px 10px rgba(247, 108, 133, 0.3);
        }
        
        .description-section-item .btn-outline-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(247, 108, 133, 0.4);
            background: linear-gradient(135deg, #e55a7a, #ff8db0);
        }
        
        .description-section-item .btn-outline-primary:focus {
            box-shadow: 0 0 0 3px rgba(247, 108, 133, 0.25);
        }
        
        /* Animation cho việc thêm/xóa mục */
        .description-section-item {
            animation: slideInUp 0.3s ease-out;
        }
        
        @keyframes slideInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        /* Responsive design */
        @media (max-width: 768px) {
            .description-section-item .col-md-10,
            .description-section-item .col-md-2 {
                margin-bottom: 15px;
            }
            
            .description-section-item .col-md-2 {
                justify-content: center;
                padding-top: 15px;
            }
            
            .description-section-item .btn {
                height: 45px;
                font-size: 14px;
            }
        }
        
        /* Hiệu ứng loading cho nút thêm */
        .btn-outline-primary.loading {
            pointer-events: none;
            opacity: 0.7;
        }
        
        .btn-outline-primary.loading::after {
            content: '';
            display: inline-block;
            width: 12px;
            height: 12px;
            border: 2px solid #fff;
            border-radius: 50%;
            border-top-color: transparent;
            animation: spin 1s linear infinite;
            margin-left: 8px;
        }
        
        @keyframes spin {
            to {
                transform: rotate(360deg);
            }
        }
    </style>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
</body>
</html>