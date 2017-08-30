/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.NativeSound;
import com.opengg.core.audio.Sound;
import com.opengg.core.audio.SoundManager;
import static com.opengg.core.engine.GGConsole.error;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alListener3f;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import org.lwjgl.openal.ALCCapabilities;

/**
 *
 * @author Javier
 */
public class AudioController {
    static long device;
    static long context;
    static boolean initialized;
    static float gain = 1;
    
    static ArrayList<Sound> sounds = new ArrayList<>();
    static void init() {
        device = alcOpenDevice((ByteBuffer)null);
        ALCCapabilities caps = ALC.createCapabilities(device);
        
        context = ALC10.alcCreateContext(device, (IntBuffer)null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(caps);
        if(AL10.alGetError() != AL10.AL_NO_ERROR)
            error("OpenAL Error in initialization: " + AL10.alGetError());
        else
            initialized = true;
        
        SoundManager.initialize();
    }
    
    public static void setListener(AudioListener s){
        alListener3f(AL_POSITION,s.pos.x,s.pos.y,s.pos.z);
        alListener3f(AL_VELOCITY,s.vel.x,s.vel.y,s.vel.z);

        int i = AL10.alGetError();
        if(i != AL10.AL_NO_ERROR)
            error("OpenAL Error in AudioHandler: " + i);
    }
    
    public static void addAudioSource(Sound s){
        sounds.add(s);
    }
    
    public static void removeAudioSource(Sound s){
        sounds.remove(s);
    }
    
    public static void setGlobalGain(float ngain){
        gain = ngain;
        for(Sound sound : sounds){
            sound.setGain(sound.getGain());
        }
    }
    
    public static float getGlobalGain(){
        return gain;
    }
    
    static void destroy(){
        for(Sound sound : sounds){
            
        }
        ALC.destroy();
    }
}
