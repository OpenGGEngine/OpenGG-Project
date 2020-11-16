package com.opengg.core.render.internal.vulkan.texture;

import com.opengg.core.exceptions.RenderException;
import com.opengg.core.math.Vector2i;
import com.opengg.core.math.Vector3i;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.internal.vulkan.VkUtil;
import com.opengg.core.render.internal.vulkan.VulkanBuffer;
import com.opengg.core.render.internal.vulkan.VulkanCommandBuffer;
import com.opengg.core.render.internal.vulkan.VulkanRenderer;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.system.NativeResource;
import com.opengg.core.system.NativeResourceManager;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanImage implements Texture, NativeResource {
    private final long image;
    private long memoryPointer;

    private int layout;
    private Texture.TextureConfig config;
    private Vector3i size;
    private int layers;

    private VulkanSampler sampler;

    public VulkanImage(Texture.TextureConfig config, int samples, int tiling, int usage, Vector3i size, int layers){
        this.layout = VK_IMAGE_LAYOUT_UNDEFINED;
        this.config = config;
        this.size = size;

        VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                .imageType(getVulkanImageType(config.type()))
                .format(getVulkanImageFormat(config.internalFormat()))
                .mipLevels(1)
                .arrayLayers(layers)
                .samples(samples)
                .tiling(tiling)
                .usage(usage)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
        if(config.type() == TextureType.TEXTURE_CUBEMAP) imageCreateInfo.flags(VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT);

        imageCreateInfo.extent().width(size.x).height(size.y).depth(size.z);

        LongBuffer imageBuf = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateImage(VulkanRenderer.getRenderer().getDevice(), imageCreateInfo, null, imageBuf));
        image = imageBuf.get(0);
        memFree(imageBuf);
        imageCreateInfo.free();

        generateImageMemoryBacking();

        sampler = new VulkanSampler(config);

        NativeResourceManager.registerNativeResource(this);
    }

    public VulkanImage(long pointer){
        this.image = pointer;
    }

    private void generateImageMemoryBacking(){

        VkMemoryRequirements memoryRequirements = VkMemoryRequirements.calloc();
        VkMemoryAllocateInfo mem_alloc = VkMemoryAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);

        vkGetImageMemoryRequirements(VulkanRenderer.getRenderer().getDevice(), image, memoryRequirements);

        IntBuffer pMemoryTypeIndex = memAllocInt(1);
        VulkanBuffer.getMemoryType(VulkanRenderer.getRenderer().getWindow().getMemoryProperties(), memoryRequirements.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT, pMemoryTypeIndex);

        mem_alloc.memoryTypeIndex(pMemoryTypeIndex.get(0));
        mem_alloc.allocationSize(memoryRequirements.size());

        memFree(pMemoryTypeIndex);

        LongBuffer pImageMemory = memAllocLong(1);
        VkUtil.catchVulkanException(vkAllocateMemory(VulkanRenderer.getRenderer().getDevice(), mem_alloc, null, pImageMemory));
        memoryPointer = pImageMemory.get(0);
        memFree(pImageMemory);
        mem_alloc.free();

        VkUtil.catchVulkanException(vkBindImageMemory(VulkanRenderer.getRenderer().getDevice(), image, memoryPointer, 0));
    }

    public void uploadBuffers(Vector3i start, Vector3i copySize, int baseLayer, VulkanBuffer... buffer){
        var commandBuffer = new VulkanCommandBuffer(VulkanRenderer.getRenderer().getCommandPool());
        commandBuffer.begin();
        transitionLayout(commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, baseLayer, buffer.length);
        for(int i = 0; i < buffer.length; i++){
            copyBuffer(commandBuffer, buffer[i], baseLayer+i, start, copySize);
        }
        transitionLayout(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, baseLayer, buffer.length);
        commandBuffer.end();
        commandBuffer.submitAndWait();
    }

    private void transitionLayout(VulkanCommandBuffer buffer, int newLayout, int baseLayer, int layerCount){
        int sourceStage, destinationStage;
        var barrier = VkImageMemoryBarrier.calloc(1)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                .oldLayout(layout)
                .newLayout(newLayout)
                .image(image);

        if (layout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
            barrier.srcAccessMask(0);
            barrier.dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);

            sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            destinationStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
        } else if (layout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
            barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
            barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);

            sourceStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
            destinationStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
        } else {
            throw new RenderException("Unsupported layout transition!");
        }


        barrier.subresourceRange()
                .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                .baseMipLevel(0)
                .levelCount(1)
                .baseArrayLayer(baseLayer)
                .layerCount(layerCount);

        vkCmdPipelineBarrier(buffer.getBuffer(), sourceStage, destinationStage, 0, null, null, barrier);
        barrier.free();

        layout = newLayout;
    }

    private void copyBuffer(VulkanCommandBuffer cmd, VulkanBuffer buffer, int layer, Vector3i start, Vector3i copySize){
        VkBufferImageCopy.Buffer region = VkBufferImageCopy.calloc(1)
                .bufferOffset(0)
                .bufferRowLength(copySize.x)
                .bufferImageHeight(copySize.y);
        region.imageSubresource()
                    .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .mipLevel(0)
                    .baseArrayLayer(layer)
                    .layerCount(1);
        region.imageOffset().set(start.x,start.y,start.z);
        region.imageExtent().set(copySize.x,copySize.y,copySize.z);

        cmd.copyBufferToImage(buffer, this, region);
        region.free();
    }

    public View getImageView(int aspectMask){
        var view = new VulkanImage.View(this, getVulkanImageViewType(config.type()), getVulkanImageFormat(config.internalFormat()), aspectMask);
        return view;
    }

    public View getImageView(int viewType, int format, int aspectMask){
        var view = new VulkanImage.View(this, viewType, format, aspectMask);
        return view;
    }

    public VulkanSampler getSampler(){
        return sampler;
    }

    public long getMemoryPointer() {
        return memoryPointer;
    }

    public static int getVulkanImageViewType(Texture.TextureType type){
        return switch (type) {
            case TEXTURE_2D -> VK_IMAGE_VIEW_TYPE_2D;
            case TEXTURE_ARRAY -> VK_IMAGE_VIEW_TYPE_2D_ARRAY;
            case TEXTURE_3D -> VK_IMAGE_VIEW_TYPE_3D;
            case TEXTURE_CUBEMAP -> VK_IMAGE_VIEW_TYPE_CUBE;
        };
    }

    public static int getVulkanImageType(Texture.TextureType type){
        return switch (type) {
            case TEXTURE_2D, TEXTURE_CUBEMAP -> VK_IMAGE_TYPE_2D;
            case TEXTURE_3D, TEXTURE_ARRAY -> VK_IMAGE_TYPE_3D;
        };
    }

    public static int getVulkanSamplerFormat(Texture.SamplerFormat format){
        return -1;//;//switch ()
    }

    public static int getVulkanImageFormat(Texture.TextureFormat format){
        return switch (format){
            case RGB8 -> VK_FORMAT_R8G8B8_UNORM;
            case RGBA8 -> VK_FORMAT_R8G8B8A8_UNORM;
            case RGB16 -> VK_FORMAT_R16G16B16_UNORM;
            case RGBA16 -> VK_FORMAT_R16G16B16A16_UNORM;
            case RGB32F -> VK_FORMAT_R32G32B32_SFLOAT;
            case RGBA16F -> VK_FORMAT_R16G16B16A16_SFLOAT;
            case RGBA32F -> VK_FORMAT_R32G32B32A32_SFLOAT;
            case SRGB8 -> VK_FORMAT_R8G8B8_SRGB;
            case SRGBA8 -> VK_FORMAT_R8G8B8A8_SRGB;
            case DEPTH32 -> VK_FORMAT_D32_SFLOAT;
            case DEPTH24_STENCIL8 -> VK_FORMAT_D24_UNORM_S8_UINT;
            case DEPTH32_STENCIL8 -> VK_FORMAT_D32_SFLOAT_S8_UINT;
            case DXT1, DXT3, ATSC, DXT5 -> throw new UnsupportedOperationException("Not yet");
        };
    }

    @Override
    public void setAsUniform(String uniform) {
        ShaderController.setUniform(uniform, this);
    }

    @Override
    public void set2DData(TextureData data) {
        var buffer = (VulkanBuffer) GraphicsBuffer.allocate(GraphicsBuffer.BufferType.COPY_READ_BUFFER, (ByteBuffer) data.buffer, GraphicsBuffer.UsageType.HOST_MAPPABLE_UPDATABLE);
        uploadBuffers(new Vector3i(), size, 0, buffer);
        buffer.destroy();
    }

    @Override
    public void set2DSubData(TextureData data, Vector2i offset) {
        var buffer = (VulkanBuffer) GraphicsBuffer.allocate(GraphicsBuffer.BufferType.COPY_READ_BUFFER, (ByteBuffer) data.buffer, GraphicsBuffer.UsageType.HOST_MAPPABLE_UPDATABLE);
        uploadBuffers(new Vector3i(offset.x, offset.y, 1), new Vector3i(data.width, data.height, 1), 0, buffer);
        buffer.destroy();
    }

    @Override
    public void setCubemapData(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6) {
        var datums = List.of(data1, data2, data4, data3, data5, data6);
        var vkBufs = datums.stream().map(datum -> (VulkanBuffer) GraphicsBuffer.allocate(GraphicsBuffer.BufferType.COPY_READ_BUFFER, (ByteBuffer) datum.buffer, GraphicsBuffer.UsageType.HOST_MAPPABLE_UPDATABLE))
                .collect(Collectors.toList());
        uploadBuffers(new Vector3i(), new Vector3i(size.x, size.y, 1), 0, vkBufs.toArray(new VulkanBuffer[6]));
        vkBufs.forEach(VulkanBuffer::destroy);
    }

    @Override
    public void set3DData(TextureData[] datums) {

    }

    @Override
    public void set3DSubData(int xoffset, int yoffset, int zoffset, TextureData[] datums) {

    }

    @Override
    public List<TextureData> getData() {
        return null;
    }

    @Override
    public long getID() {
        return image;
    }

    @Override
    public Runnable onDestroy() {
        long image2 = image;
        long pointer2 = memoryPointer;
        return () -> {
            vkDestroyImage(VulkanRenderer.getRenderer().getDevice(), image2, null);
            vkFreeMemory(VulkanRenderer.getRenderer().getDevice(), pointer2, null);
        };
    }

    public static class View{
        private final long view;
        private final VulkanImage viewedImage;

        View(VulkanImage image, int viewType, int format, int aspectMask){
            this.viewedImage = image;
            VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .viewType(viewType)
                    .format(format);
            viewCreateInfo.subresourceRange()
                    .aspectMask(aspectMask)
                    .levelCount(1)
                    .layerCount(1);

            viewCreateInfo.image(image.image);
            LongBuffer pView = memAllocLong(1);
            VkUtil.catchVulkanException(vkCreateImageView(VulkanRenderer.getRenderer().getDevice(), viewCreateInfo, null, pView));
            view = pView.get(0);
            memFree(pView);
            viewCreateInfo.free();
        }

        public long getView() {
            return view;
        }

        public VulkanImage getImage() {
            return this.viewedImage;
        }
    }

}
