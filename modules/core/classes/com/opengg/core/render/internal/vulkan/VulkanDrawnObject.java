package com.opengg.core.render.internal.vulkan;

import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.objects.DrawnObject;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipeline;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipelineCache;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.*;

public final class VulkanDrawnObject extends DrawnObject{
    private boolean hasNonEmptyBuffer = true;

    public VulkanDrawnObject(VertexArrayFormat format, IntBuffer index, Buffer... vertices) {
        this.format = format;
        if(vertices[0].capacity() != 0)
            generateGPUMemory(vertices, index);
        else
            hasNonEmptyBuffer = false;
    }

    public VulkanDrawnObject(VertexArrayFormat format, GraphicsBuffer indexBuffer, int indexCount, GraphicsBuffer... vertices) {
        super(format, indexBuffer, indexCount, vertices);
    }

    @Override
    public void render(){
        if(!hasNonEmptyBuffer) return;
        ShaderController.uploadModifiedDescriptorSets();
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindPipeline(VulkanPipelineCache.getPipeline(
                VulkanRenderer.getRenderer().getCurrentPipeline().getFormat().setVertexAssembly(new VulkanPipeline.InputAssemblyState(drawType))));
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindVertexBuffers(vertexBufferObjects);
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindIndexBuffer(indexBuffer);
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().drawVertexIndexed(this.elementCount, this.instanceCount, this.baseElement, this.baseVertex, 0);
    }

    public static int getVulkanInputAssembly(DrawnObject.DrawType type){
        return switch (type){
            case TRIANGLES -> VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
            case TRIANGLE_STRIP -> VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP;
            case TRIANGLE_FAN -> VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN;
            case POINTS -> VK_PRIMITIVE_TOPOLOGY_POINT_LIST;
            case LINES -> VK_PRIMITIVE_TOPOLOGY_LINE_LIST;
            case LINE_STRIP -> VK_PRIMITIVE_TOPOLOGY_LINE_STRIP;
        };
    }
}
