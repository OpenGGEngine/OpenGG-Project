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
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Javier
 */
public class Vector2f implements Serializable{

    public float x;
    public float y;

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

    public Vector2f add(Vector2f v){
        return new Vector2f(this).addThis(v);
    }
    
    public Vector2f addThis(Vector2f v){
        set(x + v.x, y + v.y);
        return this;
    }
    
    public Vector2f add(Vector2f[] v){
        Vector2f sum = new Vector2f(this);
        for(Vector2f n : v)
             sum.addThis(n);
        return sum;
    }
    
    public Vector2f addThis(Vector2f[] v){
        for(Vector2f n : v)
             this.addThis(n);
        return this;
    }
    
    public Vector2f add(float f){
        return new Vector2f(this).addThis(f);
    }
    
    public Vector2f addThis(float f){
        set(x + f, y + f);
        return this;
    }
    
    public Vector2f subtract(Vector2f v){
        return new Vector2f(this).subtractThis(v);
    }
    
    public Vector2f subtractThis(Vector2f v){
        set(x - v.x, y - v.y);
        return this;
    }
    
    public Vector2f subtract(Vector2f[] v){
        Vector2f diff = new Vector2f(this);
        for(Vector2f n : v)
             diff.subtractThis(n);
        return diff;
    }
    
    public Vector2f subtractThis(Vector2f[] v){
        for(Vector2f n : v)
             this.subtractThis(n);
        return this;
    }
    
    public Vector2f subtract(float f){
        return new Vector2f(this).addThis(f);
    }
    
    public Vector2f subtractThis(float f){
        set(x - f, y - f);
        return this;
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
        return new Vector2f(this).invertThis();
    }
    
    public Vector2f invertThis() {
        set(this.x * -1, this.y * -1);
        return this;
    }

    public Vector2f reciprocal(){
        return new Vector2f(this).reciprocateThis();
    }
    
    public Vector2f reciprocateThis(){
         set(1/this.x, 1/this.y);
         return this;
    }
    
    public Vector2f normalize() {
        return divide(length());
    }
    
    public Vector2f normalizeThis() {
        return divideThis(length());
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
    
    public Vector2f divideThis(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiplyThis(1f / scalar);
    }
    
    public Vector2f divide(Vector2f vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return divideThis(vector);
    }
    
    public Vector2f divideThis(Vector2f vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        set(x / vector.x, y / vector.y);
        return this;
    }
    
    
    public Vector2f multiply(float scalar) {
        return new Vector2f(this).multiplyThis(scalar);
    }
    
    public Vector2f multiplyThis(float scalar){
        set(x * scalar, y * scalar);
        return this;
    }
    
    public Vector2f multiply(Vector2f v) {
        return new Vector2f(this).multiplyThis(v);
    }
    
    public Vector2f multiplyThis(Vector2f v){
        set(x * v.x, y * v.y);
        return this;
    }
 
    public Vector2f abs(){
        return new Vector2f(this.absThis());
    }
    
    public Vector2f absThis(){
        if(x <= 0) x = -x;
        if(y <= 0) y = -y;
        return this;
    }
    
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f closertoZero(float f){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        this.x = (Math.abs(x) - f)*signX;
        this.y = (Math.abs(y) - f)*signY;
        return this;
    }
    
    public Vector2f closertoZero(Vector2f v){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        this.x = (Math.abs(x) - v.x)*signX;
        this.y = (Math.abs(y) - v.y)*signY;
        return this;
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
    
    public static Vector2f lerp(Vector2f sv, Vector2f other, float t ){
        return new Vector2f().lerpThis(sv, other, t);
    }
    
    public Vector2f lerpThis(Vector2f sv, Vector2f other, float t ) {
        x = sv.x + (other.x - sv.x) * t;
        y = sv.y + (other.y - sv.y) * t;
        return this;
    }

    public Vector2f reflect(Vector2f normal) {
        float dot = this.dot(normal);
        x = x - (dot + dot) * normal.x;
        y = y - (dot + dot) * normal.y;
        return this;
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
        if(ot instanceof Vector2f){
            Vector2f v = (Vector2f)ot;
            return FastMath.isEqual(v.x, x) && FastMath.isEqual(v.y, y);
        }   
        return false;
    }
    
    @Override
    public String toString(){
        return x + ", " + y;
    }
}
