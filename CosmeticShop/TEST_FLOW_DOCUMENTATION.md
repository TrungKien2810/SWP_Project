# 🔄 Tài Liệu Chi Tiết Luồng Hoạt Động Test

## Mục Lục
1. [Tổng Quan Kiến Trúc Test](#tổng-quan-kiến-trúc-test)
2. [UC-002: Login Flow Tests](#uc-002-login-flow-tests)
3. [UC-005: Add To Cart Flow Tests](#uc-005-add-to-cart-flow-tests)
4. [UC-009: Apply Promotion Flow Tests](#uc-009-apply-promotion-flow-tests)
5. [Mocking Strategy](#mocking-strategy)
6. [Test Data Flow](#test-data-flow)

---

## Tổng Quan Kiến Trúc Test

### Test Framework Stack
- **JUnit 5**: Testing framework chính
- **Mockito**: Mocking framework cho dependencies
- **AssertJ**: Fluent assertions
- **JaCoCo**: Code coverage tool

### Test Pattern: Arrange-Act-Assert (AAA)

Tất cả test cases tuân theo pattern AAA:

```java
@Test
void testExample() {
    // Arrange: Setup test data và mocks
    when(request.getParameter("id")).thenReturn("1");
    
    // Act: Thực thi code cần test
    servlet.doGet(request, response);
    
    // Assert: Kiểm tra kết quả
    verify(response).sendRedirect("/shop/cart");
}
```

---

## UC-002: Login Flow Tests

### Class: `LoginFlowTest.java`

### Test Cases

#### 1. `shouldForwardToLoginPageOnGet()`
**Mục đích**: Test GET request đến `/login` forward đến trang đăng nhập

**Luồng hoạt động**:
```
1. User truy cập /login (GET)
2. Servlet nhận request
3. Servlet forward đến /View/log.jsp
```

**Mock Setup**:
- `request.getRequestDispatcher("/View/log.jsp")` → trả về dispatcher

**Assertions**:
- Verify dispatcher.forward() được gọi

---

#### 2. `shouldRejectInvalidEmailFormat()`
**Mục đích**: Test validation email format (phải là Gmail)

**Luồng hoạt động**:
```
1. User submit form với email không hợp lệ: "invalid-email"
2. Servlet validate email format
3. Email không match pattern ^[A-Za-z0-9._%+-]+@gmail\.com$
4. Servlet set error message vào session
5. Redirect về /login
```

**Mock Setup**:
- `request.getParameter("email")` → "invalid-email"
- `request.getParameter("password")` → "pass"
- `request.getSession()` → session mock

**Assertions**:
- `session.setAttribute("loginErrorMsg", "Email không hợp lệ! Vui lòng nhập địa chỉ Gmail.")`
- `response.sendRedirect("/app/login")`

---

#### 3. `shouldRejectEmptyCredentials()`
**Mục đích**: Test validation trường rỗng

**Luồng hoạt động**:
```
1. User submit form với email hoặc password rỗng
2. Servlet kiểm tra: email.equals("") || password.equals("")
3. Nếu rỗng → set error và redirect
```

**Mock Setup**:
- `request.getParameter("email")` → "test@gmail.com"
- `request.getParameter("password")` → "" (rỗng)

**Assertions**:
- `session.setAttribute("loginErrorMsg", "Vui lòng nhập đầy đủ email và mật khẩu!")`

---

#### 4. `shouldNotifyWhenUserNotFound()`
**Mục đích**: Test trường hợp email chưa đăng ký

**Luồng hoạt động**:
```
1. User submit email: "missing@gmail.com"
2. Servlet gọi UserDB.getUserByEmail()
3. UserDB trả về null (user không tồn tại)
4. Servlet set error và redirect
```

**Mock Setup**:
- `MockedConstruction<UserDB>`:
  - `getUserByEmail("missing@gmail.com")` → null

**Assertions**:
- `session.setAttribute("loginErrorMsg", "Tài khoản không tồn tại. Bạn đã đăng ký chưa?")`

---

#### 5. `shouldMergeGuestCartOnSuccessfulLogin()` ⭐
**Mục đích**: Test merge giỏ hàng guest vào cart user khi đăng nhập thành công

**Luồng hoạt động**:
```
1. User có guest cart trong cookie: "42:3" (product 42, quantity 3)
2. User đăng nhập thành công
3. Servlet lấy cart của user (hoặc tạo mới)
4. Servlet đọc guest cart từ cookie
5. Servlet merge: thêm product 42, quantity 3 vào user cart
6. Servlet xóa guest cart cookie
7. Servlet reload cart items và set vào session
8. Redirect đến home
```

**Mock Setup**:
- `MockedConstruction<UserDB>`:
  - `getUserByEmail()` → user object
  - `login()` → true
- `MockedConstruction<CartDB>`:
  - `getCartByUserId()` → null (lần 1), cart (lần 2)
  - `getCartItemsByCartId()` → empty list, merged items
  - `addNewCart()` → tạo cart mới
  - `addCartItems()` → thêm item từ guest cart
- `MockedConstruction<ProductDB>`:
  - `getProductById(42)` → product object
- Cookie: `guest_cart=42:3`

**Assertions**:
- `cartDB.addNewCart()` được gọi
- `cartDB.addCartItems(cartId, 42, 3, price)` được gọi
- `session.setAttribute("user", user)`
- `session.setAttribute("cartItems", mergedItems)`
- Cookie `guest_cart` được xóa (maxAge = 0)
- `response.sendRedirect("/shop/View/home.jsp")`

**Data Flow**:
```
Cookie: "42:3"
    ↓
CartCookieUtil.readCartMap() → Map{42: 3}
    ↓
ProductDB.getProductById(42) → Product{id:42, price:200000}
    ↓
CartDB.addCartItems(cartId, 42, 3, 200000)
    ↓
CartDB.getCartItemsByCartId() → List[CartItems{productId:42, qty:3}]
    ↓
Session: cartItems = merged items
```

---

#### 6. `shouldSetRememberMeCookies()`
**Mục đích**: Test lưu cookie "remember me"

**Luồng hoạt động**:
```
1. User check "Remember me" và đăng nhập
2. Servlet lưu email và password vào cookie
3. Cookie có maxAge = 7 ngày
```

**Mock Setup**:
- `request.getParameter("remember")` → "on"

**Assertions**:
- `response.addCookie()` được gọi ít nhất 2 lần (email + password cookies)
- Cookie có maxAge = 7 * 24 * 60 * 60

---

## UC-005: Add To Cart Flow Tests

### Class: `AddToCartFlowTest.java`

### Test Cases

#### 1. `shouldHandleInvalidProductId()`
**Mục đích**: Test xử lý product ID không hợp lệ

**Luồng hoạt động**:
```
1. User request với id="abc" (không phải số)
2. Servlet parse Integer.parseInt("abc")
3. Ném NumberFormatException
4. Servlet catch exception, set error attribute
5. Forward đến /View/collection.jsp
```

**Mock Setup**:
- `request.getParameter("id")` → "abc"

**Assertions**:
- `request.setAttribute("error", ...)` chứa exception message
- `dispatcher.forward()` được gọi

---

#### 2. `shouldAddNewItemForAuthenticatedUser()` ⭐
**Mục đích**: Test thêm sản phẩm mới vào giỏ hàng (user đã đăng nhập)

**Luồng hoạt động**:
```
1. User đăng nhập, request thêm product 42, quantity 2
2. Servlet lấy user từ session
3. Servlet lấy/tạo cart của user
4. Servlet kiểm tra cart items hiện tại (empty)
5. Servlet kiểm tra product tồn tại
6. Servlet thêm cart item mới: productId=42, quantity=2
7. Servlet reload cart items
8. Servlet set cartItems vào session
9. Redirect về trang trước (referer)
```

**Mock Setup**:
- `session.getAttribute("user")` → user object
- `MockedConstruction<CartDB>`:
  - `getOrCreateCartByUserId()` → cart
  - `getCartItemsByCartId()` → empty list (lần 1, 2), items (lần 3)
  - `addCartItems(cartId, 42, 2, price)` → thêm item
- `MockedConstruction<ProductDB>`:
  - `getProductById(42)` → product
- `request.getHeader("referer")` → "http://test/products"

**Assertions**:
- `cartDB.addCartItems(cartId, 42, 2, price)` được gọi
- `session.setAttribute("cartItems", items)` với items chứa product 42, qty 2
- `response.sendRedirect("http://test/products")`

**Data Flow**:
```
Request: id=42, quantity=2
    ↓
Session: user = User{id:1}
    ↓
CartDB.getOrCreateCartByUserId(1) → Cart{id:11}
    ↓
CartDB.getCartItemsByCartId(11) → [] (empty)
    ↓
ProductDB.getProductById(42) → Product{id:42, price:200000}
    ↓
CartDB.addCartItems(11, 42, 2, 200000)
    ↓
CartDB.getCartItemsByCartId(11) → [CartItems{productId:42, qty:2}]
    ↓
Session: cartItems = [CartItems{productId:42, qty:2}]
```

---

#### 3. `shouldClampQuantityWhenItemAlreadyExists()` ⭐
**Mục đích**: Test cập nhật số lượng khi sản phẩm đã có trong giỏ, và clamp theo stock

**Luồng hoạt động**:
```
1. User có product 7, quantity 3 trong cart
2. User thêm thêm 4 sản phẩm nữa (total = 7)
3. Product có stock = 5
4. Servlet phát hiện product đã có trong cart
5. Servlet tính: currentQty(3) + addQty(4) = 7
6. Servlet so sánh với stock(5): 7 > 5
7. Servlet clamp quantity = 5 (stock)
8. Servlet update quantity trong cart
9. Redirect về trang trước
```

**Mock Setup**:
- Existing cart items: `[CartItems{productId:7, qty:3}]`
- Product stock: 5
- Request: add 4 more

**Assertions**:
- `cartDB.updateQuantityAddToCart(cartId, 7, 5)` được gọi (clamped to stock)
- `cartDB.addCartItems()` KHÔNG được gọi
- Final quantity = 5 (không phải 7)

**Data Flow**:
```
Existing: CartItems{productId:7, qty:3}
Request: add 4 more
    ↓
Calculate: 3 + 4 = 7
    ↓
Check stock: Product{stock:5}
    ↓
Clamp: min(7, 5) = 5
    ↓
CartDB.updateQuantityAddToCart(cartId, 7, 5)
    ↓
Final: CartItems{productId:7, qty:5}
```

---

#### 4. `shouldPersistCartInCookieForGuest()` ⭐
**Mục đích**: Test lưu giỏ hàng vào cookie cho guest user

**Luồng hoạt động**:
```
1. Guest user (chưa đăng nhập) thêm product 90
2. Guest đã có product 90, quantity 2 trong cookie
3. Guest thêm thêm 1 sản phẩm
4. Servlet đọc cookie cart: "90:2"
5. Servlet đọc cookie → Map{90: 2}
6. Servlet increment: Map{90: 3}
7. Servlet ghi lại cookie: "90:3"
8. Redirect về trang trước
```

**Mock Setup**:
- `session.getAttribute("user")` → null (guest)
- Cookie: `guest_cart=90:2`
- `MockedConstruction<ProductDB>`:
  - `getProductById(90)` → product

**Assertions**:
- `response.addCookie()` được gọi
- Cookie mới có value chứa "90:3"
- `session.setAttribute("cartSuccessMsg", ...)`

**Data Flow**:
```
Cookie: "90:2"
    ↓
CartCookieUtil.readCartMap() → Map{90: 2}
    ↓
Increment: Map{90: 2 + 1} = Map{90: 3}
    ↓
CartCookieUtil.writeCartMap() → Cookie: "90:3"
    ↓
Response: addCookie("guest_cart", "90:3")
```

---

#### 5. `buyNowShouldRedirectGuestToCart()`
**Mục đích**: Test "Mua ngay" redirect đến trang cart

**Luồng hoạt động**:
```
1. Guest user click "Mua ngay" (buyNow=true)
2. Servlet thêm sản phẩm vào cookie
3. Servlet kiểm tra buyNow parameter
4. Nếu buyNow=true → redirect đến /cart (không phải referer)
```

**Mock Setup**:
- `request.getParameter("buyNow")` → "true"

**Assertions**:
- `response.sendRedirect("/shop/cart")` (không phải referer)

---

## UC-009: Apply Promotion Flow Tests

### Class: `ApplyPromotionUseCaseTest.java`

### Test Cases

#### 1. `shouldHandleRemoveDiscount()`
**Mục đích**: Test gỡ mã giảm giá đã áp dụng

**Luồng hoạt động**:
```
1. User đã áp dụng mã "SPRING10"
2. User click "Xóa mã giảm giá" (removeDiscount=true)
3. Servlet xóa appliedDiscountCode và appliedDiscountAmount khỏi session
4. Servlet lưu mã đã xóa vào lastRemovedDiscountCode
5. Servlet forward đến cart.jsp với message
```

**Mock Setup**:
- `request.getParameter("removeDiscount")` → "true"
- `session.getAttribute("appliedDiscountCode")` → "SPRING10"

**Assertions**:
- `session.removeAttribute("appliedDiscountCode")`
- `session.removeAttribute("appliedDiscountAmount")`
- `session.setAttribute("lastRemovedDiscountCode", "SPRING10")`
- `request.setAttribute("msg", "Đã xóa mã giảm giá: SPRING10")`

---

#### 2. `shouldFailWhenCartIdMissing()`
**Mục đích**: Test validation cart ID

**Luồng hoạt động**:
```
1. User request áp dụng mã nhưng không có cartId trong session
2. Servlet kiểm tra cartId == null
3. Servlet set error và forward
```

**Assertions**:
- `request.setAttribute("error", "Không tìm thấy giỏ hàng.")`

---

#### 3. `shouldRequireLogin()`
**Mục đích**: Test yêu cầu đăng nhập

**Luồng hoạt động**:
```
1. Guest user cố gắng áp dụng mã giảm giá
2. Servlet kiểm tra user == null
3. Servlet set error yêu cầu đăng nhập
```

**Assertions**:
- `request.setAttribute("error", "Vui lòng đăng nhập để sử dụng mã giảm giá.")`

---

#### 4. `shouldRejectInvalidCode()`
**Mục đích**: Test mã giảm giá không hợp lệ

**Luồng hoạt động**:
```
1. User nhập mã "INVALID"
2. Servlet gọi DiscountDB.validateAndGetDiscount("INVALID")
3. DiscountDB trả về null (mã không tồn tại/hết hạn)
4. Servlet xóa discount khỏi session
5. Servlet set error message
```

**Mock Setup**:
- `MockedConstruction<DiscountDB>`:
  - `validateAndGetDiscount("INVALID")` → null

**Assertions**:
- `session.removeAttribute("appliedDiscountCode")`
- `request.setAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn.")`

---

#### 5. `shouldDenyWhenUserNotEligible()` ⭐
**Mục đích**: Test user không đủ quyền sử dụng mã

**Luồng hoạt động**:
```
1. User nhập mã "SALE50"
2. Mã tồn tại và hợp lệ
3. Servlet kiểm tra user có quyền sử dụng không
4. DiscountDB.canUserUseDiscount() trả về false
5. Servlet set error và không áp dụng mã
```

**Mock Setup**:
- `MockedConstruction<DiscountDB>`:
  - `validateAndGetDiscount("SALE50")` → discount object
  - `canUserUseDiscount(userId, discountId)` → false

**Assertions**:
- `request.setAttribute("error", "Bạn không có quyền sử dụng mã giảm giá này.")`
- Session KHÔNG được set appliedDiscountCode

---

#### 6. `shouldRejectWhenSubtotalBelowMinimum()` ⭐
**Mục đích**: Test validation minimum order amount

**Luồng hoạt động**:
```
1. User có cart total = 500,000 VND
2. Mã giảm giá yêu cầu min order = 1,000,000 VND
3. Servlet tính cart total
4. Servlet so sánh: 500,000 < 1,000,000
5. Servlet reject và set error
```

**Mock Setup**:
- `MockedConstruction<CartDB>`:
  - `calculateCartTotal(cartId)` → 500,000
- Discount: `minOrderAmount = 1,000,000`

**Assertions**:
- `request.setAttribute("error", "Đơn hàng chưa đạt tối thiểu để áp dụng mã giảm giá.")`

---

#### 7. `shouldApplyPercentageDiscountWithMaxCap()` ⭐
**Mục đích**: Test áp dụng mã phần trăm với giới hạn tối đa

**Luồng hoạt động**:
```
1. User có cart total = 1,000,000 VND
2. Mã giảm giá: 20% với max discount = 150,000 VND
3. Servlet tính: 1,000,000 * 20% = 200,000 VND
4. Servlet so sánh: 200,000 > 150,000 (max)
5. Servlet clamp discount = 150,000 VND
6. Servlet set appliedDiscountAmount = 150,000 vào session
7. Servlet forward với success message
```

**Mock Setup**:
- Cart total: 1,000,000
- Discount: type=PERCENTAGE, value=20%, maxDiscountAmount=150,000

**Calculations**:
```
Raw discount = 1,000,000 * 20% = 200,000
Clamped discount = min(200,000, 150,000) = 150,000
```

**Assertions**:
- `session.setAttribute("appliedDiscountCode", "SALE50")`
- `session.setAttribute("appliedDiscountAmount", 150,000.0)`
- `request.setAttribute("msg", "Áp dụng mã thành công: SALE50")`

**Data Flow**:
```
Cart Total: 1,000,000 VND
    ↓
Discount: 20% PERCENTAGE, max=150,000
    ↓
Calculate: 1,000,000 * 20% = 200,000
    ↓
Clamp: min(200,000, 150,000) = 150,000
    ↓
Session: appliedDiscountAmount = 150,000
```

---

#### 8. `shouldApplyFixedAmountDiscount()`
**Mục đích**: Test áp dụng mã giảm giá cố định

**Luồng hoạt động**:
```
1. User có cart total = 800,000 VND
2. Mã giảm giá: FIXED_AMOUNT = 50,000 VND
3. Servlet set discount = 50,000 (không cần tính %)
4. Servlet set vào session
```

**Calculations**:
```
Discount = 50,000 (fixed, không phụ thuộc cart total)
```

**Assertions**:
- `session.setAttribute("appliedDiscountAmount", 50,000.0)`

---

## Mocking Strategy

### MockedConstruction Pattern

Sử dụng `MockedConstruction` để mock các DAO classes được khởi tạo trong servlet:

```java
try (MockedConstruction<UserDB> mockedUserDb = 
    mockConstruction(UserDB.class, (mock, context) -> {
        when(mock.getUserByEmail("test@gmail.com"))
            .thenReturn(user);
    })) {
    // Test code here
    servlet.doPost(request, response);
    
    // Verify interactions
    UserDB userDbMock = mockedUserDb.constructed().get(0);
    verify(userDbMock).getUserByEmail("test@gmail.com");
}
```

### Lenient Stubbing

Sử dụng `lenient()` cho các stubbing không chắc chắn được gọi:

```java
lenient().when(request.getParameter("optionalParam"))
    .thenReturn("value");
```

### Argument Captor

Sử dụng `ArgumentCaptor` để kiểm tra giá trị được truyền:

```java
ArgumentCaptor<Cookie> cookieCaptor = 
    ArgumentCaptor.forClass(Cookie.class);
verify(response).addCookie(cookieCaptor.capture());
assertThat(cookieCaptor.getValue().getName())
    .isEqualTo("guest_cart");
```

---

## Test Data Flow

### Login Flow - Merge Cart

```
┌─────────────┐
│ Guest User  │
│ Cookie Cart │
│ "42:3"      │
└──────┬──────┘
       │
       ▼
┌──────────────────┐
│ Login Servlet    │
│ 1. Validate      │
│ 2. Authenticate  │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CartDB           │
│ getCartByUserId()│
│ → null (create)  │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CartCookieUtil   │
│ readCartMap()    │
│ → Map{42: 3}     │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ ProductDB        │
│ getProductById(42)│
│ → Product        │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CartDB           │
│ addCartItems()   │
│ → Merged         │
└──────┬───────────┘
```

### Add To Cart Flow

```
┌─────────────┐
│ User Request│
│ id=42, qty=2│
└──────┬──────┘
       │
       ▼
┌──────────────────┐
│ AddToCart Servlet│
│ 1. Parse ID      │
│ 2. Get User      │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CartDB           │
│ getOrCreateCart()│
│ → Cart{id:11}    │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CartDB           │
│ getCartItems()   │
│ → [] (empty)     │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ ProductDB        │
│ getProductById() │
│ → Product        │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CartDB           │
│ addCartItems()   │
│ → Success        │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ Session          │
│ cartItems = [...]│
└──────────────────┘
```

### Apply Promotion Flow

```
┌─────────────┐
│ User Input  │
│ Code: SALE50│
└──────┬──────┘
       │
       ▼
┌──────────────────┐
│ ApplyPromotion   │
│ 1. Check cartId  │
│ 2. Check user    │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ DiscountDB       │
│ validateAndGet() │
│ → Discount       │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ DiscountDB       │
│ canUserUse()     │
│ → true           │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CartDB           │
│ calculateTotal() │
│ → 1,000,000      │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ Calculate        │
│ Discount Amount  │
│ → 150,000        │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ Session          │
│ appliedDiscount  │
│ = 150,000        │
└──────────────────┘
```

---

## Kết Luận

Tài liệu này mô tả chi tiết luồng hoạt động của các test cases chính trong hệ thống. Mỗi test case được thiết kế để:

1. **Bao phủ một use case cụ thể** từ USECASE_AND_DATAFLOW.md
2. **Sử dụng mocking** để không phụ thuộc database thật
3. **Kiểm tra cả happy path và error cases**
4. **Verify interactions** với dependencies (DAO, Session, Response)

Để xem thêm chi tiết implementation, tham khảo source code trong `src/test/java/Controller/`.




