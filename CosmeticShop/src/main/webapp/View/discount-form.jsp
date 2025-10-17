<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${empty discount ? 'Tạo mã giảm giá' : 'Sửa mã giảm giá'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</head>
<body>
<div class="container my-4" style="max-width: 720px;">
    <h3 class="mb-3">${empty discount ? 'Tạo mã giảm giá' : 'Sửa mã giảm giá'}</h3>
    <form method="post" action="${pageContext.request.contextPath}/discounts">
        <input type="hidden" name="action" value="${empty discount ? 'create' : 'update'}" />
        <c:if test="${not empty discount}">
            <input type="hidden" name="id" value="${discount.discountId}" />
        </c:if>

        <div class="mb-3">
            <label class="form-label">Code</label>
            <input type="text" class="form-control" name="code" value="${discount.code}" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Tên</label>
            <input type="text" class="form-control" name="name" value="${discount.name}" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Loại</label>
            <select class="form-select" name="type" required>
                <option value="PERCENTAGE" ${discount.type=='PERCENTAGE' ? 'selected' : ''}>PERCENTAGE</option>
                <option value="FIXED_AMOUNT" ${discount.type=='FIXED_AMOUNT' ? 'selected' : ''}>FIXED_AMOUNT</option>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Giá trị</label>
            <input type="number" step="0.01" class="form-control" name="value" value="${discount.value}" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Đơn tối thiểu</label>
            <input type="number" step="0.01" class="form-control" name="minOrder" value="${discount.minOrderAmount}" />
        </div>
        <div class="mb-3">
            <label class="form-label">Giảm tối đa</label>
            <input type="number" step="0.01" class="form-control" name="maxDiscount" value="${discount.maxDiscountAmount}" />
        </div>
        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Bắt đầu</label>
                <input type="datetime-local" class="form-control" name="start" value="${discount.startDate}" placeholder="2025-01-01T00:00" />
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Kết thúc</label>
                <input type="datetime-local" class="form-control" name="end" value="${discount.endDate}" placeholder="2025-12-31T23:59" />
            </div>
        </div>
        <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" name="active" id="activeCheck" ${discount.active ? 'checked' : ''} />
            <label class="form-check-label" for="activeCheck">Kích hoạt</label>
        </div>
        <div class="d-flex gap-2">
            <button class="btn btn-primary" type="submit">Lưu</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/discounts">Quay lại</a>
        </div>
    </form>
</div>
</body>
</html>


