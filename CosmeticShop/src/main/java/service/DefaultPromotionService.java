package service;

import DAO.DiscountDB;
import Model.Discount;
import Model.UserDiscountAssign;

import java.sql.Timestamp;
import java.util.List;

/**
 * Default implementation of {@link PromotionService} delegating to {@link DiscountDB}.
 */
public class DefaultPromotionService implements PromotionService {

    private final DiscountDB discountDB;

    public DefaultPromotionService() {
        this(new DiscountDB());
    }

    public DefaultPromotionService(DiscountDB discountDB) {
        this.discountDB = discountDB;
    }

    @Override
    public void assignDueForUser(int userId) {
        discountDB.assignDueForUser(userId);
    }

    @Override
    public List<UserDiscountAssign> listAssignedDiscountsForUser(int userId) {
        return discountDB.listAssignedDiscountsForUser(userId);
    }

    @Override
    public List<Discount> listAllDiscounts() {
        return discountDB.listAll();
    }

    @Override
    public Discount getById(int id) {
        return discountDB.getById(id);
    }

    @Override
    public boolean existsByCode(String code, Integer excludeId) {
        return discountDB.existsByCode(code, excludeId);
    }

    @Override
    public boolean createDiscount(String code, String name, String type, double value, Double minOrderAmount,
                                  Double maxDiscountAmount, Timestamp start, Timestamp end, boolean isActive,
                                  String description, Integer usageLimit, String conditionType, Double conditionValue,
                                  String conditionDescription, Boolean specialEvent, Boolean autoAssignAll,
                                  Timestamp assignDate) {
        return discountDB.create(code, name, type, value, minOrderAmount, maxDiscountAmount, start, end, isActive,
                description, usageLimit, conditionType, conditionValue, conditionDescription, specialEvent,
                autoAssignAll, assignDate);
    }

    @Override
    public boolean updateDiscount(int id, String code, String name, String type, double value, Double minOrderAmount,
                                  Double maxDiscountAmount, Timestamp start, Timestamp end, boolean isActive,
                                  String description, Integer usageLimit, String conditionType, Double conditionValue,
                                  String conditionDescription, Boolean specialEvent, Boolean autoAssignAll,
                                  Timestamp assignDate) {
        return discountDB.update(id, code, name, type, value, minOrderAmount, maxDiscountAmount, start, end, isActive,
                description, usageLimit, conditionType, conditionValue, conditionDescription, specialEvent,
                autoAssignAll, assignDate);
    }

    @Override
    public boolean deleteDiscount(int id) {
        return discountDB.delete(id);
    }
}

