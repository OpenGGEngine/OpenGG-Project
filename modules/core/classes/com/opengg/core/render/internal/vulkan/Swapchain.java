package com.opengg.core.render.internal.vulkan;

import com.opengg.core.math.Vector2i;
import com.opengg.core.render.internal.vulkan.texture.VulkanImage;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

class Swapchain {
    private long swapchainHandle;
    private VulkanImage.View[] imageViews;

    private Vector2i extents;

    public Swapchain(VkDevice device, VkPhysicalDevice physicalDevice, long surface, Vector2i defaultSize, VulkanWindow.ColorDepthData colorFormat){
        this(device, physicalDevice, surface, VK_NULL_HANDLE, defaultSize, colorFormat);
    }

    public Swapchain(VkDevice device, VkPhysicalDevice physicalDevice, long surface, long oldSwapChain, Vector2i defaultSize, VulkanWindow.ColorDepthData colorFormat) {
        // Get physical device surface properties and formats
        VkSurfaceCapabilitiesKHR surfCaps = getCapabilities(physicalDevice, surface);
        int swapchainPresentMode = getSwapchainPresentMode(physicalDevice, surface, VK_PRESENT_MODE_FIFO_KHR);
        extents = calculateExtents(defaultSize, surfCaps);

        // Determine the number of images
        int desiredNumberOfSwapchainImages = surfCaps.minImageCount() + 1;
        if ((surfCaps.maxImageCount() > 0) && (desiredNumberOfSwapchainImages > surfCaps.maxImageCount())) {
            desiredNumberOfSwapchainImages = surfCaps.maxImageCount();
        }

        int preTransform;
        if ((surfCaps.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
            preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
        } else {
            preTransform = surfCaps.currentTransform();
        }
        surfCaps.free();

        var swapchainCI = VkSwapchainCreateInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                .surface(surface)
                .minImageCount(desiredNumberOfSwapchainImages)
                .imageFormat(colorFormat.colorFormat)
                .imageColorSpace(colorFormat.colorSpace)
                .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                .preTransform(preTransform)
                .imageArrayLayers(1)
                .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                .presentMode(swapchainPresentMode)
                .oldSwapchain(oldSwapChain)
                .clipped(true)
                .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
        swapchainCI.imageExtent()
                .width(extents.x)
                .height(extents.y);

        LongBuffer pSwapChain = memAllocLong(1);
        VkUtil.catchVulkanException(vkCreateSwapchainKHR(device, swapchainCI, null, pSwapChain));
        swapchainCI.free();
        long swapChain = pSwapChain.get(0);
        memFree(pSwapChain);

        // If we just re-created an existing swapchain, we should destroy the old swapchain at this point.
        // Note: destroying the swapchain also cleans up all its associated presentable images once the platform is done with them.
        if (oldSwapChain != VK_NULL_HANDLE) {
            vkDestroySwapchainKHR(device, oldSwapChain, null);
        }

        this.imageViews = getImageViews(device, colorFormat, swapChain);
        this.swapchainHandle = swapChain;
    }

    private VulkanImage.View[] getImageViews(VkDevice device, VulkanWindow.ColorDepthData colorFormat, long swapChain) {
        IntBuffer pImageCount = memAllocInt(1);
        VkUtil.catchVulkanException(vkGetSwapchainImagesKHR(device, swapChain, pImageCount, null));
        int imageCount = pImageCount.get(0);


        LongBuffer pSwapchainImages = memAllocLong(imageCount);
        VkUtil.catchVulkanException(vkGetSwapchainImagesKHR(device, swapChain, pImageCount, pSwapchainImages));
        memFree(pImageCount);

        VulkanImage.View[] views = new VulkanImage.View[imageCount];
        for (int i = 0; i < imageCount; i++) {
            var image = new VulkanImage(pSwapchainImages.get(i));
            var view = image.getImageView(VK_IMAGE_VIEW_TYPE_2D, colorFormat.colorFormat, VK_IMAGE_ASPECT_COLOR_BIT);
            views[i] = view;
        }

        memFree(pSwapchainImages);
        return views;
    }

    private VkSurfaceCapabilitiesKHR getCapabilities(VkPhysicalDevice physicalDevice, long surface) {
        VkSurfaceCapabilitiesKHR surfCaps = VkSurfaceCapabilitiesKHR.calloc();
        VkUtil.catchVulkanException(vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfCaps));
        return surfCaps;
    }

    private Vector2i calculateExtents(Vector2i defaults, VkSurfaceCapabilitiesKHR surfCaps) {
        VkExtent2D currentExtent = surfCaps.currentExtent();
        int currentWidth = currentExtent.width();
        int currentHeight = currentExtent.height();
        if (currentWidth != -1 && currentHeight != -1) {
            return new Vector2i(currentWidth, currentHeight);
        } else {
            return defaults;
        }
    }

    private int getSwapchainPresentMode(VkPhysicalDevice physicalDevice, long surface, int preferredMode) {
        IntBuffer pPresentModeCount = memAllocInt(1);
        VkUtil.catchVulkanException(vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, null));
        int presentModeCount = pPresentModeCount.get(0);


        IntBuffer pPresentModes = memAllocInt(presentModeCount);
        VkUtil.catchVulkanException(vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, pPresentModes));
        memFree(pPresentModeCount);


        // Try to use mailbox mode. Low latency and non-tearing
        int swapchainPresentMode = VK_PRESENT_MODE_FIFO_KHR;
        for (int i = 0; i < presentModeCount; i++) {
            if (pPresentModes.get(i) == preferredMode) {
                swapchainPresentMode = preferredMode;
                break;
            }
            if ((swapchainPresentMode != preferredMode) && (pPresentModes.get(i) == VK_PRESENT_MODE_IMMEDIATE_KHR)) {
                swapchainPresentMode = preferredMode;
            }
        }
        memFree(pPresentModes);
        return swapchainPresentMode;
    }

    public long getSwapchainHandle() {
        return swapchainHandle;
    }

    public VulkanImage.View[] getImageViews() {
        return imageViews;
    }

    public Vector2i getExtents() {
        return extents;
    }
}
