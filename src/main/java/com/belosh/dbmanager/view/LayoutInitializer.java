package com.belosh.dbmanager.view;

import com.belosh.dbmanager.ServiceLocator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class LayoutInitializer {

    public void initLayouts() {

        String aboutModal = "view/about-modal.fxml";
        Scene aboutScene = new Scene(getLayout(aboutModal));
        ServiceLocator.register("aboutModal", aboutScene);

        String addDatabaseModal = "view/add-database-modal.fxml";
        Scene addDatabaseScene = new Scene(getLayout(addDatabaseModal));
        ServiceLocator.register("addDatabaseModal", addDatabaseScene);

        String sideBarLayout = "view/sidebar-layout.fxml";
        Parent parent = getLayout(sideBarLayout);
        ServiceLocator.register("sideBarLayout", parent);

        String contentAreaLayout = "view/content-area-layout.fxml";
        VBox vBox = getLayout(contentAreaLayout);
        ServiceLocator.register("contentAreaLayout", vBox);

        String rootLayout = "view/root-layout.fxml";
        BorderPane borderPane = getLayout(rootLayout);
        ServiceLocator.register("rootLayout", borderPane);
    }

    private <T> T getLayout(String layoutResource) {
        try {
            // Loading layout
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource(layoutResource);
            loader.setLocation(url);
            return loader.load();

        } catch (IOException e) {
            throw new RuntimeException("Unable to read layout resource: " + layoutResource, e);
        }
    }
}
