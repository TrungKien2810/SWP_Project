package Model;

import java.sql.Timestamp;

public class Discount {

    private int discountId;
    private String code;
    private String name;
    private String type; // PERCENTAGE | FIXED_AMOUNT
    private double value;
    private double minOrderAmount;
    private Double maxDiscountAmount;
    private Boolean active;
    private Timestamp startDate;
    private Timestamp endDate;

    // Optional extended fields for assignment conditions and limits
    private String description;
    private Integer usageLimit;
    private Integer usedCount;
    private String conditionType; // TOTAL_SPENT | ORDER_COUNT | FIRST_ORDER | SPECIAL_EVENT | null
    private Double conditionValue;
    private String conditionDescription;
    private Boolean specialEvent;
    private Boolean autoAssignAll;
    private Timestamp assignDate;

    public Discount(int discountId, String code, String name, String type,
                    double value, double minOrderAmount, Double maxDiscountAmount,
                    Boolean active, Timestamp startDate, Timestamp endDate) {
        this.discountId = discountId;
        this.code = code;
        this.name = name;
        this.type = type;
        this.value = value;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.active = active;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Discount() {
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Double getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(Double maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    // Extended fields getters/setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }
    public Double getConditionValue() { return conditionValue; }
    public void setConditionValue(Double conditionValue) { this.conditionValue = conditionValue; }
    public String getConditionDescription() { return conditionDescription; }
    public void setConditionDescription(String conditionDescription) { this.conditionDescription = conditionDescription; }
    public Boolean getSpecialEvent() { return specialEvent; }
    public void setSpecialEvent(Boolean specialEvent) { this.specialEvent = specialEvent; }
    public Boolean getAutoAssignAll() { return autoAssignAll; }
    public void setAutoAssignAll(Boolean autoAssignAll) { this.autoAssignAll = autoAssignAll; }
    public Timestamp getAssignDate() { return assignDate; }
    public void setAssignDate(Timestamp assignDate) { this.assignDate = assignDate; }
}


