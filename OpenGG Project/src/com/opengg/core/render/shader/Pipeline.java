/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.GGConsole;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class Pipeline {
    int id = 0;
    
    public Pipeline(){
        id = glGenProgramPipelines();
    }
    
    public Pipeline(Program vert, Program geom, Program frag){
        id = glGenProgramPipelines();
        bind();
        if(!glIsProgramPipeline(id)){
            GGConsole.error("Failed to generate program pipeline ID!");
            return;
        }
        glUseProgramStages(id, Program.VERTEX, vert.id);
        glUseProgramStages(id, Program.GEOMETRY, geom.id);
        glUseProgramStages(id, Program.FRAGMENT, frag.id);
        validate();
        unbind();
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
            GGConsole.error(s);
        }
    }
    
    public String getStatus(){
        return glGetProgramPipelineInfoLog(id);
    }
}
