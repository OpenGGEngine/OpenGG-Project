package com.opengg.core.render.internal.vulkan.shader;

import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayBinding;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanVertexFormat implements VertexArrayObject {
    private VertexArrayFormat format;
    private VkVertexInputAttributeDescription.Buffer attribs;
    private VkVertexInputBindingDescription.Buffer bindings;

    public VulkanVertexFormat(VertexArrayFormat format) {
        generateBindingPoints(format);
    }

    private void generateBindingPoints(VertexArrayFormat format) {
        var bindingDescriptors = VkVertexInputBindingDescription.calloc(format.getBindings().size());
        for(int i = 0; i < format.getBindings().size(); i++){
            var binding = format.getBindings().get(i);
            bindingDescriptors.get(i)
                    .binding(binding.bindingIndex())
                    .inputRate(binding.divisor() == 0 ? VK_VERTEX_INPUT_RATE_VERTEX : VK_VERTEX_INPUT_RATE_INSTANCE)
                    .stride(binding.vertexSize());
        }


        int attribCount = (int) format.getBindings().stream().mapToLong(f -> f.attributes().size()).sum();

        var attributeDescriptions = VkVertexInputAttributeDescription.calloc(attribCount);

        int index = 0;
        for(var binding : format.getBindings()){
            for(var attrib : binding.attributes()){
                attributeDescriptions.get(index)
                        .binding(binding.bindingIndex())
                        .location(ShaderController.getVertexAttributeIndex(attrib.name()))
                        .format(getVulkanFormatFromType(attrib.type()))
                        .offset(attrib.offset());
                index++;
            }
        }

        this.bindings = bindingDescriptors;
        this.attribs = attributeDescriptions;
    }

    public VkVertexInputAttributeDescription.Buffer getAttribs() {
        return attribs;
    }

    public VkVertexInputBindingDescription.Buffer getBindings() {
        return bindings;
    }

    @Override
    public VertexArrayFormat getFormat() {
        return format;
    }

    @Override
    public void applyVertexBuffers(List<GraphicsBuffer> buffers) {

    }

    @Override
    public void bind() {

    }

    private int getVulkanFormatFromType(VertexArrayBinding.VertexArrayAttribute.Type type){
        return switch (type) {
            case BYTE -> VK_FORMAT_R8_SINT;
            case UNSIGNED_BYTE -> VK_FORMAT_R8_UINT;
            case INT -> VK_FORMAT_R32_UINT;
            case FLOAT -> VK_FORMAT_R32_SFLOAT;
            case FLOAT2 -> VK_FORMAT_R32G32_SFLOAT;
            case FLOAT3 -> VK_FORMAT_R32G32B32_SFLOAT;
            case FLOAT4 -> VK_FORMAT_R32G32B32A32_SFLOAT;
        };
    }
}
