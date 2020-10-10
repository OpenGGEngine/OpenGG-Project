package com.opengg.core.render.internal.vulkan;

import com.opengg.core.math.Vector2i;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipeline;
import com.opengg.core.render.internal.vulkan.texture.VulkanImage;
import com.opengg.core.system.Allocator;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.List;
import java.util.stream.IntStream;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanCommandBuffer {
    private final VkCommandBuffer buffer;
    public VulkanCommandBuffer(VulkanCommandPool pool){
        var info = VkCommandBufferAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(pool.getPool())
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(1);
        PointerBuffer pCommandBuffer = memAllocPointer(1);
        VkUtil.catchVulkanException(vkAllocateCommandBuffers(VulkanRenderer.getRenderer().getDevice(), info, pCommandBuffer));
        long commandBuffer = pCommandBuffer.get(0);
        memFree(pCommandBuffer);
        info.free();
        this.buffer = new VkCommandBuffer(commandBuffer, VulkanRenderer.getRenderer().getDevice());
    }

    private VulkanCommandBuffer(long id){
        this.buffer = new VkCommandBuffer(id, VulkanRenderer.getRenderer().getDevice());
    }

    public void begin(){
        VkCommandBufferBeginInfo info = VkCommandBufferBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
        VkUtil.catchVulkanException(vkBeginCommandBuffer(buffer, info));
        info.free();
    }

    public void setViewport(Vector2i size){
        VkViewport.Buffer viewport = VkViewport.calloc(1)
                .width(size.x)
                .height(-size.y)
                .x(0)
                .y(size.y)
                .minDepth(0.0f)
                .maxDepth(1.0f);
        vkCmdSetViewport(buffer, 0, viewport);
        viewport.free();
    }

    public void setScissor(Vector2i size){
        VkRect2D.Buffer scissor = VkRect2D.calloc(1);
        scissor.extent().set(size.x, size.y);
        scissor.offset().set(0, 0);
        vkCmdSetScissor(buffer, 0, scissor);
        scissor.free();
    }

    public void setViewportScissor(Vector2i size){
        this.setViewport(size);
        this.setScissor(size);
    }

    public void startRenderPass(VulkanRenderPass.BeginInfo passInfo){
        vkCmdBeginRenderPass(buffer, passInfo.getInfo(), VK_SUBPASS_CONTENTS_INLINE);
    }

    public void endRenderPass(){
        vkCmdEndRenderPass(buffer);
    }

    public void bindPipeline(VulkanPipeline pipeline){
        if(VulkanRenderer.getRenderer().pipeline != null && pipeline.getFormat().equals(VulkanRenderer.getRenderer().pipeline.getFormat())) return;
        VulkanRenderer.getRenderer().pipeline = pipeline;
        vkCmdBindPipeline(buffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getPipeline());
    }

    public void bindDescriptorSets(VulkanPipeline pipeline, int firstSet, VulkanDescriptorSet... sets){
        LongBuffer descriptorSets = memAllocLong(sets.length);
        for(int i = 0; i < sets.length; i++){
            descriptorSets.put(i, sets[i].getDescriptorSet());
        }
        vkCmdBindDescriptorSets(buffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getLayout(), firstSet, descriptorSets, null);
        memFree(descriptorSets);
    }

    public void bindVertexBuffers(GraphicsBuffer... vertexBuffers){
        bindVertexBuffers(List.of(vertexBuffers));
    }

    public void bindVertexBuffers(List<GraphicsBuffer> vertexBuffers){
        LongBuffer pBuffers = Allocator.stackAllocLong(vertexBuffers.size());
        for(int i = 0; i < vertexBuffers.size(); i++) {
            pBuffers.put(i, ((VulkanBuffer) vertexBuffers.get(i)).getBuffer());
        }
        LongBuffer offsets = Allocator.stackAllocLong(vertexBuffers.size());
        for(int i = 0; i < vertexBuffers.size(); i++) {
            offsets.put(i, 0);
        }

        vkCmdBindVertexBuffers(buffer, 0, pBuffers, offsets);
        Allocator.popStack();
        Allocator.popStack();
    }

    public void bindIndexBuffer(GraphicsBuffer indexBuffer){
        vkCmdBindIndexBuffer(buffer, ((VulkanBuffer)indexBuffer).getBuffer(), 0, VK_INDEX_TYPE_UINT32);
    }

    public void drawVertex(int vertexCount, int instanceCount, int firstVertex, int firstInstance){
        vkCmdDraw(buffer, vertexCount, instanceCount, firstVertex, firstInstance);
    }

    public void drawVertexIndexed(int indexCount, int instanceCount, int firstIndex, int vertexOffset, int firstInstance){
        vkCmdDrawIndexed(buffer, indexCount, instanceCount, firstIndex, vertexOffset, firstInstance);
    }

    public void copyBuffer(VulkanBuffer src, VulkanBuffer target, int size){
        var sizeBuf = VkBufferCopy.calloc(1)
                .size(size);
        vkCmdCopyBuffer(buffer, src.getBuffer(), target.getBuffer(), sizeBuf);
        sizeBuf.free();
    }

    public void copyBufferToImage(VulkanBuffer src, VulkanImage target, VkBufferImageCopy.Buffer copyData){
        vkCmdCopyBufferToImage(buffer, src.getBuffer(), target.getID(), VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, copyData);
    }

    public void end(){
        VkUtil.catchVulkanException(vkEndCommandBuffer(buffer));
    }

    public void submit(){
        VkSubmitInfo submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
        PointerBuffer pCommandBuffers = memAllocPointer(1)
                .put(buffer)
                .flip();
        submitInfo.pCommandBuffers(pCommandBuffers);
        VkUtil.catchVulkanException(vkQueueSubmit(VulkanRenderer.getRenderer().getQueue(), submitInfo, VK_NULL_HANDLE));
        memFree(pCommandBuffers);
        submitInfo.free();
    }

    public void submitAndWait(){
        submit();
        vkQueueWaitIdle(VulkanRenderer.getRenderer().getQueue());
    }

    public VkCommandBuffer getBuffer(){
        return buffer;
    }

    public static VulkanCommandBuffer[] allocate(VulkanCommandPool pool, int amount){
        var allocateInfo = VkCommandBufferAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(pool.getPool())
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(amount);

        PointerBuffer pCommandBuffer = memAllocPointer(amount);
        int err = vkAllocateCommandBuffers(VulkanRenderer.getRenderer().getDevice(), allocateInfo, pCommandBuffer);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to allocate render command buffer: " + VkUtil.translateVulkanResult(err));
        }
        var buffers = IntStream.range(0, amount).mapToObj(i -> new VulkanCommandBuffer(pCommandBuffer.get(i))).toArray(VulkanCommandBuffer[]::new);
        memFree(pCommandBuffer);
        allocateInfo.free();

        return buffers;
    }

}
