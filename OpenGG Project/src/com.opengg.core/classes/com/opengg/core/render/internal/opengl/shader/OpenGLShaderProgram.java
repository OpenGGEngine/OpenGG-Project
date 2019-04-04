/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.internal.opengl.shader.NativeOpenGLShaderProgram;
import com.opengg.core.render.shader.ShaderProgram;

import java.nio.ByteBuffer;
import java.util.HashMap;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;

/**
 *
 * @author Javier
 */
public class OpenGLShaderProgram implements ShaderProgram{

    private final String name;
    private final NativeOpenGLShaderProgram program;
    private final ShaderType type;
    
    private final HashMap<String, Integer> ulocs = new HashMap<>();
    private final HashMap<String, Integer> alocs = new HashMap<>();
    private final HashMap<Integer, Object> uniformVals = new HashMap<>();
    
    public OpenGLShaderProgram(ShaderType type, CharSequence source, String name){
        this.name = name;
        this.type = type;

        int internaltype = 0;
        switch(type){
            case VERTEX:
                internaltype = GL_VERTEX_SHADER;
                break;
            case TESS_CONTROL:
                internaltype = GL_TESS_CONTROL_SHADER;
                break;
            case TESS_EVAL:
                internaltype = GL_TESS_EVALUATION_SHADER;
                break;
            case GEOMETRY:
                internaltype = GL_GEOMETRY_SHADER;
                break;
            case FRAGMENT:
                internaltype = GL_FRAGMENT_SHADER;
                break;
        }

        program = new NativeOpenGLShaderProgram(internaltype, source);
    }
    
    @Override
    public void findUniformLocation(String pos){
        int nid = program.findUniformLocation(pos);

        ulocs.put(pos, nid);

        if(nid == -1) return;

        if(pos.equals("view") || pos.equals("model") || pos.equals("projection")) System.out.println(this.name);

        if(pos.equals("view")) System.out.println(pos + " " + nid);
        if(pos.equals("model")) System.out.println(pos + " " + nid);

    }
    
    @Override
    public int getUniformLocation(String pos){
        return ulocs.get(pos);
    }
    
    @Override
    public void bindFragmentDataLocation(int number, CharSequence name) {
        program.bindFragmentDataLocation(number, name);
    }
    
    @Override
    public void findAttributeLocation(String name) {
         int nid = program.findAttributeLocation(name);
         alocs.put(name, nid);
    }

    @Override
    public int getAttributeLocation(String name){
        return alocs.get(name);
    }
    
    /**
     * Enables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    @Override
    public void enableVertexAttribute(String location) {
        program.enableVertexAttribute(getAttributeLocation(location));
    }

    /**
     * Disables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    @Override
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
    @Override
    public void pointVertexAttribute(String location, int size, int type, int stride, int offset) {
        program.pointVertexAttribute(getAttributeLocation(location), size, type, stride, offset);
    }
    /**
     * Sets the vertex attribute divisor
     * 
     * @param location Location of attribute
     * @param divisor Set to 0 if not instanced, 1 if instanced
     */
    @Override
    public void setVertexAttribDivisor(String location, int divisor){
       program.setVertexAttribDivisor(getAttributeLocation(location), divisor);
    }
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    @Override
    public void setUniform(int location, int value) {
        uniformVals.put(location, value);
        program.setUniform(location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    @Override
    public void setUniform(int location, boolean value) {
        uniformVals.put(location, value);
        program.setUniform(location, value);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    @Override
    public void setUniform(int location, float value) {

        uniformVals.put(location, value);
        program.setUniform(location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    @Override
    public void setUniform(int location, Vector2f value) {
        uniformVals.put(location, value);
        program.setUniform(location, value);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    @Override
    public void setUniform(int location, Vector3f value) {
        uniformVals.put(location, value);
        program.setUniform(location, value);
    }

    @Override
    public void setUniform(int location, Matrix4f value) {
        uniformVals.put(location, value);
        program.setUniform(location, value);
    }
    
    @Override
    public void setUniform(int location, Matrix4f[] matrices) {
        uniformVals.put(location, matrices);
        program.setUniform(location, matrices);
    }
    @Override
    public void setUniformBlockIndex(int bind, String name){
        program.setUniformBlockIndex(bind, name);
    }
    
    @Override
    public ByteBuffer getProgramBinary(){
        return program.getProgramBinary();
    }

    @Override
    public void checkStatus() {
        int i;
        if((i = program.checkStatus()) != GL_TRUE){
            GGConsole.error("Shader " + name + " threw an error on status check: "  + i);
            throw new ShaderException(("From shader " + name + " with error code " + i + ": " + program.getProgramInfoLog()));
        }
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public ShaderType getType(){
        return type;
    }

    @Override
    public int getId(){
        return program.getId();
    }
}
