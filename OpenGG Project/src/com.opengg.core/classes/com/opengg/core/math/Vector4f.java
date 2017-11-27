/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Javier
 */
public class Vector4f {
    public float x;
    public float y;
    public float z;
    public float w;
    
    public Vector4f(){
        this(0,0,0,0);
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
    
    public float w(){
        return w;
    }
    
    public Vector4f(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4f(Vector4f v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }
    
    public Vector4f(Vector3f v){
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = 1;
    }
    
    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

        return this;
    }
    
    public Vector4f normalize() {
        float invLength = 1.0f / length();
        x *= invLength;
        y *= invLength;
        z *= invLength;
        w *= invLength;

        return this;

    }
    
    public Vector4f add(Vector4f v){
        return new Vector4f(this).addThis(v);
    }
    
    public Vector4f addThis(Vector4f v){
        set(x + v.x, y + v.y, z + v.z, w + v.w);
        return this;
    }
    
    public Vector4f add(Vector4f[] v){
        Vector4f sum = new Vector4f(this);
        for(Vector4f n : v)
             sum.addThis(n);
        return sum;
    }
    
    public Vector4f addThis(Vector4f[] v){
        for(Vector4f n : v)
             this.addThis(n);
        return this;
    }
    
    public Vector4f add(float f){
        return new Vector4f(this).addThis(f);
    }
    
    public Vector4f addThis(float f){
        set(x + f, y + f, z + f, w + f);
        return this;
    }
    
    public Vector4f subtract(Vector4f v){
        return new Vector4f(this).subtractThis(v);
    }
    
    public Vector4f subtractThis(Vector4f v){
        set(x - v.x, y - v.y, z - v.z, w - v.w);
        return this;
    }
    
    public Vector4f subtract(Vector4f[] v){
        Vector4f diff = new Vector4f(this);
        for(Vector4f n : v)
             diff.subtractThis(n);
        return diff;
    }
    
    public Vector4f subtractThis(Vector4f[] v){
        for(Vector4f n : v)
             this.subtractThis(n);
        return this;
    }
    
    public Vector4f subtract(float f){
        return new Vector4f(this).addThis(f);
    }
    
    public Vector4f subtractThis(float f){
        set(x - f, y - f, z - f, w - f);
        return this;
    }
    
    public Vector4f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(1f / scalar);
    }
    
    public Vector4f divideThis(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiplyThis(1f / scalar);
    }
    
    public Vector4f divide(Vector4f vector) {
        return divideThis(vector);
    }
    
    public Vector4f divideThis(Vector4f vector) {
        set(x / vector.x, y / vector.y, z / vector.z, w / vector.w);
        return this;
    }
    
    
    public Vector4f multiply(float scalar) {
        return new Vector4f(this).multiplyThis(scalar);
    }
    
    public Vector4f multiplyThis(float scalar){
        set(x * scalar, y * scalar, z * scalar, w * scalar);
        return this;
    }
    
    public Vector4f multiply(Vector4f v) {
        return new Vector4f(this).multiplyThis(v);
    }
    
    public Vector4f multiplyThis(Vector4f v){
        set(x * v.x, y * v.y, z * v.z, w * v.w);
        return this;
    }
    
    public Vector4f multiply(Matrix4f mat){
        return new Vector4f(this).multiplyThis(mat);
    }
    
    public Vector4f multiplyThis(Matrix4f mat) {
        set(mat.m00 * x + mat.m10 * y + mat.m20 * z + mat.m30 * w,
            mat.m01 * x + mat.m11 * y + mat.m21 * z + mat.m31 * w,
            mat.m02 * x + mat.m12 * y + mat.m22 * z + mat.m32 * w,
            mat.m03 * x + mat.m13 * y + mat.m23 * z + mat.m33 * w);

        return this;
    }
    
    public float distance(Vector4f v) {
        float dx = v.x - x;
        float dy = v.y - y;
        float dz = v.z - z;
        float dw = v.w - w;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }
    
    public float dot(Vector4f v) {
        return x * v.x + y * v.y + z * v.z + w * v.w;
    }
    
     public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public FloatBuffer getBuffer() {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(4);
            buffer.put(x).put(y).put(z).put(w);
            buffer.flip();
            return buffer;
        }
    }
    
    public Vector3f truncate(){
        return new Vector3f(x,y,z);
    }
    
    @Override
    public String toString(){
        return x + ", " + y + ", " + z + ", " + w; 
    }
}
