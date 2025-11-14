package Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests cho logout servlet
 */
@DisplayName("Logout Servlet Tests")
class LogoutServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private logout logoutServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logoutServlet = new logout();
    }

    @Test
    @DisplayName("Test doGet - invalidate session và forward đến home")
    void testDoGet_ShouldInvalidateSessionAndForwardToHome() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/View/home.jsp")).thenReturn(requestDispatcher);

        // When
        logoutServlet.doGet(request, response);

        // Then
        verify(session, times(1)).invalidate();
        verify(request, times(1)).getRequestDispatcher("/View/home.jsp");
        verify(requestDispatcher, times(1)).forward(request, response);
    }

    @Test
    @DisplayName("Test doPost - gọi processRequest")
    void testDoPost_ShouldCallProcessRequest() throws Exception {
        // Given
        when(response.getWriter()).thenReturn(new java.io.PrintWriter(new java.io.StringWriter()));

        // When
        logoutServlet.doPost(request, response);

        // Then
        verify(response, times(1)).setContentType("text/html;charset=UTF-8");
    }

    @Test
    @DisplayName("Test getServletInfo - trả về description")
    void testGetServletInfo() {
        // When
        String info = logoutServlet.getServletInfo();

        // Then
        assert info != null;
    }
}

