import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521/FREEPDB1",
                "system",
                "Aswin2006#"
            );
            
            // IMPORTANT: Set auto-commit to true to ensure changes are saved
            con.setAutoCommit(true);
            
            System.out.println("Database connection established successfully");
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to connect to database");
            e.printStackTrace();
        }
        return con;
    }
}