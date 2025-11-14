package Controller;

import Model.user;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.PromotionService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DiscountController Tests")
class DiscountControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private DiscountController controller;

    @BeforeEach
    void setUp() {
        controller.setPromotionService(promotionService);
    }

    @Test
    @DisplayName("doGet /my-promos should assign and forward when user logged in")
    void shouldForwardMyPromosForLoggedInUser() throws ServletException, IOException {
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        user mockUser = new user();
        mockUser.setUser_id(5);

        when(request.getServletPath()).thenReturn("/my-promos");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(mockUser);
        when(request.getRequestDispatcher("/View/my-discounts.jsp")).thenReturn(dispatcher);
        when(promotionService.listAssignedDiscountsForUser(5)).thenReturn(Collections.emptyList());

        controller.doGet(request, response);

        verify(promotionService).assignDueForUser(5);
        verify(promotionService).listAssignedDiscountsForUser(5);
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("doGet /my-promos should redirect to login when session missing")
    void shouldForwardToLoginWhenNotLoggedIn() throws ServletException, IOException {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getServletPath()).thenReturn("/my-promos");
        when(request.getSession(false)).thenReturn(null);
        when(request.getRequestDispatcher("/View/log.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        verify(dispatcher).forward(request, response);
        verifyNoInteractions(promotionService);
    }

    @Test
    @DisplayName("doGet discounts list should load all discounts")
    void shouldLoadDiscountList() throws ServletException, IOException {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getServletPath()).thenReturn("/discounts");
        when(request.getParameter("action")).thenReturn(null);
        when(promotionService.listAllDiscounts()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/View/discount-manager.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        verify(promotionService).listAllDiscounts();
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("doPost create should delegate to PromotionService and redirect")
    void shouldCreateDiscountAndRedirect() throws ServletException, IOException {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("code")).thenReturn("PROMO1");
        when(request.getParameter("name")).thenReturn("Promo name");
        when(request.getParameter("type")).thenReturn("PERCENTAGE");
        when(request.getParameter("value")).thenReturn("10");
        when(request.getParameter("start")).thenReturn("2025-11-01T00:00");
        when(request.getParameter("end")).thenReturn("2025-12-01T00:00");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        when(promotionService.existsByCode(eq("PROMO1"), any())).thenReturn(false);
        when(promotionService.createDiscount(
                anyString(), anyString(), anyString(),
                anyDouble(), any(), any(), any(), any(), anyBoolean(),
                any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);

        controller.doPost(request, response);

        verify(promotionService).createDiscount(
                anyString(), anyString(), anyString(),
                anyDouble(), any(), any(), any(), any(), anyBoolean(),
                any(), any(), any(), any(), any(), any(), any(), any());
        verify(response).sendRedirect("/CosmeticShop/admin?action=discounts");
    }

    @Test
    @DisplayName("doPost delete should call PromotionService")
    void shouldDeleteDiscount() throws ServletException, IOException {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("12");
        when(request.getContextPath()).thenReturn("/CosmeticShop");

        controller.doPost(request, response);

        verify(promotionService).deleteDiscount(12);
        verify(response).sendRedirect("/CosmeticShop/admin?action=discounts");
    }
}

