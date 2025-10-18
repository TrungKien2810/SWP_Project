<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <title>Thanh toán</title>
    <style>
        .container-sm { max-width: 1000px; }
        .card { border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .highlight { color:#f76c85; }
    </style>
    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <div class="container-sm my-4">
        <h3 class="mb-3">Thanh toán</h3>

        <form method="post" action="${pageContext.request.contextPath}/checkout">
            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Địa chỉ giao hàng</h5>
                        <c:choose>
                            <c:when test="${empty addresses}">
                                <p>Bạn chưa có địa chỉ. <a href="${pageContext.request.contextPath}/shipping-address">Thêm địa chỉ</a></p>
                            </c:when>
                            <c:otherwise>
                                <select class="form-select" name="shipping_address_id" required>
                                    <c:forEach var="a" items="${addresses}">
                                        <option value="${a.addressId}" ${a['default'] ? 'selected' : ''}>
                                            ${a.fullName} - ${a.phone} | ${a.address}, ${a.ward}, ${a.district}, ${a.city}
                                        </option>
                                    </c:forEach>
                                </select>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Phương thức giao hàng</h5>
                        <c:forEach var="m" items="${methods}" varStatus="st">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="shipping_method_id" id="m_${m.methodId}" value="${m.methodId}" data-cost="${m.cost}" ${st.first ? 'checked' : ''} required>
                                <label class="form-check-label" for="m_${m.methodId}">
                                    ${m.name} - <span class="highlight">${m.cost}</span> VND (dự kiến ${m.estimatedDays} ngày)
                                </label>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Phương thức thanh toán</h5>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="payment_method" id="cod" value="COD" checked>
                            <label class="form-check-label" for="cod">Thanh toán khi nhận hàng (COD)</label>
                        </div>
                        <div class="form-check mt-2">
                            <input class="form-check-input" type="radio" name="payment_method" id="bank" value="BANK">
                            <label class="form-check-label" for="bank">Thanh toán qua ngân hàng</label>
                        </div>
                        <div class="form-text">Nếu chọn ngân hàng, bạn sẽ được chuyển đến cổng thanh toán.</div>
                        <input type="hidden" name="bank_code" id="bank_code">

                        <div id="bankList" class="mt-3" style="display:none;">
                            <label class="form-label mb-2">Chọn ngân hàng:</label>
                            <div class="d-flex flex-wrap gap-2">
                                <button type="button" class="btn btn-outline-secondary bank-btn" data-bank="VCB">Vietcombank (VCB)</button>
                                <button type="button" class="btn btn-outline-secondary bank-btn" data-bank="BIDV">BIDV</button>
                                <button type="button" class="btn btn-outline-secondary bank-btn" data-bank="ACB">ACB</button>
                                <button type="button" class="btn btn-outline-secondary bank-btn" data-bank="TCB">Techcombank (TCB)</button>
                                <button type="button" class="btn btn-outline-secondary bank-btn" data-bank="VPB">VPBank (VPB)</button>
                            </div>
                            <div class="form-text mt-2">Bạn có thể chọn ngân hàng trước khi bấm Đặt hàng.</div>
                        </div>
                    </div>

                    <div class="card p-3 mb-3">
                        <h5 class="mb-3">Ghi chú</h5>
                        <textarea class="form-control" name="notes" rows="3" placeholder="Ghi chú cho đơn hàng..."></textarea>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="card p-3">
                        <h5 class="mb-3">Đơn hàng của bạn</h5>
                        <div class="list-group mb-3">
                            <c:forEach var="it" items="${items}">
                                <div class="list-group-item d-flex align-items-center justify-content-between">
                                    <div class="d-flex align-items-center">
                                        <img src="${pageContext.request.contextPath}/${it.imageUrl}" alt="${it.productName}" style="width:56px;height:56px;object-fit:cover;border-radius:6px;margin-right:10px;">
                                        <div>
                                            <div class="fw-semibold">${it.productName}</div>
                                            <div class="text-muted">SL: ${it.quantity}</div>
                                        </div>
                                    </div>
                                    <div>${it.price * it.quantity}</div>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>Tạm tính:</span>
                            <strong>${itemsTotal}</strong>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>Phí vận chuyển:</span>
                            <strong id="shippingCost">0</strong>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <span>Tổng cộng:</span>
                            <strong id="grandTotal">${itemsTotal}</strong>
                        </div>
                        <div class="mt-3">
                            <button class="btn btn-primary w-100" style="background-color:#f76c85;border-color:#f76c85;">Đặt hàng</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <script>
        (function(){
            var itemsTotal = Number('${itemsTotal}'.replace(',', '')) || 0;
            function updateTotals(){
                var checked = document.querySelector('input[name="shipping_method_id"]:checked');
                var cost = checked ? Number(checked.getAttribute('data-cost')) : 0;
                if (isNaN(cost)) cost = 0;
                document.getElementById('shippingCost').textContent = cost;
                document.getElementById('grandTotal').textContent = (itemsTotal + cost);
            }
            document.addEventListener('change', function(e){
                if (e.target && e.target.name === 'shipping_method_id') updateTotals();
                if (e.target && e.target.name === 'payment_method') {
                    var isBank = e.target.value === 'BANK';
                    document.getElementById('bankList').style.display = isBank ? 'block' : 'none';
                }
            });
            document.addEventListener('DOMContentLoaded', function(){
                var radios = document.querySelectorAll('input[name="shipping_method_id"]');
                var anyChecked = document.querySelector('input[name="shipping_method_id"]:checked');
                if (!anyChecked && radios.length > 0){
                    radios[0].checked = true;
                }
                updateTotals();

                // bank selection handlers
                var bankButtons = document.querySelectorAll('.bank-btn');
                bankButtons.forEach(function(btn){
                    btn.addEventListener('click', function(){
                        var code = btn.getAttribute('data-bank');
                        document.getElementById('bank_code').value = code;
                        bankButtons.forEach(function(b){ b.classList.remove('active'); });
                        btn.classList.add('active');
                    });
                });
            });
        })();
    </script>
</body>
</html>


