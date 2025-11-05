import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private int userId;
    private LostFoundDAO dao = new LostFoundDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public DashboardFrame(int userId) {
        this.userId = userId;
        setTitle("FindMe - Dashboard");
        setSize(1000, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top panel with buttons and search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton btnAdd = new JButton("Add Lost Item");
        JButton btnViewMy = new JButton("My Lost Items");
        JButton btnViewAll = new JButton("All Lost Items");
        JButton btnLogout = new JButton("Logout");
        
        // Search components
        searchField = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        JButton btnClearSearch = new JButton("Clear");
        
        topPanel.add(btnAdd);
        topPanel.add(btnViewMy);
        topPanel.add(btnViewAll);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(btnSearch);
        topPanel.add(btnClearSearch);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(btnLogout);
        
        add(topPanel, BorderLayout.NORTH);

        // Table to display items
        model = new DefaultTableModel(new String[]{"ID", "Posted By", "Item Name", "Location", "Date", "Description", "Contact"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Status bar at bottom
        JLabel statusLabel = new JLabel(" Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);

        // Button actions
        btnAdd.addActionListener(e -> addLostItem());
        btnViewMy.addActionListener(e -> loadMyItems());
        btnViewAll.addActionListener(e -> loadAllItems());
        btnSearch.addActionListener(e -> searchItems(searchField.getText()));
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            loadAllItems();
        });
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        // Allow Enter key to search
        searchField.addActionListener(e -> searchItems(searchField.getText()));

        // Load all items by default when dashboard opens
        loadAllItems();
    }

    private void addLostItem() {
        JTextField name = new JTextField();
        JTextField loc = new JTextField();
        JTextField date = new JTextField();
        JTextField desc = new JTextField();
        JTextField contact = new JTextField();

        Object[] fields = {
            "Item Name:", name,
            "Location:", loc,
            "Date (YYYY-MM-DD):", date,
            "Description:", desc,
            "Contact Info:", contact
        };

        int opt = JOptionPane.showConfirmDialog(this, fields, "Add Lost Item", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            // Validate inputs
            if (name.getText().trim().isEmpty() || loc.getText().trim().isEmpty() || 
                date.getText().trim().isEmpty() || contact.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate date format
            try {
                java.sql.Date.valueOf(date.getText());
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                dao.addLostItem(userId, new LostItem(name.getText(), loc.getText(), date.getText(), desc.getText(), contact.getText()));
                JOptionPane.showMessageDialog(this, "Item added successfully!");
                loadAllItems(); // Refresh the view
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Load only current user's items
    private void loadMyItems() {
        model.setRowCount(0);
        setTitle("FindMe - My Lost Items");
        try {
            ResultSet rs = dao.getLostItems(userId);
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Error loading items", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int count = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("lost_id"),
                    "Me",
                    rs.getString("item_name"),
                    rs.getString("location"),
                    rs.getDate("lost_date"),
                    rs.getString("description"),
                    rs.getString("contact_info")
                });
                count++;
            }
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "You haven't posted any lost items yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load all lost items from everyone
    private void loadAllItems() {
        model.setRowCount(0);
        setTitle("FindMe - All Lost Items");
        try {
            ResultSet rs = dao.getAllLostItems();
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Error loading items", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int count = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("lost_id"),
                    rs.getString("username"),
                    rs.getString("item_name"),
                    rs.getString("location"),
                    rs.getDate("lost_date"),
                    rs.getString("description"),
                    rs.getString("contact_info")
                });
                count++;
            }
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "No lost items have been posted yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // NEW METHOD: Search items by keyword
    private void searchItems(String keyword) {
        if (keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term!", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        model.setRowCount(0);
        setTitle("FindMe - Search Results for: " + keyword);
        try {
            ResultSet rs = dao.searchLostItems(keyword);
            if (rs == null) {
                JOptionPane.showMessageDialog(this, "Error searching items", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int count = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("lost_id"),
                    rs.getString("username"),
                    rs.getString("item_name"),
                    rs.getString("location"),
                    rs.getDate("lost_date"),
                    rs.getString("description"),
                    rs.getString("contact_info")
                });
                count++;
            }
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No items found matching: '" + keyword + "'\n\nTry searching for:\n- Item names (e.g., wallet, phone)\n- Locations (e.g., library, cafeteria)\n- Keywords in description", 
                    "No Results", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Found " + count + " item(s) matching: '" + keyword + "'", 
                    "Search Results", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}