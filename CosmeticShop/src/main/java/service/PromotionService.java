package service;

import Model.Discount;
import Model.UserDiscountAssign;

import java.sql.Timestamp;
import java.util.List;

/**
 * PromotionService defines operations for managing discounts/vouchers and assigning them to users.
 * Creating this abstraction allows controllers to be tested without hitting the real database.
 */
public interface PromotionService {

    void assignDueForUser(int userId);

    List<UserDiscountAssign> listAssignedDiscountsForUser(int userId);

    List<Discount> listAllDiscounts();

    Discount getById(int id);

    boolean existsByCode(String code, Integer excludeId);

    boolean createDiscount(String code,
                           String name,
                           String type,
                           double value,
                           Double minOrderAmount,
                           Double maxDiscountAmount,
                           Timestamp start,
                           Timestamp end,
                           boolean isActive,
                           String description,
                           Integer usageLimit,
                           String conditionType,
                           Double conditionValue,
                           String conditionDescription,
                           Boolean specialEvent,
                           Boolean autoAssignAll,
                           Timestamp assignDate);

    boolean updateDiscount(int id,
                           String code,
                           String name,
                           String type,
                           double value,
                           Double minOrderAmount,
                           Double maxDiscountAmount,
                           Timestamp start,
                           Timestamp end,
                           boolean isActive,
                           String description,
                           Integer usageLimit,
                           String conditionType,
                           Double conditionValue,
                           String conditionDescription,
                           Boolean specialEvent,
                           Boolean autoAssignAll,
                           Timestamp assignDate);

    boolean deleteDiscount(int id);
}

