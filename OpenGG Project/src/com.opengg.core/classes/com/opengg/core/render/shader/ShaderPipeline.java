/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.GGConsole;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class ShaderPipeline {
    int id = 0;
    String  vert,
            frag,
            tesc,
            tese,
            geom;
    
    public ShaderPipeline(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag){
        this.vert = vert.name;
        
        if(tesc != null)
            this.tesc = tesc.name;
        
        if(tese != null)
            this.tese = tese.name;
        
        if(geom != null)
            this.geom = geom.name;

        this.frag = frag.name;
        
        glUseProgram(0);
        id = glGenProgramPipelines();
        bind();
        if(!glIsProgramPipeline(id)){
            GGConsole.error("Failed to generate program pipeline ID!");
            return;
        }
        glUseProgramStages(id, GL_VERTEX_SHADER_BIT, vert.getId());
        
        if(tesc != null)
            glUseProgramStages(id, GL_TESS_CONTROL_SHADER_BIT, tesc.getId());
        
        if(tese != null)
            glUseProgramStages(id, GL_TESS_EVALUATION_SHADER_BIT, tese.getId());
        
        if(geom != null)
            glUseProgramStages(id, GL_GEOMETRY_SHADER_BIT, geom.getId());
        
        glUseProgramStages(id, GL_FRAGMENT_SHADER_BIT, frag.getId());
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
    
    public void deletePipeline(){
        glDeleteProgramPipelines(id);
    }
}
