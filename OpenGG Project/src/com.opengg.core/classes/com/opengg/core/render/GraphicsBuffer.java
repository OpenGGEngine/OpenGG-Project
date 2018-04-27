package com.opengg.core.render;

import com.opengg.core.render.internal.opengl.OpenGLBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Javier
 */
public interface GraphicsBuffer{

    public static GraphicsBuffer allocate(int type, int busage){
        return new OpenGLBuffer(type, busage);
    }

    public static GraphicsBuffer allocate(int type, int size, int usage){
        return new OpenGLBuffer(type, size, usage);
    }

    public static GraphicsBuffer allocate(int type, FloatBuffer buffer, int usage){
        return new OpenGLBuffer(type, buffer, usage);
    }

    public static GraphicsBuffer allocte(int type, IntBuffer buffer, int usage){
        return new OpenGLBuffer(type, buffer, usage);
    }

    void bind();

    void unbind();

    void alloc(int size);

    void uploadData(FloatBuffer data);

    void uploadData(IntBuffer data);

    void uploadSubData(FloatBuffer data, long offset);

    void uploadSubData(IntBuffer data, long offset);

    void bindBase(int base);

    int getBase();

    int getSize();

    int getTarget();

    int getUsage();

    void delete();
}
