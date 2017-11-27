/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.AL_BITS;
import static org.lwjgl.openal.AL10.AL_CHANNELS;
import static org.lwjgl.openal.AL10.AL_FREQUENCY;
import static org.lwjgl.openal.AL10.AL_SIZE;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetBufferi;

/**
 *
 * @author Javier
 */
public class ALBuffer {
    SoundData data;
    int id;

    public ALBuffer(SoundData data){
        id = alGenBuffers();
        AL10.alBufferData(id, data.format, data.data, data.samplerate);
    }
    
    public int getSize(){
        return alGetBufferi(id, AL_SIZE);
    }
    
    public int getFrequency(){
        return alGetBufferi(id, AL_FREQUENCY);
    }
    
    public int getBitDepth(){
        return alGetBufferi(id, AL_BITS);
    }
    
    public int getChannels(){
        return alGetBufferi(id, AL_CHANNELS);
    }
    
    public SoundData getData(){
        return data;
    }
    
    public void remove(){
        alDeleteBuffers(id);
    }
}
