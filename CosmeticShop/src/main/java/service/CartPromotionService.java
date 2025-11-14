package service;

import service.dto.PromotionApplicationResult;

/**
 * Service xử lý việc áp dụng mã giảm giá cho giỏ hàng hiện tại.
 */
public interface CartPromotionService {

    /**
     * Thử áp dụng mã giảm giá cho user và cart tương ứng.
     *
     * @param userId ID người dùng
     * @param cartId ID giỏ hàng
     * @param code   mã giảm giá (không null)
     * @return kết quả áp dụng (thành công hoặc thông báo lỗi)
     */
    PromotionApplicationResult applyPromotion(int userId, int cartId, String code);
}

