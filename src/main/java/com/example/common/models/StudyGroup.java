package com.example.common.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;



import com.example.common.enums.FormOfEducation;
import com.example.common.enums.Semester;

/**
 * Класс, представляющий учебную группу
 * Хранит информацию о группе, её координатах, старосте и других параметрах
 * Реализует сортировку по умолчанию по количеству студентов
 * 
 * @author Zvonkov Dmitrii
 * @see Coordinates
 * @see Person
 */


public class StudyGroup implements Comparable<StudyGroup>, Serializable{
    private static final long serialVersionUID = 4349558527574112727L;
    private int id;
    private static int nextID;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private long studentsCount;
    private Integer shouldBeExpelled;
    private FormOfEducation formOfEducation;
    private Semester semesterEnum;
    private Person groupAdmin;

    private Integer ownerId = null;


    public void setOwnerId(int id){
        this.ownerId = id;
    }

    public int getOwnerId(){
        return this.ownerId;
    }


    public static void setInitialNextId(Set<StudyGroup> set){
        StudyGroup.nextID = set.size() + 1;
    }

    /** 
     * @return int
     */
    public static int getNextId(){
        return StudyGroup.nextID;
    }




    public void setCreationDate(LocalDate d){
        this.creationDate = d;
    }

    /** 
     * @param id
     */
    public static void setNextId(int id){
        StudyGroup.nextID = id;
    }
    
    /** 
     * @return int
     */
    public int getId(){
        return this.id;
    }

    /** 
     * @return String
     */
    public String getName(){
        return this.name;
    }

    /** 
     * @return Coordinates
     */
    public Coordinates getCoordinates(){
        return this.coordinates;
    }

    /** 
     * @return LocalDate
     */
    public LocalDate getCreationDate(){
        return this.creationDate;
    }

    /** 
     * @return long
     */
    public long getStudentsCount(){
        return this.studentsCount;
    }

    /** 
     * @return Integer
     */
    public Integer getShouldBeExpelled(){
        return this.shouldBeExpelled;
    }

    /** 
     * @return FormOfEducation
     */
    public FormOfEducation getFormOfEducation(){
        return this.formOfEducation;
    }

    /** 
     * @return Semester
     */
    public Semester getSemester(){
        return this.semesterEnum;
    }

    /** 
     * @return Person
     */
    public Person getGroupAdmin(){
        return this.groupAdmin;
    }

    /** 
     * @param id
     */
    public void setId(int id){
        this.id = id;
    }

    /** 
     * @param s
     * @return int
     */
    @Override
    public int compareTo(StudyGroup s){
        return this.getName().compareToIgnoreCase(s.getName());
    }

    /** 
     * @return String
     */
    @Override
    public String toString(){
        return "ID группы: " + this.id + "\n" + "Название группы: " + this.name + "\n" +  "Координаты группы: "+ "\n" + this.coordinates + "\n" + "Количество студентов в группе: " + this.studentsCount + "\n" + "Количество студентов на отчисление: " + this.shouldBeExpelled + "\n" + "Дата создания группы: " + this.creationDate + "\n" + "Форма обучения: " + this.formOfEducation + "\n" + "Семестр: " + this.semesterEnum + "\n" + "Староста группы: " + "\n" + this.getGroupAdmin() + "\n" + "ID владельца группы:" + this.ownerId + "\n";
    }

    public StudyGroup(){};

    public StudyGroup(String name, Coordinates coordinates, long studentsCount, Integer shouldBeExpelled, FormOfEducation formOfEducation, Semester semesterEnum, Person groupAdmin){
        if (!(name == null || name.isBlank())) {
            this.name = name;
        } else throw new IllegalArgumentException("Имя не может быть пустым");
        if (!(coordinates == null)){
            this.coordinates = coordinates;
        } else throw new IllegalArgumentException("Координаты не могут быть пустыми");
        this.creationDate = LocalDate.now();
        if (studentsCount > 0){
            this.studentsCount = studentsCount;
        } else throw new IllegalArgumentException("Количество студентов должно быть положительным");
        if (!(shouldBeExpelled < 0 || shouldBeExpelled == null)){
            this.shouldBeExpelled = shouldBeExpelled;
        } else throw new IllegalArgumentException("Количество студентов, которых должны отчислить, должно быть положительным");
        if (!(formOfEducation == null)){
            this.formOfEducation = formOfEducation;
        } else throw new IllegalArgumentException("Форма образования не должна быть пустой");
        if (!(semesterEnum == null)){
            this.semesterEnum = semesterEnum;
        } else throw new IllegalArgumentException("Семестр не может быть пустой");
        if (!(groupAdmin == null)){
            this.groupAdmin = groupAdmin;
        } else throw new IllegalArgumentException("Админ не может быть пустым");
    }

    /** 
     * @return int
     */
    @Override
    public int hashCode(){
        return Objects.hash(this.getShouldBeExpelled(), this.getId(), this.getCoordinates(), this.getCreationDate(), this.getFormOfEducation(), this.getGroupAdmin(), this.getName(), this.getSemester(), this.getStudentsCount());
    }

    /** 
     * @param o
     * @return boolean
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        StudyGroup c = (StudyGroup) o;
        return c.hashCode() == this.hashCode();
    }
}



