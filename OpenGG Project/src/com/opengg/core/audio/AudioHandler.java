/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
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
public class AudioHandler {
    static long device;
    static long context;
    public static List<Integer> bufferids = new ArrayList<>();

    public static void init(int windowid) {
        device = alcOpenDevice((ByteBuffer)null);
        ALCCapabilities caps = ALC.createCapabilities(device);
        
        context = ALC10.alcCreateContext(device, (IntBuffer)null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(caps);
        if(AL10.alGetError() != AL10.AL_NO_ERROR)
            System.out.println("OpenAL Error: " + AL10.alGetError());

    }
    
    public static void setListener(AudioListener s){
        alListener3f(AL_POSITION,s.pos.x,s.pos.y,s.pos.z);
        alListener3f(AL_VELOCITY,s.vel.x,s.vel.y,s.vel.z);
        
        int i = AL10.alGetError();
        if(i != AL10.AL_NO_ERROR)
            System.out.println("OpenAL Error in AudioHandler: " + i);
    }
    public static AudioSource loadSound(URL filename){
        int buffer = alGenBuffers();
        bufferids.add(buffer);
        WaveData wavFile = WaveData.create(filename);
        AL10.alBufferData(buffer,wavFile.format,wavFile.data,wavFile.samplerate);
        wavFile.dispose();
        return new AudioSource(buffer);
    }
    public static void destroy(){
        for(int i: bufferids){
            alDeleteBuffers(i);
        }
    }
}
