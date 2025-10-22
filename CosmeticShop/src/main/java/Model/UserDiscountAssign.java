package Model;

import java.sql.Timestamp;

public class UserDiscountAssign {
    private int discountId;
    private String code;
    private String name;
    private String type; // PERCENTAGE | FIXED_AMOUNT
    private double value;
    private double minOrderAmount;
    private Double maxDiscountAmount;
    private Timestamp startDate;
    private Timestamp endDate;
    private int remainingUses;

    public UserDiscountAssign(int discountId, String code, String name, String type,
                              double value, double minOrderAmount, Double maxDiscountAmount,
                              Timestamp startDate, Timestamp endDate, int remainingUses) {
        this.discountId = discountId;
        this.code = code;
        this.name = name;
        this.type = type;
        this.value = value;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingUses = remainingUses;
    }

    public int getDiscountId() { return discountId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getValue() { return value; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public Double getMaxDiscountAmount() { return maxDiscountAmount; }
    public Timestamp getStartDate() { return startDate; }
    public Timestamp getEndDate() { return endDate; }
    public int getRemainingUses() { return remainingUses; }
}


