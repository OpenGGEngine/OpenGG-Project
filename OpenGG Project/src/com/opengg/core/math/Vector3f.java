/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * 3 component immutable vector with linear algebra functions
 * @author Javier
 */
@Immutable
public class Vector3f implements Serializable{
    private float x;
    private float y;
    private float z;

    /**
     * Creates a default 3d vector with all values set to 0.
     */
    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }

    /**
     * Creates a vector based off of 3 points.
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(float val) {
        this.x = val;
        this.y = val;
        this.z = val;
    }
    
    /**
     * Creates a new vector based off another.
     *
     * @param v Vector to be copied
     */
    public Vector3f(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public Vector3f(Vector3fm v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public Vector3f(Vector4f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public float x(){
        return x;
    }
    
    public float y(){
        return y;
    }
    
    public float z(){
        return z;
    }
    
    public Vector3f setX(float nx){
        return new Vector3f(nx,y,z);
    }
    
    public Vector3f setY(float ny){
        return new Vector3f(x,ny,z);
    }
    
    public Vector3f setZ(float nz){
        return new Vector3f(x,y,nz);
    }
    
    public Vector3f add(Vector3f v){
        return new Vector3f(this).addThis(v);
    }
    
    private Vector3f addThis(Vector3f v){
        set(x + v.x, y + v.y, z + v.z);
        return this;
    }
    
    public Vector3f add(Vector3f[] v){
        Vector3f sum = new Vector3f(this);
        for(Vector3f n : v)
             sum.addThis(n);
        return sum;
    }
    
    private Vector3f addThis(Vector3f[] v){
        for(Vector3f n : v)
             this.addThis(n);
        return this;
    }
    
    public Vector3f add(float f){
        return new Vector3f(this).addThis(f);
    }
    
    private Vector3f addThis(float f){
        set(x + f, y + f, z + f);
        return this;
    }
    
    public Vector3f subtract(Vector3f v){
        return new Vector3f(this).subtractThis(v);
    }
    
    private Vector3f subtractThis(Vector3f v){
        set(x - v.x, y - v.y, z - v.z);
        return this;
    }
    
    public Vector3f subtract(Vector3f[] v){
        Vector3f diff = new Vector3f(this);
        for(Vector3f n : v)
             diff.subtractThis(n);
        return diff;
    }
    
    public Vector3f subtract(float f){
        return new Vector3f(this).addThis(f);
    }
    
    private Vector3f subtractThis(float f){
        set(x - f, y - f, z - f);
        return this;
    }
    
    public Vector3f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(1f / scalar);
    }
    
    private Vector3f divideThis(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiplyThis(1f / scalar);
    }
    
    public Vector3f divide(Vector3f vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return divideThis(vector);
    }
    
    private Vector3f divideThis(Vector3f vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        set(x / vector.x, y / vector.y, z / vector.z);
        return this;
    }
    
    public Vector3f multiply(float scalar) {
        return new Vector3f(this).multiplyThis(scalar);
    }
    
    private Vector3f multiplyThis(float scalar){
        set(x * scalar, y * scalar, z * scalar);
        return this;
    }
    
    public Vector3f multiply(Vector3f v) {
        return new Vector3f(this).multiplyThis(v);
    }
    
    private Vector3f multiplyThis(Vector3f v){
        set(x * v.x, y * v.y, z * v.z);
        return this;
    }
    
    public float getDistance(Vector3f v) {
        return (float) Math.sqrt(this.getDistanceSquared(v));
    }
    
    public float getDistanceSquared(Vector3f v){
        return (float) (Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2) + Math.pow((this.z - v.z), 2));
    }
    
    public static float getDistance(Vector3f v1, Vector3f v2){
        return (float) Math.sqrt(Math.pow((v2.x - v1.x), 2)+Math.pow((v2.y - v1.y), 2)+Math.pow((v2.z - v1.z), 2));  
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public Vector3f inverse() {
        return new Vector3f(this).invertThis();
    }
    
    private Vector3f invertThis() {
        set(this.x * -1, this.y * -1, this.z * -1);
        return this;
    }

    public Vector3f reciprocal(){
        return new Vector3f(this).reciprocateThis();
    }
    
    private Vector3f reciprocateThis(){
         set(1/this.x, 1/this.y, 1/this.z);
         return this;
    }
    
    public Vector3f normalize() {
        return divide(length());
    }

    public float dot(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public Vector3f cross(Vector3f v) {
        return new Vector3f(y * v.z - z * v.y,
                   z * v.x - x * v.z,
                   x * v.y - y * v.x);
    }
    
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }
 
    public Vector3f abs(){
        return new Vector3f(this.absThis());
    }
    
    private Vector3f absThis(){
        if(x <= 0) x = -x;
        if(y <= 0) y = -y;
        if(z <= 0) z = -z;
        return this;
    }
    
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3f transformByQuat(Quaternionf q){
        return new Vector3f(this).transformThisByQuat(q);
    }
    
    private Vector3f transformThisByQuat(Quaternionf q){
        q.transform(this);
        return this;
    }

    public Vector3f closerToZero(float f){
        return new Vector3f().closerToZeroThis(f);
    }
    
    private Vector3f closerToZeroThis(float f){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float signZ = z < 0 ? -1 : 1;
        this.x = (Math.abs(x) - f)*signX;
        this.y = (Math.abs(y) - f)*signY;
        this.z = (Math.abs(z) - f)*signZ;
        return this;
    }
    
    public Vector3f closerToZero(Vector3f v){
        return new Vector3f().closerToZeroThis(v);
    }
    
    private Vector3f closerToZeroThis(Vector3f v){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float signZ = z < 0 ? -1 : 1;
        this.x = (Math.abs(x) - v.x)*signX;
        this.y = (Math.abs(y) - v.y)*signY;
        this.z = (Math.abs(z) - v.z)*signZ;
        return this;
    }
    
    public static FloatBuffer listToBuffer(Vector3f... list){
        FloatBuffer f = MemoryUtil.memAllocFloat(3* list.length);
        for(Vector3f v : list){ 
           f.put(v.x);
           f.put(v.y);
           f.put(v.z);
        }
        f.flip();
        return f;
    }
    
    public static Vector3f lerp(Vector3f sv, Vector3f other, float t ){
        return new Vector3f().lerpThis(sv, other, t);
    }
    
    private Vector3f lerpThis(Vector3f sv, Vector3f other, float t ) {
        x = sv.x + (other.x - sv.x) * t;
        y = sv.y + (other.y - sv.y) * t;
        z = sv.z + (other.z - sv.z) * t;
        return this;
    }

    public Vector3f reflect(Vector3f normal){
        return new Vector3f(this).reflectThis(normal);
    }
    
    private Vector3f reflectThis(Vector3f normal) {
        float dot = this.dot(normal);
        x = x - (dot + dot) * normal.x;
        y = y - (dot + dot) * normal.y;
        z = z - (dot + dot) * normal.z;
        return this;
    }
    
    public FloatBuffer getBuffer() {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(3);
            buffer.put(x).put(y).put(z);
            buffer.flip();
            return buffer;
        }
    }
    
    public byte[] toByteArray(){   
        ByteBuffer b = MemoryUtil.memAlloc(12);
        return b.putFloat(x).putFloat(y).putFloat(z).array();
    }
    
    @Override
    public boolean equals(Object ot){
        if(ot instanceof Vector3f){
            Vector3f v = (Vector3f)ot;
            return FastMath.isEqual(v.x, x) && FastMath.isEqual(v.y, y)  && FastMath.isEqual(v.z, z);
        }   
        return false;
    }
    
    @Override
    public String toString(){
        return x + ", " + y + ", " + z;
    }
}
