<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
        <link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
      integrity="sha512-…"
      crossorigin="anonymous" referrerpolicy="no-referrer" />
       <title>Quản Lý Sản Phẩm - Pinky Cloud</title>
    </head>
    <body>
        <%@ include file="/View/includes/header.jspf" %>


                        <!-- Kiểm tra quyền admin -->
                        <c:if test="${empty sessionScope.user || sessionScope.user.role != 'ADMIN'}">
                            <div class="container mt-5">
                                <div class="alert alert-danger text-center">
                                    <h4>Không có quyền truy cập!</h4>
                                    <p>Bạn cần đăng nhập với tài khoản admin để truy cập trang này.</p>
                                    <a href="${pageContext.request.contextPath}/View/home.jsp" class="btn btn-primary">Về trang chủ</a>
                                </div>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty sessionScope.user && sessionScope.user.role == 'ADMIN'}">
                        <!-- Nội dung quản lý sản phẩm -->
                        <div class="container mt-5">
                            <div class="row">
                                <div class="col-12">
                                    <h2 class="text-center mb-4" style="color: #f76c85;">QUẢN LÝ SẢN PHẨM</h2>
                                    <p class="text-center text-muted">Xin chào Admin: ${sessionScope.user.username}</p>
                                    
                                    <!-- Nút thêm sản phẩm mới -->
                                    <div class="mb-3">
                                        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal">
                                            <i class="fas fa-plus"></i> Thêm sản phẩm mới
                                        </button>
                                    </div>
                                    
                                    <!-- Bảng danh sách sản phẩm -->
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead class="table-dark">
                                                <tr>
                                                    <th>ID</th>
                                                    <th>Tên sản phẩm</th>
                                                    <th>Giá</th>
                                                    <th>Mô tả</th>
                                                    <th>Hình ảnh</th>
                                                    <th>Thao tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <!-- Dữ liệu sản phẩm từ database -->
                                                <c:forEach var="product" items="${productList}">
                                                    <tr>
                                                        <td>${product.productId}</td>
                                                        <td>${product.name}</td>
                                                        <td><span class="text-success fw-bold">${product.price} VNĐ</span></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${product.description.length() > 50}">
                                                                    ${product.description.substring(0, 50)}...
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${product.description}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:if test="${not empty product.imageUrl}">
                                                                <img src="${pageContext.request.contextPath}${product.imageUrl}" 
                                                                     width="50" height="50" alt="Product" 
                                                                     style="object-fit: cover; border-radius: 5px;">
                                                            </c:if>
                                                            <c:if test="${empty product.imageUrl}">
                                                                <span class="text-muted">Không có ảnh</span>
                                                            </c:if>
                                                        </td>
                                                        <td>
                                                            <a href="${pageContext.request.contextPath}/products?action=edit&id=${product.productId}" 
                                                               class="btn btn-sm btn-warning me-1">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                            <a href="${pageContext.request.contextPath}/products?action=delete&id=${product.productId}" 
                                                               class="btn btn-sm btn-danger"
                                                               onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')">
                                                                <i class="fas fa-trash"></i>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                                <c:if test="${empty productList}">
                                                    <tr>
                                                        <td colspan="6" class="text-center text-muted">Chưa có sản phẩm nào</td>
                                                    </tr>
                                                </c:if>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Modal thêm sản phẩm -->
                        <div class="modal fade" id="addProductModal" tabindex="-1">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Thêm sản phẩm mới</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <form action="${pageContext.request.contextPath}/products" method="post" enctype="multipart/form-data">
                                            <input type="hidden" name="action" value="insert" />
                                            
                                            <div class="mb-3">
                                                <label for="name" class="form-label">Tên Sản phẩm</label>
                                                <input type="text" class="form-control" name="name" id="name" required>
                                            </div>

                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="price" class="form-label">Giá (VNĐ)</label>
                                                        <input type="number" step="1000" class="form-control" name="price" id="price" required>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="stock" class="form-label">Tồn kho</label>
                                                        <input type="number" class="form-control" name="stock" id="stock" required>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="mb-3">
                                                <label for="description" class="form-label">Mô tả</label>
                                                <textarea class="form-control" name="description" id="description" rows="4"></textarea>
                                            </div>

                                            <div class="mb-3">
                                                <label for="imageFile" class="form-label">Hình ảnh sản phẩm chính</label>
                                                <input type="file" class="form-control" name="imageFile" id="imageFile" accept="image/*">
                                            </div>

                                            <!-- Các hình ảnh phụ -->
                                            <div class="mb-3">
                                                <label class="form-label">Các hình ảnh phụ</label>
                                                <div id="modalAdditionalImagesContainer">
                                                    <div class="additional-image-item mb-2">
                                                        <div class="row">
                                                            <div class="col-md-10">
                                                                <input type="file" class="form-control" name="additionalImageFiles[]" accept="image/*">
                                                            </div>
                                                            <div class="col-md-2">
                                                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeModalImageItem(this)">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <button type="button" class="btn btn-outline-primary btn-sm" onclick="addModalImageItem()">
                                                    <i class="fas fa-plus"></i> Thêm ảnh phụ
                                                </button>
                                                <!-- <small class="text-muted d-block mt-1">Upload ảnh phụ (thứ tự tự động theo thời gian upload)</small> -->
                                            </div>

                                            <div class="mb-3">
                                                <label for="categoryName" class="form-label">Danh mục</label>
                                                <select class="form-control" name="categoryName" id="categoryName" required>
                                                    <option value="">-- Chọn danh mục --</option>
                                                    <c:forEach var="category" items="${categories}">
                                                        <option value="${category}">${category}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                <button type="submit" class="btn" style="background-color: #f76c85; color: white;">Lưu sản phẩm</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Modal chỉnh sửa sản phẩm -->
                        <div class="modal fade" id="editProductModal" tabindex="-1">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Chỉnh sửa sản phẩm</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <form>
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="editProductName" class="form-label">Tên sản phẩm</label>
                                                        <input type="text" class="form-control" id="editProductName" value="Son môi cao cấp">
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="editProductPrice" class="form-label">Giá</label>
                                                        <input type="number" class="form-control" id="editProductPrice" value="299000">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="mb-3">
                                                <label for="editProductDescription" class="form-label">Mô tả</label>
                                                <textarea class="form-control" id="editProductDescription" rows="3">Son môi không thấm nước</textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="editProductImage" class="form-label">Hình ảnh sản phẩm chính</label>
                                                <input type="file" class="form-control" id="editProductImage" accept="image/*">
                                            </div>

                                            <!-- Các hình ảnh phụ -->
                                            <div class="mb-3">
                                                <label class="form-label">Các hình ảnh phụ</label>
                                                <div id="editModalAdditionalImagesContainer">
                                                    <div class="additional-image-item mb-2">
                                                        <div class="row">
                                                            <div class="col-md-10">
                                                                <input type="file" class="form-control" name="editAdditionalImageFiles[]" accept="image/*">
                                                            </div>
                                                            <div class="col-md-2">
                                                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeEditModalImageItem(this)">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <button type="button" class="btn btn-outline-primary btn-sm" onclick="addEditModalImageItem()">
                                                    <i class="fas fa-plus"></i> Thêm ảnh phụ
                                                </button>
                                                <!-- <small class="text-muted d-block mt-1">Upload ảnh phụ (thứ tự tự động theo thời gian upload)</small> -->
                                            </div>
                                        </form>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                        <button type="button" class="btn btn-primary">Cập nhật sản phẩm</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        </c:if>
                        
                        <!-- Font Awesome for icons -->
                        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
                        
                        <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
                        <script src="${pageContext.request.contextPath}/Js/home.js"></script>
                        <script>
                            // Variables for image management
                            let modalImageItemCount = 1;
                            let editModalImageItemCount = 1;
                            
                            // Functions for Add Product Modal
                            function addModalImageItem() {
                                modalImageItemCount++;
                                const container = document.getElementById('modalAdditionalImagesContainer');
                                const newItem = document.createElement('div');
                                newItem.className = 'additional-image-item mb-2';
                                newItem.innerHTML = `
                                    <div class="row">
                                        <div class="col-md-10">
                                            <input type="file" class="form-control" name="additionalImageFiles[]" accept="image/*">
                                        </div>
                                        <div class="col-md-2">
                                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeModalImageItem(this)">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                `;
                                container.appendChild(newItem);
                            }
                            
                            function removeModalImageItem(button) {
                                const container = document.getElementById('modalAdditionalImagesContainer');
                                if (container.children.length > 1) {
                                    button.closest('.additional-image-item').remove();
                                } else {
                                    alert('Phải có ít nhất một ảnh phụ!');
                                }
                            }
                            
                            // Functions for Edit Product Modal
                            function addEditModalImageItem() {
                                editModalImageItemCount++;
                                const container = document.getElementById('editModalAdditionalImagesContainer');
                                const newItem = document.createElement('div');
                                newItem.className = 'additional-image-item mb-2';
                                newItem.innerHTML = `
                                    <div class="row">
                                        <div class="col-md-10">
                                            <input type="file" class="form-control" name="editAdditionalImageFiles[]" accept="image/*">
                                        </div>
                                        <div class="col-md-2">
                                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeEditModalImageItem(this)">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                `;
                                container.appendChild(newItem);
                            }
                            
                            function removeEditModalImageItem(button) {
                                const container = document.getElementById('editModalAdditionalImagesContainer');
                                if (container.children.length > 1) {
                                    button.closest('.additional-image-item').remove();
                                } else {
                                    alert('Phải có ít nhất một ảnh phụ!');
                                }
                            }
                            
                            // Đóng modal sau khi submit thành công
                            document.addEventListener('DOMContentLoaded', function() {
                                // Kiểm tra nếu có thông báo thành công từ server
                                const urlParams = new URLSearchParams(window.location.search);
                                if (urlParams.get('success') === 'true') {
                                    // Có thể thêm thông báo thành công ở đây
                                    console.log('Thao tác thành công!');
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