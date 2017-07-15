/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Javier
 */
public class GLBuffer{
    NativeGLBuffer buffer;
    int target;
    int size;
    int usage;
    int index;
    boolean bound = false;
    
    private GLBuffer() {}
    
    public GLBuffer(int type, int busage){
        buffer = new NativeGLBuffer();
        target = type;
        size = 0;
        usage = busage;
    }
    
    public GLBuffer(int type, int size, int usage){
        this(type, usage);
        alloc(size);
    }
    
    public GLBuffer(int type, FloatBuffer buffer, int usage){
        this(type, usage);
        uploadData(buffer);
    }
    
    public GLBuffer(int type, IntBuffer buffer, int usage){
        this(type, usage);
        uploadData(buffer);
    }
    
    public void bind() {
        buffer.bind(target);
        bound = true;
    }
    
    public void unbind() {
        buffer.unbind(target);
    }

    public void alloc(int size) {
        bind();
        buffer.uploadData(target, size, usage);
        unbind();
    }

    public void uploadData(FloatBuffer data) {
        bind();
        buffer.uploadData(target, data, usage);
        unbind();
    }
    
    public void uploadData(IntBuffer data) {
        bind();
        buffer.uploadData(target, data, usage);
        unbind();
    }

    public void uploadSubData(FloatBuffer data, long offset) {
        bind();
        buffer.uploadSubData(target, offset, data);
        unbind();
    }
    
    public void uploadSubData(IntBuffer data, long offset) {
        bind();
        buffer.uploadSubData(target, offset, data);
        unbind();
    }
    
    public void bindBase(int base){
        buffer.bindBase(target, base);
        index = base;
    }
    
    public int getBase(){
        return index;
    }
    
    public int getSize(){
        return buffer.getSize(target);
    }
    
    public int getTarget(){
        return target;
    }
    
    public int getUsage(){
        return usage;
    }
    
    public void delete(){
        buffer.delete();
    }
}
