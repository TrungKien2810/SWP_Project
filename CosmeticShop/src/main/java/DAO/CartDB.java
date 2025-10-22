/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Cart;
import Model.CartItems;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class CartDB {

    DBConnect db = new DBConnect();
    Connection conn = db.conn;

    public Cart getCartByUserId(int userId) {
        String sql = "select * from Carts where user_id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                java.time.LocalDateTime createdAt = null;
                java.time.LocalDateTime updatedAt = null;
                try {
                    java.sql.Timestamp cts = rs.getTimestamp("created_at");
                    if (cts != null) createdAt = cts.toLocalDateTime();
                } catch (Exception ignore) { createdAt = null; }
                try {
                    java.sql.Timestamp uts = rs.getTimestamp("updated_at");
                    if (uts != null) updatedAt = uts.toLocalDateTime();
                } catch (Exception ignore) { updatedAt = null; }
                Cart cart = new Cart(rs.getInt("cart_id"), rs.getInt("user_id"), createdAt, updatedAt);
                System.out.println("[CartDB] getCartByUserId found cart_id=" + cart.getCart_id() + " for user_id=" + userId);
                return cart;
            } else {
                System.out.println("[CartDB] getCartByUserId no cart for user_id=" + userId);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CartItems> getCartItemsByCartId(int cartId) {
        List<CartItems> CartItems = new ArrayList<>();
        String sql = "select * from CartItems where cart_id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CartItems.add(new CartItems(rs.getInt("cart_item_id"), rs.getInt("cart_id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getDouble("price")));
            }
            return CartItems;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public boolean updateQuantityAddToCart(int cart_id, int product_id, int quantity) {
        boolean CheckExist = false;
        String Check = "select * from CartItems where cart_id = ? and product_id = ?";
        String update = "update CartItems set quantity = ? where cart_id = ? and product_id = ?";
        try {
            PreparedStatement checkStmt = conn.prepareStatement(Check);
            checkStmt.setInt(1, cart_id);
            checkStmt.setInt(2, product_id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                CheckExist = true;
            }
            if (CheckExist == true) {
                PreparedStatement stmt = conn.prepareStatement(update);
                stmt.setInt(1, quantity);
                stmt.setInt(2, cart_id);
                stmt.setInt(3, product_id);
                return stmt.executeUpdate() > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean addNewCart(int user_id){
        String sql = "insert into Carts(user_id) values (?)";
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user_id);
            boolean ok = stmt.executeUpdate()>0;
            System.out.println("[CartDB] addNewCart user_id=" + user_id + ", created=" + ok);
            return ok;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public Cart getOrCreateCartByUserId(int userId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) return cart;
        boolean created = addNewCart(userId);
        if (!created) return null;
        Cart reloaded = getCartByUserId(userId);
        if (reloaded == null) {
            System.out.println("[CartDB] getOrCreateCartByUserId: created but cannot reload cart for user_id=" + userId);
        }
        return reloaded;
    }

    public boolean addCartItems(int cart_id, int product_id, int quantity, double price) {
        String sql = "insert into CartItems(cart_id, product_id, quantity, price) values (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cart_id);
            stmt.setInt(2, product_id);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            return stmt.executeUpdate()>0;
        }
        catch(SQLException e){
            return false;
        }
    }

    public double calculateCartTotal(int cart_id) {
        String sql = "select ISNULL(SUM(price * quantity), 0) as total from CartItems where cart_id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cart_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean removeFromCart(int cart_id, int product_id){
        String sql = "DELETE FROM CartItems WHERE cart_id = ? AND product_id = ?";
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cart_id);
            stmt.setInt(2, product_id);
            return stmt.executeUpdate()>0;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
