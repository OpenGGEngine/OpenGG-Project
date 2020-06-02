package com.opengg.core.render.internal.vulkan.shader;

import com.opengg.core.render.internal.vulkan.VulkanDescriptorSetLayout;
import com.opengg.core.render.internal.vulkan.VulkanRenderPass;

import java.util.Arrays;

public class VulkanPipelineFormat {
    final VulkanPipeline.VertexInputState verts;
    final VulkanPipeline.InputAssemblyState vertAssembly;
    final VulkanPipeline.RasterizationState rasterizer;
    final VulkanPipeline.MultisampleState sampler;
    final VulkanPipeline.ColorBlendState blend;
    final VulkanShaderPipeline shaders;
    final VulkanDescriptorSetLayout[] descriptorSetLayouts;
    final VulkanPipeline.DepthStencilState stencil;
    final VulkanRenderPass renderPass;


    public VulkanPipelineFormat(VulkanPipeline.VertexInputState verts,
                                VulkanPipeline.InputAssemblyState vertAssembly,
                                VulkanPipeline.RasterizationState rasterizer,
                                VulkanPipeline.MultisampleState sampler,
                                VulkanPipeline.ColorBlendState blend,
                                VulkanPipeline.DepthStencilState stencil,
                                VulkanShaderPipeline shaders,
                                VulkanRenderPass renderPass){

        this.stencil = stencil;
        this.verts = verts;
        this.vertAssembly = vertAssembly;
        this.rasterizer = rasterizer;
        this.sampler = sampler;
        this.blend = blend;
        this.shaders = shaders;
        this.descriptorSetLayouts = shaders.getUsedLayouts();
        this.renderPass = renderPass;
    }

    public VulkanPipelineFormat setVertexInput(VulkanPipeline.VertexInputState verts) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setVertexAssembly(VulkanPipeline.InputAssemblyState vertAssembly) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setRasterizer(VulkanPipeline.RasterizationState rasterizer) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setSampler(VulkanPipeline.MultisampleState sampler) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setBlend(VulkanPipeline.ColorBlendState blend) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setShaders(VulkanShaderPipeline shaders) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setDescriptorSetLayouts(VulkanDescriptorSetLayout... descriptorSetLayouts) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setDepthStencil(VulkanPipeline.DepthStencilState stencil) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    public VulkanPipelineFormat setRenderPass(VulkanRenderPass renderPass) {
        return new VulkanPipelineFormat(verts, vertAssembly, rasterizer, sampler, blend, stencil, shaders, renderPass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VulkanPipelineFormat that = (VulkanPipelineFormat) o;

        if (!verts.equals(that.verts)) return false;
        if (!vertAssembly.equals(that.vertAssembly)) return false;
        if (!rasterizer.equals(that.rasterizer)) return false;
        if (!sampler.equals(that.sampler)) return false;
        if (!blend.equals(that.blend)) return false;
        if (!shaders.equals(that.shaders)) return false;
        if (!Arrays.equals(descriptorSetLayouts, that.descriptorSetLayouts)) return false;
        return stencil.equals(that.stencil);
    }

    @Override
    public int hashCode() {
        int result = 69420;
        result = 31 * result + verts.hashCode();
        result = 31 * result + vertAssembly.hashCode();
        result = 31 * result + rasterizer.hashCode();
        result = 31 * result + sampler.hashCode();
        result = 31 * result + blend.hashCode();
        result = 31 * result + shaders.hashCode();
        result = 31 * result + Arrays.hashCode(descriptorSetLayouts);
        result = 31 * result + stencil.hashCode();
        return result;
    }
}
