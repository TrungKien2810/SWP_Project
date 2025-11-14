# 🎯 4 KỊCH BẢN THUYẾT TRÌNH DỰ ÁN COSMETICSHOP

## 📋 TỔNG QUAN

Dự án CosmeticShop là hệ thống website bán mỹ phẩm trực tuyến với đầy đủ chức năng từ mua sắm, thanh toán đến quản trị. Tài liệu này cung cấp 4 kịch bản thuyết trình để nhóm 4 người có thể trình bày toàn bộ tính năng của hệ thống.

---

## 🎬 KỊCH BẢN 1: TRẢI NGHIỆM KHÁCH HÀNG - MUA SẮM VÀ THANH TOÁN
**Người trình bày: Thành viên 1**  
**Thời gian: 15-20 phút**

### Mục tiêu
Trình bày luồng mua sắm từ trang chủ đến thanh toán thành công, bao gồm: duyệt sản phẩm, giỏ hàng, checkout, thanh toán và feedback.

### Các bước trình bày

#### 1. Trang chủ và Duyệt Sản phẩm (3 phút)
- **Trang chủ (home.jsp)**
  - Hiển thị banner, sản phẩm nổi bật
  - Sản phẩm bán chạy, sản phẩm khuyến mãi
  - Navigation menu với các danh mục

- **Bộ sưu tập (collection.jsp)**
  - Xem tất cả sản phẩm
  - Lọc theo danh mục
  - Tìm kiếm sản phẩm
  - Sắp xếp theo giá, tên

- **Chi tiết sản phẩm (product-detail.jsp)**
  - Xem thông tin: tên, giá, mô tả, tồn kho
  - Gallery ảnh (nhiều ảnh)
  - Xem đánh giá và bình luận của khách hàng khác
  - Nút "Thêm vào giỏ hàng" và "Mua ngay"
  - Nút "Thêm vào Wishlist" (nếu đã đăng nhập)

**Demo:**
```
1. Truy cập trang chủ → Xem banner và sản phẩm nổi bật
2. Click "Bộ sưu tập" → Xem danh sách sản phẩm
3. Click vào một sản phẩm → Xem chi tiết với gallery ảnh
4. Đọc mô tả sản phẩm
```

#### 2. Quản lý Giỏ hàng (3 phút)
- **Thêm vào giỏ hàng**
  - Thêm từ trang chi tiết sản phẩm
  - Chọn số lượng (tự động kiểm tra tồn kho)
  - Merge giỏ hàng guest khi đăng nhập

- **Xem giỏ hàng (cart.jsp)**
  - Danh sách sản phẩm đã thêm
  - Cập nhật số lượng
  - Xóa sản phẩm
  - Chọn/bỏ chọn sản phẩm để thanh toán
  - Tổng tiền tạm tính
  - Áp dụng mã giảm giá (nếu có)

**Demo:**
```
1. Thêm 2-3 sản phẩm vào giỏ hàng
2. Vào trang giỏ hàng → Xem danh sách
3. Cập nhật số lượng một sản phẩm
4. Xóa một sản phẩm
5. Chọn các sản phẩm muốn thanh toán
```

#### 3. Checkout và Thanh toán (5 phút)
- **Trang Checkout (checkout.jsp)**
  - Xem lại sản phẩm đã chọn
  - Chọn/Thêm địa chỉ giao hàng
  - Chọn phương thức vận chuyển (Standard, Express)
  - Áp dụng mã giảm giá
  - Xem tổng kết: Subtotal, Shipping, Discount, Total

- **Thanh toán**
  - **Thanh toán COD (Cash on Delivery)**
    - Chọn "Thanh toán khi nhận hàng"
    - Xác nhận đơn hàng
    - Tạo đơn hàng với status = "PENDING"
    - Hiển thị trang xác nhận đơn hàng

  - **Thanh toán VNPay**
    - Chọn "Thanh toán VNPay"
    - Redirect đến cổng thanh toán VNPay
    - Thanh toán trên VNPay
    - Callback về hệ thống
    - Verify signature
    - Cập nhật payment_status = "PAID"
    - Hiển thị xác nhận

**Demo:**
```
1. Click "Thanh toán" từ giỏ hàng
2. Chọn địa chỉ giao hàng (hoặc thêm mới)
3. Chọn phương thức vận chuyển
4. Nhập mã giảm giá "SPRING10" → Áp dụng thành công
5. Xem tổng kết cuối cùng
6. Chọn "Thanh toán COD" → Xác nhận đơn hàng
7. (Hoặc) Chọn "Thanh toán VNPay" → Demo thanh toán
```

