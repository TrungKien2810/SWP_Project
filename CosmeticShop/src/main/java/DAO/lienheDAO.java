package DAO;

import Model.lienhe;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class lienheDAO {
    private DBConnect db;

    public lienheDAO() {
        db = new DBConnect();
    }

    public boolean insertContact(lienhe contact) {
        String sql = "INSERT INTO Contacts (name, phone, address, email, subject, message) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ps.setString(1, contact.getName());
            ps.setString(2, contact.getPhone());
            ps.setString(3, contact.getAddress());
            ps.setString(4, contact.getEmail());
            ps.setString(5, contact.getSubject());
            ps.setString(6, contact.getMessage());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
