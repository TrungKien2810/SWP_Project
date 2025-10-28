# 🔥 CosmeticShop - Website Bán Mỹ Phẩm Trực Tuyến

## 📋 Mô Tả

CosmeticShop là hệ thống website bán mỹ phẩm trực tuyến được xây dựng bằng **Java** với công nghệ **Jakarta EE 11** và **Maven**. Hệ thống hỗ trợ đầy đủ các chức năng từ mua sắm, thanh toán, quản lý đơn hàng cho đến quản trị admin.

## 🛠️ Công Nghệ

- **Backend**: Java 17, Jakarta Servlets & JSP
- **Database**: Microsoft SQL Server
- **Frontend**: HTML, CSS, JavaScript, Bootstrap
- **Build Tool**: Maven
- **Payment**: VNPay integration
- **Email**: Jakarta Mail

## 📂 Cấu Trúc Dự Án

```
CosmeticShop/
├── src/main/
│   ├── java/
│   │   ├── Controller/     # 27 servlets xử lý request
│   │   ├── DAO/            # 9 classes truy cập database
│   │   ├── Model/          # 13 entities (data models)
│   │   ├── Util/           # 4 utilities (helpers)
│   │   └── Filter/         # Security filter
│   ├── webapp/
│   │   ├── View/           # 24 trang JSP người dùng
│   │   ├── admin/          # 11 trang JSP admin
│   │   ├── Css/            # Stylesheets
│   │   ├── IMG/            # Hình ảnh
│   │   └── WEB-INF/        # Config files
│   └── resources/          # Resources
├── ProjectDB.sql           # Script tạo database
├── pom.xml                 # Maven configuration
└── PROJECT_DOCUMENTATION.md # Tài liệu chi tiết
```

## 🎯 Tính Năng Chính

### 👤 Người Dùng
- ✅ Đăng ký/Đăng nhập tài khoản
- ✅ Duyệt và tìm kiếm sản phẩm
- ✅ Xem chi tiết sản phẩm (nhiều ảnh)
- ✅ Quản lý giỏ hàng
- ✅ Thanh toán (VNPay, Ngân hàng)
- ✅ Xem lịch sử đơn hàng
- ✅ Quản lý địa chỉ giao hàng
- ✅ Áp dụng mã giảm giá
- ✅ Quên mật khẩu/Reset password
- ✅ Quản lý thông tin tài khoản

### 👨‍💼 Admin
- ✅ Dashboard tổng quan
- ✅ Quản lý sản phẩm (CRUD, tìm kiếm)
- ✅ Quản lý danh mục sản phẩm
- ✅ Quản lý đơn hàng (lọc, xem chi tiết)
- ✅ Quản lý người dùng
- ✅ Quản lý mã giảm giá
- ✅ Tạo báo cáo
- ✅ Quản lý tồn kho

### 🔐 Bảo Mật
- ✅ Phân quyền USER/ADMIN
- ✅ Session management
- ✅ Filter bảo vệ admin pages
- ✅ Password reset qua email

## 🗄️ Database Schema

### Bảng Chính
- **Users** - Người dùng (role: USER/ADMIN)
- **Products** - Sản phẩm
- **Categories** - Danh mục
- **Orders** - Đơn hàng
- **OrderDetails** - Chi tiết đơn hàng
- **Carts** - Giỏ hàng
- **CartItems** - Chi tiết giỏ hàng
- **Discounts** - Mã giảm giá
- **UserVouchers** - Voucher của người dùng
- **ShippingAddresses** - Địa chỉ giao hàng
- **ShippingMethods** - Phương thức giao hàng

### Stored Procedures
1. `sp_CheckAndAssignVouchers` - Tự động gán voucher
2. `sp_AssignSpecialEventVouchers` - Gán voucher sự kiện
3. `sp_UpdateExpiredVouchers` - Cập nhật voucher hết hạn
4. `sp_CalculateCartTotal` - Tính tổng giỏ hàng

### Triggers
1. `tr_OrderCreated_AssignVouchers` - Gán voucher khi tạo đơn
2. `tr_UserVoucherUsed_UpdateDiscount` - Cập nhật số lượng dùng
3. `tr_CartItemsUpdated_UpdateCartTime` - Cập nhật thời gian giỏ hàng
4. `tr_CartItemsInsert_CheckStock` - Kiểm tra tồn kho

## 🚀 Cài Đặt & Chạy

### Yêu Cầu
- Java 17+
- Maven 3.6+
- SQL Server
- Jakarta EE Server (Tomcat 10+)

### Các Bước

1. **Clone project**
```bash
git clone [repository-url]
cd SWP_Project-1/CosmeticShop
```

2. **Setup Database**
- Mở SQL Server Management Studio
- Chạy file `ProjectDB.sql` để tạo database và tables

