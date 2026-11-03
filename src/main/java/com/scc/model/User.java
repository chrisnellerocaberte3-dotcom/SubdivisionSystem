package com.yourdomain.model;

public record User(int userId, String username, String passwordHash,
                   String fullName, String role, String status) {

    public static final String ROLE_GUARD = "SECURITY_GUARD";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_RESIDENT = "RESIDENT";
}