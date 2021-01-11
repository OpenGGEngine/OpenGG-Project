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
import com.opengg.core.math.Vector4f;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.ShaderProgram;

import java.nio.ByteBuffer;
import java.util.*;

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
    public Map<String, Integer> uniformSet;
    private String source;

    public OpenGLShaderProgram(ShaderType type, String source, String name){
        this.name = name;
        this.type = type;
        this.source = source;
        program = new NativeOpenGLShaderProgram(getInternalType(type), source);
        uniformSet = program.getAllUniforms();
        this.checkStatus();
    }

    public OpenGLShaderProgram(ShaderType type, ByteBuffer source, String name){
        this.name = name;
        this.type = type;
        program = new NativeOpenGLShaderProgram(getInternalType(type), source);
    }

    private static int getInternalType(ShaderType type){
        return switch (type) {
            case VERTEX -> GL_VERTEX_SHADER;
            case TESS_CONTROL -> GL_TESS_CONTROL_SHADER;
            case TESS_EVAL -> GL_TESS_EVALUATION_SHADER;
            case GEOMETRY -> GL_GEOMETRY_SHADER;
            case FRAGMENT -> GL_FRAGMENT_SHADER;
            default -> 0;
        };
    }

    public void bindFragmentDataLocation(int number, CharSequence name) {
        program.bindFragmentDataLocation(number, name);
    }

    /**
     * Enables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void enableVertexAttribute(int location) {
        program.enableVertexAttribute(location);
    }

    /**
     * Disables a vertex attribute.
     *
     * @param location Location of the vertex attribute
     */
    public void disableVertexAttribute(int location) {
        program.disableVertexAttribute(location);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(String location, int value) {
        program.setUniform(uniformSet.get(location), value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(String location, boolean value) {
        program.setUniform(uniformSet.get(location), value);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(String location, float value) {
        program.setUniform(uniformSet.get(location), value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(String location, Vector2f value) {
        program.setUniform(uniformSet.get(location), value);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(String location, Vector3f value) {
        program.setUniform(uniformSet.get(location), value);
    }

    public void setUniform(String location, Vector4f value) {
        program.setUniform(uniformSet.get(location), value);
    }

    public void setUniform(String location, Matrix4f value) {
        program.setUniform(uniformSet.get(location), value);
    }
    
    public void setUniform(String location, Matrix4f[] matrices) {
        program.setUniform(uniformSet.get(location), matrices);
    }

    public void setUniformBlockIndex(int bind, String name){
        program.setUniformBlockIndex(bind, name);
    }

    public Map<String, Integer> getUniformSet() {
        return uniformSet;
    }

    @Override
    public List<ShaderController.Uniform> getUniforms() {
        return null;
    }

    @Override
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

    @Override
    public String getName(){
        return name;
    }

    @Override
    public ShaderType getType(){
        return type;
    }

    public int getId(){
        return program.getId();
    }
}
