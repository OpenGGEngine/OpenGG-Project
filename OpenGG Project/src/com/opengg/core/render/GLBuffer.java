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
public class GLBuffer extends GLNativeBuffer{
    int target;
    int size;
    int usage;
    int index;
    boolean bound = false;
    
    private GLBuffer() {super();}
    
    public GLBuffer(int type, int busage){
        super();
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
        bind(target);
        bound = true;
    }
    
    public void unbind() {
        unbind(target);
    }

    public void alloc(int size) {
        bind();
        uploadData(target, size, usage);
        this.size = size;
        unbind();
    }

    public void uploadData(FloatBuffer data) {
        bind();
        uploadData(target, data, usage);
        size = data.limit() * Float.BYTES;
        unbind();
    }
    
    public void uploadData(IntBuffer data) {
        bind();
        uploadData(target, data, usage);
        size = data.limit() * Integer.BYTES;
        unbind();
    }

    public void uploadSubData(FloatBuffer data, long offset) {
        bind();
        uploadSubData(target, offset, data);
        unbind();
    }
    
    public void uploadSubData(IntBuffer data, long offset) {
        bind();
        uploadSubData(target, offset, data);
        unbind();
    }
    
    public void bindBase(int base){
        bindBase(target, base);
        index = base;
    }
    
    public int getBase(){
        return index;
    }
    
    public int getSize(){
        return size;
    }
    
    public int getTarget(){
        return target;
    }
    
    public int getUsage(){
        return usage;
    }
}
