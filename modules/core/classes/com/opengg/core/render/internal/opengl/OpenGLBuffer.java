/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.render.GraphicsBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL42.GL_ATOMIC_COUNTER_BUFFER;
import static org.lwjgl.opengl.GL43.GL_DISPATCH_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.*;

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

    private OpenGLBuffer(BufferType type, UsageType usage){
        this.buffer = new NativeOpenGLBuffer();
        this.target = type;
        this.size = 0;
        this.usage = usage;
    }
    
    public OpenGLBuffer(BufferType type, int size, UsageType usage){
        this(type, usage);
        alloc(size);
        PerformanceManager.registerBufferAllocation(size);
    }

    public OpenGLBuffer(BufferType type, ByteBuffer buffer, UsageType usage){
        this(type, usage);
        uploadData(buffer);
        PerformanceManager.registerBufferAllocation(buffer.limit());
    }

    public OpenGLBuffer(BufferType type, FloatBuffer buffer, UsageType usage){
        this(type, usage);
        uploadData(buffer);
        PerformanceManager.registerBufferAllocation(buffer.limit() * Float.BYTES);
    }
    
    public OpenGLBuffer(BufferType type, IntBuffer buffer, UsageType usage){
        this(type, usage);
        uploadData(buffer);
        PerformanceManager.registerBufferAllocation(buffer.limit() * Integer.BYTES);
    }

    public void alloc(int size) {
        buffer.createStorage(size, fromUsageType(usage));
    }

    @Override
    public void uploadData(ByteBuffer data) {
        buffer.setStorage(data, fromUsageType(usage));
    }

    @Override
    public void uploadData(FloatBuffer data) {
        buffer.setStorage(data, fromUsageType(usage));
    }
    
    @Override
    public void uploadData(IntBuffer data) {
        buffer.setStorage(data, fromUsageType(usage));
    }

    @Override
    public void uploadSubData(FloatBuffer data, long offset) {
        buffer.uploadSubData(offset, data);
    }
    
    @Override
    public void uploadSubData(IntBuffer data, long offset) {
        buffer.uploadSubData(offset, data);
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
        return buffer.getSize();
    }
    
    @Override
    public BufferType getTarget(){
        return target;
    }
    
    @Override
    public UsageType getUsage(){
        return usage;
    }

    public int getID(){
        return buffer.getID();
    }

    public static int fromUsageType(UsageType type){
        return switch (type) {
            case HOST_MAPPABLE -> GL_MAP_READ_BIT | GL_MAP_WRITE_BIT;
            case HOST_UPDATABLE -> GL_DYNAMIC_STORAGE_BIT;
            case HOST_MAPPABLE_UPDATABLE -> GL_MAP_READ_BIT | GL_MAP_WRITE_BIT | GL_DYNAMIC_STORAGE_BIT;
            case HOST_MEMORY -> GL_CLIENT_STORAGE_BIT;
            case NONE -> 0;
        };

    }

    public static int fromBufferType(BufferType type){
        return switch (type) {
            case VERTEX_ARRAY_BUFFER -> GL_ARRAY_BUFFER;
            case ELEMENT_ARRAY_BUFFER -> GL_ELEMENT_ARRAY_BUFFER;
            case COPY_READ_BUFFER -> GL_COPY_READ_BUFFER;
            case COPY_WRITE_BUFFER -> GL_COPY_WRITE_BUFFER;
            case PIXEL_UNPACK_BUFFER -> GL_PIXEL_UNPACK_BUFFER;
            case PIXEL_PACK_BUFFER -> GL_PIXEL_PACK_BUFFER;
            case QUERY_BUFFER -> GL_QUERY_BUFFER;
            case TEXTURE_BUFFER -> GL_TEXTURE_BUFFER;
            case TRANSFORM_FEEDBACK_BUFFER -> GL_TRANSFORM_FEEDBACK_BUFFER;
            case UNIFORM_BUFFER -> GL_UNIFORM_BUFFER;
            case DRAW_INDIRECT_BUFFER -> GL_DRAW_INDIRECT_BUFFER;
            case ATOMIC_COUNTER_BUFFER -> GL_ATOMIC_COUNTER_BUFFER;
            case DISPATCH_INDIRECT_BUFFER -> GL_DISPATCH_INDIRECT_BUFFER;
            case SHADER_STORAGE_BUFFER -> GL_SHADER_STORAGE_BUFFER;
        };
    }
}
