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
public class Vector2i implements Serializable{

    public final int x;
    public final int y;

    /**
     * Creates a default 2d vector with all values set to 0.
     */
    public Vector2i() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Creates a vector based off of 2 points.
     *
     * @param x
     * @param y
     */
    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i(int val) {
        this.x = val;
        this.y = val;
    }
    
    /**
     * Creates a new vector based off another.
     *
     * @param v Vector to be copied
     */
    public Vector2i(Vector2i v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2i add(Vector2i v){
        return new Vector2i(x + v.x, y + v.y);
    }
    
    public Vector2i add(Vector2i[] v){
        Vector2i sum = new Vector2i(this);
        for(Vector2i n : v)
             sum.add(n);
        return sum;
    }
    
    public Vector2i add(int f){
        return new Vector2i(x + f, y + f);
    }

    public Vector2i subtract(Vector2i v){
        return new Vector2i(x - v.x, y - v.y);
    }

    public Vector2i subtract(Vector2i[] v){
        Vector2i diff = new Vector2i(this);
        for(Vector2i n : v)
             diff.subtract(n);
        return diff;
    }
    
    public Vector2i subtract(int f){
        return new Vector2i(x - f, y - f);
    }
    
    public int getDistance(Vector2i v) {
        return (int) Math.sqrt(this.getDistanceSquared(v));
    }
    
    public int getDistanceSquared(Vector2i v){
        return (int) (Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2));
    }
    
    public static int getDistance(Vector2i v1, Vector2i v2){
        return (int) Math.sqrt(Math.pow((v2.x - v1.x), 2)+Math.pow((v2.y - v1.y), 2));  
    }
    
    public int length() {
        return (int) Math.sqrt(lengthSquared());
    }
    
    public Vector2i inverse() {
        return new Vector2i(this.x * -1, this.y * -1);
    }

    public Vector2i reciprocal(){
        return new Vector2i(1/this.x, 1/this.y);
    }

    public Vector2i normalize() {
        return divide(length());
    }

    public int dot(Vector2i v) {
        return x * v.x + y * v.y;
    }

    public int lengthSquared() {
        return x * x + y * y;
    }

    public Vector2i divide(int scalar) {
        
        return multiply(1 / scalar);
    }
    
    public Vector2i divide(Vector2i vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(vector.inverse());
    } 
    
    public Vector2i multiply(int scalar) {
        return new Vector2i(x * scalar, y * scalar);
    }
    
    public Vector2i multiply(Vector2i v) {
        return new Vector2i(x * v.x, y * v.y);
    }

    public Vector2i abs(){
        int xx = x < 0 ? -x : x;
        int xy = y < 0 ? -y : y;
        return new Vector2i(xx, xy);
    }

    public Vector2i closertoZero(int f){
        int signX = x < 0 ? -1 : 1;
        int signY = y < 0 ? -1 : 1;
        int xx = (Math.abs(x) - f)*signX;
        int xy = (Math.abs(y) - f)*signY;
        return new Vector2i(xx, xy);
    }
    
    public Vector2i closertoZero(Vector2i v){
        int signX = x < 0 ? -1 : 1;
        int signY = y < 0 ? -1 : 1;
        int xx = (Math.abs(x) - v.x)*signX;
        int xy = (Math.abs(y) - v.y)*signY;
        return new Vector2i(xx, xy);
    }
    
    public static FloatBuffer listToBuffer(Vector2i... list){
        FloatBuffer f = Allocator.allocFloat(3* list.length);
        for(Vector2i v : list){ 
           f.put(v.x);
           f.put(v.y);
        }
        f.flip();
        return f;
    }
    
    public static Vector2i lerp(Vector2i sv, Vector2i other, int t ) {
        int x = sv.x + (other.x - sv.x) * t;
        int y = sv.y + (other.y - sv.y) * t;
        return new Vector2i(x,y);
    }

    public Vector2i reflect(Vector2i normal) {
        int dot = this.dot(normal);
        int xx = x - (dot + dot) * normal.x;
        int xy = y - (dot + dot) * normal.y;
        return new Vector2i(xx, xy);
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
    
    @Override
    public boolean equals(Object ot){
        if(ot instanceof Vector2i){
            Vector2i v = (Vector2i)ot;
            return FastMath.isEqual(v.x, x) && FastMath.isEqual(v.y, y);
        }   
        return false;
    }
    
    @Override
    public String toString(){
        return x + ", " + y;
    }
}
