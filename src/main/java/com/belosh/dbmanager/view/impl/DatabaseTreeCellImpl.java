package com.belosh.dbmanager.view.impl;

import com.belosh.dbmanager.service.DatabaseService;
import javafx.scene.control.*;


public class DatabaseTreeCellImpl extends TreeCell<String> {
    private ContextMenu addMenu = new ContextMenu();

    public DatabaseTreeCellImpl(DatabaseService databaseService) {
        MenuItem addMenuItem = new MenuItem("Remove");
        addMenu.getItems().add(addMenuItem);
        addMenuItem.setOnAction(t -> {
            TreeItem<String> item = getTreeItem().getParent();
            item.getChildren().remove(getTreeItem());
            databaseService.removeDatabaseConfigurationByName(getTreeItem().getValue());
        });
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
