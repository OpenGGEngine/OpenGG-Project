package com.opengg.core.render.internal.opengl;

import com.opengg.core.render.internal.opengl.shader.OpenGLVertexArrayObject;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipeline;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipelineFormat;
import com.opengg.core.render.shader.VertexArrayFormat;

import java.util.HashMap;

public class OpenGLVAOManager {
    private static HashMap<VertexArrayFormat, OpenGLVertexArrayObject> pipelineCache;

    public static void initialize(){
        pipelineCache = new HashMap<>();
    }

    public static OpenGLVertexArrayObject getVAO(VertexArrayFormat format){
        if(pipelineCache.containsKey(format)) return pipelineCache.get(format);
        var pipeline = new OpenGLVertexArrayObject(format);
        pipelineCache.put(format, pipeline);

        return pipeline;
    }
}
