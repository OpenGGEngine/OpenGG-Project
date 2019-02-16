package com.opengg.core.animation;

import java.util.ArrayList;

public class AnimationManager {
   private static ArrayList<Animation> animations = new ArrayList<>();

    public static void update(float delta){
       for(Animation a:animations){
           if(a.isRunning()){
               a.step(delta);
               a.updateStates();
           }
       }
    }
    public static void register(Animation a){
        animations.add(a);
    }
}
