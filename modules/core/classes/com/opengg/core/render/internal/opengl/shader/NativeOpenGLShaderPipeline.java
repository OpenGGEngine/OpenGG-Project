/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.render.RenderEngine;

import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLShaderPipeline{
    private final int id;

    public NativeOpenGLShaderPipeline(){
        if(RenderEngine.validateInitialization()) id = -1;

        else id = glGenProgramPipelines();
        glUseProgram(0);

        bind();
        if(!glIsProgramPipeline(id)){
            throw new ShaderException("Failed to generate program pipeline ID!");
        }
    }

    public void useProgramStages(OpenGLShaderProgram program, int stages){
        if(RenderEngine.validateInitialization()) return;
        glUseProgramStages(id, stages, program.getId());
    }

    public void bind(){
        if(RenderEngine.validateInitialization()) return;
        glBindProgramPipeline(id);
    }

    public void unbind(){
        if(RenderEngine.validateInitialization()) return;
        glBindProgramPipeline(0);
    }

    public void validate(){
        if(RenderEngine.validateInitialization()) return;
        glValidateProgramPipeline(id);
        int status = glGetProgramPipelinei(id, GL_VALIDATE_STATUS);
        if (status != GL_TRUE) {
            String s = getStatus();
            throw new ShaderException("Failed to validate shader pipeline: " + s);
        }
    }

    public String getStatus(){
        if(RenderEngine.validateInitialization()) return "OpenGL Not Initialized";
        return glGetProgramPipelineInfoLog(id);
    }

    public void delete(){
        if(RenderEngine.validateInitialization()) return;
        glDeleteProgramPipelines(id);
    }
}
