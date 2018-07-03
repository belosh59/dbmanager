package com.belosh.dbmanager.service;

import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.dao.JdbcDataReader;
import com.belosh.dbmanager.enity.Database;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String DATABASE_DRIVER = "org.postgresql.ds.PGPoolingDataSource";
    private static final String DATABASE_PROPERTIES = "ssl=true;sslfactory=org.postgresql.ssl.NonValidatingFactory";
    private JdbcDataReader jdbcDataReader = ServiceLocator.get(JdbcDataReader.class.toString(), JdbcDataReader.class);

    private File configFile = null;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<Database> databases = new ArrayList<>();


    public List<Database> getConfiguredDatabses() {
        if (configFile == null) {
            throw new IllegalStateException("Database configuration file has not been configured");
        }

        try {
            if (configFile.createNewFile()) {
                log.warn("Database config file missed and was created");
                return new ArrayList<>();
            } else {
                CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(List.class, Database.class);
                databases.clear();
                databases.addAll(objectMapper.readValue(configFile, type));
            }
            return new ArrayList<>(databases);
        } catch (IOException e) {
            log.error("Unable to load database configuration file", e);
            throw new RuntimeException(e);
        }
    }

    public void addDatabaseConfiguration(Database database) {
        databases.add(database);
        updateDatabaseConfigurationFile();
    }

    public void removeDatabaseConfigurationByName(String databaseName) {
        Database database = getDatabaseByName(databaseName);
        databases.remove(database);
        updateDatabaseConfigurationFile();
    }

    public void setUpDataSource(String databaseName) {
        log.info("Starting datasource configuration for: " + databaseName);

        Database requiredDatabase = getDatabaseByName(databaseName);

        StringBuilder builder = new StringBuilder("jdbc:postgresql://")
                .append(requiredDatabase.getHost())
                .append(":")
                .append(requiredDatabase.getPort())
                .append("/")
                .append(requiredDatabase.getDatabaseName());

        String databaseUrl = builder.toString();

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(requiredDatabase.getUser());
        dataSource.setPassword(requiredDatabase.getPassword());
        dataSource.setDriverClassName(DATABASE_DRIVER);
        dataSource.setConnectionProperties(DATABASE_PROPERTIES);

        jdbcDataReader.setDataSource(dataSource);
        jdbcDataReader.checkConnection();
        log.info("New datasource configured for database: " + databaseName);
    }

    public List<String> getUserTables() {
        return jdbcDataReader.getUserTables();
    }

    Database getDatabaseByName(String databaseName) {
        for (Database database : databases) {
            if (database.getDatabaseName().equals(databaseName)) {
                return database;
            }
        }
        throw new RuntimeException("Requested database was not found: " + databaseName);
    }

    void updateDatabaseConfigurationFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, databases);
        } catch (IOException e) {
            log.error("Unable to save properties to database configuration file", e);
            throw new RuntimeException(e);
        }
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }
}
