package com.opengg.core.render.internal.vulkan;

import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateCommandPool;

public class VulkanCommandPool {
    private final long commandPool;
    public VulkanCommandPool(int queueNodeIndex, int flags){
        VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                .queueFamilyIndex(queueNodeIndex)
                .flags(flags);
        LongBuffer pCmdPool = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateCommandPool(VulkanRenderer.getRenderer().getDevice(), cmdPoolInfo, null, pCmdPool));
        commandPool = pCmdPool.get(0);
        cmdPoolInfo.free();
        memFree(pCmdPool);
    }

    public long getPool(){
        return commandPool;
    }
}
