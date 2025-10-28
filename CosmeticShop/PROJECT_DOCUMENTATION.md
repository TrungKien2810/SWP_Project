# CosmeticShop - Tài liệu Dự án

## 1. Tổng Quan Dự Án

**CosmeticShop** là một ứng dụng web bán mỹ phẩm trực tuyến được xây dựng bằng Java với công nghệ **Jakarta EE 11** và **Maven**.

### 1.1 Công Nghệ Sử Dụng
- **Backend**: Java 17, Jakarta EE 11 (Servlets, JSP)
- **Frontend**: HTML, CSS, JavaScript, Bootstrap
- **Database**: Microsoft SQL Server
- **Build Tool**: Maven
- **Email**: Jakarta Mail
- **Payment**: VNPay integration
- **Authentication**: Session-based với role-based access control

### 1.2 Cấu Trúc Dự Án

```
CosmeticShop/
├── src/main/
│   ├── java/
│   │   ├── Controller/     (27 servlets - xử lý request)
│   │   ├── DAO/            (9 classes - truy cập database)
│   │   ├── Model/          (13 entities - data models)
│   │   ├── Util/           (4 utilities - helpers)
│   │   └── Filter/         (1 filter - authentication)
│   ├── resources/
│   └── webapp/
│       ├── View/           (24 JSP pages - user interface)
│       ├── admin/          (11 admin pages)
│       ├── Css/            (9 stylesheets)
│       ├── IMG/            (74 images)
│       └── WEB-INF/
└── pom.xml
```

---

## 2. Database Schema

### 2.1 Các Bảng Chính

#### **Users** - Bảng người dùng
```sql
- user_id (PK)
- full_name (unique)
- email (unique)
- phone
- password
- role (USER/ADMIN)
- reset_token
- reset_token_expiry
- date_create
```

#### **Products** - Bảng sản phẩm
```sql
- product_id (PK)
- name
- price
- stock
- description
- image_url
- category_id (FK)
```

#### **ProductImages** - Bảng ảnh sản phẩm
```sql
- image_id (PK)
- product_id (FK)
- image_url
- image_order
- created_at
```

#### **Categories** - Bảng danh mục
```sql
- category_id (PK)
- name
- description
```

#### **Orders** - Bảng đơn hàng
```sql
- order_id (PK)
- user_id (FK)
- order_date
- total_amount
- shipping_address_id (FK)
- shipping_method_id (FK)
- shipping_cost
- payment_method
- payment_status
- order_status
- tracking_number
- discount_id (FK)
- discount_amount
- notes
```

#### **OrderDetails** - Chi tiết đơn hàng
```sql
- order_detail_id (PK)
- order_id (FK)
- product_id (FK)
- quantity
- price
```

#### **Carts** - Giỏ hàng
```sql
- cart_id (PK)
- user_id (FK)
- created_at
- updated_at
```

#### **CartItems** - Chi tiết giỏ hàng
```sql
- cart_item_id (PK)
- cart_id (FK)
- product_id (FK)
- quantity
- price
- is_selected
- added_at
```

#### **Discounts** - Mã giảm giá
```sql
- discount_id (PK)
- code (unique)
- name
- description
- discount_type (PERCENTAGE/FIXED_AMOUNT)
- discount_value
- min_order_amount
- max_discount_amount
- usage_limit
- used_count
- start_date
- end_date
- is_active
- condition_type
- condition_value
- special_event
- auto_assign extent:all
- assign_date
```

#### **UserVouchers** - Voucher của người dùng
```sql
- user_voucher_id (PK)
- user_id (FK)
- discount_id (FK)
- status (UNUSED/USED/EXPIRED)
- assigned_date
- used_date
- order_id (FK)
```

#### **ShippingAddresses** - Địa chỉ giao hàng
```sql
- address_id (PK)
- user_id (FK)
- full_name
- rgbx
- address
- city
- district
- ward
- is_default
- created_at
```

#### **ShippingMethods** - Phương thức giao hàng
```sql
- method_id (PK)
- name
- description
- cost
- estimated_days
- is_active
```

### 2.2 Stored Procedures