3. **Cấu Hình Database**
- Mở file `DAO/DBConnect.java`
- Cập nhật thông tin kết nối database:
  ```java
  String url = "jdbc:sqlserver://localhost:1433;databaseName=your_db_name";
  String user = "your_username";
  String pass = "your_password";
  ```

4. **Cấu Hình Email** (optional)
- Mở file `email-config.properties`
- Cập nhật thông tin SMTP

5. **Cấu Hình VNPay** (optional)
- Mở file `Util/VnPayConfig.java`
- Cập nhật VNPay credentials

6. **Build Project**
```bash
mvn clean package
```

7. **Deploy**
- Copy file `.war` từ `target/` đến thư mục webapps của Tomcat
- Hoặc sử dụng IDE để chạy trực tiếp

8. **Truy Cập**
- User: `http://localhost:8080/CosmeticShop/`
- Admin: `http://localhost:8080/CosmeticShop/admin`

### Tạo Tài Khoản Admin
```sql
INSERT INTO Users (full_name, email, password, role) 
VALUES ('Admin', 'admin@example.com', 'admin123', 'ADMIN');
```

## 📝 Controllers

### Authentication
- `login.java` - Đăng nhập
- `signup.java` - Đăng ký
- `logout.java` - Đăng xuất
- `PasswordResetRequestServlet.java` - Yêu cầu reset
- `PasswordResetServlet.java` - Reset mật khẩu

### Products & Shopping
- `ProductController.java` - CRUD sản phẩm (admin)
- `productdetail.java` - Chi tiết sản phẩm
- `cart.java` - Giỏ hàng
- `addToCart.java` - Thêm vào giỏ
- `removeFromCart.java` - Xóa khỏi giỏ

### Orders & Payment
- `CheckoutServlet.java` - Thanh toán
- `BankPaymentServlet.java` - Thanh toán ngân hàng
- `VnPayCreateServlet.java` - Tạo giao dịch VNPay
- `VnPayReturnServlet.java` - Xử lý VNPay return
- `OrderHistoryServlet.java` - Lịch sử đơn hàng

### Admin
- `AdminServlet.java` - Controller admin chính
- `OrderManagementServlet.java` - Quản lý đơn hàng
- `DiscountController.java` - Quản lý mã giảm giá

## 🎨 Pages

### User Pages
- `home.jsp` - Trang chủ
- `log.jsp` - Đăng nhập/Đăng ký
- `product-detail.jsp` - Chi tiết sản phẩm
- `cart.jsp` - Giỏ hàng
- `checkout.jsp` - Thanh toán
- `my-orders.jsp` - Lịch sử đơn hàng
- `my-discounts.jsp` - Mã giảm giá của tôi

### Admin Pages
- `admin/dashboard.jsp` - Dashboard
- `admin/manage-products.jsp` - Quản lý sản phẩm
- `admin/manage-orders.jsp` - Quản lý đơn hàng
- `admin/manage-users.jsp` - Quản lý người dùng
- `admin/manage-categories.jsp` - Quản lý danh mục
- `admin/manage-discounts.jsp` - Quản lý mã giảm giá

## 🔑 Quy Trình Hoạt Động

### User Flow
1. Đăng ký/Đăng nhập
2. Duyệt sản phẩm → Xem chi tiết
3. Thêm vào giỏ hàng
4. Checkout → Nhập địa chỉ
5. Chọn phương thức vận chuyển
6. Áp dụng mã giảm giá (nếu có)
7. Thanh toán
8. Xác nhận đơn hàng
9. Theo dõi đơn hàng

### Admin Flow
1. Đăng nhập với role ADMIN
2. Truy cập admin panel
3. Quản lý sản phẩm/danh mục
4. Xem và xử lý đơn hàng
5. Tạo và quản lý mã giảm giá
6. Xem báo cáo

## 📊 Thống Kê Dự Án

- **Controllers**: 27 servlets
- **Models**: 13 entities
- **DAOs**: 9 classes
- **Views**: 35 JSP pages
- **Database Tables**: 13 tables
- **Stored Procedures**: 4
- **Triggers**: 4

## 🐛 Xử Lý Lỗi

### Database Connection
- Kiểm tra SQL Server đã chạy chưa
- Kiểm tra credentials trong `DBConnect.java`

### Build Errors
```bash
mvn clean install -U
```

### Deployment
- Đảm bảo Tomcat version tương thích (10+)
- Check port conflicts

## 📚 Tài Liệu

- Xem file `PROJECT_DOCUMENTATION.md` để biết chi tiết đầy đủ
- Xem file `ROLE_SYSTEM_GUIDE.md` để hiểu về hệ thống phân quyền

## 👥 Authors

SWP Project Team

## 📄 License

This project is for educational purposes.

---

**Phiên bản**: 1.0  
**Cập nhật**: 2024
