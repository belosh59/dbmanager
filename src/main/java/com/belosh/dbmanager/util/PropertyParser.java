package com.belosh.dbmanager.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyParser {
    private final Properties properties = new Properties();

    public void configureInstantiationProperties() {
        String propertyLocation = System.getProperty("propesties.location");

        if (propertyLocation != null) {
            try (InputStream inputStream = new FileInputStream(propertyLocation)) {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read property file: " + propertyLocation);
            }
        } else {
            String dbUrl = System.getProperty("db.url");
            String dbUser = System.getProperty("db.user");
            String dbPassword = System.getProperty("db.password");

            if (dbUrl != null && dbUser != null && dbPassword != null) {
                 properties.setProperty("db.url", System.getProperty("db.url"));
                 properties.setProperty("db.user", System.getProperty("db.user"));
                 properties.setProperty("db.password", System.getProperty("db.password"));
                 properties.setProperty("db.driver", System.getProperty("db.driver"));
                 properties.setProperty("db.property", System.getProperty("db.property"));
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
