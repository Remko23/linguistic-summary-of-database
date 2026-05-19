package org.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connection pool using HikariCP.
 * Configures connection parameters from db.properties or defaults.
 */
public class DbConnectionManager {
    private static HikariDataSource dataSource;

    static {
        Properties properties = new Properties();
        try (InputStream input = DbConnectionManager.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load db.properties, using default configurations.");
        }

        HikariConfig config = new HikariConfig();
        
        // Database credentials & settings
        String host = properties.getProperty("db.host", "localhost");
        String port = properties.getProperty("db.port", "5432");
        String name = properties.getProperty("db.name", "ksr_db");
        
        config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + name);
        config.setUsername(properties.getProperty("db.username", "postgres"));
        config.setPassword(properties.getProperty("db.password", "postgres"));
        config.setDriverClassName("org.postgresql.Driver");

        // Pool optimizations
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.max-size", "10")));
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.min-idle", "2")));
        config.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idle-timeout", "30000")));
        config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connection-timeout", "2000")));

        dataSource = new HikariDataSource(config);
    }

    /**
     * Obtains a connection from the pool.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool datasource.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
