/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.engine.AudioController;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.alGenBuffers;

/**
 *
 * @author Javier
 */
public class Sound{
    
    NativeSound so;
    boolean isPlaying  = false;
    
    public void setPlayState(boolean b){
        if(b) so.play();
        else so.pause();
    }
    
    public void stop(){
        so.stop();
    }
    
    public void play(){
        isPlaying = true;
        so.play();
    }
    
    public void pause(){
        isPlaying = false;
        so.pause();
    }
    
    public Sound(URL u){
        setSound(u);
    }
    
    public Sound(String u){
        setSound(u);
    }
    
    public void setSound(URL u){
        int buffer = alGenBuffers();
        WaveData wavFile = WaveData.create(u);
        AL10.alBufferData(buffer,wavFile.format,wavFile.data,wavFile.samplerate);
        wavFile.dispose();
        so = new NativeSound(buffer);
        AudioController.addAudioSource(so);
    }
    public void setSound(String u){
        try {
            setSound(new URL(u));
        } catch (MalformedURLException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
