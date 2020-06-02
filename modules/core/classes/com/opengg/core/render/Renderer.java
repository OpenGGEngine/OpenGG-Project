package com.opengg.core.render;

public interface Renderer {
    void initialize();
    void render();
    void startFrame();
    void endFrame();
    void destroy();
}
