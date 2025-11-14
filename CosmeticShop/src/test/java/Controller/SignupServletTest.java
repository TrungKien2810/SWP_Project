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
 * Tests cho signup servlet
 */
@DisplayName("Signup Servlet Tests")
class SignupServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private signup signupServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        signupServlet = new signup();
    }

    @Test
    @DisplayName("Test doGet - forward đến register.jsp")
    void testDoGet_ShouldForwardToRegisterPage() throws Exception {
        // Given
        when(request.getRequestDispatcher("/View/register.jsp")).thenReturn(requestDispatcher);

        // When
        signupServlet.doGet(request, response);

        // Then
        verify(request, times(1)).getRequestDispatcher("/View/register.jsp");
        verify(requestDispatcher, times(1)).forward(request, response);
    }

    @Test
    @DisplayName("Test doPost - invalid email should set error message and redirect")
    void testDoPost_InvalidEmail_ShouldSetErrorAndRedirect() throws Exception {
        // Given
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("email")).thenReturn("invalid-email");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirm-password")).thenReturn("password123");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        // When
        signupServlet.doPost(request, response);

        // Then
        verify(session).setAttribute(eq("signupErrorMsg"), anyString());
        verify(response).sendRedirect("/CosmeticShop/signup");
    }
}

