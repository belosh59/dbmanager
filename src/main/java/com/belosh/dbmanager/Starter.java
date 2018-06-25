package com.belosh.dbmanager;

import com.belosh.dbmanager.dao.JdbcDataReader;
import com.belosh.dbmanager.view.DataEnricher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Properties;


public class Starter extends Application {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final Properties properties = new Properties();

    private JdbcDataReader jdbcDataReader;
    private DataEnricher dataEnricher = new DataEnricher();

    public static void main(String[] args) {
        configureJdbcDataReader();
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("dbManager");

        jdbcDataReader = new JdbcDataReader(properties);

        // View Items
        Label statusLabel = new Label();

        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextArea sqlInput = new TextArea();

        Button executeButton = new Button("Execute");
        executeButton.setOnAction(e -> {
            processSqlStatement(statusLabel, tableView, sqlInput);
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10,10,10,10));
        layout.getChildren().addAll(sqlInput, executeButton, statusLabel, tableView);

        Scene scene = new Scene(layout, 500, 800);
        scene.getStylesheets().add("style/dbmanager.css");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    private void processSqlStatement(Label statusLabel, TableView<ObservableList<String>> tableView, TextArea sqlInput) {
        cleanupLayout(statusLabel, tableView);

        String sqlStatement = sqlInput.getText();

        try {
            if (sqlStatement.toLowerCase().startsWith("select")) {
                log.debug("Executing query to select data: {}", sqlStatement);

                try (ResultSet resultSet = jdbcDataReader.getResultSet(sqlStatement)) {
                    dataEnricher.enrichTableView(tableView, resultSet);
                }
            } else if (sqlStatement.isEmpty()) {
                statusLabel.setText("Statement is empty ");
            } else {
                log.debug("Executing data modification query: {}", sqlStatement);
                int rowsAffected = jdbcDataReader.execute(sqlStatement);
                statusLabel.setText("Rows Affected: " + rowsAffected);
            }
        } catch (Exception excp) {
            statusLabel.setText(excp.getCause().getMessage());
            excp.printStackTrace();
        }
    }

    private static void configureJdbcDataReader() {
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

    private void cleanupLayout(Label label, TableView tableView) {
        label.setText("");
        tableView.getItems().clear();
        tableView.getColumns().clear();
    }
}
