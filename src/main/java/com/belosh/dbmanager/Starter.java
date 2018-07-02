package com.belosh.dbmanager;

import com.belosh.dbmanager.dao.JdbcDataReader;
import com.belosh.dbmanager.service.DataService;
import com.belosh.dbmanager.service.DatabaseService;
import com.belosh.dbmanager.view.ContentInitializer;
import com.belosh.dbmanager.view.LayoutInitializer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter extends Application {

    private static final Logger log = LoggerFactory.getLogger(Starter.class);

    public static void main(String[] args) {

//        // Parse system properties
//        PropertyParser propertyParser = new PropertyParser();
//        propertyParser.configureInstantiationProperties();
//        Properties properties = propertyParser.getProperties();
//        log.info("System properties has been parsed");

//        // Configure datasource
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setUrl(properties.getProperty("db.url"));
//        dataSource.setUsername(properties.getProperty("db.user"));
//        dataSource.setPassword(properties.getProperty("db.password"));
//        dataSource.setDriverClassName(properties.getProperty("db.driver"));
//        dataSource.setConnectionProperties(properties.getProperty("db.property"));
//        log.info("Datasource has been configured");

        // Create dependencies
//        ServiceLocator.register(BasicDataSource.class.toString(), dataSource);


        JdbcDataReader jdbcDataReader = new JdbcDataReader();
        ServiceLocator.register(JdbcDataReader.class.toString(), jdbcDataReader);

        DatabaseService databaseService = new DatabaseService();
        ServiceLocator.register(DatabaseService.class.toString(), databaseService);

        DataService dataService = new DataService();
        ServiceLocator.register(DataService.class.toString(), dataService);

        ContentInitializer contentInitializer = new ContentInitializer();
        ServiceLocator.register(ContentInitializer.class.toString(), contentInitializer);


        LayoutInitializer layoutInitializer = new LayoutInitializer();
        layoutInitializer.initLayouts();

        log.info("ServiceLocator dependencies has been registered");

        // Start dbManager UI
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        BorderPane rootLayout = ServiceLocator.get("rootLayout", BorderPane.class);

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
