package com.opengg.core.render.internal.vulkan;

import com.opengg.core.render.GraphicsBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanBuffer implements GraphicsBuffer{
    private final long memoryPointer;
    private final long buf;
    private final int size;
    private final BufferType type;

    public VulkanBuffer(BufferType type, int size) {
        this(type, size, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT);
    }

    public VulkanBuffer(BufferType type, int size, int hostVisibility){
        this.size = size;
        this.type = type;

        if(size <= 0) throw new IllegalArgumentException("Cannot allocate an empty buffer");

        VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(fromBufferType(type));

        LongBuffer pBuffer = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateBuffer(VulkanRenderer.getRenderer().getDevice(), bufInfo, null, pBuffer));
        buf = pBuffer.get(0);
        memFree(pBuffer);
        bufInfo.free();

        VkMemoryRequirements memReqs = VkMemoryRequirements.calloc();
        IntBuffer memoryTypeIndex = memAllocInt(1);
        vkGetBufferMemoryRequirements(VulkanRenderer.getRenderer().getDevice(), buf, memReqs);
        getMemoryType(VulkanRenderer.getRenderer().getWindow().getMemoryProperties(), memReqs.memoryTypeBits(), hostVisibility, memoryTypeIndex);

        VkMemoryAllocateInfo memAlloc = VkMemoryAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(memReqs.size())
                .memoryTypeIndex(memoryTypeIndex.get(0));

        memFree(memoryTypeIndex);
        memReqs.free();


        LongBuffer pMemory = memAllocLong(1);
        VkUtil.catchVulkanException(vkAllocateMemory(VulkanRenderer.getRenderer().getDevice(), memAlloc, null, pMemory));
        memoryPointer = pMemory.get(0);
        memFree(pMemory);

        VkUtil.catchVulkanException(vkBindBufferMemory(VulkanRenderer.getRenderer().getDevice(), buf, memoryPointer, 0));
    }

    public VulkanBuffer(BufferType type, FloatBuffer buffer){
        this(type, buffer.limit() * Float.BYTES);
        uploadData(buffer);
    }

    public VulkanBuffer(BufferType type, IntBuffer buffer){
        this(type, buffer.limit() * Integer.BYTES);
        uploadData(buffer);
    }

    public VulkanBuffer(BufferType type, ByteBuffer buffer){
        this(type, buffer.limit() * Byte.BYTES);
        uploadData(buffer);
    }

    @Override
    public void uploadData(FloatBuffer data) {
        uploadSubData(data, 0);
    }

    @Override
    public void uploadData(IntBuffer data) {
        uploadSubData(data, 0);
    }

    @Override
    public void uploadData(ByteBuffer data) {
        uploadSubData(data, 0);
    }

    @Override
    public void uploadSubData(FloatBuffer data, long offset) {
        data.rewind();

        PointerBuffer pData = memAllocPointer(1);
        VkUtil.catchVulkanException(vkMapMemory(VulkanRenderer.getRenderer().getDevice(), memoryPointer, offset, data.limit() * Float.BYTES, 0, pData));

        long mappedData = pData.get(0);
        memFree(pData);

        MemoryUtil.memCopy(memAddress(data), mappedData, data.limit() * Float.BYTES);
        vkUnmapMemory(VulkanRenderer.getRenderer().getDevice(), memoryPointer);

        data.rewind();
    }

    @Override
    public void uploadSubData(IntBuffer data, long offset) {
        data.rewind();
        PointerBuffer pData = memAllocPointer(1);
        VkUtil.catchVulkanException(vkMapMemory(VulkanRenderer.getRenderer().getDevice(), memoryPointer, offset, data.limit() * Integer.BYTES, 0, pData));
        long mappedData = pData.get(0);
        memFree(pData);
        MemoryUtil.memCopy(memAddress(data), mappedData, data.limit() * Integer.BYTES);
        vkUnmapMemory(VulkanRenderer.getRenderer().getDevice(), memoryPointer);
        data.rewind();
    }

    public void uploadSubData(ByteBuffer data, long offset) {
        data.rewind();
        PointerBuffer pData = memAllocPointer(1);
        VkUtil.catchVulkanException(vkMapMemory(VulkanRenderer.getRenderer().getDevice(), memoryPointer, offset, data.limit() * Byte.BYTES, 0, pData));
        long mappedData = pData.get(0);
        memFree(pData);
        MemoryUtil.memCopy(memAddress(data), mappedData, data.limit() * Byte.BYTES);
        vkUnmapMemory(VulkanRenderer.getRenderer().getDevice(), memoryPointer);
        data.rewind();
    }


    @Override
    public void bindBase(int base) {

    }

    @Override
    public int getBase() {
        return 0;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public BufferType getTarget() {
        return type;
    }

    @Override
    public UsageType getUsage() {
        return null;
    }

    public long getMemoryPointer() {
        return memoryPointer;
    }

    public long getBuffer() {
        return buf;
    }

    public void destroy(){
        vkDestroyBuffer(VulkanRenderer.getRenderer().getDevice(), buf, null);
        vkFreeMemory(VulkanRenderer.getRenderer().getDevice(), memoryPointer, null);
    }

    public static boolean getMemoryType(VkPhysicalDeviceMemoryProperties deviceMemoryProperties, int typeBits, int properties, IntBuffer typeIndex) {
        int bits = typeBits;
        for (int i = 0; i < 32; i++) {
            if ((bits & 1) == 1) {
                if ((deviceMemoryProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
                    typeIndex.put(0, i);
                    return true;
                }
            }
            bits >>= 1;
        }
        return false;
    }

    public static int fromBufferType(BufferType type){
        return switch (type) {
            case VERTEX_ARRAY_BUFFER -> VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
            case ELEMENT_ARRAY_BUFFER -> VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
            case DISPATCH_INDIRECT_BUFFER, ATOMIC_COUNTER_BUFFER, DRAW_INDIRECT_BUFFER, TRANSFORM_FEEDBACK_BUFFER, QUERY_BUFFER, PIXEL_PACK_BUFFER, PIXEL_UNPACK_BUFFER, SHADER_STORAGE_BUFFER, TEXTURE_BUFFER -> VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
            case UNIFORM_BUFFER -> VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT;
            case COPY_READ_BUFFER -> VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
            case COPY_WRITE_BUFFER -> VK_BUFFER_USAGE_TRANSFER_DST_BIT;
        };
    }
}
