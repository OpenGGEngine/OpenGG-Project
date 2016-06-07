/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio;

import com.opengg.core.Vector3f;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alListener3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;


/**
 *
 * @author Javier
 */
public class AudioHandler {

    public static List<Integer> bufferids = new ArrayList<>();

    public static void init(int windowid) {
        ALCCapabilities cap = ALC.createCapabilities(windowid);
        AL.createCapabilities(cap);
        AL10.alEnable(windowid);
    }
    
    public static void setListenerPos(Vector3f pos){
        alListener3f(AL_POSITION,pos.x,pos.y,pos.z);
        alListener3f(AL_VELOCITY,0,0,0);
    }
    public static int loadSound(URL filename){
        int buffer = alGenBuffers();
        bufferids.add(buffer);
        WaveData wavFile = WaveData.create(filename);
        AL10.alBufferData(buffer,wavFile.format,wavFile.data,wavFile.samplerate);
        wavFile.dispose();
        return buffer;
    }
    public static void destroy(){
        for(int i: bufferids){
            alDeleteBuffers(i);
        }
    }
}
