<%@page import="Model.lienhe"%>
<%@page import="java.util.List"%>
<%@page import="Model.user" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/contact-manager.css">
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

        <!-- MODAL LOGIN -->
        <!-- Modal Đăng Nhập -->
        <!--<div class="modal" id="loginModal">
                <div class="modal-content">
                    <span class="close">&times;</span>
                    <h2>Đăng nhập</h2>
                    <form>
                        <div class="input-group">
                            <label for="email">Email</label>
                            <input type="email" id="email" placeholder="Nhập email của bạn" required>
                        </div>
                        <div class="input-group">
                            <label for="password">Mật khẩu</label>
                            <input type="password" id="password" placeholder="Nhập mật khẩu" required>
                        </div>
                        <button type="submit">Đăng nhập</button>
                    </form>
                    <p>Chưa có tài khoản? <a href="#" id="showRegister">Đăng ký ngay</a></p>
                </div>
            </div> -->

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
<head>
    <title>Quản lý phản hồi khách hàng</title>
    <link rel="stylesheet" href="../Css/contact-manager.css"/>
</head>
<body>
    <div class="table-wrapper">
    <table class="feedback-table">
        <thead>
            <tr>
                <th>Tên</th>
                <th>Điện thoại</th>
                <th>Địa chỉ</th>
                <th>Email</th>
                <th>Chủ đề</th>
                <th>Nội dung</th>
                <th>Trạng thái</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="c" items="${contactList}">
                <tr>
                    <td>${c.name}</td>
                    <td>${c.phone}</td>
                    <td>${c.address}</td>
                    <td>${c.email}</td>
                    <td>${c.subject}</td>
                    <td>${c.message}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/UpdateLienheStatusServlet" method="post">
                            <input type="hidden" name="id" value="${c.id}">
                            <input type="hidden" name="redirect" value="${pageContext.request.contextPath}/lienheManagerServlet">
                            <select name="status" class="status-select" onchange="this.form.submit()">
                                <option value="Chưa xử lý" <c:if test="${!c.status}">selected</c:if>>Chưa xử lý</option>
                                <option value="Đã xử lý" <c:if test="${c.status}">selected</c:if>>Đã xử lý</option>
                            </select>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</body>

<%@ include file="/View/includes/footer.jspf" %>
            <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
            <script src="${pageContext.request.contextPath}/Js/home.js"></script>

    </body>

</html>