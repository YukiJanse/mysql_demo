package se.jensen.yuki.jdbc_demo.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private final Properties properties;
    private final HikariDataSource dataSource;

    private ConfigurationManager() {
        properties = new Properties();
        try (InputStream input = ClassLoader.getSystemResourceAsStream("application.properties")) {
            properties.load(input);
            logger.info("Successfully loaded application.properties.");
        } catch (IOException e) {
            logger.error("Failed to load application.properties.", e);
            e.printStackTrace();
        }
        dataSource = createDataSource();
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();

        // Basic settings
        config.setJdbcUrl(getProperty("db.url"));
        config.setUsername(getProperty("db.username"));
        config.setPassword(getProperty("db.password"));

        // Pool-settings
        config.setMaximumPoolSize(10); // Max number of connections in the pool
        config.setMinimumIdle(2); // Minimum number of idle connections
        config.setConnectionTimeout(30000); // 30-second timeout
        config.setIdleTimeout(600000); // 10-minutes idle timeout
        config.setMaxLifetime(1800000); // 30-minutes max lifetime

        // Performance-settings
        config.setAutoCommit(true);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Pool name for logging
        config.setPoolName("MySQL-Pool");

        logger.info("HikariCP DataSource created with pool size: {}", config.getMaximumPoolSize());

        return new HikariDataSource(config);
    }

    private String getProperty(String key) {
        return properties.getProperty(key);
    }
//
//    public Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(getProperty("db.url"), getProperty("db.username"), getProperty("db.password"));
//    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP DataSource closed");
        }
    }
}
