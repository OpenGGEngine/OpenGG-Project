package com.opengg.core.render.postprocess;

import com.opengg.core.render.texture.WindowFramebuffer;

public class CopyStage implements PostProcessStage{
    private String sourceBuffer;
    private String destBuffer;

    public CopyStage(String sourceBuffer, String destBuffer) {
        this.sourceBuffer = sourceBuffer;
        this.destBuffer = destBuffer;
    }

    @Override
    public void render() {
        var realSrc = ((WindowFramebuffer)PostProcessController.getBuffer(sourceBuffer)).getUnderlyingBuffer();
        var realDest = ((WindowFramebuffer)PostProcessController.getBuffer(destBuffer)).getUnderlyingBuffer();

        realSrc.blitTo(realDest);

    }
}
