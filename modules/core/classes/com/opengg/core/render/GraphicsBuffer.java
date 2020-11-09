package com.opengg.core.render;

import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.exceptions.RenderException;
import com.opengg.core.render.internal.opengl.OpenGLBuffer;
import com.opengg.core.render.internal.vulkan.VulkanBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Represents a GPU VRAM mapped buffer for the current graphics context
 * @author Javier
 */
public interface GraphicsBuffer{

    /**
     * Allocates empty memory of the given buffer type, size, and access type
     * @param type Memory usage type
     * @param size Amount of bytes to allocate
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    static GraphicsBuffer allocate(BufferType type, int size, UsageType access){
        PerformanceManager.registerBufferAllocation(size);
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLBuffer(type, size, access);
            case VULKAN -> new VulkanBuffer(type, size);
        };    }

    /**
     * Allocates and uploads the given {@code FloatBuffer} to the GPU using the given buffer type and access type
     * @param type Memory usage type
     * @param buffer Buffer to upload
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    static GraphicsBuffer allocate(BufferType type, ByteBuffer buffer, UsageType access){
        PerformanceManager.registerBufferAllocation(buffer.limit());
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLBuffer(type, buffer, access);
            case VULKAN -> new VulkanBuffer(type, buffer);
        };
    }

    /**
     * Allocates and uploads the given {@code FloatBuffer} to the GPU using the given buffer type and access type
     * @param type Memory usage type
     * @param buffer Buffer to upload
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    static GraphicsBuffer allocate(BufferType type, FloatBuffer buffer, UsageType access){
        PerformanceManager.registerBufferAllocation(buffer.limit() * Float.BYTES);
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLBuffer(type, buffer, access);
            case VULKAN -> new VulkanBuffer(type, buffer);
        };
    }

    /**
     * Allocates and uploads the given {@code IntBuffer} to the GPU using the given buffer type and access type
     * @param type Memory usage type
     * @param buffer Buffer to upload
     * @param access Memory usage (can be GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW)
     * @return
     */
    static GraphicsBuffer allocate(BufferType type, IntBuffer buffer, UsageType access){
        PerformanceManager.registerBufferAllocation(buffer.limit() * Integer.BYTES);
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLBuffer(type, buffer, access);
            case VULKAN -> new VulkanBuffer(type, buffer);
        };
    }

    /**
     * Binds this buffer for use
     */
    void bind();

    /**
     * Unbinds this buffer
     */
    void unbind();

    /**
     * Uploads the given {@code FloatBuffer} to the buffer
     * @param data
     */
    void uploadData(ByteBuffer data);

    /**
     * Uploads the given {@code FloatBuffer} to the buffer
     * @param data
     */
    void uploadData(FloatBuffer data);

    /**
     * Uploads the given {@code IntBuffer} to the buffer
     * @param data
     */
    void uploadData(IntBuffer data);

    /**
     * Copies the given {@code FloatBuffer} to the buffer starting at the given offset
     * @param data
     * @param offset
     */
    void uploadSubData(FloatBuffer data, long offset);

    /**
     * Copies the given {@code IntBuffer} to the buffer starting at the given offset
     * @param data
     * @param offset
     */
    void uploadSubData(IntBuffer data, long offset);

    /**
     * Binds this buffer to the given base position
     * @param base
     */
    void bindBase(int base);

    /**
     * Binds this buffer to the given attribute binding location
     * @param attrib
     * @param size
     */
    void bindToAttribute(int attrib, int size);

    /**
     * Gets the base index this buffer is bound to
     * @return
     */
    int getBase();

    /**
     * Gets the size of this buffer in bytes
     * @return
     */
    int getSize();

    /**
     * Gets the target this buffer is set to
     * @return
     */
    BufferType getTarget();

    /**
     * Gets the memory usage format this buffer is laid out in
     * @return
     */
    UsageType getUsage();

    /**
     * Denotes the usage types for the underlying rendering system
     * <br/>
     *     This enum contains 9 types, with 3 each of type DRAW, READ, and COPY.
     *     <ul>
     *         <li>
     *              DRAW indicates that this buffer is written by the user and read by the backend
     *         </li>
     *         <li>
     *              READ indicates that this buffer is written by the backend and read by the user
     *         </li>
     *         <li>
     *              COPY indicates that the buffer is both read and written to by the backend
     *         </li>
     *     </ul>
     *     These types are enforced, so attempts to read from a buffer marked COPY or DRAW or
     *     attempts to write to a buffer marked READ or COPY have no guarantee of succeeding,
     * <br/>
     *     Additionally, each type is marked with one of STATIC, DYNAMIC, and STREAM, each indicating a usage behavior recommendation.
     *     <ul>
     *         <li>
     *             STATIC indicates that the buffer is rarely written to and often read from.
     *         </li>
     *         <li>
     *             DYNAMIC indicates that the buffer is both written to and read from equally.
     *         </li>
     *         <li>
     *             STREAM indicates that the buffer is often written to and rarely read from.
     *         </li>
     *     </ul>
     *     The above 3 parts are simply unenforced recommendations, and are used to optimize the underlying render system
     */
    enum UsageType{
        STATIC_DRAW,
        DYNAMIC_DRAW,
        STREAM_DRAW,
        STATIC_READ,
        DYNAMIC_READ,
        STREAM_READ,
        STATIC_COPY,
        DYNAMIC_COPY,
        STREAM_COPY
    }

    /**
     * Indicates the type of this buffer.
     * <br/>
     * Using a certain buffer type during an operation that requires a different buffer type will result in an error.
     */
    enum BufferType{
        /**
         * Buffer used to contain the vertex values for a draw command
         */
        VERTEX_ARRAY_BUFFER,
        /**
         * Buffer used to contain the indices for a draw command
         */
        ELEMENT_ARRAY_BUFFER,
        COPY_READ_BUFFER,
        COPY_WRITE_BUFFER,
        PIXEL_UNPACK_BUFFER,
        PIXEL_PACK_BUFFER,
        QUERY_BUFFER,
        TEXTURE_BUFFER,
        TRANSFORM_FEEDBACK_BUFFER,
        UNIFORM_BUFFER,
        DRAW_INDIRECT_BUFFER,
        ATOMIC_COUNTER_BUFFER,
        DISPATCH_INDIRECT_BUFFER,
        SHADER_STORAGE_BUFFER
    }
}