#### 4. Feedback Sản phẩm (3 phút)
- **Điều kiện feedback**
  - Chỉ feedback sau khi đã mua và nhận hàng (order status = COMPLETED)
  - Mỗi sản phẩm chỉ feedback 1 lần

- **Thêm đánh giá (product-detail.jsp)**
  - Chọn số sao (1-5)
  - Viết bình luận
  - Upload ảnh/video (tùy chọn)
  - Submit đánh giá

- **Xem đánh giá**
  - Hiển thị đánh giá trung bình
  - Phân bố số sao
  - Danh sách bình luận với ảnh
  - Trả lời bình luận (nếu có)

**Demo:**
```
1. Sau khi admin xác nhận đơn hàng = COMPLETED
2. Vào trang chi tiết sản phẩm đã mua
3. Click "Đánh giá sản phẩm"
4. Chọn 5 sao, viết bình luận tích cực
5. Upload 2-3 ảnh sản phẩm
6. Submit → Xem đánh giá hiển thị trên trang
```

### Tóm tắt Kịch bản 1
✅ Duyệt sản phẩm từ trang chủ → Bộ sưu tập → Chi tiết  
✅ Thêm vào giỏ hàng và quản lý giỏ hàng  
✅ Checkout với địa chỉ và phương thức vận chuyển  
✅ Thanh toán COD và VNPay  
✅ Feedback sản phẩm sau khi nhận hàng  

---

## 🎬 KỊCH BẢN 2: QUẢN LÝ TÀI KHOẢN VÀ THEO DÕI ĐƠN HÀNG
**Người trình bày: Thành viên 2**  
**Thời gian: 15-20 phút**

### Mục tiêu
Trình bày các chức năng quản lý tài khoản, theo dõi đơn hàng, wishlist, và hệ thống thông báo.

### Các bước trình bày

#### 1. Đăng ký và Đăng nhập (3 phút)
- **Đăng ký tài khoản (signup.java)**
  - Form đăng ký: Email (Gmail), Username, Password
  - Validation email format (phải là @gmail.com)
  - Kiểm tra email đã tồn tại
  - Tạo tài khoản với role = "USER"
  - Redirect đến trang đăng nhập

- **Đăng nhập (login.java)**
  - Form đăng nhập: Email, Password
  - Validate credentials
  - Merge giỏ hàng guest vào cart user (nếu có)
  - Tạo session (30 phút)
  - Lưu user object vào session
  - "Remember me" → Lưu cookie (7 ngày)

- **Quên mật khẩu (PasswordResetRequest.java)**
  - Nhập email
  - Gửi email reset với token
  - Token hết hạn sau 24 giờ
  - Reset password (PasswordReset.java)

**Demo:**
```
1. Đăng ký tài khoản mới với email @gmail.com
2. Đăng nhập với tài khoản vừa tạo
3. (Optional) Demo quên mật khẩu → Nhận email reset
```

#### 2. Quản lý Tài khoản (3 phút)
- **Thông tin tài khoản (account-management.jsp)**
  - Xem thông tin: Họ tên, Email, Số điện thoại
  - Cập nhật thông tin cá nhân
  - Upload avatar

- **Đổi mật khẩu (change-password.jsp)**
  - Nhập mật khẩu cũ
  - Nhập mật khẩu mới (2 lần)
  - Validate và cập nhật

**Demo:**
```
1. Vào "Quản lý tài khoản"
2. Cập nhật số điện thoại
3. Upload avatar mới
4. Đổi mật khẩu
```

#### 3. Wishlist (2 phút)
- **Thêm vào Wishlist**
  - Click icon "Yêu thích" trên sản phẩm
  - Lưu vào database
  - Hiển thị số lượng wishlist trên header

- **Xem Wishlist (wishlist.jsp)**
  - Danh sách sản phẩm đã yêu thích
  - Xóa khỏi wishlist
  - Thêm vào giỏ hàng từ wishlist

**Demo:**
```
1. Thêm 3 sản phẩm vào wishlist từ trang chi tiết
2. Vào trang "Wishlist" → Xem danh sách
3. Xóa 1 sản phẩm khỏi wishlist
4. Thêm 1 sản phẩm từ wishlist vào giỏ hàng
```

