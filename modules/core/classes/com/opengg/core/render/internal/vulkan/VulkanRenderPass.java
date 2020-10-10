package com.opengg.core.render.internal.vulkan;

import com.opengg.core.math.Vector2i;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.internal.vulkan.texture.VulkanFramebuffer;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanRenderPass {

    private final long renderPass;

    public VulkanRenderPass(List<VulkanAttachment> attachments, List<Subpass> subpasses){
        var attachmentsBuffer = VkAttachmentDescription.calloc(attachments.size());
        for(int i = 0; i < attachments.size(); i++){
            attachmentsBuffer.get(i).set(attachments.get(i).getDescription());
        }

        var subpassBuffer = VkSubpassDescription.calloc(subpasses.size());
        for(int i = 0; i < subpasses.size(); i++){
            subpassBuffer.get(i).set(subpasses.get(i).subpass.get(0));
        }

        VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                .pAttachments(attachmentsBuffer)
                .pSubpasses(subpassBuffer);


        LongBuffer pRenderPass = memAllocLong(1);
        int err = vkCreateRenderPass(VulkanRenderer.getRenderer().getDevice(), renderPassInfo, null, pRenderPass);
        renderPass = pRenderPass.get(0);
        memFree(pRenderPass);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create clear render pass: " + VkUtil.translateVulkanResult(err));
        }

    }

    public long getPass() {
        return renderPass;
    }

    public BeginInfo generateInfo(Vector3f clearColor, VulkanFramebuffer buffer, Vector2i startRenderArea, Vector2i endRenderArea){
        VkClearValue.Buffer clearValues = VkClearValue.calloc(2);
        clearValues.get(0).color()
                .float32(0, clearColor.x)
                .float32(1, clearColor.y)
                .float32(2, clearColor.z)
                .float32(3, 1.0f);
        clearValues.get(1).depthStencil().depth(1.0f).stencil(0);
        VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .renderPass(renderPass)
                .pClearValues(clearValues);
        VkRect2D renderArea = renderPassBeginInfo.renderArea();
        renderArea.offset().set(startRenderArea.x, startRenderArea.y);
        renderArea.extent().set(endRenderArea.x, endRenderArea.y);
        renderPassBeginInfo.framebuffer(buffer.getFramebuffer());

        return new BeginInfo(renderPassBeginInfo);
    }

    public static class BeginInfo{
        private final VkRenderPassBeginInfo info;

        BeginInfo(VkRenderPassBeginInfo info){
            this.info = info;
        }

        public VkRenderPassBeginInfo getInfo() {
            return info;
        }
    }

    public static class Subpass{
        private final VkSubpassDescription.Buffer subpass;

        public Subpass(int bindPoint, List<VulkanAttachment.Reference> colorAttachments, VulkanAttachment.Reference depthAttachment){
            var colorAttachmentBuffer = VkAttachmentReference.calloc(colorAttachments.size());
            for(int i = 0; i < colorAttachments.size(); i++){
                colorAttachmentBuffer.get(i).set(colorAttachments.get(i).getReference());
            }
            subpass = VkSubpassDescription.calloc(1)
                    .pipelineBindPoint(bindPoint)
                    .colorAttachmentCount(colorAttachments.size())
                    .pColorAttachments(colorAttachmentBuffer) // <- only color attachment
                    .pDepthStencilAttachment(depthAttachment.getReference()) // <- and depth-stencil
                    ;
        }
    }
}
