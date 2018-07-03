package com.belosh.dbmanager.view.impl;

import com.belosh.dbmanager.service.DatabaseService;
import javafx.scene.control.*;


public class DatabaseTreeCellImpl extends TreeCell<String> {
    private final ContextMenu addMenu = new ContextMenu();

    public DatabaseTreeCellImpl(DatabaseService databaseService) {
        MenuItem remove = new MenuItem("Remove");
        addMenu.getItems().add(remove);
        remove.setOnAction(t -> {
            TreeItem<String> item = getTreeItem().getParent();
            item.getChildren().remove(getTreeItem());
            databaseService.removeDatabaseConfigurationByName(getTreeItem().getValue());
        });

        //TODO: Under design
//        MenuItem change = new MenuItem("Change");
//        addMenu.getItems().add(change);
//        change.setOnAction(t -> {
//            Scene addDatabaseModal = ServiceLocator.get("addDatabaseModal", Scene.class);
//
//            Stage stage = new Stage();
//            stage.setScene(addDatabaseModal);
//            stage.setTitle("Change Database configuration for: " + getString());
//            stage.initModality(Modality.WINDOW_MODAL);
//            stage.initStyle(StageStyle.UNDECORATED);
//            stage.showAndWait();
//        });

    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
            if (getTreeItem().getParent() != null && getTreeItem().getParent().getParent() == null){
                setContextMenu(addMenu);
            }
        }
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