#### 4. Lịch sử Đơn hàng (4 phút)
- **Xem danh sách đơn hàng (my-orders.jsp)**
  - Tất cả đơn hàng của user
  - Hiển thị: Order ID, Ngày đặt, Tổng tiền, Trạng thái
  - Ảnh sản phẩm đầu tiên
  - Sắp xếp theo ngày (mới nhất trước)

- **Chi tiết đơn hàng (order-detail.jsp)**
  - Thông tin đơn hàng: ID, Ngày đặt, Trạng thái
  - Danh sách sản phẩm: Tên, Số lượng, Giá
  - Địa chỉ giao hàng
  - Phương thức vận chuyển
  - Mã giảm giá đã áp dụng
  - Tổng tiền chi tiết
  - Tracking number (nếu đã ship)
  - Payment status

**Demo:**
```
1. Vào "Lịch sử đơn hàng" → Xem danh sách
2. Click vào một đơn hàng → Xem chi tiết
3. Xem các trạng thái: PENDING, PROCESSING, SHIPPED, DELIVERED
```

#### 5. Hệ thống Thông báo (4 phút)
- **Notification Center (notifications.jsp)**
  - Danh sách thông báo
  - Đánh dấu đã đọc/chưa đọc
  - Xóa thông báo
  - Badge số thông báo chưa đọc trên header

- **Các loại thông báo**
  - **ORDER_STATUS_UPDATE**: Admin cập nhật trạng thái đơn hàng
    - "Đơn hàng #123 đã được xác nhận"
    - "Đơn hàng #123 đang được xử lý"
    - "Đơn hàng #123 đã được giao hàng"
    - "Đơn hàng #123 đã hoàn thành"
  - **NEW_ORDER**: Tạo đơn hàng mới (cho admin)
  - **LOW_RATING**: Đánh giá thấp (cho admin)

- **Luồng thông báo**
  1. User tạo đơn hàng → Admin nhận thông báo "Đơn hàng mới"
  2. Admin xử lý đơn hàng → User nhận thông báo "Đơn hàng đã được xác nhận"
  3. Admin cập nhật status → User nhận thông báo tương ứng
  4. User nhận hàng và thanh toán → Có thể feedback

**Demo:**
```
1. Tạo một đơn hàng mới
2. (Chuyển sang Admin) Admin xác nhận đơn hàng
3. (Quay lại User) Vào Notification Center
4. Xem thông báo "Đơn hàng #XXX đã được xác nhận"
5. Click vào thông báo → Redirect đến chi tiết đơn hàng
6. Đánh dấu đã đọc
```

#### 6. Quản lý Địa chỉ Giao hàng (2 phút)
- **Danh sách địa chỉ (shipping-address.jsp)**
  - Xem tất cả địa chỉ đã lưu
  - Đặt địa chỉ mặc định
  - Thêm địa chỉ mới
  - Sửa địa chỉ
  - Xóa địa chỉ

**Demo:**
```
1. Vào "Địa chỉ giao hàng"
2. Thêm địa chỉ mới (Họ tên, SĐT, Địa chỉ, Tỉnh/Thành, Quận/Huyện, Phường/Xã)
3. Đặt làm địa chỉ mặc định
4. Sửa một địa chỉ
```

### Tóm tắt Kịch bản 2
✅ Đăng ký, đăng nhập, quên mật khẩu  
✅ Quản lý thông tin tài khoản và đổi mật khẩu  
✅ Wishlist - Yêu thích sản phẩm  
✅ Lịch sử đơn hàng và chi tiết đơn hàng  
✅ Hệ thống thông báo khi admin xử lý đơn hàng  
✅ Quản lý địa chỉ giao hàng  

---

## 🎬 KỊCH BẢN 3: QUẢN TRỊ - QUẢN LÝ SẢN PHẨM VÀ ĐƠN HÀNG
**Người trình bày: Thành viên 3**  
**Thời gian: 15-20 phút**

### Mục tiêu
Trình bày các chức năng quản trị: quản lý sản phẩm, danh mục, đơn hàng, và người dùng.

### Các bước trình bày

#### 1. Dashboard Admin (3 phút)
- **Trang Dashboard (admin/dashboard.jsp)**
  - **Thống kê hôm nay:**
    - Doanh thu hôm nay
    - Số đơn hàng mới
    - Số khách hàng mới
    - Số sản phẩm sắp hết hàng (stock < 5)
  
  - **Biểu đồ doanh thu 7 ngày gần nhất**
    - Line chart hiển thị doanh thu theo ngày
    - Phân tích xu hướng

  - **Thông báo cho Admin**
    - Đơn hàng mới
    - Đánh giá thấp (rating <= 2)
    - Liên hệ mới

