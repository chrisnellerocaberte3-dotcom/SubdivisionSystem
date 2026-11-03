package com.yourdomain.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private DatabaseInitializer() { }

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS Users ("
                    + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL UNIQUE,"
                    + "password_hash TEXT NOT NULL,"
                    + "full_name TEXT NOT NULL,"
                    + "role TEXT NOT NULL,"
                    + "status TEXT NOT NULL DEFAULT 'PENDING')");

            stmt.execute("CREATE TABLE IF NOT EXISTS Resident ("
                    + "resident_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "resident_name TEXT NOT NULL,"
                    + "block_lot_address TEXT NOT NULL,"
                    + "contact_number TEXT,"
                    + "status TEXT NOT NULL DEFAULT 'ACTIVE')");

            stmt.execute("CREATE TABLE IF NOT EXISTS Vehicle ("
                    + "vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "resident_id INTEGER NOT NULL,"
                    + "plate_number TEXT NOT NULL UNIQUE,"
                    + "vehicle_type TEXT,"
                    + "vehicle_color TEXT,"
                    + "FOREIGN KEY (resident_id) REFERENCES Resident(resident_id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS GateEntry ("
                    + "entry_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "resident_id INTEGER NOT NULL,"
                    + "visitor_name TEXT NOT NULL,"
                    + "purpose_of_visit TEXT,"
                    + "plate_number TEXT,"
                    + "time_in TEXT NOT NULL,"
                    + "time_out TEXT,"
                    + "entry_status TEXT NOT NULL DEFAULT 'INSIDE',"
                    + "FOREIGN KEY (resident_id) REFERENCES Resident(resident_id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Activity_Logs ("
                    + "log_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_id INTEGER NOT NULL,"
                    + "action TEXT NOT NULL,"
                    + "description TEXT,"
                    + "timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES Users(user_id))");

            seedDefaultAdmin(conn);
            System.out.println("[DB] Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[DB] Failed to initialize database: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void seedDefaultAdmin(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Users WHERE role = 'ADMIN'")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        String sql = "INSERT INTO Users (username, password_hash, full_name, role, status) "
                + "VALUES (?, ?, ?, 'ADMIN', 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, UserDao.hashPassword("admin123"));
            ps.setString(3, "Default Administrator");
            ps.executeUpdate();
            System.out.println("[DB] Default admin created (username: admin / password: admin123).");
        }
    }
}