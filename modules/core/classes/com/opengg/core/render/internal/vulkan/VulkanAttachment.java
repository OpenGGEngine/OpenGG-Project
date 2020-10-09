package com.opengg.core.render.internal.vulkan;

import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanAttachment {
    private final VkAttachmentDescription description;
    public VulkanAttachment(int format, int samples, int loadOp, int storeOp, int usage){
        description = VkAttachmentDescription.calloc()
                .format(format)
                .samples(samples)
                .loadOp(loadOp)
                .storeOp(storeOp)
                .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                .finalLayout(usage);
    }

    public VkAttachmentDescription getDescription() {
        return description;
    }

    public static class Reference{
        private final VkAttachmentReference reference;

        public Reference(int id, int layout) {
            reference = VkAttachmentReference.calloc()
                    .attachment(id)
                    .layout(layout);
        }

        public VkAttachmentReference getReference() {
            return reference;
        }
    }
}
