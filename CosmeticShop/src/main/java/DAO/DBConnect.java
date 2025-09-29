/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ADMIN
 */
public class DBConnect {
    public Connection conn;
    public DBConnect(){
        try {
           String connectionUrl =
    "jdbc:sqlserver://localhost:1433;"
  + "databaseName=PinkyCloudDB;"
  + "user=sa;"
  + "password=kingqn132;"
  + "encrypt=true;"
  + "trustServerCertificate=true;";




            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(connectionUrl);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Không kết nối được với sql server");
            e.printStackTrace();
        }
    }
}
