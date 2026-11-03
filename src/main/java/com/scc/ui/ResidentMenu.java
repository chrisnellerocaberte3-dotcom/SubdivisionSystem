package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.util.Scanner;

public class ResidentMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public ResidentMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void show(User user) {
        while (true) {
            System.out.println("\n===== Resident / Homeowner MENU =====");
            System.out.println("Logged in as: " + user.fullName());
            System.out.println("1. (Coming soon) Features for this role");
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;
            System.out.println("Not implemented yet.");
        }
    }
}