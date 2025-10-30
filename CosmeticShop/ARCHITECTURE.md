# 🏗️ CosmeticShop - Kiến Trúc Hệ Thống

## 1. Tổng Quan Kiến Trúc

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENT (Browser)                     │
└────────────────┬────────────────────────────────────────────┘
                 │ HTTP/HTTPS
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌────────────────────────────────────────────────────┐    │
│  │  JSP Pages (View/)                                 │    │
│  │  - home.jsp, cart.jsp, checkout.jsp                │    │
│  │  - Admin pages (admin/*.jsp)                       │    │
│  └────────────────────────────────────────────────────┘    │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
│  ┌────────────────────────────────────────────────────┐    │
│  │  Servlets (Controller/*)                           │    │
│  │  - ProductController, OrderServlet                 │    │
│  │  - AdminServlet, CheckoutServlet                   │    │
│  │  - VnPayCreateServlet, LoginServlet                │    │
│  └────────────────────────────────────────────────────┘    │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                      Filter Layer                            │
│  ┌────────────────────────────────────────────────────┐    │
│  │  AdminAuthFilter                                   │    │
│  │  - Authentication check                            │    │
│  │  - Role-based authorization                        │    │
│  └────────────────────────────────────────────────────┘    │
└────────────────┬────────────────────────────────────────────┘
                 │
                寓意 ▼
┌─────────────────────────────────────────────────────────────┐
│                       Service Layer                          │
│  ┌────────────────────────────────────────────────────┐    │
│  │  DAO Classes (DAO/*)                               │    │
│  │  - UserDB, ProductDB, OrderDB                      │    │
│  │  - CartDB, DiscountDB, CategoryDB                  │    │
│  └────────────────────────────────────────────────────┘    │
└────────────────┬────────────────────────────────────────────┘
                 │ JDBC
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                            │
│  ┌────────────────────────────────────────────────────┐    │
│  │  SQL Server Database                               │    │
│  │  - 13 Tables                                       │    │
│  │  - 4 Stored Procedures                            │    │
│  │  - 4 Triggers                                     │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   External Services                          │
│  - VNPay Payment Gateway                                    │
│  - Email Service (SMTP)                                     │
└─────────────────────────────────────────────────────────────┘
```

## 2. Luồng Dữ Liệu Chính

### 2.1 User Authentication Flow

```
User Login Request
    │
    ▼
LoginServlet (Controller)
    │
    ▼
UserDB (DAO)
    │ ┌────────────────┐
    ├─► Validate credentials
    │ └────────────────┘
    │
    ▼
Create Session
    │
    ▼
Set user object in session
    │
    ▼
Redirect to home
```

### 2.2 Product Browsing Flow

```
GET /product-detail?id=123
    │
    ▼
ProductController (Controller)
    │
    ▼
ProductDB.getById() (DAO)
    │
    ├─► SELECT product
    ├─► SELECT product images
    └─► SELECT category
    │
    ▼
Return Product object
    │
    ▼
product-detail.jsp (View)
    │
    ▼
Render HTML to user
```

### 2.3 Shopping Cart Flow

```
POST /addToCart
    │
    ├─► product_id
    ├─► quantity
    └─► user_id (from session)
    │
    ▼
AddToCartServlet
    │
    ▼
CartDB.addItem() (DAO)
    │
    ├─► Check if cart exists
    ├─► Create cart if not exists
    └─► Insert cart item
    │
    ▼
Calculate total
    │
    ▼
Return success
```

### 2.4 Checkout & Order Flow

```
GET /checkout
    │
    ▼
CheckoutServlet
    │
    ├─► Get cart items
    ├─► Get shipping addresses
    ├─► Get shipping methods
    └─► Get available discounts
    │
    ▼
checkout.jsp (View)

User fills form
    │
    ├─► Shipping address
    ├─► Shipping method
    └─► Apply discount (optional)
    │
    ▼
POST /checkout
    │
    ▼
CheckoutServlet.doPost()
    │
    ├─► Validate cart
    ├─► Calculate totals
    ├─► Create order
    └─► Create order details
    │
    ▼
Create shipping address (if new)
    │
    ▼
Apply discount (if valid)
    │
    ▼
Redirect to payment
```

### 2.5 Payment Flow (VNPay)

```
POST /payment/vnpay/create
    │
    ├─► Order ID
    ├─► Total amount
    └─► Product info
    │
    ▼
VnPayCreateServlet
    │
    ▼
VnPayConfig.buildPaymentUrl()
    │
    ├─► Create payment request
    ├─► Add signature
    └─► Generate payment URL
    │
    ▼
Redirect to VNPay
    │
    ▼
User pays on VNPay
    │
    ▼
VNPay redirects back
    │
    ▼
GET /payment/vnpay/return
    │
    ▼
VnPayReturnServlet
    │
    ├─► Verify signature
    ├─► Check payment status
    └─► Update order status
    │
    ▼
Redirect to order confirmation
```

### 2.6 Admin Flow

```
GET /admin?action=products
    │
    ▼
AdminAuthFilter
    │
    ├─► Check logged in?
    ├─► Check role = ADMIN?
    └─► Redirect if invalid
    │
    ▼
AdminServlet.doGet()
    │
    ├─► action = "products"
    ├─► Get search parameter
    └─► Query products
    │
    ▼
ProductDB.searchProducts() / getAllProducts()
    │
    ▼
Return product list
    │
    ▼
admin/manage-products.jsp (View)
    │
    ▼
Render admin interface
```

## 3. Database Relationships

```
┌──────────────┐
│    Users     │
│──────────────│
│ user_id (PK) │
│ username     │◄────┐
│ email        │     │
│ role         │     │
└──────────────┘     │
                     │
                     │ user_id
┌──────────────┐     │
│   Orders     │     │
│──────────────│     │
│ order_id(PK) │─────┤
│ user_id(FK)  │     │
│ total_amount │     │
│ status       │     │
└──────────────┘     │
                     │
                     │ user_id
┌──────────────┐     │
│   Carts      │     │
│──────────────│     │
│ cart_id(PK)  │     │
│ user_id(FK)  │─────┤
└──────────────┘     │
                     │
                     │ user_id
┌──────────────┐     │
│ShippingAddrs │     │
│──────────────│     │
│ address_id   │     │
│ user_id(FK)  │─────┘
└──────────────┘

┌──────────────┐         ┌──────────────┐
│  Categories  │         │  Products    │
│──────────────│         │──────────────│
│category_id   │────────┤│ product_id   │
│ name         │◄────┐  ││ category_id  │
│ description  │     │  ││ name         │
└──────────────┘     │  ││ price        │
                     │  ││ stock        │
                     │  │└──────────────┘
                     │  │      │
                     │  │      │ product_id
                     │  │      │
┌──────────────┐     │  │  ┌──────────────┐
│ProductImages │     │  │  │ OrderDetails │
│──────────────│     │  │  │──────────────│
│ image_id     │     │  └─┤│ product_id   │
│ product_id   │     │    ││ order_id     │
│ image_url    │     │    ││ quantity     │
└──────────────┘     │    └──────────────┘
                     │
                     │ category_id
┌──────────────┐     │
│  Discounts   │     │
│──────────────│     │
│discount_id(PK)     │
│ code         │     │
│ value        │     │
└──────────────┘     │
                     │
                     │ discount_id
┌──────────────┐     │
│UserVouchers  │     │
│──────────────│     │
│user_voucher_id     │
│ user_id      │     │
│ discount_id  │     │
│ status       │     │
└──────────────┘
```

## 4. Session Management

```
┌─────────────────────────────────────────────────┐
│            Session Lifecycle                     │
├─────────────────────────────────────────────────┤
│                                                 │
│  1. User Login                                 │
│     └─► Create session                         │
│         └─► Store user object                  │
│              session.setAttribute("user", user) │
│                                                 │
│  2. User Requests                               │
│     └─► Verify session exists                  │
│         └─► Check timeout (30 min)             │
│              └─► Validate or destroy           │
│                                                 │
│  3. User Logout                                 │
│     └─► Invalidate session                     │
│         session.invalidate()                    │
│                                                 │
│  4. Admin Pages                                 │
│     └─► Check session                          │
│         └─► Verify role = ADMIN                │
│              └─► Allow or redirect             │
│                                                 │
└─────────────────────────────────────────────────┘
```

## 5. Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│                  Security Layers                         │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  1. Authentication                                      │
│     ├─► LoginServlet                                    │
│     ├─► Password hashing                                │
│     └─► Session creation                                │
│                                                          │
│  2. Authorization                                       │
│     ├─► AdminAuthFilter                                 │
│     ├─► Role-based access control                       │
│     └─► Page-level protection                           │
│                                                          │
│  3. Input Validation                                    │
│     ├─► Server-side validation                          │
│     ├─► SQL injection prevention (PreparedStatement)    │
│     └─► XSS prevention (JSTL escaping)                  │
│                                                          │
│  4. Session Security                                    │
│     ├─► Session timeout (30 min)                        │
│     ├─► HTTPS recommended                               │
│     └─► Cookie security                                 │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## 6. Component Interaction

```
┌──────────┐         ┌──────────┐
│   JSP    │────────►│ Servlet  │
│  (View)  │         │(Controller)
└──────────┘         └────┬─────┘
                          │
                          ▼
                    ┌──────────┐
                    │   DAO    │
                    │ (Service)│
                    └────┬─────┘
                         │
                         ▼
                    ┌──────────┐
                    │  Model   │
                    │ (Entity) │
                    └────┬─────┘
                         │
                         ▼
                    ┌──────────┐
                    │ Database │
                    │  (SQL)   │
                    └──────────┘
```

## 7. Error Handling

```
┌──────────────────────────────────────────┐
│          Error Handling Flow              │
├──────────────────────────────────────────┤
│                                          │
│  Exception occurs                        │
│      │                                   │
│      ▼                                   │
│  Try-catch in Servlet                    │
│      │                                   │
│      ▼                                   │
│  Log error                               │
│      │                                   │
│      ├─► Database error                  │
│      │   └─► Show error page             │
│      │                                   │
│      ├─► Authentication error            │
│      │   └─► Redirect to login           │
│      │                                   │
│      ├─► Authorization error             │
│      │   └─► Show access denied          │
│      │                                   │
│      └─► Unknown error                   │
│          └─► Show generic error          │
│                                          │
└──────────────────────────────────────────┘
```

## 8. Deployment Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Production                         │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌─────────────────────┐                           │
│  │   Load Balancer     │                           │
│  │   (optional)        │                           │
│  └──────────┬──────────┘                           │
│             │                                      │
│       ┌─────┴──────┐                              │
│       │            │                              │
│  ┌────▼───┐  ┌────▼───┐                          │
│  │ Tomcat │  │ Tomcat │                          │
│  │ Node 1 │  │ Node 2 │                          │
│  └────┬───┘  └────┬───┘                          │
│       │            │                              │
│       └─────┬──────┘                              │
│             ▼                                     │
│  ┌──────────────────────┐                         │
│  │   SQL Server DB      │                         │
│  │   (Primary)          │                         │
│  └──────────────────────┘                         │
│                                                     │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│                Development                           │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────┐         ┌──────────┐                 │
│  │  IDE     │────────►│  Tomcat  │                 │
│  │ (IntelliJ)│         │  Embedded│                │
│  └──────────┘         └────┬─────┘                 │
│                            │                       │
│                     ┌──────▼──────┐                │
│                     │  SQL Server │                │
│                     │  Local DB   │                │
│                     └─────────────┘                │
│                                                     │
└─────────────────────────────────────────────────────┘
```

## 9. Key Design Patterns

### 9.1 MVC Pattern
- **Model**: Entity classes in `Model/`
- **View**: JSP pages in `View/` and `admin/`
- **Controller**: Servlet classes in `Controller/`

### 9.2 DAO Pattern
- Data Access Objects in `DAO/`
- Abstracts database operations
- Example: `ProductDB`, `UserDB`, `OrderDB`

### 9.3 Singleton Pattern (Implicit)
- Database connection management
- Session management

### 9.4 Filter Pattern
- `AdminAuthFilter` for authentication/authorization

## 10. Performance Considerations

```
┌─────────────────────────────────────────────────────┐
│            Performance Optimizations                 │
├─────────────────────────────────────────────────────┤
│                                                     │
│  1. Connection Pooling                             │
│     - Reuse database connections                    │
│                                                     │
│  2. ASP Statement                                   │
│     - Prevent SQL injection                         │
│     - Allow query plan caching                      │
│                                                     │
│  3. Database Indexes                               │
│     - On foreign keys                               │
│     - On frequently queried columns                 │
│                                                     │
│  4. Stored Procedures                              │
│     - Pre-compiled queries                          │
│     - Reduce network traffic                        │
│                                                     │
│  5. Session Management                             │
│     - 30 minute timeout                             │
│     - Selective session storage                     │
│                                                     │
│  6. Static Resources                               │
│     - CSS/JS/images served separately               │
│     - Browser caching                               │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

**Document Version**: 1.0  
**Last Updated**: 2024
