/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.gui.GUI;
import com.opengg.core.model.ModelManager;
import com.opengg.core.render.GLBuffer;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.postprocess.PostProcessPipeline;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayAttribute;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.Camera;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DECR_WRAP;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.GL_INCR_WRAP;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.glStencilOpSeparate;
import static org.lwjgl.opengl.GL30.GL_MAJOR_VERSION;
import static org.lwjgl.opengl.GL30.GL_MINOR_VERSION;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import org.lwjgl.system.MemoryUtil;

/**
 * 
 * @author Javier
 */
public class RenderEngine {
    static List<RenderGroup> groups = new ArrayList<>();
    static List<Light> lights = new ArrayList<>();
    static List<RenderPath> paths = new ArrayList<>();
    static GLBuffer lightobj;
    static RenderGroup dlist;
    static Drawable skybox;
    static Cubemap skytex;
    static boolean initialized;
    static Framebuffer sceneTex;
    static VertexArrayFormat vaoformat;
    static VertexArrayFormat particle;
    static boolean cull = true;
    static int lightoffset;
    static Camera camera;
    static VertexArrayObject currentvao;
    static VertexArrayObject defaultvao;
    
    static boolean init(){
        ShaderController.initialize();
        
        vaoformat = new VertexArrayFormat();
        vaoformat.addAttribute(new VertexArrayAttribute("position", 3, 0, 0, false));
        vaoformat.addAttribute(new VertexArrayAttribute("color", 4, 3, 0, false));
        vaoformat.addAttribute(new VertexArrayAttribute("normal", 3, 7, 0, false));
        vaoformat.addAttribute(new VertexArrayAttribute("texcoord", 2, 10, 0, false));
        
        particle = new VertexArrayFormat();
        particle.addAttribute(new VertexArrayAttribute("position", 3, 0, 0, false));
        particle.addAttribute(new VertexArrayAttribute("color", 3, 0, 1, true));
        particle.addAttribute(new VertexArrayAttribute("normal", 3, 7, 0, false));
        particle.addAttribute(new VertexArrayAttribute("texcoord", 2, 10, 0, false));
        
        TextureManager.initialize();
        ModelManager.initialize();
        sceneTex = Framebuffer.getFramebuffer(OpenGG.window.getWidth(), OpenGG.window.getHeight(), 4, GL_RGBA16F);
        PostProcessPipeline.initialize(sceneTex);
        
        defaultvao = new VertexArrayObject(vaoformat);
        lightobj = new GLBuffer(GL_UNIFORM_BUFFER, 1600, GL_DYNAMIC_DRAW);
        lightobj.bindBase(ShaderController.getUniqueUniformBufferLocation());
        ShaderController.setUniformBlockLocation(lightobj, "LightBuffer");
        
        enableDefaultGroups();
        
        lightoffset = (MemoryUtil.memAllocFloat(Light.bfsize).capacity());// << 2;

        groups.add(dlist);
        
        Camera c = new Camera();
        useCamera(c);
        
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        return true;
    }
    
    public static void enableDefaultGroups(){
        dlist = new RenderGroup("default");
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
                    //ShaderController.setView(lights.get(i).getView());
                    
                    lights.get(i).getLightbuffer().startTexRender();
                    lights.get(i).getLightbuffer().enableColorAttachments();
                    for(RenderGroup d : getActiveRenderGroups()){
                        d.render();
                    }
                    lights.get(i).getLightbuffer().endTexRender();
                    lights.get(i).getLightbuffer().useDepthTexture(6 + used);
                    used++;
                }
            }
            sceneTex.startTexRender();
            
            for(RenderGroup d : getActiveRenderGroups()){
                ShaderController.setView(camera.getMatrix());
                if(d.pipeline.equals("object"))
                    ShaderController.useConfiguration("shadobject");
                else
                    ShaderController.useConfiguration(d.pipeline);
                d.render(); 
            }
            
        });
        paths.add(path);
    }
    
    public static void checkForGLErrors(){
        int i = 0;
        while((i = glGetError()) != GL_NO_ERROR){
            GGConsole.warning("OpenGL Error code : " + i);
        }
    }
    
    public static String getGLVersion(){
        return glGetInteger(GL_MAJOR_VERSION) + "." + glGetInteger(GL_MINOR_VERSION);
    }
    
    public static VertexArrayFormat getDefaultFormat(){
        return vaoformat;
    }
    
    public static VertexArrayFormat getParticleFormat(){
        return particle;
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
    
    public static void addRenderGroup(RenderGroup r){
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
        ArrayList<RenderGroup> list = new ArrayList<>();
        
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
    
    public static void setSkybox(Drawable sky, Cubemap c){
        skybox = sky;
        skytex = c;
    }    

    public static void setCulling(boolean enable){
        cull = enable;
    }
    
    public static void useCamera(Camera c){
        camera = c;
    }
    
    public static Camera getCurrentCamera(){
        return camera;
    }
    
    static void useLights(){
        for(int i = 0; i < lights.size(); i++){
            lightobj.uploadSubData(lights.get(i).getBuffer(), i * lightoffset);
        }
        ShaderController.setUniform("numLights", lights.size());
    }
     
    private static void writeToDepth(){
        glDepthMask(true);
        glDrawBuffer(GL_NONE);
        ShaderController.useConfiguration("adjpassthrough");
        groups.stream().forEach((group) -> {
            group.render();
        });
    }
    
    private static void cullShadowFaces(){
        glEnable(GL_STENCIL_TEST);

        glDepthMask(false);
        glEnable(GL_DEPTH_CLAMP); 
        glDisable(GL_CULL_FACE);
        
        glStencilFunc(GL_ALWAYS, 0, 0xff);
        glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR_WRAP, GL_KEEP);
        glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR_WRAP, GL_KEEP); 
        
        ShaderController.useConfiguration("volume");
        groups.stream().forEach((group) -> {
            group.render();
        });

        glDisable(GL_DEPTH_CLAMP);
        glEnable(GL_CULL_FACE); 
        
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
    
    public static void draw(){
        ShaderController.setView(camera.getMatrix());
        ShaderController.setUniform("camera", camera.getPos().inverse());
        sceneTex.startTexRender();
        sceneTex.enableColorAttachments();
        useLights();
        resetConfig();
        
        for(RenderPath path : getActiveRenderPaths()){
            path.render();
            resetConfig();
        }

        glDisable(GL_CULL_FACE); 
        ShaderController.useConfiguration("sky");
        skytex.use(0);
        skybox.render();    
        GUI.startGUIPos();
        PostProcessPipeline.process();
        GUI.render();        
    }
    
    public static void resetConfig(){
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glDisable(GL_CULL_FACE);
    }
    
    static void destroy(){
        TextureManager.destroy();
    }
}
