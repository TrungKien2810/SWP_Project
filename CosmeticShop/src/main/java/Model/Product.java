package Model;

public class Product {
    private int productId;
    private String name;
    private double price;
    private int stock;
    private String description;
    private String imageUrl;
    private int categoryId;

    public Product(int productId, String name, double price, int stock, String description, String imageUrl, int categoryId) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    // Getter & Setter
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getCategoryId() { return categoryId; }
}
