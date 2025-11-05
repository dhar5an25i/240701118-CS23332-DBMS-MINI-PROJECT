import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {
    private JTextField txtUser, txtEmail;
    private JPasswordField txtPass;
    private UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("FindMe - Register");
        setSize(350, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        txtUser = new JTextField();
        panel.add(txtUser);

        panel.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        panel.add(txtPass);

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        JButton btnRegister = new JButton("Register");
        JButton btnBack = new JButton("Back");
        panel.add(btnRegister);
        panel.add(btnBack);

        add(panel);

        btnRegister.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());
            String eMail = txtEmail.getText();

            if (userDAO.registerUser(new User(u, p, eMail))) {
                JOptionPane.showMessageDialog(this, "Registered successfully!");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            }
        });

        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }
}