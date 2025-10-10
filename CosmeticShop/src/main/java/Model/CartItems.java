/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author ADMIN
 */
public class CartItems {
//    cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
//    cart_id INT FOREIGN KEY REFERENCES Carts(cart_id),
//    product_id INT FOREIGN KEY REFERENCES Products(product_id),
//    quantity INT NOT NULL CHECK (quantity > 0),
//    price DECIMAL(10,2) NOT NULL
    private int id;
    private int cart_id;
    private int product_id;
    private int quantity;
    private double price;

    public CartItems() {
    }
    
    public CartItems(int id, int cart_id, int product_id, int quantity, double price) {
        this.id = id;
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
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

    
    
}
