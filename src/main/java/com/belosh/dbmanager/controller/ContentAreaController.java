package com.belosh.dbmanager.controller;

import com.belosh.dbmanager.service.DatabaseService;
import com.belosh.dbmanager.view.ContentInitializer;
import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.enity.DataVO;
import com.belosh.dbmanager.task.JdbcDataReadTask;
import com.belosh.dbmanager.view.impl.DatabaseTreeCellImpl;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class ContentAreaController implements Initializable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Parent sidebar = ServiceLocator.get("sideBarLayout", Parent.class);

    private ContentInitializer contentInitializer = ServiceLocator.get(ContentInitializer.class.toString(), ContentInitializer.class);
    private DatabaseService databaseService = ServiceLocator.get(DatabaseService.class.toString(), DatabaseService.class);

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
        //sqlTextArea.setVisible(false);
        sqlTextArea.textProperty().bindBidirectional(contentInitializer.getSqlAreaTextProperty());
    }

    @FXML
    private void open_sidebar(ActionEvent event) throws IOException {
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
        });

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event1 -> {
            String errorMessage = event1.getSource().getException().getMessage();
            Label errorLabel = new Label(errorMessage);
            errorLabel.setTextFill(Color.RED);
            errorLabel.setWrapText(true);
            errorLabel.autosize();
            stackPane.getChildren().add(errorLabel);
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
                    log.info(String.format("File: %s with content %s dragged", path, contentType));
                    byte[] encoded = Files.readAllBytes(file.toPath());
                    sqlTextArea.appendText(new String(encoded, Charset.defaultCharset()));
                } else {
                    String warn = String.format("Drag aborted for: %s. Only text content allowed", path);
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
    private void connectDatabase(MouseEvent event) {
        TreeItem<String> item = databaseTree.getSelectionModel().getSelectedItem();
        boolean isDatabase = item.getParent() != null && item.getParent().getParent() == null;
        boolean isPrimaryDoubleClick = event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY;

        if(isPrimaryDoubleClick && isDatabase) {
            String databaseName = item.getValue();
            try {
                databaseService.setUpDataSource(databaseName);
                connectedAsLabel.setText("Connected to: " + databaseName);
                sqlTextArea.setVisible(true);

                FontAwesomeIcon tableIcon = new FontAwesomeIcon();
                tableIcon.setIcon(FontAwesomeIconName.TABLE);

                TreeItem<String> tables = new TreeItem<>("Tables", tableIcon);
                tables.setExpanded(true);

                List<String> userTables = databaseService.getUserTables();
                for (String userTable : userTables) {
                    TreeItem<String> table = new TreeItem<>(userTable);
                    tables.getChildren().add(table);
                }

                item.getChildren().add(tables);
            } catch (InvalidStateException e) {
                connectedAsLabel.setText("Unable to connect to: " + databaseName + ". Check database configuration");
            }
            item.setExpanded(true);
        }
    }


    private void initAccelerators() {
//        executeJFXButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
//            if (event.getCode()== KeyCode.F5) {
//                executeStatement(new ActionEvent());
//            }
//        });

//        Scene scene = executeJFXButton.getScene();
//
//        executeJFXButton.getScene().getAccelerators().put(
//                new KeyCodeCombination(KeyCode.F5),
//                () -> executeJFXButton.fire()
//        );
    }

}
