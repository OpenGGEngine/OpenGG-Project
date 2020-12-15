/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Javier
 */
public class OpenGLShaderPipeline implements com.opengg.core.render.shader.ShaderPipeline{
    final private NativeOpenGLShaderPipeline nativepipeline;
    private final OpenGLShaderProgram vert;
    private final OpenGLShaderProgram frag;
    private OpenGLShaderProgram tesc;
    private OpenGLShaderProgram tese;
    private OpenGLShaderProgram geom;
    
    public OpenGLShaderPipeline(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag){
        this.vert = (OpenGLShaderProgram) vert;
        this.tesc = (OpenGLShaderProgram) tesc;
        this.tese = (OpenGLShaderProgram) tese;
        this.geom = (OpenGLShaderProgram) geom;
        this.frag = (OpenGLShaderProgram) frag;
        
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
    }

    public List<String> getAllUsedUniforms(){
        var allUniforms = new ArrayList<>(vert.getUniformSet().keySet());
        if(geom != null) allUniforms.addAll(geom.getUniformSet().keySet());
        if(tesc != null) allUniforms.addAll(tesc.getUniformSet().keySet());
        if(tese != null) allUniforms.addAll(tese.getUniformSet().keySet());
        allUniforms.addAll(frag.getUniformSet().keySet());

        return allUniforms.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void delete(){
        nativepipeline.delete();
    }

    public void validate(){
        nativepipeline.validate();
    }

    @Override
    public void use(){
        nativepipeline.bind();
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
