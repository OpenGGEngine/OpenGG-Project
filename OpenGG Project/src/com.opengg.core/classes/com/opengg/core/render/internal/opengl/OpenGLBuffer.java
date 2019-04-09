/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import com.opengg.core.render.GraphicsBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL42.GL_ATOMIC_COUNTER_BUFFER;
import static org.lwjgl.opengl.GL43.GL_DISPATCH_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.GL_QUERY_BUFFER;

/**
 *
 * @author Javier
 */
public class OpenGLBuffer implements GraphicsBuffer{
    NativeOpenGLBuffer buffer;
    BufferType target;
    int size;
    UsageType usage;
    int index;
    boolean bound = false;

    public OpenGLBuffer(BufferType type, UsageType usage){
        this.buffer = new NativeOpenGLBuffer();
        this.target = type;
        this.size = 0;
        this.usage = usage;
    }
    
    public OpenGLBuffer(BufferType type, int size, UsageType usage){
        this(type, usage);
        alloc(size);
    }
    
    public OpenGLBuffer(BufferType type, FloatBuffer buffer, UsageType usage){
        this(type, usage);
        uploadData(buffer);
    }
    
    public OpenGLBuffer(BufferType type, IntBuffer buffer, UsageType usage){
        this(type, usage);
        uploadData(buffer);
    }
    
    @Override
    public void bind() {
        buffer.bind(fromBufferType(target));
        bound = true;
    }
    
    @Override
    public void unbind() {
        buffer.unbind(fromBufferType(target));
    }

    @Override
    public void alloc(int size) {
        bind();
        buffer.uploadData(fromBufferType(target), size, fromUsageType(usage));
        unbind();
    }

    @Override
    public void uploadData(FloatBuffer data) {
        bind();
        buffer.uploadData(fromBufferType(target), data, fromUsageType(usage));
        unbind();
    }
    
    @Override
    public void uploadData(IntBuffer data) {
        bind();
        buffer.uploadData(fromBufferType(target), data, fromUsageType(usage));
        unbind();
    }

    @Override
    public void uploadSubData(FloatBuffer data, long offset) {
        bind();
        buffer.uploadSubData(fromBufferType(target), offset, data);
        unbind();
    }
    
    @Override
    public void uploadSubData(IntBuffer data, long offset) {
        bind();
        buffer.uploadSubData(fromBufferType(target), offset, data);
        unbind();
    }
    
    @Override
    public void bindBase(int base){
        buffer.bindBase(fromBufferType(target), base);
        index = base;
    }
    
    @Override
    public int getBase(){
        return index;
    }
    
    @Override
    public int getSize(){
        return buffer.getSize(fromBufferType(target));
    }
    
    @Override
    public BufferType getTarget(){
        return target;
    }
    
    @Override
    public UsageType getUsage(){
        return usage;
    }

    public static int fromUsageType(UsageType type){
        switch(type){
            case STATIC_DRAW: return GL_STATIC_DRAW;
            case DYNAMIC_DRAW: return GL_DYNAMIC_DRAW;
            case STREAM_DRAW: return GL_STREAM_DRAW;
            case STATIC_READ: return GL_STATIC_READ;
            case DYNAMIC_READ: return GL_DYNAMIC_READ;
            case STREAM_READ: return GL_STREAM_READ;
            case STATIC_COPY: return GL_STATIC_COPY;
            case DYNAMIC_COPY: return GL_DYNAMIC_COPY;
            case STREAM_COPY: return GL_STREAM_COPY;
        }

        return 0;
    }

    public static int fromBufferType(BufferType type){
        switch(type) {
            case VERTEX_ARRAY_BUFFER:
                return GL_ARRAY_BUFFER;
            case ELEMENT_ARRAY_BUFFER:
                return GL_ELEMENT_ARRAY_BUFFER;
            case COPY_READ_BUFFER:
                return GL_COPY_READ_BUFFER;
            case COPY_WRITE_BUFFER:
                return GL_COPY_WRITE_BUFFER;
            case PIXEL_UNPACK_BUFFER:
                return GL_PIXEL_UNPACK_BUFFER;
            case PIXEL_PACK_BUFFER:
                return GL_PIXEL_PACK_BUFFER;
            case QUERY_BUFFER:
                return GL_QUERY_BUFFER;
            case TEXTURE_BUFFER:
                return GL_TEXTURE_BUFFER;
            case TRANSFORM_FEEDBACK_BUFFER:
                return GL_TRANSFORM_FEEDBACK_BUFFER;
            case UNIFORM_BUFFER:
                return GL_UNIFORM_BUFFER;
            case DRAW_INDIRECT_BUFFER:
                return GL_DRAW_INDIRECT_BUFFER;
            case ATOMIC_COUNTER_BUFFER:
                return GL_ATOMIC_COUNTER_BUFFER;
            case DISPATCH_INDIRECT_BUFFER:
                return GL_DISPATCH_INDIRECT_BUFFER;
            case SHADER_STORAGE_BUFFER:
                return GL_SHADER_STORAGE_BUFFER;
        }
        return 0;
    }
}
