/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import com.opengg.core.render.GraphicsBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Javier
 */
public class OpenGLBuffer implements GraphicsBuffer{
    NativeOpenGLBuffer buffer;
    int target;
    int size;
    int usage;
    int index;
    boolean bound = false;

    public OpenGLBuffer(int type, int busage){
        buffer = new NativeOpenGLBuffer();
        target = type;
        size = 0;
        usage = busage;
    }
    
    public OpenGLBuffer(int type, int size, int usage){
        this(type, usage);
        alloc(size);
    }
    
    public OpenGLBuffer(int type, FloatBuffer buffer, int usage){
        this(type, usage);
        uploadData(buffer);
    }
    
    public OpenGLBuffer(int type, IntBuffer buffer, int usage){
        this(type, usage);
        uploadData(buffer);
    }
    
    @Override
    public void bind() {
        buffer.bind(target);
        bound = true;
    }
    
    @Override
    public void unbind() {
        buffer.unbind(target);
    }

    @Override
    public void alloc(int size) {
        bind();
        buffer.uploadData(target, size, usage);
        unbind();
    }

    @Override
    public void uploadData(FloatBuffer data) {
        bind();
        buffer.uploadData(target, data, usage);
        unbind();
    }
    
    @Override
    public void uploadData(IntBuffer data) {
        bind();
        buffer.uploadData(target, data, usage);
        unbind();
    }

    @Override
    public void uploadSubData(FloatBuffer data, long offset) {
        bind();
        buffer.uploadSubData(target, offset, data);
        unbind();
    }
    
    @Override
    public void uploadSubData(IntBuffer data, long offset) {
        bind();
        buffer.uploadSubData(target, offset, data);
        unbind();
    }
    
    @Override
    public void bindBase(int base){
        buffer.bindBase(target, base);
        index = base;
    }
    
    @Override
    public int getBase(){
        return index;
    }
    
    @Override
    public int getSize(){
        return buffer.getSize(target);
    }
    
    @Override
    public int getTarget(){
        return target;
    }
    
    @Override
    public int getUsage(){
        return usage;
    }
    
    @Override
    public void delete(){
        buffer.delete();
    }
}
