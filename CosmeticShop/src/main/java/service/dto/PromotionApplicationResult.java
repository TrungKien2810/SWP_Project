package service.dto;

/**
 * Kết quả từ việc áp dụng mã giảm giá.
 */
public class PromotionApplicationResult {

    private final boolean success;
    private final String message;
    private final String discountCode;
    private final Double discountAmount;

    private PromotionApplicationResult(boolean success, String message, String discountCode, Double discountAmount) {
        this.success = success;
        this.message = message;
        this.discountCode = discountCode;
        this.discountAmount = discountAmount;
    }

    public static PromotionApplicationResult success(String code, double amount, String message) {
        return new PromotionApplicationResult(true, message, code, amount);
    }

    public static PromotionApplicationResult failure(String message) {
        return new PromotionApplicationResult(false, message, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }
}