**Demo:**
```
1. Đăng nhập với tài khoản ADMIN
2. Vào Dashboard → Xem thống kê
3. Xem biểu đồ doanh thu 7 ngày
4. Xem thông báo đơn hàng mới
```

#### 2. Quản lý Sản phẩm (5 phút)
- **Danh sách sản phẩm (admin/manage-products.jsp)**
  - Xem tất cả sản phẩm
  - Tìm kiếm sản phẩm theo tên
  - Hiển thị: ID, Tên, Giá, Tồn kho, Danh mục, Hành động

- **Thêm sản phẩm mới (ProductController.java)**
  - Form thêm sản phẩm:
    - Tên sản phẩm
    - Mô tả
    - Giá
    - Tồn kho
    - Chọn danh mục (có thể chọn nhiều)
    - Upload ảnh chính
    - Upload nhiều ảnh phụ (gallery)
  - Validation: Tên, giá, tồn kho không được rỗng
  - Lưu ảnh vào thư mục IMG
  - Lưu thông tin vào database

- **Sửa sản phẩm**
  - Load thông tin sản phẩm hiện tại
  - Cập nhật thông tin
  - Thêm/xóa ảnh
  - Cập nhật danh mục

- **Xóa sản phẩm**
  - Xác nhận trước khi xóa
  - Xóa ảnh liên quan
  - Xóa khỏi database

**Demo:**
```
1. Vào "Quản lý sản phẩm" → Xem danh sách
2. Tìm kiếm sản phẩm theo tên
3. Click "Thêm sản phẩm mới"
   - Nhập: Tên "Kem dưỡng ẩm", Giá 500000, Tồn kho 50
   - Chọn danh mục "Chăm sóc da"
   - Upload ảnh chính và 3 ảnh phụ
   - Submit → Xem sản phẩm mới trong danh sách
4. Click "Sửa" một sản phẩm → Cập nhật giá
5. Click "Xóa" một sản phẩm → Xác nhận và xóa
```

#### 3. Quản lý Danh mục (2 phút)
- **Danh sách danh mục (admin/manage-categories.jsp)**
  - Xem tất cả danh mục
  - Thêm danh mục mới (Tên, Mô tả, Ảnh)
  - Sửa danh mục
  - Xóa danh mục (kiểm tra có sản phẩm không)

**Demo:**
```
1. Vào "Quản lý danh mục"
2. Thêm danh mục mới: "Trang điểm"
3. Sửa danh mục "Chăm sóc da" → Thêm mô tả
4. Xem danh sách danh mục
```

#### 4. Quản lý Đơn hàng (5 phút)
- **Danh sách đơn hàng (admin/manage-orders.jsp)**
  - Xem tất cả đơn hàng
  - **Lọc theo trạng thái:**
    - PENDING (Chờ xử lý)
    - PROCESSING (Đang xử lý)
    - SHIPPED (Đã giao hàng)
    - DELIVERED (Đã nhận hàng)
    - COMPLETED (Hoàn thành)
    - CANCELLED (Đã hủy)
  
  - **Lọc theo ngày:**
    - Hôm nay
    - 7 ngày qua
    - 30 ngày qua
    - Khoảng thời gian tùy chọn

  - Hiển thị: Order ID, Khách hàng, Ngày đặt, Tổng tiền, Trạng thái

- **Chi tiết đơn hàng (admin/order-detail.jsp)**
  - Thông tin khách hàng
  - Danh sách sản phẩm
  - Địa chỉ giao hàng
  - Phương thức vận chuyển
  - Mã giảm giá
  - Tổng tiền
  - **Cập nhật trạng thái đơn hàng:**
    - PENDING → PROCESSING: Xác nhận đơn hàng, trừ tồn kho
    - PROCESSING → SHIPPED: Nhập tracking number
    - SHIPPED → DELIVERED: Xác nhận đã giao
    - DELIVERED → COMPLETED: Hoàn thành, tự động cập nhật payment_status = PAID
  - **Gửi thông báo:** Mỗi lần cập nhật status → Tạo notification cho user

