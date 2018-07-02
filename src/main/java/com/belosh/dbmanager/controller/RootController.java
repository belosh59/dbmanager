package com.belosh.dbmanager.controller;

import com.belosh.dbmanager.ServiceLocator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @FXML
    private BorderPane border_pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        VBox contentAreaLayout = ServiceLocator.get("contentAreaLayout", VBox.class);

        border_pane.setCenter(contentAreaLayout);
        log.info("Main BorderPane initialized");
    }

}
