/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class Vector2i implements Serializable{

    public int x;
    public int y;

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
        return new Vector2i(this).addThis(v);
    }
    
    public Vector2i addThis(Vector2i v){
        set(x + v.x, y + v.y);
        return this;
    }
    
    public Vector2i add(Vector2i[] v){
        Vector2i sum = new Vector2i(this);
        for(Vector2i n : v)
             sum.addThis(n);
        return sum;
    }
    
    public Vector2i addThis(Vector2i[] v){
        for(Vector2i n : v)
             this.addThis(n);
        return this;
    }
    
    public Vector2i add(int f){
        return new Vector2i(this).addThis(f);
    }
    
    public Vector2i addThis(int f){
        set(x + f, y + f);
        return this;
    }
    
    public Vector2i subtract(Vector2i v){
        return new Vector2i(this).subtractThis(v);
    }
    
    public Vector2i subtractThis(Vector2i v){
        set(x - v.x, y - v.y);
        return this;
    }
    
    public Vector2i subtract(Vector2i[] v){
        Vector2i diff = new Vector2i(this);
        for(Vector2i n : v)
             diff.subtractThis(n);
        return diff;
    }
    
    public Vector2i subtractThis(Vector2i[] v){
        for(Vector2i n : v)
             this.subtractThis(n);
        return this;
    }
    
    public Vector2i subtract(int f){
        return new Vector2i(this).addThis(f);
    }
    
    public Vector2i subtractThis(int f){
        set(x - f, y - f);
        return this;
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
        return new Vector2i(this).invertThis();
    }
    
    public Vector2i invertThis() {
        set(this.x * -1, this.y * -1);
        return this;
    }

    public Vector2i reciprocal(){
        return new Vector2i(this).reciprocateThis();
    }
    
    public Vector2i reciprocateThis(){
         set(1/this.x, 1/this.y);
         return this;
    }
    
    public Vector2i normalize() {
        return divide(length());
    }
    
    public Vector2i normalizeThis() {
        return divideThis(length());
    }

    public int dot(Vector2i v) {
        return x * v.x + y * v.y;
    }

    public int lengthSquared() {
        return x * x + y * y;
    }

    public Vector2i divide(int scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(1 / scalar);
    }
    
    public Vector2i divideThis(int scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiplyThis(1 / scalar);
    }
    
    public Vector2i divide(Vector2i vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return divideThis(vector);
    }
    
    public Vector2i divideThis(Vector2i vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        set(x / vector.x, y / vector.y);
        return this;
    }
    
    
    public Vector2i multiply(int scalar) {
        return new Vector2i(this).multiplyThis(scalar);
    }
    
    public Vector2i multiplyThis(int scalar){
        set(x * scalar, y * scalar);
        return this;
    }
    
    public Vector2i multiply(Vector2i v) {
        return new Vector2i(this).multiplyThis(v);
    }
    
    public Vector2i multiplyThis(Vector2i v){
        set(x * v.x, y * v.y);
        return this;
    }
 
    public Vector2i abs(){
        return new Vector2i(this.absThis());
    }
    
    public Vector2i absThis(){
        if(x <= 0) x = -x;
        if(y <= 0) y = -y;
        return this;
    }
    
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i closertoZero(int f){
        int signX = x < 0 ? -1 : 1;
        int signY = y < 0 ? -1 : 1;
        this.x = (Math.abs(x) - f)*signX;
        this.y = (Math.abs(y) - f)*signY;
        return this;
    }
    
    public Vector2i closertoZero(Vector2i v){
        int signX = x < 0 ? -1 : 1;
        int signY = y < 0 ? -1 : 1;
        this.x = (Math.abs(x) - v.x)*signX;
        this.y = (Math.abs(y) - v.y)*signY;
        return this;
    }
    
    public static FloatBuffer listToBuffer(Vector2i... list){
        FloatBuffer f = MemoryUtil.memAllocFloat(3* list.length);
        for(Vector2i v : list){ 
           f.put(v.x);
           f.put(v.y);
        }
        f.flip();
        return f;
    }
    
    public static Vector2i lerp(Vector2i sv, Vector2i other, int t ){
        return new Vector2i().lerpThis(sv, other, t);
    }
    
    public Vector2i lerpThis(Vector2i sv, Vector2i other, int t ) {
        x = sv.x + (other.x - sv.x) * t;
        y = sv.y + (other.y - sv.y) * t;
        return this;
    }

    public Vector2i reflect(Vector2i normal) {
        int dot = this.dot(normal);
        x = x - (dot + dot) * normal.x;
        y = y - (dot + dot) * normal.y;
        return this;
    }

    public FloatBuffer getBuffer() {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(2);
            buffer.put(x).put(y);
            buffer.flip();
            return buffer;
        }
    }
    
    public byte[] toByteArray(){   
        ByteBuffer b = MemoryUtil.memAlloc(8);
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
