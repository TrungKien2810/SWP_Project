/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */
public class CartItems {
//    cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
//    cart_id INT FOREIGN KEY REFERENCES Carts(cart_id),
//    product_id INT FOREIGN KEY REFERENCES Products(product_id),
//    quantity INT NOT NULL CHECK (quantity > 0),
//    price DECIMAL(10,2) NOT NULL,
//    is_selected BIT DEFAULT 1,
//    added_at DATETIME DEFAULT GETDATE()
    private int id;
    private int cart_id;
    private int product_id;
    private int quantity;
    private double price;
    private boolean is_selected;
    private LocalDateTime added_at;

    public CartItems() {
    }
    
    public CartItems(int id, int cart_id, int product_id, int quantity, double price) {
        this.id = id;
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
        this.is_selected = true; // Default value
        this.added_at = LocalDateTime.now();
    }
    
    public CartItems(int id, int cart_id, int product_id, int quantity, double price, boolean is_selected, LocalDateTime added_at) {
        this.id = id;
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
        this.is_selected = is_selected;
        this.added_at = added_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }

    public LocalDateTime getAdded_at() {
        return added_at;
    }

    public void setAdded_at(LocalDateTime added_at) {
        this.added_at = added_at;
    }
    
}
