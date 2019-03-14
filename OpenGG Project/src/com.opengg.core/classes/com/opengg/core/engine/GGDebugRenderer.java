package com.opengg.core.engine;

import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;

import java.util.LinkedList;
import java.util.Queue;

public class GGDebugRenderer implements KeyboardListener {
    private static boolean render = false;
    private static boolean update = true;


    private static double computedFramerate = 0.1f;
    private static Queue<Float> lastFrames = new LinkedList<>();

    private static Font font = Resource.getTruetypeFont("consolas.ttf");
    private static Text displaytext = Text.from("");
    private static Drawable display;

    private static final float FONT_SCALE = 0.1f;

    public static void initialize(){
        KeyboardController.addKeyboardListener(new GGDebugRenderer());
    }

    public static void update(float delta){
        lastFrames.add(delta);
        if (lastFrames.size() > 15){
            computedFramerate = lastFrames.stream().mapToDouble(i -> i).average().getAsDouble();
            lastFrames.clear();
            update = true;
        }
    }

    public static void render(){
        if(render){
            RenderEngine.setDepthCheck(false);

            if(update){
                update = false;
                displaytext = Text.from(
                                "Frame time: " + String.format("%.2f", computedFramerate) + "\n" +
                                "Frame rate: " + String.format("%.2f", 1/(computedFramerate)) + "\n\n" +
                                "Camera position: (" + String.format("%.2f", RenderEngine.getCurrentView().getPosition().x) + ", " +  String.format("%.2f",     RenderEngine.getCurrentView().getPosition().y) + ", " +  String.format("%.2f", RenderEngine.getCurrentView().getPosition().z) + ")")
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

    public static float getLastFrameTime(){
        return (float) computedFramerate;
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
