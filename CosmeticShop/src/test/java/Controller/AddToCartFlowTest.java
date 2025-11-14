package Controller;

import DAO.CartDB;
import DAO.ProductDB;
import E2E.TestDataHelper;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Use case tests cho servlet thêm vào giỏ hàng (UC-005).
 * Bao phủ các nhánh: ID sai, user đăng nhập/guest, clamp tồn kho, buy now.
 * 
 * Note: Sử dụng TestDataHelper để lấy data động từ database thay vì hardcode.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UC-005: Add To Cart Servlet Use Cases")
class AddToCartFlowTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private addToCart servlet;
    
    // Sample data từ database (lấy một lần để dùng cho nhiều test)
    private static Product sampleProduct;
    private static user sampleUser;
    private static Integer sampleProductId;
    private static Integer sampleUserId;

    @BeforeEach
    void setUp() {
        servlet = new addToCart();
        
        // Lấy sample data từ database một lần (có thể cache)
        if (sampleProduct == null) {
            sampleProduct = TestDataHelper.getRandomProductInStock();
            if (sampleProduct == null) {
                // Fallback nếu không có product trong DB
                sampleProduct = new Product(1, "Test Product", 200_000d, 10, "Test Description", "test.jpg", 1);
            }
            sampleProductId = sampleProduct.getProductId();
        }
        
        if (sampleUser == null) {
            sampleUser = TestDataHelper.getRandomUser();
            if (sampleUser == null) {
                // Fallback nếu không có user trong DB
                sampleUser = new user(1, "testuser", "test@gmail.com", "0123456789", 
                    "password", "USER", LocalDateTime.now());
            }
            sampleUserId = sampleUser.getUser_id();
        }
    }

    @Test
    @DisplayName("ID sản phẩm không hợp lệ -> forward về collection với lỗi")
    void shouldHandleInvalidProductId() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("id")).thenReturn("abc");
        when(request.getRequestDispatcher("/View/collection.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("error"), contains("For input string"));
        verify(dispatcher).forward(request, response);
        verifyNoMoreInteractions(response);
    }

    @Test
    @DisplayName("User đăng nhập thêm sản phẩm mới -> tạo cart item và redirect về trang trước")
    void shouldAddNewItemForAuthenticatedUser() throws Exception {
        // Dùng sample data từ database
        user account = sampleUser;
        Cart cart = new Cart(11, account.getUser_id(), LocalDateTime.now(), LocalDateTime.now());
        Product product = sampleProduct;
        int productId = product.getProductId();
        int quantity = 2;
        
        List<CartItems> refreshedItems = new ArrayList<>();
        refreshedItems.add(new CartItems(1, cart.getCart_id(), productId, quantity, product.getDiscountedPrice()));

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("id")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getParameter("buyNow")).thenReturn(null);
        when(request.getHeader("referer")).thenReturn("http://test/products");

        try (MockedConstruction<CartDB> mockedCartDb = mockConstruction(CartDB.class, (mock, context) -> {
                 when(mock.getOrCreateCartByUserId(account.getUser_id())).thenReturn(cart);
                 when(mock.getCartItemsByCartId(cart.getCart_id()))
                         .thenReturn(new ArrayList<>(), new ArrayList<>(), refreshedItems);
             });
             MockedConstruction<ProductDB> mockedProductDb = mockConstruction(ProductDB.class, (mock, context) -> {
                 when(mock.getProductById(productId)).thenReturn(product);
             })) {

            servlet.doGet(request, response);

            CartDB cartDbMock = mockedCartDb.constructed().get(0);
            verify(cartDbMock).addCartItems(eq(cart.getCart_id()), eq(productId), eq(quantity), eq(product.getDiscountedPrice()));
        }

        ArgumentCaptor<List<CartItems>> cartItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(session).setAttribute(eq("cartItems"), cartItemsCaptor.capture());
        assertThat(cartItemsCaptor.getValue())
                .extracting(CartItems::getProduct_id, CartItems::getQuantity)
                .containsExactly(tuple(productId, quantity));
        verify(session).setAttribute(eq("cartSuccessMsg"), contains("giỏ hàng"));

        verify(response).sendRedirect("http://test/products");
    }

    @Test
    @DisplayName("User đăng nhập thêm sản phẩm đã có -> cập nhật số lượng theo tồn kho")
    void shouldClampQuantityWhenItemAlreadyExists() throws Exception {
        // Dùng sample data từ database
        user account = sampleUser;
        Cart cart = new Cart(21, account.getUser_id(), LocalDateTime.now(), LocalDateTime.now());
        Product product = sampleProduct;
        int productId = product.getProductId();
        int stock = product.getStock();
        int existingQuantity = 3;
        int addQuantity = 4;
        int finalQuantity = Math.min(existingQuantity + addQuantity, stock); // Clamp theo stock

        CartItems existing = new CartItems(2, cart.getCart_id(), productId, existingQuantity, product.getDiscountedPrice());
        List<CartItems> existingList = new ArrayList<>();
        existingList.add(existing);
        List<CartItems> refreshedItems = new ArrayList<>();
        refreshedItems.add(new CartItems(3, cart.getCart_id(), productId, finalQuantity, product.getDiscountedPrice()));

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("id")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(addQuantity));
        when(request.getParameter("buyNow")).thenReturn(null);
        when(request.getHeader("referer")).thenReturn("http://test/detail");

        try (MockedConstruction<CartDB> mockedCartDb = mockConstruction(CartDB.class, (mock, context) -> {
                 when(mock.getOrCreateCartByUserId(account.getUser_id())).thenReturn(cart);
                 when(mock.getCartItemsByCartId(cart.getCart_id()))
                         .thenReturn(existingList, existingList, refreshedItems);
             });
             MockedConstruction<ProductDB> mockedProductDb = mockConstruction(ProductDB.class, (mock, context) -> {
                 when(mock.getProductById(productId)).thenReturn(product);
             })) {

            servlet.doGet(request, response);

            CartDB cartDbMock = mockedCartDb.constructed().get(0);
            verify(cartDbMock).updateQuantityAddToCart(cart.getCart_id(), productId, finalQuantity);
            verify(cartDbMock, never()).addCartItems(eq(cart.getCart_id()), eq(productId), anyInt(), anyDouble());
        }

        ArgumentCaptor<List<CartItems>> cartItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(session).setAttribute(eq("cartItems"), cartItemsCaptor.capture());
        assertThat(cartItemsCaptor.getValue())
                .extracting(CartItems::getProduct_id, CartItems::getQuantity)
                .containsExactly(tuple(productId, finalQuantity));
        verify(response).sendRedirect("http://test/detail");
        verify(session).setAttribute(eq("cartSuccessMsg"), contains("giỏ hàng"));
    }

    @Test
    @DisplayName("Guest thêm sản phẩm -> ghi cookie và redirect")
    void shouldPersistCartInCookieForGuest() throws Exception {
        // Dùng sample data từ database
        Product product = sampleProduct;
        int productId = product.getProductId();
        Cookie existing = new Cookie("guest_cart", productId + ":2");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getParameter("id")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn("");
        lenient().when(request.getParameter("buyNow")).thenReturn(null);
        when(request.getHeader("referer")).thenReturn("http://test/shop");
        when(request.getCookies()).thenReturn(new Cookie[]{existing});

        try (MockedConstruction<ProductDB> mockedProductDb = mockConstruction(ProductDB.class, (mock, context) -> {
            when(mock.getProductById(productId)).thenReturn(product);
        })) {
            servlet.doGet(request, response);
        }

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, atLeastOnce()).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getAllValues())
                .anySatisfy(cookie -> {
                    assertThat(cookie.getName()).isEqualTo("guest_cart");
                    assertThat(cookie.getValue()).contains(productId + ":3");
                });

        verify(session).setAttribute(eq("cartSuccessMsg"), contains("giỏ hàng"));
        verify(response).sendRedirect("http://test/shop");
    }

    @Test
    @DisplayName("\"Mua ngay\" cho guest -> redirect tới trang cart")
    void buyNowShouldRedirectGuestToCart() throws Exception {
        // Dùng sample data từ database
        Product product = sampleProduct;
        int productId = product.getProductId();
        
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getParameter("id")).thenReturn(String.valueOf(productId));
        lenient().when(request.getParameter("buyNow")).thenReturn("true");
        lenient().when(request.getParameter("quantity")).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{});
        when(request.getContextPath()).thenReturn("/shop");

        try (MockedConstruction<ProductDB> mockedProductDb = mockConstruction(ProductDB.class, (mock, context) -> {
            when(mock.getProductById(productId)).thenReturn(product);
        })) {
            servlet.doGet(request, response);
        }

        verify(response).sendRedirect("/shop/cart");
        verify(session).setAttribute(eq("cartSuccessMsg"), contains("giỏ hàng"));
    }
}

