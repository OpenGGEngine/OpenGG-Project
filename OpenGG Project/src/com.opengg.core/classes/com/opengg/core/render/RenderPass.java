package com.opengg.core.render;

import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.WindowFramebuffer;

import java.util.function.Consumer;

public class RenderPass {
    private Framebuffer sceneBuffer;
    private boolean shouldBlitToBack;
    private boolean isPostProcessEnabled;

    private Runnable enableOp;

    public RenderPass(boolean shouldBlitToBack, boolean isPostProcessEnabled, Runnable enableOp) {
        this.sceneBuffer = WindowFramebuffer.getFloatingPointWindowFramebuffer(2);
        this.shouldBlitToBack = shouldBlitToBack;
        this.isPostProcessEnabled = isPostProcessEnabled;
        this.enableOp = enableOp;
    }

    public RenderPass(Framebuffer sceneBuffer, boolean blitToMain, boolean isPostProcessEnabled, Runnable enableOp) {
        this.sceneBuffer = sceneBuffer;
        this.shouldBlitToBack = blitToMain;
        this.isPostProcessEnabled = isPostProcessEnabled;
        this.enableOp = enableOp;
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
    
    void runOp(){
        enableOp.run();
    }
}
