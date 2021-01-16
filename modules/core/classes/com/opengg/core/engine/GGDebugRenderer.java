package com.opengg.core.engine;

import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.world.WorldEngine;

import java.text.NumberFormat;

public class GGDebugRenderer implements KeyboardListener {
    private static boolean render = false;

    private static Font font;
    private static Text displaytext = Text.from("");
    private static Renderable display;

    private static final float FONT_SCALE = 0.08f;

    private static int counter = 16;

    public static void initialize() {
        KeyboardController.addKeyboardListener(new GGDebugRenderer());
        font = Resource.getTruetypeFont("consolas.ttf");
    }

    public static void render() {
        counter++;

        if (counter > 15) {
            counter = 0;

            var debugString =
                    "Framebuffer resolution: " + WindowController.getWidth() + "/" + WindowController.getHeight() + "\n" +
                            "Frame time: " + String.format("%.2f", PerformanceManager.getComputedFrameTime() * 1000) + "\n" +
                            "Frame rate: " + String.format("%.2f", 1 / (PerformanceManager.getComputedFrameTime())) + "\n\n" +
                            "Camera position: (" + String.format("%.2f", RenderEngine.getCurrentView().getPosition().x) + ", " + String.format("%.2f", RenderEngine.getCurrentView().getPosition().y) + ", " + String.format("%.2f", RenderEngine.getCurrentView().getPosition().z) + ") \n\n" +
                            "Current world: " + WorldEngine.getCurrent().getName() + " \n" +
                            "Component count: " + WorldEngine.getComponentCount() + "\n\n" +
                            "Render unit count: " + RenderEngine.getActiveRenderUnits().size() + "\n\n" +
                            "Average render calls: " + PerformanceManager.getComputedDrawCalls() + "\n" +
                            "GPU Buffer allocations: " + PerformanceManager.getBufferAllocsThisFrame() + "\n" +
                            "GPU Buffer allocations size: " + PerformanceManager.getBufferAllocSizeThisFrame();

            if (RenderEngine.getRendererType() == WindowOptions.RendererType.VULKAN) {
                debugString += "\nDescriptor set allocations: " + PerformanceManager.getDescriptorSetAllocations();
            }

            if (NetworkEngine.isRunning()) {
                debugString += "\n\n" + "Packets received / sec: " + PerformanceManager.getPacketsInSec() + "\n" +
                        "Packets sent / sec: " + PerformanceManager.getPacketsOutSec() + "\n" +
                        "Guaranteed packets sent / sec: " + PerformanceManager.getAckPacketsOutSec() + "\n" +
                        "Packets dropped / sec: " + PerformanceManager.getPacketsDroppedSec() + "\n" +
                        "Packets dropped %: " + NumberFormat.getPercentInstance().format((double) PerformanceManager.getPacketsDroppedSec() / PerformanceManager.getAckPacketsOutSec()) + "\n" +
                        "Bytes received / sec: " + PerformanceManager.getNetworkBytesInSec() + "\n" +
                        "Bytes sent / sec: " + PerformanceManager.getNetworkBytesOutSec();
            }

            displaytext = Text.from(debugString)
                    .maxLineSize(1f)
                    .kerning(true)
                    .size(FONT_SCALE);
        }
        if (render) {
            display = font.createFromText(displaytext);

            CommonUniforms.setModel(Matrix4f.translate(0.02f, 0.975f, 0));
            display.render();
        }
    }

    public static void setEnabled(boolean enabled) {
        render = enabled;
    }

    public static boolean isEnabled() {
        return render;
    }

    public static String getCurrentDebugString() {
        return displaytext.getText();
    }

    @Override
    public void keyPressed(int key) {
        if (key == Key.KEY_F3) render = !render;
    }

    @Override
    public void keyReleased(int key) {

    }
}
