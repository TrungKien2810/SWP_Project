# 🔄 CosmeticShop - Sơ Đồ Use Case và Luồng Dữ Liệu

## MỤC LỤC
1. [Use Case Diagrams](#1-use-case-diagrams)
2. [Detailed Use Cases](#2-detailed-use-cases)
3. [Data Flow Diagrams](#3-data-flow-diagrams)
4. [User Stories](#4-user-stories)

---

## 1. USE CASE DIAGRAMS

### 1.1 Use Case Tổng Quan Hệ Thống

```
┌────────────────────────────────────────────────────────────────────┐
│                        COSMETIC SHOP SYSTEM                         │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌────────────────┐         ┌────────────────┐                   │
│  │   Guest User   │         │  Authenticated │                   │
│  │                │         │     User       │                   │
│  └────────┬───────┘         └────────┬───────┘                   │
│           │                          │                            │
│           │ Browse Products          │ Manage Profile            │
│           │ View Product Details     │ Manage Addresses          │
│           │ Search Products          │ View Order History        │
│           │                         │ Manage Cart               │
│           │                          │ Checkout                  │
│           │                          │ Apply Discount            │
│           │                          │ Payment                   │
│           │                          │ Track Orders              │
│           │                          │ Request Password Reset    │
│           │                          │                           │
│           └───────────┬──────────────┘                           │
│                       │                                          │
│                       │ Register/Login                           │
│                       │                                         │
│                  ┌────▼───────────────────┐                      │
│                  │      Admin User        │                      │
│                  │                        │                      │
│                  │ Manage Products        │                      │
│                  │ Manage Categories      │                      │
│                  │ Manage Orders          │                      │
│                  │ Manage Users           │                      │
│                  │ Manage Discounts       │                      │
│                  │ View Reports           │                      │
│                  │ Configure Settings     │                      │
│                  │                        │                      │
│                  └────────────────────────┘                      │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### 1.2 Use Case - User (Người Dùng)

```
┌────────────────────────────────────────────────────────────────┐
│                          USER ACTOR                             │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌─────────────┐                                               │
│  │  Register   │◄── Create account                             │
│  └─────────────┘                                               │
│                                                                │
│  ┌─────────────┐                                               │
│  │    Login    │◄── Authenticate                               │
│  └─────────────┘                                               │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Account        │                                    │
│  │  - View Profile        │                                    │
│  │  - Update Info         │                                    │
│  │  - Change Password     │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Browse Products       │                                    │
│  │  - View Product List   │                                    │
│  │  - View Details        │                                    │
│  │  - Search              │                                    │
│  │  - Filter by Category  │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Cart           │                                    │
│  │  - Add to Cart         │                                    │
│  │  - Update Quantity     │                                    │
│  │  - Remove from Cart    │                                    │
│  │  - View Cart           │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Addresses      │                                    │
│  │  - Add Address         │                                    │
│  │  - Edit Address        │                                    │
│  │  - Delete Address      │                                    │
│  │  - Set Default         │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Checkout              │                                    │
│  │  - Select Items        │                                    │
│  │  - Select Address      │                                    │
│  │  - Select Shipping     │                                    │
│  │  - Apply Discount      │                                    │
│  │  - Review Order        │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Payment               │                                    │
│  │  - VNPay               │                                    │
│  │  - Bank Transfer       │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Orders         │                                    │
│  │  - View Orders         │                                    │
│  │  - View Order Details  │                                    │
│  │  - Track Order         │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Discounts      │                                    │
│  │  - View Available      │                                    │
│  │  - Apply to Order      │                                    │
│  └────────────────────────┘                                    │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### 1.3 Use Case - Admin (Quản Trị Viên)

```
┌────────────────────────────────────────────────────────────────┐
│                         ADMIN ACTOR                             │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Products       │                                    │
│  │  - Create Product      │                                    │
│  │  - Update Product      │                                    │
│  │  - Delete Product      │                                    │
│  │  - Upload Images       │                                    │
│  │  - Search Products     │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Categories     │                                    │
│  │  - Create Category     │                                    │
│  │  - Update Category     │                                    │
│  │  - Delete Category     │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Orders         │                                    │
│  │  - View All Orders     │                                    │
│  │  - Filter Orders       │                                    │
│  │  - Update Status       │                                    │
│  │  - View Details        │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Users          │                                    │
│  │  - View Users          │                                    │
│  │  - Change Role         │                                    │
│  │  - Block/Unblock User  │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  Manage Discounts      │                                    │
│  │  - Create Discount     │                                    │
│  │  - Update Discount     │                                    │
│  │  - Delete Discount     │                                    │
│  │  - Set Auto-Assign     │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  View Dashboard        │                                    │
│  │  - Sales Statistics    │                                    │
│  │  - Order Statistics    │                                    │
│  │  - User Statistics     │                                    │
│  └────────────────────────┘                                    │
│                                                                │
│  ┌────────────────────────┐                                    │
│  │  View Reports          │                                    │
│  │  - Sales Report        │                                    │
│  │  - Product Report      │                                    │
│  │  - User Report         │                                    │
│  └────────────────────────┘                                    │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### 1.4 Use Case - Hệ Thống (System Actors)

```
┌─────────────────────────────────────────────────────────────────┐
│                       SYSTEM ACTORS                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────┐                                      │
│  │  Payment Gateway     │                                      │
│  │  (VNPay)             │                                      │
│  │  - Process Payment   │                                      │
│  │  - Return Response   │                                      │
│  │  - IPN Callback      │                                      │
│  └──────────────────────┘                                      │
│                                                                 │
│  ┌──────────────────────┐                                      │
│  │  Email Service       │                                      │
│  │  (SMTP)              │                                      │
│  │  - Send Password     │                                      │
│  │    Reset Email       │                                      │
│  │  - Send Notifications│                                      │
│  └──────────────────────┘                                      │
│                                                                 │
│  ┌──────────────────────┐                                      │
│  │  Database            │                                      │
│  │  (SQL Server)        │                                      │
│  │  - Store Data        │                                      │
│  │  - Execute Queries   │                                      │
│  │  - Run Procedures    │                                      │
│  │  - Trigger Events    │                                      │
│  └──────────────────────┘                                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. DETAILED USE CASES

### 2.1 UC-001: User Registration

**Actor**: Guest User  
**Precondition**: User chưa có tài khoản  
**Main Flow**:
1. User truy cập trang đăng ký
2. User nhập thông tin (email, username, password)
3. Hệ thống validate thông tin
4. Hệ thống kiểm tra email đã tồn tại chưa
5. Nếu chưa tồn tại → Tạo tài khoản mới
6. User được tạo với role = "USER"
7. Redirect đến trang đăng nhập
8. Use case kết thúc

**Alternative Flow**:
- 4a. Email đã tồn tại → Hiển thị lỗi, yêu cầu dùng email khác
- 3a. Validation fail → Hiển thị lỗi cụ thể

**Postcondition**: Tài khoản mới được tạo trong database

---

### 2.2 UC-002: User Login

**Actor**: User  
**Precondition**: User đã có tài khoản  
**Main Flow**:
1. User truy cập trang đăng nhập
2. User nhập email và password
3. Hệ thống validate credentials
4. Tạo session cho user
5. Lưu user object vào session
6. Redirect đến trang chủ
7. Use case kết thúc

**Alternative Flow**:
- 3a. Credentials sai → Hiển thị lỗi "Email hoặc mật khẩu không đúng"
- 4a. Session timeout (30 min) → Yêu cầu đăng nhập lại

**Postcondition**: User đã đăng nhập, session được tạo

---

### 2.3 UC-003: Browse Products

**Actor**: User, Guest  
**Precondition**: Không cần đăng nhập  
**Main Flow**:
1. User truy cập trang sản phẩm
2. Hệ thống load danh sách sản phẩm từ database
3. Hiển thị danh sách sản phẩm với ảnh, tên, giá
4. User có thể click vào sản phẩm để xem chi tiết
5. User có thể search hoặc filter
6. Use case kết thúc

**Alternative Flow**:
- 4a. Click sản phẩm → Redirect đến trang chi tiết (UC-004)
- 5a. Search → Load kết quả tìm kiếm
- 5b. Filter by category → Load sản phẩm theo danh mục

**Postcondition**: User xem được danh sách sản phẩm

---

### 2.4 UC-004: View Product Details

**Actor**: User, Guest  
**Precondition**: User đã chọn một sản phẩm  
**Main Flow**:
1. User click vào sản phẩm
2. Hệ thống load chi tiết sản phẩm (name, price, description, stock)
3. Hệ thống load tất cả ảnh của sản phẩm
4. Hiển thị thông tin chi tiết với gallery ảnh
5. User có thể thêm vào giỏ hàng
6. Use case kết thúc

**Alternative Flow**:
- 5a. Thêm vào giỏ → Thực hiện UC-005
- 3a. Sản phẩm hết hàng → Hiển thị "Out of Stock"

**Postcondition**: User xem được chi tiết sản phẩm

---

### 2.5 UC-005: Add to Cart

**Actor**: Authenticated User  
**Precondition**: User đã đăng nhập, sản phẩm còn tồn kho  
**Main Flow**:
1. User click "Thêm vào giỏ hàng" tại trang chi tiết
2. Hệ thống lấy cart của user (hoặc tạo mới nếu chưa có)
3. Kiểm tra sản phẩm đã có trong cart chưa
4. Nếu đã có → Tăng quantity
5. Nếu chưa có → Thêm item mới
6. Hệ thống kiểm tra số lượng không vượt quá stock
7. Lưu vào database
8. Hiển thị thông báo thành công
9. Use case kết thúc

**Alternative Flow**:
- 6a. Quantity > stock → Hiển thị lỗi, giới hạn số lượng
- 1a. Chưa đăng nhập → Redirect đến trang đăng nhập

**Postcondition**: Sản phẩm được thêm vào giỏ hàng

---

### 2.6 UC-006: Checkout Process

**Actor**: Authenticated User  
**Precondition**: User có ít nhất 1 item trong giỏ hàng  
**Main Flow**:
1. User click "Thanh toán" từ giỏ hàng
2. Hệ thống load các items đã chọn trong cart
3. Hệ thống load địa chỉ giao hàng của user
4. Hệ thống load danh sách phương thức vận chuyển
5. User chọn/điền địa chỉ giao hàng
6. User chọn phương thức vận chuyển
7. Hệ thống tính tổng tiền (subtotal + shipping - discount)
8. User chọn mã giảm giá (optional)
9. User xem tổng kết và xác nhận
10. Click "Đặt hàng"
11. Hệ thống tạo đơn hàng và chi tiết
ー. Use case kết thúc

**Alternative Flow**:
- 3a. Chưa có địa chỉ → Yêu cầu thêm địa chỉ mới
- 8a. Chọn mã giảm giá → Validate và apply (UC-009)
- 10a. Có sản phẩm hết hàng → Hiển thị lỗi, cập nhật giỏ hàng

**Postcondition**: Đơn hàng được tạo với status = "PENDING"

---

### 2.7 UC-007: Payment via VNPay

**Actor**: User, VNPay Gateway  
**Precondition**: Đơn hàng đã được tạo  
**Main Flow**:
1. User chọn "Thanh toán VNPay" tại checkout
2. Hệ thống gọi `VnPayCreateServlet`
3. Hệ thống tạo payment request với thông tin đơn hàng
4. Hệ thống ký tên (signature) cho war tranh request
5. Redirect user đến trang thanh toán VNPay
6. User thanh toán trên VNPay
7. VNPay xử lý thanh toán
8. VNPay redirect về `/payment/vnpay/return`
9. Hệ thống nhận và verify signature
10. Hệ thống kiểm tra payment status
11. Nếu thành công → Update order status = "PAID"
12. Redirect đến trang xác nhận đơn hàng
13. Use case kết thúc

**Alternative Flow**:
- 11a. Thanh toán thất bại → Hiển thị lỗi, giữ đơn hàng ở trạng thái PENDING
- 9a. Signature không hợp lệ → Từ chối giao dịch

**Postcondition**: Đơn hàng được thanh toán và cập nhật trong database

---

### 2.8 UC-008: View Order History

**Actor**: Authenticated User  
**Precondition**: User đã đăng nhập  
**Main Flow**:
1. User click "Lịch sử đơn hàng"
2. Hệ thống truy vấn tất cả đơn hàng của user
3. Hiển thị danh sách đơn hàng với:
   - Order ID, Ngày đặt, Tổng tiền
   - Status, Payment status
   - Ảnh sản phẩm đầu tiên
4. User có thể click để xem chi tiết
5. Use case kết thúc

**Alternative Flow**:
- 4a. Click chi tiết → Hiển thị đầy đủ thông tin đơn hàng

**Postcondition**: User xem được lịch sử đơn hàng của lucky

---

### 2.9 UC-009: Apply Discount

**Actor**: Authenticated User  
**Precondition**: User đang ở trang checkout  
**Main Flow**:
1. User nhập mã giảm giá
2. Click "Áp dụng"
3. Hệ thống validate mã giảm giá:
   - Mã có tồn tại không?
   - Mã còn hiệu lực không?
   - User đã đủ điều kiện chưa?
   - Đã đạt minimum order chưa?
4. Nếu hợp lệ → Tính discount amount
5. Áp dụng discount vào tổng tiền
6. Hiển thị discount info và tổng mới
7. Use case kết thúc

**Alternative Flow**:
- 3a. Mã không hợp lệ → Hiển thị lỗi cụ thể
- 4a. Discount type = PERCENTAGE → Tính theo %
- 4b. Discount type = FIXED_AMOUNT → Trừ thẳng

**Postcondition**: Mã giảm giá được áp dụng vào đơn hàng

---

### 2.10 UC-010: Admin Manage Products

**Actor**: Admin  
**Precondition**: Admin đã đăng nhập  
**Main Flow**:
1. Admin truy cập "Quản lý sản phẩm"
2. Hệ thống hiển thị danh sách sản phẩm
3. Admin có thể:
   - Tạo sản phẩm mới (nhập thông tin, upload ảnh)
   - Sửa sản phẩm
   - Xóa sản phẩm
   - Tìm kiếm sản phẩm
4. Admin submit form
5. Hệ thống validate và lưu vào database
6. Cập nhật hiển thị
7. Use case kết thúc

**Alternative Flow**:
- 3b. Upload ảnh → Lưu vào IMG folder và database
- 5a. Validation fail → Hiển thị lỗi

**Postcondition**: Sản phẩm được cập nhật trong database

---

### 2.11 UC-011: Admin Manage Orders

**Actor**: Admin  
**Precondition**: Admin đã đăng nhập  
**Main Flow**:
1. Admin truy cập "Quản lý đơn hàng"
2. Hệ thống load tất cả đơn hàng
3. Admin có thể filter theo:
   - Status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
   - Date (Today, Date Range)
   - Customer name
4. Admin click vào đơn hàng để xem chi tiết
5. Admin cập nhật order status
6. Hệ thống cập nhật database
7. Use case kết thúc

**Alternative Flow**:
- 5a. Cập nhật status = SHIPPED → Nhập tracking number

**Postcondition**: Trạng thái đơn hàng được cập nhật

---

### 2.12 UC-012: Auto-Assign Vouchers

**Actor**: System (Trigger)  
**Precondition**: User hoàn thành đơn hàng  
**Main Flow**:
1. Order status chuyển thành "COMPLETED"
2. Trigger `tr_OrderCreated_AssignVouchers` được kích hoạt
3. Gọi stored procedure `sp_CheckAndAssignVouchers`
4. System kiểm tra các điều kiện voucher:
   - TOTAL_SPENT: Tổng tiền đã chi >= giá trị
   - ORDER_COUNT: Số đơn hàng >= giá trị
   - FIRST_ORDER: Đây là đơn hàng đầu tiên
5. Nếu đủ điều kiện → Gán voucher cho user
6. User nhận được voucher trong "My Discounts"
7. Use case kết thúc

**Postcondition**: User có voucher mới (nếu đủ điều kiện)

---

## 3. DATA FLOW DIAGRAMS

### 3.1 DFD Level 0 - Context Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        SYSTEM BOUNDARY                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────┐        Products        ┌──────────────┐         │
│  │  User    │◄───────────────────────│ CosmeticShop │         │
│  └────┬─────┘                        │   System     │         │
│       │                               └──────┬───────┘         │
│       │              Orders                  │                 │
│       ├──────────────────────────────────────┤                 │
│       │                                       │                 │
│       │              Payment Requests         │                 │
│       ├──────────────────────────────────────┤                 │
│       │                                       │                 │
│       │              Admin Requests           │                 │
│       └──────────────────────────────────────┘                 │
│                                              │                  │
│                           ┌──────────────────┼──────────────┐   │
│                           │                  │              │   │
│                    ┌──────▼───────┐  ┌──────▼──────┐ ┌─────▼───────┐
│                    │SQL Server    │  │ VNPay       │ │ Email       │
│                    │Database      │  │ Gateway     │ │ Service     │
│                    └──────────────┘  └─────────────┘ └──────────────┘
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 DFD Level 1 - Main Processes

```
┌─────────────────────────────────────────────────────────────────┐
│                        COSMETIC SHOP SYSTEM                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────┐                                           │
│  │ 1.0 User         │  ┌──────────────────┐                   │
│  │    Management    │  │ 2.0 Product      │                   │
│  │                  │  │    Browsing      │                   │
│  └────────┬─────────┘  └────────┬─────────┘                   │
│           │                     │                              │
│           │                     │                              │
│  ┌────────▼─────────┐  ┌────────▼─────────┐                   │
│  │ 1.1 Login        │  │ 2.1 View Products│                   │
│  │ 1.2 Register     │  │ 2.2 Search       │                   │
│  │ 1.3 Update Profile│  │ 2.3 View Details│                   │
│  └────────┬─────────┘  └────────┬─────────┘                   │
│           │                     │                              │
│           │                     │                              │
│  ┌────────┴─────────────────────┴─────────┐                   │
│  │ 3.0 Shopping Cart Management           │                   │
│  │ 3.1 Add to Cart                        │                   │
│  │ 3.2 Update Quantity                    │                   │
│  │ 3.3 Remove from Cart                   │                   │
│  └────────┬───────────────────────────────┘                   │
│           │                                                    │
│           │                                                    │
│  ┌────────▼─────────┐                                          │
│  │ 4.0 Checkout     │  ┌──────────────────┐                   │
│  │ 4.1 Select Items │  │ 5.0 Payment      │                   │
│  │ 4.2 Enter Address│  │ 5.1 Create Order │                   │
│  │ 4.3 Apply Discount│ │ 5.2 Process Payment                  │
│  │ 4.4 Review Order │  │ 5.3 VNPay Callback                   │
│  └────────┬─────────┘  └────────┬─────────┘                   │
│           │                     │                              │
│           │                     │                              │
│  ┌────────┴─────────────────────┴─────────┐                   │
│  │ 6.0 Order Management                   │                   │
│  │ 6.1 View Order History                 │                   │
│  │ 6.2 Track Order                        │                   │
│  └────────┬───────────────────────────────┘                   │
│           │                                                    │
│           │                                                    │
│  ┌────────▼─────────┐                                          │
│  │ 7.0 Admin        │                                          │
│  │    Functions     │                                          │
│  │ 7.1 Manage Products                                          │
│  │ 7.2 Manage Orders                                           │
│  │ 7.3 Manage Users                                            │
│  │ 7.4 Manage Discounts                                        │
│  └────────┬─────────┘                                          │
│           │                                                    │
└───────────┼────────────────────────────────────────────────────┘
            │
            │ Data Stores:
            │ D1: Users
            │ D2: Products
            │ D3: Orders
            │ D4: Cart
            │ D5: Discounts
            │
    ┌───────▼───────┐
    │  Database     │
    └───────────────┘
```

### 3.3 DFD Level 2 - Checkout Process

```
┌────────────────────────────────────────────────────────────┐
│              4.0 CHECKOUT PROCESS                          │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌───────────────┐                                        │
│  │4.1 Validate   │                                        │
│  │   Cart Items  │                                        │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐   Read Shipping     ┌─────────────┐   │
│  │4.2 Load       │◄────────────────────│D4: Shipping │   │
│  │   Shipping    │                     │Addresses    │   │
│  │   Addresses   │                     └─────────────┘   │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐   Read Methods      ┌─────────────┐   │
│  │4.3 Load       │◄────────────────────│D6: Shipping │   │
│  │   Shipping    │                     │Methods      │   │
│  │   Methods     │                     └─────────────┘   │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐   Calculate Total   ┌─────────────┐   │
│  │4.4 Calculate  │◄────────────────────│D2: Products │   │
│  │   Subtotal    │                     └─────────────┘   │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐                                        │
│  │4.5 Apply      │                                        │
│  │   Discount    │                                        │
│  └───────┬───────┘   Read Discounts    ┌─────────────┐   │
│          │         ◄────────────────────│D5: Discounts│   │
│          │                               └─────────────┘   │
│          ▼                                                │
│  ┌───────────────┐                                        │
│  │4.6 Calculate  │                                        │
│  │   Final Total │                                        │
│  │   (+Shipping) │                                        │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐   Write Order       ┌─────────────┐   │
│  │4.7 Create     │────────────────────►│D3: Orders   │   │
│  │   Order       │                     └─────────────┘   │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  5.0 Payment                                              │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### 3.4 DFD Level 2 - Payment Process

```
┌────────────────────────────────────────────────────────────┐
│              5.0 PAYMENT PROCESS                           │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌───────────────┐                                        │
│  │5.1 Create     │                                        │
│  │   Payment     │                                        │
│  │   Request     │                                        │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐                                        │
│  │5.2 Build      │                                        │
│  │   VNPay URL   │                                        │
│  │   + Signature │                                        │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  Redirect to VNPay                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐                                        │
│  │upa1 Pay on    │                                        │
│  │   VNPay       │                                        │
│  │   Gateway     │                                        │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐   Update Order      ┌─────────────┐   │
│  │5.3 Receive    │────────────────────►│D3: Orders   │   │
│  │   Callback    │                     └─────────────┘   │
│  │   + Verify    │                                        │
│  │   Signature   │                                        │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  ┌───────────────┐   Send Email        Email Service     │
│  │5.4 Send       │────────────────────►                  │
│  │   Confirmation│                       (optional)       │
│  └───────┬───────┘                                        │
│          │                                                │
│          ▼                                                │
│  Redirect to Confirmation Page                            │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### 3.5 DFD Level 2 - Admin Process

```
┌────────────────────────────────────────────────────────────┐
│              7.0 ADMIN PROCESSES                            │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌───────────────┐                                        │
│  │7.1 Manage     │    Read/Write      ┌─────────────┐     │
│  │   Products    │◄──────────────────►│D2: Products │     │
│  └───────────────┘                     └─────────────┘     │
│                                                            │
│  ┌───────────────┐                                        │
│  │7.2 Manage     │    Read/Write      ┌─────────────┐     │
│  │   Orders      │◄──────────────────►│D3: Orders   │     │
│  └───────────────┘                     └─────────────┘     │
│                                                            │
│  ┌───────────────┐                                        │
│  │7.3 Manage     │    Read/Write      ┌─────────────┐     │
│  │   Users       │◄──────────────────►│D1: Users    │     │
│  └───────────────┘                     └─────────────┘     │
│                                                            │
│  ┌───────────────┐                                        │
│  │7.4 Manage     │    Read/Write      ┌─────────────┐     │
│  │   Categories  │◄──────────────────►│D8:          │     │
│  └───────────────┘                     │Categories   │     │
│                                        └─────────────┘     │
│                                                            │
│  ┌───────────────┐                                        │
│  │7.5 Manage     │    Read/Write      ┌─────────────┐     │
│  │   Discounts   │◄──────────────────►│D5: Discounts│     │
│  └───────────────┘                     └─────────────┘     │
│                                                            │
│  ┌───────────────┐                                        │
│  │7.6 Generate   │    Read/Write      ┌─────────────┐     │
│  │   Reports     │◄──────────────────►│D3: Orders   │     │
│  └───────────────┘                     │D2: Products │     │
│                                        │D1: Users    │     │
│                                        └─────────────┘     │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

## 4. USER STORIES

### 4.1 ○User Stories (Epic 1: Authentication)

**US-001: User Registration**
```
As a guest user,
I want to create an account
So that I can shop and track my orders

Acceptance Criteria:
- User can register with email, username, and password
- Email must be unique
- Password must meet security requirements
- System creates account with role "USER"
- User receives confirmation
```

**US-002: User Login**
```
As a registered user,
I want to login to my account
So that I can access my profile and order history

Acceptance Criteria:
- User can login with email and password
- System creates session after successful login
- Session lasts 30 minutes
- User is redirected to home after login
```

**US-003: Password Reset**
```
As a user who forgot password,
I want to reset my password
So that I can regain access to my account

Acceptance Criteria:
- User can request password reset
- System sends reset email with token
- Token expires in 24 hours
- User can create new password
```

### 4.2 User Stories (Epic 2: Shopping)

**US-004: Browse Products**
```
As a customer,
I want to browse available products
So that I can find items to purchase

Acceptance Criteria:
- User sees list of products with images and prices
- User can click to view product details
- User can search for products
- User can filter by category
```

**US-005: View Product Details**
```
As a customer,
I want to view detailed information about a product
So that I can make informed purchase decisions

Acceptance Criteria:
- User sees product name, price, description, stock
- User sees all product images in gallery
- User sees related products (optional)
- Stock availability is shown
```

**US-006: Add to Cart**
```
As a logged-in customer,
I want to add products to my cart
So that I can buy multiple items together

Acceptance Criteria:
- User can add product to cart
- Quantity cannot exceed stock
- Cart persists across sessions
- Cart shows total items count
```

### 4.3 User Stories (Epic 3: Checkout)

**US-007: Checkout**
```
As a customer,
I want to checkout my cart
So that I can complete my purchase

Acceptance Criteria:
- User can review cart items
- User can select or add shipping address
- User can choose shipping method
- System calculates total including shipping
- User can apply discount codes
```

**US-008: Payment**
```
As a customer,
I want to pay for my order
So that I can receive my products

Acceptance Criteria:
- User can pay via VNPay
- User can pay via Bank Transfer
- Payment status is updated
- Order status becomes "PAID" after payment
- User receives confirmation
```

**US-009: Apply Discount**
```
As a customer,
I want to apply discount codes
So that I can save money on my purchase

Acceptance Criteria:
- User can enter discount code
- System validates code and conditions
- System applies discount to total
- Discount amount is clearly shown
- Invalid codes show error message
```

### 4.4 User Stories (Epic 4: Order Management)

**US-010: View Order History**
```
As a customer,
I want to view my order history
So that I can track my past purchases

Acceptance Criteria:
- User sees list of all past orders
- Each order shows order ID, date, total, status
- User can click to view order details
- Orders are sorted by date (newest first)
```

**US-011: Track Order**
```
As a customer,
I want to track my order status
So that I know when to expect my delivery

Acceptance Criteria:
- User sees current order status
- User sees estimated delivery date
- User can see tracking number (if shipped)
- Order statuses: PENDING, PROCESSING, SHIPPED, DELIVERED
```

### 4.5 User Stories (Epic 5: Admin)

**US-012: Manage Products**
```
As an admin,
I want to manage products
So that I can keep product information up-to-date

Acceptance Criteria:
- Admin can create new products
- Admin can edit existing products
- Admin can delete products
- Admin can upload multiple product images
- Admin can search and filter products
```

**US-013: Manage Orders**
```
As an admin,
I want to manage orders
So that I can process and fulfill customer orders

Acceptance Criteria:
- Admin sees all orders in system
- Admin can filter by status and date
- Admin can view order details
- Admin can update order status
- Admin can add tracking number
```

**US-014: Manage Discounts**
```
As an admin,
I want to create and manage discounts
So that I can run promotions and sales

Acceptance Criteria:
- Admin can create discount codes
- Admin can set conditions for auto-assignment
- Admin can set expiry dates
- Admin can view usage statistics
- Admin can enable/disable discounts
```

---

## 5. INTERACTION DIAGRAMS

### 5.1 Sequence Diagram - Complete Order Flow

```
User    Browser    Servlet    DAO       Database    VNPay
│          │          │         │          │          │
│──POST───►│          │         │          │          │
│  Add to │          │         │          │          │
│  Cart   │          │         │          │          │
│         │          │         │          │          │
│         │──POST───►│         │          │          │
│         │          │──SQL───►│          │          │
│         │          │         │──defined──►         │
│         │          │         │          │          │
│         │          │◄──Result├──────────┤          │
│         │◄──200────│         │          │          │
│◄─Redirect│          │         │          │          │
│          │          │         │          │          │
│────┐     │          │         │          │          │
│    │     │          │         │          │          │
│    ▼     │          │         │          │          │
│ Checkout │          │         │          │          │
│    │     │          │         │          │          │
│──GET────►│          │         │          │          │
│          │──GET────►│         │          │          │
│          │          │ fecthing          │          │
│          │          │ cart items│          │          │
│          │          │──Query──►│          │          │
│          │          │◄─Data───│          │          │
│          │◄─cart.jsp│         │          │          │
│◄─────────│          │         │          │          │
│ Fill Form│          │         │          │          │
│    │     │          │         │          │          │
│──POST────►│          │         │          │          │
│  Checkout│          │         │          │          │
│    │     │──POST───►│         │          │          │
│    │     │          │ Create Order│          │          │
│    │     │          │          │──INSERT──►│          │
│    │     │          │          │◄──OK─────┤          │
│    │     │          │          │          │          │
│    │     │          │ Build    │          │          │
│    │     │          │ Payment URL          │          │
│    │     │◄─redirect│          │          │          │
│    │◄────│          │          │          │          │
│ Redirect │          │          │          │          │
│ to VNPay │          │          │          │          │
│    │     │          │          │          │          │
│──Pay────►│          │          │          │          │
│ on VNPay │          │          │          │          │
│    │     │          │          │          │          │
│    │     │◄─Callback├──────────┤──────────┼──Return──┤
│    │     │          │          │          │          │
│    │     │ Verify   │          │          │          │
│    │     │ Signature│          │          │          │
│    │     │          │          │          │          │
│    │     │ Update   │          │          │          │
│    │     │ Order    │          │          │          │
│    │     │          │────UPDATE│          │          │
│    │     │◄─200 OK──│          │          │          │
│◄─Confirmation          │          │          │          │
│          │          │          │          │          │
```

### 5.2 Sequence Diagram - Admin Update Product

```
Admin   Browser   AdminServlet  ProductDB  Database   FileSystem
│          │            │          │          │            │
│──GET────►│            │          │          │            │
│  Manage  │            │          │          │            │
│ Products │            │          │          │            │
│          │            │          │          │            │
│          │──GET──────►│          │          │            │
│          │            │──Query──►│          │            │
│          │            │          │──SELECT─►│            │
│          │            │          │◄──Data───┤            │
│          │            │◄──Result─│          │            │
│          │◄─products.jsp         │          │ Observe     │
│◄─────────│            │          │          │            │
│ Edit     │            │          │          │            │
│ Product  │            │          │          │            │
│          │            │          │          │            │
│──POST────►│            │          │          │            │
│  Update  │            │          │          │            │
│          │──POST─────►│          │          │            │
│          │            │ Save     │          │            │
│          │            │ Images   │          │            │
│          │            │─Upload──►│          │──SAVE──►   │
│          │            │◄──OK─────│          │◄──────┤    │
│          │            │          │          │            │
│          │            │ Update   │          │            │
│          │            │ Product  │          │            │
│          │            │─────────►│──UPDATE─►│            │
│          │            │          │◄──OK─────┤            │
│          │            │◄──Success│          │            │
│          │◄─200 OK────│          │          │            │
│◄─Redirect │            │          │          │            │
│          │            │          │          │            │
```

---

**Document Version**: 1.0  
**Last Updated**: 2024  
**Authors**: SWP Project Team
