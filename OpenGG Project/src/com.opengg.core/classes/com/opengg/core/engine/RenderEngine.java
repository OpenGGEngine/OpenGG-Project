/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.console.GGConsole;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIController;
import com.opengg.core.model.ModelManager;
import com.opengg.core.physics.PhysicsRenderer;
import com.opengg.core.render.GLBuffer;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.postprocess.PostProcessController;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayAttribute;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.render.texture.WindowFramebuffer;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.Camera;
import com.opengg.core.world.Skybox;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private static List<RenderGroup> groups = new ArrayList<>();
    private static List<Light> lights = new ArrayList<>();
    private static List<RenderPath> paths = new ArrayList<>();
    private static GLBuffer lightobj;
    private static RenderGroup dlist, animlist;
    private static Skybox skybox;
    private static boolean initialized;
    private static Framebuffer sceneTex;
    private static VertexArrayFormat vaoformat;
    private static VertexArrayFormat particle;
    private static VertexArrayFormat animation;
    private static boolean cull = true;
    private static int lightoffset;
    private static Camera camera;
    private static VertexArrayObject currentvao;
    private static VertexArrayObject defaultvao;
    private static ProjectionData projdata;

    /**
     * Initializes the render engine, should rarely if ever be called
     */
    static void initialize() {
        ShaderController.initialize();
        TextureManager.initialize();
        ModelManager.initialize();
        GUIController.initialize();
        PostProcessController.initialize();
        PhysicsRenderer.initialize();
        
        vaoformat = new VertexArrayFormat();
        vaoformat.addAttribute(new VertexArrayAttribute("position", 3, 12, GL_FLOAT, 0, 0, false));
        //vaoformat.addAttribute(new VertexArrayAttribute("color", 4, 12, GL_FLOAT, 3, 0, false));
        vaoformat.addAttribute(new VertexArrayAttribute("normal", 3, 12, GL_FLOAT, 7, 0, false));
        vaoformat.addAttribute(new VertexArrayAttribute("texcoord", 2, 12, GL_FLOAT, 10, 0, false));

        particle = new VertexArrayFormat();
        particle.addAttribute(new VertexArrayAttribute("position", 3, 12, GL_FLOAT, 0, 0, false));
        particle.addAttribute(new VertexArrayAttribute("offset", 3, 3, GL_FLOAT, 0, 1, true));
        particle.addAttribute(new VertexArrayAttribute("normal", 3, 12, GL_FLOAT, 7, 0, false));
        particle.addAttribute(new VertexArrayAttribute("texcoord", 2, 12, GL_FLOAT, 10, 0, false));

        animation = new VertexArrayFormat();
        animation.addAttribute(new VertexArrayAttribute("position", 3, 20, GL_FLOAT, 0, 0, false));
        //animation.addAttribute(new VertexArrayAttribute("color", 4, 20, GL_FLOAT, 3, 0, false));
        animation.addAttribute(new VertexArrayAttribute("normal", 3, 20, GL_FLOAT, 7, 0, false));
        animation.addAttribute(new VertexArrayAttribute("texcoord", 2, 20, GL_FLOAT, 10, 0, false));
        animation.addAttribute(new VertexArrayAttribute("jointindex", 4, 20, GL_FLOAT, 12, 0, false));
        animation.addAttribute(new VertexArrayAttribute("weights", 4, 20, GL_FLOAT, 16, 0, false));

        sceneTex = WindowFramebuffer.getFloatingPointWindowFramebuffer(1);
        defaultvao = new VertexArrayObject(vaoformat);
        lightobj = new GLBuffer(GL_UNIFORM_BUFFER, 1600, GL_DYNAMIC_DRAW);
        lightobj.bindBase(ShaderController.getUniqueUniformBufferLocation());
        ShaderController.setUniformBlockLocation(lightobj, "LightBuffer");

        enableDefaultGroups();

        setProjectionData(ProjectionData.getPerspective(100, 0.2f, 3000f));
        
        lightoffset = (Allocator.allocFloat(Light.bfsize).capacity());// << 2;

        groups.add(dlist);
       // groups.add(animlist);

        Camera c = new Camera();
        useCamera(c);

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        
        GGConsole.log("Render engine initialized");
    }

    private static void enableDefaultGroups(){
        dlist = new RenderGroup("defaultgroup");
        dlist.setPipeline("object");
        
        RenderPath path = new RenderPath("mainpath", () -> {
            for(RenderGroup d : getActiveRenderGroups()){
                ShaderController.useConfiguration(d.pipeline);
                d.render(); 
            }
        });
        
        RenderPath light = new RenderPath("shadowmap", () -> {
            ShaderController.useConfiguration("passthrough");
            int used = 0;
            for(int i = 0; i < lights.size() && used < 2; i++){
                if(lights.get(i).hasShadow()){    
                    ShaderController.setView(lights.get(i).getView());
                    ShaderController.setProjection(lights.get(i).getPerspective());

                    lights.get(i).getLightbuffer().enableRendering();
                    lights.get(i).getLightbuffer().useEnabledAttachments();
                    
                    for(RenderGroup d : getActiveRenderGroups()){
                        d.render();
                    }
                    
                    lights.get(i).getLightbuffer().disableRendering();
                    lights.get(i).getLightbuffer().useTexture(Framebuffer.DEPTH, 0);
                    //lights.get(i).getLightbuffer().blitToWithDepth(sceneTex);
                    used++;
                }
            }
            
            sceneTex.restartRendering();
            sceneTex.useEnabledAttachments();
            useLights();
            ShaderController.setView(camera.getMatrix());
            projdata.use();
            
            for(RenderGroup d : getActiveRenderGroups()){
                if(d.pipeline.equals("object"))
                    ShaderController.useConfiguration("shadobject");
                else
                    ShaderController.useConfiguration(d.pipeline);
                d.render(); 
            }
            
        });
        paths.add(path);
    }
    
    static void useLights(){
        for(int i = 0; i < lights.size(); i++){
            lightobj.uploadSubData(lights.get(i).getBuffer(), i * lightoffset);
        }
        ShaderController.setUniform("numLights", lights.size());
    }
    
    public static void sortOrders(){
        groups = groups.stream().sorted((RenderGroup o1, RenderGroup o2) -> {
            if(o1.getOrder() > o2.getOrder()){
                return 1;
            }else if(o1.getOrder() < o2.getOrder()){
                return -1;
            }else{
                return 0;
            }
        }).collect(Collectors.toList());
    }
    
    public static void render(){
        ShaderController.setView(camera.getMatrix());
        ShaderController.setUniform("camera", camera.getPos().inverse());
        projdata.ratio = WindowController.getWindow().getRatio();
        projdata.use();

        sceneTex.enableRendering();
        sceneTex.useEnabledAttachments();
        useLights();
        resetConfig();
        defaultvao.bind();
        if(skybox != null){
            ShaderController.useConfiguration("sky");
            skybox.getCubemap().use(2);
            skybox.getDrawable().render();
        }
        
        for(RenderPath path : getActiveRenderPaths()){
            path.render();
            resetConfig();
        }       
        sceneTex.disableRendering();
        
        defaultvao.bind();
        
        GUI.startGUIPos();
        PostProcessController.process(sceneTex);
        GUIController.render();
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
        return vaoformat;
    }
    
    public static VertexArrayFormat getParticleFormat(){
        return particle;
    }
    public static VertexArrayFormat getAnimationFormat(){
        return animation;
    }
    
    public static Framebuffer getSceneFramebuffer(){
        return sceneTex;
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
            if(r.name.equals(name)) return r;
        
        return null;
    }
    
    public static List<RenderGroup> getRenderGroups(){
        return groups;
    }
    
    public static List<RenderGroup> getActiveRenderGroups(){
        ArrayList<RenderGroup> list = new ArrayList<>(groups.size());
        
        for(RenderGroup r : groups)
            if(r.enabled)
                list.add(r);
        
        return list;
    }
    
    public static void removeRenderGroup(RenderGroup r){
        groups.remove(r);
    }
    
    public static void addRenderPath(RenderPath r){
        paths.add(r);
    }
    
    public static RenderPath getRenderPath(String name){
        for(RenderPath r : paths)
            if(r.name.equals(name)) return r;
        
        return null;
    }
    
    public static List<RenderPath> getRenderPaths(){
        return paths;
    }
    
    public static List<RenderPath> getActiveRenderPaths(){
        ArrayList<RenderPath> list = new ArrayList<>();
        
        for(RenderPath r : paths)
            if(r.enabled)
                list.add(r);
        
        return list;
    }
    
    public static void removeRenderPath(RenderPath r){
        paths.remove(r);
    }
    
    public static void addRenderable(Renderable r){
        dlist.add(r);
    }
    
    public static void addAnim(Renderable r){
        animlist.add(r);
    }
    
    public static Skybox getSkybox(){
        return skybox;
    }
    
    public static void setSkybox(Skybox box){
        skybox = box;
    }    

    public static void setCulling(boolean enable){
        cull = enable;
        resetConfig();
    }
    
    public static void useCamera(Camera c){
        camera = c;
    }
    
    public static Camera getCurrentCamera(){
        return camera;
    }

    public static void setProjectionData(ProjectionData data){
        projdata = data;
    }
    
    public static ProjectionData getData(){
        return projdata;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    static void destroy(){
        TextureManager.destroy();
        GGConsole.log("Render engine has released all OpenGL resources and has finalized");
    }
}