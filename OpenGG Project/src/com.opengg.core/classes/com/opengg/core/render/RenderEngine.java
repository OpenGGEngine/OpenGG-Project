/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.GGGameConsole;
import com.opengg.core.exceptions.RenderException;
import com.opengg.core.gui.GUIController;
import com.opengg.core.model.ModelManager;
import com.opengg.core.physics.PhysicsRenderer;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.postprocess.PostProcessController;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayAttribute;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.render.texture.WindowFramebuffer;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.Camera;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldEngine;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_MAJOR_VERSION;
import static org.lwjgl.opengl.GL30.GL_MINOR_VERSION;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

/**
 * 
 * @author Javier
 */
public class RenderEngine {
    public static final Object glLock = new Object();

    private static boolean initialized;

    private static RenderEnvironment currentEnvironment;

    private static final List<RenderGroup> groups = new ArrayList<>();
    private static final List<Light> lights = new ArrayList<>();
    private static final List<RenderOperation> paths = new ArrayList<>();
    private static final List<RenderPass> passes = new ArrayList<>();

    private static VertexArrayFormat defaultVAOFormat;
    private static VertexArrayFormat particleVAOFormat;
    private static VertexArrayFormat animationVAOFormat;
    public static VertexArrayFormat animation2VAOFormat;
    public static VertexArrayFormat tangentVAOFormat;
    public static VertexArrayFormat tangentAnimVAOFormat;

    private static boolean cull = true;
    private static int lightoffset;
    private static View camera;

    private static VertexArrayObject currentvao;
    private static VertexArrayObject defaultvao;
    private static ProjectionData projdata;
    private static GraphicsBuffer lightBuffer;
    private static RenderGroup defaultList;
    private static Framebuffer currentFramebuffer;

