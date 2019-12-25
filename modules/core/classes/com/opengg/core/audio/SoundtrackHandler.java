/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

/**
 * Handler for managing and updating {@link Soundtrack soundtracks}
 * @author Javier
 */
public class SoundtrackHandler {
    /**
     * Current soundtrack
     */
    private static Soundtrack current;
    
    /**
     * Returns the current soundtrack
     * @return Current soundtrack
     */
    public static Soundtrack getCurrent(){
        return current;
    }
    
    /**
     * Sets the soundtrack to the given object
     * @param track New track
     */
    public static void setSoundtrack(Soundtrack track){
        if(current != null)
            current.stop();
        current = track;
        current.play();
    }

    public static void removeSoundtrack(){
        current.stop();
        current = null;
    }

    /**
     * Updates the current soundtrack, does not have to be called
     */
    public static void update(){
        if(current != null)
            current.update();
    }
    
    /**
     * Refreshes and restarts the current soundrack
     */
    public static void refresh(){
        current.stop();
        current.shuffle();
        current.play();
    }

    private SoundtrackHandler() {
    }
}
