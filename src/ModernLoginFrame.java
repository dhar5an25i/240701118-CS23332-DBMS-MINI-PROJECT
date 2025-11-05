import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ModernLoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private UserDAO userDAO = new UserDAO();
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color DARK_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_COLOR = new Color(236, 240, 241);

    public ModernLoginFrame() {
        setTitle("FindMe - Modern Login");
        setSize(450, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, h, SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);
        
        // Logo/Title area
        JLabel lblTitle = new JLabel("FindMe", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 40, 450, 60);
        mainPanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Lost & Found System", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.setBounds(0, 100, 450, 30);
        mainPanel.add(lblSubtitle);

        // White card panel for login form
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(null);
        cardPanel.setBounds(50, 170, 350, 340);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Add shadow effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            cardPanel.getBorder()
        ));

        // Login form title
        JLabel lblFormTitle = new JLabel("Sign In");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblFormTitle.setForeground(DARK_COLOR);
        lblFormTitle.setBounds(20, 20, 310, 40);
        cardPanel.add(lblFormTitle);

        // Username field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(DARK_COLOR);
        lblUser.setBounds(20, 80, 310, 25);
        cardPanel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUser.setBounds(20, 110, 310, 40);
        txtUser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(txtUser);

        // Password field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(DARK_COLOR);
        lblPass.setBounds(20, 160, 310, 25);
        cardPanel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setBounds(20, 190, 310, 40);
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(txtPass);

        // Login button
        JButton btnLogin = createStyledButton("LOGIN", PRIMARY_COLOR);
        btnLogin.setBounds(20, 250, 310, 45);
        cardPanel.add(btnLogin);

        // Register link
        JLabel lblRegister = new JLabel("<html><u>Don't have an account? Register here</u></html>");
        lblRegister.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRegister.setForeground(PRIMARY_COLOR);
        lblRegister.setBounds(20, 305, 310, 20);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.add(lblRegister);

        mainPanel.add(cardPanel);
        add(mainPanel);

        // Action listeners
        btnLogin.addActionListener(e -> handleLogin());
        txtPass.addActionListener(e -> handleLogin());
        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ModernRegisterFrame().setVisible(true);
                dispose();
            }
        });
    }

    private void handleLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStyledMessage("Please fill in all fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = userDAO.loginUser(username, password);
        if (userId != -1) {
            showStyledMessage("Welcome back, " + username + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            new ModernDashboardFrame(userId, username).setVisible(true);
            dispose();
        } else {
            showStyledMessage("Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPass.setText("");
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    private void showStyledMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.messageForeground", DARK_COLOR);
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ModernLoginFrame().setVisible(true);
        });
    }
}