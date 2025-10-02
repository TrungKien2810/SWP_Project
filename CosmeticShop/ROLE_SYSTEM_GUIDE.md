# Hướng dẫn hệ thống Role trong CosmeticShop

## Tổng quan
Hệ thống đã được cập nhật để hỗ trợ phân quyền user với 2 role chính:
- **USER**: Người dùng thông thường
- **ADMIN**: Quản trị viên có quyền quản lý sản phẩm

## Các thay đổi đã thực hiện

### 1. Model User
- Thêm trường `role` vào class `user`
- Constructor được cập nhật để nhận tham số `role`
- Thêm getter/setter cho trường `role`
- Mặc định role là "USER" nếu không được chỉ định

### 2. Database
- Cần chạy script SQL để thêm cột `role` vào bảng `Users`
- Script được lưu trong file `database_update.sql`

### 3. UserDB DAO
- Cập nhật method `getUserByEmail()` để lấy thông tin role
- Cập nhật method `signup()` để set role mặc định là "USER"

### 4. Giao diện
- Menu "QUẢN LÝ SẢN PHẨM" chỉ hiển thị cho admin
- Trang quản lý sản phẩm có kiểm tra quyền admin
- Hiển thị thông báo lỗi nếu user không có quyền

## Cách sử dụng

### 1. Cập nhật Database
```sql
-- Chạy script này trong database
ALTER TABLE Users ADD COLUMN role VARCHAR(20) DEFAULT 'user';
UPDATE Users SET role = 'user' WHERE role IS NULL;
```

### 2. Tạo tài khoản Admin
```sql
-- Cách 1: Tạo admin mới
INSERT INTO Users (full_name, email, password, role) 
VALUES ('Admin', 'admin@pinkycloud.com', 'admin123', 'ADMIN');

-- Cách 2: Cập nhật user hiện có thành admin
UPDATE Users SET role = 'ADMIN' WHERE email = 'your-email@gmail.com';
```

### 3. Kiểm tra quyền trong JSP
```jsp
<!-- Chỉ hiển thị cho admin -->
<c:if test="${not empty sessionScope.user && sessionScope.user.role == 'ADMIN'}">
    <li><a href="product-manager.jsp">QUẢN LÝ SẢN PHẨM</a></li>
</c:if>
```

## Tính năng bảo mật

1. **Kiểm tra đăng nhập**: User phải đăng nhập
2. **Kiểm tra role**: Chỉ admin mới thấy menu quản lý
3. **Bảo vệ trang**: Trang quản lý kiểm tra quyền admin
4. **Thông báo lỗi**: Hiển thị thông báo nếu không có quyền

## Lưu ý quan trọng

- Mặc định tất cả user mới đăng ký sẽ có role "USER"
- Chỉ admin mới có thể truy cập trang quản lý sản phẩm
- Cần cập nhật database trước khi sử dụng tính năng mới
- Script SQL được lưu trong file `database_update.sql`
