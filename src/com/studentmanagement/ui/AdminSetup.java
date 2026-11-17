
package com.studentmanagement.ui;

import com.studentmanagement.dao.AdminDAO;

import javax.swing.*;
import java.awt.GridLayout;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;

public class AdminSetup {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(AdminSetup::startSetup);
    }

    private static void startSetup() {

        AdminDAO adminDAO = new AdminDAO();

        try {

            if (adminDAO.hasAnyAdmin()) {

                JOptionPane.showMessageDialog(
                        null,
                        "An administrator account already exists.\n"
                                + "Initial setup is no longer available.",
                        "Setup Completed",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            JTextField usernameField = new JTextField(18);
            JPasswordField passwordField = new JPasswordField(18);
            JPasswordField confirmPasswordField = new JPasswordField(18);

            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

            panel.add(new JLabel("Administrator Username:"));
            panel.add(usernameField);

            panel.add(new JLabel("Password (minimum 12 characters):"));
            panel.add(passwordField);

            panel.add(new JLabel("Confirm Password:"));
            panel.add(confirmPasswordField);

            int choice = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Create First Administrator",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            String username = usernameField.getText().trim();

            char[] password = passwordField.getPassword();
            char[] confirmPassword =
                    confirmPasswordField.getPassword();

            try {

                if (username.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Username cannot be empty."
                    );

                    return;
                }

                if (password.length < 12) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Password must contain at least 12 characters."
                    );

                    return;
                }

                if (!Arrays.equals(password, confirmPassword)) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Passwords do not match."
                    );

                    return;
                }

                if (adminDAO.hasAnyAdmin()) {

                    JOptionPane.showMessageDialog(
                            null,
                            "An administrator account already exists."
                    );

                    return;
                }

                boolean created = adminDAO.createAdmin(
                        username,
                        password
                );

                if (created) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Administrator account created successfully!\n"
                                    + "You can now log in.",
                            "Setup Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } else {

                    JOptionPane.showMessageDialog(
                            null,
                            "Unable to create administrator account."
                    );
                }

            } finally {

                // Clear password arrays from memory
                Arrays.fill(password, '\0');
                Arrays.fill(confirmPassword, '\0');

            }

        } catch (SQLException | GeneralSecurityException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Setup failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