    /**
     * Initializes the render engine, should rarely if ever be called
     */
    public static void initialize() {
        initialized = true;

        ShaderController.initialize();

        defaultVAOFormat = new VertexArrayFormat();
        defaultVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 12, GL_FLOAT, 0, 0, false));
        //defaultVAOFormat.addAttribute(new VertexArrayAttribute("color", 4, 12, GL_FLOAT, 3, 0, false));
        defaultVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 12, GL_FLOAT, 7, 0, false));
        defaultVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 12, GL_FLOAT, 10, 0, false));

        tangentVAOFormat = new VertexArrayFormat();
        tangentVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 11, GL_FLOAT, 0, 0, false));
        tangentVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 11, GL_FLOAT, 3, 0, false));
        tangentVAOFormat.addAttribute(new VertexArrayAttribute("tangents", 3, 11, GL_FLOAT, 6, 0, false));
        tangentVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 11, GL_FLOAT, 9, 0, false));

        tangentAnimVAOFormat = new VertexArrayFormat();
        tangentAnimVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 19, GL_FLOAT, 0, 0, false));
        tangentAnimVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 19, GL_FLOAT, 3, 0, false));
        tangentAnimVAOFormat.addAttribute(new VertexArrayAttribute("tangents", 3, 19, GL_FLOAT, 6, 0, false));
        tangentAnimVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 19, GL_FLOAT, 9, 0, false));
        tangentAnimVAOFormat.addAttribute(new VertexArrayAttribute("jointindex", 4, 19, GL_FLOAT, 11, 0, false));
        tangentAnimVAOFormat.addAttribute(new VertexArrayAttribute("weights", 4, 19, GL_FLOAT, 15, 0, false));

        particleVAOFormat = new VertexArrayFormat();
        particleVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 12, GL_FLOAT, 0, 0, false));
        particleVAOFormat.addAttribute(new VertexArrayAttribute("offset", 3, 3, GL_FLOAT, 0, 1, true));
        particleVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 12, GL_FLOAT, 7, 0, false));
        particleVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 12, GL_FLOAT, 10, 0, false));

        animationVAOFormat = new VertexArrayFormat();
        animationVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 20, GL_FLOAT, 0, 0, false));
        //animationVAOFormat.addAttribute(new VertexArrayAttribute("color", 4, 20, GL_FLOAT, 3, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 20, GL_FLOAT, 7, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 20, GL_FLOAT, 10, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("jointindex", 4, 20, GL_FLOAT, 12, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("weights", 4, 20, GL_FLOAT, 16, 0, false));

        animation2VAOFormat = new VertexArrayFormat();
        animation2VAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 19, GL_FLOAT, 0, 0, false));
        //animationVAOFormat.addAttribute(new VertexArrayAttribute("color", 4, 20, GL_FLOAT, 3, 0, false));
        animation2VAOFormat.addAttribute(new VertexArrayAttribute("tangent", 3, 19, GL_FLOAT, 3, 0, false));
        animation2VAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 19, GL_FLOAT, 6, 0, false));
        animation2VAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 19, GL_FLOAT, 9, 0, false));
        animation2VAOFormat.addAttribute(new VertexArrayAttribute("jointindex", 4, 19, GL_FLOAT, 11, 0, false));
        animation2VAOFormat.addAttribute(new VertexArrayAttribute("weights", 4, 19, GL_FLOAT, 15, 0, false));

        defaultvao = new VertexArrayObject(defaultVAOFormat);

        passes.add(new RenderPass(true, true, () -> {}, f -> {}));

        enableDefaultGroups();

        lightBuffer = GraphicsBuffer.allocate(GL_UNIFORM_BUFFER, 1600, GL_DYNAMIC_DRAW);
        lightBuffer.bindBase(ShaderController.getUniqueUniformBufferLocation());
        lightoffset = (Allocator.allocFloat(Light.BUFFERSIZE).capacity());// << 2;

        ShaderController.setUniformBlockLocation(lightBuffer, "LightBuffer");

        View c = new Camera();
        useView(c);
        setProjectionData(ProjectionData.getPerspective(100, 0.2f, 3000f));

        GLOptions.set(GL_BLEND, true);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        GLOptions.set(GL_TEXTURE_CUBE_MAP_SEAMLESS, true);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        GGConsole.log("Enabling rendering submanagers");

        TextureManager.initialize();
        ModelManager.initialize();
        GUIController.initialize();
        PostProcessController.initialize();
        PhysicsRenderer.initialize();

        GGConsole.log("Render engine initialized");
    }

    public static void initializeForHeadless() {
        defaultVAOFormat = new VertexArrayFormat();
        defaultVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 12, GL_FLOAT, 0, 0, false));
        //defaultVAOFormat.addAttribute(new VertexArrayAttribute("color", 4, 12, GL_FLOAT, 3, 0, false));
        defaultVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 12, GL_FLOAT, 7, 0, false));
        defaultVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 12, GL_FLOAT, 10, 0, false));

        particleVAOFormat = new VertexArrayFormat();
        particleVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 12, GL_FLOAT, 0, 0, false));
        particleVAOFormat.addAttribute(new VertexArrayAttribute("offset", 3, 3, GL_FLOAT, 0, 1, true));
        particleVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 12, GL_FLOAT, 7, 0, false));
        particleVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 12, GL_FLOAT, 10, 0, false));

        animationVAOFormat = new VertexArrayFormat();
        animationVAOFormat.addAttribute(new VertexArrayAttribute("position", 3, 20, GL_FLOAT, 0, 0, false));
        //animationVAOFormat.addAttribute(new VertexArrayAttribute("color", 4, 20, GL_FLOAT, 3, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("normal", 3, 20, GL_FLOAT, 7, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("texcoord", 2, 20, GL_FLOAT, 10, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("jointindex", 4, 20, GL_FLOAT, 12, 0, false));
        animationVAOFormat.addAttribute(new VertexArrayAttribute("weights", 4, 20, GL_FLOAT, 16, 0, false));

        TextureManager.initialize();
        ModelManager.initialize();
    }

    public static void render(){
        for(RenderPass pass : passes){
            pass.runEnableOp();

            ShaderController.setView(camera.getMatrix());
            ShaderController.setUniform("camera", camera.getPosition().inverse());
            projdata.ratio = WindowController.getWindow().getRatio();
            projdata.use();

            pass.getSceneBuffer().enableRendering();
            pass.getSceneBuffer().useEnabledAttachments();
            useLights();
            resetConfig();
            defaultvao.bind();

            for(RenderOperation path : getActiveRenderPaths()){
                path.render();
                resetConfig();
            }
            pass.getSceneBuffer().disableRendering();

            defaultvao.bind();

            enableDefaultVP();

            if(pass.isPostProcessEnabled())
                PostProcessController.process(pass.getSceneBuffer());

            //if(pass.shouldBlitToBack())
            //    pass.getSceneBuffer().blitToBack();

            RenderEngine.setCulling(true);

            pass.runDisableOp();
        }

        GUIController.render();
        GGGameConsole.render();
    }

    private static void enableDefaultGroups(){
        defaultList = new RenderGroup("defaultgroup");
        defaultList.setPipeline("object");

        groups.add(defaultList);

        RenderOperation skybox = new RenderOperation("skyboxpath", () -> {
            if(currentEnvironment.getSkybox() != null){
                ShaderController.useConfiguration("sky");
                currentEnvironment.getSkybox().getCubemap().use(2);
                currentEnvironment.getSkybox().getDrawable().render();
            }
        });
        
        RenderOperation path = new RenderOperation("mainpath", () -> {
            for(RenderGroup d : getActiveRenderGroups()){
                ShaderController.useConfiguration(d.getPipeline());
                d.render(); 
            }
        });
        
        RenderOperation light = new RenderOperation("shadowmap", () -> {
            int used = 0;
            var lights = getActiveLights();
            glCullFace(GL_FRONT);
            var fb = getCurrentFramebuffer();
            for(int i = 0; i < lights.size() && used < 2; i++){
                if(lights.get(i).hasShadow()){
                    lights.get(i).initializeRender();

                    for(RenderGroup d : getActiveRenderGroups()){
                        d.render();
                    }

                    lights.get(i).finalizeRender(6 + used);

                    WorldEngine.getCurrent().getRenderEnvironment().setSkybox(
                            new Skybox(lights.get(i).getLightbuffer().getTextures().get(0), 1000f));
                    used++;
                }
            }
            glCullFace(GL_BACK);
            fb.restartRendering();
            fb.useEnabledAttachments();
            useLights();
            ShaderController.setView(camera.getMatrix());
            projdata.use();
            
            for(RenderGroup d : getActiveRenderGroups()){
                ShaderController.useConfiguration(d.getPipeline());
                d.render();
            }
            
        });

        paths.add(skybox);
        paths.add(path);
    }

    static List<Light> getActiveLights(){
        var lights = new ArrayList<Light>(groups.size());

        for(var light : lights)
            if(light.isActive())
                lights.add(light);

        for(var light : currentEnvironment.getLights())
            if(light.isActive())
                lights.add(light);

        return lights;
    }

    static void useLights(){
        var allLights = getActiveLights();
        for(int i = 0; i < allLights.size(); i++){
            lightBuffer.uploadSubData(allLights.get(i).getBuffer(), i * lightoffset);
        }
        ShaderController.setUniform("numLights", allLights.size());
    }
    
    public static void sortOrders(){
        groups.sort((RenderGroup o1, RenderGroup o2) -> {
            if(o1.getOrder() > o2.getOrder()){
                return 1;
            }else if(o1.getOrder() < o2.getOrder()){
                return -1;
            }else{
                return 0;
            }
        });
    }

    public static void resetConfig(){
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        //if(cull)
        //    glEnable(GL_CULL_FACE);
        //else
            glDisable(GL_CULL_FACE);
    }
    
    /**
     * Checks for and prints out any OpenGL errors
     */
    public static void checkForGLErrors(){
        int i = 0;
        while((i = glGetError()) != GL_NO_ERROR){
            GGConsole.warning("OpenGL Error code : " + i);
        }
    }
    
    /**
     * @return The current OpenGL version in a major . minor format
     */
    public static String getGLVersion(){
        return glGetInteger(GL_MAJOR_VERSION) + "." + glGetInteger(GL_MINOR_VERSION);
    }
    
    public static VertexArrayFormat getDefaultFormat(){
        return defaultVAOFormat;
    }
    
    public static VertexArrayFormat getParticleFormat(){
        return particleVAOFormat;
    }

    public static VertexArrayFormat getAnimationFormat(){
        return animationVAOFormat;
    }

    public static VertexArrayObject getCurrentVAO(){
        return currentvao;
    }
    
    public static void setVAO(VertexArrayObject vao){
        currentvao = vao;
        if(vao == null)
            currentvao = defaultvao;
    }
    
    public static VertexArrayObject getDefaultVAO(){
        return defaultvao;
    }
    
    public static void setWireframe(boolean wf){
        if(wf)
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        else
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
    }
    
    public static void addLight(Light l){
        lights.add(l);
    }
    
    public static void removeLight(Light l){
        lights.remove(l);
    }
    
    public static List<Light> getLights(){
        return lights;
    }
    
    public static void addRenderGroup(RenderGroup r){
        if(!groups.contains(r))
            groups.add(r);
    }

    public RenderGroup getRenderGroup(String name){
        for(RenderGroup r : groups)
            if(r.getName().equals(name)) return r;

        return null;
    }

    public static List<RenderGroup> getRenderGroups(){
        return groups;
    }

    public static void addRenderPass(RenderPass pass) {
        passes.add(pass);
    }

    public static List<RenderPass> getRenderPasses(){
        return passes;
    }

    public static List<RenderGroup> getActiveRenderGroups(){
        ArrayList<RenderGroup> list = new ArrayList<>(groups.size());

        for(RenderGroup r : groups)
            if(r.isEnabled())
                list.add(r);

        for(RenderGroup r : currentEnvironment.getGroups())
            if(r.isEnabled())
                list.add(r);

        return list;
    }

    public static void removeRenderGroup(RenderGroup r){
        groups.remove(r);
    }
    
    public static void addRenderPath(RenderOperation r){
        paths.add(r);
    }
    
    public static RenderOperation getRenderPath(String name){
        for(RenderOperation r : paths)
            if(r.name.equals(name)) return r;
        
        return null;
    }
    
    public static List<RenderOperation> getRenderPaths(){
        return paths;
    }
    
    public static List<RenderOperation> getActiveRenderPaths(){
        ArrayList<RenderOperation> list = new ArrayList<>();
        
        for(RenderOperation r : paths)
            if(r.enabled)
                list.add(r);
        
        return list;
    }
    
    public static void removeRenderPath(RenderOperation r){
        paths.remove(r);
    }
    
    public static void addRenderable(Renderable r){
        defaultList.add(r);
    }

    public static Skybox getSkybox(){
        return currentEnvironment.getSkybox();
    }

    public static void setCulling(boolean enable){
        cull = enable;
        resetConfig();
    }

    public static void setDepthCheck(boolean check){
        GLOptions.set(GL_DEPTH_TEST, check);
    }

    public static RenderEnvironment getCurrentEnvironment(){
        return currentEnvironment;
    }

    public static void setCurrentEnvironment(RenderEnvironment currentEnvironment){
        RenderEngine.currentEnvironment = currentEnvironment;
    }

    public static Framebuffer getCurrentFramebuffer() {
        return currentFramebuffer;
    }

    public static void setCurrentFramebuffer(Framebuffer currentFramebuffer) {
        RenderEngine.currentFramebuffer = currentFramebuffer;
    }

    public static void useView(View c){
        camera = c;
    }
    
    public static View getCurrentView(){
        return camera;
    }

    public static void setProjectionData(ProjectionData data){
        projdata = data;
    }
    
    public static ProjectionData getData(){
        return projdata;
    }

    public static void enableDefaultVP(){
        ShaderController.setOrtho(0, 1, 0, 1, -1, 1);
        ShaderController.setView(new Camera().getMatrix());
    }

    public static boolean validateInitialization() {
        //if(!GGInfo.isServer() && !initialized) throw new RenderException("OpenGL is not initialized!");
        return true;
        //return initialized;
    }
    
    public static void destroy(){
        TextureManager.destroy();
        GGConsole.log("Render engine has released all OpenGL Resource and has finalized");
    }
}
