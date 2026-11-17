
package com.studentmanagement.dao;

import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.Admin;
import com.studentmanagement.util.PasswordUtil;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public boolean hasAnyAdmin() throws SQLException {

        String sql = "SELECT 1 FROM admins LIMIT 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next();
        }
    }

    public boolean createAdmin(
            String username,
            char[] password
    ) throws SQLException, GeneralSecurityException {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        if (password == null || password.length < 12) {
            throw new IllegalArgumentException(
                    "Password must contain at least 12 characters."
            );
        }

        String salt = PasswordUtil.generateSalt();

        int iterations = PasswordUtil.getIterations();

        String passwordHash = PasswordUtil.hashPassword(
                password,
                salt,
                iterations
        );

        String sql = "INSERT INTO admins "
                + "(username, salt, password_hash, iterations) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, salt);
            ps.setString(3, passwordHash);
            ps.setInt(4, iterations);

            return ps.executeUpdate() > 0;
        }
    }

    public Admin authenticate(
            String username,
            char[] password
    ) throws SQLException, GeneralSecurityException {

        if (username == null || username.trim().isEmpty()
                || password == null) {
            return null;
        }

        String sql = "SELECT admin_id, username, salt, "
                + "password_hash, iterations "
                + "FROM admins WHERE username = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int adminId = rs.getInt("admin_id");
                    String storedUsername = rs.getString("username");
                    String salt = rs.getString("salt");
                    String storedHash = rs.getString("password_hash");
                    int iterations = rs.getInt("iterations");

                    if (iterations != PasswordUtil.getIterations()) {
                        throw new SQLException(
                                "Unsupported password hashing configuration."
                        );
                    }

                    boolean valid = PasswordUtil.verifyPassword(
                            password,
                            salt,
                            storedHash,
                            iterations
                    );

                    if (valid) {

                        return new Admin(
                                adminId,
                                storedUsername,
                                salt,
                                storedHash,
                                iterations
                        );
                    }
                }
            }
        }

        return null;
    }

    public boolean changePassword(
            String username,
            char[] currentPassword,
            char[] newPassword
    ) throws SQLException, GeneralSecurityException {

        if (username == null || username.trim().isEmpty()
                || currentPassword == null || newPassword == null) {
            return false;
        }

        if (newPassword.length < 12) {
            throw new IllegalArgumentException(
                    "New password must contain at least 12 characters."
            );
        }

        String selectSQL =
                "SELECT admin_id, salt, password_hash, iterations "
                + "FROM admins WHERE username = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement select =
                     con.prepareStatement(selectSQL)) {

            select.setString(1, username.trim());

            try (ResultSet rs = select.executeQuery()) {

                if (!rs.next()) {
                    return false;
                }

                int adminId = rs.getInt("admin_id");
                String oldSalt = rs.getString("salt");
                String oldHash = rs.getString("password_hash");
                int iterations = rs.getInt("iterations");

                if (iterations != PasswordUtil.getIterations()) {
                    throw new SQLException(
                            "Unsupported password hashing configuration."
                    );
                }

                // Verify current password
                boolean currentValid = PasswordUtil.verifyPassword(
                        currentPassword,
                        oldSalt,
                        oldHash,
                        iterations
                );

                if (!currentValid) {
                    return false;
                }

                // Prevent reusing the current password
                if (PasswordUtil.verifyPassword(
                        newPassword, oldSalt, oldHash, iterations)) {

                    throw new IllegalArgumentException(
                            "New password must be different from the current password."
                    );
                }

                // Generate a fresh salt and hash
                String newSalt = PasswordUtil.generateSalt();

                String newHash = PasswordUtil.hashPassword(
                        newPassword,
                        newSalt,
                        iterations
                );

                String updateSQL =
                        "UPDATE admins SET salt = ?, password_hash = ?, "
                        + "iterations = ? "
                        + "WHERE admin_id = ? AND password_hash = ?";

                try (PreparedStatement update =
                             con.prepareStatement(updateSQL)) {

                    update.setString(1, newSalt);
                    update.setString(2, newHash);
                    update.setInt(3, iterations);
                    update.setInt(4, adminId);
                    update.setString(5, oldHash);

                    return update.executeUpdate() > 0;
                }
            }
        }
    }

}

