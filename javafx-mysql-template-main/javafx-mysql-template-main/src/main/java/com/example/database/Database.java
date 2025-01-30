package com.example.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static final Properties databaseProperties = new Properties();
    private static String connectionString;
    private static boolean isInitialized = false;

    private Database() {} // no constructor, should be referenced statically

    public static void initialize(String propertiesFilePath) throws Exception {
        // Initialize MySQL driver (from mysql-connector-j dependency in build.gradle)
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Build connection string
        databaseProperties.load(new FileInputStream(propertiesFilePath));
        String serverName = databaseProperties.getProperty("database.server");
        String serverPort = databaseProperties.getProperty("database.port");
        String schemaName = databaseProperties.getProperty("database.schema");
        String username = databaseProperties.getProperty("database.username");
        String password = databaseProperties.getProperty("database.password");
        connectionString = "jdbc:mysql://" + serverName + ":" + serverPort + "/" + schemaName
                            + "?user=" + username + "&password=" + password;

        isInitialized = true;
    }

    public static Connection newConnection() throws SQLException {
        if (!isInitialized) {
            throw new RuntimeException("Database was not successfully initialized.");
        }

        return DriverManager.getConnection(connectionString);
    }
}
