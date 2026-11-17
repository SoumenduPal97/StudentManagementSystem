
package com.studentmanagement.ui;

import com.studentmanagement.dao.AdminDAO;
import com.studentmanagement.model.Admin;

import javax.swing.*;
import java.awt.*;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;

public class AdminLogin extends JFrame {

    private final AdminDAO adminDAO = new AdminDAO();

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    private final JButton loginButton = new JButton("Login");

    public AdminLogin() {

        setTitle("Administrator Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        createLoginPanel();
    }

    private void createLoginPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel(
                "STUDENT MANAGEMENT SYSTEM",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(35, 47, 62));

        JLabel subtitle = new JLabel(
                "Administrator Login",
                SwingConstants.CENTER
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel header = new JPanel(new GridLayout(2, 1, 5, 5));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 8));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        loginButton.setBackground(new Color(35, 47, 62));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);

        loginButton.addActionListener(e -> login());

        getRootPane().setDefaultButton(loginButton);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void login() {

        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password."
            );

            Arrays.fill(password, '\0');
            return;
        }

        loginButton.setEnabled(false);

        try {

            Admin admin = adminDAO.authenticate(username, password);

            if (admin != null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Welcome, " + admin.getUsername() + "!",
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dispose();

                SwingUtilities.invokeLater(() -> {

                    StudentManagementGUI dashboard =
                            new StudentManagementGUI(admin);

                    dashboard.setVisible(true);
                });

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                passwordField.setText("");
            }

        } catch (SQLException | GeneralSecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login failed due to a system error.\n"
                            + "Please check your database connection.",
                    "System Error",
                    JOptionPane.ERROR_MESSAGE
            );

            System.err.println("Login error: " + e.getMessage());

        } finally {

            Arrays.fill(password, '\0');
            loginButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AdminLogin login = new AdminLogin();
            login.setVisible(true);

        });
    }
}
