package com.belosh.dbmanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Starter extends Application {

    public static void main(String[] args) {
        // Start dbManager UI
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        // Setup dependencies
        DependencyManager dependencyManager = new DependencyManager();
        dependencyManager.setUp();

        BorderPane rootLayout = ServiceLocator.get("rootLayout", BorderPane.class);

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
