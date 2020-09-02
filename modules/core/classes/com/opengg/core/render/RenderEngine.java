package com.opengg.core.render;

import com.opengg.core.GGInfo;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.exceptions.RenderException;
import com.opengg.core.gui.GUIController;
import com.opengg.core.model.ModelManager;
import com.opengg.core.physics.PhysicsRenderer;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.internal.vulkan.VulkanRenderer;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.shader.VertexArrayBinding;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.world.Camera;
import com.opengg.core.world.Skybox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class RenderEngine {
    private static final boolean debug = true;
    private static final List<RenderGroup> groups = new ArrayList<>();
    private static final List<RenderOperation> paths = new ArrayList<>();
    private static final List<RenderPass> passes = new ArrayList<>();
    private static final List<Light> lights = new ArrayList<>();
    public static Renderer renderer;
    private static boolean initialized;
    private static RenderEnvironment currentEnvironment;
    private static VertexArrayFormat defaultVAOFormat;
    private static VertexArrayFormat particleVAOFormat;
    private static VertexArrayFormat animationVAOFormat;
    private static VertexArrayFormat animation2VAOFormat;
    public static VertexArrayFormat superCSCFormat;
    private static VertexArrayFormat tangentVAOFormat;
    private static VertexArrayFormat tangentAnimVAOFormat;
    private static RenderGroup defaultList;
    private static View camera = new Camera();
    private static ProjectionData projectionData;
    private boolean bindSkyboxToCamera = false;

    public static void initialize(WindowOptions opts) {
        initializeForHeadless();
        initialized = true;

        renderer = switch (opts.renderer) {
            case OPENGL -> new OpenGLRenderer();
            case VULKAN -> new VulkanRenderer();
        };

        renderer.initialize();

        TextureManager.initialize();
        ModelManager.initialize();
        PhysicsRenderer.initialize();
        GUIController.initialize();

        View c = new Camera();
        useView(c);
        setProjectionData(ProjectionData.getPerspective(90, 0.2f, 3000f));
        defaultList = new RenderGroup("defaultgroup");
        defaultList.setPipeline("object");

        groups.add(defaultList);
    }


    public static void initializeForHeadless() {

        defaultVAOFormat = new VertexArrayFormat(List.of(
                new VertexArrayBinding(0, 8 * 4, 0, List.of(
                        new VertexArrayBinding.VertexArrayAttribute("position", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 0),
                        new VertexArrayBinding.VertexArrayAttribute("normal", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 3 * 4),
                        new VertexArrayBinding.VertexArrayAttribute("texcoord", 2 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT2, 6 * 4)
                ))
        ));

        superCSCFormat = new VertexArrayFormat(List.of(
                new VertexArrayBinding(0, 10 * 4, 0, List.of(
                        new VertexArrayBinding.VertexArrayAttribute("position", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 0),
                        new VertexArrayBinding.VertexArrayAttribute("normal", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 3 * 4),
                        new VertexArrayBinding.VertexArrayAttribute("texcoord", 2 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT2, 6 * 4),
                        new VertexArrayBinding.VertexArrayAttribute("lightmapcoord", 2 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT2, 8 * 4)
                ))
        ));

        tangentVAOFormat = new VertexArrayFormat(List.of(new VertexArrayBinding(0, 11 * 4, 0, List.of(
                new VertexArrayBinding.VertexArrayAttribute("position", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 0),
                new VertexArrayBinding.VertexArrayAttribute("normal", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 3 * 4),
                new VertexArrayBinding.VertexArrayAttribute("tangent", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 6 * 4),
                new VertexArrayBinding.VertexArrayAttribute("texcoord", 2 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT2, 9 * 4)
        ))));

        tangentAnimVAOFormat = new VertexArrayFormat(List.of(new VertexArrayBinding(0, 19 * 4, 0, List.of(
                new VertexArrayBinding.VertexArrayAttribute("position", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 0),
                new VertexArrayBinding.VertexArrayAttribute("normal", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 3 * 4),
                new VertexArrayBinding.VertexArrayAttribute("tangent", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 6 * 4),
                new VertexArrayBinding.VertexArrayAttribute("texcoord", 2 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT2, 9 * 4),
                new VertexArrayBinding.VertexArrayAttribute("jointindex", 4 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT4, 11 * 4),
                new VertexArrayBinding.VertexArrayAttribute("weights", 4 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT4, 15 * 4)
        ))));

        particleVAOFormat = new VertexArrayFormat(List.of(new VertexArrayBinding(0, 8 * 4, 0, List.of(
                new VertexArrayBinding.VertexArrayAttribute("position", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 0),
                new VertexArrayBinding.VertexArrayAttribute("normal", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 3 * 4),
                new VertexArrayBinding.VertexArrayAttribute("texcoord", 2 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT2, 6 * 4)
                )),
                new VertexArrayBinding(1, 3 * 4, 1, List.of(
                        new VertexArrayBinding.VertexArrayAttribute("offset", 3 * 4, VertexArrayBinding.VertexArrayAttribute.Type.FLOAT3, 0)
                ))
        ));

    }

    public static void render() {
        renderer.render();
    }

    public static void sortOrders() {
        groups.sort(Comparator.comparingInt(RenderGroup::getOrder));
    }

    public static void endFrame() {
        WindowController.getWindow().endFrame();
        renderer.endFrame();
    }

    public static void startFrame() {
        WindowController.getWindow().startFrame();
        renderer.startFrame();
    }

    public static void addRenderGroup(RenderGroup r) {
        if (!groups.contains(r))
            groups.add(r);
    }

    public static RenderGroup getRenderGroup(String name) {
        for (RenderGroup r : groups)
            if (r.getName().equals(name)) return r;

        return null;
    }

    public static List<RenderGroup> getRenderGroups() {
        return groups;
    }

    public static void addRenderPass(RenderPass pass) {
        passes.add(pass);
    }

    public static List<RenderPass> getRenderPasses() {
        return passes;
    }

    public static List<RenderGroup> getActiveRenderGroups() {
        ArrayList<RenderGroup> list = new ArrayList<>(groups.size());

        for (RenderGroup r : groups)
            if (r.isEnabled())
                list.add(r);

        for (RenderGroup r : currentEnvironment.getGroups())
            if (r.isEnabled())
                list.add(r);

        return list;
    }

    public static void removeRenderGroup(RenderGroup r) {
        groups.remove(r);
    }

    public static void addRenderPath(RenderOperation r) {
        paths.add(r);
    }

    public static RenderOperation getRenderPath(String name) {
        for (RenderOperation r : paths)
            if (r.getName().equals(name)) return r;

        return null;
    }

    public static List<RenderOperation> getRenderPaths() {
        return paths;
    }

    public static List<RenderOperation> getActiveRenderPaths() {
        ArrayList<RenderOperation> list = new ArrayList<>();

        for (RenderOperation r : paths)
            if (r.isEnabled())
                list.add(r);

        return list;
    }


    public static List<Light> getActiveLights() {
        var lights = new ArrayList<Light>();

        for (var light : RenderEngine.lights)
            if (light.isActive())
                lights.add(light);

        for (var light : RenderEngine.getCurrentEnvironment().getLights())
            if (light.isActive())
                lights.add(light);

        return lights;
    }

    public static void removeRenderPath(RenderOperation r) {
        paths.remove(r);
    }

    public static void addRenderable(Renderable r) {
        defaultList.add(r);
    }

    public static Skybox getSkybox() {
        return currentEnvironment.getSkybox();
    }

    public static RenderEnvironment getCurrentEnvironment() {
        return currentEnvironment;
    }

    public static void setCurrentEnvironment(RenderEnvironment currentEnvironment) {
        RenderEngine.currentEnvironment = currentEnvironment;
    }

    public static void useView(View c) {
        camera = c;
    }

    public static View getCurrentView() {
        return camera;
    }

    public static ProjectionData getProjectionData() {
        return projectionData;
    }

    public static void setProjectionData(ProjectionData data) {
        projectionData = data;
    }

    public static VertexArrayFormat getDefaultFormat() {
        return defaultVAOFormat;
    }

    public static VertexArrayFormat getParticleFormat() {
        return particleVAOFormat;
    }

    public static VertexArrayFormat getAnimationFormat() {
        return animationVAOFormat;
    }

    public static VertexArrayFormat getTangentVAOFormat() {
        return tangentVAOFormat;
    }

    public static VertexArrayFormat getTangentAnimVAOFormat() {
        return tangentAnimVAOFormat;
    }

    public static WindowOptions.RendererType getRendererType() {
        return OpenGG.getInitOptions().getWindowOptions().renderer;
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean validateInitialization() {
        if (!GGInfo.isServer() && !initialized) throw new RenderException("OpenGL is not initialized!");
        return !initialized;
    }

    public static void destroy() {
        renderer.destroy();
    }

    public void setBindSkyboxToCamera(boolean bindSkyboxToCamera) {
        this.bindSkyboxToCamera = bindSkyboxToCamera;
    }


}
