package com.opengg.core.render.internal.vulkan;

import com.opengg.core.console.GGConsole;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.system.Allocator;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugReport.*;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1;

public class VulkanWindow {
    private long surface;

    private final VkInstance instance;
    private VkPhysicalDevice physicalDevice;
    private VkDevice device;
    private VkPhysicalDeviceMemoryProperties memoryProperties;
    private DeviceAndGraphicsQueueFamily deviceAndGraphicsQueueFamily;
    private ColorDepthData colorFormatAndSpace;
    private int queueFamilyIndex;

    private static final ByteBuffer[] layers = {
            memUTF8("VK_LAYER_KHRONOS_validation")
    };
    private VkDebugReportCallbackEXT debugCallback;

    private VulkanWindow(VkInstance instance){
        this.instance = instance;
    }

    public static VulkanWindow createVulkanInstance(PointerBuffer extensions){
        VkApplicationInfo appInfo = VkApplicationInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .apiVersion(VK_API_VERSION_1_1);

        PointerBuffer ppEnabledExtensionNames = MemoryUtil.memAllocPointer(extensions.remaining() + 1);
        ppEnabledExtensionNames.put(extensions);

        var VK_EXT_DEBUG_REPORT_EXTENSION = MemoryUtil.memUTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
        ppEnabledExtensionNames.put(VK_EXT_DEBUG_REPORT_EXTENSION);
        ppEnabledExtensionNames.flip();

        PointerBuffer ppEnabledLayerNames = MemoryUtil.memAllocPointer(layers.length);
        for (int i = 0; RenderEngine.isDebug() && i < layers.length; i++)
            ppEnabledLayerNames.put(layers[i]);
        ppEnabledLayerNames.flip();

        VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(ppEnabledExtensionNames)
                .ppEnabledLayerNames(ppEnabledLayerNames);
        PointerBuffer pInstance = MemoryUtil.memAllocPointer(1);
        VkUtil.catchVulkanException(vkCreateInstance(pCreateInfo, null, pInstance));
        long instance = pInstance.get(0);
        MemoryUtil.memFree(pInstance);
        VkInstance ret = new VkInstance(instance, pCreateInfo);
        pCreateInfo.free();
        MemoryUtil.memFree(ppEnabledLayerNames);
        MemoryUtil.memFree(VK_EXT_DEBUG_REPORT_EXTENSION);
        MemoryUtil.memFree(ppEnabledExtensionNames);
        MemoryUtil.memFree(appInfo.pApplicationName());
        MemoryUtil.memFree(appInfo.pEngineName());
        appInfo.free();
        GGConsole.log("Initialized Vulkan instance");
        return new VulkanWindow(ret);
    }

    public void setSurface(long surface){
        this.surface = surface;
    }


    public void initialize(){
        debugCallback = new VkDebugReportCallbackEXT() {
            public int invoke(int flags, int objectType, long object, long location, int messageCode, long pLayerPrefix, long pMessage, long pUserData) {
                GGConsole.log("Vulkan Layer: " + VkDebugReportCallbackEXT.getString(pMessage));
                return 0;
            }
        };
        long debugCallbackHandle = setupDebugging(VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT | VK_DEBUG_REPORT_DEBUG_BIT_EXT | VK_DEBUG_REPORT_INFORMATION_BIT_EXT | VK_DEBUG_REPORT_OBJECT_TYPE_VALIDATION_CACHE_EXT, debugCallback);
        physicalDevice = getFirstPhysicalDevice();
        deviceAndGraphicsQueueFamily = createDeviceAndGetGraphicsQueueFamily(physicalDevice);
        device = deviceAndGraphicsQueueFamily.device;
        queueFamilyIndex = deviceAndGraphicsQueueFamily.queueFamilyIndex;
        memoryProperties = deviceAndGraphicsQueueFamily.memoryProperties;

        colorFormatAndSpace = getColorFormatAndSpace(physicalDevice, surface);
    }

