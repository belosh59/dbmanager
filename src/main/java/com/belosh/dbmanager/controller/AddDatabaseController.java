package com.belosh.dbmanager.controller;

import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.enity.Database;
import com.belosh.dbmanager.service.DatabaseService;
import com.belosh.dbmanager.view.ContentInitializer;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class AddDatabaseController implements Initializable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ContentInitializer contentInitializer = ServiceLocator.get(ContentInitializer.class.toString(), ContentInitializer.class);
    private final DatabaseService databaseService = ServiceLocator.get(DatabaseService.class.toString(), DatabaseService.class);

    @FXML private TextField newDbHost;
    @FXML private TextField newDbDatabase;
    @FXML private TextField newDbPort;
    @FXML private TextField newDbUser;
    @FXML private TextField newDbPassword;
    @FXML private JFXButton create;
    @FXML private JFXButton cancel;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        create.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(newDbHost.textProperty(),
                        newDbDatabase.textProperty(),
                        newDbUser.textProperty(),
                        newDbPassword.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (newDbHost.getText().isEmpty()
                        || newDbDatabase.getText().isEmpty()
                        || newDbUser.getText().isEmpty()
                        || newDbPassword.getText().isEmpty());

            }
        });
    }

    @FXML
    void createDatabase(ActionEvent event) {
        Database database = new Database();
        database.setHost(newDbHost.getText());
        database.setDatabaseName(newDbDatabase.getText());
        database.setPort(newDbPort.getText());
        database.setUser(newDbUser.getText());
        database.setPassword(newDbPassword.getText());

        databaseService.addDatabaseConfiguration(database);
        contentInitializer.initDatabaseTree();

        log.info("New Database requested to be added: " + database);
        exitModal(event);
    }

    @FXML
    void exitModal(ActionEvent event) {
        newDbHost.clear();
        newDbDatabase.clear();
        newDbPort.clear();
        newDbUser.clear();
        newDbPassword.clear();

        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }
}