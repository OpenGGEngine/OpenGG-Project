package com.opengg.core.render.internal.vulkan;

import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanDrawnObject extends DrawnObject{
    public VulkanDrawnObject(FloatBuffer... vertices) {
        super(vertices);
    }

    public VulkanDrawnObject(VertexArrayFormat format, FloatBuffer... vertices) {
        super(format, vertices);
    }

    public VulkanDrawnObject(IntBuffer index, FloatBuffer... vertices) {
        super(index, vertices);
    }

    public VulkanDrawnObject(VertexArrayFormat format, IntBuffer index, FloatBuffer... vertices) {
        super(format, index, vertices);
    }

    @Override
    public void render(){
        ShaderController.uploadNeededSetsForCurrent();
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindVertexBuffers(vertexBufferObjects);
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindIndexBuffer(indexBuffer);
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().drawVertexIndexed(this.elementCount, this.instanceCount, 0, this.baseVertex, 0);
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
