package Databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {
    public static Connection Connect() {
        String url = "jdbc:mysql://localhost:3306/2202_tc";
        String user = "root";
        String pass = "";
        
        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error Connecting to the Database: " + e.getMessage());
            return null;
        }
    }
}