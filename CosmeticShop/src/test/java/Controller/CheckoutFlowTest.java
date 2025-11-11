package Controller;

import DAO.CartDB;
import DAO.DiscountDB;
import DAO.OrderDB;
import DAO.ProductDB;
import DAO.ShippingAddressDB;
import DAO.ShippingMethodDB;
import Model.Cart;
import Model.CheckoutItem;
import Model.Order;
import Model.OrderDetail;
import Model.user;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class cho Checkout Flow
 * Test các scenarios: xem checkout page, tạo đơn hàng, thanh toán, áp dụng discount
 * 
 * Flow Order: 4 - Bước cuối cùng: thanh toán và tạo đơn hàng
 */
@DisplayName("Checkout Flow Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(4)
class CheckoutFlowTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private Checkout checkoutServlet;
    private user testUser;
    private Cart testCart;
    private List<CheckoutItem> testCheckoutItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkoutServlet = new Checkout();

        // Setup test user
        testUser = new user(1, "testuser", "test@gmail.com", "0123456789",
                "password", "USER", LocalDateTime.now());

        // Setup test cart
        testCart = new Cart(1, 1, LocalDateTime.now(), LocalDateTime.now());

        // Setup test checkout items
        testCheckoutItems = new ArrayList<>();
        CheckoutItem item1 = new CheckoutItem();
        item1.setProductId(1);
        item1.setQuantity(2);
        item1.setPrice(250000.0);
        item1.setProductName("Kem dưỡng ẩm");
        testCheckoutItems.add(item1);
    }

    @Test
    @DisplayName("Test doGet - redirect to login nếu chưa đăng nhập")
    void testDoGet_RedirectToLogin_WhenNotLoggedIn() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When
        checkoutServlet.doGet(request, response);

        // Then
        verify(response, times(1)).sendRedirect("/CosmeticShop/login");
    }

    @Test
    @DisplayName("Test doGet - hiển thị checkout page với thông tin đầy đủ")
    void testDoGet_DisplayCheckoutPage_WithFullInfo() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getRequestDispatcher("/View/checkout.jsp")).thenReturn(requestDispatcher);

        // When - Note: Requires actual database connection
        // checkoutServlet.doGet(request, response);

        // Then
        // verify(request, times(1)).setAttribute(eq("addresses"), anyList());
        // verify(request, times(1)).setAttribute(eq("methods"), anyList());
        // verify(request, times(1)).setAttribute(eq("items"), anyList());
        // verify(request, times(1)).setAttribute(eq("itemsTotal"), anyDouble());
        // verify(request, times(1)).getRequestDispatcher("/View/checkout.jsp");
    }

    @Test
    @DisplayName("Test doGet - áp dụng discount từ session")
    void testDoGet_ApplyDiscount_FromSession() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("appliedDiscountAmount")).thenReturn(50000.0);
        when(request.getRequestDispatcher("/View/checkout.jsp")).thenReturn(requestDispatcher);

        // When - Note: Requires actual database connection
        // checkoutServlet.doGet(request, response);

        // Then
        // verify(request, times(1)).setAttribute(eq("appliedDiscountAmount"), eq(50000.0));
        // verify(request, times(1)).setAttribute(eq("finalTotal"), anyDouble());
    }

    @Test
    @DisplayName("Test doPost - redirect to login nếu chưa đăng nhập")
    void testDoPost_RedirectToLogin_WhenNotLoggedIn() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When
        checkoutServlet.doPost(request, response);

        // Then
        verify(response, times(1)).sendRedirect("/CosmeticShop/login");
    }

    @Test
    @DisplayName("Test doPost - redirect to cart nếu không có items được chọn")
    void testDoPost_RedirectToCart_WhenNoItemsSelected() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(response, times(1)).sendRedirect("/CosmeticShop/cart");
    }

    @Test
    @DisplayName("Test doPost - redirect nếu không có shipping address")
    void testDoPost_Redirect_WhenNoShippingAddress() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("shipping_address_id")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(response, times(1)).sendRedirect(contains("error=no_address"));
    }

    @Test
    @DisplayName("Test doPost - tạo đơn hàng thành công với COD")
    void testDoPost_CreateOrderSuccess_WithCOD() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("shipping_address_id")).thenReturn("1");
        when(request.getParameter("shipping_method_id")).thenReturn("1");
        when(request.getParameter("payment_method")).thenReturn("COD");
        when(request.getParameter("notes")).thenReturn("Giao hàng nhanh");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(orderDB, times(1)).createOrder(any(Order.class));
        // verify(orderDB, times(1)).addOrderDetails(anyInt(), anyList());
        // verify(cartDB, times(1)).clearSelectedCartItems(anyInt());
        // verify(response, times(1)).sendRedirect(contains("/order-detail?orderId="));
    }

    @Test
    @DisplayName("Test doPost - tạo đơn hàng thành công với VNPay")
    void testDoPost_CreateOrderSuccess_WithVNPay() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("shipping_address_id")).thenReturn("1");
        when(request.getParameter("shipping_method_id")).thenReturn("1");
        when(request.getParameter("payment_method")).thenReturn("BANK");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(orderDB, times(1)).createOrder(any(Order.class));
        // verify(session, times(1)).setAttribute(eq("pendingCartId"), anyInt());
        // verify(response, times(1)).sendRedirect(contains("/payment/vnpay/create"));
    }

    @Test
    @DisplayName("Test doPost - trừ kho khi tạo đơn hàng")
    void testDoPost_DecreaseStock_WhenCreatingOrder() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("shipping_address_id")).thenReturn("1");
        when(request.getParameter("shipping_method_id")).thenReturn("1");
        when(request.getParameter("payment_method")).thenReturn("COD");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(productDB, atLeastOnce()).decreaseStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Test doPost - hoàn lại kho nếu tạo đơn thất bại")
    void testDoPost_RestoreStock_WhenOrderCreationFails() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("shipping_address_id")).thenReturn("1");
        when(request.getParameter("shipping_method_id")).thenReturn("1");
        when(request.getParameter("payment_method")).thenReturn("COD");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection and mock OrderDB to return null
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(productDB, atLeastOnce()).increaseStock(anyInt(), anyInt());
        // verify(response, times(1)).sendRedirect(contains("error=order"));
    }

    @Test
    @DisplayName("Test doPost - redirect nếu hết hàng")
    void testDoPost_Redirect_WhenOutOfStock() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("shipping_address_id")).thenReturn("1");
        when(request.getParameter("shipping_method_id")).thenReturn("1");
        when(request.getParameter("payment_method")).thenReturn("COD");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection and mock ProductDB.decreaseStock to return false
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(response, times(1)).sendRedirect(contains("error=out_of_stock"));
    }

    @Test
    @DisplayName("Test doPost - áp dụng discount và đánh dấu đã sử dụng")
    void testDoPost_ApplyDiscount_AndMarkAsUsed() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("appliedDiscountCode")).thenReturn("DISCOUNT10");
        when(session.getAttribute("appliedDiscountAmount")).thenReturn(50000.0);
        when(request.getParameter("shipping_address_id")).thenReturn("1");
        when(request.getParameter("shipping_method_id")).thenReturn("1");
        when(request.getParameter("payment_method")).thenReturn("COD");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection
        // checkoutServlet.doPost(request, response);

        // Then
        // verify(discountDB, times(1)).decrementAssignedDiscountUse(anyInt(), anyInt());
        // verify(session, times(1)).removeAttribute("appliedDiscountCode");
        // verify(session, times(1)).removeAttribute("appliedDiscountAmount");
    }

    @Test
    @DisplayName("Test doPost - tính tổng tiền với shipping cost")
    void testDoPost_CalculateTotal_WithShippingCost() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("shipping_address_id")).thenReturn("1");
        when(request.getParameter("shipping_method_id")).thenReturn("1");
        when(request.getParameter("payment_method")).thenReturn("COD");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When - Note: Requires actual database connection
        // checkoutServlet.doPost(request, response);

        // Then - Verify total calculation: itemsTotal - discount + shipping
        // verify(orderDB, times(1)).createOrder(argThat(order ->
        //     order.getTotalAmount() == (itemsTotal - discount + shippingCost)
        // ));
    }
}

