import java.sql.*;

public class UserDAO {
    
    // Add this test method
   public void testConnection() {
    System.out.println("=== Database Connection Test ===");
    try (Connection con = DBConnection.getConnection()) {
        if (con == null) {
            System.out.println("❌ ERROR: Connection is null!");
            return;
        }
        
        System.out.println("✓ Connection successful!");
        System.out.println("  Database: " + con.getCatalog());
        System.out.println("  User: " + con.getMetaData().getUserName());
        
        // Check for UsersData table
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getTables(null, "SYSTEM", "USERSDATA", null);
        if (rs.next()) {
            System.out.println("✓ UsersData table found!");
        } else {
            System.out.println("❌ UsersData table NOT found!");
            System.out.println("  Available tables:");
            rs = meta.getTables(null, "SYSTEM", "%", new String[]{"TABLE"});
            while (rs.next()) {
                System.out.println("    - " + rs.getString("TABLE_NAME"));
            }
        }
        
        // Check for LostItems table
        rs = meta.getTables(null, "SYSTEM", "LOSTITEMS", null);
        if (rs.next()) {
            System.out.println("✓ LostItems table found!");
        } else {
            System.out.println("❌ LostItems table NOT found!");
        }
        
        // Check for FoundItems table
        rs = meta.getTables(null, "SYSTEM", "FOUNDITEMS", null);
        if (rs.next()) {
            System.out.println("✓ FoundItems table found!");
        } else {
            System.out.println("❌ FoundItems table NOT found!");
        }
        
    } catch (Exception e) {
        System.out.println("❌ ERROR during connection test:");
        e.printStackTrace();
    }
    System.out.println("================================\n");
}
    
    public boolean registerUser(User user) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO UsersData (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int loginUser(String username, String password) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT user_id FROM UsersData WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}