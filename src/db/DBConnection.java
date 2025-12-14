package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton-style helper for obtaining a JDBC Connection.
 * Adjust URL, USER, and PASSWORD to your setup.
 */
public final class DBConnection {

    // CHANGE these to match your database:
    private static final String URL = "jdbc:yourdb://localhost:1234/your_database";
    private static final String USER = "your_user";
    private static final String PASSWORD = "your_password";

    private static Connection instance;

    private DBConnection() {
    }

    public static synchronized Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return instance;
        } catch (SQLException e) {
            throw new DataAccessException(DBMessages.CONNECTION_FAILED, e);
        }
    }
}