1. **sp_CheckAndAssignVouchers**: Tự động gán voucher cho user dựa trên điều kiện
2. **sp_AssignSpecialEventVouchers**: Gán voucher sự kiện cho tất cả user
3. **sp_UpdateExpiredVouchers**: Cập nhật voucher hết hạn
4. **sp_CalculateCartTotal**: Tính tổng tiền giỏ hàng

### 2.3 Triggers

1. **tr_OrderCreated_AssignVouchers**: Tự động gán voucher khi tạo đơn hàng
2. **tr_UserVoucherUsed_UpdateDiscount**: Cập nhật số lượng voucher đã dùng
3. **tr_CartItemsUpdated_UpdateCartTime**: Cập nhật thời gian giỏ hàng
4. **tr_CartItemsInsert_CheckStock**: Kiểm tra tồn kho khi thêm vào giỏ hàng

---

## 3. Controllers (Servlets)

### 3.1 Authentication & Account
- `login.java` - Đăng nhập
- `signup.java` - Đăng ký
- `logout.java` - Đăng xuất
- `AccountManagementServlet.java` - Quản lý tài khoản
- `ChangePasswordServlet.java` - Đổi mật khẩu
- `PasswordResetRequestServlet.java` - Yêu cầu reset mật khẩu
- `PasswordResetServlet.java` - Reset mật khẩu

### 3.2 Products & Shopping
- `ProductController.java` - Controller sản phẩm (CRUD)
- `productdetail.java` - Chi tiết sản phẩm
- `bosuutap.java` - Bộ sưu tập sản phẩm

### 3.3 Cart Management
- `cart.java` - Hiển thị giỏ hàng
- `addToCart.java` - Thêm sản phẩm vào giỏ hàng
- `removeFromCart.java` - Xóa sản phẩm khỏi giỏ hàng
- `UpdateCartQuantityServlet.java` - Cập nhật số lượng

### 3.4 Checkout & Orders
- `ShippingAddressServlet.java` - Quản lý địa chỉ giao hàng
- `CheckoutServlet.java` - Xử lý checkout
- `BankPaymentServlet.java` - Thanh toán qua ngân hàng
- `OrderHistoryServlet.java` - Lịch sử đơn hàng
- `ApplyPromotionServlet.java` - Áp dụng mã giảm giá

### 3.5 Payment Integration
- `VnPayCreateServlet.java` - Tạo giao dịch VNPay
- `VnPayReturnServlet.java` - Xử lý return từ VNPay
- `VnPayIpnServlet.java` - IPN callback từ VNPay
- `PaymentCallbackServlet.java` - Callback tổng quát

### 3.6 Admin
- `AdminServlet.java` - Controller admin chính (Dashboard, Products, Orders, Users, Categories, Discounts)
- `OrderManagementServlet.java` - Quản lý đơn hàng
- `DiscountController.java` - Quản lý mã giảm giá

### 3.7 Utilities
- `ImageServlet.java` - Phục vụ hình ảnh
- `lienheServlet.java` - Liên hệ

---

## 4. DAO Classes

### 4.1 Core DAOs
- `DBConnect.java` - Kết nối database
- `UserDB.java` - CRUD users, authentication
- `ProductDB.java` - CRUD products, search
- `DistrictDB.java` - Quản lý danh mục
- `OrderDB.java` - CRUD orders, order details
- `CartDB.java` - Quản lý giỏ hàng
- `DiscountDB.java` - Quản lý mã giảm giá
- `ShippingAddressDB.java` - Quản lý địa chỉ giao hàng
- `ShippingMethodDB.java` - Quản lý phương thức giao hàng
- `lienheDAO.java` - Xử lý liên hệ

---

## 5. Model Classes

### 5.1 User Models
- `user.java` - User entity với role support
- `UserDiscountAssign.java` - Voucher đã gán cho user

### 5.2 Product Models
- `Product.java` - Product entity với multiple images
- `Category.java` - Category entity

### 5.3 Order Models
- `Order.java` - Order entity
- `OrderDetail.java` - Order detail item
- `OrderItemSummary.java` - Summary cho UI

### 5.4 Cart Models
- `Cart.java` - Cart entity
- `CartItems.java` - Cart item entity
- `CheckoutItem.java` - Checkout item

