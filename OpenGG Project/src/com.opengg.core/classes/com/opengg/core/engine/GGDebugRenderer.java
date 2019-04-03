package com.opengg.core.engine;

import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.world.WorldEngine;

public class GGDebugRenderer implements KeyboardListener {
    private static boolean render = false;

    private static Font font = Resource.getTruetypeFont("consolas.ttf");
    private static Text displaytext = Text.from("");
    private static Drawable display;

    private static final float FONT_SCALE = 0.08f;

    private static int counter = 16;

    public static void initialize(){
        KeyboardController.addKeyboardListener(new GGDebugRenderer());
    }

    public static void render(){
        if(render){
            RenderEngine.setDepthCheck(false);

            counter++;

            if(counter > 15){
                counter = 0;

                var debugString = "Frame time: " + String.format("%.2f", PerformanceManager.getComputedFramerate()) + "\n" +
                        "Frame rate: " + String.format("%.2f", 1/(PerformanceManager.getComputedFramerate())) + "\n\n" +
                        "Camera position: (" + String.format("%.2f", RenderEngine.getCurrentView().getPosition().x) + ", " +  String.format("%.2f",     RenderEngine.getCurrentView().getPosition().y) + ", " +  String.format("%.2f", RenderEngine.getCurrentView().getPosition().z) + ") \n\n" +
                        "Current world: " + WorldEngine.getCurrent().getName() + " \n" +
                        "Component count: " + WorldEngine.getCurrent().getAllDescendants().size() + "\n\n" +
                        "Render group count: " + RenderEngine.getActiveRenderGroups().size() + "  Rendered object count: " + RenderEngine.getActiveRenderGroups().stream().mapToInt(r -> r.getList().size()).sum();

                if(NetworkEngine.isRunning()){
                    debugString += "\n\n" + "Packets received / sec: " + PerformanceManager.getPacketsInSec() + "\n" +
                            "Packets sent / sec: " + PerformanceManager.getPacketsOutSec() + "\n" +
                            "Bytes received / sec: " + PerformanceManager.getNetworkBytesInSec() + "\n" +
                            "Bytes sent / sec: " + PerformanceManager.getNetworkBytesOutSec();
                }

                displaytext = Text.from(debugString)
                        .maxLineSize(1f)
                        .kerning(true)
                        .size(FONT_SCALE);
                display = font.createFromText(displaytext);
                display.setMatrix(Matrix4f.translate(0.02f,0.975f,0));
            }

            display.render();
            RenderEngine.setDepthCheck(true);

        }
    }

    public static void setEnabled(boolean enabled){
        render = enabled;
    }

    @Override
    public void keyPressed(int key) {
        if(key == Key.KEY_F3) render = !render;
    }

    @Override
    public void keyReleased(int key) {

    }
}
