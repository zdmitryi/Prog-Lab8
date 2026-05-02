package com.example;

import com.example.models.*;
import javafx.beans.property.*;

public class StudyGroupDto {

    private final IntegerProperty id;
    private final StringProperty name;
    private final DoubleProperty coordX;
    private final LongProperty coordY;
    private final StringProperty creationDate;
    private final LongProperty studentsCount;
    private final IntegerProperty shouldBeExpelled;
    private final StringProperty formOfEducation;
    private final StringProperty semester;
    private final StringProperty adminName;
    private final DoubleProperty adminWeight;
    private final StringProperty adminEyeColor;
    private final StringProperty adminHairColor;
    private final StringProperty adminNationality;
    private final LongProperty adminLocX;
    private final LongProperty adminLocY;
    private final LongProperty adminLocZ;
    private final StringProperty adminLocName;
    private final IntegerProperty ownerId;

    private final StudyGroup original;

    public StudyGroupDto(StudyGroup group) {
        this.original = group;

        this.id = new SimpleIntegerProperty(group.getId());
        this.name = new SimpleStringProperty(group.getName());
        this.coordX = new SimpleDoubleProperty(group.getCoordinates().getX());
        this.coordY = new SimpleLongProperty(group.getCoordinates().getY());
        this.creationDate = new SimpleStringProperty(group.getCreationDate().toString());
        this.studentsCount = new SimpleLongProperty(group.getStudentsCount());
        this.shouldBeExpelled = new SimpleIntegerProperty(group.getShouldBeExpelled());
        this.formOfEducation = new SimpleStringProperty(group.getFormOfEducation().name());
        this.semester = new SimpleStringProperty(group.getSemester().name());

        Person admin = group.getGroupAdmin();
        this.adminName = new SimpleStringProperty(admin.getName());
        this.adminWeight = new SimpleDoubleProperty(admin.getWeight() != null ? admin.getWeight() : 0);
        this.adminEyeColor = new SimpleStringProperty(admin.getEyeColor() != null ? admin.getEyeColor().name() : "-");
        this.adminHairColor = new SimpleStringProperty(admin.getHairColor().name());
        this.adminNationality = new SimpleStringProperty(admin.getNationality().name());

        Location loc = admin.getLocation();
        this.adminLocX = new SimpleLongProperty(loc.getX());
        this.adminLocY = new SimpleLongProperty(loc.getY());
        this.adminLocZ = new SimpleLongProperty(loc.getZ());
        this.adminLocName = new SimpleStringProperty(loc.getName() != null ? loc.getName() : "-");

        this.ownerId = new SimpleIntegerProperty(group.getOwnerId());
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public double getCoordX() { return coordX.get(); }
    public DoubleProperty coordXProperty() { return coordX; }

    public long getCoordY() { return coordY.get(); }
    public LongProperty coordYProperty() { return coordY; }

    public String getCreationDate() { return creationDate.get(); }
    public StringProperty creationDateProperty() { return creationDate; }

    public long getStudentsCount() { return studentsCount.get(); }
    public LongProperty studentsCountProperty() { return studentsCount; }

    public int getShouldBeExpelled() { return shouldBeExpelled.get(); }
    public IntegerProperty shouldBeExpelledProperty() { return shouldBeExpelled; }

    public String getFormOfEducation() { return formOfEducation.get(); }
    public StringProperty formOfEducationProperty() { return formOfEducation; }

    public String getSemester() { return semester.get(); }
    public StringProperty semesterProperty() { return semester; }

    public String getAdminName() { return adminName.get(); }
    public StringProperty adminNameProperty() { return adminName; }

    public double getAdminWeight() { return adminWeight.get(); }
    public DoubleProperty adminWeightProperty() { return adminWeight; }

    public String getAdminEyeColor() { return adminEyeColor.get(); }
    public StringProperty adminEyeColorProperty() { return adminEyeColor; }

    public String getAdminHairColor() { return adminHairColor.get(); }
    public StringProperty adminHairColorProperty() { return adminHairColor; }

    public String getAdminNationality() { return adminNationality.get(); }
    public StringProperty adminNationalityProperty() { return adminNationality; }

    public long getAdminLocX() { return adminLocX.get(); }
    public LongProperty adminLocXProperty() { return adminLocX; }

    public long getAdminLocY() { return adminLocY.get(); }
    public LongProperty adminLocYProperty() { return adminLocY; }

    public long getAdminLocZ() { return adminLocZ.get(); }
    public LongProperty adminLocZProperty() { return adminLocZ; }

    public String getAdminLocName() { return adminLocName.get(); }
    public StringProperty adminLocNameProperty() { return adminLocName; }

    public int getOwnerId() { return ownerId.get(); }
    public IntegerProperty ownerIdProperty() { return ownerId; }

    public StudyGroup getOriginal() { return original; }
}