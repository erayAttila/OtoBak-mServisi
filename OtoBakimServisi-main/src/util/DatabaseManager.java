package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    
    private static final String URL =
            "jdbc:mysql://localhost:3306/oto_bakim_servisi?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    
    private static Connection connection;

    public static Connection getConnection() {
        try {
            
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database bağlantısı aktif ✅");
            }
        } catch (SQLException e) {
            System.out.println("Database bağlantı hatası ❌");
            e.printStackTrace();
        }
        
        return connection;
    }
}