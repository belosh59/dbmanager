package com.belosh.dbmanager.view;

import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.enity.DataVO;
import com.belosh.dbmanager.enity.Database;
import com.belosh.dbmanager.service.DatabaseService;
import com.belosh.dbmanager.util.RegexpParser;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class ContentInitializer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private SimpleObjectProperty<TreeItem<String>> treeRootProperty = new SimpleObjectProperty<>(new TreeItem<>("Databases"));
    private SimpleObjectProperty<String> sqlAreaTextProperty = new SimpleObjectProperty<>("");
    private DatabaseService databaseService = ServiceLocator.get(DatabaseService.class.toString(), DatabaseService.class);

    public Parent getTabPane(List<DataVO> dataVOList, String statement) {
        TabPane tabPane = new TabPane();
        tabPane.setId("queryResultTabPane");
        ObservableList<Tab> tabs = tabPane.getTabs();

        String[] statements = RegexpParser.splitSemicolon(statement);
        for (int i = 0; i < dataVOList.size(); i++) {
            DataVO dataVO = dataVOList.get(i);
            String currentStatement = statements[i];

            Parent tabContent = (dataVO.isUpdatable()) ? getResponseLabel(currentStatement) : buildTable(dataVO);
            VBox.setVgrow(tabContent, Priority.ALWAYS);
            Label status = new Label(String.format("Affected Rows: %d. Executed in: %d ms.", dataVO.getChangesCount(), dataVO.getExecutionTime()));

            VBox vBox = new VBox(tabContent, status);
            Tab tab = new Tab(currentStatement, vBox);
            tabs.add(tab);
        }
        return tabPane;
    }

    private Parent getResponseLabel(String statement) {
        Label label = new Label(String.format("Statement: %s successfully executed", statement));
        return new StackPane(label);
    }

    public void initDatabaseTree() {
        TreeItem<String> treeRoot = treeRootProperty.get();
        treeRoot.setExpanded(true);
        treeRoot.getChildren().clear();

        List<Database> databases = databaseService.getConfiguredDatabses();

        for (Database database: databases) {
            FontAwesomeIcon databaseIcon = new FontAwesomeIcon();
            databaseIcon.setIcon(FontAwesomeIconName.DATABASE);

            TreeItem<String> databaseItem = new TreeItem<> (database.getDatabaseName(), databaseIcon);

            treeRoot.getChildren().add(databaseItem);
        }
    }

    public SimpleObjectProperty<TreeItem<String>> getTreeRootProperty() {
        return treeRootProperty;
    }

    public SimpleObjectProperty<String> getSqlAreaTextProperty() {
        return sqlAreaTextProperty;
    }

    private Parent buildTable(DataVO dataVO)  {
        log.info("Start enriching table with ResultSet dataVO");

        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        List<String> columns = dataVO.getColumns();
        for (String column : columns) {
            TableColumn<ObservableList<String>, String> tableColumn = new TableColumn<>(column);
            tableColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(columns.indexOf(column))));
            tableView.getColumns().add(tableColumn);
        }

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (List<String> list : dataVO.getRows()) {
            ObservableList<String> row = FXCollections.observableList(list);
            data.add(row);
        }

        tableView.setItems(data);

        log.info("Data table successfully enriched with dataVO");
        return tableView;
    }
}
