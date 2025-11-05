import java.sql.*;

public class LostFoundDAO {
    public void addLostItem(int userId, LostItem item) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO LostItems (user_id, item_name, location, lost_date, description, contact_info, status) VALUES (?, ?, ?, ?, ?, ?, 'LOST')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getLocation());
            ps.setDate(4, java.sql.Date.valueOf(item.getLostDate()));
            ps.setString(5, item.getDescription());
            ps.setString(6, item.getContactInfo());
            ps.executeUpdate();
            System.out.println("Item added with status: LOST");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // FIXED: Explicitly select status column
    public ResultSet getLostItems(int userId) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT lost_id, user_id, item_name, location, lost_date, description, contact_info, status FROM LostItems WHERE user_id=? ORDER BY lost_date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("ERROR in getLostItems: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // FIXED: Explicitly select status column
    public ResultSet getAllLostItems() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT l.lost_id, l.user_id, l.item_name, l.location, l.lost_date, l.description, l.contact_info, l.status, u.username " +
                        "FROM LostItems l " +
                        "JOIN UsersData u ON l.user_id = u.user_id " +
                        "ORDER BY l.lost_date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("ERROR in getAllLostItems: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // FIXED: Explicitly select status column
    public ResultSet searchLostItems(String keyword) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT l.lost_id, l.user_id, l.item_name, l.location, l.lost_date, l.description, l.contact_info, l.status, u.username " +
                        "FROM LostItems l " +
                        "JOIN UsersData u ON l.user_id = u.user_id " +
                        "WHERE LOWER(l.item_name) LIKE ? " +
                        "OR LOWER(l.location) LIKE ? " +
                        "OR LOWER(l.description) LIKE ? " +
                        "ORDER BY l.lost_date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("ERROR in searchLostItems: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean markItemAsFound(int itemId, int userId) {
        System.out.println("=== Marking Item as Found ===");
        System.out.println("Item ID: " + itemId + ", User ID: " + userId);
        
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("ERROR: Connection is null!");
                return false;
            }
            
            // First verify the item belongs to this user
            String checkSql = "SELECT user_id, status FROM LostItems WHERE lost_id=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setInt(1, itemId);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                int itemUserId = rs.getInt("user_id");
                String currentStatus = rs.getString("status");
                System.out.println("Found item - Current status: " + currentStatus + ", Owner: " + itemUserId);
                
                if (itemUserId == userId) {
                    // Update the status to FOUND
                    String updateSql = "UPDATE LostItems SET status='FOUND' WHERE lost_id=?";
                    PreparedStatement updatePs = con.prepareStatement(updateSql);
                    updatePs.setInt(1, itemId);
                    int rows = updatePs.executeUpdate();
                    
                    System.out.println("Update executed - Rows affected: " + rows);
                    
                    if (rows > 0) {
                        // Verify the update
                        checkPs = con.prepareStatement("SELECT status FROM LostItems WHERE lost_id=?");
                        checkPs.setInt(1, itemId);
                        rs = checkPs.executeQuery();
                        if (rs.next()) {
                            System.out.println("New status verified: " + rs.getString("status"));
                        }
                        return true;
                    }
                } else {
                    System.out.println("ERROR: User mismatch - Expected: " + userId + ", Found: " + itemUserId);
                }
            } else {
                System.out.println("ERROR: Item ID " + itemId + " not found in database");
            }
            
            return false;
        } catch (SQLException e) {
            System.out.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteItem(int itemId, int userId) {
        System.out.println("=== Deleting Item ===");
        System.out.println("Item ID: " + itemId + ", User ID: " + userId);
        
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("ERROR: Connection is null!");
                return false;
            }
            
            String sql = "DELETE FROM LostItems WHERE lost_id=? AND user_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, itemId);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            
            System.out.println("Delete executed - Rows affected: " + rows);
            
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}