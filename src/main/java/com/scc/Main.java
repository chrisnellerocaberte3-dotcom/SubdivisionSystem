package com.yourdomain;

import com.yourdomain.dao.DatabaseInitializer;
import com.yourdomain.dao.UserDao;
import com.yourdomain.ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        UserDao userDao = new UserDao();
        try (Scanner scanner = new Scanner(System.in)) {

            new Authentication(userDao, scanner).start();


        }
    }
}