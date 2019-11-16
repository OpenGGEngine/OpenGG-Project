/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.engine.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.lwjgl.openal.AL10.AL_STOPPED;

/**
 * Soundtrack containing a list of songs
 * @author Javier
 */
public class Soundtrack {
    /**
     * List of songs in soundtrack
     */
    List<Sound> soundtrack = new ArrayList<>();
    /**
     * Current sound
     */
    Sound current;
    
    /**
     * Creates an empty soundtrack
     */
    public Soundtrack(){
    }
    
    /**
     * Adds a song to the soundtrack, loaded from the given path
     * @param path Path to song
     */
    public void addSong(String path){
        soundtrack.add(new Sound(Resource.getSoundData(path)));
        if(current == null)
            current = soundtrack.get(0);
    }
    
    /**
     * Adds a song to the soundtrack, loaded from the given data
     * @param data Sound data
     */
    public void addSong(SoundData data){
        soundtrack.add(new Sound(data));
        if(current == null)
            current = soundtrack.get(0);
    }
    
    /**
     * Adds a song to the soundtrack, directly from the sound
     * @param sound Sound to add
     */
    public void addSong(Sound sound){
        soundtrack.add(sound);
        if(current == null)
            current = soundtrack.get(0);
    }
    
    /**
     * Gets the currently playing or paused song
     * @return Current sound
     */
    public Sound getCurrentSound(){
        return current;
    }
    
    /**
     * Goes to the next sound in the track
     */
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
    
    /**
     * Stops the soundtrack
     */
    public void stop(){
        current.stop();
    }
    
    /**
     * Rewinds and plays the current song
     */
    public void play(){
        if(soundtrack.isEmpty()) return;
        current.rewind();
        current.play();
    }
    
    /**
     * Polls if the song ended, should rarely be called
     */
    public void update(){
        if(current == null) return;
        if(current.getSoundSource().getState() == AL_STOPPED)
            next();
    }
    
    /**
     * Shuffles soundtrack
     */
    public void shuffle(){
        stop();
        current.rewind();
        Collections.shuffle(soundtrack);
        current = soundtrack.get(0);
    }

    /**
     * Sets the volume for the whole soundtrack
     * @param volume
     */
    public void setVolume(float volume){
        soundtrack.forEach(s -> s.setGain(volume));
    }

    public List<Sound> getSongs() {
        return soundtrack;
    }
}
