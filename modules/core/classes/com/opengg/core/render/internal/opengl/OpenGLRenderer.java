/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.GGDebugRenderer;
import com.opengg.core.engine.GGGameConsole;
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
import com.opengg.core.system.Allocator;
import com.opengg.core.world.Camera;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL43.*;

/**
 * 
 * @author Javier
 */
public class OpenGLRenderer implements Renderer {
    private boolean suppressErrors = false;
    private int lightoffset;

    private boolean depthTestEnabled = true;
    private boolean depthWriteEnabled = true;
    private DepthTestFunction currentDepthFunc = DepthTestFunction.LEQUAL;

    private boolean alphaBlendingEnabled = true;
    private AlphaBlendFunction currentAlphaBlendFunction = AlphaBlendFunction.ADD;
    private AlphaBlendSource currentSrcSource = AlphaBlendSource.SRC_ALPHA;
    private AlphaBlendSource currentDestSource = AlphaBlendSource.ONE_MINUS_SRC_ALPHA;
    private WindingOrder windingOrder = WindingOrder.CCW;
    private boolean cullFace = true;


    private Vector3f clearColor = new Vector3f();

    private OpenGLVertexArrayObject currentVAO;
    private GraphicsBuffer lightBuffer;
    private Framebuffer currentFramebuffer;
    private Framebuffer lastBackBuffer;
    private DebugCallback callback;

