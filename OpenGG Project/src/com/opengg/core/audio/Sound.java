/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.engine.AudioController;

/**
 *
 * @author Javier
 */
public class Sound{
    ALBuffer buffer;
    NativeSound so;
    boolean isPlaying  = false;
    float gain = 1;
    
    public Sound(){}
    
    public Sound(String path){
        setSound(path);
    }
    
    public Sound(SoundData data){
        setSound(data);
    }
    
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
    
    public void rewind(){
        isPlaying = false;
        so.rewind();
    }
    
    public void pause(){
        isPlaying = false;
        so.pause();
    }
    
    public void setGain(float gain){
        this.gain = gain;
        so.setGain(gain * AudioController.getGlobalGain());
    }
    
    public void setSound(String path){
        setSound(SoundManager.loadSound(path));
    }
    
    public void setSound(SoundData data){
        buffer = new ALBuffer(data);
        setSound(buffer);
    }
    
    public void setSound(ALBuffer buffer){
        so = new NativeSound(buffer);
        AudioController.addAudioSource(so);
    }
    
    public ALBuffer getBuffer(){
        return buffer;
    }
    
    public NativeSound getSoundSource(){
        return so;
    }
    
    public void remove(){
        AudioController.removeAudioSource(so);
    }
}
