import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ModernDashboardFrame extends JFrame {
    private int userId;
    private String username;
    private LostFoundDAO dao = new LostFoundDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JLabel statusLabel;
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color DARK_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_BG = new Color(236, 240, 241);
    private static final Color CARD_BG = Color.WHITE;

    public ModernDashboardFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;
        
        setTitle("FindMe - Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(LIGHT_BG);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel(" Ready | Logged in as: " + username);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DARK_COLOR);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        statusLabel.setBackground(CARD_BG);
        statusLabel.setOpaque(true);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
        loadAllItems();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(DARK_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(DARK_COLOR);
        
        JLabel titleLabel = new JLabel("FindMe");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Lost & Found Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        leftPanel.add(subtitleLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(DARK_COLOR);
        
        JLabel userLabel = new JLabel("👤 " + username);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        rightPanel.add(userLabel);
        
        JButton btnLogout = createModernButton("Logout", DANGER_COLOR, 100, 35);
        btnLogout.addActionListener(e -> handleLogout());
        rightPanel.add(btnLogout);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(LIGHT_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(CARD_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JButton btnAdd = createModernButton("➕ Add Item", SUCCESS_COLOR, 140, 40);
        JButton btnViewAll = createModernButton("🌐 All Items", PRIMARY_COLOR, 140, 40);
        JButton btnViewMy = createModernButton("📋 My Items", WARNING_COLOR, 140, 40);
        
        controlPanel.add(btnAdd);
        controlPanel.add(btnViewAll);
        controlPanel.add(btnViewMy);
        
        // Search panel
        controlPanel.add(Box.createHorizontalStrut(20));
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlPanel.add(searchLabel);
        
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        controlPanel.add(searchField);
        
        JButton btnSearch = createModernButton("Search", PRIMARY_COLOR, 90, 40);
        JButton btnClear = createModernButton("Clear", new Color(149, 165, 166), 80, 40);
        
        controlPanel.add(btnSearch);
        controlPanel.add(btnClear);
        
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Table
        model = new DefaultTableModel(
            new String[]{"ID", "Posted By", "Item Name", "Location", "Date", "Description", "Contact", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(52, 152, 219, 100));
        table.setSelectionForeground(DARK_COLOR);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setOpaque(true);
        
        // Custom header renderer to ensure white text
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(52, 73, 94));
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Custom renderer for Status column with color coding
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                if (value != null) {
                    String status = value.toString();
                    if ("FOUND".equals(status)) {
                        setForeground(SUCCESS_COLOR);
                        setText("✓ FOUND");
                    } else {
                        setForeground(DANGER_COLOR);
                        setText("⚠ LOST");
                    }
                }
                
                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(7).setCellRenderer(statusRenderer);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        table.getColumnModel().getColumn(6).setPreferredWidth(130);
        table.getColumnModel().getColumn(7).setPreferredWidth(90);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom action panel for selected items
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(CARD_BG);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel actionLabel = new JLabel("Actions for selected item:");
        actionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionLabel.setForeground(DARK_COLOR);
        actionPanel.add(actionLabel);
        
        JButton btnMarkFound = createModernButton("✓ Mark as Found", SUCCESS_COLOR, 140, 35);
        JButton btnDelete = createModernButton("✗ Delete Item", DANGER_COLOR, 130, 35);
        
        actionPanel.add(btnMarkFound);
        actionPanel.add(btnDelete);
        
        centerPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Button actions
        btnAdd.addActionListener(e -> addLostItem());
        btnViewAll.addActionListener(e -> loadAllItems());
        btnViewMy.addActionListener(e -> loadMyItems());
        btnSearch.addActionListener(e -> searchItems());
        btnClear.addActionListener(e -> {
            searchField.setText("");
            loadAllItems();
        });
        searchField.addActionListener(e -> searchItems());
        
        btnMarkFound.addActionListener(e -> markSelectedItemAsFound());
        btnDelete.addActionListener(e -> deleteSelectedItem());
        
        return centerPanel;
    }

    private JButton createModernButton(String text, Color bgColor, int width, int height) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color = bgColor;
                if (getModel().isPressed()) {
                    color = bgColor.darker();
                } else if (getModel().isRollover()) {
                    color = bgColor.brighter();
                }
                
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        
        btn.setPreferredSize(new Dimension(width, height));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            new ModernLoginFrame().setVisible(true);
            dispose();
        }
    }

    private void addLostItem() {
        JDialog dialog = new JDialog(this, "Add Lost Item", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Report Lost Item");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_COLOR);
        titleLabel.setBounds(20, 15, 400, 35);
        mainPanel.add(titleLabel);
        
        int yPos = 65;
        int spacing = 75;
        
        // Item Name
        addFormField(mainPanel, "Item Name *", 20, yPos);
        JTextField txtName = new JTextField();
        styleTextField(txtName, 20, yPos + 25);
        mainPanel.add(txtName);
        
        // Location
        yPos += spacing;
        addFormField(mainPanel, "Location *", 20, yPos);
        JTextField txtLocation = new JTextField();
        styleTextField(txtLocation, 20, yPos + 25);
        mainPanel.add(txtLocation);
        
        // Date
        yPos += spacing;
        addFormField(mainPanel, "Date Lost (YYYY-MM-DD) *", 20, yPos);
        JTextField txtDate = new JTextField();
        styleTextField(txtDate, 20, yPos + 25);
        mainPanel.add(txtDate);
        
        // Description
        yPos += spacing;
        addFormField(mainPanel, "Description", 20, yPos);
        JTextField txtDescription = new JTextField();
        styleTextField(txtDescription, 20, yPos + 25);
        mainPanel.add(txtDescription);
        
        // Contact
        yPos += spacing;
        addFormField(mainPanel, "Contact Info *", 20, yPos);
        JTextField txtContact = new JTextField();
        styleTextField(txtContact, 20, yPos + 25);
        mainPanel.add(txtContact);
        
        // Buttons
        yPos += 75;
        JButton btnSubmit = createModernButton("Submit", SUCCESS_COLOR, 180, 45);
        btnSubmit.setBounds(20, yPos, 180, 45);
        mainPanel.add(btnSubmit);
        
        JButton btnCancel = createModernButton("Cancel", new Color(149, 165, 166), 180, 45);
        btnCancel.setBounds(230, yPos, 180, 45);
        mainPanel.add(btnCancel);
        
        btnSubmit.addActionListener(e -> {
            String name = txtName.getText().trim();
            String location = txtLocation.getText().trim();
            String date = txtDate.getText().trim();
            String description = txtDescription.getText().trim();
            String contact = txtContact.getText().trim();
            
            if (name.isEmpty() || location.isEmpty() || date.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                java.sql.Date.valueOf(date);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format! Use YYYY-MM-DD", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                dao.addLostItem(userId, new LostItem(name, location, date, description, contact));
                JOptionPane.showMessageDialog(dialog, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAllItems();
                updateStatus("Item added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, int x, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(DARK_COLOR);
        lbl.setBounds(x, y, 400, 20);
        panel.add(lbl);
    }

    private void styleTextField(JTextField field, int x, int y) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBounds(x, y, 430, 40);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void loadMyItems() {
        model.setRowCount(0);
        setTitle("FindMe - My Lost Items");
        updateStatus("Loading your items...");
        
        System.out.println("=== Loading My Items ===");
        
        try {
            ResultSet rs = dao.getLostItems(userId);
            if (rs == null) {
                System.out.println("ERROR: ResultSet is null!");
                JOptionPane.showMessageDialog(this, "Error loading items", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int count = 0;
            while (rs.next()) {
                String status = rs.getString("status");
                System.out.println("Row " + count + " - Status from DB: '" + status + "'");
                
                model.addRow(new Object[]{
                    rs.getInt("lost_id"),
                    "Me",
                    rs.getString("item_name"),
                    rs.getString("location"),
                    rs.getDate("lost_date"),
                    rs.getString("description"),
                    rs.getString("contact_info"),
                    status != null ? status : "LOST"  // Handle null status
                });
                count++;
            }
            
            System.out.println("Loaded " + count + " items");
            updateStatus("Showing " + count + " item(s) posted by you");
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "You haven't posted any lost items yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("ERROR loading my items: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllItems() {
        model.setRowCount(0);
        setTitle("FindMe - All Lost Items");
        updateStatus("Loading all items...");
        
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
                    rs.getString("contact_info"),
                    rs.getString("status")
                });
                count++;
            }
            
            updateStatus("Showing " + count + " total item(s) | Logged in as: " + username);
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "No lost items have been posted yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchItems() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term!", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        model.setRowCount(0);
        setTitle("FindMe - Search Results");
        updateStatus("Searching for: " + keyword);
        
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
                    rs.getString("contact_info"),
                    rs.getString("status")
                });
                count++;
            }
            
            updateStatus("Found " + count + " item(s) matching: '" + keyword + "' | Logged in as: " + username);
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this,
                    "No items found matching: '" + keyword + "'\n\nTry searching for:\n• Item names (wallet, phone)\n• Locations (library, cafeteria)\n• Keywords in description",
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markSelectedItemAsFound() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table first!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int itemId = (int) model.getValueAt(selectedRow, 0);
        String postedBy = (String) model.getValueAt(selectedRow, 1);
        String itemName = (String) model.getValueAt(selectedRow, 2);
        String currentStatus = (String) model.getValueAt(selectedRow, 7);
        
        // Check if item already marked as found
        if ("FOUND".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This item is already marked as FOUND!", "Already Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Check if item belongs to current user
        if (!"Me".equals(postedBy) && !username.equals(postedBy)) {
            JOptionPane.showMessageDialog(this, "You can only mark your own items as found!", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Mark '" + itemName + "' as FOUND?\n\nThis will indicate you've recovered this item.",
            "Confirm Mark as Found",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.markItemAsFound(itemId, userId)) {
                JOptionPane.showMessageDialog(this, "Item marked as FOUND successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateStatus("Item #" + itemId + " marked as found");
                // Refresh the current view
                refreshCurrentView();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark item as found. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table first!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int itemId = (int) model.getValueAt(selectedRow, 0);
        String postedBy = (String) model.getValueAt(selectedRow, 1);
        String itemName = (String) model.getValueAt(selectedRow, 2);
        
        // Check if item belongs to current user
        if (!"Me".equals(postedBy) && !username.equals(postedBy)) {
            JOptionPane.showMessageDialog(this, "You can only delete your own items!", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete '" + itemName + "'?\n\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteItem(itemId, userId)) {
                JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateStatus("Item #" + itemId + " deleted");
                // Refresh the current view
                refreshCurrentView();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCurrentView() {
        String currentTitle = getTitle();
        if (currentTitle.contains("My Lost Items")) {
            loadMyItems();
        } else if (currentTitle.contains("Search Results")) {
            searchItems();
        } else {
            loadAllItems();
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(" " + message);
    }
}