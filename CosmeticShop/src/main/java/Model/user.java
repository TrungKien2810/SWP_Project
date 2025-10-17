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
public class user {

    private int user_id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String role;
    private LocalDateTime date_create;
    public user() {
    }


    public user(int user_id, String username, String email, String phone, String password, String role, LocalDateTime date_create) {

        this.user_id = user_id;
        this.username = username;
        this.email = email;
        if (phone == null) {
            this.phone = "";
        } else {
            this.phone = phone;
        }
        this.password = password;
        this.role = role != null ? role : "USER"; // Mặc định là "USER" nếu null
        this.date_create = date_create;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDate_create() {
        return date_create;
    }

    public void setDate_create(LocalDateTime date_create) {
        this.date_create = date_create;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
