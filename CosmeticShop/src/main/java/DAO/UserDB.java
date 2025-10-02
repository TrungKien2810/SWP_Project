/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.user;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ADMIN
 */
public class UserDB {

    DBConnect db = new DBConnect();
    Connection conn = db.conn;

    public user getUserByEmail(String email) {
        String sql = "select * from Users where email = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                return new user(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString("role"), rs.getTimestamp("created_at").toLocalDateTime());

            }
        } catch (SQLException e) {

            e.printStackTrace();
            return null;

        }
        return null;
    }

    public boolean signup(String username, String email, String password) {
        String sql = "insert into Users(full_name, email, password, role) values (?, ?, ?, ?)";
        try {
            user check = getUserByEmail(email);
            if (check != null) {
                return false;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            if (stmt.executeUpdate() > 0) {

            stmt.setString(4, "USER"); // Mặc định role là "USER"
            if(stmt.executeUpdate()>0){

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean login(String email, String password) {
        user check = getUserByEmail(email);
        if (check == null) {
            return false;
        } else {
            String sql = "select * from Users where email = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    if (rs.getString("password").equals(password)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
