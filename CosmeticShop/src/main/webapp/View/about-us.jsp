<%@page import="Model.user" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/about-us.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
                    integrity="sha512-…" crossorigin="anonymous" referrerpolicy="no-referrer" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
                <title>Pinky Cloud</title>
            </head>

            <body>
                <%@ include file="/View/includes/header.jspf" %>
                    <c:if test="${not empty param.msg}">
                        <div class="container mt-3">
                            <div class="alert alert-success">${param.msg}</div>
                        </div>
                    </c:if>

                    <!-- Modal Đăng Ký -->
                    <div class="modal" id="registerModal">
                        <div class="modal-content">
                            <span class="close">&times;</span>
                            <h2>Đăng ký</h2>
                            <form>
                                <div class="input-group">
                                    <label for="fullname">Họ và tên</label>
                                    <input type="text" id="fullname" placeholder="Nhập họ và tên" required>
                                </div>
                                <div class="input-group">
                                    <label for="emailReg">Email</label>
                                    <input type="email" id="emailReg" placeholder="Nhập email của bạn" required>
                                </div>
                                <div class="input-group">
                                    <label for="passwordReg">Mật khẩu</label>
                                    <input type="password" id="passwordReg" placeholder="Tạo mật khẩu" required>
                                </div>
                                <button type="submit">Đăng ký</button>
                            </form>
                            <p>Đã có tài khoản? <a href="#" id="showLogin">Đăng nhập</a></p>
                        </div>
                    </div>
                    <div class="container brand-section" style="margin-top: 30px;">
                        <div class="row align-items-center">
                            <!-- Cột Hình Ảnh -->
                            <div class="col-md-6 text-center">
                                <img src="../IMG/logoQuangba.png" class="img-fluid" alt="Thương Hiệu PinkyCloud">
                            </div>

                            <!-- Cột Nội Dung -->
                            <div class="col-md-6">
                                <h2 class="brand-title">VỀ THƯƠNG HIỆU PINKYCLOUD</h2>
                                <p class="brand-text">
                                    Thương hiệu mỹ phẩm <strong>PINKYCLOUD</strong> thuộc sở hữu của Công ty TNHH Sản
                                    Xuất & Thương Mại PinkyCloud, được đăng ký độc quyền tại Việt Nam từ năm 2023.
                                    PINKYCLOUD tự hào là một trong những đơn vị tiên phong trong lĩnh vực cung cấp mỹ
                                    phẩm trang điểm và chăm sóc da với phong cách hiện đại, dễ thương và phù hợp với làn
                                    da phụ nữ Việt.
                                </p>
                                <p class="brand-text">
                                    Trụ sở chính của PINKYCLOUD được đặt tại Phường Quy Nhơn, Tỉnh Gia Lai Hiện nay, hệ
                                    thống chi nhánh của PINKYCLOUD đã có mặt tại TP. Hồ Chí Minh, Nha Trang, Đà Nẵng,
                                    Bình Dương và Gia Lai, nhằm phục vụ khách hàng trên khắp cả nước. Đặc biệt, cửa
                                    hàng trải nghiệm và mua sắm <strong>PINKYCLOUD Beauty Concept Store</strong> chính
                                    thức khai trương vào tháng 03/2024, là điểm đến lý tưởng cho những ai yêu thích mỹ
                                    phẩm chính hãng, muốn trực tiếp trải nghiệm các dòng sản phẩm trang điểm và chăm sóc
                                    da mang phong cách hiện đại, dễ thương và phù hợp với làn da người Việt.
                                </p>
                                <p class="brand-text">
                                    PINKYCLOUD sở hữu nhà máy sản xuất hiện đại với diện tích hơn 1.500m², bao gồm 5
                                    phân xưởng, 10 dây chuyền đóng gói và hơn 200 nhân sự lành nghề. Toàn bộ quy trình
                                    sản xuất được thực hiện 100% tại Việt Nam, từ nghiên cứu công thức đến đóng gói
                                    thành phẩm.
                                </p>
                                <p class="brand-text">
                                    Các sản phẩm của PINKYCLOUD đều phải trải qua quy trình kiểm định chất lượng nghiêm
                                    ngặt, đảm bảo độ an toàn, hiệu quả và phù hợp với làn da phụ nữ Việt trước khi chính
                                    thức phân phối ra thị trường.
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="container feature-box">
                        <div class="row g-4">
                            <!-- Cột 1 -->
                            <div class="col-md-6">
                                <div class="feature-card">
                                    <div class="d-flex align-items-start">
                                        <span class="feature-icon">❯</span>
                                        <h5 class="feature-title">100% SẢN XUẤT TẠI VIỆT NAM</h5>
                                    </div>
                                    <p class="feature-text">
                                        Tất cả sản phẩm tại PinkyCloud đều tuyển chọn kỹ lưỡng, phân phối chính hãng.
                                        Chúng tôi tự hào là thương hiệu Việt, đồng hành cùng vẻ đẹp của phụ nữ Việt Nam.
                                    </p>
                                </div>
                            </div>

                            <!-- Cột 2 -->
                            <div class="col-md-6">
                                <div class="feature-card">
                                    <div class="d-flex align-items-start">
                                        <span class="feature-icon">❯</span>
                                        <h5 class="feature-title">HÀI LÒNG KHÁCH HÀNG</h5>
                                    </div>
                                    <p class="feature-text">
                                        Luôn có mặt khi khách hàng cần. Luôn lắng nghe khi khách hàng nói. Luôn cố gắng
                                        đáp ứng mọi nhu cầu của khách hàng.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="container mt-5 text-center">
                        <img src="${pageContext.request.contextPath}/IMG/ceo.png" alt="CEO"
                            class="img-fluid rounded shadow-lg">
                    </div>
                    <section class="core-values m-lg-2">
                        <div class="overlay"></div>
                        <div class="container position-relative">
                            <h2 class="text-uppercase fw-bold" data-aos="fade-up">Giá Trị Cốt Lõi</h2>
                            <div class="row">
                                <div class="col-md-4 mb-4" data-aos="fade-up" data-aos-delay="200">
                                    <div class="core-item p-4 shadow">
                                        <h4>Uy Tín</h4>
                                        <p>Uy tín với triết lý kinh doanh của công ty, với khách hàng, với đối tác, với
                                            cộng sự. Uy tín trong từng giao dịch và từng sản phẩm.</p>
                                    </div>
                                </div>
                                <div class="col-md-4 mb-4" data-aos="fade-up" data-aos-delay="400">
                                    <div class="core-item p-4 shadow">
                                        <h4>Chất Lượng</h4>
                                        <p>Sản phẩm tung ra thị trường phải là những sản phẩm chất lượng nhất, được làm
                                            ra từ khối óc và bàn tay người Việt Nam.</p>
                                    </div>
                                </div>
                                <div class="col-md-4 mb-4" data-aos="fade-up" data-aos-delay="600">
                                    <div class="core-item p-4 shadow">
                                        <h4>Tử Tế</h4>
                                        <p>Tử tế với khách hàng, với cộng sự, với đối tác và xã hội. Tôn trọng các tiêu
                                            chuẩn đã được thiết lập và hành động một cách tử tế.</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <section class="mission-vision">
                        <div class="container">
                            <div class="row">
                                <!-- Mission -->
                                <div class="col-md-6">
                                    <div class="image-container">
                                        <img src="${pageContext.request.contextPath}/IMG/GT4.png" alt="Mission">
                                        <div class="overlay-content">
                                            <h3 class="title">SỨ MỆNH</h3>
                                            <p class="content">Mang đến những sản phẩm làm đẹp chất lượng nhất với giá
                                                thành tối ưu, giúp bạn tự tin tỏa sáng mỗi ngày.</p>
                                        </div>
                                    </div>
                                </div>

                                <!-- Vision -->
                                <div class="col-md-6">
                                    <div class="image-container">
                                        <img src="${pageContext.request.contextPath}/IMG/GT5.png" alt="Vision">
                                        <div class="overlay-content">
                                            <h3 class="title">TẦM NHÌN</h3>
                                            <p class="content">Trở thành thương hiệu mỹ phẩm được phái đẹp Việt tin chọn
                                                và yêu mến.</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <%@ include file="/View/includes/footer.jspf" %>
                        <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
                        <script src="${pageContext.request.contextPath}/Js/home.js"></script>

            </body>

            </html>