**Demo:**
```
1. Vào "Quản lý đơn hàng" → Xem danh sách
2. Lọc đơn hàng "PENDING" → Xem các đơn chờ xử lý
3. Click vào một đơn hàng → Xem chi tiết
4. Cập nhật trạng thái:
   - PENDING → PROCESSING (Xác nhận đơn hàng)
   - PROCESSING → SHIPPED (Nhập tracking number "VN123456")
   - SHIPPED → DELIVERED
   - DELIVERED → COMPLETED
5. Mỗi lần cập nhật → User nhận thông báo (kiểm tra Notification Center)
6. Lọc đơn hàng theo ngày "Hôm nay"
```

#### 5. Quản lý Người dùng (2 phút)
- **Danh sách người dùng (admin/manage-users.jsp)**
  - Xem tất cả user
  - Hiển thị: ID, Tên, Email, Role, Ngày tạo
  - Thay đổi role (USER ↔ ADMIN)
  - Xem thống kê: Số đơn hàng, Tổng tiền đã chi

**Demo:**
```
1. Vào "Quản lý người dùng"
2. Xem danh sách user
3. Thay đổi role một user từ USER → ADMIN
4. Xem thống kê của một user
```

### Tóm tắt Kịch bản 3
✅ Dashboard Admin với thống kê và biểu đồ  
✅ Quản lý sản phẩm: Thêm, sửa, xóa, upload ảnh, nhiều danh mục  
✅ Quản lý danh mục sản phẩm  
✅ Quản lý đơn hàng: Lọc, xem chi tiết, cập nhật trạng thái, tracking  
✅ Quản lý người dùng và phân quyền  

---

## 🎬 KỊCH BẢN 4: QUẢN TRỊ - MÃ GIẢM GIÁ VÀ BÁO CÁO
**Người trình bày: Thành viên 4**  
**Thời gian: 15-20 phút**

### Mục tiêu
Trình bày hệ thống mã giảm giá, voucher tự động, báo cáo, và các tính năng nâng cao.

### Các bước trình bày

#### 1. Quản lý Mã giảm giá (6 phút)
- **Danh sách mã giảm giá (admin/manage-discounts.jsp)**
  - Xem tất cả mã giảm giá
  - Hiển thị: Code, Tên, Loại, Giá trị, Số lần dùng, Trạng thái

- **Tạo mã giảm giá mới (DiscountController.java)**
  - **Thông tin cơ bản:**
    - Mã code (unique)
    - Tên mã giảm giá
    - Mô tả
    - Trạng thái (Active/Inactive)
  
  - **Loại giảm giá:**
    - **PERCENTAGE**: Giảm theo phần trăm (ví dụ: 20%)
      - Có thể set max discount amount (ví dụ: tối đa 150,000 VND)
    - **FIXED_AMOUNT**: Giảm số tiền cố định (ví dụ: 50,000 VND)
  
  - **Điều kiện sử dụng:**
    - Minimum order amount (ví dụ: đơn hàng tối thiểu 500,000 VND)
    - Usage limit (số lần sử dụng tối đa)
    - Start date / End date (thời gian hiệu lực)
  
  - **Auto-assign (Tự động gán voucher):**
    - Bật/tắt tự động gán
    - **Điều kiện gán:**
      - **TOTAL_SPENT**: Tổng tiền đã chi >= giá trị
      - **ORDER_COUNT**: Số đơn hàng >= giá trị
      - **FIRST_ORDER**: Đơn hàng đầu tiên
    - **Special event**: Gán cho tất cả user (ví dụ: Sinh nhật shop)

- **Sửa/Xóa mã giảm giá**
  - Cập nhật thông tin
  - Xóa mã giảm giá (kiểm tra đã sử dụng chưa)

**Demo:**
```
1. Vào "Quản lý mã giảm giá" → Xem danh sách
2. Click "Tạo mã mới":
   - Code: "SPRING20"
   - Tên: "Giảm 20% mùa xuân"
   - Loại: PERCENTAGE, Giá trị: 20%
   - Max discount: 200,000 VND
   - Min order: 500,000 VND
   - Usage limit: 100 lần
   - Thời gian: 01/01/2024 - 31/03/2024
   - Auto-assign: OFF
   - Submit → Xem mã mới trong danh sách

3. Tạo mã với Auto-assign:
   - Code: "WELCOME10"
   - Loại: PERCENTAGE, 10%
   - Auto-assign: ON
   - Condition: FIRST_ORDER
   - → Mã này sẽ tự động gán cho user khi đặt đơn hàng đầu tiên

4. Sửa một mã giảm giá → Cập nhật usage limit
```

