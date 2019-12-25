/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import com.opengg.core.system.Allocator;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 *
 * @author Javier
 */
public class Vector2f implements Serializable{

    public final float x;
    public final float y;

    /**
     * Creates a default 2d vector with all values set to 0.
     */
    public Vector2f() {
        this.x = 0f;
        this.y = 0f;
    }

    /**
     * Creates a vector based off of 2 points.
     *
     * @param x
     * @param y
     */
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(float val) {
        this.x = val;
        this.y = val;
    }
    
    /**
     * Creates a new vector based off another.
     *
     * @param v Vector to be copied
     */
    public Vector2f(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2f x(float x){
        return new Vector2f(x, this.y);
    }
    
    public Vector2f y(float y){
        return new Vector2f(this.x, y);
    }
    
    public Vector2f add(Vector2f v){
        return new Vector2f(x + v.x, y + v.y);
    }

    public Vector2f add(Vector2f[] v){
        Vector2f sum = new Vector2f(this);
        for(Vector2f n : v)
             sum.add(n);
        return sum;
    }
    
    public Vector2f add(float f){
        return new Vector2f(x + f, y + f);
    }

    public Vector2f subtract(Vector2f v){
        return new Vector2f(x - v.x, y - v.y);
    }

    public Vector2f subtract(Vector2f[] v){
        Vector2f diff = new Vector2f(this);
        for(Vector2f n : v)
             diff.subtract(n);
        return diff;
    }
    
    public Vector2f subtract(float f){
        return new Vector2f(x - f, y - f);
    }
    
    public float getDistance(Vector2f v) {
        return (float) Math.sqrt(this.getDistanceSquared(v));
    }
    
    public float getDistanceSquared(Vector2f v){
        return (float) (Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2));
    }
    
    public static float getDistance(Vector2f v1, Vector2f v2){
        return (float) Math.sqrt(Math.pow((v2.x - v1.x), 2)+Math.pow((v2.y - v1.y), 2));  
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public Vector2f inverse() {
        return new Vector2f(this.x * -1, this.y * -1);
    }

    public Vector2f reciprocal(){
        return new Vector2f(1/this.x, 1/this.y);
    }
    
    public Vector2f normalize() {
        return divide(length());
    }

    public float dot(Vector2f v) {
        return x * v.x + y * v.y;
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public Vector2f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(1f / scalar);
    }
    
    public Vector2f divide(Vector2f vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(vector.reciprocal());
    }
    
    public Vector2f multiply(float scalar) {
        return new Vector2f(x * scalar, y * scalar);
    }
    
    public Vector2f multiply(Vector2f v) {
        return new Vector2f(x * v.x, y * v.y);
    }

    public Vector2f abs(){
        float xx = x < 0 ? -x : x;
        float xy = y < 0 ? -y : y;
        return new Vector2f(xx, xy);
    }

    public Vector2f closertoZero(float f){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float xx = (Math.abs(x) - f)*signX;
        float xy = (Math.abs(y) - f)*signY;
        return new Vector2f(xx, xy);
    }
    
    public Vector2f closertoZero(Vector2f v){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float xx = (Math.abs(x) - v.x)*signX;
        float xy = (Math.abs(y) - v.y)*signY;
        return new Vector2f(xx, xy);
    }
    
    public static FloatBuffer listToBuffer(Vector2f... list){
        FloatBuffer f = Allocator.allocFloat(3* list.length);
        for(Vector2f v : list){ 
           f.put(v.x);
           f.put(v.y);
        }
        f.flip();
        return f;
    }

    public static Vector2f lerp(Vector2f sv, Vector2f other, float t ) {
        float x = sv.x + (other.x - sv.x) * t;
        float y = sv.y + (other.y - sv.y) * t;
        return new Vector2f(x, y);
    }

    public Vector2f reflect(Vector2f normal) {
        float dot = this.dot(normal);
        float xx = x - (dot + dot) * normal.x;
        float xy = y - (dot + dot) * normal.y;
        return new Vector2f(xx, xy);
    }

    public FloatBuffer getStackBuffer() {
        FloatBuffer buffer = Allocator.stackAllocFloat(2);
        buffer.put(x).put(y);
        buffer.flip();
        return buffer;
    }
    
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = Allocator.allocFloat(2);
        buffer.put(x).put(y);
        buffer.flip();
        return buffer;
    }
    
    public byte[] toByteArray(){   
        ByteBuffer b = Allocator.alloc(8);
        return b.putFloat(x).putFloat(y).array();
    }

    public static Vector2f parseVector2f(String value){
        String fixed = value.replace('(', ' ');
        fixed = fixed.replace(')', ' ');
        fixed = fixed.trim();
        var split = fixed.split(",");
        return new Vector2f(Float.parseFloat(split[0].trim()), Float.parseFloat(split[1].trim()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2f vector2f = (Vector2f) o;

        if (Float.compare(vector2f.x, x) != 0) return false;
        return Float.compare(vector2f.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @Override
    public String toString(){
        return x + ", " + y;
    }
}
