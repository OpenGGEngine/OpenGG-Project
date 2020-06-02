/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.shader.OpenGLShaderPipeline;
import com.opengg.core.render.internal.vulkan.shader.VulkanShaderPipeline;

/**
 *
 * @author Javier
 */
public interface ShaderPipeline{
    static ShaderPipeline create(ShaderProgram vertex, ShaderProgram fragment){
        return switch (RenderEngine.getRendererType()) {
            case OPENGL -> new OpenGLShaderPipeline(vertex, null, null, null, fragment);
            case VULKAN -> new VulkanShaderPipeline(vertex, null, null, null, fragment);
        };
    }

    static ShaderPipeline create(ShaderProgram vertex, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram fragment){
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLShaderPipeline(vertex, tesc, tese, geom, fragment);
            case VULKAN -> new VulkanShaderPipeline(vertex, tesc, tese, geom, fragment);
        };
    }

    void delete();

    void validate();

    void use();

    void unbind();

    ShaderProgram getShader(ShaderProgram.ShaderType type);
}
