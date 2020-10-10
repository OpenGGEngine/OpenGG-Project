package com.opengg.core.render.internal.vulkan.shader;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.render.internal.vulkan.VkUtil;
import com.opengg.core.render.internal.vulkan.VulkanDescriptorSetLayout;
import com.opengg.core.render.internal.vulkan.VulkanRenderer;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.ShaderProgram;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanShaderStage implements ShaderProgram {
    private final String name;
    private ShaderType type;
    private final VkPipelineShaderStageCreateInfo shaderStageInfo;
    private List<Tuple<Integer, VulkanDescriptorSetLayout>> descriptorSets;
    private List<ShaderController.Uniform> uniforms;
    private final ByteBuffer spirv;

    public VulkanShaderStage(ShaderType type, String source, String name, List<ShaderController.Uniform> uniforms) {
        this.name = name;
        this.type = type;
        shaderStageInfo = VkPipelineShaderStageCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(getVulkanStageFromShaderType(type))
                .module(getShaderModule(spirv = VkUtil.glslToSpirv(name, source, getVulkanStageFromShaderType(type))))
                .pName(memUTF8("main"));
        this.uniforms = uniforms;
        descriptorSets = generateSetsFromUniforms(uniforms);
    }

    public VulkanShaderStage(ShaderType type, ByteBuffer source, String name) {
        this.name = name;
        shaderStageInfo = VkPipelineShaderStageCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(getVulkanStageFromShaderType(type))
                .module(getShaderModule(spirv = source))
                .pName(memUTF8("main"));
    }

    private List<Tuple<Integer, VulkanDescriptorSetLayout>> generateSetsFromUniforms(List<ShaderController.Uniform> uniforms){
        var knownTypes = new HashMap<ShaderController.DescriptorPosition, ShaderController.DescriptorType>();
        for(var uniform : uniforms){
            knownTypes.put(uniform.position().descriptor(), uniform.type());
        }

        var layouts = new HashMap<Integer, VulkanDescriptorSetLayout>();
        for(var uniform : uniforms){
            if(layouts.containsKey(uniform.position().descriptor().set())) continue;
            var bindings = new ArrayList<VulkanDescriptorSetLayout.Descriptor>();
            for(var uniformm : uniforms){
                if(uniformm.position().descriptor().set() == uniform.position().descriptor().set() &&
                        bindings.stream().noneMatch(b -> b.binding() == uniformm.position().descriptor().binding())){
                    bindings.add(new VulkanDescriptorSetLayout.Descriptor(
                            uniformm.position().descriptor().binding(), uniformm.type(), getVulkanStageFromShaderType(type)));
                }
            }
            layouts.put(uniform.position().descriptor().set(), new VulkanDescriptorSetLayout(bindings));
        }

        return layouts.entrySet().stream().map(e -> Tuple.of(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    private static int getVulkanStageFromShaderType(ShaderType type){
        return switch (type){
            case VERTEX -> VK_SHADER_STAGE_VERTEX_BIT;
            case GEOMETRY -> VK_SHADER_STAGE_GEOMETRY_BIT;
            case FRAGMENT -> VK_SHADER_STAGE_FRAGMENT_BIT;
            case TESS_CONTROL -> VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT;
            case TESS_EVAL -> VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT;
            case UTIL -> 0;
        };
    }

    private long getShaderModule(ByteBuffer shaderCode){
        try {
            VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pCode(shaderCode);
            LongBuffer pShaderModule = memAllocLong(1);
            VkUtil.catchVulkanException(vkCreateShaderModule(VulkanRenderer.getRenderer().getDevice(), moduleCreateInfo, null, pShaderModule));
            long shaderModule = pShaderModule.get(0);
            memFree(pShaderModule);
            return shaderModule;
        } catch (ShaderException e) {
            throw new ShaderException("Exception compiling " + name + ": "  + e.getMessage(), e);
        }
    }

    @Override
    public void bindFragmentDataLocation(int number, CharSequence name) {

    }

    @Override
    public void enableVertexAttribute(int location) {

    }

    @Override
    public void disableVertexAttribute(int location) {

    }

    @Override
    public void setUniform(int location, int value) {

    }

    @Override
    public void setUniform(int location, boolean value) {

    }

    @Override
    public void setUniform(int location, float value) {

    }

    @Override
    public void setUniform(int location, Vector2f value) {

    }

    @Override
    public void setUniform(int location, Vector3f value) {

    }

    @Override
    public void setUniform(int location, Matrix4f value) {

    }

    @Override
    public void setUniform(int location, Matrix4f[] matrices) {

    }

    @Override
    public void setUniformBlockIndex(int bind, String name) {

    }

    @Override
    public List<ShaderController.Uniform> getUniforms() {
        return uniforms;
    }

    @Override
    public ByteBuffer getProgramBinary() {
        return spirv;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void checkStatus() {

    }

    @Override
    public ShaderType getType() {
        return type;
    }

    public VkPipelineShaderStageCreateInfo getShaderStageInfo(){
        return shaderStageInfo;
    }

    public List<Tuple<Integer, VulkanDescriptorSetLayout>> getDescriptorSets() {
        return descriptorSets;
    }

    public static VkPipelineShaderStageCreateInfo.Buffer combineForUse(List<VulkanShaderStage> shaders){
        VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(shaders.size());
        for(int i = 0; i < shaders.size(); i++){
            shaderStages.get(i).set(shaders.get(i).getShaderStageInfo());
        }

        return shaderStages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VulkanShaderStage that = (VulkanShaderStage) o;

        if (!name.equals(that.name)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VulkanShaderStage{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
