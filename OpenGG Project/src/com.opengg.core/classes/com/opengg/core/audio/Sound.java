/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

/**
 * High level abstraction of a sound<br>
 * 
 * @author Javier
 */
public class Sound{
    SoundData data;
    /**
     * Sound buffer used in this sound
     */
    ALBuffer buffer;
    
    /**
     * Underlying native sound object
     */
    NativeSound so;
    
    /**
     * 
     */
    boolean isPlaying  = false;
    
    /**
     * Current gain/volume (used as offset to global sound)
     */
    float gain = 1;
    
    /**
     * Creates an empty Sound object
     */
    public Sound(){
        so = new NativeSound(null);
    }
    
    /**
     * Loads an audio file from the given path and creates a new Sound object based on it
     * @param path Path to sound
     */
    public Sound(String path){
        this(SoundManager.loadSound(path));
    }
    
    /**
     * Creates a Sound based off of the given SoundData
     * @param data SoundData to use
     */
    public Sound(SoundData data){
        so = new NativeSound();
        setSound(data);
        setGain(1);
        SoundEngine.addAudioSource(this);
    }
    
    /**
     * Sets if the sound should play
     * @param b If sound should play
     */
    public void setPlayState(boolean b){
        if(b) so.play();
        else so.pause();
    }
    
    /**
     * Stops the sound
     */
    public void stop(){
        isPlaying = false;
        so.stop();
    }
    
    /**
     * Plays the sound
     */
    public void play(){
        isPlaying = true;
        so.play();
    }
    
   /**
    * Rewinds the sound
    */
    public void rewind(){
        isPlaying = false;
        so.rewind();
    }
    
    /**
     * Pauses the sound
     */
    public void pause(){
        isPlaying = false;
        so.pause();
    }
    
    /**
     * Sets the gain/volume for the sound
     * @param gain New gain
     */
    public void setGain(float gain){
        this.gain = gain;
        so.setGain(gain * SoundEngine.getGlobalGain());
    }
    
    /**
     * Returns the current gain
     * @return Current gain
     */
    public float getGain(){
        return gain;
    }
    
    /**
     * Sets the sound data for this sound, loaded from the given file path
     * @param path Sound path
     */
    public void setSound(String path){
        setSound(SoundManager.loadSound(path));
    }
    
    /**
     * Sets the sound data for this sound
     * @param data New sound data
     */
    public void setSound(SoundData data){
        so.setBuffer(new ALBuffer(data));
    }
    
    /**
     * Returns the {@link ALBuffer} object used for this sound
     * @return Buffer used
     */
    public ALBuffer getBuffer(){
        return buffer;
    }
    
    /**
     * Returns the underlying {@link NativeSound} for this sound 
     * @return NativeSound object used
     */
    public NativeSound getSoundSource(){
        return so;
    }

    public SoundData getData(){
        return data;
    }
    
    /**
     * Frees all Resource associated with this source
     */
    public void remove(){
        so.remove();
    }
}
