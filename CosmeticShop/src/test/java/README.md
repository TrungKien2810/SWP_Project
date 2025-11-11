# Test Package - CosmeticShop

## Cấu trúc Test Package

Package test được tổ chức theo cấu trúc của source code chính:

```
src/test/java/
├── Model/          # Tests cho các Model classes
│   ├── ProductTest.java
│   ├── UserTest.java
│   └── OrderTest.java
├── DAO/            # Tests cho các DAO classes
│   ├── ProductDBTest.java
│   └── UserDBTest.java
├── Controller/     # Tests cho các Servlet/Controller
│   └── LoginServletTest.java
└── Util/           # Tests cho các Utility classes
    └── CartCookieUtilTest.java
```

## Dependencies

Project sử dụng các testing frameworks sau:
- **JUnit 5** - Testing framework chính
- **Mockito** - Mocking framework cho unit tests
- **AssertJ** - Fluent assertions library
- **Hamcrest** - Matcher library

## Chạy Tests

### Chạy tất cả tests:
```bash
mvn test
```

### Chạy tests cho một package cụ thể:
```bash
mvn test -Dtest=Model.*
```

### Chạy một test class cụ thể:
```bash
mvn test -Dtest=ProductTest
```

### Chạy một test method cụ thể:
```bash
mvn test -Dtest=ProductTest#testConstructorWithSingleImage
```

## Test Categories

### 1. Model Tests
- Test các getter/setter methods
- Test constructors
- Test business logic trong models
- Test validation logic

### 2. DAO Tests
- **Lưu ý**: Các DAO tests hiện tại được đánh dấu `@Disabled` vì yêu cầu database connection
- Để chạy integration tests:
  - Cấu hình test database
  - Hoặc sử dụng Testcontainers
  - Hoặc sử dụng in-memory database (H2, HSQLDB)

### 3. Controller Tests
- Test servlet request/response handling
- Test session management
- Test redirects và forwards
- Sử dụng Mockito để mock HttpServletRequest, HttpServletResponse

### 4. Util Tests
- Test utility methods
- Test edge cases
- Test error handling

## Best Practices

1. **Test Naming**: Sử dụng `@DisplayName` để mô tả rõ ràng test case
2. **Arrange-Act-Assert**: Tổ chức test code theo pattern AAA
3. **Isolation**: Mỗi test phải độc lập, không phụ thuộc vào test khác
4. **Mocking**: Sử dụng Mockito cho external dependencies
5. **Assertions**: Sử dụng AssertJ cho assertions dễ đọc

## Ví dụ Test

```java
@Test
@DisplayName("Test constructor với single image")
void testConstructorWithSingleImage() {
    // Arrange
    Product product = new Product(1, "Test", 100000.0, 10, 
            "Description", "test.jpg", 1);
    
    // Act & Assert
    assertThat(product.getProductId()).isEqualTo(1);
    assertThat(product.getName()).isEqualTo("Test");
}
```

## TODO

- [ ] Thêm integration tests cho DAO với test database
- [ ] Thêm tests cho các Controller còn lại
- [ ] Thêm tests cho Filter (AdminAuthFilter)
- [ ] Thêm tests cho EmailUtil
- [ ] Thêm tests cho PaymentClient
- [ ] Setup Testcontainers cho database tests
- [ ] Thêm code coverage reporting (JaCoCo)

## Notes

- Một số tests được đánh dấu `@Disabled` vì yêu cầu database connection hoặc external services
- Để enable các tests này, cần cấu hình test environment phù hợp



