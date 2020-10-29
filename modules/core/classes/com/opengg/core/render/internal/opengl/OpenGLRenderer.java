/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.GGDebugRenderer;
import com.opengg.core.engine.GGGameConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.gui.GUIController;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.*;
import com.opengg.core.render.internal.opengl.shader.OpenGLVertexArrayObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.postprocess.PostProcessController;
import com.opengg.core.render.shader.*;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.Camera;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

/**
 * 
 * @author Javier
 */
public class OpenGLRenderer implements Renderer {
    private boolean cull = true;
    private boolean suppressErrors = true;
    private int lightoffset;

    private VertexArrayObject currentvao;
    private VertexArrayObject defaultvao;
    private GraphicsBuffer lightBuffer;
    private Framebuffer currentFramebuffer;

    /**
     * Initializes the render engine, should rarely if ever be called
     */
    @Override
    public void initialize() {
        ShaderController.initialize();

        this.checkForGLErrors();

        GGConsole.log("Created default vertex array formats");

        defaultvao = VertexArrayObject.create(RenderEngine.getDefaultFormat());

        enableDefaultGroups();

        lightBuffer = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.UNIFORM_BUFFER, 1600, GraphicsBuffer.UsageType.STREAM_DRAW);
        lightBuffer.bindBase(ShaderController.getUniqueUniformBufferLocation());
        lightoffset = Allocator.allocFloat(Light.BUFFERSIZE).capacity();

        ShaderController.setUniformBlockLocation(lightBuffer, "LightBuffer");

        GLOptions.set(GL_BLEND, true);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_PROGRAM_POINT_SIZE);
        glDepthFunc(GL_LEQUAL);
        GLOptions.set(GL_TEXTURE_CUBE_MAP_SEAMLESS, true);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        GGConsole.log("Enabling rendering submanagers");

        PostProcessController.initialize();
        RenderEngine.addRenderPass(new RenderPass(true, true, () -> {}, f -> {}));

        GGConsole.log("Render engine initialized");
    }

    private void enableDefaultGroups(){

        RenderOperation skybox = new RenderOperation("skyboxpath", () -> {
            this.setCulling(false);
            if(RenderEngine.getCurrentEnvironment().getSkybox() != null){
                ShaderController.useConfiguration("sky");
                CommonUniforms.setModel(new Matrix4f());
                ShaderController.setUniform("cubemap", RenderEngine.getCurrentEnvironment().getSkybox().getCubemap());
                RenderEngine.getCurrentEnvironment().getSkybox().getDrawable().render();

            }
            this.setCulling(true);
        });

        RenderOperation path = new RenderOperation("mainpath", () -> {
            for(var group : RenderEngine.getActiveRenderGroups()){
                ShaderController.useConfiguration(group.getPipeline());
                ((OpenGLVertexArrayObject)group.getVertexArrayObject()).bind();
                group.render();
                ((OpenGLVertexArrayObject)group.getVertexArrayObject()).unbind();

            }
        });

        RenderOperation light = new RenderOperation("shadowmap", () -> {
            int used = 0;
            var lights = RenderEngine.getActiveLights();
            var fb = getCurrentFramebuffer();
            for(int i = 0; i < lights.size() && used < 2; i++){
                if(lights.get(i).hasShadow()){
                    lights.get(i).initializeRender();
                    for(var group : RenderEngine.getActiveRenderGroups()){
                        group.render();
                    }
                    lights.get(i).finalizeRender(used);
                    used++;
                }
            }
            fb.enableRendering(0,0,fb.getWidth(),fb.getHeight());
            useLights();
            CommonUniforms.setView(RenderEngine.getCurrentView().getMatrix());
            RenderEngine.getProjectionData().use();
        });

        RenderEngine.addRenderPath(skybox);
        //RenderEngine.addRenderPath(light);
        RenderEngine.addRenderPath(path);
    }

    @Override
    public void render(){
        for(RenderPass pass : RenderEngine.getRenderPasses()){
            pass.runEnableOp();
            CommonUniforms.setView(RenderEngine.getCurrentView().getMatrix());
            ShaderController.setUniform("camera", RenderEngine.getCurrentView().getPosition());
            ShaderController.useConfiguration("texture");
            RenderEngine.getProjectionData().ratio = WindowController.getWindow().getRatio();
            RenderEngine.getProjectionData().use();

            pass.getSceneBuffer().clearFramebuffer();
            pass.getSceneBuffer().enableRendering(0,0,pass.getSceneBuffer().getWidth(),pass.getSceneBuffer().getHeight());
            useLights();
            resetConfig();
            ((OpenGLVertexArrayObject)defaultvao).bind();

            for(RenderOperation path : RenderEngine.getActiveRenderPaths()){
                path.render();
                resetConfig();
            }
            ((OpenGLVertexArrayObject)defaultvao).bind();

            pass.getSceneBuffer().disableRendering();
            enableDefaultVP();

            if(pass.isPostProcessEnabled())
                PostProcessController.process(pass.getSceneBuffer());

            if(pass.shouldBlitToBack())
                pass.getSceneBuffer().blitToBack();

            pass.runDisableOp();
        }
        this.setCulling(false);

        GUIController.render();
        GGGameConsole.render();
        GGDebugRenderer.render();

        this.setCulling(true);

        this.checkForGLErrors();
    }

    @Override
    public void startFrame() {
        
    }

    void useLights(){
        var allLights = RenderEngine.getActiveLights();
        for(int i = 0; i < allLights.size(); i++){
            lightBuffer.uploadSubData(allLights.get(i).getBuffer(), i * lightoffset);
        }
        ShaderController.setUniform("numLights", allLights.size());
    }

    public void resetConfig(){
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        cull = false;
        if(cull)
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);

    }

    /**
     * Checks for and prints out any OpenGL errors
     */
    public void checkForGLErrors(){
        int i;
        if(suppressErrors) return;
        while((i = glGetError()) != GL_NO_ERROR){
            GGConsole.warning("OpenGL Error code : " + i);
        }
    }

    /**
     * @return The current OpenGL version in a major . minor format
     */
    public String getGLVersion(){
        return OpenGG.getInitOptions().getWindowOptions().renderer == WindowOptions.RendererType.OPENGL ?
                glGetInteger(GL_MAJOR_VERSION) + "." + glGetInteger(GL_MINOR_VERSION):
                "unknown";
    }

    public VertexArrayObject getCurrentVAO(){
        return currentvao;
    }

    public void setVAO(VertexArrayObject vao){
        currentvao = vao;
        if(vao == null)
            currentvao = defaultvao;
    }

    public VertexArrayObject getDefaultVAO(){
        return defaultvao;
    }

    public void setWireframe(boolean wf){
        if(wf)
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        else
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
    }

    public void setClearColor(Vector3f color){
        glClearColor(color.x, color.y, color.z, 1);
    }

    public void endFrame(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void setCulling(boolean enable){
        cull = enable;
        resetConfig();
    }

    public void setDepthCheck(boolean check){
        GLOptions.set(GL_DEPTH_TEST, check);
    }

    public Framebuffer getCurrentFramebuffer() {
        return currentFramebuffer;
    }

    public void setCurrentFramebuffer(Framebuffer currentFramebuffer) {
        this.currentFramebuffer = currentFramebuffer;
    }

    public void enableDefaultVP(){
        CommonUniforms.setOrtho(0, 1, 0, 1, -1, 1);
        CommonUniforms.setView(new Camera().getMatrix());
    }

    public void destroy(){
        TextureManager.clearCache();
        GGConsole.log("Render engine has released all OpenGL Resource and has finalized");
    }
}
