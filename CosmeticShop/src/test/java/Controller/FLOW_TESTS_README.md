# Flow Tests Documentation

Tài liệu này mô tả các test cases cho các luồng (flow) chính của ứng dụng CosmeticShop.

## Tổng Quan

Các flow tests được thiết kế để kiểm tra các luồng nghiệp vụ chính của ứng dụng:
1. **Login Flow** - Đăng nhập người dùng
2. **Add To Cart Flow** - Thêm sản phẩm vào giỏ hàng
3. **Cart Flow** - Xem và quản lý giỏ hàng
4. **Checkout Flow** - Thanh toán và tạo đơn hàng

## 1. Login Flow Tests (`LoginFlowTest.java`)

### Test Cases

#### ✅ doGet Tests
- **testDoGet_ShouldForwardToLoginPage**: Test forward đến trang login

#### ✅ doPost Tests - Success Cases
- **testDoPost_LoginSuccess_ValidCredentials**: Đăng nhập thành công với email và password hợp lệ
- **testDoPost_RememberMe_SetsCookies**: Test remember me - set cookies
- **testDoPost_MergeGuestCart_OnLoginSuccess**: Merge guest cart từ cookie khi login thành công
- **testDoPost_CreateNewCart_IfNotExists**: Tạo cart mới nếu user chưa có cart

#### ❌ doPost Tests - Failure Cases
- **testDoPost_LoginFailure_EmailNotExists**: Email không tồn tại
- **testDoPost_LoginFailure_WrongPassword**: Password sai
- **testDoPost_LoginFailure_InvalidEmailFormat**: Email format không hợp lệ (chỉ chấp nhận @gmail.com)
- **testDoPost_LoginFailure_EmptyFields**: Email hoặc password rỗng

### Flow Diagram

```
User submits login form
    ↓
Validate email format (@gmail.com only)
    ↓
Check if email exists
    ↓
Validate password
    ↓
Create/get user cart
    ↓
Merge guest cart from cookie (if exists)
    ↓
Set user in session
    ↓
Redirect to home page
```

## 2. Add To Cart Flow Tests (`AddToCartFlowTest.java`)

### Test Cases

#### ✅ Success Cases
- **testDoGet_AddProductSuccess_LoggedInUser**: Thêm sản phẩm thành công cho user đã đăng nhập
- **testDoGet_AddProductSuccess_GuestUser**: Thêm sản phẩm thành công cho guest user (lưu vào cookie)
- **testDoGet_AddProduct_UpdateExistingItem**: Cập nhật quantity nếu sản phẩm đã có trong giỏ
- **testDoGet_AddProduct_DefaultQuantity**: Thêm với quantity mặc định = 1
- **testDoGet_GuestUser_AddsToCookieCart**: Guest user thêm vào cookie cart
- **testDoGet_GuestUser_IncrementQuantityInCookie**: Guest user increment quantity trong cookie

#### ❌ Failure Cases
- **testDoGet_AddProductFailure_ProductNotFound**: Sản phẩm không tồn tại
- **testDoGet_AddProductFailure_InvalidProductId**: Product ID không hợp lệ
- **testDoGet_AddProduct_ExceedsStock**: Vượt quá stock - tự động clamp về stock

### Flow Diagram

```
User clicks "Add to Cart"
    ↓
Check if user is logged in
    ├─ YES → Get/Create cart from database
    │         ↓
    │      Check if product exists
    │         ↓
    │      Check if product already in cart
    │         ├─ YES → Update quantity (clamp to stock)
    │         └─ NO → Add new item (clamp to stock)
    │         ↓
    │      Update session cartItems
    │
    └─ NO → Read cookie cart
             ↓
          Increment quantity
             ↓
          Write back to cookie
```

## 3. Cart Flow Tests (`CartFlowTest.java`)

### Test Cases

#### ✅ Display Cart Tests
- **testDoGet_DisplayCart_FromSession_LoggedInUser**: Hiển thị giỏ hàng từ session
- **testDoGet_LoadCart_FromDatabase_WhenSessionEmpty**: Load từ database nếu session rỗng
- **testDoGet_DisplayCart_FromCookie_GuestUser**: Hiển thị giỏ hàng từ cookie cho guest
- **testDoGet_CreateCartItems_FromCookie_WithFullInfo**: Tạo cart items từ cookie với đầy đủ thông tin

#### ✅ Discount Tests
- **testDoGet_LoadAssignedDiscounts_ForLoggedInUser**: Load discounts cho user đã đăng nhập
- **testDoGet_NoDiscounts_ForGuestUser**: Không load discounts cho guest
- **testDoGet_AutoAssignDueDiscounts_BeforeLoading**: Tự động assign discounts trước khi load

#### ✅ Edge Cases
- **testDoGet_SkipNonExistentProducts_InCookieCart**: Bỏ qua sản phẩm không tồn tại

### Flow Diagram

```
User views cart page
    ↓
Check if cartItems in session
    ├─ YES → Display from session
    │
    └─ NO → Check if user is logged in
             ├─ YES → Load from database
             │         ↓
             │      Auto-assign due discounts
             │         ↓
             │      Load assigned discounts
             │
             └─ NO → Read from cookie
                      ↓
                   Build cart items from products
                      ↓
                   Display cart
```

