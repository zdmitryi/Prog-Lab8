package com.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class MainWindow {

    private final Stage stage;
    private final Reader scriptReader;
    private final DataService dataService;
    private AnimationTimer animationTimer;

    // Верхняя панель
    private Label userLabel;
    private Label userIdLabel;
    private Label totalLabel;
    private Label myObjectsLabel;

    // Таблица
    private TableView<StudyGroupDto> table;
    private TextField filterField;

    // Визуализация
    private Label infoLabel;

    // Кнопки
    private Button helpBtn, infoBtn, showBtn, addBtn, updateBtn, removeBtn;
    private Button clearBtn, exitBtn, addIfMinBtn, removeGreaterBtn;
    private Button historyBtn, sumBtn, countLessBtn, printBtn;

    public MainWindow(String login, ClientNetworkManager networkManager,
                      String password, int ownerId) throws IOException, ClassNotFoundException {
        this.dataService = new DataService(networkManager, login, password, ownerId);
        this.stage = new Stage();
        CommandValidator commandValidator = new CommandValidator(networkManager);
        commandValidator.initialize();
        scriptReader = new Reader(commandValidator, networkManager, login, password);
        createWindow();
    }

    private void createWindow() {
        BorderPane root = new BorderPane();

        // ============================================
        // ВЕРХНЯЯ ПАНЕЛЬ
        // ============================================
        HBox topPanel = new HBox(20);
        topPanel.setPadding(new Insets(15));
        topPanel.setAlignment(Pos.CENTER_LEFT);
        topPanel.setStyle("-fx-background-color: #2c3e50;");

        VBox userInfo = new VBox(5);
        userLabel = new Label("Пользователь: " + dataService.getLogin());
        userLabel.setTextFill(Color.WHITE);
        userLabel.setFont(Font.font("Arial", 14));
        userIdLabel = new Label("Ваш ID: " + dataService.getOwnerId());
        userIdLabel.setTextFill(Color.LIGHTGRAY);
        userIdLabel.setFont(Font.font("Arial", 12));
        userInfo.getChildren().addAll(userLabel, userIdLabel);

        VBox statsInfo = new VBox(5);
        totalLabel = new Label("Всего объектов: 0");
        totalLabel.setTextFill(Color.WHITE);
        totalLabel.setFont(Font.font("Arial", 14));
        myObjectsLabel = new Label("Моих объектов: 0");
        myObjectsLabel.setTextFill(Color.LIGHTGREEN);
        myObjectsLabel.setFont(Font.font("Arial", 12));
        statsInfo.getChildren().addAll(totalLabel, myObjectsLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox legendBox = new HBox(15);
        HBox myColorBox = new HBox(5);
        myColorBox.setAlignment(Pos.CENTER_LEFT);
        Canvas greenBox = new Canvas(14, 14);
        greenBox.getGraphicsContext2D().setFill(Color.GREEN);
        greenBox.getGraphicsContext2D().fillRect(0, 0, 14, 14);
        Label myColorLabel = new Label("Мои");
        myColorLabel.setTextFill(Color.WHITE);
        myColorLabel.setFont(Font.font("Arial", 11));
        myColorBox.getChildren().addAll(greenBox, myColorLabel);

        HBox otherColorBox = new HBox(5);
        otherColorBox.setAlignment(Pos.CENTER_LEFT);
        Canvas blueBox = new Canvas(14, 14);
        blueBox.getGraphicsContext2D().setFill(Color.BLUE);
        blueBox.getGraphicsContext2D().fillRect(0, 0, 14, 14);
        Label otherColorLabel = new Label("Чужие");
        otherColorLabel.setTextFill(Color.WHITE);
        otherColorLabel.setFont(Font.font("Arial", 11));
        otherColorBox.getChildren().addAll(blueBox, otherColorLabel);
        legendBox.getChildren().addAll(myColorBox, otherColorBox);

        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll("Русский", "Čeština", "Dansk", "Español (CO)");
        languageBox.setValue("Русский");

        topPanel.getChildren().addAll(userInfo, statsInfo, spacer, legendBox, languageBox);

        // ============================================
        // ЦЕНТР — ТАБЛИЦА
        // ============================================
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));

        filterField = new TextField();
        filterField.setPromptText("Поиск...");
        filterField.setMaxWidth(300);
        filterField.textProperty().addListener((obs, oldVal, newVal) ->
                dataService.applyFilter(newVal));

        table = new TableView<>();
        table.setPlaceholder(new Label("Нет данных для отображения"));
        table.setItems(dataService.getFilteredData());

        createTableColumns();

        centerPanel.getChildren().addAll(filterField, table);

        // ============================================
        // ПРАВАЯ ПАНЕЛЬ — ВИЗУАЛИЗАЦИЯ
        // ============================================
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: #f0f0f0;");
        rightPanel.setPrefWidth(250);

        Label visTitle = new Label("Информация");
        visTitle.setFont(Font.font("Arial", 14));

        infoLabel = new Label("Выберите объект в таблице\nили нажмите на домик");
        infoLabel.setWrapText(true);
        infoLabel.setMaxWidth(230);


// Добавляем слушатель выбора строки в таблице
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showGroupInfo(newVal);
            }
        });
        table.setRowFactory(tv -> {
            TableRow<StudyGroupDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) {
                    table.getSelectionModel().clearSelection();
                }
            });
            return row;
        });

        rightPanel.getChildren().addAll(visTitle, infoLabel);

        // ============================================
        // НИЖНЯЯ ПАНЕЛЬ — КНОПКИ
        // ============================================
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(15));
        bottomPanel.setStyle("-fx-background-color: #ecf0f1;");

        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER);
        helpBtn = new Button("help");
        infoBtn = new Button("info");
        showBtn = new Button("show");
        addBtn = new Button("add");
        updateBtn = new Button("update_by_id");
        removeBtn = new Button("remove_by_id");
        clearBtn = new Button("clear");
        exitBtn = new Button("exit");

        helpBtn.setOnAction(e -> dataService.executeCommand("help", null, null));
        infoBtn.setOnAction(e -> dataService.executeCommand("info", null, null));
        showBtn.setOnAction(e -> dataService.refreshData());
        addBtn.setOnAction(e -> {
            EditDialog.showAddDialog().ifPresent(group -> {
                dataService.executeCommand("add", null, group);
                dataService.refreshData();
            });
        });
        updateBtn.setOnAction(e -> {
            StudyGroupDto selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Выберите объект для редактирования");
                return;
            }
            if (selected.getOwnerId() != dataService.getOwnerId()) {
                showAlert("Это не ваш объект!");
                return;
            }
            System.out.println("Updating ID: " + selected.getId());  // ← ДОБАВЬ

            EditDialog.showEditDialog(selected.getOriginal()).ifPresent(group -> {
                System.out.println("Sending update for ID: " + selected.getId());  // ← ДОБАВЬ
                System.out.println("New name: " + group.getName());  // ← ДОБАВЬ
                dataService.executeCommand("updateId",
                        new String[]{String.valueOf(selected.getId())}, group);
            });
        });
        removeBtn.setOnAction(e -> {
            StudyGroupDto selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Выберите объект для удаления");
                return;
            }
            if (selected.getOwnerId() != dataService.getOwnerId()) {
                showAlert("Это не ваш объект!");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить объект #" + selected.getId() + "?");
            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    dataService.executeCommand("removeId",
                            new String[]{String.valueOf(selected.getId())}, null);
                    dataService.refreshData();  // ← Внутри if
                }
            });
        });
        clearBtn.setOnAction(e -> {
                    dataService.executeCommand("clear", null, null);
                    dataService.refreshData();
                }
        );
        exitBtn.setOnAction(e -> {
            animationTimer.stop();
            dataService.stopAutoRefresh();
            stage.close();
        });

        row1.getChildren().addAll(helpBtn, infoBtn, showBtn, addBtn, updateBtn, removeBtn, clearBtn, exitBtn);

        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER);
        addIfMinBtn = new Button("add_if_min");
        removeGreaterBtn = new Button("remove_greater");
        historyBtn = new Button("history");
        sumBtn = new Button("sum_of_students_count");
        countLessBtn = new Button("count_less_than_group_admin");
        printBtn = new Button("print_field_descending");
        Button executeScriptBtn = new Button("execute_script");
        executeScriptBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл скрипта");
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                new Thread(() -> {
                    try {
                        scriptReader.inConsole(false);
                        scriptReader.setScanner(new Scanner(file));

                        // Выполняем скрипт
                        while (scriptReader.getScanner().hasNextLine()) {
                            scriptReader.readCommand();
                        }

                        Platform.runLater(() -> {
                            showAlert("Скрипт выполнен");
                            dataService.refreshData();
                        });

                    } catch (Exception ex) {
                        Platform.runLater(() -> showAlert("Ошибка скрипта: " + ex.getMessage()));
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        addIfMinBtn.setOnAction(e -> {
            EditDialog.showAddDialog().ifPresent(group -> {
                dataService.executeCommand("addIfMin", null, group);
                dataService.refreshData();
            });
        });
        removeGreaterBtn.setOnAction(e -> {
            EditDialog.showAddDialog().ifPresent(group -> {
                dataService.executeCommand("removeGreater", null, group);
                dataService.refreshData();
            });
        });
        historyBtn.setOnAction(e -> dataService.executeCommand("history", null, null));
        sumBtn.setOnAction(e -> dataService.executeCommand("sumOfStudents", null, null));
        countLessBtn.setOnAction(e -> {
            // Упрощённо: открываем диалог и берём админа
            EditDialog.showAddDialog().ifPresent(group ->
                    dataService.executeCommand("countLessThanGroupAdmin", null, group));
        });
        printBtn.setOnAction(e -> dataService.executeCommand("printField", null, null));

        row2.getChildren().addAll(addIfMinBtn, removeGreaterBtn, historyBtn, sumBtn, countLessBtn, printBtn, executeScriptBtn);

        bottomPanel.getChildren().addAll(row1, row2);

        // ============================================
        // СОБИРАЕМ ВСЁ
        // ============================================
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
                    !(event.getTarget() instanceof Canvas)) {
                table.getSelectionModel().clearSelection();
            }
        });

        // ============================================
        // НАСТРАИВАЕМ DataService
        // ============================================
        dataService.setOnTotalCountChanged(count ->
                totalLabel.setText("Всего объектов: " + count));
        dataService.setOnMyCountChanged(count ->
                myObjectsLabel.setText("Моих объектов: " + count));
        dataService.setOnError(this::showAlert);
        dataService.setOnCommandResult(msg -> {
            if (msg != null && !msg.isEmpty()) {
                showAlert(msg);
            }
        });

        table.setOnMouseClicked(event -> {
                    if (event.getTarget() instanceof TableView && table.getSelectionModel().getSelectedItem() != null) {
                        // Проверяем, что клик был именно по пустому месту, а не по строке
                        // Снимаем выделение
                        table.getSelectionModel().clearSelection();
                    }
        });
        // Запускаем автообновление
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Перерисовываем таблицу для анимации
                table.refresh();
            }
        };
        animationTimer.start();
    }

    @SuppressWarnings("unchecked")
    private void createTableColumns() {
        TableColumn<StudyGroupDto, Number> idCol = new TableColumn<>();
        idCol.setGraphic(createSortableHeader("ID", "id"));
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty());
        idCol.setPrefWidth(50);
        idCol.setSortable(false);

        TableColumn<StudyGroupDto, String> nameCol = new TableColumn<>();
        nameCol.setGraphic(createSortableHeader("Название", "name"));
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        nameCol.setPrefWidth(120);
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
        studentsCol.setGraphic(createSortableHeader("Студенты", "students"));
        studentsCol.setCellValueFactory(cell -> cell.getValue().studentsCountProperty());
        studentsCol.setPrefWidth(70);
        studentsCol.setSortable(false);

        TableColumn<StudyGroupDto, Number> expelledCol = new TableColumn<>();
        expelledCol.setGraphic(createSortableHeader("Отчислены", "expelled"));
        expelledCol.setCellValueFactory(cell -> cell.getValue().shouldBeExpelledProperty());
        expelledCol.setPrefWidth(70);
        expelledCol.setSortable(false);

        TableColumn<StudyGroupDto, String> formCol = new TableColumn<>();
        formCol.setGraphic(createSortableHeader("Форма", "form"));
        formCol.setCellValueFactory(cell -> cell.getValue().formOfEducationProperty());
        formCol.setPrefWidth(120);
        formCol.setSortable(false);

        TableColumn<StudyGroupDto, String> semCol = new TableColumn<>();
        semCol.setGraphic(createSortableHeader("Семестр", "semester"));
        semCol.setCellValueFactory(cell -> cell.getValue().semesterProperty());
        semCol.setPrefWidth(70);
        semCol.setSortable(false);

        TableColumn<StudyGroupDto, String> adminCol = new TableColumn<>();
        adminCol.setGraphic(createSortableHeader("Админ", "admin"));
        adminCol.setCellValueFactory(cell -> cell.getValue().adminNameProperty());
        adminCol.setPrefWidth(100);
        adminCol.setSortable(false);

        // ============================================
        // КОЛОНКА С ВИЗУАЛИЗАЦИЕЙ
        // ============================================
        TableColumn<StudyGroupDto, Void> visCol = new TableColumn<>("Дом");
        visCol.setPrefWidth(120);
        visCol.setSortable(false);

        visCol.setCellFactory(col -> new TableCell<StudyGroupDto, Void>() {
            private final Canvas cellCanvas = new Canvas(100, 50);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    StudyGroupDto dto = getTableRow().getItem();
                    drawHouse(cellCanvas, dto);
                    setGraphic(cellCanvas);

                    // Клик по ячейке — выделяем строку и показываем информацию
                    setOnMouseClicked(e -> {
                        table.getSelectionModel().select(getTableRow().getItem());
                        showGroupInfo(dto);
                    });
                }
            }
        });

        table.getColumns().addAll(
                visCol, idCol, nameCol, xCol, yCol, studentsCol, expelledCol,
                formCol, semCol, adminCol
        );
    }

    private void drawHouse(Canvas canvas, StudyGroupDto dto) {
        var gc = canvas.getGraphicsContext2D();

        // Анимация пульсации через системное время
        double pulse = 1.0 + Math.sin(System.currentTimeMillis() / 500.0) * 0.1;

        double baseSize = Math.max(8, Math.min(30, dto.getStudentsCount() / 3));
        double size = baseSize * pulse;

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double cx = canvas.getWidth() / 2;
        double cy = canvas.getHeight() / 2;

        // Цвет
        if (dto.getOwnerId() == dataService.getOwnerId()) {
            gc.setFill(Color.GREEN);
        } else {
            gc.setFill(Color.BLUE);
        }

        // Дом
        gc.fillRect(cx - size/2, cy - size/2, size, size);

        // Крыша
        gc.setFill(Color.SADDLEBROWN);
        gc.fillPolygon(
                new double[]{cx - size/2, cx, cx + size/2},
                new double[]{cy - size/2, cy - size, cy - size/2},
                3
        );

        // Окна
        int windows = Math.min(dto.getShouldBeExpelled() / 5, 4);
        gc.setFill(Color.YELLOW);
        double winSize = size / 5;
        for (int i = 0; i < windows; i++) {
            double wx = cx - size/4 + (i % 2) * size/3;
            double wy = cy - size/4 + (i / 2) * size/3;
            gc.fillRect(wx, wy, winSize, winSize);
        }
    }
    // Показать информацию о группе
    private void showGroupInfo(StudyGroupDto dto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Группа #" + dto.getId());
        alert.setHeaderText(dto.getName());
        alert.setContentText(
                "ID: " + dto.getId() + "\n" +
                        "Название: " + dto.getName() + "\n" +
                        "Координаты: (" + dto.getCoordX() + ", " + dto.getCoordY() + ")\n" +
                        "Студенты: " + dto.getStudentsCount() + "\n" +
                        "Отчислены: " + dto.getShouldBeExpelled() + "\n" +
                        "Форма: " + dto.getFormOfEducation() + "\n" +
                        "Семестр: " + dto.getSemester() + "\n" +
                        "Админ: " + dto.getAdminName()
        );
        alert.show();
    }

    private Label createSortableHeader(String text, String field) {
        Label header = new Label(text);
        header.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");

        // Храним направление сортировки: null = не сортировано, true = asc, false = desc
        header.setUserData(null);

        header.setOnMouseClicked(e -> {
            Boolean current = (Boolean) header.getUserData();
            boolean asc;

            if (current == null) {
                asc = true;   // Первый клик — по возрастанию
            } else if (current) {
                asc = false;  // Второй клик — по убыванию
            } else {
                asc = true;   // Третий клик — снова по возрастанию
            }

            header.setUserData(asc);
            dataService.sortBy(field, asc);
        });

        return header;
    }

    // ============================================
    // ВИЗУАЛИЗАЦИЯ
    // ============================================

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