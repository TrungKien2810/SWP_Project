package service;

import DAO.CartDB;
import DAO.DiscountDB;
import Model.Discount;
import service.dto.PromotionApplicationResult;

/**
 * Triển khai mặc định cho CartPromotionService.
 */
public class DefaultCartPromotionService implements CartPromotionService {

    private final DiscountDB discountDB;
    private final CartDB cartDB;

    public DefaultCartPromotionService() {
        this(new DiscountDB(), new CartDB());
    }

    public DefaultCartPromotionService(DiscountDB discountDB, CartDB cartDB) {
        this.discountDB = discountDB;
        this.cartDB = cartDB;
    }

    @Override
    public PromotionApplicationResult applyPromotion(int userId, int cartId, String code) {
        if (code == null || code.trim().isEmpty()) {
            return PromotionApplicationResult.failure("Mã giảm giá không được để trống.");
        }

        Discount discount = discountDB.validateAndGetDiscount(code);
        if (discount == null) {
            return PromotionApplicationResult.failure("Mã giảm giá không hợp lệ hoặc đã hết hạn.");
        }

        if (!discountDB.canUserUseDiscount(userId, discount.getDiscountId())) {
            return PromotionApplicationResult.failure("Bạn không có quyền sử dụng mã giảm giá này.");
        }

        double subtotal = cartDB.calculateCartTotal(cartId);
        if (subtotal < discount.getMinOrderAmount()) {
            return PromotionApplicationResult.failure("Đơn hàng chưa đạt tối thiểu để áp dụng mã giảm giá.");
        }

        double discountAmount = calculateDiscountAmount(discount, subtotal);
        return PromotionApplicationResult.success(discount.getCode(), discountAmount,
                "Áp dụng mã thành công: " + discount.getCode());
    }

    private double calculateDiscountAmount(Discount discount, double subtotal) {
        double discountAmount;
        if ("PERCENTAGE".equalsIgnoreCase(discount.getType())) {
            discountAmount = subtotal * (discount.getValue() / 100.0);
            if (discount.getMaxDiscountAmount() != null) {
                discountAmount = Math.min(discountAmount, discount.getMaxDiscountAmount());
            }
        } else {
            discountAmount = discount.getValue();
        }

        if (discountAmount < 0) discountAmount = 0;
        if (discountAmount > subtotal) discountAmount = subtotal;
        return discountAmount;
    }
}

