/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

/**
 *
 * @author Javier
 */
public class SoundtrackHandler {
    private static Soundtrack current;
    
    public static Soundtrack getCurrent(){
        return current;
    }
    
    public static void setSoundtrack(Soundtrack track){
        if(current != null)
            current.stop();
        current = track;
        current.play();
    }
    
    public static void update(){
        if(current != null)
            current.update();
    }
    
    public static void refresh(){
        current.stop();
        current.shuffle();
        current.play();
    }
}
