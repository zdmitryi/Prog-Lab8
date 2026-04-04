package com.example.common.models;


import java.io.Serializable;
import java.util.Objects;


public class Coordinates implements Serializable {

    private double x;

    private long y;
    /** 
     * @return double
     */
    public double getX(){
        return x;
    }
    /** 
     * @return long
     */
    public long getY(){
        return y;
    }
    /** 
     * @param x
     */
    public void setX(double x){
        this.x = x;
    }
    /** 
     * @param y
     */
    public void setY(long y){
        this.y = y;
    }
    public Coordinates(double x, long y){
        if (!(x > 225)) {
            this.x = x;
        } else {
            throw new IllegalArgumentException("X должен быть меньше 225");
        }
        this.y = y;
    }

    public Coordinates(){};
    /** 
     * @return int
     */
    @Override
    public int hashCode(){
        return Objects.hash(this.getX(),this.getY());
    }
    /** 
     * @return String
     */
    @Override
    public String toString(){
        return "Координата по X: " + this.x + "\n" + "Координата по Y " + this.y;
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
        Coordinates c = (Coordinates) o;
        return c.hashCode() == this.hashCode();
    }
}
