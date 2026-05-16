package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class MainWindow {

    private final Stage stage;
    private final DataService dataService;
    private final Reader scriptReader;
    private final LocalizationManager loc;

    private Label userLabel;
    private Label userIdLabel;
    private Label totalLabel;
    private Label myObjectsLabel;
    private Label collectionTypeLabel;

    private TableView<StudyGroupDto> table;

    private Label infoTitle;
    private Label infoLabel;
    private Label historyTitle;
    private Label historyLabel;

    private Button showBtn, addBtn, updateBtn, removeBtn;
    private Button clearBtn, helpBtn, infoBtn, sumBtn, exitBtn;
    private Button executeScriptBtn, openMapBtn;
    private ComboBox<String> languageBox;

    public MainWindow(String login, ClientNetworkManager networkManager,
                      String password, int ownerId) throws IOException, ClassNotFoundException {
        this.loc = LocalizationManager.getInstance();
        this.dataService = new DataService(networkManager, login, password, ownerId);
        this.stage = new Stage();
        CommandValidator commandValidator = new CommandValidator(networkManager);
        commandValidator.initialize();
        this.scriptReader = new Reader(commandValidator, networkManager, login, password);
        createWindow();
    }

    private void createWindow() {
        BorderPane root = new BorderPane();

        HBox topPanel = new HBox(20);
        topPanel.setPadding(new Insets(12, 20, 12, 20));
        topPanel.setAlignment(Pos.CENTER_LEFT);
        topPanel.setStyle("-fx-background-color: #2c3e50;");

        VBox userInfo = new VBox(3);
        userLabel = new Label();
        userLabel.setTextFill(Color.WHITE);
        userLabel.setFont(Font.font("Arial", 14));
        userIdLabel = new Label();
        userIdLabel.setTextFill(Color.LIGHTGRAY);
        userIdLabel.setFont(Font.font("Arial", 11));
        collectionTypeLabel = new Label();
        collectionTypeLabel.setTextFill(Color.LIGHTGRAY);
        collectionTypeLabel.setFont(Font.font("Arial", 11));
        userInfo.getChildren().addAll(userLabel, userIdLabel, collectionTypeLabel);

        VBox statsInfo = new VBox(3);
        totalLabel = new Label();
        totalLabel.setTextFill(Color.WHITE);
        totalLabel.setFont(Font.font("Arial", 14));
        myObjectsLabel = new Label();
        myObjectsLabel.setTextFill(Color.LIGHTGREEN);
        myObjectsLabel.setFont(Font.font("Arial", 12));
        statsInfo.getChildren().addAll(totalLabel, myObjectsLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        languageBox = new ComboBox<>();
        languageBox.getItems().addAll("Русский", "Čeština", "Dansk", "Español (CO)");
        languageBox.setValue("Русский");
        languageBox.setMaxWidth(150);
        languageBox.setOnAction(e -> {
            String selected = languageBox.getValue();
            switch (selected) {
                case "Русский" -> loc.setLocale(new Locale("ru"));
                case "Čeština" -> loc.setLocale(new Locale("cs"));
                case "Dansk" -> loc.setLocale(new Locale("da"));
                case "Español (CO)" -> loc.setLocale(new Locale("es", "CO"));
            }
            updateLanguage();
        });
        topPanel.getChildren().addAll(userInfo, statsInfo, spacer, languageBox);
        VBox centerPanel = new VBox(8);
        centerPanel.setPadding(new Insets(8, 8, 0, 8));
        VBox.setVgrow(centerPanel, Priority.ALWAYS);
        HBox tableToolbar = new HBox(10);
        tableToolbar.setAlignment(Pos.CENTER_LEFT);
        showBtn = new Button();
        showBtn.setPrefWidth(100);
        showBtn.setOnAction(e -> dataService.refreshData());
        addBtn = new Button("+");
        addBtn.setPrefWidth(40);
        addBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setOnAction(e -> {
            EditDialog.showAddDialog().ifPresent(group -> {
                dataService.executeCommand("add", null, group);
                dataService.refreshData();
            });
        });

        Region toolbarSpacer = new Region();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);
        updateBtn = new Button();
        updateBtn.setPrefWidth(100);
        updateBtn.setOnAction(e -> updateSelected());
        removeBtn = new Button();
        removeBtn.setPrefWidth(100);
        removeBtn.setOnAction(e -> removeSelected());
        tableToolbar.getChildren().addAll(showBtn, addBtn, toolbarSpacer, updateBtn, removeBtn);

        table = new TableView<>();
        table.setPlaceholder(new Label("No data to show"));
        table.setItems(dataService.getFilteredData());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        createTableColumns();

        table.setRowFactory(tv -> {
            TableRow<StudyGroupDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    StudyGroupDto dto = row.getItem();
                    if (dto.getOwnerId() == dataService.getOwnerId()) {
                        EditDialog.showEditDialog(dto.getOriginal()).ifPresent(group -> {
                            dataService.executeCommand("updateId",
                                    new String[]{String.valueOf(dto.getId())}, group);
                            dataService.refreshData();
                        });
                    } else {
                        showAlert(loc.get("main.yours"));
                    }
                }
                if (row.isEmpty()) {
                    table.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showObjectInfo(newVal);
            }
        });
        table.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DELETE -> removeSelected();
                case ESCAPE -> table.getSelectionModel().clearSelection();
            }
        });
        centerPanel.getChildren().addAll(tableToolbar, table);
        VBox rightPanel = new VBox(8);
        rightPanel.setPadding(new Insets(8));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: #f0f0f0;");
        rightPanel.setPrefWidth(260);

        infoTitle = new Label();
        infoTitle.setFont(Font.font("Arial", 14));
        infoLabel = new Label(loc.get("main.select"));
        infoLabel.setWrapText(true);
        infoLabel.setMaxWidth(240);
        infoLabel.setStyle("-fx-font-size: 11px;");
        Separator sep1 = new Separator();
        historyTitle = new Label();
        historyTitle.setFont(Font.font("Arial", 14));
        historyLabel = new Label();
        historyLabel.setWrapText(true);
        historyLabel.setMaxWidth(240);
        historyLabel.setMinHeight(150);
        historyLabel.setStyle("-fx-font-size: 11px; -fx-background-color: white; -fx-padding: 8; -fx-border-color: #ddd; -fx-border-radius: 3;");
        Separator sep2 = new Separator();
        openMapBtn = new Button();
        openMapBtn.setMaxWidth(Double.MAX_VALUE);
        openMapBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        openMapBtn.setOnAction(e -> {
            MapWindow mapWindow = new MapWindow(dataService);
            mapWindow.show();
        });

        rightPanel.getChildren().addAll(infoTitle, infoLabel, sep1,
                historyTitle, historyLabel, sep2, openMapBtn);

        HBox bottomPanel = new HBox(15);
        bottomPanel.setPadding(new Insets(12, 20, 12, 20));
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #ecf0f1;");
        clearBtn = new Button("Clear");
        clearBtn.setPrefWidth(130);
        clearBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, loc.get("main.confirm_clear"));
            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    dataService.executeCommand("clear", null, null);
                    dataService.refreshData();
                }
            });
        });

        helpBtn = new Button("Help");
        helpBtn.setPrefWidth(100);
        helpBtn.setOnAction(e -> dataService.executeCommand("help", null, null));
        infoBtn = new Button("Info");
        infoBtn.setPrefWidth(100);
        infoBtn.setOnAction(e -> dataService.executeCommand("info", null, null));
        sumBtn = new Button("Sum");
        sumBtn.setPrefWidth(150);
        sumBtn.setOnAction(e -> dataService.executeCommand("sumOfStudents", null, null));
        executeScriptBtn = new Button("Execute");
        executeScriptBtn.setPrefWidth(130);
        executeScriptBtn.setOnAction(e -> executeScript());
        exitBtn = new Button("Exit");
        exitBtn.setPrefWidth(100);
        exitBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        exitBtn.setOnAction(e -> stage.close());

        bottomPanel.getChildren().addAll(clearBtn, helpBtn, infoBtn, sumBtn,
                executeScriptBtn, exitBtn);

        root.setTop(topPanel);
        root.setCenter(centerPanel);
        root.setRight(rightPanel);
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("Study Group Manager");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        scene.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof TableRow) &&
                    !(event.getTarget() instanceof TableCell) &&
                    !(event.getTarget() instanceof Button) &&
                    !(event.getTarget() instanceof TextArea)) {
                table.getSelectionModel().clearSelection();
            }
        });
        dataService.setOnTotalCountChanged(count -> updateStats());
        dataService.setOnMyCountChanged(count -> updateStats());
        dataService.setOnError(this::showAlert);
        dataService.setOnCommandResult(msg -> {
            if (msg != null && !msg.isEmpty()) {
                showAlert(msg);
            }
        });
        dataService.setOnHistoryResult(msg -> {
            if (msg != null && !msg.isEmpty()) {
                historyLabel.setText(msg);
            }
        });
        updateLanguage();
    }


    private void createTableColumns() {
        TableColumn<StudyGroupDto, Number> idCol = new TableColumn<>();
        idCol.setGraphic(createSortableHeader("ID", "id"));
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty());
        idCol.setPrefWidth(50);
        idCol.setSortable(false);
        TableColumn<StudyGroupDto, String> nameCol = new TableColumn<>();
        nameCol.setGraphic(createSortableHeader("name", "name"));
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        nameCol.setPrefWidth(160);
        nameCol.setSortable(false);
        TableColumn<StudyGroupDto, Number> xCol = new TableColumn<>();
        xCol.setGraphic(createSortableHeader("X", "x"));
        xCol.setCellValueFactory(cell -> cell.getValue().coordXProperty());
        xCol.setPrefWidth(60);
        xCol.setSortable(false);
        TableColumn<StudyGroupDto, Number> yCol = new TableColumn<>();
        yCol.setGraphic(createSortableHeader("Y", "y"));
        yCol.setCellValueFactory(cell -> cell.getValue().coordYProperty());
        yCol.setPrefWidth(60);
        yCol.setSortable(false);
        TableColumn<StudyGroupDto, Number> studentsCol = new TableColumn<>();
        studentsCol.setGraphic(createSortableHeader("students", "students"));
        studentsCol.setCellValueFactory(cell -> cell.getValue().studentsCountProperty());
        studentsCol.setPrefWidth(80);
        studentsCol.setSortable(false);
        TableColumn<StudyGroupDto, Number> expelledCol = new TableColumn<>();
        expelledCol.setGraphic(createSortableHeader("expelled", "expelled"));
        expelledCol.setCellValueFactory(cell -> cell.getValue().shouldBeExpelledProperty());
        expelledCol.setPrefWidth(80);
        expelledCol.setSortable(false);
        TableColumn<StudyGroupDto, String> formCol = new TableColumn<>();
        formCol.setGraphic(createSortableHeader("form", "form"));
        formCol.setCellValueFactory(cell -> cell.getValue().formOfEducationProperty());
        formCol.setPrefWidth(130);
        formCol.setSortable(false);
        TableColumn<StudyGroupDto, String> semCol = new TableColumn<>();
        semCol.setGraphic(createSortableHeader("semester", "semester"));
        semCol.setCellValueFactory(cell -> cell.getValue().semesterProperty());
        semCol.setPrefWidth(70);
        semCol.setSortable(false);
        TableColumn<StudyGroupDto, String> adminCol = new TableColumn<>();
        adminCol.setGraphic(createSortableHeader("admin", "admin"));
        adminCol.setCellValueFactory(cell -> cell.getValue().adminNameProperty());
        adminCol.setPrefWidth(120);
        adminCol.setSortable(false);
        TableColumn<StudyGroupDto, Number> ownerCol = new TableColumn<>();
        ownerCol.setGraphic(createSortableHeader("owner", "owner"));
        ownerCol.setCellValueFactory(cell -> cell.getValue().ownerIdProperty());
        ownerCol.setPrefWidth(60);
        ownerCol.setSortable(false);
        table.getColumns().addAll(
                idCol, nameCol, xCol, yCol, studentsCol, expelledCol,
                formCol, semCol, adminCol, ownerCol
        );
    }

    private Label createSortableHeader(String key, String field) {
        Label header = new Label(key);
        header.setStyle("-fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 11px;");
        header.setUserData(null);
        header.setOnMouseClicked(e -> {
            Boolean current = (Boolean) header.getUserData();
            boolean asc = current == null || !current;
            header.setUserData(asc);
            dataService.sortBy(field, asc);
        });
        return header;
    }

    private void updateLanguage() {
        stage.setTitle(loc.get("main.title"));
        userLabel.setText(loc.get("main.user_label", dataService.getLogin()));
        userIdLabel.setText(loc.get("main.your_id", dataService.getOwnerId()));
        collectionTypeLabel.setText(loc.get("main.collection_type"));
        showBtn.setText(loc.get("main.refresh"));
        updateBtn.setText(loc.get("main.update"));
        removeBtn.setText(loc.get("main.remove"));
        historyTitle.setText(loc.get("main.history"));
        openMapBtn.setText(loc.get("main.map"));
        infoLabel.setText(loc.get("main.select"));

        updateStats();
    }

    private void updateStats() {
        totalLabel.setText(loc.get("main.total", dataService.getMasterData().size()));
        myObjectsLabel.setText(loc.get("main.my_objects",
                dataService.getMasterData().stream()
                        .filter(d -> d.getOwnerId() == dataService.getOwnerId()).count()));
    }

    private void updateSelected() {
        StudyGroupDto selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(loc.get("main.select"));
            return;
        }
        if (selected.getOwnerId() != dataService.getOwnerId()) {
            showAlert(loc.get("main.yours"));
            return;
        }
        EditDialog.showEditDialog(selected.getOriginal()).ifPresent(group -> {
            dataService.executeCommand("updateId",
                    new String[]{String.valueOf(selected.getId())}, group);
            dataService.refreshData();
        });
    }

    private void removeSelected() {
        StudyGroupDto selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(loc.get("main.select"));
            return;
        }
        if (selected.getOwnerId() != dataService.getOwnerId()) {
            showAlert(loc.get("main.yours"));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                loc.get("main.confirm_delete") + " #" + selected.getId() + "?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                dataService.executeCommand("removeId",
                        new String[]{String.valueOf(selected.getId())}, null);
                dataService.refreshData();
            }
        });
    }

    private void showObjectInfo(StudyGroupDto dto) {
        infoLabel.setText(String.format(
                "ID: %d\nName: %s\nX: %.1f | Y: %d\nStudents: %d\nExpelled: %d\nForm: %s\nSemester: %s\nAdmin: %s",
                dto.getId(), dto.getName(),
                dto.getCoordX(), (long) dto.getCoordY(),
                dto.getStudentsCount(), dto.getShouldBeExpelled(),
                dto.getFormOfEducation(), dto.getSemester(),
                dto.getAdminName()
        ));
    }

    private void executeScript() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(loc.get("main.script"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            new Thread(() -> {
                try {
                    scriptReader.inConsole(false);
                    scriptReader.setScanner(new Scanner(file));
                    while (scriptReader.getScanner().hasNextLine()) {
                        scriptReader.readCommand();
                    }
                    Platform.runLater(() -> {
                        showAlert(loc.get("main.script") + " - OK");
                        dataService.refreshData();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert("Error: " + ex.getMessage()));
                }
            }).start();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }


    public void show() {
        stage.show();
        dataService.refreshData();
    }
}