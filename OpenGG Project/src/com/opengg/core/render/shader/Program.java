/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import java.util.HashMap;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class Program {
    public final static int VERTEX = GL_VERTEX_SHADER, 
            GEOMETRY = GL_GEOMETRY_SHADER,
            FRAGMENT = GL_FRAGMENT_SHADER;
    
    public String name;
    public int id;
    public int type;
    
    private HashMap<String, Integer> ulocs = new HashMap<>();
    private HashMap<String, Integer> alocs = new HashMap<>();
    
    public Program(int type, CharSequence source, String name){
        this.name = name;
        this.type = type;
        id = glCreateShaderProgramv(type, source);
        glUseProgram(id);
        checkStatus();
        glUseProgram(0);
    }
    
    public void findUniformLocation(String pos){
        int nid = glGetUniformLocation(id, pos);
        ulocs.put(pos, nid);
    }
    
    public int getUniformLocation(String pos){
        return ulocs.get(pos);
    }
    
    public void bindFragmentDataLocation(int number, CharSequence name) {
        glBindFragDataLocation(id, number, name);
    }
    
    public void findAttributeLocation(String name) {
         int nid = glGetAttribLocation(id, name);
         alocs.put(name, nid);
    }

    public int getAttributeLocation(String name){
        return ulocs.get(name);
    }
    
    /**
     * Enables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void enableVertexAttribute(String location) {
        glEnableVertexAttribArray(alocs.get(location));
    }

    /**
     * Disables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void disableVertexAttribute(String location) {
        glDisableVertexAttribArray(alocs.get(location));
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
    public void pointVertexAttribute(String location, int size, int stride, int offset) {
        glVertexAttribPointer(alocs.get(location), size, GL_FLOAT, false, stride, offset);
    }
    /**
     * Sets the vertex attribute divisor
     * 
     * @param location Location of attribute
     * @param divisor Set to 0 if not instanced, 1 if instanced
     */
    public void setVertexAttribDivisor(String location, int divisor){
        glVertexAttribDivisor(alocs.get(location), divisor);
    }
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, int value) {
        glProgramUniform1i(id, location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, boolean value) {
        if(value){
            glProgramUniform1i(id, location, 1);
        }else{
            glProgramUniform1i(id, location, 0);
        }  
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, float value) {
        glProgramUniform1f(id, location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector2f value) {
        glProgramUniform2fv(id, location, value.getBuffer());
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector3f value) {
        glProgramUniform3fv(id, location, value.getBuffer());
    }

    public void setUniform(int location, Matrix4f value) {
        glProgramUniformMatrix4fv(id, location, false, value.getBuffer());
    }
    
    public void setUniformBlockIndex(int bind, String name){
        int index = glGetUniformBlockIndex(id, name);
        glUniformBlockBinding(id, index, bind);
        glGetError(); //CATCH 1281 FROM MISSING INDEX
    }
    
    public void checkStatus() {
        int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            String s = glGetProgramInfoLog(id);
            throw new RuntimeException("From shader (" + name + "):" + glGetProgramInfoLog(id));
        }
    }
}
