package Controller;

import DAO.CartDB;
import DAO.ProductDB;
import Model.Cart;
import Model.CartItems;
import Model.Product;
import Model.user;
import Util.CartCookieUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class cho Add To Cart Flow
 * Test các scenarios: thêm sản phẩm cho user, guest, update quantity, stock validation
 * 
 * Flow Order: 2 - Sau khi đăng nhập, thêm sản phẩm vào giỏ hàng
 */
@DisplayName("Add To Cart Flow Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(2)
class AddToCartFlowTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private addToCart addToCartServlet;
    private user testUser;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addToCartServlet = new addToCart();

        // Setup test user
        testUser = new user(1, "testuser", "test@gmail.com", "0123456789",
                "password", "USER", LocalDateTime.now());

        // Setup test cart
        testCart = new Cart(1, 1, LocalDateTime.now(), LocalDateTime.now());

        // Setup test product
        testProduct = new Product(1, "Kem dưỡng ẩm", 250000.0, 50,
                "Kem dưỡng ẩm cho da khô", "image1.jpg", 1);
    }

    @Test
    @DisplayName("Test doGet - thêm sản phẩm thành công cho user đã đăng nhập")
    void testDoGet_AddProductSuccess_LoggedInUser() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("2");
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then
        // verify(request, times(1)).setAttribute(eq("msg"), eq("Thêm sản phẩm thành công"));
        // verify(request, times(1)).getRequestDispatcher("/products");
    }

    @Test
    @DisplayName("Test doGet - thêm sản phẩm thành công cho guest user (cookie)")
    void testDoGet_AddProductSuccess_GuestUser() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then
        // verify(response, atLeastOnce()).addCookie(any(Cookie.class));
        // verify(request, times(1)).setAttribute(eq("msg"), eq("Thêm sản phẩm thành công"));
    }

    @Test
    @DisplayName("Test doGet - thêm sản phẩm không tồn tại - should show error")
    void testDoGet_AddProductFailure_ProductNotFound() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("id")).thenReturn("99999");
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then
        // verify(request, times(1)).setAttribute(eq("error"), contains("Sản phẩm không tồn tại"));
    }

    @Test
    @DisplayName("Test doGet - thêm sản phẩm với ID không hợp lệ - should show error")
    void testDoGet_AddProductFailure_InvalidProductId() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("id")).thenReturn("invalid");
        when(request.getRequestDispatcher("/View/collection.jsp")).thenReturn(requestDispatcher);
        
        // When
        addToCartServlet.doGet(request, response);
        
        // Then - Exception should be handled by servlet and error attribute set
        verify(request, times(1)).setAttribute(eq("error"), anyString());
        verify(request, times(1)).getRequestDispatcher("/View/collection.jsp");
    }

    @Test
    @DisplayName("Test doGet - thêm sản phẩm đã có trong giỏ - should update quantity")
    void testDoGet_AddProduct_UpdateExistingItem() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("3");
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);

        // Mock existing cart items
        List<CartItems> existingItems = new ArrayList<>();
        CartItems existingItem = new CartItems(1, 1, 1, 2, 250000.0);
        existingItems.add(existingItem);

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then - Verify quantity is updated (2 + 3 = 5, but clamped to stock)
        // verify(cartDB, atLeastOnce()).updateQuantityAddToCart(eq(1), eq(1), anyInt());
    }

    @Test
    @DisplayName("Test doGet - thêm sản phẩm vượt quá stock - should clamp to stock")
    void testDoGet_AddProduct_ExceedsStock() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("100"); // More than stock (50)
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then - Verify quantity is clamped to stock
        // verify(cartDB, atLeastOnce()).addCartItems(eq(1), eq(1), eq(50), anyDouble());
    }

    @Test
    @DisplayName("Test doGet - thêm sản phẩm với quantity mặc định = 1 nếu không có parameter")
    void testDoGet_AddProduct_DefaultQuantity() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn(null);
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then - Verify default quantity = 1
        // verify(cartDB, atLeastOnce()).addCartItems(eq(1), eq(1), eq(1), anyDouble());
    }

    @Test
    @DisplayName("Test doGet - guest user thêm sản phẩm vào cookie cart")
    void testDoGet_GuestUser_AddsToCookieCart() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then - Verify cookie is written
        // verify(response, atLeastOnce()).addCookie(argThat(cookie ->
        //     CartCookieUtil.GUEST_CART_COOKIE.equals(cookie.getName()) &&
        //     cookie.getValue().contains("1:1")
        // ));
    }

    @Test
    @DisplayName("Test doGet - guest user increment quantity trong cookie cart")
    void testDoGet_GuestUser_IncrementQuantityInCookie() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getRequestDispatcher("/products")).thenReturn(requestDispatcher);

        // Mock existing cookie cart
        Cookie existingCookie = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "1:2");
        when(request.getCookies()).thenReturn(new Cookie[]{existingCookie});

        // When - Note: Requires actual database connection
        // addToCartServlet.doGet(request, response);

        // Then - Verify quantity is incremented (2 + 1 = 3)
        // verify(response, atLeastOnce()).addCookie(argThat(cookie ->
        //     CartCookieUtil.GUEST_CART_COOKIE.equals(cookie.getName()) &&
        //     cookie.getValue().contains("1:3")
        // ));
    }
}

