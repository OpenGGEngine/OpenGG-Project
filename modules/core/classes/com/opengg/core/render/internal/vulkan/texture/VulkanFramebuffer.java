package com.opengg.core.render.internal.vulkan.texture;

import com.opengg.core.math.Vector2i;
import com.opengg.core.render.internal.vulkan.VkUtil;
import com.opengg.core.render.internal.vulkan.VulkanAttachment;
import com.opengg.core.render.internal.vulkan.VulkanRenderPass;
import com.opengg.core.render.internal.vulkan.VulkanRenderer;
import com.opengg.core.render.texture.Framebuffer;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanFramebuffer{
    private final long framebuffer;

    public VulkanFramebuffer(List<VulkanImage.View> attachments, Vector2i size, VulkanRenderPass renderPass) {
        LongBuffer attachmentsBuf = memAllocLong(attachments.size());
        for(int i = 0; i < attachments.size(); i++){
            attachmentsBuf.put(i, attachments.get(i).getView());
        }
        VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .pAttachments(attachmentsBuf)
                .width(size.x)
                .height(size.y)
                .layers(1)
                .renderPass(renderPass.getPass());

        LongBuffer pFramebuffer = memAllocLong(1);

        VkUtil.catchVulkanException(vkCreateFramebuffer(VulkanRenderer.getRenderer().getDevice(), fci, null, pFramebuffer));
        framebuffer = pFramebuffer.get(0);

        memFree(attachmentsBuf);
        memFree(pFramebuffer);
        fci.free();
    }

    public void destroy(){
        vkDestroyFramebuffer(VulkanRenderer.getRenderer().getDevice(), framebuffer, null);
    }

    public long getFramebuffer() {
        return framebuffer;
    }
}
