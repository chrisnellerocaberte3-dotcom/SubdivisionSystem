package com.yourdomain.dao;

import com.yourdomain.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class UserDao {

    public boolean register(String fullName, String username, String rawPassword, String role) {
        String sql = "INSERT INTO Users (username, password_hash, full_name, role, status) "
                + "VALUES (?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(rawPassword));
            ps.setString(3, fullName);
            ps.setString(4, role);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    public User authenticate(String username, String rawPassword) {
        User user = findByUsername(username);
        if (user != null && verifyPassword(rawPassword, user.passwordHash())) {
            return user;
        }
        return null;
    }

    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    private User findByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, full_name, role, status "
                + "FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("password_hash"), rs.getString("full_name"),
                            rs.getString("role"), rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lookup error: " + e.getMessage());
        }
        return null;
    }

    // ---- Password hashing (salted SHA-256, stored as salt:hash) ----

    static String hashPassword(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + digest(salt, password);
    }

    private static boolean verifyPassword(String password, String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 2) return false;
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        return digest(salt, password).equals(parts[1]);
    }

    private static String digest(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return Base64.getEncoder().encodeToString(
                    md.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}