package com.opengg.core.render.internal.vulkan.texture;

import com.opengg.core.render.internal.vulkan.VkUtil;
import com.opengg.core.render.internal.vulkan.VulkanRenderer;
import com.opengg.core.render.texture.Texture;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanSampler {
    private final long samplerId;

    public VulkanSampler(Texture.TextureConfig config){
        VkSamplerCreateInfo info = VkSamplerCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                .minFilter(getVulkanFilterType(config.minFilter()))
                .magFilter(getVulkanFilterType(config.maxFilter()))
                .addressModeU(getVulkanWrapType(config.wrapTypeR()))
                .addressModeV(getVulkanWrapType(config.wrapTypeS()))
                .addressModeW(getVulkanWrapType(config.wrapTypeT()))
                .anisotropyEnable(false)//config.isAnisotropic())
                .borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
                .maxAnisotropy(1)//config.isAnisotropic() ? 16 : 1)
                .unnormalizedCoordinates(false)
                .compareEnable(false)
                .compareEnable(true)
                .mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
                .mipLodBias(0.0f)
                .minLod(0.0f)
                .maxLod(1.0f);

        long[] sampler = new long[1];
        VkUtil.catchVulkanException(vkCreateSampler(VulkanRenderer.getRenderer().getDevice(), info, null, sampler));
        samplerId = sampler[0];
        info.free();
    }


    public long getSampler() {
        return samplerId;
    }

    public int getVulkanWrapType(Texture.WrapType type){
        return switch (type){
            case REPEAT -> VK_SAMPLER_ADDRESS_MODE_REPEAT;
            case REPEAT_MIRRORED -> VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT;
            case CLAMP_BORDER -> VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER;
            case CLAMP_EDGE -> VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
        };
    }

    public int getVulkanFilterType(Texture.FilterType type){
        return switch (type){
            case NEAREST -> VK_FILTER_NEAREST;
            case LINEAR -> VK_FILTER_LINEAR;
        };
    }
}
