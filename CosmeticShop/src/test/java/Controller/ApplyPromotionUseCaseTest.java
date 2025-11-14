package Controller;

import DAO.CartDB;
import DAO.DiscountDB;
import E2E.TestDataHelper;
import Model.Discount;
import Model.user;
import jakarta.servlet.RequestDispatcher;
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

import java.sql.Timestamp;
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
    private RequestDispatcher dispatcher;

    private ApplyPromotion servlet;
    
    // Sample user từ database (lấy một lần để dùng cho nhiều test)
    private static user sampleUser;

    @BeforeEach
    void setUp() {
        servlet = new ApplyPromotion();
        
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

    private void setCommonDispatch() {
        when(request.getRequestDispatcher("/View/cart.jsp")).thenReturn(dispatcher);
    }

    @Test
    @DisplayName("Gỡ mã giảm giá -> lưu mã cuối cùng để khôi phục và forward với thông báo")
    void shouldHandleRemoveDiscount() throws Exception {
        setCommonDispatch();
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("removeDiscount")).thenReturn("true");
        lenient().when(request.getParameter("promoCode")).thenReturn(null);
        lenient().when(session.getAttribute("cartId")).thenReturn(0);
        lenient().when(session.getAttribute("appliedDiscountCode")).thenReturn("SPRING10");

        servlet.doPost(request, response);

        verify(session).removeAttribute("appliedDiscountCode");
        verify(session).removeAttribute("appliedDiscountAmount");
        verify(session).setAttribute("lastRemovedDiscountCode", "SPRING10");
        verify(request).setAttribute("msg", "Đã xóa mã giảm giá: SPRING10");
        verify(dispatcher).forward(request, response);
        verifyNoInteractions(response);
    }

    @Test
    @DisplayName("Không có cart id -> báo lỗi")
    void shouldFailWhenCartIdMissing() throws Exception {
        setCommonDispatch();
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("promoCode")).thenReturn("CODE");
        when(session.getAttribute("cartId")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Không tìm thấy giỏ hàng.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("User chưa đăng nhập -> báo lỗi yêu cầu login")
    void shouldRequireLogin() throws Exception {
        setCommonDispatch();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(99);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getParameter("promoCode")).thenReturn("CODE");

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Vui lòng đăng nhập để sử dụng mã giảm giá.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Mã giảm giá không hợp lệ -> xóa khỏi session và báo lỗi")
    void shouldRejectInvalidCode() throws Exception {
        setCommonDispatch();
        // Dùng sample user từ database
        user account = sampleUser;

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(7);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("INVALID");

        try (MockedConstruction<DiscountDB> mockedDiscountDb = mockConstruction(DiscountDB.class, (mock, context) -> {
                 when(mock.validateAndGetDiscount("INVALID")).thenReturn(null);
             })) {
            servlet.doPost(request, response);
        }

        verify(session).removeAttribute("appliedDiscountCode");
        verify(session).removeAttribute("appliedDiscountAmount");
        verify(request).setAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("User không đủ quyền sử dụng mã -> báo lỗi và không set session")
    void shouldDenyWhenUserNotEligible() throws Exception {
        setCommonDispatch();
        // Dùng sample user từ database
        user account = sampleUser;
        Discount discount = createPercentDiscount();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(15);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("SALE50");

        try (MockedConstruction<DiscountDB> mockedDiscountDb = mockConstruction(DiscountDB.class, (mock, context) -> {
                 when(mock.validateAndGetDiscount("SALE50")).thenReturn(discount);
                 when(mock.canUserUseDiscount(account.getUser_id(), discount.getDiscountId())).thenReturn(false);
             });
             MockedConstruction<CartDB> mockedCartDb = mockConstruction(CartDB.class, (mock, context) -> {
                 when(mock.calculateCartTotal(15)).thenReturn(800_000d);
             })) {
            servlet.doPost(request, response);
        }

        verify(request).setAttribute("error", "Bạn không có quyền sử dụng mã giảm giá này.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Không đạt min order -> báo lỗi")
    void shouldRejectWhenSubtotalBelowMinimum() throws Exception {
        setCommonDispatch();
        // Dùng sample user từ database
        user account = sampleUser;
        Discount discount = createPercentDiscount();
        discount.setMinOrderAmount(1_000_000d);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(77);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("SALE50");

        try (MockedConstruction<DiscountDB> mockedDiscountDb = mockConstruction(DiscountDB.class, (mock, context) -> {
                 when(mock.validateAndGetDiscount("SALE50")).thenReturn(discount);
                 when(mock.canUserUseDiscount(account.getUser_id(), discount.getDiscountId())).thenReturn(true);
             });
             MockedConstruction<CartDB> mockedCartDb = mockConstruction(CartDB.class, (mock, context) -> {
                 when(mock.calculateCartTotal(77)).thenReturn(500_000d);
             })) {
            servlet.doPost(request, response);
        }

        verify(request).setAttribute("error", "Đơn hàng chưa đạt tối thiểu để áp dụng mã giảm giá.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Áp dụng mã phần trăm với giới hạn tối đa -> set session và forward thành công")
    void shouldApplyPercentageDiscountWithMaxCap() throws Exception {
        setCommonDispatch();
        // Dùng sample user từ database
        user account = sampleUser;
        Discount discount = createPercentDiscount();
        discount.setCode("SALE50");
        discount.setValue(20.0);
        discount.setMinOrderAmount(100_000d);
        discount.setMaxDiscountAmount(150_000d);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cartId")).thenReturn(101);
        when(session.getAttribute("user")).thenReturn(account);
        when(request.getParameter("promoCode")).thenReturn("SALE50");

        try (MockedConstruction<DiscountDB> mockedDiscountDb = mockConstruction(DiscountDB.class, (mock, context) -> {
                 when(mock.validateAndGetDiscount("SALE50")).thenReturn(discount);
                 when(mock.canUserUseDiscount(account.getUser_id(), discount.getDiscountId())).thenReturn(true);
             });
             MockedConstruction<CartDB> mockedCartDb = mockConstruction(CartDB.class, (mock, context) -> {
                 when(mock.calculateCartTotal(101)).thenReturn(1_500_000d);
             })) {
            servlet.doPost(request, response);
        }

        ArgumentCaptor<Double> discountCaptor = ArgumentCaptor.forClass(Double.class);
        verify(session).setAttribute(eq("appliedDiscountCode"), eq("SALE50"));
        verify(session).setAttribute(eq("appliedDiscountAmount"), discountCaptor.capture());
        assertThat(discountCaptor.getValue()).isEqualTo(150_000d);
        verify(session).removeAttribute("lastRemovedDiscountCode");
        verify(request).setAttribute("msg", "Áp dụng mã thành công: SALE50");
        verify(dispatcher).forward(request, response);
    }

    private Discount createPercentDiscount() {
        Discount discount = new Discount();
        discount.setDiscountId(123);
        discount.setType("PERCENTAGE");
        discount.setCode("SALE");
        discount.setValue(10.0);
        discount.setMinOrderAmount(0);
        discount.setStartDate(new Timestamp(System.currentTimeMillis()));
        discount.setEndDate(new Timestamp(System.currentTimeMillis() + 86_400_000));
        return discount;
    }
}

