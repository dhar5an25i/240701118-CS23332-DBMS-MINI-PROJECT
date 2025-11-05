import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ModernRegisterFrame extends JFrame {
    private JTextField txtUser, txtEmail;
    private JPasswordField txtPass, txtConfirmPass;
    private UserDAO userDAO = new UserDAO();
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DARK_COLOR = new Color(44, 62, 80);

    public ModernRegisterFrame() {
        setTitle("FindMe - Register");
        setSize(450, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, SUCCESS_COLOR, 0, h, new Color(39, 174, 96));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);
        
        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 30, 450, 60);
        mainPanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Join FindMe Today", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.setBounds(0, 90, 450, 30);
        mainPanel.add(lblSubtitle);

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(null);
        cardPanel.setBounds(50, 150, 350, 460);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblFormTitle = new JLabel("Register");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblFormTitle.setForeground(DARK_COLOR);
        lblFormTitle.setBounds(20, 15, 310, 35);
        cardPanel.add(lblFormTitle);

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(DARK_COLOR);
        lblUser.setBounds(20, 60, 310, 20);
        cardPanel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUser.setBounds(20, 85, 310, 38);
        txtUser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(txtUser);

        // Email
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEmail.setForeground(DARK_COLOR);
        lblEmail.setBounds(20, 133, 310, 20);
        cardPanel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setBounds(20, 158, 310, 38);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(txtEmail);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPass.setForeground(DARK_COLOR);
        lblPass.setBounds(20, 206, 310, 20);
        cardPanel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setBounds(20, 231, 310, 38);
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(txtPass);

        // Confirm Password
        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblConfirm.setForeground(DARK_COLOR);
        lblConfirm.setBounds(20, 279, 310, 20);
        cardPanel.add(lblConfirm);

        txtConfirmPass = new JPasswordField();
        txtConfirmPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmPass.setBounds(20, 304, 310, 38);
        txtConfirmPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(txtConfirmPass);

        // Register button
        JButton btnRegister = createStyledButton("CREATE ACCOUNT", SUCCESS_COLOR);
        btnRegister.setBounds(20, 362, 310, 45);
        cardPanel.add(btnRegister);

        // Back to login link
        JLabel lblBack = new JLabel("<html><u>Already have an account? Sign in</u></html>");
        lblBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBack.setForeground(SUCCESS_COLOR);
        lblBack.setBounds(20, 417, 310, 20);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.add(lblBack);

        mainPanel.add(cardPanel);
        add(mainPanel);

        btnRegister.addActionListener(e -> handleRegister());
        txtConfirmPass.addActionListener(e -> handleRegister());
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ModernLoginFrame().setVisible(true);
                dispose();
            }
        });
    }

    private void handleRegister() {
        String username = txtUser.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showStyledMessage("Please fill in all fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 3) {
            showStyledMessage("Username must be at least 3 characters!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showStyledMessage("Please enter a valid email address!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            showStyledMessage("Password must be at least 6 characters!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPass)) {
            showStyledMessage("Passwords do not match!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.registerUser(new User(username, password, email))) {
            showStyledMessage("Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new ModernLoginFrame().setVisible(true);
            dispose();
        } else {
            showStyledMessage("Username already exists! Please choose another.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
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
}