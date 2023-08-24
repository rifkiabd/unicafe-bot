package my.uum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/muhammadrifkiabdillah/Master/RTProgramming/unicafe-bot/src/main/java/my/uum/unicafebot.db");
            System.out.println("Connected to the SQLite database successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the SQLite database!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
