package com.opengg.core.render;

import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.WindowFramebuffer;

import java.util.function.Consumer;

public class RenderPass {
    private final Framebuffer sceneBuffer;
    private boolean shouldBlitToBack;
    private boolean isPostProcessEnabled;

    private final Runnable enableOp;
    private final Consumer<Framebuffer> disableOp;

    public RenderPass(boolean shouldBlitToBack, boolean isPostProcessEnabled, Runnable enableOp, Consumer<Framebuffer> disableOp) {
        this.sceneBuffer = WindowFramebuffer.getWindowFramebuffer(2);
        this.shouldBlitToBack = shouldBlitToBack;
        this.isPostProcessEnabled = isPostProcessEnabled;
        this.enableOp = enableOp;
        this.disableOp = disableOp;
    }

    public RenderPass(Framebuffer sceneBuffer, boolean blitToMain, boolean isPostProcessEnabled, Runnable enableOp, Consumer<Framebuffer> disableOp) {
        this.sceneBuffer = sceneBuffer;
        this.shouldBlitToBack = blitToMain;
        this.isPostProcessEnabled = isPostProcessEnabled;
        this.enableOp = enableOp;
        this.disableOp = disableOp;
    }

    public Framebuffer getSceneBuffer() {
        return sceneBuffer;
    }

    public boolean shouldBlitToBack() {
        return shouldBlitToBack;
    }

    public void setShouldBlitToBack(boolean shouldBlitToBack) {
        this.shouldBlitToBack = shouldBlitToBack;
    }

    public boolean isPostProcessEnabled() {
        return isPostProcessEnabled;
    }

    public void setPostProcessEnabled(boolean postProcessEnabled) {
        isPostProcessEnabled = postProcessEnabled;
    }
    
    public void runEnableOp(){
        enableOp.run();
    }

    public void runDisableOp(){
        disableOp.accept(sceneBuffer);
    }

}
