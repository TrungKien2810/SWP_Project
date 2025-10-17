<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý mã giảm giá</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bosuutap.css">
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <style>
        .container { max-width: 1100px; }
    </style>
    </head>
<body>
<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3>Quản lý mã giảm giá</h3>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/discounts?action=new">Tạo mã mới</a>
    </div>

    <table class="table table-striped">
        <thead>
            <tr>
                <th>ID</th><th>Code</th><th>Tên</th><th>Loại</th><th>Giá trị</th><th>Bắt đầu</th><th>Kết thúc</th><th>Active</th><th></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="d" items="${discounts}">
                <tr>
                    <td>${d.discountId}</td>
                    <td>${d.code}</td>
                    <td>${d.name}</td>
                    <td>${d.type}</td>
                    <td>${d.value}</td>
                    <td>${d.startDate}</td>
                    <td>${d.endDate}</td>
                    <td>${d.active}</td>
                    <td>
                        <a class="btn btn-sm btn-secondary" href="${pageContext.request.contextPath}/discounts?action=edit&id=${d.discountId}">Sửa</a>
                        <form action="${pageContext.request.contextPath}/discounts" method="post" style="display:inline" onsubmit="return confirm('Xóa mã này?');">
                            <input type="hidden" name="action" value="delete" />
                            <input type="hidden" name="id" value="${d.discountId}" />
                            <button class="btn btn-sm btn-danger">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>


