/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.GGInfo;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.internal.opengl.shader.OpenGLVertexArrayObject;
import com.opengg.core.render.internal.vulkan.shader.VulkanVertexFormat;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL43.*;

/**
 *
 * @author Javier
 */
public interface VertexArrayObject {
    static VertexArrayObject create(VertexArrayFormat format){
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLVertexArrayObject(format);
            case VULKAN -> new VulkanVertexFormat(format);

        };
    }

    VertexArrayFormat getFormat();

    void applyFormat(List<GraphicsBuffer> buffers);

}