### 5.5 Discount Models
- `Discount.java` - Discount/Voucher entity

### 5.6 Shipping Models
- `ShippingAddress.java` - Shipping address entity

### 5.7 Contact
- `lienhe.java` - Contact message entity

---

## 6. View Pages (JSP)

### 6.1 User Views
- `home.jsp` - Trang chủ
- `log.jsp` - Đăng nhập/Đăng ký
- `register.jsp` - Đăng ký
- `account-management.jsp` - Quản lý tài khoản
- `change-password.jsp` - Đổi mật khẩu
- `forgot-password.jsp` - Quên mật khẩu
- `reset-password.jsp` - Reset mật khẩu

### 6.2 Product Views
- `product-detail.jsp` - Chi tiết sản phẩm
- `bosuutap.jsp` - Bộ sưu tập sản phẩm

### 6.3 Shopping Views
- `cart.jsp` - Giỏ hàng
- `checkout.jsp` - Thanh toán
- `shipping-address.jsp` - Địa chỉ giao hàng
- `bank-payment.jsp` - Thanh toán qua ngân hàng
- `order-confirmation.jsp` - Xác nhận đơn hàng
- `my-orders.jsp` - Lịch sử đơn hàng
- `my-discounts.jsp` - Mã giảm giá của tôi

### 6.4 Admin Views
- `admin/dashboard.jsp` - Dashboard
- `admin/manage-products.jsp` - Quản lý sản phẩm
- `admin/manage-orders.jsp` - Quản lý đơn hàng
- `admin/order-detail.jsp` - Chi tiết đơn hàng (admin)
- `admin/manage-users.jsp` - Quản lý người dùng
- `admin/manage-categories.jsp` - Quản lý danh mục
- `admin/manage-discounts.jsp` - Quản lý mã giảm giá
- `admin/reports.jsp` - Báo cáo
- `admin/settings.jsp` - Cài đặt

### 6.5 Utility Views
- `product-form.jsp` - Form sản phẩm (admin)
- `product-manager.jsp` - Quản lý sản phẩm (admin)
- `discount-form.jsp` - Form mã giảm giá
- `discount-manager.jsp` - Quản lý mã giảm giá
- `lienhe.jsp` - Liên hệ
- `vechungtoi.jsp` - Về chúng tôi

### 6.6 Includes
- `includes/header.jspf` - Header
- `includes/footer.jspf` - Footer
- `admin/includes/header.jspf` - Admin header
- `admin/includes/footer.jspf` - Admin footer

---

## 7. Utilities

### 7.1 EmailUtil.java
- Gửi email reset mật khẩu
- Cấu hình trong `email-config.properties`

### 7.2 CartCookieUtil.java
- Quản lý giỏ hàng qua cookie (guest users)

### 7.3 VnPayConfig.java
- Cấu hình tích hợp VNPay
- Tạo URL thanh toán

### 7.4 PaymentClient.java
- Client xử lý payment callbacks

---

## 8. Security & Filters

### 8.1 AdminAuthFilter.java
- Filter bảo vệ các trang admin
- Kiểm tra authentication và role
- Chỉ cho phép ADMIN truy cập

---

## 9. Configuration Files

### 9.1 pom.xml
```xml
- Jakarta EE 11
- SQL Server JDBC Driver 12.4.2
- Jakarta Mail 2.0.1
- JSTL 2.0.0
- JSON 20240303
- Google Auth Library
```

### 9.2 web.xml
- Welcome file: View/home.jsp
- Session timeout: 30 minutes
- Email configuration
- Image servlet mapping
- VNPay servlet mappings

### 9.3 email-config.properties
- Mail host, port
- Username, password
- SMTP configuration

---

## 10. Tính Năng Chính

### 10.1 User Features
✅ Đăng ký/Đăng nhập
✅ Quản lý tài khoản
✅ Xem sản phẩm (chi tiết, bộ sưu tập)
✅ Giỏ hàng (thêm, sửa, xóa)
✅ Checkout (địa chỉ giao hàng, phương thức vận chuyển)
✅ Thanh toán (VNPay, ngân hàng)
✅ Lịch sử đơn hàng
✅ Mã giảm giá (view, apply)
✅ Quên mật khẩu/Reset password
✅ Liên hệ

