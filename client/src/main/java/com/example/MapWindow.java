package com.example;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class MapWindow {
    private final Stage stage;
    private final DataService dataService;
    private final LocalizationManager loc;
    private Canvas canvas;
    private List<StudyGroupDto> currentGroups;
    private Label infoLabel;
    private Label pageLabel;
    private Label title;
    private Button refreshBtn;
    private Button addBtn;
    private Label prevBtn;
    private Label nextBtn;
    private final List<ExplosionParticle> particles = new ArrayList<>();
    private final Set<Integer> removingIds = new HashSet<>();
    private int currentPage = 0;
    private int housesPerPage = 30;
    private double currentScale = 7;
    private static class ExplosionParticle {
        double x, y;
        double vx, vy;
        double size;
        Color color;
        long startTime;
        ExplosionParticle(double x, double y, double vx, double vy, double size, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.color = color;
            this.startTime = System.currentTimeMillis();
        }
    }
    public MapWindow(DataService dataService) {
        this.dataService = dataService;
        this.loc = LocalizationManager.getInstance();
        this.stage = new Stage();
        this.currentGroups = new ArrayList<>();
        createWindow();
    }
    private void createWindow() {
        BorderPane root = new BorderPane();
        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER_LEFT);
        topPanel.setStyle("-fx-background-color: #2c3e50;");
        title = new Label();
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", 16));
        refreshBtn = new Button();
        refreshBtn.setOnAction(e -> {
            currentPage = 0;
            updateMap();
        });
        addBtn = new Button("+");
        addBtn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setOnAction(e -> openAddDialog());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topPanel.getChildren().addAll(title, spacer, refreshBtn, addBtn);
        canvas = new Canvas(1000, 700);
        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setStyle("-fx-background-color: #e8f5e9;");
        canvas.setOnMouseClicked(e -> handleCanvasClick(e.getX(), e.getY()));
        root.setCenter(canvasContainer);
        VBox bottomBox = new VBox(5);
        bottomBox.setStyle("-fx-background-color: #f5f5f5;");
        infoLabel = new Label();
        infoLabel.setPadding(new Insets(5, 10, 0, 10));
        infoLabel.setMaxWidth(Double.MAX_VALUE);
        HBox paginationBox = new HBox(10);
        paginationBox.setPadding(new Insets(5, 10, 10, 10));
        paginationBox.setAlignment(Pos.CENTER);
        Button prevPageBtn = new Button("◀");
        prevPageBtn.setOnAction(e -> {
            if (currentPage > 0) currentPage--;
        });
        Button nextPageBtn = new Button("▶");
        nextPageBtn.setOnAction(e -> {
            if ((currentPage + 1) * housesPerPage < currentGroups.size()) currentPage++;
        });
        pageLabel = new Label();
        paginationBox.getChildren().addAll(prevPageBtn, pageLabel, nextPageBtn);
        bottomBox.getChildren().addAll(infoLabel, paginationBox);
        root.setTop(topPanel);
        root.setBottom(bottomBox);
        Scene scene = new Scene(root, 1000, 780);
        stage.setTitle(loc.get("map.title"));
        stage.setScene(scene);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawAllHouses();
            }
        };
        timer.start();
        updateLanguage();
    }

    private void updateLanguage() {
        stage.setTitle(loc.get("map.title"));
        title.setText(loc.get("map.title"));
        refreshBtn.setText(loc.get("map.refresh"));
        infoLabel.setText(loc.get("map.click_hint"));
    }

    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for (int x = 0; x < canvas.getWidth(); x += 50) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }
        for (int y = 0; y < canvas.getHeight(); y += 50) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        double cx = canvas.getWidth() / 2;
        double cy = canvas.getHeight() / 2;
        gc.strokeLine(cx, 0, cx, canvas.getHeight());
        gc.strokeLine(0, cy, canvas.getWidth(), cy);
    }
    private void startExplosion(double x, double y, double houseSize, Color baseColor) {
        Random rand = new Random();
        Color[] colors = {Color.YELLOW, Color.ORANGE, Color.RED, Color.GOLD, baseColor.brighter()};
        for (int i = 0; i < 40; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double speed = 2 + rand.nextDouble() * 6;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 3;
            Color c = colors[rand.nextInt(colors.length)];
            double ps = 2 + rand.nextDouble() * 5;
            particles.add(new ExplosionParticle(x, y, vx, vy, ps, c));
        }

        for (int i = 0; i < 20; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double speed = 1 + rand.nextDouble() * 3;
            particles.add(new ExplosionParticle(x, y,
                    Math.cos(angle) * speed, Math.sin(angle) * speed - 2,
                    1 + rand.nextDouble() * 2, Color.WHITE));
        }

        for (int i = 0; i < 8; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double speed = 0.5 + rand.nextDouble() * 1.5;
            particles.add(new ExplosionParticle(x, y,
                    Math.cos(angle) * speed, Math.sin(angle) * speed,
                    4 + rand.nextDouble() * 6, Color.LIGHTGRAY));
        }
    }

    private void drawAllHouses() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawGrid();
        long now = System.currentTimeMillis();
        List<ExplosionParticle> deadParticles = new ArrayList<>();
        for (ExplosionParticle p : particles) {
            double elapsed = (now - p.startTime) / 1000.0;
            if (elapsed > 1.5) {
                deadParticles.add(p);
                continue;
            }
            double px = p.x + p.vx * elapsed * 30;
            double py = p.y + p.vy * elapsed * 30 + 0.5 * 80 * elapsed * elapsed;
            double alpha = 1.0 - elapsed / 1.5;
            if (alpha < 0) alpha = 0;

            gc.setGlobalAlpha(alpha);
            gc.setFill(p.color);
            gc.fillOval(px - p.size/2, py - p.size/2, p.size, p.size);
        }
        gc.setGlobalAlpha(1.0);
        particles.removeAll(deadParticles);
        if (currentGroups.isEmpty()) return;
        double cw = canvas.getWidth();
        double ch = canvas.getHeight();
        double maxX = 0, maxY = 0;
        for (StudyGroupDto dto : currentGroups) {
            if (removingIds.contains(dto.getId())) continue;
            if (Math.abs(dto.getCoordX()) > maxX) maxX = Math.abs(dto.getCoordX());
            if (Math.abs(dto.getCoordY()) > maxY) maxY = Math.abs(dto.getCoordY());
        }
        double margin = 50;
        double scaleX = (cw / 2 - margin) / Math.max(maxX, 1);
        double scaleY = (ch / 2 - margin) / Math.max(maxY, 1);
        currentScale = Math.min(scaleX, scaleY);
        currentScale = Math.max(2, Math.min(currentScale, 20));

        int start = currentPage * housesPerPage;
        int end = Math.min(start + housesPerPage, currentGroups.size());

        int totalPages = (currentGroups.size() + housesPerPage - 1) / housesPerPage;
        pageLabel.setText(loc.get("map.page", currentPage + 1, Math.max(1, totalPages)));
        for (int i = start; i < end; i++) {
            StudyGroupDto dto = currentGroups.get(i);
            if (removingIds.contains(dto.getId())) continue;
            double x = cw / 2 + dto.getCoordX() * currentScale;
            double y = ch / 2 - dto.getCoordY() * currentScale;
            double baseSize = Math.max(10, Math.min(35, dto.getStudentsCount() / 5));
            double pulse = 1.0 + Math.sin(System.currentTimeMillis() / 600.0 + i) * 0.08;
            double size = baseSize * pulse;
            Color baseColor = dto.getOwnerId() == dataService.getOwnerId() ? Color.GREEN : Color.BLUE;
            gc.setFill(baseColor);
            gc.fillRect(x - size/2, y - size/2, size, size);
            gc.setStroke(Color.DARKGRAY);
            gc.strokeRect(x - size/2, y - size/2, size, size);
            gc.setFill(Color.SADDLEBROWN);
            gc.fillPolygon(
                    new double[]{x - size/2 - 3, x, x + size/2 + 3},
                    new double[]{y - size/2, y - size - 8, y - size/2},
                    3
            );
            int windows = Math.min(dto.getShouldBeExpelled() / 5, 4);
            gc.setFill(Color.YELLOW);
            double winW = size / 5;
            double winH = size / 4;
            for (int j = 0; j < windows; j++) {
                double wx = x - size/3 + (j % 2) * size/3;
                double wy = y - size/4 + (j / 2) * size/3;
                gc.fillRect(wx, wy, winW, winH);
            }
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", 9));
            gc.fillText(dto.getName(), x - 15, y + size/2 + 12);
        }
    }

    private void handleCanvasClick(double clickX, double clickY) {
        double cw = canvas.getWidth();
        double ch = canvas.getHeight();
        for (int i = currentGroups.size() - 1; i >= 0; i--) {
            StudyGroupDto dto = currentGroups.get(i);
            if (removingIds.contains(dto.getId())) continue;
            double x = cw / 2 + dto.getCoordX() * currentScale;
            double y = ch / 2 - dto.getCoordY() * currentScale;
            double size = Math.max(10, Math.min(35, dto.getStudentsCount() / 5));
            if (clickX >= x - size && clickX <= x + size &&
                    clickY >= y - size - 15 && clickY <= y + size/2 + 15) {
                showHouseDialog(dto);
                return;
            }
        }
    }

    private void showHouseDialog(StudyGroupDto dto) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(loc.get("map.house.title") + " #" + dto.getId());
        dialog.setHeaderText(dto.getName());
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().add(new Label(String.format(
                loc.get("map.house.info"),
                dto.getId(), dto.getName(), dto.getCoordX(), (long) dto.getCoordY(),
                dto.getStudentsCount(), dto.getShouldBeExpelled(),
                dto.getFormOfEducation(), dto.getAdminName()
        )));
        boolean isOwner = dto.getOwnerId() == dataService.getOwnerId();
        ButtonType editBtn = new ButtonType(loc.get("map.house.edit"), ButtonBar.ButtonData.LEFT);
        ButtonType deleteBtn = new ButtonType(loc.get("map.house.delete"), ButtonBar.ButtonData.LEFT);
        ButtonType closeBtn = new ButtonType(loc.get("map.house.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(
                isOwner ? editBtn : null,
                isOwner ? deleteBtn : null,
                closeBtn
        );
        dialog.getDialogPane().getButtonTypes().removeIf(Objects::isNull);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait().ifPresent(result -> {
            if (result == editBtn) {
                EditDialog.showEditDialog(dto.getOriginal()).ifPresent(group -> {
                    dataService.executeCommand("updateId",
                            new String[]{String.valueOf(dto.getId())}, group);
                    dataService.refreshData();
                    updateMap();
                });
            } else if (result == deleteBtn) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        loc.get("map.house.confirm_delete") + " #" + dto.getId() + "?");
                confirm.showAndWait().ifPresent(c -> {
                    if (c == ButtonType.OK) {
                        double cw = canvas.getWidth();
                        double ch = canvas.getHeight();
                        double hx = cw / 2 + dto.getCoordX() * currentScale;
                        double hy = ch / 2 - dto.getCoordY() * currentScale;
                        double hsize = Math.max(10, Math.min(35, dto.getStudentsCount() / 5));
                        Color color = dto.getOwnerId() == dataService.getOwnerId() ?
                                Color.GREEN : Color.BLUE;
                        removingIds.add(dto.getId());
                        startExplosion(hx, hy, hsize, color);
                        dataService.executeCommand("removeId",
                                new String[]{String.valueOf(dto.getId())}, null);
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                Platform.runLater(() -> {
                                    removingIds.remove(dto.getId());
                                    updateMap();
                                    dataService.refreshData();
                                });
                            } catch (InterruptedException e) {}
                        }).start();
                    }
                });
            }
        });
    }

    public void updateMap() {
        currentGroups = new ArrayList<>(dataService.getMasterData());
        currentPage = Math.min(currentPage,
                Math.max(0, (currentGroups.size() - 1) / housesPerPage));
    }
    private void openAddDialog() {
        EditDialog.showAddDialog().ifPresent(group -> {
            dataService.executeCommand("add", null, group);
            dataService.refreshData();
            updateMap();
        });
    }
    public void show() {
        stage.show();
        updateMap();
    }
}