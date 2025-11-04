<%@page import="Model.ShippingAddress"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <title>Địa chỉ giao hàng</title>
    <style>
        .container-sm { max-width: 900px; }
        .card { border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .required::after { content:" *"; color:#dc3545; }
        .badge-default { background-color:#f76c85; }
    </style>
    <script>
        function confirmDelete(id){
            if(confirm('Xóa địa chỉ này?')){
                window.location = '${pageContext.request.contextPath}/shipping-address?action=delete&id=' + id;
            }
        }
    </script>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <div class="container-sm my-4">
        <c:if test="${param.error == 'cannot_delete'}">
            <div class="alert alert-danger">Không thể xóa địa chỉ. Có thể đang được dùng trong đơn hàng.</div>
        </c:if>
        <div class="card p-4 mb-4">
            <h4 class="mb-3">Địa chỉ giao hàng</h4>
            <form method="post" action="${pageContext.request.contextPath}/shipping-address">
                <input type="hidden" name="address_id" value="${address.addressId}">
                <c:if test="${not empty return_to}">
                    <input type="hidden" name="return_to" value="${return_to}">
                </c:if>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label required">Họ và tên</label>
                        <input name="full_name" class="form-control" value="${address.fullName}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label required">Số điện thoại</label>
                        <input name="phone" class="form-control" value="${address.phone}" required>
                    </div>
                    <div class="col-12">
                        <label class="form-label required">Địa chỉ</label>
                        <input name="address" class="form-control" value="${address.address}" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label required">Tỉnh/Thành</label>
                        <input name="city" class="form-control" value="${address.city}" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label required">Quận/Huyện</label>
                        <input name="district" class="form-control" value="${address.district}" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label required">Phường/Xã</label>
                        <input name="ward" class="form-control" value="${address.ward}" required>
                    </div>
                    <div class="col-12">
                        <div class="form-check">
                            <c:choose>
                                <c:when test="${address['default']}">
                                    <input class="form-check-input" type="checkbox" name="is_default" id="is_default" checked="checked">
                                </c:when>
                                <c:otherwise>
                                    <input class="form-check-input" type="checkbox" name="is_default" id="is_default">
                                </c:otherwise>
                            </c:choose>
                            <label class="form-check-label" for="is_default">Đặt làm địa chỉ mặc định</label>
                        </div>
                    </div>
                </div>
                <div class="mt-3 d-flex gap-2">
                    <button class="btn btn-primary" style="background-color:#f76c85;border-color:#f76c85;">
                        <i class="fa fa-save me-1"></i>Lưu địa chỉ
                    </button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/shipping-address">Làm mới</a>
                    <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/account-management">Quay lại tài khoản</a>
                </div>
            </form>
        </div>

        <div class="card p-4">
            <h5 class="mb-3">Danh sách địa chỉ</h5>
            <c:choose>
                <c:when test="${empty addresses}">
                    <p>Chưa có địa chỉ nào.</p>
                </c:when>
                <c:otherwise>
                    <div class="list-group">
                        <c:forEach var="item" items="${addresses}">
                            <div class="list-group-item d-flex justify-content-between align-items-start">
                                <div class="ms-2 me-auto">
                                    <div class="fw-bold">${item.fullName} - ${item.phone}
                                        <c:if test="${item['default']}">
                                            <span class="badge badge-default ms-2">Mặc định</span>
                                        </c:if>
                                    </div>
                                    <div>${item.address}, ${item.ward}, ${item.district}, ${item.city}</div>
                                </div>
                                <div class="btn-group">
                                    <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/shipping-address?action=edit&id=${item.addressId}"><i class="fa fa-pen"></i></a>
                                    <button class="btn btn-sm btn-outline-danger" onclick="confirmDelete('${item.addressId}')"><i class="fa fa-trash"></i></button>
                                    <a class="btn btn-sm btn-outline-success" href="${pageContext.request.contextPath}/shipping-address?action=default&id=${item.addressId}"><i class="fa fa-check"></i></a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>


