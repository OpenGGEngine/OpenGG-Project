/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Animator {
    static List<Animation> animations = new ArrayList<>();
    public static void initialize(){}
    
    public static void update(float delta){
        for(Animation animation : animations){
            animation.updateAnimation(delta);
        }
    }
    
    public static void registerAnimation(Animation animation){
        animations.add(animation);
    }
    
    public static void removeAnimation(Animation animation){
        animations.remove(animation);
    }
}
