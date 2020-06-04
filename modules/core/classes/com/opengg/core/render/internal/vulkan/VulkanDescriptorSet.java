package com.opengg.core.render.internal.vulkan;

import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.render.internal.vulkan.texture.VulkanImage;
import com.opengg.core.render.internal.vulkan.texture.VulkanSampler;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.system.NativeResource;
import com.opengg.core.system.NativeResourceManager;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;

public class VulkanDescriptorSet{
    private final long descriptorSet;
    private final VulkanDescriptorPool pool;
    private final VulkanDescriptorSetLayout layout;
    private final List<VulkanBuffer> buffers = new ArrayList<>();

    public VulkanDescriptorSet(VulkanDescriptorSetLayout layout){
        this(layout, VulkanRenderer.getRenderer().getDescriptorPoolPool().findWithCapacity(layout.getBufferCount(), layout.getImageCount()));
    }

    private VulkanDescriptorSet(VulkanDescriptorSetLayout layout, VulkanDescriptorPool pool){
        this.pool = pool;
        this.layout = layout;

        PerformanceManager.registerDescriptorSet();

        LongBuffer pDescriptorSetLayout = memAllocLong(1);
        pDescriptorSetLayout.put(0, layout.generateStruct());
        VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                .descriptorPool(pool.getPool())
                .pSetLayouts(pDescriptorSetLayout);

        LongBuffer pDescriptorSet = memAllocLong(1);
        VkUtil.processVulkanException(vkAllocateDescriptorSets(VulkanRenderer.getRenderer().getDevice(), allocInfo, pDescriptorSet), () -> System.out.println(layout));
        descriptorSet = pDescriptorSet.get(0);
        memFree(pDescriptorSet);
        allocInfo.free();
        memFree(pDescriptorSetLayout);

        pool.addUsed(layout.getBufferCount(), layout.getImageCount(), 1);

        VulkanRenderer.getRenderer().getDescriptorSetCache().cache(this);
    }

    public void setDescriptorSetContents(VulkanBuffer buffer, long offset, int binding){
        VkDescriptorBufferInfo.Buffer descriptor = VkDescriptorBufferInfo.calloc(1)
                .buffer(buffer.getBuffer())
                .range(buffer.getSize())
                .offset(offset);

        VkWriteDescriptorSet.Buffer writeDescriptorSet = VkWriteDescriptorSet.calloc(1);
        writeDescriptorSet.get(0)
                .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                .dstSet(descriptorSet)
                .descriptorCount(1)
                .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                .pBufferInfo(descriptor)
                .dstBinding(binding);

        vkUpdateDescriptorSets(VulkanRenderer.getRenderer().getDevice(), writeDescriptorSet, null);

        buffers.add(buffer);
        writeDescriptorSet.free();
        descriptor.free();
    }

    public void setDescriptorSetContents(VulkanImage.View image, VulkanSampler sampler, int binding){
        VkDescriptorImageInfo.Buffer descriptor = VkDescriptorImageInfo.calloc(1)
                .sampler(sampler.getSampler())
                .imageView(image.getView())
                .imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

        VkWriteDescriptorSet.Buffer writeDescriptorSet = VkWriteDescriptorSet.calloc(1);
        writeDescriptorSet.get(0)
                .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                .dstSet(descriptorSet)
                .descriptorCount(1)
                .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .pImageInfo(descriptor)
                .dstBinding(binding);

        vkUpdateDescriptorSets(VulkanRenderer.getRenderer().getDevice(), writeDescriptorSet, null);

        writeDescriptorSet.free();
        descriptor.free();
    }

    public long getDescriptorSet() {
        return descriptorSet;
    }

    public void free() {
        VkUtil.catchVulkanException(vkFreeDescriptorSets(VulkanRenderer.getRenderer().getDevice(), pool.getPool(), descriptorSet));
        pool.addUsed(-layout.getBufferCount(), -layout.getImageCount(), -1);
        buffers.forEach(VulkanBuffer::destroy);
    }
}
