package com.belosh.dbmanager.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcDataReader {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Connection connection;
    private Properties properties;

    public JdbcDataReader(Properties properties) {
        this.properties = properties;
    }

    public ResultSet getResultSet(String sql) throws SQLException {
        Connection connection = getConnection();

        log.info("Getting Result Set as per the query: {}", sql);
        if (sql.toLowerCase().startsWith("select")) {
            return connection.createStatement().executeQuery(sql);
        } else {
            throw new RuntimeException("Unable to get result set from query: " + sql);
        }

    }

    public int execute(String sql) throws SQLException {
        log.info("Getting result set as per the query: {}", sql);
        return connection.createStatement().executeUpdate(sql);
    }

    private Connection connect() throws SQLException {
        String dbUrl = properties.getProperty("db.url");
        String dbUser = properties.getProperty("db.user");
        String dbPassword = properties.getProperty("db.password");
        String dbDriverClass = properties.getProperty("db.driver");
        String dbConnectionProperties = properties.getProperty("db.property");


        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriverClass);
        dataSource.setConnectionProperties(dbConnectionProperties);

        connection = dataSource.getConnection();
        return connection;
    }

    private Connection getConnection() {
        try {
            return connection != null && !connection.isClosed() ? connection : connect();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get connection", e);
        }
    }
}
