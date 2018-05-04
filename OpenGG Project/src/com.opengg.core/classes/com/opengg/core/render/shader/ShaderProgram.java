/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.internal.opengl.shader.OpenGLShaderProgram;

import java.nio.ByteBuffer;

/**
 *
 * @author Javier
 */
public interface ShaderProgram{
    enum ShaderType{
        VERTEX, TESS_CONTROL, TESS_EVAL, GEOMETRY, FRAGMENT
    }

    public static ShaderProgram create(ShaderType type, CharSequence source, String name){
        return new OpenGLShaderProgram(type, source, name);
    }

    void findUniformLocation(String pos);

    int getUniformLocation(String pos);

    void bindFragmentDataLocation(int number, CharSequence name);

    void findAttributeLocation(String name);

    int getAttributeLocation(String name);

    void enableVertexAttribute(String location);

    void disableVertexAttribute(String location);

    void pointVertexAttribute(String location, int size, int type, int stride, int offset);

    void setVertexAttribDivisor(String location, int divisor);

    void setUniform(int location, int value);

    void setUniform(int location, boolean value);

    void setUniform(int location, float value);

    void setUniform(int location, Vector2f value);

    void setUniform(int location, Vector3f value);

    void setUniform(int location, Matrix4f value);

    void setUniform(int location, Matrix4f[] matrices);

    void setUniformBlockIndex(int bind, String name);

    ByteBuffer getProgramBinary();

    String getName();

    void checkStatus();

    ShaderType getType();

    int getId();
}