#### 2. Hệ thống Voucher Tự động (3 phút)
- **Cơ chế hoạt động:**
  1. User hoàn thành đơn hàng (status = COMPLETED)
  2. Trigger `tr_OrderCreated_AssignVouchers` được kích hoạt
  3. Gọi stored procedure `sp_CheckAndAssignVouchers`
  4. System kiểm tra các điều kiện:
     - Tổng tiền đã chi
     - Số đơn hàng đã đặt
     - Đơn hàng đầu tiên
  5. Nếu đủ điều kiện → Gán voucher vào UserVouchers
  6. User xem voucher trong "My Discounts"

- **Xem voucher của user (my-discounts.jsp)**
  - Danh sách voucher đã được gán
  - Trạng thái: UNUSED, USED, EXPIRED
  - Ngày hết hạn
  - Sử dụng voucher khi checkout

**Demo:**
```
1. (Chuyển sang User) Vào "Mã giảm giá của tôi"
2. Xem danh sách voucher đã được gán tự động
3. (Quay lại Admin) Tạo đơn hàng test → Hoàn thành
4. (Quay lại User) Kiểm tra có voucher mới không (nếu đủ điều kiện)
```

#### 3. Áp dụng Mã giảm giá (2 phút)
- **Tại trang Checkout**
  - User nhập mã giảm giá
  - System validate:
    - Mã có tồn tại không?
    - Mã còn hiệu lực không?
    - User có quyền sử dụng không?
    - Đơn hàng đã đạt minimum order chưa?
  - Tính discount amount:
    - PERCENTAGE: (Subtotal × %) nhưng không vượt max discount
    - FIXED_AMOUNT: Trừ thẳng số tiền
  - Cập nhật tổng tiền
  - Hiển thị discount info

- **Xóa mã giảm giá**
  - Click "Xóa mã giảm giá"
  - Cập nhật lại tổng tiền

**Demo:**
```
1. (User) Vào giỏ hàng → Thanh toán
2. Nhập mã "SPRING20" → Áp dụng thành công
3. Xem tổng tiền đã giảm
4. Xóa mã giảm giá → Tổng tiền trở về ban đầu
5. Thử mã không hợp lệ → Hiển thị lỗi
```

#### 4. Báo cáo và Thống kê (4 phút)
- **Trang Báo cáo (admin/reports.jsp)**
  - **Báo cáo doanh thu:**
    - Doanh thu theo ngày/tuần/tháng
    - So sánh các kỳ
    - Top sản phẩm bán chạy
  
  - **Báo cáo đơn hàng:**
    - Số đơn hàng theo trạng thái
    - Tỷ lệ hủy đơn
    - Đơn hàng theo phương thức thanh toán
  
  - **Báo cáo khách hàng:**
    - Số khách hàng mới
    - Khách hàng VIP (chi tiêu cao)
    - Tỷ lệ quay lại
  
  - **Báo cáo mã giảm giá:**
    - Số lần sử dụng
    - Tổng tiền đã giảm
    - Mã giảm giá hiệu quả nhất

**Demo:**
```
1. Vào "Báo cáo" → Xem các loại báo cáo
2. Xem báo cáo doanh thu tháng này
3. Xem top 10 sản phẩm bán chạy
4. Xem báo cáo sử dụng mã giảm giá
```

#### 5. Quản lý Liên hệ (2 phút)
- **Danh sách liên hệ (admin/manage-contact.jsp)**
  - Xem tất cả tin nhắn từ khách hàng
  - Trạng thái: Chưa đọc, Đã đọc, Đã trả lời
  - Cập nhật trạng thái
  - Xóa tin nhắn

**Demo:**
```
1. (User) Vào trang "Liên hệ" → Gửi tin nhắn
2. (Admin) Vào "Quản lý liên hệ" → Xem tin nhắn mới
3. Đánh dấu đã đọc
4. Xóa tin nhắn cũ
```

#### 6. Tính năng Nâng cao (3 phút)
- **Tìm kiếm và Gợi ý (SearchSuggestions.java)**
  - Tìm kiếm sản phẩm real-time
  - Gợi ý sản phẩm khi gõ
  - Tìm kiếm theo tên, danh mục

- **Sản phẩm nổi bật (FeaturedProducts.java)**
  - Hiển thị sản phẩm nổi bật trên trang chủ
  - Dựa trên lượt xem, đánh giá

