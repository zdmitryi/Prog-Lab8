package com.example.common.models;

import java.io.Serializable;
import java.util.Objects;



import com.example.common.enums.Color;
import com.example.common.enums.Country;
public class Person implements Serializable {
    private String name;
    private Double weight;
    private Color eyeColor;
    private Color hairColor;
    private Country nationality;
    private Location location;
    /** 
     * @return String
     */
    public String getName(){
        return this.name;
    }
    /** 
     * @return Double
     */
    public Double getWeight(){
        return this.weight;
    }
    /** 
     * @return Color
     */
    public Color getEyeColor(){
        return this.eyeColor;
    }
    /** 
     * @return Color
     */
    public Color getHairColor(){
        return this.hairColor;
    }
    /** 
     * @return Country
     */
    public Country getNationality(){
        return this.nationality;         
    }
    /** 
     * @return Location
     */
    public Location getLocation(){
        return this.location;
    }
    /** 
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }
    /** 
     * @param weight
     */
    public void setWeight(Double weight){
        this.weight = weight;
    }
    /** 
     * @param eyeColor
     */
    public void setEyeColor(Color eyeColor){
        this.eyeColor = eyeColor;
    }
    /** 
     * @param hairColor
     */
    public void setHairColor(Color hairColor){
        this.hairColor = hairColor;
    }

    public Person(){};

    public Person (String name, Double weight, Color eyeColor, Color hairColor, Country nationality, Location location){
        if (!(name == null || name.isBlank())) {
            this.name = name;
        } else throw new IllegalArgumentException("Имя не может быть пустым");
        if (weight != null && weight <= 0) {
        throw new IllegalArgumentException("Вес должен быть > 0 или null");
        }
        this.weight = weight;
        this.eyeColor = eyeColor;
        if (!(hairColor == null)) {
            this.hairColor = hairColor;
        } else throw new IllegalArgumentException("Цвет волос не может быть пустым");
        if (!(nationality == null)) {
            this.nationality = nationality;
        } else throw new IllegalArgumentException("Национальность не может быть пустой");
        if (!(location == null)) {
            this.location = location;
        } else throw new IllegalArgumentException("Местоположение не может быть пустым");
    }

    /** 
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getWeight(), this.getEyeColor(), this.getHairColor(), this.getNationality(), this.getLocation());
    }

    /** 
     * @return String
     */
    @Override
    public String toString(){
        return "Имя: " + this.name + "\n" + "Вес: " + this.weight + "\n" + "Цвет глаз: " + this.eyeColor + "\n" + "Цвет волос: " + this.hairColor + "\n" + "Национальность: " + this.nationality + "\n" + "Локация: " + "\n" + this.location + "\n";
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
        Person c = (Person) o;
        return c.hashCode() == this.hashCode();
    }
}
