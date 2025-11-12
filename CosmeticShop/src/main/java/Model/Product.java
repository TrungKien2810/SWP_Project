package Model;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productId;
    private String name;
    private double price;
    private int stock;
    private String description;
    private String imageUrl;
    private List<String> imageUrls; // Danh sách nhiều ảnh
    private int categoryId;
    private Discount activeDiscount;

    public Product(int productId, String name, double price, int stock, String description, String imageUrl, int categoryId) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageUrls = new ArrayList<>();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            this.imageUrls.add(imageUrl);
        }
        this.categoryId = categoryId;
    }
    
    // Constructor mới với danh sách ảnh
    public Product(int productId, String name, double price, int stock, String description, List<String> imageUrls, int categoryId) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.imageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : "";
        this.categoryId = categoryId;
    }

    // Getter & Setter

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public List<String> getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        if (imageUrls != null && !imageUrls.isEmpty()) {
            this.imageUrl = imageUrls.get(0);
        }
    }
    
    public void addImageUrl(String imageUrl) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(imageUrl);
        if (this.imageUrl == null || this.imageUrl.trim().isEmpty()) {
            this.imageUrl = imageUrl;
        }
    }

    public Discount getActiveDiscount() {
        return activeDiscount;
    }

    public void setActiveDiscount(Discount activeDiscount) {
        this.activeDiscount = activeDiscount;
    }

    public double getDiscountedPrice() {
        if (activeDiscount == null) {
            return price;
        }
        double discountedPrice = price;
        if ("PERCENTAGE".equalsIgnoreCase(activeDiscount.getType())) {
            discountedPrice = price * (1 - (activeDiscount.getValue() / 100.0));
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(activeDiscount.getType())) {
            discountedPrice = price - activeDiscount.getValue();
        }
        if (activeDiscount.getMaxDiscountAmount() != null && "PERCENTAGE".equalsIgnoreCase(activeDiscount.getType())) {
            double maxDiscount = activeDiscount.getMaxDiscountAmount();
            double actualDiscount = price - discountedPrice;
            if (actualDiscount > maxDiscount) {
                discountedPrice = price - maxDiscount;
            }
        }
        if (discountedPrice < 0) {
            discountedPrice = 0;
        }
        return discountedPrice;
    }

    public boolean isDiscountActive() {
        return activeDiscount != null && getDiscountedPrice() < price;
    }

    public double getDiscountAmount() {
        return Math.max(0, price - getDiscountedPrice());
    }
}
