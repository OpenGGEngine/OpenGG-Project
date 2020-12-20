/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.system.Allocator;
import com.opengg.core.system.SystemInfo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.ARBSeparateShaderObjects.glProgramUniformMatrix4fv;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL43.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLShaderProgram{
    private final int id;
    
    public NativeOpenGLShaderProgram(int type, CharSequence source){
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glCreateShaderProgramv(type, source);
    }

    public NativeOpenGLShaderProgram(int type, ByteBuffer source){
        if(RenderEngine.validateInitialization()) id = -1;
        else{
            id = glCreateProgram();
            glProgramParameteri(id, GL_PROGRAM_SEPARABLE, GL_TRUE);
            glProgramBinary(id, 36894, source);
        }
    }
    
    public int findUniformLocation(String name){
        if(RenderEngine.validateInitialization()) return -1;
        return glGetUniformLocation(id, name);
    }
    
    public void bindFragmentDataLocation(int number, CharSequence name) {
        if(RenderEngine.validateInitialization()) return;
        glBindFragDataLocation(id, number, name);
    }

    /**
     * Enables a vertex attribute.
     *
     * @param loc Location of the vertex attribute
     */
    public void enableVertexAttribute(int loc) {
        glEnableVertexAttribArray(loc);
    }

    /**
     * Disables a vertex attribute.
     *
     * @param loc Location of the vertex attribute
     */
    public void disableVertexAttribute(int loc) {
        glDisableVertexAttribArray(loc);
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
        glProgramUniform1f(id,location,value?1f:0f);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, float value) {
        if(RenderEngine.validateInitialization()) return;
        glProgramUniform1f(id, location, value);
    }
    
    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector2f value) {
        if(RenderEngine.validateInitialization()) return;
        glProgramUniform2fv(id, location, value.getBuffer());
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value Value to set
     */
    public void setUniform(int location, Vector3f value) {
        if(RenderEngine.validateInitialization()) return;
        glProgramUniform3fv(id, location, value.getStackBuffer());
        Allocator.popStack();
    }

    public void setUniform(int location, Matrix4f value) {
        if(RenderEngine.validateInitialization()) return;

        glProgramUniformMatrix4fv(id, location, false, value.getStackBuffer());
        Allocator.popStack();
    }
    
    public void setUniform(int location, Matrix4f[] matrices) {
        if(RenderEngine.validateInitialization()) return;
            int length = matrices != null ? matrices.length : 0;
            FloatBuffer fb = Allocator.stackAllocFloat(16 * length);
            for (Matrix4f mat : matrices) {
                if(mat == null){
                    mat = new Matrix4f();
                }
                
                fb.put(mat.m00).put(mat.m01).put(mat.m02).put(mat.m03);
                fb.put(mat.m10).put(mat.m11).put(mat.m12).put(mat.m13);
                fb.put(mat.m20).put(mat.m21).put(mat.m22).put(mat.m23);
                fb.put(mat.m30).put(mat.m31).put(mat.m32).put(mat.m33);
            }
            
            fb.flip();
            glProgramUniformMatrix4fv(id, location, false, fb);
            Allocator.popStack();
    }
    
    public void setUniformBlockIndex(int bind, String name){
        if(RenderEngine.validateInitialization()) return;
        int index = glGetUniformBlockIndex(id, name);
        if(index == -1) return;
        glUniformBlockBinding(id, index, bind);
    }

    public Map<String, Integer> getAllUniforms() {
        int[] numActiveUniforms = new int[]{0};
        glGetProgramInterfaceiv(id, GL_UNIFORM, GL_ACTIVE_RESOURCES, numActiveUniforms);

        var uniforms = new HashMap<String, Integer>();
        for(int uniform = 0; uniform < numActiveUniforms[0]; ++uniform)
        {
            var name = glGetProgramResourceName(id, GL_UNIFORM, uniform);
            var index = glGetUniformLocation(id, name);
            uniforms.put(name, index);
        }
        return uniforms;
    }

    public void bind(){
        glUseProgram(id);
    }

    public void unbind(){
        glUseProgram(0);
    }

    public ByteBuffer getProgramBinary(){
        if(RenderEngine.validateInitialization()) return null;
        IntBuffer length = Allocator.stackAllocInt(1);
        IntBuffer type = Allocator.stackAllocInt(1);
        ByteBuffer data = Allocator.alloc(128*1024);
        glGetProgramBinary(id, length, type, data);
        int truelength = length.get();
        ByteBuffer finaldata = Allocator.alloc(truelength);
        for(int i = 0; i < truelength; i++){
            finaldata.put(data.get());
        }
                
        Allocator.popStack();
        Allocator.popStack();
        finaldata.flip();
        return finaldata;
    }

    public int getId(){
        return id;
    }

    public int checkStatus() {
        if(RenderEngine.validateInitialization()) return -1;
        return glGetProgrami(id, GL_LINK_STATUS);
    }
    
    public String getProgramInfoLog(){
        if(RenderEngine.validateInitialization()) return "OpenGL not initialized";
        return glGetProgramInfoLog(id);
    }


}
