<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/admin/includes/header.jspf" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h5><i class="fa fa-user"></i> Chi tiết khách hàng</h5>
    <a href="${pageContext.request.contextPath}/admin?action=users" class="btn btn-secondary">
      <i class="fa fa-arrow-left"></i> Quay lại
    </a>
  </div>
  
  <c:choose>
    <c:when test="${empty userDetail}">
      <div class="alert alert-danger">Không tìm thấy khách hàng</div>
    </c:when>
    <c:otherwise>
      
      <!-- Thông tin cá nhân -->
      <div class="card mb-3">
        <div class="card-header bg-primary text-white">
          <h6 class="mb-0"><i class="fa fa-info-circle"></i> Thông tin cá nhân</h6>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-2 text-center">
              <c:choose>
                <c:when test="${not empty userDetail.avatarUrl}">
                  <img src="${pageContext.request.contextPath}${userDetail.avatarUrl}" alt="Avatar" 
                       class="rounded-circle" width="32" height="32"
                       style="margin: 0px 0px 0px 0px;"
                       onerror="this.src='${pageContext.request.contextPath}/IMG/default-avatar.png'">
                </c:when>
                <c:otherwise>
                  <div class="rounded-circle bg-secondary text-white d-flex align-items-center justify-content-center" 
                       style="width: 32px; height: 32px; font-size: 0.875rem; margin: 0px 0px 0px 0px;">
                    ${fn:substring(userDetail.username, 0, 1)}
                  </div>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="col-md-10">
              <table class="table table-borderless">
                <tr>
                  <th width="150">ID:</th>
                  <td><strong>#${userDetail.user_id}</strong></td>
                </tr>
                <tr>
                  <th>Tên:</th>
                  <td>${userDetail.username}</td>
                </tr>
                <tr>
                  <th>Email:</th>
                  <td>${userDetail.email}</td>
                </tr>
                <c:if test="${not empty userDetail.phone}">
                  <tr>
                    <th>Số điện thoại:</th>
                    <td>${userDetail.phone}</td>
                  </tr>
                </c:if>
                <tr>
                  <th>Vai trò:</th>
                  <td>
                    <span class="badge ${userDetail.role == 'ADMIN' ? 'bg-danger' : 'bg-primary'}">
                      ${userDetail.role}
                    </span>
                  </td>
                </tr>
                <tr>
                  <th>Ngày tham gia:</th>
                  <td>${userDetail.date_create}</td>
                </tr>
              </table>
            </div>
          </div>
        </div>
      </div>
      
      <div class="row">
        <!-- Địa chỉ giao hàng -->
        <div class="col-md-6">
          <div class="card mb-3">
            <div class="card-header bg-info text-white">
              <h6 class="mb-0"><i class="fa fa-map-marker-alt"></i> Sổ địa chỉ (${fn:length(userAddresses)})</h6>
            </div>
            <div class="card-body">
              <c:choose>
                <c:when test="${empty userAddresses}">
                  <p class="text-muted">Chưa có địa chỉ nào</p>
                </c:when>
                <c:otherwise>
                  <div class="list-group">
                    <c:forEach var="address" items="${userAddresses}">
                      <div class="list-group-item">
                        <div class="d-flex justify-content-between align-items-start">
                          <div>
                            <strong>${address.fullName}</strong>
                            <c:if test="${address['default']}">
                              <span class="badge bg-primary ms-2">Mặc định</span>
                            </c:if>
                            <div class="mt-1 text-muted small">
                              <div><i class="fa fa-phone"></i> ${address.phone}</div>
                              <div>${address.address}, ${address.ward}, ${address.district}, ${address.city}</div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </c:forEach>
                  </div>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
        
        <!-- Voucher của user -->
        <div class="col-md-6">
          <div class="card mb-3">
            <div class="card-header bg-success text-white">
              <h6 class="mb-0"><i class="fa fa-ticket-alt"></i> Mã giảm giá (${fn:length(userVouchers)})</h6>
            </div>
            <div class="card-body">
              <c:choose>
                <c:when test="${empty userVouchers}">
                  <p class="text-muted">Chưa có voucher nào</p>
                </c:when>
                <c:otherwise>
                  <div class="list-group">
                    <c:forEach var="voucher" items="${userVouchers}">
                      <div class="list-group-item">
                        <div class="d-flex justify-content-between align-items-start">
                          <div>
                            <strong>${voucher.code}</strong> - ${voucher.name}
                            <div class="mt-1 text-muted small">
                              <div>
                                <c:choose>
                                  <c:when test="${voucher.type == 'PERCENTAGE'}">
                                    Giảm ${voucher.value}%
                                  </c:when>
                                  <c:otherwise>
                                    Giảm ${String.format("%,.0f", voucher.value)} đ
                                  </c:otherwise>
                                </c:choose>
                              </div>
                              <div>Số lần sử dụng còn lại: ${voucher.remainingUses}</div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </c:forEach>
                  </div>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Lịch sử đơn hàng -->
      <div class="card">
        <div class="card-header bg-warning text-dark">
          <h6 class="mb-0"><i class="fa fa-shopping-cart"></i> Lịch sử đơn hàng (${fn:length(userOrders)})</h6>
        </div>
        <div class="card-body">
          <c:choose>
            <c:when test="${empty userOrders}">
              <p class="text-muted">Chưa có đơn hàng nào</p>
            </c:when>
            <c:otherwise>
              <div class="table-responsive">
                <table class="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Ngày đặt</th>
                      <th>Tổng tiền</th>
                      <th>Thanh toán</th>
                      <th>Trạng thái</th>
                      <th>Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="order" items="${userOrders}">
                      <tr>
                        <td><strong>#${order.orderId}</strong></td>
                        <td>
                          <c:if test="${not empty order.orderDate}">
                            <c:set var="orderDateStr" value="${order.orderDate.toString()}" />
                            <c:choose>
                              <c:when test="${fn:length(orderDateStr) >= 16}">
                                ${fn:substring(orderDateStr, 0, 10)} ${fn:substring(orderDateStr, 11, 16)}
                              </c:when>
                              <c:otherwise>
                                ${orderDateStr}
                              </c:otherwise>
                            </c:choose>
                          </c:if>
                        </td>
                        <td>
                          <strong style="color:#f76c85;">
                            ${String.format("%,.0f", order.totalAmount)} đ
                          </strong>
                        </td>
                        <td>
                          <c:choose>
                            <c:when test="${order.paymentStatus == 'PAID'}">
                              <span class="badge bg-success">Đã thanh toán</span>
                            </c:when>
                            <c:otherwise>
                              <span class="badge bg-warning">Chưa thanh toán</span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <c:choose>
                            <c:when test="${order.orderStatus == 'COMPLETED'}">
                              <span class="badge bg-success">Đã hoàn thành</span>
                            </c:when>
                            <c:when test="${order.orderStatus == 'CANCELLED'}">
                              <span class="badge bg-danger">Đã hủy</span>
                            </c:when>
                            <c:when test="${order.orderStatus == 'SHIPPING'}">
                              <span class="badge bg-info">Đang giao</span>
                            </c:when>
                            <c:when test="${order.orderStatus == 'CONFIRMED'}">
                              <span class="badge bg-primary">Đã xác nhận</span>
                            </c:when>
                            <c:otherwise>
                              <span class="badge bg-secondary">${order.orderStatus}</span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <a href="${pageContext.request.contextPath}/admin?action=orderDetail&orderId=${order.orderId}&fullPage=true" 
                             class="btn btn-sm btn-outline-primary">
                            <i class="fa fa-eye"></i> Chi tiết
                          </a>
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
      
    </c:otherwise>
  </c:choose>
</div>

<%@ include file="/admin/includes/footer.jspf" %>

