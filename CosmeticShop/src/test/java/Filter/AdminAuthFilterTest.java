package Filter;

import Model.user;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests cho {@link AdminAuthFilter}.
 *
 * Kiểm tra các nhánh:
 * - Không có session -> redirect login
 * - Không có user trong session -> redirect login
 * - User không phải ADMIN -> redirect về home với thông báo lỗi
 * - User là ADMIN -> cho phép qua filter chain
 */
class AdminAuthFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FilterChain filterChain;

    private AdminAuthFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new AdminAuthFilter();

        when(request.getContextPath()).thenReturn("/CosmeticShop");
    }

    @Test
    @DisplayName("Should redirect to login when session is null")
    void shouldRedirectToLoginWhenNoSession() throws IOException, ServletException {
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/CosmeticShop/login");
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("Should redirect to login when session has no user attribute")
    void shouldRedirectToLoginWhenNoUser() throws IOException, ServletException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/CosmeticShop/login");
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("Should redirect to home when user is not admin")
    void shouldRedirectWhenNotAdmin() throws IOException, ServletException {
        user normalUser = new user();
        normalUser.setRole("USER");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(normalUser);

        filter.doFilter(request, response, filterChain);

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(redirectCaptor.capture());
        String redirectUrl = redirectCaptor.getValue();
        String decoded = java.net.URLDecoder.decode(
                redirectUrl,
                java.nio.charset.StandardCharsets.UTF_8
        );
        assertThat(decoded)
                .startsWith("/CosmeticShop/View/home.jsp")
                .contains("Không có quyền truy cập");
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("Should allow request when user is admin")
    void shouldAllowWhenAdmin() throws IOException, ServletException {
        user admin = new user();
        admin.setRole("ADMIN");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(admin);

        filter.doFilter(request, response, filterChain);

        verify(response, never()).sendRedirect(anyString());
        verify(filterChain, times(1)).doFilter(eq(request), eq(response));
    }
}


