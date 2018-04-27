/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.render.shader.ShaderProgram;

import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class OpenGLShaderPipeline implements com.opengg.core.render.shader.ShaderPipeline{
    final private NativeOpenGLShaderPipeline nativepipeline;
    final private String  vert, frag, tesc, tese, geom;
    
    public OpenGLShaderPipeline(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag){
        this.vert = vert.getName();
        
        if(tesc != null)
            this.tesc = tesc.getName();
        else
            this.tesc = "";
        
        if(tese != null)
            this.tese = tese.getName();
        else
            this.tese = "";
        
        if(geom != null)
            this.geom = geom.getName();
        else
            this.geom = "";

        this.frag = frag.getName();
        
        nativepipeline = new NativeOpenGLShaderPipeline();

        nativepipeline.useProgramStages(vert, GL_VERTEX_SHADER_BIT);
        
        if(tesc != null)
            nativepipeline.useProgramStages(tesc, GL_TESS_CONTROL_SHADER_BIT);
        
        if(tese != null)
            nativepipeline.useProgramStages(tese, GL_TESS_EVALUATION_SHADER_BIT);
        
        if(geom != null)
            nativepipeline.useProgramStages(geom, GL_GEOMETRY_SHADER_BIT);

        nativepipeline.useProgramStages(frag, GL_FRAGMENT_SHADER_BIT);
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
    public void bind(){
        nativepipeline.bind();
    }

    @Override
    public void unbind(){
        nativepipeline.unbind();
    }

    @Override
    public String getShader(ShaderProgram.ShaderType type){
        switch(type){
            case VERTEX:
                return vert;
            case TESS_CONTROL:
                return tesc;
            case TESS_EVAL:
                return tese;
            case GEOMETRY:
                return geom;
            case FRAGMENT:
                return frag;
        }
        return null;
    }

}
