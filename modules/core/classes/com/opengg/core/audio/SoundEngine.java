/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.audio.openal.ALCContext;
import com.opengg.core.audio.openal.ALCDevice;
import com.opengg.core.console.GGConsole;
import java.util.ArrayList;

import org.lwjgl.openal.*;

import static org.lwjgl.openal.AL10.*;

/**
 * Primary controller and manager for the OpenAL audio engine
 * @author Javier
 */
public class SoundEngine {
    private static ALCContext context;
    private static ALCDevice device;
    private static boolean initialized;
    private static float gain = 1;
    
    private static final ArrayList<Sound> sounds = new ArrayList<>();

    public static void initialize() {
        device = new ALCDevice(null);

        context = device.getContextFromDevice(null);
        context.makeCurrent();
        
        AL.createCapabilities(device.getCapabilities());
        
        if(AL10.alGetError() != AL10.AL_NO_ERROR)
            GGConsole.error("OpenAL Error in initialization: " + AL10.alGetError());
        else
            initialized = true;

        GGConsole.log("Initializing sound engine on " + ALUtil.getStringList(device.getDeviceID(), ALC11.ALC_CAPTURE_DEVICE_SPECIFIER).get(0));

        SoundManager.initialize();
        GGConsole.log("Audio Controller initialized, using OpenAL version " +  AL10.alGetString(AL10.AL_VERSION) 
                + " from " + AL10.alGetString(AL10.AL_VENDOR));
    }
    
    /**
     * Scans for and prints out any OpenAL errors that have arisen since the last call to this method
     */
    public static void checkForALErrors(){
        int i;
        while((i = alGetError()) != AL_NO_ERROR){
            GGConsole.error("OpenAL Error: " + i);
        }
    }
    
    /**
     * Restarts the engine, restarting all sounds and refreshing the soundtrack<br>
     * Note, this will stop any ongoing non-soundtrack sounds
     */
    public static void restart(){
        for(Sound sound : sounds){
            sound.stop();
            sound.rewind();
        }
        SoundtrackHandler.refresh();
    }
    
    public static void setListener(AudioListener s){
        alListener3f(AL_POSITION, s.getPosition().x, s.getPosition().y, s.getPosition().z);
        alListener3f(AL_VELOCITY, s.getVelocity().x, s.getVelocity().y, s.getVelocity().z);

        int i = AL10.alGetError();
        if(i != AL10.AL_NO_ERROR)
            GGConsole.error("OpenAL Error in AudioHandler: " + i);
    }
    
    /**
     * Adds a sound to be managed by the controller
     * @param s Sound to be managed
     */
    public static void addAudioSource(Sound s){
        sounds.add(s);
    }
    
    /**
     * Removes a sound from being managed by the controller
     * @param s Sound to be removed
     */
    public static void removeAudioSource(Sound s){
        sounds.remove(s);
    }
    
    /**
     * Sets the global/master volume for all managed sounds
     * @param ngain New volume
     */
    public static void setGlobalGain(float ngain){
        gain = ngain;
        for(Sound sound : sounds){
            sound.setGain(sound.getGain());
        }
    }
    
    /**
     * Returns the current master volume/gain
     * @return Current gain
     */
    public static float getGlobalGain(){
        return gain;
    }
    
    public static boolean isInitialized(){
        return initialized;
    }
    
    /**
     * Destroys and frees all sounds and closes the OpenAL context
     */
    public static void destroy(){
        for(Sound sound : sounds){
            sound.remove();
        }
        ALC.destroy();
    }

    private SoundEngine() {
    }
}
