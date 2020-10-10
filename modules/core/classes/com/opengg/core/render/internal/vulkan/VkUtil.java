package com.opengg.core.render.internal.vulkan;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.RenderException;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.system.Allocator;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.ShadercIncludeResolve;
import org.lwjgl.util.shaderc.ShadercIncludeResultRelease;

import java.nio.ByteBuffer;

import static org.lwjgl.util.shaderc.Shaderc.*;
import static org.lwjgl.vulkan.EXTDebugReport.VK_ERROR_VALIDATION_FAILED_EXT;
import static org.lwjgl.vulkan.KHRDisplaySwapchain.VK_ERROR_INCOMPATIBLE_DISPLAY_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_ERROR_NATIVE_WINDOW_IN_USE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_ERROR_SURFACE_LOST_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_SUBOPTIMAL_KHR;
import static org.lwjgl.vulkan.NVRayTracing.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_ERROR_OUT_OF_POOL_MEMORY;

public class VkUtil {
    public static int VK_FLAGS_NONE = 0;

    public static ByteBuffer glslToSpirv(String name, String source, int vulkanStage) {
        source = source.substring(0, source.lastIndexOf(";"));
        ByteBuffer src = Allocator.alloc(source.length()*Character.BYTES).put(source.getBytes()).flip();
        long compiler = shaderc_compiler_initialize();
        long options = shaderc_compile_options_initialize();
        ShadercIncludeResolve resolver;
        ShadercIncludeResultRelease releaser;
        shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
        /*shaderc_compile_options_set_include_callbacks(options, resolver = new ShadercIncludeResolve() {
            public long invoke(long user_data, long requested_source, int type, long requesting_source, long include_depth) {
                ShadercIncludeResult res = ShadercIncludeResult.calloc();
                try {
                    String src = classPath.substring(0, classPath.lastIndexOf('/')) + "/" + memUTF8(requested_source);
                    res.content(FileUtil.ioResourceToByteBuffer(src, 1024));
                    res.source_name(memUTF8(src));
                    return res.address();
                } catch (IOException e) {
                    throw new AssertionError("Failed to resolve include: " + src);
                }
            }
        }, releaser = new ShadercIncludeResultRelease() {
            public void invoke(long user_data, long include_result) {
                ShadercIncludeResult result = ShadercIncludeResult.create(include_result);
                memFree(result.source_name());
                result.free();
            }
        }, 0L);*/
        long res;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            res = shaderc_compile_into_spv(compiler, src, vulkanStageToShadercKind(vulkanStage),
                    stack.UTF8("Error"), stack.UTF8("main"), options);
            if (res == 0L)
                throw new AssertionError("Internal error during compilation!");
        }
        if (shaderc_result_get_compilation_status(res) != shaderc_compilation_status_success) {
            throw new ShaderException("Shader compilation failed for " + name + ":" + shaderc_result_get_error_message(res));
        }
        int size = (int) shaderc_result_get_length(res);
        ByteBuffer resultBytes = Allocator.alloc(size);
        resultBytes.put(shaderc_result_get_bytes(res));
        resultBytes.flip();
        //shaderc_compiler_release(res); //todo FIGURE THIS ONE OUT
        shaderc_compiler_release(compiler);
        return resultBytes;
    }

    private static int vulkanStageToShadercKind(int stage) {
        return switch (stage) {
            case VK_SHADER_STAGE_VERTEX_BIT -> shaderc_vertex_shader;
            case VK_SHADER_STAGE_FRAGMENT_BIT -> shaderc_fragment_shader;
            case VK_SHADER_STAGE_RAYGEN_BIT_NV -> shaderc_raygen_shader;
            case VK_SHADER_STAGE_CLOSEST_HIT_BIT_NV -> shaderc_closesthit_shader;
            case VK_SHADER_STAGE_MISS_BIT_NV -> shaderc_miss_shader;
            case VK_SHADER_STAGE_ANY_HIT_BIT_NV -> shaderc_anyhit_shader;
            case VK_SHADER_STAGE_GEOMETRY_BIT -> shaderc_geometry_shader;
            default -> throw new IllegalArgumentException("Stage: " + stage);
        };
    }

    public static void processVulkanException(int result, Runnable onFail){
        if (result != VK_SUCCESS) {
            GGConsole.error(VkUtil.translateVulkanResult(result));
            onFail.run();
            throw new RenderException(VkUtil.translateVulkanResult(result));
        }
    }

    public static void catchVulkanException(int result){
        if (result != VK_SUCCESS) {
            throw new RenderException(VkUtil.translateVulkanResult(result));
        }
    }

    public static String translateVulkanResult(int result) {
        return switch (result) {
            case VK_SUCCESS -> "Command successfully completed.";
            case VK_NOT_READY -> "A fence or query has not yet completed.";
            case VK_TIMEOUT -> "A wait operation has not completed in the specified time.";
            case VK_EVENT_SET -> "An event is signaled.";
            case VK_EVENT_RESET -> "An event is unsignaled.";
            case VK_INCOMPLETE -> "A return array was too small for the result.";
            case VK_SUBOPTIMAL_KHR -> "A swapchain no longer matches the surface properties exactly, but can still be used to present to the surface successfully.";
            case VK_ERROR_OUT_OF_HOST_MEMORY -> "A host memory allocation has failed.";
            case VK_ERROR_OUT_OF_POOL_MEMORY -> "A descriptor or command buffer creation has failed due to no pool memory.";
            case VK_ERROR_OUT_OF_DEVICE_MEMORY -> "A device memory allocation has failed.";
            case VK_ERROR_INITIALIZATION_FAILED -> "Initialization of an object could not be completed for implementation-specific reasons.";
            case VK_ERROR_DEVICE_LOST -> "The logical or physical device has been lost.";
            case VK_ERROR_MEMORY_MAP_FAILED -> "Mapping of a memory object has failed.";
            case VK_ERROR_LAYER_NOT_PRESENT -> "A requested layer is not present or could not be loaded.";
            case VK_ERROR_EXTENSION_NOT_PRESENT -> "A requested extension is not supported.";
            case VK_ERROR_FEATURE_NOT_PRESENT -> "A requested feature is not supported.";
            case VK_ERROR_INCOMPATIBLE_DRIVER -> "The requested version of Vulkan is not supported by the driver or is otherwise incompatible for implementation-specific reasons.";
            case VK_ERROR_TOO_MANY_OBJECTS -> "Too many objects of the type have already been created.";
            case VK_ERROR_FORMAT_NOT_SUPPORTED -> "A requested format is not supported on this device.";
            case VK_ERROR_SURFACE_LOST_KHR -> "A surface is no longer available.";
            case VK_ERROR_NATIVE_WINDOW_IN_USE_KHR -> "The requested window is already connected to a VkSurfaceKHR, or to some other non-Vulkan API.";
            case VK_ERROR_OUT_OF_DATE_KHR -> "A surface has changed in such a way that it is no longer compatible with the swapchain, and further presentation requests using the "
                    + "swapchain will fail. Applications must query the new surface properties and recreate their swapchain if they wish to continue presenting to the surface.";
            case VK_ERROR_INCOMPATIBLE_DISPLAY_KHR -> "The display used by a swapchain does not use the same presentable image layout, or is incompatible in a way that prevents sharing an image.";
            case VK_ERROR_VALIDATION_FAILED_EXT -> "A validation layer found an error.";
            default -> String.format("%s [%d]", "Unknown", result);
        };
    }
}
