/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.ARBSeparateShaderObjects.glProgramUniformMatrix4fv;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL41.glCreateShaderProgramv;
import static org.lwjgl.opengl.GL41.glGetProgramBinary;
import static org.lwjgl.opengl.GL41.glProgramUniform1f;
import static org.lwjgl.opengl.GL41.glProgramUniform1i;
import static org.lwjgl.opengl.GL41.glProgramUniform2fv;
import static org.lwjgl.opengl.GL41.glProgramUniform3fv;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class NativeGLProgram {
    public int id;
    
    public NativeGLProgram(int type, CharSequence source){
        id = glCreateShaderProgramv(type, source);
    }
    
    public int findUniformLocation(String pos){
        return glGetUniformLocation(id, pos);
    }
    
    public void bindFragmentDataLocation(int number, CharSequence name) {
        glBindFragDataLocation(id, number, name);
    }
    
    public int findAttributeLocation(String name) {
         return glGetAttribLocation(id, name);
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
    public void pointVertexAttribute(int location, int size, int type, int stride, int offset) {
        glVertexAttribPointer(location, size, type, false, stride, offset);
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
    
    public ByteBuffer getProgramBinary(){
        IntBuffer length = MemoryUtil.memAllocInt(1);
        IntBuffer type = MemoryUtil.memAllocInt(1);
        ByteBuffer data = MemoryUtil.memAlloc(128*1024);
        glGetProgramBinary(id, length, type, data);
        int length2 = length.get();
        ByteBuffer finaldata = MemoryUtil.memAlloc(length2);
        for(int i = 0; i < length2; i++){
            finaldata.put(data.get());
        }
        finaldata.flip();
        return finaldata;
    }
    
    public int checkStatus() {
        return glGetProgrami(id, GL_LINK_STATUS);
    }
}
