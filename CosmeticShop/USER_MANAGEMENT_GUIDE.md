# Hướng dẫn sử dụng chức năng Quản lý Người dùng

## Tổng quan
Chức năng quản lý người dùng cho phép admin thực hiện các thao tác CRUD (Create, Read, Update, Delete) trên tài khoản người dùng trong hệ thống.

## Các tính năng chính

### 1. Xem danh sách người dùng
- Hiển thị tất cả người dùng trong hệ thống
- Thống kê tổng quan: tổng người dùng, admin, user thường, người dùng mới trong tháng
- Hiển thị thông tin: tên, email, vai trò, ngày tạo, trạng thái

### 2. Tìm kiếm và lọc
- **Tìm kiếm**: Nhập tên hoặc email để tìm kiếm người dùng
- **Lọc theo vai trò**: 
  - Tất cả
  - Admin
  - User
- Tìm kiếm tự động submit khi nhập từ 3 ký tự trở lên

### 3. Chỉnh sửa thông tin người dùng
- Click nút "Chỉnh sửa" (biểu tượng bút) trên dòng người dùng
- Modal form sẽ hiển thị với thông tin hiện tại
- Có thể chỉnh sửa:
  - Họ và tên
  - Email (kiểm tra trùng lặp)
  - Số điện thoại
  - Vai trò (USER/ADMIN)

### 4. Xóa người dùng
- Click nút "Xóa" (biểu tượng thùng rác) trên dòng người dùng
- Modal xác nhận sẽ hiển thị
- **Lưu ý**: Không thể xóa chính mình
- Hành động không thể hoàn tác

## Cách sử dụng

### Truy cập trang quản lý
1. Đăng nhập với tài khoản có role "ADMIN"
2. Click vào menu "ADMIN DASHBOARD" 
3. Chọn "Quản lý người dùng"

### Tìm kiếm người dùng
1. Nhập từ khóa vào ô tìm kiếm
2. Hệ thống sẽ tự động tìm kiếm khi nhập từ 3 ký tự
3. Để xóa bộ lọc, xóa hết nội dung ô tìm kiếm

### Lọc theo vai trò
1. Click vào các nút "Tất cả", "Admin", "User"
2. Hệ thống sẽ hiển thị người dùng theo vai trò được chọn

### Chỉnh sửa người dùng
1. Click nút "Chỉnh sửa" trên dòng người dùng muốn sửa
2. Điền thông tin mới trong form
3. Click "Cập nhật" để lưu thay đổi
4. Click "Hủy" để đóng form

### Xóa người dùng
1. Click nút "Xóa" trên dòng người dùng muốn xóa
2. Xác nhận trong modal hiển thị
3. Click "Xóa" để thực hiện xóa
4. Click "Hủy" để hủy thao tác

## Bảo mật

### Kiểm tra quyền truy cập
- Chỉ user có role "ADMIN" mới có thể truy cập
- Nếu không có quyền, sẽ được chuyển về trang đăng nhập

### Bảo vệ dữ liệu
- Không thể xóa chính mình
- Không thể thay đổi vai trò của chính mình
- Kiểm tra email trùng lặp khi cập nhật

## Thông báo hệ thống

### Thông báo thành công
- Màu xanh lá
- Hiển thị ở góc phải màn hình
- Tự động ẩn sau 5 giây

### Thông báo lỗi
- Màu đỏ
- Hiển thị ở góc phải màn hình
- Tự động ẩn sau 5 giây

## Các lỗi thường gặp

### "Không thể xóa chính mình"
- **Nguyên nhân**: Admin cố gắng xóa tài khoản của chính mình
- **Giải pháp**: Không thể thực hiện, đây là tính năng bảo mật

### "Email đã được sử dụng bởi người dùng khác"
- **Nguyên nhân**: Email đã tồn tại trong hệ thống
- **Giải pháp**: Sử dụng email khác hoặc kiểm tra lại thông tin

### "Bạn không có quyền truy cập trang này"
- **Nguyên nhân**: Tài khoản không có role ADMIN
- **Giải pháp**: Đăng nhập với tài khoản admin

## API Endpoints

### GET /admin-users
- **Mục đích**: Hiển thị danh sách người dùng
- **Tham số**:
  - `search`: Từ khóa tìm kiếm
  - `role`: Lọc theo vai trò (ADMIN/USER)
- **Response**: Trang admin-users.jsp với danh sách người dùng

### POST /admin-users
- **Mục đích**: Xử lý các thao tác CRUD
- **Tham số**:
  - `action`: Loại thao tác (updateUser, delete, updateRole)
  - `userId`: ID người dùng
  - `fullName`: Họ tên (cho updateUser)
  - `email`: Email (cho updateUser)
  - `phone`: Số điện thoại (cho updateUser)
  - `role`: Vai trò (cho updateUser)
  - `newRole`: Vai trò mới (cho updateRole)

## Database Schema

### Bảng Users
```sql
- user_id (int, primary key)
- full_name (varchar) - Tên đầy đủ
- email (varchar, unique) - Email
- phone (varchar) - Số điện thoại
- password (varchar) - Mật khẩu (đã hash)
- role (varchar) - Vai trò (USER/ADMIN)
- created_at (datetime) - Ngày tạo
```

## Cải tiến trong tương lai

### Tính năng có thể thêm
1. **Phân trang**: Hiển thị người dùng theo trang
2. **Sắp xếp**: Sắp xếp theo tên, email, ngày tạo
3. **Xuất dữ liệu**: Export danh sách người dùng ra Excel/CSV
4. **Khóa tài khoản**: Tạm khóa/mở khóa tài khoản
5. **Lịch sử hoạt động**: Theo dõi các thao tác của người dùng
6. **Thêm người dùng mới**: Form thêm người dùng từ admin panel

### Cải tiến UI/UX
1. **Responsive design**: Tối ưu cho mobile
2. **Loading states**: Hiển thị trạng thái loading
3. **Bulk operations**: Thao tác hàng loạt
4. **Advanced filters**: Bộ lọc nâng cao
5. **Real-time updates**: Cập nhật real-time khi có thay đổi
