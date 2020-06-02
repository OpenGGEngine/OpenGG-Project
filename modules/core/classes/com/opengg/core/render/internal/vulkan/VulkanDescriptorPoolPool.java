package com.opengg.core.render.internal.vulkan;

import java.util.ArrayList;
import java.util.List;

public class VulkanDescriptorPoolPool {
    private final int POOL_IMAGE_CAPACITY = 100, POOL_UNIFORM_CAPACITY = 100, SET_CAPACITY = 200;
    private List<VulkanDescriptorPool> pools = new ArrayList<>();

    public VulkanDescriptorPool findWithCapacity(int buffers, int textures){
        for(var pool : pools){
            if(pool.getAvailableImages() - pool.getUsedImages() > textures
                    && pool.getAvailableUniforms() - pool.getUsedUniforms() > buffers
                    && pool.getAvailableSets() - pool.getUsedSets() > 1) return pool;
        }

        var pool = createNew();
        pools.add(pool);
        return pool;
    }

    public VulkanDescriptorPool createNew(){
        return new VulkanDescriptorPool(POOL_UNIFORM_CAPACITY, POOL_IMAGE_CAPACITY, SET_CAPACITY);
    }
}