    private long setupDebugging(int flags, VkDebugReportCallbackEXT callback) {
        VkDebugReportCallbackCreateInfoEXT dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                .pfnCallback(callback)
                .flags(flags);
        LongBuffer pCallback = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateDebugReportCallbackEXT(instance, dbgCreateInfo, null, pCallback));
        long callbackHandle = pCallback.get(0);
        memFree(pCallback);
        dbgCreateInfo.free();
        return callbackHandle;
    }

    private VkPhysicalDevice getFirstPhysicalDevice() {
        IntBuffer pPhysicalDeviceCount = Allocator.allocInt(1);
        int err = vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, null);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get number of physical devices: " + VkUtil.translateVulkanResult(err));
        }
        PointerBuffer pPhysicalDevices = memAllocPointer(pPhysicalDeviceCount.get(0));
        err = vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, pPhysicalDevices);
        long physicalDevice = pPhysicalDevices.get(0);
        memFree(pPhysicalDeviceCount);
        memFree(pPhysicalDevices);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical devices: " + VkUtil.translateVulkanResult(err));
        }
        return new VkPhysicalDevice(physicalDevice, instance);
    }

    private DeviceAndGraphicsQueueFamily createDeviceAndGetGraphicsQueueFamily(VkPhysicalDevice physicalDevice) {
        IntBuffer pQueueFamilyPropertyCount = memAllocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
        int queueCount = pQueueFamilyPropertyCount.get(0);
        VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, queueProps);
        memFree(pQueueFamilyPropertyCount);
        int graphicsQueueFamilyIndex;
        for (graphicsQueueFamilyIndex = 0; graphicsQueueFamilyIndex < queueCount; graphicsQueueFamilyIndex++) {
            if ((queueProps.get(graphicsQueueFamilyIndex).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)
                break;
        }
        queueProps.free();
        FloatBuffer pQueuePriorities = memAllocFloat(1).put(0.0f);
        pQueuePriorities.flip();
        VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(graphicsQueueFamilyIndex)
                .pQueuePriorities(pQueuePriorities);

        PointerBuffer extensions = memAllocPointer(1);
        ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        extensions.put(VK_KHR_SWAPCHAIN_EXTENSION);
        extensions.flip();
        PointerBuffer ppEnabledLayerNames = memAllocPointer(layers.length);
        for (int i = 0; RenderEngine.isDebug() && i < layers.length; i++)
            ppEnabledLayerNames.put(layers[i]);
        ppEnabledLayerNames.flip();

        VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pQueueCreateInfos(queueCreateInfo)
                .ppEnabledExtensionNames(extensions)
                .ppEnabledLayerNames(ppEnabledLayerNames);

        PointerBuffer pDevice = memAllocPointer(1);
        VkUtil.catchVulkanException(vkCreateDevice(physicalDevice, deviceCreateInfo, null, pDevice));
        long device = pDevice.get(0);
        memFree(pDevice);


        var memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);

        var deviceGraphicsQueueFamily = new DeviceAndGraphicsQueueFamily();
        deviceGraphicsQueueFamily.device = new VkDevice(device, physicalDevice, deviceCreateInfo);
        deviceGraphicsQueueFamily.queueFamilyIndex = graphicsQueueFamilyIndex;
        deviceGraphicsQueueFamily.memoryProperties = memoryProperties;

        deviceCreateInfo.free();
        memFree(ppEnabledLayerNames);
        memFree(VK_KHR_SWAPCHAIN_EXTENSION);
        memFree(extensions);
        memFree(pQueuePriorities);
        return deviceGraphicsQueueFamily;
    }

    private boolean getSupportedDepthFormat(VkPhysicalDevice physicalDevice, IntBuffer depthFormat) {
        // Since all depth formats may be optional, we need to find a suitable depth format to use
        // Start with the highest precision packed format
        int[] depthFormats = {
                VK_FORMAT_D32_SFLOAT_S8_UINT,
                VK_FORMAT_D32_SFLOAT,
                VK_FORMAT_D24_UNORM_S8_UINT,
                VK_FORMAT_D16_UNORM_S8_UINT,
                VK_FORMAT_D16_UNORM
        };

        VkFormatProperties formatProps = VkFormatProperties.calloc();
        for (int format : depthFormats) {
            vkGetPhysicalDeviceFormatProperties(physicalDevice, format, formatProps);
            // Format must support depth stencil attachment for optimal tiling
            if ((formatProps.optimalTilingFeatures() & VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT) != 0) {
                depthFormat.put(0, format);
                return true;
            }
        }
        return false;
    }

    private ColorDepthData getColorFormatAndSpace(VkPhysicalDevice physicalDevice, long surface) {
        IntBuffer pQueueFamilyPropertyCount = memAllocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
        int queueCount = pQueueFamilyPropertyCount.get(0);
        VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, queueProps);
        memFree(pQueueFamilyPropertyCount);

        // Iterate over each queue to learn whether it supports presenting:
        IntBuffer supportsPresent = memAllocInt(queueCount);
        for (int i = 0; i < queueCount; i++) {
            supportsPresent.position(i);
            VkUtil.catchVulkanException(vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface, supportsPresent));
        }

        // Search for a graphics and a present queue in the array of queue families, try to find one that supports both
        int graphicsQueueNodeIndex = Integer.MAX_VALUE;
        int presentQueueNodeIndex = Integer.MAX_VALUE;
        for (int i = 0; i < queueCount; i++) {
            if ((queueProps.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
                    graphicsQueueNodeIndex = i;
                }
                if (supportsPresent.get(i) == VK_TRUE) {
                    graphicsQueueNodeIndex = i;
                    presentQueueNodeIndex = i;
                    break;
                }
            }
        }
        queueProps.free();
        if (presentQueueNodeIndex == Integer.MAX_VALUE) {
            // If there's no queue that supports both present and graphics try to find a separate present queue
            for (int i = 0; i < queueCount; ++i) {
                if (supportsPresent.get(i) == VK_TRUE) {
                    presentQueueNodeIndex = i;
                    break;
                }
            }
        }
        memFree(supportsPresent);

        // Generate error if could not find both a graphics and a present queue
        if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
            throw new AssertionError("No graphics queue found");
        }
        if (presentQueueNodeIndex == Integer.MAX_VALUE) {
            throw new AssertionError("No presentation queue found");
        }
        if (graphicsQueueNodeIndex != presentQueueNodeIndex) {
            throw new AssertionError("Presentation queue != graphics queue");
        }

        // Get list of supported formats
        IntBuffer pFormatCount = memAllocInt(1);
        VkUtil.catchVulkanException(vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, null));
        int formatCount = pFormatCount.get(0);

        VkSurfaceFormatKHR.Buffer surfFormats = VkSurfaceFormatKHR.calloc(formatCount);
        VkUtil.catchVulkanException(vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, surfFormats));
        memFree(pFormatCount);

        int colorFormat;
        if (formatCount == 1 && surfFormats.get(0).format() == VK_FORMAT_UNDEFINED) {
            colorFormat = VK_FORMAT_B8G8R8A8_UNORM;
        } else {
            colorFormat = surfFormats.get(0).format();
        }
        int colorSpace = surfFormats.get(0).colorSpace();
        surfFormats.free();

        IntBuffer pDepthFormat = memAllocInt(1).put(0, -1);
        getSupportedDepthFormat(physicalDevice, pDepthFormat);
        int depthFormat = pDepthFormat.get(0);

        var ret = new ColorDepthData();
        ret.colorFormat = colorFormat;
        ret.colorSpace = colorSpace;
        ret.depthFormat = depthFormat;
        return ret;
    }

    public long getSurface() {
        return surface;
    }

    public VkInstance getInstance() {
        return instance;
    }

    public VkPhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public VkDevice getDevice() {
        return device;
    }

    public VkPhysicalDeviceMemoryProperties getMemoryProperties() {
        return memoryProperties;
    }

    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }

    public DeviceAndGraphicsQueueFamily getDeviceAndGraphicsQueueFamily() {
        return deviceAndGraphicsQueueFamily;
    }

    public ColorDepthData getColorFormatAndSpace() {
        return colorFormatAndSpace;
    }

    public static class ColorDepthData {
        int colorFormat;
        int colorSpace;
        int depthFormat;
    }

    private static class DeviceAndGraphicsQueueFamily {
        VkDevice device;
        int queueFamilyIndex;
        VkPhysicalDeviceMemoryProperties memoryProperties;
    }
}
