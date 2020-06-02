package com.opengg.core.render.internal.vulkan;

import org.lwjgl.vulkan.VkInstance;

public class VkInitPackage {
    public VkInstance vulkanInstance;
    public long surface;

    public VkInitPackage(VkInstance vulkanInstance, long surface) {
        this.vulkanInstance = vulkanInstance;
        this.surface = surface;
    }
}
