package ru.bdayloop.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static String URL = "jdbc:postgresql://localhost:5433/" + System.getenv("POSTGRES_DB");
    private static String USER = System.getenv("POSTGRES_USER");
    private static String PASSWORD = System.getenv("POSTGRES_PASSWORD");

    public static void configure(String newURL, String newUSER, String newPASSWORD){
        URL = newURL;
        USER = newUSER;
        PASSWORD= newPASSWORD;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
