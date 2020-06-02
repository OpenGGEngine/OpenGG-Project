package com.opengg.core.render.internal.vulkan;

import com.opengg.core.render.shader.ShaderController;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public record VulkanDescriptorSetLayout(List<Descriptor> bindingList) {
    public long generateStruct() {
        VkDescriptorSetLayoutBinding.Buffer layoutBinding = VkDescriptorSetLayoutBinding.calloc(bindingList.size());
        for(int i = 0; i < bindingList.size(); i++){
            var binding = bindingList.get(i);
            layoutBinding.get(i).binding(binding.binding)
                    .descriptorType(toVulkanType(binding.type))
                    .descriptorCount(1)
                    .stageFlags(binding.shader);
        }

        VkDescriptorSetLayoutCreateInfo descriptorLayout = VkDescriptorSetLayoutCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
                .pBindings(layoutBinding);

        LongBuffer pDescriptorSetLayout = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateDescriptorSetLayout(VulkanRenderer.getRenderer().getDevice(), descriptorLayout, null, pDescriptorSetLayout));
        long descriptorSetLayout = pDescriptorSetLayout.get(0);
        memFree(pDescriptorSetLayout);
        descriptorLayout.free();
        layoutBinding.free();
        return descriptorSetLayout;
    }

    public record Descriptor(int binding, ShaderController.DescriptorType type, int shader){}

    public int getImageCount(){
        return (int) bindingList.stream().filter(d -> d.type.equals(ShaderController.DescriptorType.COMBINED_TEXTURE_SAMPLER))
                .count();
    }

    public int getBufferCount(){
        return (int) bindingList.stream().filter(d -> d.type.equals(ShaderController.DescriptorType.UNIFORM_BUFFER))
                .count();
    }

    private static int toVulkanType(ShaderController.DescriptorType type){
        return switch (type){
            case UNIFORM_BUFFER: yield VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
            case COMBINED_TEXTURE_SAMPLER: yield VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
        };
    }
}
