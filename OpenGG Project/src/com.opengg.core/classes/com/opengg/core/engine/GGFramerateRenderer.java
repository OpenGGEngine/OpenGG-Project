package com.opengg.core.engine;

import java.util.LinkedList;
import java.util.Queue;

public class GGFramerateRenderer {

    private static double computedFramerate;
    private static Queue<Integer> lastFrames = new LinkedList<>();
    private static long lastFrame = 0;

    public static void update(){
        var frametime = System.currentTimeMillis() - lastFrame;
        lastFrame = System.currentTimeMillis();
        lastFrames.add((int) frametime);

        if (lastFrames.size() > 10){
            computedFramerate = lastFrames.stream().mapToInt(i -> i).average().getAsDouble();
            lastFrames.clear();
            System.out.println(1/(computedFramerate/1000));
        }
    }
}
