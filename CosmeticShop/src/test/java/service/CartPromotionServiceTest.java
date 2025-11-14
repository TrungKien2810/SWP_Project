package service;

import DAO.CartDB;
import DAO.DiscountDB;
import Model.Discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.dto.PromotionApplicationResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("CartPromotionService Tests")
class CartPromotionServiceTest {

    private DiscountDB discountDB;
    private CartDB cartDB;
    private CartPromotionService service;

    @BeforeEach
    void setUp() {
        discountDB = mock(DiscountDB.class);
        cartDB = mock(CartDB.class);
        service = new DefaultCartPromotionService(discountDB, cartDB);
    }

    @Test
    @DisplayName("applyPromotion trả lỗi khi code rỗng")
    void shouldFailWhenCodeEmpty() {
        PromotionApplicationResult result = service.applyPromotion(1, 1, "  ");
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("không được để trống");
    }

    @Test
    @DisplayName("applyPromotion trả lỗi khi discount không tồn tại")
    void shouldFailWhenDiscountNotFound() {
        when(discountDB.validateAndGetDiscount("PROMO")).thenReturn(null);

        PromotionApplicationResult result = service.applyPromotion(1, 1, "PROMO");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("không hợp lệ");
    }

    @Test
    @DisplayName("applyPromotion trả lỗi khi user không có quyền")
    void shouldFailWhenUserNotAllowed() {
        Discount discount = createDiscount("PROMO", "PERCENTAGE", 10, 0, null);
        when(discountDB.validateAndGetDiscount("PROMO")).thenReturn(discount);
        when(discountDB.canUserUseDiscount(2, discount.getDiscountId())).thenReturn(false);

        PromotionApplicationResult result = service.applyPromotion(2, 1, "PROMO");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("không có quyền");
    }

    @Test
    @DisplayName("applyPromotion trả lỗi khi không đủ điều kiện min order")
    void shouldFailWhenSubtotalTooLow() {
        Discount discount = createDiscount("PROMO", "PERCENTAGE", 10, 500_000, null);
        when(discountDB.validateAndGetDiscount("PROMO")).thenReturn(discount);
        when(discountDB.canUserUseDiscount(2, discount.getDiscountId())).thenReturn(true);
        when(cartDB.calculateCartTotal(99)).thenReturn(100_000.0);

        PromotionApplicationResult result = service.applyPromotion(2, 99, "PROMO");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Đơn hàng chưa đạt tối thiểu");
    }

    @Test
    @DisplayName("applyPromotion thành công với voucher phần trăm")
    void shouldApplyPercentageDiscount() {
        Discount discount = createDiscount("PROMO", "PERCENTAGE", 10, 100_000, 30_000.0);
        when(discountDB.validateAndGetDiscount("PROMO")).thenReturn(discount);
        when(discountDB.canUserUseDiscount(2, discount.getDiscountId())).thenReturn(true);
        when(cartDB.calculateCartTotal(99)).thenReturn(400_000.0);

        PromotionApplicationResult result = service.applyPromotion(2, 99, "PROMO");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getDiscountCode()).isEqualTo("PROMO");
        assertThat(result.getDiscountAmount()).isEqualTo(30_000.0); // capped by max
    }

    @Test
    @DisplayName("applyPromotion thành công với voucher cố định")
    void shouldApplyFixedDiscount() {
        Discount discount = createDiscount("FIX", "FIXED_AMOUNT", 50_000, 0, null);
        when(discountDB.validateAndGetDiscount("FIX")).thenReturn(discount);
        when(discountDB.canUserUseDiscount(1, discount.getDiscountId())).thenReturn(true);
        when(cartDB.calculateCartTotal(1)).thenReturn(120_000.0);

        PromotionApplicationResult result = service.applyPromotion(1, 1, "FIX");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getDiscountAmount()).isEqualTo(50_000.0);
    }

    private Discount createDiscount(String code, String type, double value, double minOrder, Double maxAmount) {
        Discount discount = new Discount();
        discount.setDiscountId(10);
        discount.setCode(code);
        discount.setType(type);
        discount.setValue(value);
        discount.setMinOrderAmount(minOrder);
        discount.setMaxDiscountAmount(maxAmount);
        return discount;
    }
}

