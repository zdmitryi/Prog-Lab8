package com.example;

import com.example.enums.*;
import com.example.models.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class EditDialog {

    public static Optional<StudyGroup> showAddDialog() {
        return showDialog(null);
    }

    public static Optional<StudyGroup> showEditDialog(StudyGroup existing) {
        return showDialog(existing);
    }

    private static Optional<StudyGroup> showDialog(StudyGroup existing) {
        Dialog<StudyGroup> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить группу" : "Изменить группу");
        dialog.setHeaderText(existing == null ? "Введите данные новой группы" : "Измените данные группы");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Поля ввода
        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        TextField xField = new TextField(existing != null ? String.valueOf(existing.getCoordinates().getX()) : "");
        TextField yField = new TextField(existing != null ? String.valueOf(existing.getCoordinates().getY()) : "");
        TextField studentsField = new TextField(existing != null ? String.valueOf(existing.getStudentsCount()) : "");
        TextField expelledField = new TextField(existing != null ? String.valueOf(existing.getShouldBeExpelled()) : "");

        ComboBox<String> formBox = new ComboBox<>();
        formBox.getItems().addAll("DISTANCE_EDUCATION", "FULL_TIME_EDUCATION", "EVENING_CLASSES");
        formBox.setValue(existing != null ? existing.getFormOfEducation().name() : "FULL_TIME_EDUCATION");

        ComboBox<String> semBox = new ComboBox<>();
        semBox.getItems().addAll("SECOND", "FIFTH", "EIGHTH");
        semBox.setValue(existing != null ? existing.getSemester().name() : "FIFTH");

        // Админ
        TextField adminNameField = new TextField(existing != null ? existing.getGroupAdmin().getName() : "");
        TextField weightField = new TextField(existing != null && existing.getGroupAdmin().getWeight() != null ?
                String.valueOf(existing.getGroupAdmin().getWeight()) : "");
        TextField eyeColorField = new TextField(existing != null && existing.getGroupAdmin().getEyeColor() != null ?
                existing.getGroupAdmin().getEyeColor().name() : "");
        TextField hairColorField = new TextField(existing != null ?
                existing.getGroupAdmin().getHairColor().name() : "BLACK");
        TextField nationField = new TextField(existing != null ?
                existing.getGroupAdmin().getNationality().name() : "GERMANY");

        // Локация
        TextField locXField = new TextField(existing != null ?
                String.valueOf(existing.getGroupAdmin().getLocation().getX()) : "0");
        TextField locYField = new TextField(existing != null ?
                String.valueOf(existing.getGroupAdmin().getLocation().getY()) : "0");
        TextField locZField = new TextField(existing != null ?
                String.valueOf(existing.getGroupAdmin().getLocation().getZ()) : "0");
        TextField locNameField = new TextField(existing != null && existing.getGroupAdmin().getLocation().getName() != null ?
                existing.getGroupAdmin().getLocation().getName() : "");

        // Добавляем поля в сетку
        int row = 0;
        grid.add(new Label("Название:"), 0, row); grid.add(nameField, 1, row++);
        grid.add(new Label("X (≤225):"), 0, row); grid.add(xField, 1, row++);
        grid.add(new Label("Y:"), 0, row); grid.add(yField, 1, row++);
        grid.add(new Label("Студенты (>0):"), 0, row); grid.add(studentsField, 1, row++);
        grid.add(new Label("Отчислены (>0):"), 0, row); grid.add(expelledField, 1, row++);
        grid.add(new Label("Форма:"), 0, row); grid.add(formBox, 1, row++);
        grid.add(new Label("Семестр:"), 0, row); grid.add(semBox, 1, row++);

        grid.add(new Label("--- АДМИН ---"), 0, row++, 2, 1);
        grid.add(new Label("Имя админа:"), 0, row); grid.add(adminNameField, 1, row++);
        grid.add(new Label("Вес (необяз.):"), 0, row); grid.add(weightField, 1, row++);
        grid.add(new Label("Цвет глаз (GREEN/RED/WHITE/BROWN):"), 0, row); grid.add(eyeColorField, 1, row++);
        grid.add(new Label("Цвет волос (GREEN/BLACK/YELLOW/BROWN):"), 0, row); grid.add(hairColorField, 1, row++);
        grid.add(new Label("Страна (GERMANY/FRANCE/INDIA/SOUTH_KOREA):"), 0, row); grid.add(nationField, 1, row++);

        grid.add(new Label("--- ЛОКАЦИЯ ---"), 0, row++, 2, 1);
        grid.add(new Label("Loc X:"), 0, row); grid.add(locXField, 1, row++);
        grid.add(new Label("Loc Y:"), 0, row); grid.add(locYField, 1, row++);
        grid.add(new Label("Loc Z:"), 0, row); grid.add(locZField, 1, row++);
        grid.add(new Label("Название локации:"), 0, row); grid.add(locNameField, 1, row++);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    double x = Double.parseDouble(xField.getText().trim());
                    long y = Long.parseLong(yField.getText().trim());
                    Coordinates coords = new Coordinates(x, y);

                    long locX = Long.parseLong(locXField.getText().trim());
                    long locY = Long.parseLong(locYField.getText().trim());
                    long locZ = Long.parseLong(locZField.getText().trim());
                    String locName = locNameField.getText().trim();
                    Location loc = new Location(locX, locY, locZ, locName.isEmpty() ? null : locName);

                    String eyeStr = eyeColorField.getText().trim();
                    com.example.enums.Color eyeColor = eyeStr.isEmpty() ? null :
                            com.example.enums.Color.valueOf(eyeStr.toUpperCase());
                    Double weight = weightField.getText().trim().isEmpty() ? null :
                            Double.parseDouble(weightField.getText().trim());
                    com.example.enums.Color hairColor =
                            com.example.enums.Color.valueOf(hairColorField.getText().trim().toUpperCase());
                    Country nation = Country.valueOf(nationField.getText().trim().toUpperCase());

                    Person admin = new Person(adminNameField.getText().trim(), weight,
                            eyeColor, hairColor, nation, loc);

                    StudyGroup group = new StudyGroup(
                            nameField.getText().trim(), coords,
                            Long.parseLong(studentsField.getText().trim()),
                            Integer.parseInt(expelledField.getText().trim()),
                            FormOfEducation.valueOf(formBox.getValue()),
                            Semester.valueOf(semBox.getValue()),
                            admin
                    );

                    return group;
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка ввода: " + e.getMessage());
                    alert.show();
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }
}