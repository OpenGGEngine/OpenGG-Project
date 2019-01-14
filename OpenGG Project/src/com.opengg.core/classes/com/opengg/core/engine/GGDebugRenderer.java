package com.opengg.core.engine;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;

import java.util.LinkedList;
import java.util.Queue;

public class GGDebugRenderer {
    private static boolean render = true;

    private static double computedFramerate;
    private static Queue<Integer> lastFrames = new LinkedList<>();
    private static long lastFrame = 0;

    private static Font font = Resource.getTruetypeFont("consolas.ttf");;
    private static Text displaytext = Text.from("");
    private static Drawable display;

    private static final float FONT_SCALE = 0.034f;

    public static void update(){
        var frametime = System.currentTimeMillis() - lastFrame;
        lastFrame = System.currentTimeMillis();
        lastFrames.add((int) frametime);

        if (lastFrames.size() > 15){
            computedFramerate = lastFrames.stream().mapToInt(i -> i).average().getAsDouble();
            lastFrames.clear();
        }
    }

    public static void render(){
        if(render){
            RenderEngine.setDepthCheck(false);

            if(!displaytext.getText().contains(String.format("%.2f", computedFramerate))){
                displaytext = Text.from(
                                "Frame time: " + String.format("%.2f", computedFramerate) + "\n" +
                                "Frame rate: " + String.format("%.2f", 1/(computedFramerate/1000)) + "\n\n" +
                                "Camera position: (" + String.format("%.2f", RenderEngine.getCurrentView().getPosition().x) + ", " +  String.format("%.2f", RenderEngine.getCurrentView().getPosition().y) + ", " +  String.format("%.2f", RenderEngine.getCurrentView().getPosition().z) + ")")
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
}
