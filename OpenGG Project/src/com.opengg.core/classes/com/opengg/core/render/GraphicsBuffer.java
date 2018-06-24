package com.opengg.core.render;

import com.opengg.core.render.internal.opengl.OpenGLBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Represents a GPU VRAM mapped buffer for the current graphics context
 * @author Javier
 */
public interface GraphicsBuffer{

    /**
     * Allocates empty memory of the given type and access type
     * @param type Memory usage type
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    public static GraphicsBuffer allocate(int type, int access){
        return new OpenGLBuffer(type, access);
    }

    /**
     * Allocates empty memory of the given buffer type, size, and access type
     * @param type Memory usage type
     * @param size Amount of bytes to allocate
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    public static GraphicsBuffer allocate(int type, int size, int access){
        return new OpenGLBuffer(type, size, access);
    }

    /**
     * Allocates and uploads the given {@code FloatBuffer} to the GPU using the given buffer type and access type
     * @param type Memory usage type
     * @param buffer Buffer to upload
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    public static GraphicsBuffer allocate(int type, FloatBuffer buffer, int access){
        return new OpenGLBuffer(type, buffer, access);
    }

    /**
     * Allocates and uploads the given {@code IntBuffer} to the GPU using the given buffer type and access type
     * @param type Memory usage type
     * @param buffer Buffer to upload
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    public static GraphicsBuffer allocate(int type, IntBuffer buffer, int access){
        return new OpenGLBuffer(type, buffer, access);
    }

    /**
     * Binds this buffer for use
     */
    void bind();

    /**
     * Unbinds this buffer
     */
    void unbind();

    /**
     * Clears and reallocates a buffer of size {@code size}
     * @param size New size
     */
    void alloc(int size);

    /**
     * Uploads the given {@code FloatBuffer} to the buffer
     * @param data
     */
    void uploadData(FloatBuffer data);

    /**
     * Uploads the given {@code IntBuffer} to the buffer
     * @param data
     */
    void uploadData(IntBuffer data);

    /**
     * Copies the given {@code FloatBuffer} to the buffer starting at the given offset
     * @param data
     * @param offset
     */
    void uploadSubData(FloatBuffer data, long offset);

    /**
     * Copies the given {@code IntBuffer} to the buffer starting at the given offset
     * @param data
     * @param offset
     */
    void uploadSubData(IntBuffer data, long offset);

    /**
     * Binds this buffer to the given base position
     * @param base
     */
    void bindBase(int base);

    /**
     * Gets the base index this buffer is bound to
     * @return
     */
    int getBase();

    /**
     * Gets the size of this buffer in bytes
     * @return
     */
    int getSize();

    /**
     * Gets the target this buffer is set to
     * @return
     */
    int getTarget();

    /**
     * Gets the memory usage format this buffer is laid out in
     * @return
     */
    int getUsage();

    /**
     * Deletes and frees all VRAM used by this buffer
     */
    void delete();
}