## 4. Checkout Flow Tests (`CheckoutFlowTest.java`)

### Test Cases

#### ✅ doGet Tests
- **testDoGet_RedirectToLogin_WhenNotLoggedIn**: Redirect nếu chưa đăng nhập
- **testDoGet_DisplayCheckoutPage_WithFullInfo**: Hiển thị checkout page với đầy đủ thông tin
- **testDoGet_ApplyDiscount_FromSession**: Áp dụng discount từ session

#### ✅ doPost Tests - Success Cases
- **testDoPost_CreateOrderSuccess_WithCOD**: Tạo đơn hàng thành công với COD
- **testDoPost_CreateOrderSuccess_WithVNPay**: Tạo đơn hàng thành công với VNPay
- **testDoPost_ApplyDiscount_AndMarkAsUsed**: Áp dụng discount và đánh dấu đã sử dụng
- **testDoPost_CalculateTotal_WithShippingCost**: Tính tổng tiền với shipping cost

#### ❌ doPost Tests - Failure Cases
- **testDoPost_RedirectToLogin_WhenNotLoggedIn**: Redirect nếu chưa đăng nhập
- **testDoPost_RedirectToCart_WhenNoItemsSelected**: Redirect nếu không có items được chọn
- **testDoPost_Redirect_WhenNoShippingAddress**: Redirect nếu không có địa chỉ giao hàng
- **testDoPost_Redirect_WhenOutOfStock**: Redirect nếu hết hàng
- **testDoPost_RestoreStock_WhenOrderCreationFails**: Hoàn lại kho nếu tạo đơn thất bại

#### ✅ Business Logic Tests
- **testDoPost_DecreaseStock_WhenCreatingOrder**: Trừ kho khi tạo đơn hàng

### Flow Diagram

```
User clicks "Checkout"
    ↓
Check if logged in
    ├─ NO → Redirect to login
    │
    └─ YES → Get selected cart items
             ↓
          Check if items exist
             ├─ NO → Redirect to cart
             │
             └─ YES → Get shipping address & method
                      ↓
                   Calculate totals (items - discount + shipping)
                      ↓
                   Decrease stock for each item
                      ↓
                   Create order
                      ↓
                   Add order details
                      ↓
                   Mark discount as used (if applied)
                      ↓
                   Clear selected cart items
                      ↓
                   Redirect based on payment method
                      ├─ COD → Order detail page
                      └─ VNPay → VNPay payment page
```

## End-to-End Flow Test

### Complete Shopping Flow

```
1. Guest User
   ↓
2. Browse products
   ↓
3. Add products to cart (stored in cookie)
   ↓
4. View cart (from cookie)
   ↓
5. Login
   ↓
6. Guest cart merged to user cart
   ↓
7. View cart (from database)
   ↓
8. Apply discount (if available)
   ↓
9. Checkout
   ↓
10. Select shipping address & method
    ↓
11. Create order
    ↓
12. Payment (COD or VNPay)
    ↓
13. Order confirmation
```

## Chạy Tests

### Chạy tất cả flow tests:
```bash
mvn test -Dtest=*FlowTest
```

### Chạy một flow test cụ thể:
```bash
mvn test -Dtest=LoginFlowTest
mvn test -Dtest=AddToCartFlowTest
mvn test -Dtest=CartFlowTest
mvn test -Dtest=CheckoutFlowTest
```

## Lưu Ý

1. **Database Dependency**: Nhiều tests yêu cầu database connection thực tế. Các tests này được đánh dấu với comment `// Note: Requires actual database connection`.

2. **Mocking Limitations**: Một số tests không thể chạy đầy đủ vì các DAO classes không được inject như dependencies. Để test đầy đủ, cần:
   - Refactor để inject dependencies (Dependency Injection)
   - Hoặc sử dụng integration tests với test database
   - Hoặc sử dụng Testcontainers

3. **Integration Tests**: Để chạy integration tests đầy đủ:
   - Setup test database
   - Cấu hình test environment
   - Chạy tests với `@Disabled` annotation removed

## Best Practices

1. **Test Isolation**: Mỗi test phải độc lập, không phụ thuộc vào test khác
2. **Arrange-Act-Assert**: Tổ chức test code theo pattern AAA
3. **Mock External Dependencies**: Sử dụng Mockito để mock HttpServletRequest, HttpServletResponse, Session
4. **Test Both Success and Failure**: Test cả success cases và failure cases
5. **Edge Cases**: Test các edge cases như null values, empty collections, invalid inputs

## TODO

- [ ] Refactor controllers để support dependency injection
- [ ] Setup test database hoặc Testcontainers
- [ ] Enable và hoàn thiện các tests đang bị disable
- [ ] Thêm tests cho payment callbacks (VNPay return, IPN)
- [ ] Thêm tests cho order history flow
- [ ] Thêm tests cho discount application flow
- [ ] Thêm performance tests cho các flow chính



