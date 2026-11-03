package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n===== SUBDIVISION GATE ENTRY SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1": login(); break;
                case "2": register(); break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Registration ---");
        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken.");
            return;
        }

        String role = chooseRole();
        if (role == null) return;

        if (userDao.register(fullName, username, password, role)) {
            System.out.println("Registration successful! Status: PENDING. "
                    + "Wait for an administrator to approve your account.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private String chooseRole() {
        System.out.println("Select role:");
        System.out.println("1. Security Guard / Gate Personnel");
        System.out.println("2. Subdivision Administrator / HOA Staff");
        System.out.println("3. Resident / Homeowner");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": return User.ROLE_GUARD;
            case "2": return User.ROLE_ADMIN;
            case "3": return User.ROLE_RESIDENT;
            default:
                System.out.println("Invalid role.");
                return null;
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDao.authenticate(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        if (!"ACTIVE".equalsIgnoreCase(user.status())) {
            System.out.println("Your account is " + user.status()
                    + ". Please wait for administrator approval.");
            return;
        }
        routeByRole(user);
    }

    private void routeByRole(User user) {
        switch (user.role()) {
            case User.ROLE_GUARD:    new GuardMenu(userDao, scanner).show(user); break;
            case User.ROLE_ADMIN:    new AdminMenu(userDao, scanner).show(user); break;
            case User.ROLE_RESIDENT: new ResidentMenu(userDao, scanner).show(user); break;
            default: System.out.println("Unknown role: " + user.role());
        }
    }
}