- **Sản phẩm bán chạy (BestSellingProducts.java)**
  - Top sản phẩm bán chạy
  - Dựa trên số lượng đã bán

- **Sản phẩm khuyến mãi (PromotionalProducts.java)**
  - Sản phẩm đang có giảm giá
  - Hiển thị % giảm giá

**Demo:**
```
1. (User) Trang chủ → Xem sản phẩm nổi bật
2. Tìm kiếm "kem" → Xem gợi ý real-time
3. Xem sản phẩm bán chạy
4. Xem sản phẩm khuyến mãi
```

### Tóm tắt Kịch bản 4
✅ Quản lý mã giảm giá: Tạo, sửa, xóa, điều kiện sử dụng  
✅ Hệ thống voucher tự động gán dựa trên điều kiện  
✅ Áp dụng mã giảm giá khi checkout  
✅ Báo cáo và thống kê: Doanh thu, đơn hàng, khách hàng  
✅ Quản lý liên hệ từ khách hàng  
✅ Tính năng nâng cao: Tìm kiếm, gợi ý, sản phẩm nổi bật  

---

## 📊 TỔNG KẾT 4 KỊCH BẢN

### Bảng Phân Công Tính Năng

| Tính Năng | Kịch bản 1 | Kịch bản 2 | Kịch bản 3 | Kịch bản 4 |
|-----------|------------|------------|------------|------------|
| **Trang chủ & Duyệt sản phẩm** | ✅ | | | |
| **Giỏ hàng** | ✅ | | | |
| **Checkout & Thanh toán** | ✅ | | | |
| **Feedback sản phẩm** | ✅ | | | |
| **Đăng ký/Đăng nhập** | | ✅ | | |
| **Quản lý tài khoản** | | ✅ | | |
| **Wishlist** | | ✅ | | |
| **Lịch sử đơn hàng** | | ✅ | | |
| **Thông báo** | | ✅ | | |
| **Địa chỉ giao hàng** | | ✅ | | |
| **Dashboard Admin** | | | ✅ | |
| **Quản lý sản phẩm** | | | ✅ | |
| **Quản lý danh mục** | | | ✅ | |
| **Quản lý đơn hàng** | | | ✅ | |
| **Quản lý người dùng** | | | ✅ | |
| **Quản lý mã giảm giá** | | | | ✅ |
| **Voucher tự động** | | | | ✅ |
| **Báo cáo** | | | | ✅ |
| **Liên hệ** | | | | ✅ |

### Checklist Trước Khi Thuyết Trình

- [ ] Đã chuẩn bị dữ liệu test (sản phẩm, đơn hàng, user)
- [ ] Đã test tất cả luồng hoạt động
- [ ] Đã chuẩn bị tài khoản ADMIN và USER
- [ ] Đã kiểm tra kết nối database
- [ ] Đã kiểm tra VNPay integration (nếu demo thanh toán)
- [ ] Đã chuẩn bị slide/script cho từng kịch bản
- [ ] Đã phân công thời gian cho từng phần

### Lưu Ý Khi Thuyết Trình

1. **Kết nối giữa các kịch bản:**
   - Kịch bản 1 tạo đơn hàng → Kịch bản 3 xử lý đơn hàng
   - Kịch bản 3 cập nhật đơn hàng → Kịch bản 2 nhận thông báo
   - Kịch bản 4 tạo mã giảm giá → Kịch bản 1 sử dụng mã

2. **Demo thực tế:**
   - Sử dụng dữ liệu thật, không chỉ mô tả
   - Thể hiện các edge cases (lỗi, validation)
   - Giải thích logic nghiệp vụ

3. **Tương tác:**
   - Mỗi thành viên nên có phần Q&A
   - Chuẩn bị trả lời về kiến trúc, database, security

4. **Thời gian:**
   - Mỗi kịch bản: 15-20 phút
   - Tổng: 60-80 phút
   - Dành 10-20 phút cuối cho Q&A

---

## 🔄 LUỒNG HOẠT ĐỘNG TỔNG QUAN

### Luồng Khách Hàng Hoàn Chỉnh

