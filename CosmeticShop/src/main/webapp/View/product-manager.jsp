<%@page import="Model.user"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
        <!-- header -->
        <div class="header">
            <div class="header_text"><p>THEO DÕI CHÚNG TÔI</p></div>
            <div class="header_social">
                <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/fb.png" alt="" ></a>
                <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ins.png" alt=""></a>
                <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/tt.png"alt=""><a>
                        <a href=""><img class="header_social_img" src="${pageContext.request.contextPath}/IMG/ytb.png" alt="" ></a>
                        </div>
                        </div>
                        <!-- menu -->
                        <div class="menu">
                            <div class="menu_logo">
                                <img src="${pageContext.request.contextPath}/IMG/logo.jpg" alt="" style="width: 230px;">
                            </div>
                            <div class="menu_list">
                                <ul class="menu_list_item">
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/home.jsp">TRANG CHỦ</a></li>
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/vechungtoi.jsp">VỀ CHÚNG TÔI</a></li>
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/products">BỘ SƯU TẬP</a></li>
                                        <c:if test="${empty sessionScope.user}">
                                        <li><a class="menu_list_link" href="${pageContext.request.contextPath}/signup">
                                                ĐĂNG NHẬP & ĐĂNG KÝ
                                            </a></li>
                                        </c:if>
                                    <li ><a class="menu_list_link" href="${pageContext.request.contextPath}/View/lienhe.jsp">LIÊN HỆ</a></li>
                                </ul>
                                <div class="menu_search">
                                    <div class="menu_search_input">
                                        <input type="text" placeholder="Nhập từ khóa bạn cần tìm kiếm . . . ">
                                    </div>
                                    <div class="menu_search_icon">
                                        <a href=""><i class="fa-solid fa-magnifying-glass fa-xl" style="color: #f76c85;"></i></a>
                                    </div>
                                </div>
                                <div class="menu_search_cart">
                                    <i class="fa-solid fa-cart-shopping"></i>
                                    <!-- Tài khoản -->
                                    <div class="account-menu">
                                        <i class="fas fa-user-circle account-icon"></i>
                                        <c:if test="${not empty sessionScope.user}">
                                            <div class="account-dropdown">
                                                <p class="welcome-text">Welcome, ${sessionScope.user.username}</p>
                                                <a href="${pageContext.request.contextPath}/View/account.jsp">Account Setting</a>
                                                <a href="${pageContext.request.contextPath}/cart">My Cart</a>
                                                <a href="${pageContext.request.contextPath}/logout">Log Out</a>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>    
                        </div>

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
                                                <!-- Dữ liệu sản phẩm sẽ được load từ database -->
                                                <tr>
                                                    <td>1</td>
                                                    <td>Son môi cao cấp</td>
                                                    <td>299,000 VNĐ</td>
                                                    <td>Son môi không thấm nước</td>
                                                    <td><img src="${pageContext.request.contextPath}/IMG/anh1.png" width="50" height="50" alt="Product"></td>
                                                    <td>
                                                        <button class="btn btn-sm btn-warning me-1" data-bs-toggle="modal" data-bs-target="#editProductModal">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <!-- Thêm các dòng sản phẩm khác ở đây -->
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
                                        <form>
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="productName" class="form-label">Tên sản phẩm</label>
                                                        <input type="text" class="form-control" id="productName" required>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="productPrice" class="form-label">Giá</label>
                                                        <input type="number" class="form-control" id="productPrice" required>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="mb-3">
                                                <label for="productDescription" class="form-label">Mô tả</label>
                                                <textarea class="form-control" id="productDescription" rows="3"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="productImage" class="form-label">Hình ảnh</label>
                                                <input type="file" class="form-control" id="productImage" accept="image/*">
                                            </div>
                                        </form>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                        <button type="button" class="btn btn-primary">Lưu sản phẩm</button>
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
                                                <label for="editProductImage" class="form-label">Hình ảnh</label>
                                                <input type="file" class="form-control" id="editProductImage" accept="image/*">
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
                        
                        <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
                        </body>
                        </html>