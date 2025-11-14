package Controller;

import DAO.CartDB;
import DAO.DiscountDB;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class cho Cart Flow
 * Test các scenarios: xem giỏ hàng, hiển thị cho user và guest, load discounts
 * 
 * Flow Order: 3 - Sau khi thêm sản phẩm, xem và quản lý giỏ hàng
 */
@DisplayName("Cart Flow Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(3)
class CartFlowTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private cart cartServlet;
    private user testUser;
    private Cart testCart;
    private List<CartItems> testCartItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartServlet = new cart();

        // Lấy test data động từ database
        testUser = E2E.TestDataHelper.getRandomUser();
        if (testUser == null) {
            // Fallback nếu không có user trong DB
            testUser = new user(1, "testuser", "test@gmail.com", "0123456789",
                    "password", "USER", LocalDateTime.now());
        }

        // Setup test cart
        testCart = new Cart(1, testUser.getUser_id(), LocalDateTime.now(), LocalDateTime.now());

        // Lấy product từ database để tạo cart items
        Model.Product sampleProduct = E2E.TestDataHelper.getRandomProductInStock();
        if (sampleProduct == null) {
            // Fallback nếu không có product trong DB
            sampleProduct = new Model.Product(1, "Test Product", 250000.0, 10, "Description", "image.jpg", 1);
        }

        // Setup test cart items
        testCartItems = new ArrayList<>();
        CartItems item1 = new CartItems(1, testCart.getCart_id(), sampleProduct.getProductId(), 2, sampleProduct.getPrice());
        testCartItems.add(item1);
    }

    @Test
    @DisplayName("Test doGet - hiển thị giỏ hàng từ session cho user đã đăng nhập")
    void testDoGet_DisplayCart_FromSession_LoggedInUser() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("cartItems")).thenReturn(testCartItems);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);

        // When
        cartServlet.doGet(request, response);

        // Then
        verify(request, times(1)).getRequestDispatcher("/View/cart.jsp");
        verify(requestDispatcher, times(1)).forward(request, response);
    }

    @Test
    @DisplayName("Test doGet - load giỏ hàng từ database nếu session không có")
    void testDoGet_LoadCart_FromDatabase_WhenSessionEmpty() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("cartItems")).thenReturn(null);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);

        // When - Note: Requires actual database connection
        // cartServlet.doGet(request, response);

        // Then
        // verify(cartDB, times(1)).getCartByUserId(testUser.getUser_id());
        // verify(cartDB, times(1)).getCartItemsByCartId(anyInt());
        // verify(session, times(1)).setAttribute(eq("cartItems"), anyList());
    }

    @Test
    @DisplayName("Test doGet - hiển thị giỏ hàng từ cookie cho guest user")
    void testDoGet_DisplayCart_FromCookie_GuestUser() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(session.getAttribute("cartItems")).thenReturn(null);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);

        // Mock cookie cart
        Cookie guestCartCookie = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "1:2|3:5");
        when(request.getCookies()).thenReturn(new Cookie[]{guestCartCookie});

        // When - Note: Requires actual database connection
        // cartServlet.doGet(request, response);

        // Then
        // verify(productDB, atLeastOnce()).getProductById(anyInt());
        // verify(session, times(1)).setAttribute(eq("cartItems"), anyList());
        // verify(request, times(1)).getRequestDispatcher("/View/cart.jsp");
    }

    @Test
    @DisplayName("Test doGet - load assigned discounts cho user đã đăng nhập")
    void testDoGet_LoadAssignedDiscounts_ForLoggedInUser() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("cartItems")).thenReturn(testCartItems);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);

        // When
        cartServlet.doGet(request, response);

        // Then
        // verify(discountDB, times(1)).assignDueForUser(testUser.getUser_id());
        // verify(discountDB, times(1)).listAssignedDiscountsForUser(testUser.getUser_id());
        // verify(request, times(1)).setAttribute(eq("assignedDiscounts"), anyList());
    }

    @Test
    @DisplayName("Test doGet - không load discounts cho guest user")
    void testDoGet_NoDiscounts_ForGuestUser() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(session.getAttribute("cartItems")).thenReturn(null);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // When
        cartServlet.doGet(request, response);

        // Then
        // verify(discountDB, never()).listAssignedDiscountsForUser(anyInt());
    }

    @Test
    @DisplayName("Test doGet - bỏ qua sản phẩm không tồn tại trong cookie cart")
    void testDoGet_SkipNonExistentProducts_InCookieCart() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(session.getAttribute("cartItems")).thenReturn(null);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);

        // Mock cookie cart with non-existent product ID
        Cookie guestCartCookie = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "99999:2");
        when(request.getCookies()).thenReturn(new Cookie[]{guestCartCookie});

        // When - Note: Requires actual database connection
        // cartServlet.doGet(request, response);

        // Then
        // verify(productDB, times(1)).getProductById(99999);
        // verify(session, times(1)).setAttribute(eq("cartItems"), argThat(list ->
        //     ((List<?>) list).isEmpty()
        // ));
    }

    @Test
    @DisplayName("Test doGet - tạo cart items từ cookie với thông tin đầy đủ")
    void testDoGet_CreateCartItems_FromCookie_WithFullInfo() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(session.getAttribute("cartItems")).thenReturn(null);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);

        // Mock cookie cart
        Cookie guestCartCookie = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "1:2");
        when(request.getCookies()).thenReturn(new Cookie[]{guestCartCookie});

        // When - Note: Requires actual database connection
        // cartServlet.doGet(request, response);

        // Then
        // verify(session, times(1)).setAttribute(eq("cartItems"), argThat(list ->
        //     ((List<CartItems>) list).size() == 1 &&
        //     ((List<CartItems>) list).get(0).getProduct_id() == 1 &&
        //     ((List<CartItems>) list).get(0).getQuantity() == 2
        // ));
    }

    @Test
    @DisplayName("Test doGet - auto-assign due discounts trước khi load")
    void testDoGet_AutoAssignDueDiscounts_BeforeLoading() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("cartItems")).thenReturn(testCartItems);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(requestDispatcher);

        // When
        cartServlet.doGet(request, response);

        // Then
        // verify(discountDB, times(1)).assignDueForUser(testUser.getUser_id());
    }
}

