package com.belosh.dbmanager.controller;

import com.belosh.dbmanager.enity.ExportDataVO;
import com.belosh.dbmanager.service.DatabaseService;
import com.belosh.dbmanager.service.ExportService;
import com.belosh.dbmanager.view.ContentInitializer;
import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.enity.DataVO;
import com.belosh.dbmanager.task.JdbcDataReadTask;
import com.belosh.dbmanager.view.impl.DatabaseTreeCellImpl;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ContentAreaController implements Initializable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Parent sidebar = ServiceLocator.get("sideBarLayout", Parent.class);

    private final ContentInitializer contentInitializer = ServiceLocator.get(ContentInitializer.class.toString(), ContentInitializer.class);
    private final DatabaseService databaseService = ServiceLocator.get(DatabaseService.class.toString(), DatabaseService.class);
    private final ExportService exportService = ServiceLocator.get(ExportService.class.toString(), ExportService.class);

    private boolean flag;

    @FXML private ProgressIndicator progressIndicator;
    @FXML private TextArea sqlTextArea;
    @FXML private TreeView<String> databaseTree;
    @FXML private Label executedTime;
    @FXML private Label connectedAsLabel;
    @FXML private StackPane stackPane;
    @FXML private JFXButton executeJFXButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseTree.setCellFactory(view -> new DatabaseTreeCellImpl(databaseService));
        databaseTree.rootProperty().bind(contentInitializer.getTreeRootProperty());
        contentInitializer.initDatabaseTree();

        progressIndicator.setVisible(false);
        sqlTextArea.textProperty().bindBidirectional(contentInitializer.getSqlAreaTextProperty());
        initAccelerators();
    }

    @FXML
    private void openSidebar(ActionEvent event) {
        BorderPane border_pane = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
        if (flag) {
            border_pane.setLeft(null);
        } else {
            border_pane.setLeft(sidebar);
        }
        flag = !flag;
    }

    @FXML
    private void executeStatement(ActionEvent event) {
        String sqlStatement = sqlTextArea.getText();
        JdbcDataReadTask task = new JdbcDataReadTask(sqlStatement);

        stackPane.getChildren().clear();
        executedTime.setText("");
        progressIndicator.visibleProperty().bind(task.runningProperty());
        executeJFXButton.disableProperty().bind(task.runningProperty());

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event1 -> {
            List<DataVO> dataVOList = task.getValue();
            Parent parent = contentInitializer.getTabPane(dataVOList, sqlStatement);
            stackPane.getChildren().add(parent);
            executedTime.setText("Overall timeout: " + task.getExecutionTime() + " ms.");
            log.info("JdbcDataReadTask has been successfully executed for statement: " + sqlStatement);
        });

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event1 -> {
            String errorMessage = event1.getSource().getException().getMessage();
            setUpErrorLabel(errorMessage);
            log.error(errorMessage);
        });

        new Thread(task).start();
    }

    @FXML
    private void copyStatement() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String sqlStatement = sqlTextArea.getText();
        ClipboardContent content = new ClipboardContent();
        content.putString(sqlStatement);
        clipboard.setContent(content);
    }

    @FXML
    private void pasteStatement() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        Object clipboardContent = clipboard.getContent(DataFormat.PLAIN_TEXT);
        if (clipboardContent instanceof String) {
            sqlTextArea.setText(((String) clipboardContent));
        }
    }

    @FXML
    private void cutStatement() {
        copyStatement();
        sqlTextArea.setText("");
    }

    @FXML
    private void sqlTextAreaDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }

    @FXML
    private void sqlTextAreaDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        String textDragged = dragboard.getString();
        if (textDragged != null) {
            sqlTextArea.appendText(textDragged);
        }

        for (File file : dragboard.getFiles()) {
            Path path = file.toPath();
            try {
                String contentType = Files.probeContentType(path);
                if (contentType != null && contentType.startsWith("text")) {
                    log.info("File: " + path + " with content " + contentType + " dragged");
                    byte[] encoded = Files.readAllBytes(file.toPath());
                    sqlTextArea.appendText(new String(encoded, Charset.defaultCharset()));
                } else {
                    String warn = "Drag aborted for: " + path + ". Only text content allowed";
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Drag file error");
                    alert.setHeaderText("Invalid content type");
                    alert.setContentText(warn);
                    alert.showAndWait();
                    log.warn(warn);
                }
            } catch (IOException e) {
                log.error("Unable get file content", e);
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void exportToCSV() {
        ObservableList<Node> children = stackPane.getChildren();
        if (children.size() > 0) {
            TabPane tabPane = (TabPane) children.get(0);
            List<ExportDataVO> exportDataVOList = getExportDataVOList(tabPane);
            File excelFile = showModalExport();
            exportService.exportDataToCSV(exportDataVOList, excelFile);
        } else {
            setUpErrorLabel("Nothing to export. Please query data first");
        }
    }

    @SuppressWarnings("unchecked")
    private List<ExportDataVO> getExportDataVOList(TabPane tabPane) {
        List<ExportDataVO> exportDataVOList = new ArrayList<>();
        for (Tab tab : tabPane.getTabs()) {
            log.info("Exporting table from tab: " + tab.getText());

            VBox vbox = (VBox) tab.getContent();
            Node node = vbox.getChildren().get(0);

            TableView<ObservableList<String>> table;
            if (node instanceof TableView ) { // Only data tabs could be exported
                table = (TableView<ObservableList<String>>) node;

                ObservableList<TableColumn<ObservableList<String>, ?>> columns = table.getColumns();
                List<String> columnsText = new ArrayList<>();
                for (TableColumn<ObservableList<String>, ?> column : columns) {
                    String columnText = column.getText();
                    log.info("Column to be exported: " + columnText);
                    columnsText.add(columnText);
                }

                ObservableList<ObservableList<String>> items = table.getItems();
                List<List<String>> data = new ArrayList<>();
                for (ObservableList<String> cells : items) {
                    log.info("Item to be exported: " + cells);
                    data.add(cells.subList(0, cells.size()));
                }

                ExportDataVO exportDataVO = new ExportDataVO();
                exportDataVO.setColumns(columnsText);
                exportDataVO.setRows(data);
                exportDataVO.setSource(tab.getText());

                exportDataVOList.add(exportDataVO);
            }
        }
        return exportDataVOList;
    }

    private File showModalExport() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        return file;
    }

    @FXML
    private void connectDatabase(MouseEvent event) {
        TreeItem<String> selectedDatabase = databaseTree.getSelectionModel().getSelectedItem();

        if (selectedDatabase != null) {

            boolean isDatabase = selectedDatabase.getParent() != null && selectedDatabase.getParent().getParent() == null;
            boolean isPrimaryDoubleClick = event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY;

            if (isPrimaryDoubleClick && isDatabase) {
                String databaseName = selectedDatabase.getValue();
                try {
                    connectedAsLabel.setText("Connecting to: " + databaseName + " ... ");
                    databaseService.setUpDataSource(databaseName);
                    connectedAsLabel.setText("Connected to: " + databaseName);

                    setupDatabaseObjectItems(selectedDatabase);
                } catch (IllegalStateException e) {
                    String error = "Unable to connect to: " + databaseName + ". Check database configuration";
                    log.error(error);
                    connectedAsLabel.setText(error);
                }

            }
        }
    }

    private void setupDatabaseObjectItems(TreeItem<String> selectedDatabase) {
        // Cleanup Tree
        for (TreeItem<String> databaseItem : databaseTree.getRoot().getChildren()) {
            databaseItem.getChildren().clear();
        }

        // Setup database objects for chosen database
        FontAwesomeIcon tableIcon = new FontAwesomeIcon();
        tableIcon.setIcon(FontAwesomeIconName.TABLE);

        TreeItem<String> tables = new TreeItem<>("Tables", tableIcon);
        List<String> userTables = databaseService.getUserTables();
        for (String userTable : userTables) {
            TreeItem<String> table = new TreeItem<>(userTable);
            tables.getChildren().add(table);
        }

        selectedDatabase.getChildren().clear();
        selectedDatabase.getChildren().add(tables);
    }

    private void initAccelerators() {
        ReadOnlyObjectProperty<Scene> sceneReadOnlyObjectProperty = executeJFXButton.sceneProperty();

        sceneReadOnlyObjectProperty.addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode()== KeyCode.F5) {
                        executeStatement(new ActionEvent());
                    }
                });
            }
        });
    }

    private void setUpErrorLabel(String errorMessage) {
        Label errorLabel = new Label(errorMessage);
        errorLabel.setId("errorLabel");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Calibri", 18));
        errorLabel.setWrapText(true);
        errorLabel.autosize();
        stackPane.getChildren().add(errorLabel);
    }
}
