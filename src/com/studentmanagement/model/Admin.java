
package com.studentmanagement.model;

public class Admin {

    private final int adminId;
    private final String username;
    private final String salt;
    private final String passwordHash;
    private final int iterations;

    public Admin(int adminId, String username, String salt,
                 String passwordHash, int iterations) {

        this.adminId = adminId;
        this.username = username;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.iterations = iterations;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }

    public String getSalt() {
        return salt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getIterations() {
        return iterations;
    }
}