### 10.2 Admin Features
✅ Dashboard
✅ Quản lý sản phẩm (CRUD, tìm kiếm, upload ảnh)
✅ Quản lý đơn hàng (lọc theo status, ngày, xem chi tiết)
✅ Quản lý người dùng
✅ Quản lý danh mục (CRUD)
✅ Quản lý mã giảm giá (CRUD, auto-assign)
✅ Báo cáo
✅ Cài đặt

### 10.3 Advanced Features
✅ Role-based access control (USER/ADMIN)
✅ Auto-assign vouchers (based on conditions)
✅ Special event vouchers
✅ Multiple product images
✅ Stock management
✅ Shipping methods with cost
✅ VNPay payment integration
✅ Email notifications (password reset)
✅ Session management
✅ Filter-based security

---

## 11. Quy Trình Hoạt Động

### 11.1 User Flow
1. User đăng ký/đăng nhập
2. Browse products → Xem chi tiết
3. Add to cart → Xem giỏ hàng
4. Checkout → Nhập địa chỉ & chọn shipping method
5. Apply discount (optional)
6. Payment (VNPay or Bank)
7. Order confirmation
8. Track orders

### 11.2 Admin Flow
1. Login as admin
2. Access admin panel
3. Manage products/categories/discounts
4. View and manage orders
5. Update order status
6. View reports

### 11.3 Voucher System
1. Admin tạo discount với điều kiện
2. System tự động gán voucher cho user đủ điều kiện
3. User xem voucher trong "My Discounts"
4. Apply voucher khi checkout
5. System validate và apply discount
6. Update voucher status sau khi dùng

---

## 12. Deploy & Run

### 12.1 Requirements
- Java 17+
- Maven 3.6+
- SQL Server
- Jakarta EE compatible server (Tomcat 10+, GlassFish, etc.)

### 12.2 Setup Steps
1. Run `ProjectDB.sql` to create database
2. Update database connection in `DBConnect.java`
3. Configure email in `email-config.properties`
4. Configure VNPay in `VnPayConfig.java`
5. Build: `mvn clean package`
6. Deploy WAR file to server
7. Access: `http://localhost:8080/CosmeticShop/`

### 12.3 Default Admin Account
Tạo admin account bằng SQL:
```sql
INSERT INTO Users (full_name, email, password, role) 
VALUES ('Admin', 'admin@example.com', 'admin123', 'ADMIN');
```

---

## 13. Project Statistics

- **Total Controllers**: 27 servlets
- **Total Models**: 13 entities
- **Total DAOs**: 9 classes
- **Total Views**: 35 JSP pages
- **Database Tables**: 13 tables
- **Stored Procedures**: 4 procedures
- **Triggers**: 4 triggers
- **Lines of Code**: ~15,000+ lines (estimated)

---

## 14. File Naming Conventions

### 14.1 Controllers
- PascalCase: `ProductController.java`, `AdminServlet.java`
- Action-based: `login.java`, `logout.java`, `cart.java`

### 14.2 Models
- PascalCase: `Product.java`, `Order.java`, `user.java`

### 14.3 DAOs
- ClassName + "DB": `ProductDB.java`, `UserDB.java`

### 14.4 Views
- lowercase with hyphens: `product-detail.jsp`, `manage-products.jsp`

---

## 15. Future Enhancements

Suggestions:
- [ ] Add product reviews & ratings
- [ ] Add wishlist/favorites
- [ ] Add social media login (OAuth)
- [ ] Add product comparison
- [ ] Add live chat support
- [ ] Add inventory alerts
- [ ] Add analytics dashboard
- [ ] Add export reports (PDF, Excel)
- [ ] Add mobile app
- [ ] Add multi-language support

---

## 16. Notes

- Database uses NVARCHAR for Vietnamese text support
- Authentication is session-based
- Images stored in IMG folder
- VNPay integration for payment
- Email used for password reset only
- Admin pages protected by filter
- Auto-voucher assignment on order completion

---

**Document Version**: 1.0  
**Last Updated**: 2024  
**Author**: SWP Project Team
