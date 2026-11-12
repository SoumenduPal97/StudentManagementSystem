package com.studentmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConnection {

    private static Properties loadProperties() throws SQLException {

        Properties properties = new Properties();

        try (FileInputStream input =
                     new FileInputStream("db.properties")) {

            properties.load(input);

        } catch (IOException e) {

            throw new SQLException(
                    "Unable to load db.properties. Check its location.",
                    e
            );
        }

        return properties;
    }

    public static Connection getConnection() throws SQLException {

        Properties properties = loadProperties();

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        if (url == null || username == null || password == null) {
            throw new SQLException(
                    "Missing database configuration."
            );
        }

        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            System.out.println("Database connected successfully!");

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
