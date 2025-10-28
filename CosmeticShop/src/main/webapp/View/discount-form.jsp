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
                <!-- Hiển thị thông báo lỗi -->
                <c:if test="${not empty requestScope.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        ${requestScope.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <form method="post" action="${pageContext.request.contextPath}/discounts">
                    <input type="hidden" name="action" value="${empty discount ? 'create' : 'update'}" />
                    <c:if test="${not empty discount}">
                        <input type="hidden" name="id" value="${discount.discountId}" />
                    </c:if>

                    <div class="mb-3">
                        <label class="form-label">Code</label>
                        <input type="text" class="form-control" name="code" value="${not empty param.code ? param.code : discount.code}" required />
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Tên</label>
                        <input type="text" class="form-control" name="name" value="${not empty param.name ? param.name : discount.name}" required />
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Loại</label>
                            <select class="form-select" name="type" required>
                                <option value="PERCENTAGE" ${(not empty param.type && param.type=='PERCENTAGE') || (empty param.type && discount.type=='PERCENTAGE') ? 'selected' : ''}>PERCENTAGE</option>
                                <option value="FIXED_AMOUNT" ${(not empty param.type && param.type=='FIXED_AMOUNT') || (empty param.type && discount.type=='FIXED_AMOUNT') ? 'selected' : ''}>FIXED_AMOUNT</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giá trị</label>
                            <input type="number" step="0.01" class="form-control" name="value" value="${not empty param.value ? param.value : discount.value}" required />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Đơn tối thiểu</label>
                            <input type="number" step="0.01" class="form-control" name="minOrder" value="${not empty param.minOrder ? param.minOrder : discount.minOrderAmount}" />
                        </div>
                        <div class="col-md-6 mb-3" id="maxDiscountCol">
                            <label class="form-label">Giảm tối đa</label>
                            <input type="number" step="0.01" class="form-control" name="maxDiscount" value="${not empty param.maxDiscount ? param.maxDiscount : discount.maxDiscountAmount}" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giới hạn lượt dùng (toàn hệ thống)</label>
                            <input type="number" min="0" class="form-control" name="usageLimit" value="${not empty param.usageLimit ? param.usageLimit : discount.usageLimit}" placeholder="VD: 100 (để trống nếu không giới hạn)" />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Mô tả</label>
                            <input type="text" class="form-control" name="description" value="${not empty param.description ? param.description : discount.description}" placeholder="Mô tả ngắn" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Kiểu gán</label>
                            <select class="form-select" name="assignmentMode" id="assignmentModeSelect">
                                <option value="MANUAL"
                                  ${ (not empty param.assignmentMode && param.assignmentMode=='MANUAL')
                                     || (empty param.assignmentMode && (empty discount || (discount.autoAssignAll ne true and empty discount.conditionType))) ? 'selected' : '' }>
                                  MANUAL (Mã công khai - người dùng tự nhập khi thanh toán)
                                </option>
                                <option value="AUTO_CONDITION"
                                  ${ (not empty param.assignmentMode && param.assignmentMode=='AUTO_CONDITION')
                                     || (empty param.assignmentMode && (not empty discount and (discount.autoAssignAll ne true and not empty discount.conditionType))) ? 'selected' : '' }>
                                  AUTO_CONDITION (Tự gán theo điều kiện)
                                </option>
                                <option value="AUTO_ALL"
                                  ${ (not empty param.assignmentMode && param.assignmentMode=='AUTO_ALL')
                                     || (empty param.assignmentMode && (not empty discount and (discount.autoAssignAll eq true))) ? 'selected' : '' }>
                                  AUTO_ALL (Gán cho tất cả)
                                </option>
                            </select>
                            <small class="text-muted">
                                <strong>MANUAL:</strong> Mã công khai (VD: CHAOHE20), user tự nhập khi thanh toán.<br>
                                <strong>AUTO_CONDITION:</strong> Tự động gán cho user thỏa điều kiện.<br>
                                <strong>AUTO_ALL:</strong> Tự động gán cho tất cả user.
                            </small>
                            <input type="hidden" name="autoAssignAll" id="autoAssignAllHidden" value="${ (not empty param.assignmentMode && param.assignmentMode=='AUTO_ALL') || (empty param.assignmentMode && discount.autoAssignAll) ? 'on' : '' }" />
                        </div>
                    </div>
                    <div class="row" id="conditionSection">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Điều kiện gán tự động</label>
                            <select class="form-select" name="conditionType" id="conditionTypeSelect">
                                <option value="TOTAL_SPENT" ${(not empty param.conditionType && param.conditionType=='TOTAL_SPENT') || (empty param.conditionType && discount.conditionType=='TOTAL_SPENT') ? 'selected' : ''}>Tổng chi tiêu ≥</option>
                                <option value="ORDER_COUNT" ${(not empty param.conditionType && param.conditionType=='ORDER_COUNT') || (empty param.conditionType && discount.conditionType=='ORDER_COUNT') ? 'selected' : ''}>Số đơn hàng ≥</option>
                                <option value="FIRST_ORDER" ${(not empty param.conditionType && param.conditionType=='FIRST_ORDER') || (empty param.conditionType && discount.conditionType=='FIRST_ORDER') ? 'selected' : ''}>Đơn hàng đầu tiên</option>
                                <option value="SPECIAL_EVENT" ${(not empty param.conditionType && param.conditionType=='SPECIAL_EVENT') || (empty param.conditionType && discount.conditionType=='SPECIAL_EVENT') ? 'selected' : ''}>Sự kiện đặc biệt</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3" id="conditionValueCol">
                            <label class="form-label">Giá trị điều kiện</label>
                            <input type="number" step="0.01" class="form-control" name="conditionValue" value="${not empty param.conditionValue ? param.conditionValue : discount.conditionValue}" placeholder="VD: 100000 hoặc 3" />
                        </div>
                        <div class="mb-3" id="conditionDescriptionRow">
                            <label class="form-label">Mô tả điều kiện</label>
                            <input type="text" class="form-control" name="conditionDescription" value="${not empty param.conditionDescription ? param.conditionDescription : discount.conditionDescription}" placeholder="VD: Đạt chi tiêu tích lũy 100k" />
                        </div>
                    </div>
                    <div class="row" id="assignDateRow">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Ngày gán</label>
                            <input type="datetime-local" class="form-control" name="assignDate" value="${not empty param.assignDate ? param.assignDate : discount.assignDate}" placeholder="2025-01-01T00:00" />
                        </div>
                    </div>
                    <div class="row" id="normalDateRow">
                        <div class="col-md-6 mb-3" id="startCol">
                            <label class="form-label">Bắt đầu <span class="text-danger">*</span></label>
                            <input type="datetime-local" class="form-control" name="start" value="${not empty param.start ? param.start : discount.startDate}" placeholder="2025-01-01T00:00" required />
                        </div>
                        <div class="col-md-6 mb-3" id="endCol">
                            <label class="form-label">Kết thúc <span class="text-danger">*</span></label>
                            <input type="datetime-local" class="form-control" name="end" value="${not empty param.end ? param.end : discount.endDate}" placeholder="2025-12-31T23:59" required />
                        </div>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" name="active" id="activeCheck" ${(not empty param.active && param.active == 'on') || (empty param.active && discount.active) ? 'checked' : ''} />
                        <label class="form-check-label" for="activeCheck">Kích hoạt</label>
                    </div>
                    <div class="d-flex gap-2">
                        <button class="btn btn-primary" style="background-color:#f76c85; border-color:#f76c85;" type="submit">Lưu</button>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin?action=discounts">Quay lại</a>
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
            function qs(sel){ return document.querySelector(sel); }
            function qsa(sel){ return Array.prototype.slice.call(document.querySelectorAll(sel)); }

            function toggleConditionInputs() {
                var mode = qs('#assignmentModeSelect') ? qs('#assignmentModeSelect').value : 'MANUAL';
                var select = document.getElementById('conditionTypeSelect');
                var valueCol = document.getElementById('conditionValueCol');
                var valueInput = document.querySelector('input[name="conditionValue"]');
                if (!select || !valueCol || !valueInput) return;
                var hideValue = (select.value === 'FIRST_ORDER' || select.value === 'SPECIAL_EVENT');
                var condEnabled = (mode === 'AUTO_CONDITION');
                // show/hide value column depending on both mode and type
                valueCol.style.display = (!condEnabled || hideValue) ? 'none' : '';
                valueInput.disabled = (!condEnabled || hideValue);
                if (!condEnabled || hideValue) { valueInput.value = ''; }
            }

            function toggleByMode() {
                var modeSel = qs('#assignmentModeSelect');
                var mode = modeSel ? modeSel.value : 'MANUAL';
                var condSection = qs('#conditionSection');
                var condDescRow = qs('#conditionDescriptionRow');
                var assignRow = qs('#assignDateRow');
                var startInput = qs('input[name="start"]');
                var endInput = qs('input[name="end"]');
                var assignInput = assignRow ? assignRow.querySelector('input[name="assignDate"]') : null;
                var autoAssignAllHidden = qs('#autoAssignAllHidden');

                // Normal dates are always visible and required
                if (startInput) { startInput.disabled = false; startInput.setAttribute('required', 'required'); }
                if (endInput) { endInput.disabled = false; endInput.setAttribute('required', 'required'); }

                // Condition section only for AUTO_CONDITION
                if (condSection) {
                    var showCond = (mode === 'AUTO_CONDITION');
                    condSection.style.display = showCond ? '' : 'none';
                    qsa('#conditionSection select, #conditionSection input').forEach(function(el){
                        el.disabled = !showCond;
                        if (!showCond && el.name === 'conditionValue') { el.value = ''; }
                    });
                }
                if (condDescRow) { condDescRow.style.display = (mode === 'AUTO_CONDITION') ? '' : 'none'; }

                // Assign date row only for AUTO_ALL
                if (assignRow) {
                    var showAssign = (mode === 'AUTO_ALL');
                    assignRow.style.display = showAssign ? '' : 'none';
                    if (assignInput) {
                        assignInput.disabled = !showAssign;
                        if (!showAssign) assignInput.value = '';
                    }
                }

                // Hidden autoAssignAll value for server compatibility
                if (autoAssignAllHidden) { autoAssignAllHidden.value = (mode === 'AUTO_ALL') ? 'on' : ''; }

                // Update condition value visibility based on type selection
                toggleConditionInputs();
            }

            function toggleMaxDiscount() {
                var typeSel = qs('select[name="type"]');
                var col = document.getElementById('maxDiscountCol');
                if (!typeSel || !col) return;
                var hide = (typeSel.value === 'FIXED_AMOUNT');
                col.style.display = hide ? 'none' : '';
                var input = col.querySelector('input[name="maxDiscount"]');
                if (input) { input.disabled = hide; if (hide) input.value = ''; }
            }

            document.addEventListener('DOMContentLoaded', function() {
                var modeSel = document.getElementById('assignmentModeSelect');
                if (modeSel) { modeSel.addEventListener('change', toggleByMode); }
                var condTypeSel = document.getElementById('conditionTypeSelect');
                if (condTypeSel) { condTypeSel.addEventListener('change', toggleConditionInputs); }
                var typeSel = document.querySelector('select[name="type"]');
                if (typeSel) { typeSel.addEventListener('change', toggleMaxDiscount); }
                toggleByMode();
                toggleMaxDiscount();
            });
        })();
    </script>
</body>
</html>


