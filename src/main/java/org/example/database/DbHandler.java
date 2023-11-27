package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHandler {

    static Connection connection;

    public static Connection getConnection() {
        if (connection == null)
            createConnection();

        return connection;
    }

    private static void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s/%s", SchemeDB.HOST, SchemeDB.DB_NAME);
            connection = DriverManager.getConnection(url, "root", "root");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
