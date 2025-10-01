<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>PinkyCloud - Bộ sưu tập</title>

                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bosuutap.css">


            </head>

            <body>
                <%-- Phần Header và Menu được giữ nguyên từ trang home.jsp cho đồng bộ --%>

                    <div class="header">
                        <div class="header_text">
                            <p>THEO DÕI CHÚNG TÔI</p>
                        </div>
                        <div class="header_social">
                            <a href=""><img class="header_social_img"
                                    src="${pageContext.request.contextPath}/IMG/fb.png" alt=""></a>
                            <a href=""><img class="header_social_img"
                                    src="${pageContext.request.contextPath}/IMG/ins.png" alt=""></a>
                            <a href=""><img class="header_social_img"
                                    src="${pageContext.request.contextPath}/IMG/tt.png" alt=""></a>
                            <a href=""><img class="header_social_img"
                                    src="${pageContext.request.contextPath}/IMG/ytb.png" alt=""></a>
                        </div>
                    </div>

                    <div class="menu">
                        <div class="menu_logo">
                            <img src="${pageContext.request.contextPath}/IMG/logo.jpg" alt="" style="width: 230px;">
                        </div>
                        <div class="menu_list">
                            <ul class="menu_list_item">
                                <li><a class="menu_list_link"
                                        href="${pageContext.request.contextPath}/View/home.jsp">TRANG CHỦ</a></li>
                                <li><a class="menu_list_link"
                                        href="${pageContext.request.contextPath}/View/vechungtoi.jsp">VỀ CHÚNG TÔI</a>
                                </li>
                                <li><a class="menu_list_link" href="${pageContext.request.contextPath}/products">BỘ SƯU
                                        TẬP</a></li>
                                <c:if test="${empty sessionScope.user}">
                                    <li><a class="menu_list_link" href="${pageContext.request.contextPath}/signup">ĐĂNG
                                            NHẬP & ĐĂNG KÝ</a></li>
                                </c:if>
                                <li><a class="menu_list_link"
                                        href="${pageContext.request.contextPath}/View/lienhe.jsp">LIÊN HỆ</a></li>
                            </ul>
                            <div class="menu_search">
                                <div class="menu_search_input">
                                    <input type="text" placeholder="Nhập từ khóa bạn cần tìm kiếm . . . ">
                                </div>
                                <div class="menu_search_icon">
                                    <a href=""><i class="fa-solid fa-magnifying-glass fa-xl"
                                            style="color: #f76c85;"></i></a>
                                </div>
                            </div>
                            <div class="menu_search_cart">
                                <i class="fa-solid fa-cart-shopping"></i>
                                <div class="account-menu">
                                    <i class="fas fa-user-circle account-icon"></i>
                                    <c:if test="${not empty sessionScope.user}">
                                        <div class="account-dropdown">
                                            <p class="welcome-text">Welcome, ${sessionScope.user.username}</p>
                                            <a href="#">Account Setting</a>
                                            <a href="#">My Cart</a>
                                            <a href="${pageContext.request.contextPath}/logout">Log Out</a>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <main class="container my-5">
                        <div class="text-center mb-4">
                            <h2 class="fw-bold" style="color: #f76c85; font-family: 'Times New Roman', Times, serif;">BỘ
                                SƯU TẬP SẢN PHẨM</h2>
                            <p>Khám phá những sản phẩm làm đẹp tốt nhất dành cho bạn.</p>

                            <%-- Nút để thêm sản phẩm mới --%>
                                <a href="${pageContext.request.contextPath}/products?action=new" class="btn btn-lg mt-3"
                                    style="background-color: #f76c85; color: white;">
                                    <i class="fas fa-plus-circle"></i> Thêm sản phẩm mới
                                </a>
                        </div>

                        <%-- Lưới hiển thị danh sách sản phẩm --%>
                            <div class="product-grid">
                                <c:forEach var="product" items="${productList}">
                                    <div class="product-card"
                                        onclick="window.location.href='${pageContext.request.contextPath}/product-detail?id=${product.productId}'"
                                        style="cursor: pointer;">
                                        <img src="${pageContext.request.contextPath}${product.imageUrl}"
                                            alt="${product.name}" loading="lazy">
                                        <div class="product-card-body">
                                            <h5>
                                                <c:out value="${product.name}" />
                                            </h5>
                                            <p class="price">
                                                <%-- Định dạng giá tiền cho dễ đọc --%>
                                                    <fmt:formatNumber value="${product.price}" type="currency"
                                                        currencySymbol="" maxFractionDigits="0" /> VNĐ
                                            </p>
                                            <div class="action-buttons">
                                                <a href="${pageContext.request.contextPath}/product-detail?id=${product.productId}"
                                                    class="btn btn-sm btn-outline-info">
                                                    <i class="fas fa-eye"></i> Xem chi tiết
                                                </a>
                                                <a href="${pageContext.request.contextPath}/products?action=edit&id=${product.productId}"
                                                    class="btn btn-sm btn-outline-primary"
                                                    onclick="event.stopPropagation();">
                                                    <i class="fas fa-edit"></i> Sửa
                                                </a>
                                                <a href="${pageContext.request.contextPath}/products?action=delete&id=${product.productId}"
                                                    class="btn btn-sm btn-outline-danger"
                                                    onclick="event.stopPropagation(); return confirm('Bạn có chắc chắn muốn xóa sản phẩm này không?');">
                                                    <i class="fas fa-trash-alt"></i> Xóa
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                    </main>

                    <footer class="text-white py-4 w-100" style="background-color:#f76c85;">
                        <div class="container-fluid text-center">
                            <div class="row">
                                <div class="col-md-3">
                                    <h5 class="fw-bold">PINKYCLOUD OFFICE</h5>
                                    <p>Địa chỉ: Số 31, đường Nguyễn Thị Minh Khai, Phường Quy Nhơn, Gia Lai</p>
                                    <p>Mail: <a href="mailto:pinkycloudvietnam@gmail.com"
                                            class="text-white">pinkycloudvietnam@gmail.com</a></p>
                                    <p>Website: <a href="${pageContext.request.contextPath}/View/home.jsp"
                                            class="text-white">www.pinkycloud.vn</a></p>
                                </div>
                                <div class="col-md-3">
                                    <h5 class="fw-bold">DANH MỤC</h5>
                                    <ul class="list-unstyled">
                                        <li><a href="#" class="text-white text-decoration-none">Sức khỏe và làm đẹp</a>
                                        </li>
                                        <li><a href="#" class="text-white text-decoration-none">Chăm sóc cơ thể</a></li>
                                        <li><a href="#" class="text-white text-decoration-none">Chăm sóc da mặt</a></li>
                                    </ul>
                                </div>
                                <div class="col-md-3">
                                    <h5 class="fw-bold">CHÍNH SÁCH HỖ TRỢ</h5>
                                    <ul class="list-unstyled">
                                        <li><a href="#" class="text-white text-decoration-none">Hỗ trợ đặt hàng</a></li>
                                        <li><a href="#" class="text-white text-decoration-none">Chính sách trả hàng</a>
                                        </li>
                                        <li><a href="#" class="text-white text-decoration-none">Chính sách bảo hành</a>
                                        </li>
                                    </ul>
                                </div>
                                <div class="col-md-3">
                                    <h5 class="fw-bold">THEO DÕI CHÚNG TÔI</h5>
                                    <div class="d-flex info justify-content-center">
                                        <a href="#" class="me-3"><img
                                                src="${pageContext.request.contextPath}/IMG/fbf.png" alt="Facebook"
                                                width="32"></a>
                                        <a href="#" class="me-3"><img
                                                src="${pageContext.request.contextPath}/IMG/linkedin-54890.png"
                                                alt="instagram" width="32"></a>
                                    </div>
                                </div>
                            </div>
                            <hr class="border-white my-3">
                            <div class="text-center">
                                <p class="mb-0">2023 Copyright PinkyCloud.vn</p>
                            </div>
                        </div>
                    </footer>

                    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
                    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
            </body>

            </html>