package com.opengg.core.render.internal.vulkan.shader;

import java.util.HashMap;

public class VulkanPipelineCache {
    private static HashMap<VulkanPipelineFormat, VulkanPipeline> pipelineCache;

    public static void initialize(){
        pipelineCache = new HashMap<>();
    }

    public static VulkanPipeline getPipeline(VulkanPipelineFormat format){
        if(pipelineCache.containsKey(format)) return pipelineCache.get(format);
        var pipeline = new VulkanPipeline(format);
        pipelineCache.put(format, pipeline);

        return pipeline;
    }
}
