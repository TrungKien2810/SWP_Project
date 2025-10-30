<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý mã giảm giá</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Css/home.css">
    <link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
      integrity="sha512-…"
      crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <%@ include file="/View/includes/header.jspf" %>

    <!-- Nội dung trang Discount Manager -->
    <div class="container my-4" style="max-width: 1100px; min-height: 60vh;">
        <div class="card shadow-sm">
            <div class="card-header d-flex justify-content-between align-items-center" style="background-color:#fdf1f4; border-color:#fbd0da;">
                <h5 class="mb-0" style="color:#f76c85;">Quản lý mã giảm giá</h5>
                <a class="btn btn-primary" style="background-color:#f76c85; border-color:#f76c85;" href="${pageContext.request.contextPath}/discounts?action=new">
                    <i class="fa-solid fa-plus me-1"></i> Tạo mã mới
                </a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty discounts}">
                        <div class="text-center py-5">
                            <i class="fa-solid fa-ticket-simple fa-2xl mb-3" style="color:#f76c85;"></i>
                            <h6 class="mb-2">Chưa có mã giảm giá</h6>
                            <p class="text-muted mb-3">Hãy tạo mã đầu tiên để bắt đầu chiến dịch khuyến mãi.</p>
                            <a class="btn btn-primary" style="background-color:#f76c85; border-color:#f76c85;" href="${pageContext.request.contextPath}/discounts?action=new">
                                <i class="fa-solid fa-plus me-1"></i> Tạo mã mới
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Code</th>
                                        <th>Tên</th>
                                        <th>Loại</th>
                                        <th>Giá trị</th>
                                        <th>Bắt đầu</th>
                                        <th>Kết thúc</th>
                                        <th>Trạng thái</th>
                                        <th class="text-end">Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="d" items="${discounts}">
                                        <tr>
                                            <td>${d.discountId}</td>
                                            <td><span class="fw-semibold">${d.code}</span></td>
                                            <td>${d.name}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${d.type == 'PERCENTAGE'}">
                                                        <span class="badge bg-info text-dark">PERCENTAGE</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">FIXED_AMOUNT</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${d.value}</td>
                                            <td>${d.startDate}</td>
                                            <td>${d.endDate}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${d.active}"><span class="badge bg-success">Đang kích hoạt</span></c:when>
                                                    <c:otherwise><span class="badge bg-danger">Tắt</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-end">
                                                <div class="d-inline-flex gap-2 align-items-center justify-content-end">
                                                    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/discounts?action=edit&id=${d.discountId}">
                                                        <i class="fa-solid fa-pen"></i> Sửa
                                                    </a>
                                                    <form action="${pageContext.request.contextPath}/discounts" method="post" onsubmit="return confirm('Xóa mã này?');" style="margin:0;">
                                                        <input type="hidden" name="action" value="delete" />
                                                        <input type="hidden" name="id" value="${d.discountId}" />
                                                        <button class="btn btn-sm btn-outline-danger" type="submit">
                                                            <i class="fa-solid fa-trash"></i> Xóa
                                                        </button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <%@ include file="/View/includes/footer.jspf" %>

    <script src="${pageContext.request.contextPath}/Js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Js/home.js"></script>
</body>
</html>


