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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.CartPromotionService;
import service.dto.PromotionApplicationResult;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("ApplyPromotion servlet tests")
class ApplyPromotionTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private CartPromotionService cartPromotionService;

    private ApplyPromotion servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new ApplyPromotion();
        servlet.setCartPromotionService(cartPromotionService);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(dispatcher);
    }

    @Test
    @DisplayName("removeDiscount=true sẽ xóa mã và forward")
    void shouldRemoveDiscount() throws ServletException, IOException {
        when(request.getParameter("removeDiscount")).thenReturn("true");
        when(session.getAttribute("appliedDiscountCode")).thenReturn("PROMO");

        servlet.doPost(request, response);

        verify(session).removeAttribute("appliedDiscountCode");
        verify(session).removeAttribute("appliedDiscountAmount");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Không đăng nhập -> yêu cầu login")
    void shouldRequireLogin() throws ServletException, IOException {
        when(request.getParameter("removeDiscount")).thenReturn(null);
        when(session.getAttribute("cartId")).thenReturn(5);
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doPost(request, response);

        verify(dispatcher).forward(request, response);
        verifyNoInteractions(cartPromotionService);
    }

    @Test
    @DisplayName("Service trả lỗi -> gỡ mã và hiển thị lỗi")
    void shouldHandleServiceFailure() throws ServletException, IOException {
        user u = new user();
        u.setUser_id(2);
        when(session.getAttribute("cartId")).thenReturn(10);
        when(session.getAttribute("user")).thenReturn(u);
        when(request.getParameter("promoCode")).thenReturn("PROMO");
        when(cartPromotionService.applyPromotion(2, 10, "PROMO"))
                .thenReturn(PromotionApplicationResult.failure("Lỗi"));

        servlet.doPost(request, response);

        verify(session).removeAttribute("appliedDiscountCode");
        verify(session).removeAttribute("appliedDiscountAmount");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Service thành công -> lưu mã và forward")
    void shouldApplyDiscountSuccessfully() throws ServletException, IOException {
        user u = new user();
        u.setUser_id(2);
        when(session.getAttribute("cartId")).thenReturn(10);
        when(session.getAttribute("user")).thenReturn(u);
        when(request.getParameter("promoCode")).thenReturn("PROMO");
        when(cartPromotionService.applyPromotion(2, 10, "PROMO"))
                .thenReturn(PromotionApplicationResult.success("PROMO", 50_000, "Áp dụng mã thành công"));

        servlet.doPost(request, response);

        verify(session).setAttribute("appliedDiscountCode", "PROMO");
        verify(session).setAttribute("appliedDiscountAmount", 50_000.0);
        verify(dispatcher).forward(request, response);
    }
}

