package com.example.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий локацию для персоны
 * Хранит информацию о местоположении по X, Y, Z и наименовании локации
 * 
 * @author Zvonkov Dmitrii
 */
public class Location implements Serializable{
    private long x;
    private long y;
    private long z;
    private String name;
    /** 
     * @return long
     */
    public long getX(){
        return this.x;
    }
    /** 
     * @return long
     */
    public long getY(){
        return this.y;
    }
    /** 
     * @return long
     */
    public long getZ(){
        return this.z;
    }
    /** 
     * @return String
     */
    public String getName(){
        return this.name;
    }
    /** 
     * @param x
     */
    public void setX(long x){
        this.x = x;
    }   
    /** 
     * @param y
     */
    public void setY(long y){
        this.y = y;
    }
    /** 
     * @param z
     */
    public void setZ(long z){
        this.z = z;
    }
    /** 
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }
    public Location(long x, long y, long z, String name){
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Location(){};

    /** 
     * @return String
     */
    @Override
    public String toString(){
        return "Название локации: " + this.getName() + "\n" + "Координата по X: " + this.x + "\n" + "Координата по Y: " + this.y + "\n" + "Координата по Z: " + this.z + "\n";
    }

    /** 
     * @return int
     */
    @Override
    public int hashCode(){
        return Objects.hash(this.getX(),this.getY(),this.getZ()) + this.getName().hashCode();
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
        Location c = (Location) o;
        return c.hashCode() == this.hashCode();
    }
}
