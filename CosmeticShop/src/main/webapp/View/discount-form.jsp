<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty discount ? 'Tạo mã giảm giá' : 'Sửa mã giảm giá'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
      integrity="sha512-…"
      crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <!-- Nội dung form -->
    <div class="container my-4" style="max-width: 840px; min-height: 60vh;">
        <div class="card shadow-sm">
            <div class="card-header" style="background-color:#fdf1f4; border-color:#fbd0da;">
                <h5 class="mb-0" style="color:#f76c85;">${empty discount ? 'Tạo mã giảm giá' : 'Sửa mã giảm giá'}</h5>
            </div>
            <div class="card-body">
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
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Loại</label>
                            <select class="form-select" name="type" required>
                                <option value="PERCENTAGE" ${discount.type=='PERCENTAGE' ? 'selected' : ''}>PERCENTAGE</option>
                                <option value="FIXED_AMOUNT" ${discount.type=='FIXED_AMOUNT' ? 'selected' : ''}>FIXED_AMOUNT</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giá trị</label>
                            <input type="number" step="0.01" class="form-control" name="value" value="${discount.value}" required />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Đơn tối thiểu</label>
                            <input type="number" step="0.01" class="form-control" name="minOrder" value="${discount.minOrderAmount}" />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giảm tối đa</label>
                            <input type="number" step="0.01" class="form-control" name="maxDiscount" value="${discount.maxDiscountAmount}" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giới hạn lượt dùng (toàn hệ thống)</label>
                            <input type="number" min="0" class="form-control" name="usageLimit" value="${discount.usageLimit}" placeholder="VD: 100 (để trống nếu không giới hạn)" />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Mô tả</label>
                            <input type="text" class="form-control" name="description" value="${discount.description}" placeholder="Mô tả ngắn" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Điều kiện gán tự động</label>
                            <select class="form-select" name="conditionType" id="conditionTypeSelect">
                                <option value="">-- Không điều kiện --</option>
                                <option value="TOTAL_SPENT" ${discount.conditionType=='TOTAL_SPENT' ? 'selected' : ''}>Tổng chi tiêu ≥</option>
                                <option value="ORDER_COUNT" ${discount.conditionType=='ORDER_COUNT' ? 'selected' : ''}>Số đơn hàng ≥</option>
                                <option value="FIRST_ORDER" ${discount.conditionType=='FIRST_ORDER' ? 'selected' : ''}>Đơn hàng đầu tiên</option>
                                <option value="SPECIAL_EVENT" ${discount.conditionType=='SPECIAL_EVENT' ? 'selected' : ''}>Sự kiện đặc biệt</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3" id="conditionValueCol">
                            <label class="form-label">Giá trị điều kiện</label>
                            <input type="number" step="0.01" class="form-control" name="conditionValue" value="${discount.conditionValue}" placeholder="VD: 100000 hoặc 3" />
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Mô tả điều kiện</label>
                        <input type="text" class="form-control" name="conditionDescription" value="${discount.conditionDescription}" placeholder="VD: Đạt chi tiêu tích lũy 100k" />
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="autoAssignAll" id="autoAssignAllCheck" ${discount.autoAssignAll ? 'checked' : ''} />
                                <label class="form-check-label" for="autoAssignAllCheck">Tự gán cho tất cả user</label>
                            </div>
                            <small class="text-muted">Chỉ áp dụng khi bạn muốn phát mã hàng loạt. Nếu không, người dùng vẫn có thể nhập mã thủ công trong thời gian hiệu lực.</small>
                        </div>
                    </div>
                    <div class="row" id="assignDateRow">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Ngày gán</label>
                            <input type="datetime-local" class="form-control" name="assignDate" value="${discount.assignDate}" placeholder="2025-01-01T00:00" />
                        </div>
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
                        <button class="btn btn-primary" style="background-color:#f76c85; border-color:#f76c85;" type="submit">Lưu</button>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/discounts">Quay lại</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%@ include file="/View/includes/footer.jspf" %>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
    <script>
        (function() {
            function toggleConditionInputs() {
                var select = document.getElementById('conditionTypeSelect');
                var valueCol = document.getElementById('conditionValueCol');
                var valueInput = document.querySelector('input[name="conditionValue"]');
                if (!select || !valueCol || !valueInput) return;
                var hide = (select.value === 'FIRST_ORDER' || select.value === 'SPECIAL_EVENT');
                valueCol.style.display = hide ? 'none' : '';
                valueInput.disabled = hide;
                if (hide) { valueInput.value = ''; }
            }
            function toggleAssignDate() {
                var autoAll = document.getElementById('autoAssignAllCheck');
                var row = document.getElementById('assignDateRow');
                if (!autoAll || !row) return;
                var show = !!autoAll.checked;
                row.style.display = show ? '' : 'none';
                var input = row.querySelector('input[name="assignDate"]');
                if (input) input.disabled = !show;
                if (!show && input) input.value = '';
            }
            document.addEventListener('DOMContentLoaded', function() {
                var select = document.getElementById('conditionTypeSelect');
                if (select) {
                    select.addEventListener('change', toggleConditionInputs);
                    toggleConditionInputs();
                }
                var autoAll = document.getElementById('autoAssignAllCheck');
                if (autoAll) {
                    autoAll.addEventListener('change', toggleAssignDate);
                    toggleAssignDate();
                }
            });
        })();
    </script>
</body>
</html>


