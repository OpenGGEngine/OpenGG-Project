/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.render.shader.ShaderProgram;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLShaderPipeline{
    private final int id;

    public NativeOpenGLShaderPipeline(){
        glUseProgram(0);
        id = glGenProgramPipelines();
        bind();
        if(!glIsProgramPipeline(id)){
            throw new ShaderException("Failed to generate program pipeline ID!");
        }
    }

    public void useProgramStages(ShaderProgram program, int stages){
        glUseProgramStages(id, stages, program.getId());
    }

    public void bind(){
        glBindProgramPipeline(id);
    }

    public void unbind(){
        glBindProgramPipeline(0);
    }

    public void validate(){
        glValidateProgramPipeline(id);
        int status = glGetProgramPipelinei(id, GL_VALIDATE_STATUS);
        if (status != GL_TRUE) {
            String s = getStatus();
            throw new ShaderException("Failed to validate shader pipeline: " + s);
        }
    }

    public String getStatus(){
        return glGetProgramPipelineInfoLog(id);
    }

    public void delete(){
        glDeleteProgramPipelines(id);
    }
}
