package Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class cho login servlet
 * 
 * Note: Đây là ví dụ test cho servlet. 
 * Cần mock các dependencies như UserDB, CartDB, etc.
 */
@DisplayName("Login Servlet Tests")
class LoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private login loginServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginServlet = new login();
    }

    @Test
    @DisplayName("Test doGet - should forward to log.jsp")
    void testDoGet() throws Exception {
        // Given
        when(request.getRequestDispatcher("/View/log.jsp")).thenReturn(requestDispatcher);

        // When
        loginServlet.doGet(request, response);

        // Then
        verify(request, times(1)).getRequestDispatcher("/View/log.jsp");
        verify(requestDispatcher, times(1)).forward(request, response);
    }

    @Test
    @DisplayName("Test doPost với email và password hợp lệ")
    @Disabled("Requires mocking UserDB and database connection")
    void testDoPost_ValidCredentials() throws Exception {
        // TODO: Implement test với mocked UserDB
        // Given
        // when(request.getParameter("email")).thenReturn("test@example.com");
        // when(request.getParameter("password")).thenReturn("password123");
        // when(request.getSession()).thenReturn(session);
        // 
        // // Mock UserDB
        // UserDB userDB = mock(UserDB.class);
        // user user = new user(1, "testuser", "test@example.com", 
        //         "123", "hashedpassword", "USER", LocalDateTime.now());
        // when(userDB.getUserByEmail("test@example.com")).thenReturn(user);
        //
        // // When
        // loginServlet.doPost(request, response);
        //
        // // Then
        // verify(session, times(1)).setAttribute(eq("user"), any(user.class));
        // verify(response, times(1)).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Test doPost với thông tin đăng nhập không hợp lệ")
    @Disabled("Requires mocking UserDB")
    void testDoPost_InvalidCredentials() throws Exception {
        // TODO: Implement test
    }

    // TODO: Thêm các test cases khác:
    // - testDoPost với email không tồn tại
    // - testDoPost với password sai
    // - testDoPost với merge guest cart
    // - testDoPost với remember me
}

