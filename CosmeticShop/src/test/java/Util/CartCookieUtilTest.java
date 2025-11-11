package Util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class cho CartCookieUtil
 */
@DisplayName("CartCookieUtil Tests")
class CartCookieUtilTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test readCartMap với cookie hợp lệ")
    void testReadCartMap_ValidCookie() {
        // Given
        Cookie cookie = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "1:2|3:5|7:1");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // When
        Map<Integer, Integer> cartMap = CartCookieUtil.readCartMap(request);

        // Then
        assertThat(cartMap).isNotNull();
        assertThat(cartMap).hasSize(3);
        assertThat(cartMap.get(1)).isEqualTo(2);
        assertThat(cartMap.get(3)).isEqualTo(5);
        assertThat(cartMap.get(7)).isEqualTo(1);
    }

    @Test
    @DisplayName("Test readCartMap không có cookie")
    void testReadCartMap_NoCookie() {
        // Given
        when(request.getCookies()).thenReturn(null);

        // When
        Map<Integer, Integer> cartMap = CartCookieUtil.readCartMap(request);

        // Then
        assertThat(cartMap).isNotNull();
        assertThat(cartMap).isEmpty();
    }

    @Test
    @DisplayName("Test readCartMap với cookie rỗng")
    void testReadCartMap_EmptyCookie() {
        // Given
        Cookie cookie = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // When
        Map<Integer, Integer> cartMap = CartCookieUtil.readCartMap(request);

        // Then
        assertThat(cartMap).isNotNull();
        assertThat(cartMap).isEmpty();
    }

    @Test
    @DisplayName("Test readCartMap với format không hợp lệ")
    void testReadCartMap_InvalidFormat() {
        // Given
        Cookie cookie = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "invalid|format|data");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // When
        Map<Integer, Integer> cartMap = CartCookieUtil.readCartMap(request);

        // Then
        assertThat(cartMap).isNotNull();
        assertThat(cartMap).isEmpty();
    }

    @Test
    @DisplayName("Test writeCartMap")
    void testWriteCartMap() {
        // Given
        Map<Integer, Integer> cartMap = new HashMap<>();
        cartMap.put(1, 2);
        cartMap.put(3, 5);
        cartMap.put(7, 1);

        // When
        CartCookieUtil.writeCartMap(response, cartMap);

        // Then
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("Test writeCartMap với map rỗng")
    void testWriteCartMap_EmptyMap() {
        // Given
        Map<Integer, Integer> cartMap = new HashMap<>();

        // When
        CartCookieUtil.writeCartMap(response, cartMap);

        // Then
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("Test writeCartMap bỏ qua quantity <= 0")
    void testWriteCartMap_IgnoreZeroQuantity() {
        // Given
        Map<Integer, Integer> cartMap = new HashMap<>();
        cartMap.put(1, 2);
        cartMap.put(3, 0);  // Should be ignored
        cartMap.put(5, -1); // Should be ignored
        cartMap.put(7, 1);

        // When
        CartCookieUtil.writeCartMap(response, cartMap);

        // Then
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("Test getCookie method")
    void testGetCookie() {
        // Given
        Cookie cookie1 = new Cookie("other_cookie", "value");
        Cookie cookie2 = new Cookie(CartCookieUtil.GUEST_CART_COOKIE, "1:2");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2});

        // When
        Cookie result = CartCookieUtil.getCookie(request, CartCookieUtil.GUEST_CART_COOKIE);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(CartCookieUtil.GUEST_CART_COOKIE);
        assertThat(result.getValue()).isEqualTo("1:2");
    }

    @Test
    @DisplayName("Test getCookie không tìm thấy")
    void testGetCookie_NotFound() {
        // Given
        Cookie cookie = new Cookie("other_cookie", "value");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // When
        Cookie result = CartCookieUtil.getCookie(request, CartCookieUtil.GUEST_CART_COOKIE);

        // Then
        assertThat(result).isNull();
    }
}



