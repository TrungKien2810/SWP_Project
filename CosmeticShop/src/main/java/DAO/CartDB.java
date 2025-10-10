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
import java.time.LocalDate;
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
                return new Cart(rs.getInt("cart_id"), rs.getInt("user_id"), rs.getDouble("totalPrice"), rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime());
            } else {
                return null;
            }
        } catch (SQLException e) {
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
    
    public boolean addNewCart(int user_id, double totalPrice){
        String sql = "insert into Carts(user_id, totalPrice) values (?, ?)";
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user_id);
            stmt.setDouble(2, totalPrice);
            return stmt.executeUpdate()>0;
        }
        catch(SQLException e){
            return false;
        }
    }
    
    public boolean updateTotalPrice (int cart_id, double totalPrice){
        String sql = "Update Carts set totalPrice = ? where cart_id = ?";
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, totalPrice);
            stmt.setInt(2, cart_id);
            return stmt.executeUpdate()>0;
        }
        catch(SQLException e){
            return false;
        }
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
