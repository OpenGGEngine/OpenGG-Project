package com.opengg.core.render.internal.vulkan;

import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanDescriptorPool {
    private final long descriptorPool;
    private final int availableUniforms, availableImages, availableSets;
    private int usedUniforms, usedImages, usedSets;

    public VulkanDescriptorPool(int uniformCount, int samplerCount, int setCount){
        availableImages = samplerCount;
        availableUniforms = uniformCount;
        availableSets = setCount;
        // We need to tell the API the number of max. requested descriptors per type
        VkDescriptorPoolSize.Buffer typeCounts = VkDescriptorPoolSize.calloc(2);

        typeCounts.get(0)
                .type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                .descriptorCount(uniformCount);

        typeCounts.get(1)
                .type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .descriptorCount(samplerCount);

        VkDescriptorPoolCreateInfo descriptorPoolInfo = VkDescriptorPoolCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                .pPoolSizes(typeCounts)
                .flags(VK_DESCRIPTOR_POOL_CREATE_FREE_DESCRIPTOR_SET_BIT)
                // Set the max. number of sets that can be requested
                // Requesting descriptors beyond maxSets will result in an error
                .maxSets(setCount);

        LongBuffer pDescriptorPool = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateDescriptorPool(VulkanRenderer.getRenderer().getDevice(), descriptorPoolInfo, null, pDescriptorPool));
        descriptorPool = pDescriptorPool.get(0);
        memFree(pDescriptorPool);
        descriptorPoolInfo.free();
        typeCounts.free();
    }

    public void reset(){
        VkUtil.catchVulkanException(vkResetDescriptorPool(VulkanRenderer.getRenderer().getDevice(), descriptorPool, 0));
    }

    public long getPool(){
        return descriptorPool;
    }

    public int getAvailableUniforms() {
        return availableUniforms;
    }

    public int getAvailableImages() {
        return availableImages;
    }

    public int getAvailableSets() {
        return availableSets;
    }

    public int getUsedUniforms() {
        return usedUniforms;
    }

    public int getUsedImages() {
        return usedImages;
    }

    public int getUsedSets() {
        return usedSets;
    }

    public void addUsed(int uniforms, int images, int set){
        usedUniforms += uniforms;
        usedImages += images;
        usedSets += set;
    }
}
