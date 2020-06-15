/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.ShaderProgram;

import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class OpenGLShaderPipeline implements com.opengg.core.render.shader.ShaderPipeline{
    final private NativeOpenGLShaderPipeline nativepipeline;
    private final ShaderProgram vert;
    private final ShaderProgram frag;
    private ShaderProgram tesc;
    private ShaderProgram tese;
    private ShaderProgram geom;
    
    public OpenGLShaderPipeline(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag){
        this.vert = vert;
        
        if(tesc != null)
            this.tesc = tesc;

        
        if(tese != null)
            this.tese = tese;
        
        if(geom != null)
            this.geom = geom;


        this.frag = frag;
        
        nativepipeline = new NativeOpenGLShaderPipeline();

        nativepipeline.useProgramStages((OpenGLShaderProgram) vert, GL_VERTEX_SHADER_BIT);
        
        if(tesc != null)
            nativepipeline.useProgramStages((OpenGLShaderProgram) tesc, GL_TESS_CONTROL_SHADER_BIT);
        
        if(tese != null)
            nativepipeline.useProgramStages((OpenGLShaderProgram) tese, GL_TESS_EVALUATION_SHADER_BIT);
        
        if(geom != null)
            nativepipeline.useProgramStages((OpenGLShaderProgram) geom, GL_GEOMETRY_SHADER_BIT);

        nativepipeline.useProgramStages((OpenGLShaderProgram) frag, GL_FRAGMENT_SHADER_BIT);
        validate();
        unbind();
    }

    @Override
    public void delete(){
        nativepipeline.delete();
    }

    @Override
    public void validate(){
        nativepipeline.validate();
    }

    @Override
    public void use(){
        nativepipeline.bind();
    }

    @Override
    public void unbind(){
        nativepipeline.unbind();
    }

    @Override
    public ShaderProgram getShader(ShaderProgram.ShaderType type){
        return switch (type) {
            case VERTEX -> vert;
            case TESS_CONTROL -> tesc;
            case TESS_EVAL -> tese;
            case GEOMETRY -> geom;
            case FRAGMENT -> frag;
            default -> null;
        };
    }
}
