package com.opengg.core.animation;

import java.util.ArrayList;

public class AnimationManager {
   private static final ArrayList<Animation> animations = new ArrayList<>();
   private static final ArrayList<Animation> newAnims = new ArrayList<>();

    public static void update(float delta){
        animations.addAll(newAnims);
        newAnims.clear();
        for(Animation a : animations){
           if(a.isRunning()){
               a.step(delta);
               a.updateStates();
           }
        }
    }
    public static void register(Animation a){
        newAnims.add(a);
    }
}
