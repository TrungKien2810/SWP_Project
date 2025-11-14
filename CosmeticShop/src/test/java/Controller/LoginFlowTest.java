package Controller;

import DAO.CartDB;
import DAO.ProductDB;
import DAO.UserDB;
import Model.Cart;
import Model.CartItems;
import Model.Product;
import Model.user;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Use case tests cho servlet đăng nhập (UC-002: User Login).
 * Bao phủ các luồng chính/ngoại lệ: email không hợp lệ, bỏ trống, user không tồn tại,
 * đăng nhập thành công và hợp nhất giỏ hàng guest.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UC-002: Login Servlet Use Cases")
class LoginFlowTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private login servlet;

    @BeforeEach
    void setUp() {
        servlet = new login();
    }

    @Test
    @DisplayName("GET /login -> forward tới trang đăng nhập")
    void shouldForwardToLoginPageOnGet() throws Exception {
        when(request.getRequestDispatcher("/View/log.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/View/log.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST /login với email sai format -> đặt thông báo lỗi và redirect")
    void shouldRejectInvalidEmailFormat() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("invalid-email");
        when(request.getParameter("password")).thenReturn("pass");
        when(request.getContextPath()).thenReturn("/app");

        servlet.doPost(request, response);

        verify(session).setAttribute("loginErrorMsg", "Email không hợp lệ! Vui lòng nhập địa chỉ Gmail.");
        verify(response).sendRedirect("/app/login");
        verifyNoMoreInteractions(response);
    }

    @Test
    @DisplayName("POST /login với trường rỗng -> đặt thông báo lỗi và redirect")
    void shouldRejectEmptyCredentials() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("test@gmail.com");
        when(request.getParameter("password")).thenReturn("");
        when(request.getContextPath()).thenReturn("/shop");

        servlet.doPost(request, response);

        verify(session).setAttribute("loginErrorMsg", "Vui lòng nhập đầy đủ email và mật khẩu!");
        verify(response).sendRedirect("/shop/login");
    }

    @Test
    @DisplayName("POST /login với email chưa đăng ký -> trả về lỗi và redirect")
    void shouldNotifyWhenUserNotFound() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("missing@gmail.com");
        when(request.getParameter("password")).thenReturn("secret123");
        when(request.getContextPath()).thenReturn("/shop");

        try (MockedConstruction<UserDB> mockedUserDb = mockConstruction(UserDB.class, (mock, context) -> {
            when(mock.getUserByEmail("missing@gmail.com")).thenReturn(null);
        })) {
            servlet.doPost(request, response);
        }

        verify(session).setAttribute("loginErrorMsg", "Tài khoản không tồn tại. Bạn đã đăng ký chưa?");
        verify(response).sendRedirect("/shop/login");
    }

    @Test
    @DisplayName("POST /login thành công -> hợp nhất cart guest vào cart user")
    void shouldMergeGuestCartOnSuccessfulLogin() throws Exception {
        String email = "user@gmail.com";
        String password = "secret123";
        user account = new user(1, "tester", email, "0123456789", "hash", "USER", LocalDateTime.now());
        Cart persistedCart = new Cart(10, account.getUser_id(), LocalDateTime.now(), LocalDateTime.now());
        List<CartItems> emptyCartItems = new ArrayList<>();
        List<CartItems> mergedItems = List.of(new CartItems(5, persistedCart.getCart_id(), 42, 3, 200_000d));
        Product product = new Product(42, "Serum", 200_000d, 50, "Moisture", "serum.jpg", 1);

        Cookie guestCartCookie = new Cookie("guest_cart", "42:3");

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/shop");
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
        when(request.getParameter("remember")).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{guestCartCookie});

        try (MockedConstruction<UserDB> mockedUserDb = mockConstruction(UserDB.class, (mock, context) -> {
                 when(mock.getUserByEmail(email)).thenReturn(account);
                 when(mock.login(email, password)).thenReturn(true);
             });
             MockedConstruction<CartDB> mockedCartDb = mockConstruction(CartDB.class, (mock, context) -> {
                 when(mock.getCartByUserId(account.getUser_id())).thenReturn(null, persistedCart);
                 when(mock.getCartItemsByCartId(persistedCart.getCart_id()))
                         .thenReturn(emptyCartItems, mergedItems);
             });
             MockedConstruction<ProductDB> mockedProductDb = mockConstruction(ProductDB.class, (mock, context) -> {
                 when(mock.getProductById(42)).thenReturn(product);
             })) {

            servlet.doPost(request, response);

            CartDB cartDbMock = mockedCartDb.constructed().get(0);
            verify(cartDbMock).addNewCart(account.getUser_id());
            verify(cartDbMock).addCartItems(eq(persistedCart.getCart_id()), eq(42), eq(3), eq(product.getDiscountedPrice()));
        }

        verify(session).setAttribute("user", account);
        verify(session).setAttribute("cartItems", mergedItems);
        verify(session).setAttribute(eq("loginSuccessMsg"), contains("Đăng nhập thành công"));

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, atLeastOnce()).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getAllValues())
                .anySatisfy(cookie -> {
                    assertThat(cookie.getName()).isEqualTo("guest_cart");
                    assertThat(cookie.getMaxAge()).isZero();
                });

        verify(response).sendRedirect("/shop/View/home.jsp");
    }

    @Test
    @DisplayName("POST /login với remember me -> lưu cookie đăng nhập")
    void shouldSetRememberMeCookies() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/shop");
        when(request.getParameter("email")).thenReturn("remember@gmail.com");
        when(request.getParameter("password")).thenReturn("secret123");
        when(request.getParameter("remember")).thenReturn("on");
        when(request.getCookies()).thenReturn(new Cookie[]{});

        user account = new user(20, "remember", "remember@gmail.com", "000", "hash", "USER", LocalDateTime.now());
        Cart cart = new Cart(33, 20, LocalDateTime.now(), LocalDateTime.now());

        try (MockedConstruction<UserDB> mockedUserDb = mockConstruction(UserDB.class, (mock, context) -> {
                 when(mock.getUserByEmail("remember@gmail.com")).thenReturn(account);
                 when(mock.login("remember@gmail.com", "secret123")).thenReturn(true);
             });
             MockedConstruction<CartDB> mockedCartDb = mockConstruction(CartDB.class, (mock, context) -> {
                 when(mock.getCartByUserId(account.getUser_id())).thenReturn(cart);
                 when(mock.getCartItemsByCartId(cart.getCart_id())).thenReturn(new ArrayList<>());
             });
             MockedConstruction<ProductDB> ignored = mockConstruction(ProductDB.class)) {

            servlet.doPost(request, response);
        }

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getAllValues())
                .anySatisfy(cookie -> {
                    if (cookie.getName().equals("email")) {
                        assertThat(cookie.getValue()).isEqualTo("remember@gmail.com");
                        assertThat(cookie.getMaxAge()).isPositive();
                    }
                });

        verify(response).sendRedirect("/shop/View/home.jsp");
    }
}

