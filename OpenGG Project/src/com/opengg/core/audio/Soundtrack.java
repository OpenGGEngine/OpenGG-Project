/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.openal.AL10.AL_PAUSED;
import static org.lwjgl.openal.AL10.AL_STOPPED;

/**
 *
 * @author Javier
 */
public class Soundtrack {
    List<Sound> soundtrack = new ArrayList<>();
    Sound current;
    
    public Soundtrack(){
    }
    
    public void addSong(String path){
        soundtrack.add(new Sound(path));
        if(current == null)
            current = soundtrack.get(0);
    }
    
    public Sound getCurrentSound(){
        return current;
    }
    
    public void next(){
        Sound next;
        try{
            next = soundtrack.get(soundtrack.indexOf(current) + 1);
        }catch(Exception e){
            next = soundtrack.get(0);
        }
        current.rewind();
        next.rewind();
        next.play();
        current = next;
    }
    
    public void stop(){
        current.stop();
    }
    
    public void play(){
        current.rewind();
        current.play();
    }
    
    public void update(){
        if(current.getSoundSource().getState() == AL_STOPPED)
            next();
    }
}
