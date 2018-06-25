package com.belosh.dbmanager.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataEnricher {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void enrichTableView(TableView<ObservableList<String>> tableView, ResultSet resultSet) {
        log.info("Start enriching table with ResultSet data");
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try {
            int columnCount = resultSet.getMetaData().getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                final int columnId = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(resultSet.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(columnId)));

                tableView.getColumns().add(col);
            }

            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                }
                data.add(row);
                log.debug("Row added: {} ",  row);
            }

            log.info("Table successfully enriched with data");
            tableView.setItems(data);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to enrich TableView with data", e);
        }
    }
}
