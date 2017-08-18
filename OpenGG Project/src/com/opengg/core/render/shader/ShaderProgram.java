/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import java.nio.ByteBuffer;
import java.util.HashMap;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 *
 * @author Javier
 */
public class ShaderProgram {
    public final static int VERTEX = GL_VERTEX_SHADER, 
            GEOMETRY = GL_GEOMETRY_SHADER,
            FRAGMENT = GL_FRAGMENT_SHADER;
    
    public String name;
    NativeGLProgram program;
    public int type;
    
    private HashMap<String, Integer> ulocs = new HashMap<>();
    private HashMap<String, Integer> alocs = new HashMap<>();
    
    public ShaderProgram(int type, CharSequence source, String name){
        this.name = name;
        this.type = type;
        program = new NativeGLProgram(type, source);
    }
    
    public void findUniformLocation(String pos){
        int nid = program.findUniformLocation(pos);
        ulocs.put(pos, nid);
    }
    
    public int getUniformLocation(String pos){
        return ulocs.get(pos);
    }
    
    public void bindFragmentDataLocation(int number, CharSequence name) {
        program.bindFragmentDataLocation(number, name);
    }
    
    public void findAttributeLocation(String name) {
        System.out.println("---"+this.name+"---");
        System.out.println("local name " +name);
         int nid = program.findAttributeLocation(name);
         System.out.println("nid:" +nid);
         alocs.put(name, nid);
    }

    public int getAttributeLocation(String name){
        return alocs.get(name);
    }
    
    /**
     * Enables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void enableVertexAttribute(String location) {
        program.enableVertexAttribute(getAttributeLocation(location));
    }

    /**
     * Disables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void disableVertexAttribute(String location) {
        program.disableVertexAttribute(getAttributeLocation(location));
    }

    /**
     * Sets the vertex attribute pointer.
     *
     * @param location Location of the vertex attribute
     * @param size Number of values per vertex
     * @param type Type of data
     * @param stride Offset between consecutive generic vertex attributes in
     * bytes
     * @param offset Offset of the first component of the first generic vertex
     * attribute in bytes
     */
    public void pointVertexAttribute(String location, int size, int type, int stride, int offset) {
        program.pointVertexAttribute(getAttributeLocation(location), size, type, stride, offset);
    }
    /**
     * Sets the vertex attribute divisor
     * 
     * @param location Location of attribute
     * @param divisor Set to 0 if not instanced, 1 if instanced
     */
    public void setVertexAttribDivisor(String location, int divisor){
       program.setVertexAttribDivisor(getAttributeLocation(location), divisor);
    }
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, int value) {
        program.setUniform(location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, boolean value) {
        program.setUniform(location, value);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, float value) {
        program.setUniform(location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector2f value) {
        program.setUniform(location, value);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector3f value) {
        program.setUniform(location, value);
    }

    public void setUniform(int location, Matrix4f value) {
        program.setUniform(location, value);
    }
    
    public void setUniform(int location, Matrix4f[] matrices) {
        program.setUniform(location, matrices);
    }

    public void setUniformBlockIndex(int bind, String name){
        program.setUniformBlockIndex(bind, name);
    }
    
    public ByteBuffer getProgramBinary(){
        return program.getProgramBinary();
    }
    
    public void checkStatus() {
        int i;
        if((i = program.checkStatus()) != GL_TRUE){
            GGConsole.error("Shader " + name + " threw an error on status check: "  + i);
            throw new ShaderException(("From shader " + name + " with error code " + i + ": " + program.getProgramInfoLog()));
        }
    }
    
    public int getId(){
        return program.id;
    }
}
