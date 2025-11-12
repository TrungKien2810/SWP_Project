package Model;

import java.time.LocalDateTime;

public class Wishlist {
    private int wishlistId;
    private int userId;
    private int productId;
    private LocalDateTime createdAt;
    private Product product; // Thông tin sản phẩm (optional, để load khi cần)

    public Wishlist() {
    }

    public Wishlist(int wishlistId, int userId, int productId, LocalDateTime createdAt) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    public Wishlist(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
        this.createdAt = LocalDateTime.now();
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

