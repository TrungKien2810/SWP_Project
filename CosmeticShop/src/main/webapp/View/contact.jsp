<%@page import="Model.user" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/contact.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/toast-notification.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
        <title>Pinky Cloud</title>
    </head>

    <body>
        <%@ include file="/View/includes/header.jspf" %>
        <%@ include file="/View/includes/mobile-search.jspf" %>

        <main class="container-fluid" style="margin-top: 20px;">
        
        <!-- Toast notification sẽ được hiển thị tự động qua JavaScript -->
        <div class="row">
            <div class="contact">
                <div class="contact-info">
                    <h2 class="text-center text-uppercase" style="color: grey;"><strong>Liên hệ</strong>
                    </h2>
                    <br>
                    <br>
                    <p>Trụ sở chính: Số 31, đường Nguyễn Thị Minh Khai, Phường Quy Nhơn, Gia Lai</p>
                    <p>Hotline: 0982 350 821</p>
                    <p>Email: pinkycloudvietnam@gmail.com</p>
                </div>
                <div class="col-lg-6 px-5">
<!--                                <form id="contactForm" action="${pageContext.request.contextPath}/lienheServlet"
                        method="post">
                        <h2 class="text-center text-uppercase" style="margin-top: 30px; color: gray;">
                            <strong>Nhập thông tin và ý kiến góp ý</strong>
                        </h2>
                        <br>
                        <div class="row mb-3">
                            <div class="col-md-6"><input type="text" class="form-control" placeholder="Name"
                                    required></div>
                            <div class="col-md-6"><input type="text" class="form-control"
                                    placeholder="Phone" required></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-6"><input type="text" class="form-control"
                                    placeholder="Address"></div>
                            <div class="col-md-6"><input type="email" class="form-control"
                                    placeholder="Email" required></div>
                        </div>
                        <div class="mb-3">
                            <input type="text" class="form-control" placeholder="Chủ đề">
                        </div>
                        <div class="mb-3">
                            <textarea class="form-control" rows="4" placeholder="Nội dung"
                                required></textarea>
                            <br>
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-submit">Gửi</button>
                                <button type="reset" class="btn btn-reset">Nhập lại</button>
                            </div>
                        </div>
                    </form>-->
                    <form id="contactForm" action="${pageContext.request.contextPath}/lienheServlet" method="post">
                        <h2 class="text-center text-uppercase" style="margin-top: 30px; color: gray;">
                            <strong>Nhập thông tin và ý kiến góp ý</strong></h2>
                        <br>
                        <div class="row mb-3">
                            <div class="col-md-6"><input type="text" name="name" class="form-control"
                                                         placeholder="Họ tên" required></div>
                            <div class="col-md-6"><input type="text" name="phone" class="form-control"
                                                         placeholder="Số điện thoại" required></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-6"><input type="text" name="address" class="form-control"
                                                         placeholder="Địa chỉ"></div>
                            <div class="col-md-6"><input type="email" name="email" class="form-control"
                                                         placeholder="Email" required></div>
                        </div>
                        <div class="mb-3">
                            <input type="text" name="subject" class="form-control" placeholder="Chủ đề">
                        </div>
                        <div class="mb-3">
                            <textarea name="message" class="form-control" rows="4" placeholder="Nội dung"
                                      required></textarea>
                            <br>
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-submit">Gửi</button>
                                <button type="reset" class="btn btn-reset">Nhập lại</button>
                            </div>
                        </div>
                    </form>

                </div>
            </div>
            <iframe
                src="https://www.google.com/maps/embed?pb=!1m16!1m12!1m3!1d15501.010015694377!2d109.1995276787998!3d13.763640667669454!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!2m1!1shasaki!5e0!3m2!1svi!2s!4v1761183316788!5m2!1svi!2s"
                width="600" height="450" style="border:0;" allowfullscreen="" loading="lazy"
                referrerpolicy="no-referrer-when-downgrade"></iframe>
                </main>
                <%@ include file="/View/includes/footer.jspf" %>
            <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
            <script src="${pageContext.request.contextPath}/Js/toast-notification.js"></script>
            <script src="${pageContext.request.contextPath}/Js/home.js"></script>
            
            <script>
                // Hiển thị toast notification từ URL parameter
                // Đảm bảo script chạy sau khi DOM và các script khác đã load
                window.addEventListener('load', function() {
                    try {
                        const urlParams = new URLSearchParams(window.location.search);
                        const msg = urlParams.get('msg');
                        
                        if (msg) {
                            // Decode URL parameter (nếu đã được encode)
                            let decodedMsg;
                            try {
                                decodedMsg = decodeURIComponent(msg);
                            } catch (e) {
                                // Nếu decode thất bại, dùng giá trị gốc
                                decodedMsg = msg;
                            }
                            
                            // Xác định loại thông báo dựa trên nội dung
                            const msgLower = decodedMsg.toLowerCase();
                            let type = 'info';
                            let duration = 4000;
                            
                            if (msgLower.includes('thành công') || msgLower.includes('success')) {
                                type = 'success';
                                duration = 5000;
                            } else if (msgLower.includes('thất bại') || msgLower.includes('lỗi') || msgLower.includes('error')) {
                                type = 'error';
                                duration = 6000;
                            } else if (msgLower.includes('cảnh báo') || msgLower.includes('warning')) {
                                type = 'warning';
                                duration = 5000;
                            }
                            
                            // Đợi một chút để đảm bảo showToast đã được load
                            setTimeout(function() {
                                if (typeof showToast === 'function') {
                                    showToast(decodedMsg, type, duration);
                                } else {
                                    // Fallback: hiển thị alert nếu toast không có
                                    alert(decodedMsg);
                                    console.warn('showToast function not found, using alert fallback');
                                }
                            }, 200);
                            
                            // Xóa parameter khỏi URL để tránh hiển thị lại khi refresh
                            const newUrl = window.location.pathname;
                            window.history.replaceState({}, document.title, newUrl);
                        }
                    } catch (e) {
                        console.error('Error showing notification:', e);
                    }
                });
            </script>

    </body>

</html>