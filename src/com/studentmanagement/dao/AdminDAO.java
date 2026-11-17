
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
}

