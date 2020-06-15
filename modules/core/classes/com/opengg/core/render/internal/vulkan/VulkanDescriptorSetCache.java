package com.opengg.core.render.internal.vulkan;

import java.util.ArrayList;
import java.util.List;

public class VulkanDescriptorSetCache {
    private final int frames;
    private final List<List<VulkanDescriptorSet>> sets;

    public VulkanDescriptorSetCache(int frames){
        this.frames = frames;
        sets = new ArrayList<>();
        createNewCache();
    }

    public void createNewCache(){
        sets.add(new ArrayList<>());
        if(sets.size() > frames){
            sets.get(0).forEach(VulkanDescriptorSet::free);
            sets.remove(0);
        }
    }

    public void cache(VulkanDescriptorSet set){
        sets.get(sets.size()-1).add(set);
    }
}
