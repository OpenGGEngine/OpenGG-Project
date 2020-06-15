package com.opengg.core.render.internal.vulkan.shader;

import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.internal.vulkan.*;
import com.opengg.core.render.shader.VertexArrayFormat;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanPipeline {
    private final VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo;
    private final VulkanPipelineFormat format;
    private final long layout;
    private final long pipeline;


    public VulkanPipeline(VulkanPipelineFormat format) {
        this.format = format;

        LongBuffer pDescriptorSetLayout = memAllocLong(format.descriptorSetLayouts.length);
        for(int i = 0; i < format.descriptorSetLayouts.length; i++){
            pDescriptorSetLayout.put(i, format.descriptorSetLayouts[i].generateStruct());
        }
        var pPipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pSetLayouts(pDescriptorSetLayout);

        LongBuffer pPipelineLayout = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreatePipelineLayout(VulkanRenderer.getRenderer().getDevice(), pPipelineLayoutCreateInfo, null, pPipelineLayout));
        layout = pPipelineLayout.get(0);
        memFree(pPipelineLayout);
        pPipelineLayoutCreateInfo.free();
        memFree(pDescriptorSetLayout);


        IntBuffer pDynamicStates = memAllocInt(2);
        pDynamicStates.put(VK_DYNAMIC_STATE_VIEWPORT).put(VK_DYNAMIC_STATE_SCISSOR).flip();
        var dynamicState = VkPipelineDynamicStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(pDynamicStates);

        var viewportState = VkPipelineViewportStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                .viewportCount(1)
                .scissorCount(1);

        pipelineCreateInfo = VkGraphicsPipelineCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                .layout(layout)
                .renderPass(format.renderPass.getPass())
                .pVertexInputState(format.verts.createStruct())
                .pInputAssemblyState(format.vertAssembly.createStruct())
                .pRasterizationState(format.rasterizer.createStruct())
                .pColorBlendState(format.blend.createStruct())
                .pMultisampleState(format.sampler.createStruct())
                .pViewportState(viewportState)
                .pDepthStencilState(format.stencil.createStruct())
                .pStages(format.shaders.getPipelineCreateInfo())
                .pDynamicState(dynamicState);

        LongBuffer pPipelines = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateGraphicsPipelines(VulkanRenderer.getRenderer().getDevice(), VK_NULL_HANDLE, pipelineCreateInfo, null, pPipelines));
        pipeline = pPipelines.get(0);
        memFree(pPipelines);
    }

    public long getPipeline() {
        return pipeline;
    }

    public long getLayout() {
        return layout;
    }

    public VulkanPipelineFormat getFormat() {
        return format;
    }

    public record VertexInputState(VertexArrayFormat format) {
        public VkPipelineVertexInputStateCreateInfo createStruct(){
            var vulkanFormat = new VulkanVertexFormat(format);
            return VkPipelineVertexInputStateCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                    .pVertexBindingDescriptions(vulkanFormat.getBindings())
                    .pVertexAttributeDescriptions(vulkanFormat.getAttribs());
        }
    }

    public record InputAssemblyState(DrawnObject.DrawType drawType) {
        public VkPipelineInputAssemblyStateCreateInfo createStruct(){
            return VkPipelineInputAssemblyStateCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                    .topology(VulkanDrawnObject.getVulkanInputAssembly(drawType));
        }
    }

    public record RasterizationState(int polygonMode, int cullMode, int cullDirection) {
        public VkPipelineRasterizationStateCreateInfo createStruct(){
            return VkPipelineRasterizationStateCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                    .polygonMode(polygonMode)
                    .cullMode(cullMode)
                    .frontFace(cullDirection)
                    .lineWidth(1.0f);
        }
    }

    public record MultisampleState(int multisample) {
        public VkPipelineMultisampleStateCreateInfo createStruct(){
            return VkPipelineMultisampleStateCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                    .rasterizationSamples(multisample);
        }
    }

    public record ColorBlendState(int mask) {
        public VkPipelineColorBlendStateCreateInfo createStruct(){
            VkPipelineColorBlendAttachmentState.Buffer colorWriteMask = VkPipelineColorBlendAttachmentState.calloc(1)
                    .colorWriteMask(mask);
            return VkPipelineColorBlendStateCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                    .pAttachments(colorWriteMask);
        }
    }

    public record DepthStencilState(boolean testDepth, boolean writeDepth, int testOp) {
        public VkPipelineDepthStencilStateCreateInfo createStruct(){
            var depthStencilState = VkPipelineDepthStencilStateCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                    .depthTestEnable(testDepth)
                    .depthWriteEnable(writeDepth)
                    .depthCompareOp(testOp);
            depthStencilState.back()
                    .failOp(VK_STENCIL_OP_REPLACE)
                    .passOp(VK_STENCIL_OP_KEEP);
            depthStencilState.back()
                    .compareOp(VK_COMPARE_OP_ALWAYS);
            depthStencilState.front(depthStencilState.back());
            return depthStencilState;
        }
    }
}
