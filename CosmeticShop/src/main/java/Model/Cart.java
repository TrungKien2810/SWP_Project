/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */
public class Cart {
//    cart_id INT IDENTITY(1,1) PRIMARY KEY,
//    user_id INT UNIQUE FOREIGN KEY REFERENCES Users(user_id), -- m?i user 1 gi? hàng
//	totalPrice Decimal(10,2) NOT NULL,
//    created_at DATETIME DEFAULT GETDATE(),
//    updated_at DATETIME DEFAULT GETDATE()
    private int cart_id;
    private int user_id;
    private double totalPrice;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Cart() {
    }

    public Cart(int cart_id, int user_id, double totalPrice, LocalDateTime created_at, LocalDateTime updated_at) {
        this.cart_id = cart_id;
        this.user_id = user_id;
        this.totalPrice = totalPrice;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
    
    
}
