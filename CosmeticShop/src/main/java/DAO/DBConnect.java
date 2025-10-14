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




    //         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    //         conn = DriverManager.getConnection(connectionUrl);
    //     } catch (ClassNotFoundException | SQLException e) {
    //         System.out.println("Không kết nối được với sql server");
    //         e.printStackTrace();
    //     }
    // }
    
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(connectionUrl);

            if (conn != null) {
                System.out.println("✅ Kết nối SQL Server thành công!");
            } else {
                System.out.println("⚠️ Kết nối SQL Server thất bại: conn = null");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("❌ Không tìm thấy driver JDBC. Hãy kiểm tra thư viện mssql-jdbc.jar.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi kết nối SQL Server. Kiểm tra thông tin đăng nhập hoặc cổng 1433.");
            e.printStackTrace();
        }
    }
}
