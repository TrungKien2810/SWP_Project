package Controller;

import E2E.TestDataHelper;
import Model.user;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CartPromotionService;
import service.dto.PromotionApplicationResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Use case tests cho servlet áp dụng mã giảm giá (UC-009).
 * Bao phủ các nhánh: gỡ mã, chưa login, mã không hợp lệ, không đủ điều kiện, áp dụng thành công.
 * 
 * Note: Sử dụng TestDataHelper để lấy data động từ database thay vì hardcode.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UC-009: Apply Promotion Servlet Use Cases")
class ApplyPromotionUseCaseTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private CartPromotionService cartPromotionService;

    private ApplyPromotion servlet;
    
    // Sample user từ database (lấy một lần để dùng cho nhiều test)
    private static user sampleUser;

    @BeforeEach
    void setUp() {
        servlet = new ApplyPromotion();
        servlet.setCartPromotionService(cartPromotionService);
        
        when(request.getContextPath()).thenReturn("/CosmeticShop");
        
        // Lấy sample user từ database một lần (có thể cache)
        if (sampleUser == null) {
            sampleUser = TestDataHelper.getRandomUser();
            if (sampleUser == null) {
                // Fallback nếu không có user trong DB
                sampleUser = new user(1, "testuser", "test@gmail.com", "0123456789", 
                    "password", "USER", LocalDateTime.now());
            }
        }
    }

    @Test
    @DisplayName("Gỡ mã giảm giá -> lưu mã cuối cùng để khôi phục và redirect với thông báo")
    void shouldHandleRemoveDiscount() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("removeDiscount")).thenReturn("true");
        lenient().when(request.getParameter("promoCode")).thenReturn(null);
        lenient().when(session.getAttribute("cartId")).thenReturn(0);
        lenient().when(session.getAttribute("appliedDiscountCode")).thenReturn("SPRING10");

        servlet.doPost(request, response);

        verify(session).removeAttribute("appliedDiscountCode");
        verify(session).removeAttribute("appliedDiscountAmount");
        verify(session).setAttribute("lastRemovedDiscountCode", "SPRING10");
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Không có cart id -> báo lỗi")
    void shouldFailWhenCartIdMissing() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("promoCode")).thenReturn("CODE");
        when(session.getAttribute("cartId")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("User chưa đăng nhập -> báo lỗi yêu cầu login")
    void shouldRequireLogin() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(99);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getParameter("promoCode")).thenReturn("CODE");

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Mã giảm giá không hợp lệ -> xóa khỏi session và báo lỗi")
    void shouldRejectInvalidCode() throws Exception {
        // Dùng sample user từ database
        user account = sampleUser;

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(7);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("INVALID");
        when(cartPromotionService.applyPromotion(account.getUser_id(), 7, "INVALID"))
                .thenReturn(PromotionApplicationResult.failure("Mã giảm giá không hợp lệ hoặc đã hết hạn."));

        servlet.doPost(request, response);

        verify(session).removeAttribute("appliedDiscountCode");
        verify(session).removeAttribute("appliedDiscountAmount");
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("User không đủ quyền sử dụng mã -> báo lỗi và không set session")
    void shouldDenyWhenUserNotEligible() throws Exception {
        // Dùng sample user từ database
        user account = sampleUser;
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(15);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("SALE50");
        when(cartPromotionService.applyPromotion(account.getUser_id(), 15, "SALE50"))
                .thenReturn(PromotionApplicationResult.failure("Bạn không có quyền sử dụng mã giảm giá này."));

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Không đạt min order -> báo lỗi")
    void shouldRejectWhenSubtotalBelowMinimum() throws Exception {
        // Dùng sample user từ database
        user account = sampleUser;
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(77);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("SALE50");
        when(cartPromotionService.applyPromotion(account.getUser_id(), 77, "SALE50"))
                .thenReturn(PromotionApplicationResult.failure("Đơn hàng chưa đạt tối thiểu để áp dụng mã giảm giá."));

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Áp dụng mã phần trăm với giới hạn tối đa -> set session và redirect thành công")
    void shouldApplyPercentageDiscountWithMaxCap() throws Exception {
        // Dùng sample user từ database
        user account = sampleUser;
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(101);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("SALE50");
        when(cartPromotionService.applyPromotion(account.getUser_id(), 101, "SALE50"))
                .thenReturn(PromotionApplicationResult.success("SALE50", 150_000d, "Áp dụng mã thành công: SALE50"));

        servlet.doPost(request, response);

        ArgumentCaptor<Double> discountCaptor = ArgumentCaptor.forClass(Double.class);
        verify(session).setAttribute(eq("appliedDiscountCode"), eq("SALE50"));
        verify(session).setAttribute(eq("appliedDiscountAmount"), discountCaptor.capture());
        assertThat(discountCaptor.getValue()).isEqualTo(150_000d);
        verify(session).removeAttribute("lastRemovedDiscountCode");
        verify(response).sendRedirect(anyString());
    }

}

