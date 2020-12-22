package com.opengg.core.render.internal.vulkan;

import com.opengg.core.engine.GGDebugRenderer;
import com.opengg.core.engine.GGGameConsole;
import com.opengg.core.gui.GUIController;
import com.opengg.core.math.*;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.RenderOperation;
import com.opengg.core.render.Renderer;
import com.opengg.core.render.objects.DrawnObject;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipeline;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipelineCache;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipelineFormat;
import com.opengg.core.render.internal.vulkan.shader.VulkanShaderPipeline;
import com.opengg.core.render.internal.vulkan.texture.VulkanFramebuffer;
import com.opengg.core.render.internal.vulkan.texture.VulkanImage;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.system.Allocator;
import com.opengg.core.util.Time;
import com.opengg.core.world.Camera;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanRenderer implements Renderer {

    private static VulkanWindow window;
    VulkanPipeline pipeline;

    private Swapchain swapchain;
    private VulkanFramebuffer[] finalFramebuffers;
    private VulkanCommandBuffer[] finalCommandBuffers;

    private VulkanCommandBuffer currentBuffer;
    private VulkanFramebuffer currentFramebuffer;

    /**
     * This is just -1L, but it is nicer as a symbolic constant.
     */
    private final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;
    private VkSemaphoreCreateInfo semaphoreCreateInfo;
    private VkSubmitInfo submitInfo;
    private VkPresentInfoKHR presentInfo;
    private IntBuffer pImageIndex;
    private LongBuffer pRenderCompleteSemaphore;
    private LongBuffer pImageAcquiredSemaphore;
    private LongBuffer pSwapchains;
    private PointerBuffer pCommandBuffers;
    private VkQueue queue;

    private VulkanImage.View depthStencil;
    private VulkanCommandPool commonPool;
    private VulkanDescriptorPoolPool poolPool;
    private VulkanDescriptorSetCache descriptorSetCache;
    private VulkanRenderPass renderPass;
    VulkanPipelineFormat format;

    private ByteBuffer lightBuffer;
    private int lightOffset;

    private final Time t = new Time();

    public VulkanRenderer() {
    }

    @Override
    public void initialize() {
        window.initialize();
        descriptorSetCache = new VulkanDescriptorSetCache(4);
        poolPool = new VulkanDescriptorPoolPool();
        commonPool = createCommandPool(window.getQueueFamilyIndex());
        queue = createDeviceQueue(window.getQueueFamilyIndex());
        VulkanPipelineCache.initialize();
        ShaderController.initialize();
        createRenderer();
        enableDefaultRenderPaths();

        lightBuffer = Allocator.alloc(1600);
        lightOffset = Light.BUFFERSIZE;
    }

    private void createRenderer() {
        VulkanCommandBuffer setupCommandBuffer = createCommandBuffer(createCommandPool(window.getQueueFamilyIndex()));
        VulkanCommandPool renderCommandPool = createCommandPool(window.getQueueFamilyIndex());

        renderPass = createRenderPass(window.getColorFormatAndSpace());

        regenerateSwapchain(setupCommandBuffer, renderCommandPool);
        WindowController.addResizeListener((non) -> regenerateSwapchain(setupCommandBuffer, renderCommandPool));

        // Pre-allocate everything needed in the render loop
        pImageIndex = memAllocInt(1);
        pCommandBuffers = memAllocPointer(1);
        pSwapchains = memAllocLong(1);
        pImageAcquiredSemaphore = memAllocLong(1);
        pRenderCompleteSemaphore = memAllocLong(1);


        // Info struct to create a semaphore
        semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

        // Info struct to submit a command buffer which will wait on the semaphore
        IntBuffer pWaitDstStageMask = memAllocInt(1);
        pWaitDstStageMask.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
        submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .waitSemaphoreCount(pImageAcquiredSemaphore.remaining())
                .pWaitSemaphores(pImageAcquiredSemaphore)
                .pWaitDstStageMask(pWaitDstStageMask)
                .pCommandBuffers(pCommandBuffers)
                .pSignalSemaphores(pRenderCompleteSemaphore);

        // Info struct to present the current swapchain image to the display
        presentInfo = VkPresentInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pWaitSemaphores(pRenderCompleteSemaphore)
                .swapchainCount(pSwapchains.remaining())
                .pSwapchains(pSwapchains)
                .pImageIndices(pImageIndex);

    }

    private void enableDefaultRenderPaths(){
        RenderOperation path = new RenderOperation("mainpath", () -> {
            for(var group : RenderEngine.getActiveRenderUnits()){
                setInputFormat(group.renderableUnitProperties().format());
                group.renderable().render();
            }
        });

        RenderOperation skybox = new RenderOperation("skyboxpath", () -> {
            setCulling(false);
            if(RenderEngine.getCurrentEnvironment().getSkybox() != null){
                setInputFormat(RenderEngine.getDefaultFormat());
                ShaderController.useConfiguration("sky");
                CommonUniforms.setModel(new Matrix4f());
                ShaderController.setUniform("cubemap", RenderEngine.getCurrentEnvironment().getSkybox().getCubemap());
                RenderEngine.getCurrentEnvironment().getSkybox().getDrawable().render();

            }
            setCulling(true);
        });

        RenderEngine.addRenderPath(skybox);
        RenderEngine.addRenderPath(path);

    }

    private void regenerateSwapchain(VulkanCommandBuffer setupCommandBuffer, VulkanCommandPool renderCommandPool){

        setupCommandBuffer.begin();

        long oldChain = swapchain != null ? swapchain.getSwapchainHandle() : VK_NULL_HANDLE;
        // Create the swapchain (this will also add a memory barrier to initialize the framebuffer images)
        swapchain = new Swapchain(window.getDevice(), window.getPhysicalDevice(), window.getSurface(), oldChain,
                new Vector2i(0,0), window.getColorFormatAndSpace());
        depthStencil = createDepthStencil();
        setupCommandBuffer.end();
        setupCommandBuffer.submitAndWait();

        if (finalFramebuffers != null) {
            for (var fb : finalFramebuffers)
                fb.destroy();
        }
        finalFramebuffers = createFramebuffers(swapchain, renderPass, depthStencil);
        // Create render command buffers
        if (finalCommandBuffers != null) {
            vkResetCommandPool(window.getDevice(), renderCommandPool.getPool(), VkUtil.VK_FLAGS_NONE);
        }
        finalCommandBuffers = VulkanCommandBuffer.allocate(renderCommandPool, finalFramebuffers.length);
    }

    private VulkanCommandPool createCommandPool(int queueNodeIndex) {
        return new VulkanCommandPool(queueNodeIndex, VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
    }

    private VulkanCommandBuffer createCommandBuffer(VulkanCommandPool commandPool) {
        return new VulkanCommandBuffer(commandPool);
    }

    private VkQueue createDeviceQueue(int queueFamilyIndex) {
        PointerBuffer pQueue = memAllocPointer(1);
        vkGetDeviceQueue(window.getDevice(), queueFamilyIndex, 0, pQueue);
        long queue = pQueue.get(0);
        memFree(pQueue);
        return new VkQueue(queue, window.getDevice());
    }

    private VulkanImage.View createDepthStencil() {
        var image = new VulkanImage(new Texture.TextureConfig(Texture.TextureType.TEXTURE_2D, Texture.FilterType.LINEAR, Texture.FilterType.LINEAR, Texture.WrapType.CLAMP_BORDER,Texture.WrapType.CLAMP_BORDER,Texture.WrapType.CLAMP_BORDER,
                Texture.SamplerFormat.DEPTH, Texture.TextureFormat.DEPTH32_STENCIL8, Texture.InputFormat.FLOAT, false),
                VK_SAMPLE_COUNT_1_BIT, VK_IMAGE_TILING_OPTIMAL, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
                new Vector3i(swapchain.getExtents().x, swapchain.getExtents().y, 1), 1);

        return image.getImageView(VK_IMAGE_ASPECT_DEPTH_BIT | VK_IMAGE_ASPECT_STENCIL_BIT);
    }

    private static VulkanRenderPass createRenderPass(VulkanWindow.ColorDepthData format) {
        VulkanAttachment colorAttachment = new VulkanAttachment(format.colorFormat, VK_SAMPLE_COUNT_1_BIT, VK_ATTACHMENT_LOAD_OP_CLEAR, VK_ATTACHMENT_STORE_OP_STORE, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);
        VulkanAttachment depthAttachment = new VulkanAttachment(VK_FORMAT_D32_SFLOAT_S8_UINT, VK_SAMPLE_COUNT_1_BIT, VK_ATTACHMENT_LOAD_OP_CLEAR, VK_ATTACHMENT_STORE_OP_STORE, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

        VulkanAttachment.Reference colorReference = new VulkanAttachment.Reference(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
        VulkanAttachment.Reference depthReference = new VulkanAttachment.Reference(1, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);

        VulkanRenderPass.Subpass subpass = new VulkanRenderPass.Subpass(VK_PIPELINE_BIND_POINT_GRAPHICS, List.of(colorReference), depthReference);
        VulkanRenderPass renderPass = new VulkanRenderPass(List.of(colorAttachment, depthAttachment), List.of(subpass));

        return renderPass;
    }

    private static VulkanFramebuffer[] createFramebuffers(Swapchain swapchain, VulkanRenderPass renderPass, VulkanImage.View depthStencil) {
        VulkanFramebuffer[] framebuffers = new VulkanFramebuffer[swapchain.getImageViews().length];
        for (int i = 0; i < swapchain.getImageViews().length; i++) {
            framebuffers[i] = new VulkanFramebuffer(List.of(swapchain.getImageViews()[i], depthStencil), swapchain.getExtents(), renderPass);
        }
        return framebuffers;
    }

    private VulkanPipelineFormat createDefaultPipelineFormat(VulkanRenderPass renderPass) {
        return new VulkanPipelineFormat(
                new VulkanPipeline.VertexInputState(RenderEngine.getDefaultFormat()),
                new VulkanPipeline.InputAssemblyState(DrawnObject.DrawType.TRIANGLES),
                new VulkanPipeline.RasterizationState(VK_POLYGON_MODE_FILL, VK_CULL_MODE_NONE, VK_FRONT_FACE_COUNTER_CLOCKWISE),
                new VulkanPipeline.MultisampleState(VK_SAMPLE_COUNT_1_BIT),
                new VulkanPipeline.ColorBlendState(0xF),
                new VulkanPipeline.DepthStencilState(true, true, VK_COMPARE_OP_LESS_OR_EQUAL),
                (VulkanShaderPipeline) ShaderController.getConfiguration("texture"),
                renderPass
        );
    }

    @Override
    public void render() {
        if(swapchain == null) return;
        selectCurrentScreenCommandBuffer();
        descriptorSetCache.createNewCache();

        format = createDefaultPipelineFormat(renderPass);
        var pipeline = VulkanPipelineCache.getPipeline(format);
        // Select the command buffer for the current framebuffer image/texture
        // Submit to the graphics queue
        var renderPassBeginInfo = renderPass.generateInfo(new Vector3f(), currentFramebuffer, new Vector2i(), swapchain.getExtents());
        currentBuffer.begin();
        currentBuffer.startRenderPass(renderPassBeginInfo);
        currentBuffer.setViewportScissor(swapchain.getExtents());
        currentBuffer.bindPipeline(pipeline);
        useLights();

        ShaderController.setUniform("view", RenderEngine.getCurrentView().getMatrix());
        ShaderController.setUniform("projection", RenderEngine.getProjectionData().getMatrix());

        for(RenderOperation path : RenderEngine.getActiveRenderPaths()){
            path.render();
        }

        setInputFormat(RenderEngine.getDefaultFormat());

        ShaderController.setUniform("model", new Matrix4f());
        CommonUniforms.setOrtho(0, 1, 0, 1, -1, 1);
        CommonUniforms.setView(new Camera().getMatrix());

        setCulling(false);

        GUIController.render();

        setDepthCheck(false);
        GGGameConsole.render();
        GGDebugRenderer.render();
        setDepthCheck(true);

        setCulling(true);

        currentBuffer.endRenderPass();
        currentBuffer.end();

        presentToScreen();
    }

    public void selectCurrentScreenCommandBuffer(){
        // Create a semaphore to wait for the swapchain to acquire the next image
        VkUtil.catchVulkanException(vkCreateSemaphore(window.getDevice(), semaphoreCreateInfo, null, pImageAcquiredSemaphore));
        VkUtil.catchVulkanException(vkCreateSemaphore(window.getDevice(), semaphoreCreateInfo, null, pRenderCompleteSemaphore));

        // Get next image from the swap chain (back/front buffer).
        // This will setup the imageAquiredSemaphore to be signalled when the operation is complete
        VkUtil.catchVulkanException(vkAcquireNextImageKHR(window.getDevice(), swapchain.getSwapchainHandle(), UINT64_MAX, pImageAcquiredSemaphore.get(0), VK_NULL_HANDLE, pImageIndex));
        int currentBuffer = pImageIndex.get(0);
        pCommandBuffers.put(0, finalCommandBuffers[currentBuffer].getBuffer());
        this.currentBuffer = finalCommandBuffers[currentBuffer];
        this.currentFramebuffer = finalFramebuffers[currentBuffer];
    }

    public void presentToScreen(){
        VkUtil.catchVulkanException(vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE));

        // Present the current buffer to the swap chain
        // This will display the image
        pSwapchains.put(0, swapchain.getSwapchainHandle());
        VkUtil.catchVulkanException(vkQueuePresentKHR(queue, presentInfo));

        // Create and submit post present barrier
        vkQueueWaitIdle(queue);

        // Destroy this semaphore (we will create a new one in the next frame)
        vkDestroySemaphore(window.getDevice(), pImageAcquiredSemaphore.get(0), null);
        vkDestroySemaphore(window.getDevice(), pRenderCompleteSemaphore.get(0), null);
    }

    private void useLights(){
        var allLights = RenderEngine.getActiveLights();
        for(int i = 0; i < allLights.size(); i++){
            lightBuffer.asFloatBuffer().position(i * lightOffset).put(allLights.get(i).getBuffer());
        }
        ShaderController.setUniform("numLights", allLights.size());
        ShaderController.setUniform("lights[LIGHTNUM]", lightBuffer);
    }

    private void setCulling(boolean cull){
        var pipeline = VulkanPipelineCache.getPipeline(
                VulkanRenderer.getRenderer().getCurrentPipeline().getFormat().setRasterizer(
                        new VulkanPipeline.RasterizationState(VK_POLYGON_MODE_FILL, cull ? VK_CULL_MODE_BACK_BIT : VK_CULL_MODE_NONE, VK_FRONT_FACE_COUNTER_CLOCKWISE)));
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindPipeline(pipeline);
    }

    private void setInputFormat(VertexArrayFormat format){
        var pipeline = VulkanPipelineCache.getPipeline(
                VulkanRenderer.getRenderer().getCurrentPipeline().getFormat().setVertexInput(new VulkanPipeline.VertexInputState(format)));
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindPipeline(pipeline);
    }

    private void setDepthCheck(boolean depthCheck){
        var pipeline = VulkanPipelineCache.getPipeline(
                VulkanRenderer.getRenderer().getCurrentPipeline().getFormat().setDepthStencil(new VulkanPipeline.DepthStencilState(depthCheck, depthCheck, VK_COMPARE_OP_LESS_OR_EQUAL)));
        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindPipeline(pipeline);
    }

    @Override
    public void startFrame() {

    }

    @Override
    public void endFrame() {

    }

    @Override
    public void destroy() {
    }

    public static VulkanRenderer getRenderer(){
        return (VulkanRenderer) RenderEngine.getRenderer();
    }

    public VkDevice getDevice() {
        return window.getDevice();
    }

    public VulkanWindow getWindow(){
        return window;
    }

    public static void setWindow(VulkanWindow window){
        VulkanRenderer.window = window;
    }

    public VulkanCommandBuffer getCurrentCommandBuffer(){
        return currentBuffer;
    }

    public VulkanPipeline getCurrentPipeline(){
        return this.pipeline;
    }

    public VulkanCommandPool getCommandPool() {
        return commonPool;
    }

    public VulkanDescriptorPoolPool getDescriptorPoolPool(){
        return poolPool;
    }

    public VulkanDescriptorSetCache getDescriptorSetCache(){
        return descriptorSetCache;
    }

    public VkQueue getQueue(){
        return queue;
    }

}
