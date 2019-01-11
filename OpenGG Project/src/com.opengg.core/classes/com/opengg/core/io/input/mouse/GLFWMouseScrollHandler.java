package com.opengg.core.io.input.mouse;

import org.lwjgl.glfw.GLFWScrollCallback;

public class GLFWMouseScrollHandler extends GLFWScrollCallback implements MouseScrollHandler{
    double wheelX,wheelY;
    @Override
    public double getWheelX() {
        return wheelX;
    }

    @Override
    public double getWheelY() {
        return wheelY;
    }

    @Override
    public void invoke(long window, double xoffset, double yoffset) {
        wheelX = xoffset;
        wheelY = yoffset;
    }
}