```
1. Trang chủ
   ↓
2. Bộ sưu tập → Tìm kiếm/Lọc sản phẩm
   ↓
3. Chi tiết sản phẩm → Xem mô tả, đánh giá
   ↓
4. Thêm vào giỏ hàng (hoặc Wishlist)
   ↓
5. Xem giỏ hàng → Cập nhật số lượng, chọn sản phẩm
   ↓
6. Checkout → Chọn địa chỉ, phương thức vận chuyển
   ↓
7. Áp dụng mã giảm giá (nếu có)
   ↓
8. Thanh toán (COD hoặc VNPay)
   ↓
9. Xác nhận đơn hàng → Đợi Admin xử lý
   ↓
10. Nhận thông báo khi Admin cập nhật trạng thái
   ↓
11. Nhận hàng → Thanh toán (nếu COD)
   ↓
12. Feedback sản phẩm (nếu đã nhận hàng)
   ↓
13. Xem lịch sử đơn hàng
```

### Luồng Admin Xử Lý Đơn Hàng

```
1. Nhận thông báo "Đơn hàng mới"
   ↓
2. Vào Quản lý đơn hàng → Xem danh sách
   ↓
3. Click vào đơn hàng → Xem chi tiết
   ↓
4. Cập nhật trạng thái: PENDING → PROCESSING
   - Trừ tồn kho
   - Gửi thông báo cho user
   ↓
5. Cập nhật trạng thái: PROCESSING → SHIPPED
   - Nhập tracking number
   - Gửi thông báo cho user
   ↓
6. Cập nhật trạng thái: SHIPPED → DELIVERED
   - Gửi thông báo cho user
   ↓
7. Cập nhật trạng thái: DELIVERED → COMPLETED
   - Tự động cập nhật payment_status = PAID
   - Gửi thông báo cho user
   - Trigger tự động gán voucher (nếu đủ điều kiện)
```

### Luồng Mã Giảm Giá

```
1. Admin tạo mã giảm giá
   - Thiết lập điều kiện auto-assign (nếu có)
   ↓
2. User đặt đơn hàng và hoàn thành
   ↓
3. Trigger kiểm tra điều kiện
   - Tổng tiền đã chi?
   - Số đơn hàng?
   - Đơn hàng đầu tiên?
   ↓
4. Nếu đủ điều kiện → Gán voucher vào UserVouchers
   ↓
5. User xem voucher trong "Mã giảm giá của tôi"
   ↓
6. User sử dụng voucher khi checkout
   - Validate mã
   - Tính discount amount
   - Áp dụng vào tổng tiền
```

---

## 📝 GHI CHÚ QUAN TRỌNG

### Các Trường Hợp Đặc Biệt Cần Demo

1. **Validation và Error Handling:**
   - Email không đúng format (@gmail.com)
   - Số lượng vượt quá tồn kho
   - Mã giảm giá không hợp lệ
   - Đơn hàng rỗng

2. **Edge Cases:**
   - Guest user thêm vào giỏ → Đăng nhập → Merge cart
   - Sản phẩm hết hàng khi checkout
   - Mã giảm giá hết hạn
   - Đơn hàng bị hủy

3. **Tính Năng Nâng Cao:**
   - Upload nhiều ảnh sản phẩm
   - Gallery ảnh trong chi tiết sản phẩm
   - Upload ảnh/video khi feedback
   - Trả lời bình luận

### Các Câu Hỏi Thường Gặp (Q&A)

**Q: Hệ thống xử lý tồn kho như thế nào?**  
A: Khi admin xác nhận đơn hàng (PENDING → PROCESSING), hệ thống tự động trừ tồn kho. Nếu sản phẩm hết hàng, sẽ hiển thị cảnh báo.

**Q: Làm sao đảm bảo tính bảo mật?**  
A: 
- Session-based authentication
- Role-based access control (USER/ADMIN)
- AdminAuthFilter bảo vệ các trang admin
- Password được hash trước khi lưu
- VNPay signature verification

**Q: Hệ thống thông báo hoạt động như thế nào?**  
A: Sử dụng database table Notifications. Mỗi khi có sự kiện (đơn hàng mới, cập nhật trạng thái), hệ thống tạo notification và hiển thị real-time cho user.

**Q: Voucher tự động gán khi nào?**  
A: Khi đơn hàng chuyển sang trạng thái COMPLETED, trigger sẽ kiểm tra các điều kiện (tổng tiền, số đơn, đơn đầu tiên) và tự động gán voucher nếu user đủ điều kiện.

**Q: Hệ thống xử lý thanh toán VNPay như thế nào?**  
A: 
1. Tạo payment request với signature
2. Redirect user đến VNPay
3. User thanh toán trên VNPay
4. VNPay callback về hệ thống
5. Verify signature và cập nhật payment status

---

**Chúc nhóm thuyết trình thành công! 🎉**


