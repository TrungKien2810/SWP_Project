# Hướng Dẫn Chạy Flow Tests Theo Trình Tự

## Tổng Quan

Các flow tests đã được cấu hình để chạy theo trình tự nghiệp vụ logic:

1. **Login Flow** (Order: 1) - Đăng nhập người dùng
2. **Add To Cart Flow** (Order: 2) - Thêm sản phẩm vào giỏ hàng
3. **Cart Flow** (Order: 3) - Xem và quản lý giỏ hàng
4. **Checkout Flow** (Order: 4) - Thanh toán và tạo đơn hàng

## Cách Chạy

### 1. Chạy Tất Cả Flow Tests Theo Trình Tự (Khuyến Nghị)

Sử dụng Test Suite để chạy tất cả flow tests theo thứ tự:

```bash
mvn test -Dtest=ShoppingFlowTestSuite
```

Hoặc chạy trực tiếp test suite class:

```bash
mvn test -Dtest=Controller.ShoppingFlowTestSuite
```

### 2. Chạy Từng Flow Test Theo Thứ Tự

```bash
# Bước 1: Login Flow
mvn test -Dtest=LoginFlowTest

# Bước 2: Add To Cart Flow
mvn test -Dtest=AddToCartFlowTest

# Bước 3: Cart Flow
mvn test -Dtest=CartFlowTest

# Bước 4: Checkout Flow
mvn test -Dtest=CheckoutFlowTest
```

### 3. Chạy Tất Cả Flow Tests (Theo Thứ Tự Tự Động)

```bash
mvn test -Dtest=*FlowTest
```

JUnit 5 sẽ tự động sắp xếp các test classes theo `@Order` annotation.

### 4. Chạy Từng Test Method Trong Flow

```bash
# Chạy một test method cụ thể
mvn test -Dtest=LoginFlowTest#testDoPost_LoginFailure_InvalidEmailFormat

# Chạy nhiều test methods
mvn test -Dtest=LoginFlowTest#testDoGet_ShouldForwardToLoginPage,LoginFlowTest#testDoPost_LoginFailure_InvalidEmailFormat
```

## Cấu Hình Order

Các test classes đã được đánh dấu với `@Order` annotation:

```java
@Order(1)  // LoginFlowTest - Chạy đầu tiên
@Order(2)  // AddToCartFlowTest - Chạy thứ hai
@Order(3)  // CartFlowTest - Chạy thứ ba
@Order(4)  // CheckoutFlowTest - Chạy cuối cùng
```

## Test Suite

File `ShoppingFlowTestSuite.java` đã được tạo để chạy tất cả flow tests theo thứ tự:

```java
@Suite
@SelectClasses({
    LoginFlowTest.class,      // Order 1
    AddToCartFlowTest.class,  // Order 2
    CartFlowTest.class,       // Order 3
    CheckoutFlowTest.class    // Order 4
})
```

## Lưu Ý

1. **Test Isolation**: Mỗi test class vẫn độc lập, không phụ thuộc vào test class khác
2. **Order Guarantee**: `@Order` chỉ đảm bảo thứ tự chạy giữa các test classes, không đảm bảo thứ tự giữa các test methods trong cùng class
3. **Parallel Execution**: Nếu chạy song song, order có thể không được đảm bảo. Để đảm bảo order, chạy tuần tự:
   ```xml
   <configuration>
       <parallel>none</parallel>
   </configuration>
   ```

## Best Practices

1. **Chạy Test Suite**: Luôn chạy `ShoppingFlowTestSuite` để đảm bảo flow đầy đủ
2. **CI/CD**: Trong CI/CD pipeline, chạy test suite để đảm bảo toàn bộ flow hoạt động
3. **Development**: Khi phát triển, có thể chạy từng flow test riêng lẻ để debug nhanh hơn

## Ví Dụ Workflow

```bash
# Development workflow
# 1. Chạy test suite để kiểm tra toàn bộ flow
mvn test -Dtest=ShoppingFlowTestSuite

# 2. Nếu có lỗi, chạy từng flow để debug
mvn test -Dtest=LoginFlowTest
mvn test -Dtest=AddToCartFlowTest

# 3. Sau khi sửa, chạy lại test suite
mvn test -Dtest=ShoppingFlowTestSuite
```

## Troubleshooting

### Test không chạy theo thứ tự

- Kiểm tra `@Order` annotation đã được thêm vào các test classes
- Đảm bảo `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` đã được thêm
- Kiểm tra JUnit 5 version (>= 5.4.0)

### Test Suite không chạy

- Kiểm tra dependency `junit-platform-suite` đã được thêm vào `pom.xml`
- Đảm bảo `@Suite` annotation được import từ `org.junit.platform.suite.api`