    /**
     * Initializes the render engine, should rarely if ever be called
     */
    @Override
    public void initialize() {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(callback = new DebugCallback(), 0);

        OpenGLVAOManager.initialize();
        ShaderController.initialize();

        GGConsole.log("Created default vertex array formats");

        currentVAO = OpenGLVAOManager.getVAO(RenderEngine.getDefaultFormat());
        currentVAO.bind();

        enableDefaultGroups();

        lightBuffer = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.UNIFORM_BUFFER, 1600, GraphicsBuffer.UsageType.HOST_MAPPABLE_UPDATABLE);
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
            this.setBackfaceCulling(false);
            if(RenderEngine.getCurrentEnvironment().getSkybox() != null){
                ShaderController.useConfiguration("sky");
                CommonUniforms.setModel(Matrix4f.IDENTITY);
                ShaderController.setUniform("cubemap", RenderEngine.getCurrentEnvironment().getSkybox().getCubemap());
                RenderEngine.getCurrentEnvironment().getSkybox().getDrawable().render();

            }
            this.setBackfaceCulling(true);
        });

        RenderOperation path = new RenderOperation("mainpath", () -> {
            for(var group : RenderEngine.getActiveRenderUnits()){
                group.renderable().render();
            }
        });

        RenderOperation light = new RenderOperation("shadowmap", () -> {
            int used = 0;
            var lights = RenderEngine.getActiveLights();
            var fb = getCurrentFramebuffer();
            for(int i = 0; i < lights.size() && used < 2; i++){
                if(lights.get(i).hasShadow()){
                    lights.get(i).initializeRender();
                    /*for(var group : RenderEngine.getActiveRenderGroups()){
                        group.render();
                    }
                    lights.get(i).finalizeRender(used);
                    used++;*/

                    throw new UnsupportedOperationException("Fix later");
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
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDrawBuffer(GL_BACK);
        glViewport(0, 0, WindowController.getWidth(), WindowController.getHeight());
        for(RenderPass pass : RenderEngine.getRenderPasses()){
            pass.runEnableOp();
            CommonUniforms.setView(RenderEngine.getCurrentView().getMatrix());
            ShaderController.setUniform("camera", RenderEngine.getCurrentView().getPosition());
            ShaderController.useConfiguration("texture");
            RenderEngine.getProjectionData().ratio = WindowController.getWindow().getRatio();
            RenderEngine.getProjectionData().use();
            setCurrentVAOFormat(RenderEngine.getDefaultFormat());
            pass.getSceneBuffer().enableRendering(0,0, pass.getSceneBuffer().getWidth(), pass.getSceneBuffer().getHeight());
            pass.getSceneBuffer().clearFramebuffer(clearColor);

            useLights();
            resetConfig();

            for(RenderOperation path : RenderEngine.getActiveRenderPaths()){
                path.render();
                resetConfig();
            }

            setCurrentVAOFormat(RenderEngine.getDefaultFormat());

            enableDefaultVP();

            pass.getSceneBuffer().disableRendering();
            if(pass.isPostProcessEnabled()){
                var outputBuffer = PostProcessController.process(pass.getSceneBuffer());
                lastBackBuffer = outputBuffer;
                if(pass.shouldBlitToBack())
                    outputBuffer.blitToBack();
            }else if(pass.shouldBlitToBack()) {
                pass.getSceneBuffer().blitToBack();
                lastBackBuffer = pass.getSceneBuffer();
            }

            pass.runDisableOp();
        }
        this.setBackfaceCulling(false);

        GUIController.render();
        GGGameConsole.render();
        GGDebugRenderer.render();

        this.setBackfaceCulling(true);
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

        this.setDepthTest(true);
        this.setDepthWrite(true);
        this.setDepthFunc(DepthTestFunction.LEQUAL);

        this.setAlphaBlendEnable(true);
        this.setAlphaBlendFunction(AlphaBlendFunction.ADD);
        this.setAlphaBlendSource(AlphaBlendSource.SRC_ALPHA, AlphaBlendSource.ONE_MINUS_SRC_ALPHA);

        this.setBackfaceCulling(true);
        this.setCullingFace(CullingFace.BACK);
    }


    public void endFrame(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void setCurrentVAOFormat(VertexArrayFormat format){
        if(this.currentVAO.getFormat().equals(format)) return;
        currentVAO = OpenGLVAOManager.getVAO(format);
        currentVAO.bind();
    }

    public OpenGLVertexArrayObject getCurrentVAO(){
        return currentVAO;
    }

    public void setWireframe(boolean wireframe){
        if(wireframe)
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        else
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
    }

    public void setFrontWindingOrder(WindingOrder order) {
        if (windingOrder != order) {
            windingOrder = order;

            switch (order) {
                case CW -> glFrontFace(GL_CW);
                case CCW -> glFrontFace(GL_CCW);
            }
        }
    }

    public void setCullingFace(CullingFace face) {
        switch (face){
            case BACK -> glCullFace(GL_BACK);
            case FRONT -> glCullFace(GL_FRONT);
            case BOTH -> glCullFace(GL_FRONT_AND_BACK);
        }
    }

    public void setBackfaceCulling(boolean enable) {
        if (cullFace != enable) {
            cullFace = enable;

            if (enable) {
                glEnable(GL_CULL_FACE);
            } else {
                glDisable(GL_CULL_FACE);
            }
        }

    }

    public void setDepthTest(boolean check) {
        if(depthTestEnabled != check){
            depthTestEnabled = check;
            GLOptions.set(GL_DEPTH_TEST, check);
        }
    }

    public void setDepthWrite(boolean write) {
        if(depthWriteEnabled != write){
            depthWriteEnabled = write;
            glDepthMask(write);
        }
    }

    public void setDepthFunc(DepthTestFunction func) {
        if(currentDepthFunc != func){
            currentDepthFunc = func;
            int glFunc = switch (func){
                case ALWAYS -> GL_ALWAYS;
                case LEQUAL -> GL_LEQUAL;
            };
            glDepthFunc(glFunc);
        }
    }

    public void setAlphaBlendEnable(boolean blend) {
        if(alphaBlendingEnabled != blend){
            alphaBlendingEnabled = blend;
            GLOptions.set(GL_BLEND, blend);
        }
    }

    public void setAlphaBlendFunction(AlphaBlendFunction function) {
        if(currentAlphaBlendFunction != function){
            currentAlphaBlendFunction = function;
            switch (function){
                case ADD -> glBlendEquation(GL_FUNC_ADD);
                case SUBTRACT -> glBlendEquation(GL_FUNC_SUBTRACT);
                case REV_SUBTRACT -> glBlendEquation(GL_FUNC_REVERSE_SUBTRACT);
            }
        }
    }

    public void setAlphaBlendSource(AlphaBlendSource srcFactor, AlphaBlendSource dstFactor){
        if(currentSrcSource != srcFactor || currentDestSource != dstFactor){
            currentSrcSource = srcFactor;
            currentDestSource = dstFactor;
            glBlendFunc(getGlBlendSource(srcFactor), getGlBlendSource(dstFactor));
        }
    }

    private int getGlBlendSource(AlphaBlendSource source){
        return switch (source){
            case SRC_ALPHA -> GL_SRC_ALPHA;
            case ONE_MINUS_SRC_ALPHA -> GL_ONE_MINUS_SRC_ALPHA;
            case ONE -> GL_ONE;
            case ZERO -> GL_ZERO;
        };
    }

    public void setClearColor(Vector3f color){
        this.clearColor = color;
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

    public ByteBuffer getLastPassContents(){
        return lastBackBuffer.readData();
    }

    public static OpenGLRenderer getOpenGLRenderer(){
        return (OpenGLRenderer) RenderEngine.getRenderer();
    }

    public enum DepthTestFunction{LEQUAL, ALWAYS}

    public enum AlphaBlendSource{SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO}

    public enum AlphaBlendFunction{ADD, SUBTRACT, REV_SUBTRACT}

    public enum CullingFace{BACK, FRONT, BOTH};

    public enum WindingOrder{CW, CCW};
}
