/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.internal.opengl.shader.OpenGLShaderProgram;
import com.opengg.core.render.internal.vulkan.shader.VulkanShaderStage;
import com.opengg.core.render.shader.ShaderController.Uniform;
import com.opengg.core.render.shader.ggsl.ShaderFile;

import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface ShaderProgram{
    enum ShaderType{
        VERTEX, TESS_CONTROL, TESS_EVAL, GEOMETRY, FRAGMENT, UTIL;
        
        public static ShaderType fromFileType(ShaderFile.ShaderFileType type){
            ShaderType ntype;

            return switch (type) {
                case FRAG -> ShaderType.FRAGMENT;
                case VERT -> ShaderType.VERTEX;
                case TESSCONTROL -> ShaderType.TESS_CONTROL;
                case TESSEVAL -> ShaderType.TESS_EVAL;
                case GEOM -> ShaderType.GEOMETRY;
                default -> throw new ShaderException("Attempted to load utility shader as GLSL");
            };
        }
    }

    static ShaderProgram create(ShaderType type, String source, String name, List<Uniform> uniforms){
        return switch (OpenGG.getInitOptions().getWindowOptions().renderer){
            case OPENGL -> new OpenGLShaderProgram(type, source, name);
            case VULKAN -> new VulkanShaderStage(type, source, name, uniforms);
        };
    }

    static ShaderProgram createFromBinary(ShaderType type, ByteBuffer data, String name){
        return new OpenGLShaderProgram(type, data, name);
    }

    List<Uniform> getUniforms();

    ByteBuffer getProgramBinary();

    String getName();

    ShaderType getType();
}
