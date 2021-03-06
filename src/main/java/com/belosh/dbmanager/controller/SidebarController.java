package com.belosh.dbmanager.controller;

import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.view.ContentInitializer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    private final ContentInitializer contentInitializer = ServiceLocator.get(ContentInitializer.class.toString(), ContentInitializer.class);

    @FXML private VBox sidebar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initAccelerators();
    }

    @FXML
    void addNewDatabase(ActionEvent event) {
        Scene addDatabaseModal = ServiceLocator.get("addDatabaseModal", Scene.class);

        Stage stage = new Stage();
        stage.setScene(addDatabaseModal);
        stage.setTitle("New Database configuration");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();
    }

    @FXML
    void openSQL(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = new Stage();

        //Show save file dialog
        File file = fileChooser.showOpenDialog(stage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        // Read content to sqlArea
        if (file != null) {
            try {
                byte[] encoded = Files.readAllBytes(file.toPath());
                String content = new String(encoded, Charset.defaultCharset());
                contentInitializer.getSqlAreaTextProperty().set(content);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read file from filesystem");
            }
        }
    }

    @FXML
    void saveSQL(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = new Stage();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        String textAreaContent = contentInitializer.getSqlAreaTextProperty().get();

        // Write content from sqlArea
        if (file != null) {
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.println(textAreaContent);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Unable to write to file in filesystem");
            }
        }
    }

    @FXML
    void openAbout(ActionEvent event) {
        Scene aboutModal = ServiceLocator.get("aboutModal", Scene.class);

        Stage stage = new Stage();
        stage.setScene(aboutModal);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                stage.close();
            }
        });

        stage.showAndWait();
    }

    private void initAccelerators() {
        ReadOnlyObjectProperty<Scene> sceneReadOnlyObjectProperty = sidebar.sceneProperty();

        sceneReadOnlyObjectProperty.addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode()== KeyCode.F1) {
                        openAbout(new ActionEvent());
                    }
                });
            }
        });
    }
}
