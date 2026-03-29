# Cosmetic Shop - Project Overview

## 📋 Project Description

**Cosmetic Shop** is a full-featured e-commerce web application designed for selling cosmetic products. The platform provides a comprehensive system for managing products, orders, users, and promotional campaigns with integrated payment solutions and notification systems.

## 🎯 Key Features

### Customer Features

- **Product Browsing & Search**: Browse cosmetics with filtering and search suggestions
- **Shopping Cart**: Add/remove products with quantity management
- **Wishlist**: Save favorite products for later purchase
- **Secure Checkout**: Multi-step checkout process with address management
- **Multiple Payment Methods**:
  - VNPay integration for electronic payments
  - Bank payment option
- **Order Management**: View order history, track orders, and manage order details
- **Account Management**: User profile, password change, password reset
- **Notifications**: Real-time notification system for order updates
- **Comments & Reviews**: Leave comments with media support on products
- **Discount System**: Apply promotional codes and view available discounts

### Admin Features

- **Dashboard**: Overview of key metrics and business analytics
- **Product Management**: Create, edit, delete products with gallery support
- **Category Management**: Organize products into multiple categories
- **Order Management**: View and update order statuses
- **User Management**: Monitor and manage user accounts
- **Discount Management**: Create and assign discount codes to customers
- **Banner Management**: Manage promotional banners
- **Contact Management**: Manage customer inquiries and support tickets
- **Reports & Analytics**: Generate sales reports and business insights

## 🏗️ Architecture

### Technology Stack

- **Backend**: Java Jakarta EE 11
- **Build Tool**: Maven
- **Database**: SQL Server (MSSQL)
- **Frontend**: JSP, HTML5, CSS3, JavaScript
- **Framework**: Servlet-based MVC Architecture
- **Payment Gateway**: VNPay integration
- **Email**: Jakarta Mail for notifications
- **Authentication**: Google OAuth2 support

### Project Structure

```
CosmeticShop/
├── src/main/
│   ├── java/
│   │   ├── Controller/        # Servlet Controllers (41+ controllers)
│   │   ├── DAO/               # Database Access Objects (16 DAOs)
│   │   ├── Model/             # Entity Models (20+ models)
│   │   ├── Filter/            # Request Filters
│   │   ├── Util/              # Utility Classes
│   │   └── com/mycompany/
│   ├── resources/
│   │   └── META-INF/persistence.xml
│   └── webapp/
│       ├── View/              # Customer JSP Pages (30+ pages)
│       ├── admin/             # Admin JSP Pages (13+ pages)
│       ├── Js/                # JavaScript files
│       ├── Css/               # Stylesheets
│       ├── IMG/               # Images
│       └── WEB-INF/
├── pom.xml                    # Maven Configuration
└── Dockerfile                 # Docker Containerization

```

## 📦 Core Components

### Controllers (41 servlets)

Key controllers handling business logic:

- **AccountManagement**: User account operations
- **ProductController**: Product listing and management
- **Checkout**: Order processing flow
- **OrderManagement**: Order tracking and updates
- **PaymentCallback**: VNPay payment processing
- **DiscountController**: Discount application logic
- **NotificationController**: Push notifications
- **WishlistController**: Wishlist operations
- **CommentServlet**: Product reviews and comments

### Database Access Objects (16 DAOs)

- ProductDB, CategoryDB, OrderDB
- UserDB, CartDB, WishlistDB
- DiscountDB, NotificationDB
- BannerDB, ShippingAddressDB
- CommentDB, and more...

### Data Models (20+ entities)

- Product, Category, Order, OrderDetail
- User, Cart, Wishlist, Notification
- Discount, Banner, Comment, CommentReply
- ShippingAddress, and others

### User Interface

- **Customer Pages** (30+ JSP pages):
  - Home, Product Detail, Collection
  - Shopping Cart, Checkout, Order Confirmation
  - My Orders, My Discounts, Wishlist
  - Notifications, Account Management
  - Password Management, Contact Form

- **Admin Dashboard** (13+ JSP pages):
  - Dashboard with Analytics
  - Product/Category/Order Management
  - User Management, Discount Assignment
  - Banner Management, Reports, Contact Management

## 🔌 Integration Features

### Payment Integration

- **VNPay**: Vietnamese payment gateway with:
  - Payment creation (VnPayCreate.java)
  - IPN callback handling (VnPayIpn.java)
  - Payment return handling (VnPayReturn.java)
  - Bank payment option (BankPayment.java)

### Email System

- Order confirmation emails
- Password reset notifications
- Promotional notifications
- Order status updates

### Authentication & Authorization

- User login/signup
- Google OAuth2 integration
- Role-based access (User/Admin/Manager)
- Session management

## 💾 Database Features

### Core Tables

- Users (with role-based system)
- Products (with multi-category support)
- Categories, ProductCategories
- Orders, OrderDetails
- Carts, WishLists
- Discounts, UserDiscountAssign
- Banners, Notifications
- Comments, CommentReplies
- ShippingAddress, ShippingMethods
- Contact Forms (Liên Hệ)

### Key Database Capabilities

- Multi-category product support
- Discount assignment to specific users
- Notification tracking system
- Comment system with media attachments
- Wishlist management
- Multiple shipping address support

## 🎨 User Interface Highlights

### Frontend Technologies

- Bootstrap 5.x for responsive design
- Bootstrap Icons for UI elements
- Custom CSS for styling:
  - Responsive layouts
  - Theme consistency
  - Mobile-optimized views

### Key Pages

- **Home Page**: Feature products, best sellers, promotional banners
- **Product Detail**: Gallery, comments, wishlist, related products
- **Shopping Experience**: Cart management, checkout wizard
- **Admin Dashboard**: Analytics charts, quick actions, reports
- **Order Tracking**: Real-time status updates, order history
- **Notification Center**: Unified notification management

## 🚀 Deployment

- **Docker Support**: Dockerfile included for containerization
- **Build Tool**: Maven for dependency management and build automation
- **Production Ready**: WAR packaging for Java application servers

## 🔐 Security Features

- Password reset mechanism with email verification
- Secure payment processing with VNPay
- User role-based access control
- Session-based authentication
- CSRF protection through servlets

## 📊 Admin Analytics

- Sales reports and metrics
- Product performance tracking
- User activity monitoring
- Order status dashboard
- Discount effectiveness analysis

## 🌟 Recent Enhancements

- Notification system implementation
- Multi-category product support update
- Banner management system
- Comment and review system with media
- Advanced discount assignment features
- Real-time order notifications

## 🛠️ Development Notes

- **Framework**: Pure Servlet-based MVC (no Spring Framework)
- **ORM**: Direct JDBC through DAO pattern
- **Scalability**: DAO layer allows easy database migration
- **Modularity**: Clear separation between Controller, DAO, and Model layers
- **Extensibility**: Easy to add new features through servlet controllers

---

**Project Status**: Production Ready  
**Last Updated**: March 2026  
**Technology Stack**: Java Jakarta EE 11, SQL Server, JSP/JavaScript
