package com.opengg.core.render.shader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import static com.opengg.core.util.GlobalUtil.print;


import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * This class represents a shader program.
 */
public class ShaderProgram {

    /**
     * Stores the handle of the program.
     */
    private final int id;

    /**
     * Creates a shader program.
     */
    public ShaderProgram() {
        id = glCreateProgram();
    }

    /**
     * Attach a shader to this program.
     *
     * @param shader Shader to get attached
     */
    public void attachShader(Shader shader) {
        glAttachShader(id, shader.getID());
    }

    /**
     * Binds the fragment out color variable.
     *
     * @param number Color number you want to bind
     * @param name Variable name
     */
    public void bindFragmentDataLocation(int number, CharSequence name) {
        glBindFragDataLocation(id, number, name);
    }

    /**
     * Link this program.
     */
    public void link() {
        glLinkProgram(id);

        checkStatus();
    }

    /**
     * Gets the location of an attribute variable with specified name.
     *
     * @param name Attribute name
     * @return Location of the attribute
     */
    public int getAttributeLocation(CharSequence name) {
        return glGetAttribLocation(id, name);
    }

    /**
     * Enables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void enableVertexAttribute(int location) {
        glEnableVertexAttribArray(location);
    }

    /**
     * Disables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void disableVertexAttribute(int location) {
        glDisableVertexAttribArray(location);
    }

    /**
     * Sets the vertex attribute pointer.
     *
     * @param location Location of the vertex attribute
     * @param size Number of values per vertex
     * @param stride Offset between consecutive generic vertex attributes in
     * bytes
     * @param offset Offset of the first component of the first generic vertex
     * attribute in bytes
     */
    public void pointVertexAttribute(int location, int size, int stride, int offset) {
        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
    }
    /**
     * Sets the vertex attribute divisor
     * 
     * @param location Location of attribute
     * @param divisor Set to 0 if not instanced, 1 if instanced
     */
    public void setVertexAttribDivisor(int location, int divisor){
        glVertexAttribDivisor(location, divisor);
    }
    /**
     * Gets the location of an uniform variable with specified name.
     *
     * @param name Uniform name
     * @return Location of the uniform
     */
    public int getUniformLocation(CharSequence name) {
        return glGetUniformLocation(id, name);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, int value) {
        glUniform1i(location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, boolean value) {
        if(value){
            glUniform1i(location, 1);
        }else{
            glUniform1i(location, 0);
        }
        
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, float value) {
        glUniform1f(location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector2f value) {
        glUniform2fv(location, value.getBuffer());
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector3f value) {
        glUniform3fv(location, value.getBuffer());
    }

    public void setUniform(int location, Matrix4f value) {
        glUniformMatrix4fv(location, false, value.getBuffer());
    }

    /**
     * Use this shader program.
     */
    public void use() {
        glUseProgram(id);
        
    }

    /**
     * Checks if the program was linked successfully.
     */
    public void checkStatus() {
        int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            int e = glGetProgrami(id, GL_INFO_LOG_LENGTH);
            String s = glGetProgramInfoLog(id,e);
            System.out.println(s);
            throw new RuntimeException(glGetProgramInfoLog(id));
        }
    }

    /**
     * Deletes the shader program.
     */
    public void delete() {
        glDeleteProgram(id);
    }
}
