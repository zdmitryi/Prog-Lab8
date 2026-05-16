package com.example;
import com.example.enums.*;
import com.example.models.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Optional;
public class EditDialog {
    private static final LocalizationManager loc = LocalizationManager.getInstance();
    private static Label titleLabel;
    private static Label nameLabel, xLabel, yLabel, studentsLabel, expelledLabel;
    private static Label formLabel, semLabel;
    private static Label adminSectionLabel, adminNameLabel, weightLabel;
    private static Label eyeColorLabel, hairColorLabel, nationLabel;
    private static Label locSectionLabel, locXLabel, locYLabel, locZLabel, locNameLabel;
    private static ButtonType saveBtn;
    private static ButtonType cancelBtn;
    public static Optional<StudyGroup> showAddDialog() {
        return showDialog(null);
    }
    public static Optional<StudyGroup> showEditDialog(StudyGroup existing) {
        return showDialog(existing);
    }
    private static Optional<StudyGroup> showDialog(StudyGroup existing) {
        Dialog<StudyGroup> dialog = new Dialog<>();
        dialog.setTitle(loc.get("dialog." + (existing == null ? "add" : "edit") + ".title"));
        dialog.setHeaderText(loc.get("dialog." + (existing == null ? "add" : "edit") + ".header"));
        saveBtn = new ButtonType(loc.get("dialog.save"), ButtonBar.ButtonData.OK_DONE);
        cancelBtn = new ButtonType(loc.get("dialog.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
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
        TextField adminNameField = new TextField(existing != null ? existing.getGroupAdmin().getName() : "");
        TextField weightField = new TextField(existing != null && existing.getGroupAdmin().getWeight() != null ?
                String.valueOf(existing.getGroupAdmin().getWeight()) : "");
        TextField eyeColorField = new TextField(existing != null && existing.getGroupAdmin().getEyeColor() != null ?
                existing.getGroupAdmin().getEyeColor().name() : "BLACK");
        TextField hairColorField = new TextField(existing != null ?
                existing.getGroupAdmin().getHairColor().name() : "BLACK");
        TextField nationField = new TextField(existing != null ?
                existing.getGroupAdmin().getNationality().name() : "GERMANY");
        TextField locXField = new TextField(existing != null ?
                String.valueOf(existing.getGroupAdmin().getLocation().getX()) : "0");
        TextField locYField = new TextField(existing != null ?
                String.valueOf(existing.getGroupAdmin().getLocation().getY()) : "0");
        TextField locZField = new TextField(existing != null ?
                String.valueOf(existing.getGroupAdmin().getLocation().getZ()) : "0");
        TextField locNameField = new TextField(existing != null && existing.getGroupAdmin().getLocation().getName() != null ?
                existing.getGroupAdmin().getLocation().getName() : "Location");
        nameLabel = new Label();
        xLabel = new Label();
        yLabel = new Label();
        studentsLabel = new Label();
        expelledLabel = new Label();
        formLabel = new Label();
        semLabel = new Label();
        adminSectionLabel = new Label();
        adminNameLabel = new Label();
        weightLabel = new Label();
        eyeColorLabel = new Label();
        hairColorLabel = new Label();
        nationLabel = new Label();
        locSectionLabel = new Label();
        locXLabel = new Label();
        locYLabel = new Label();
        locZLabel = new Label();
        locNameLabel = new Label();
        int row = 0;
        grid.add(nameLabel, 0, row); grid.add(nameField, 1, row++);
        grid.add(xLabel, 0, row); grid.add(xField, 1, row++);
        grid.add(yLabel, 0, row); grid.add(yField, 1, row++);
        grid.add(studentsLabel, 0, row); grid.add(studentsField, 1, row++);
        grid.add(expelledLabel, 0, row); grid.add(expelledField, 1, row++);
        grid.add(formLabel, 0, row); grid.add(formBox, 1, row++);
        grid.add(semLabel, 0, row); grid.add(semBox, 1, row++);
        grid.add(adminSectionLabel, 0, row++, 2, 1);
        grid.add(adminNameLabel, 0, row); grid.add(adminNameField, 1, row++);
        grid.add(weightLabel, 0, row); grid.add(weightField, 1, row++);
        grid.add(eyeColorLabel, 0, row); grid.add(eyeColorField, 1, row++);
        grid.add(hairColorLabel, 0, row); grid.add(hairColorField, 1, row++);
        grid.add(nationLabel, 0, row); grid.add(nationField, 1, row++);
        grid.add(locSectionLabel, 0, row++, 2, 1);
        grid.add(locXLabel, 0, row); grid.add(locXField, 1, row++);
        grid.add(locYLabel, 0, row); grid.add(locYField, 1, row++);
        grid.add(locZLabel, 0, row); grid.add(locZField, 1, row++);
        grid.add(locNameLabel, 0, row); grid.add(locNameField, 1, row++);
        updateDialogLanguage(dialog, existing == null);
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
                    Color eyeColor = eyeStr.isEmpty() ? null :
                            com.example.enums.Color.valueOf(eyeStr.toUpperCase());
                    Double weight = weightField.getText().trim().isEmpty() ? null :
                            Double.parseDouble(weightField.getText().trim());
                    Color hairColor =
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
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            loc.get("dialog.error") + ": " + e.getMessage());
                    alert.show();
                    return null;
                }
            }
            return null;
        });
        return dialog.showAndWait();
    }

    private static void updateDialogLanguage(Dialog<StudyGroup> dialog, boolean isAdd) {
        String mode = isAdd ? "add" : "edit";
        dialog.setTitle(loc.get("dialog." + mode + ".title"));
        dialog.setHeaderText(loc.get("dialog." + mode + ".header"));
        nameLabel.setText(loc.get("dialog.name"));
        xLabel.setText(loc.get("dialog.x"));
        yLabel.setText(loc.get("dialog.y"));
        studentsLabel.setText(loc.get("dialog.students"));
        expelledLabel.setText(loc.get("dialog.expelled"));
        formLabel.setText(loc.get("dialog.form"));
        semLabel.setText(loc.get("dialog.semester"));
        adminSectionLabel.setText(loc.get("dialog.admin_section"));
        adminNameLabel.setText(loc.get("dialog.admin_name"));
        weightLabel.setText(loc.get("dialog.weight"));
        eyeColorLabel.setText(loc.get("dialog.eye_color"));
        hairColorLabel.setText(loc.get("dialog.hair_color"));
        nationLabel.setText(loc.get("dialog.nation"));
        locSectionLabel.setText(loc.get("dialog.loc_section"));
        locXLabel.setText(loc.get("dialog.loc_x"));
        locYLabel.setText(loc.get("dialog.loc_y"));
        locZLabel.setText(loc.get("dialog.loc_z"));
        locNameLabel.setText(loc.get("dialog.loc_name"));
        saveBtn = new ButtonType(loc.get("dialog.save"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, new ButtonType(loc.get("dialog.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
    }
}