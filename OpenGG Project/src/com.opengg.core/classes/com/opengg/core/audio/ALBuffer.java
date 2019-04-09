/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.system.NativeResource;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.AL_BITS;
import static org.lwjgl.openal.AL10.AL_CHANNELS;
import static org.lwjgl.openal.AL10.AL_FREQUENCY;
import static org.lwjgl.openal.AL10.AL_SIZE;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetBufferi;

/**
 * Direct, low abstraction object version of an OpenAL buffer
 * @author Javier
 */
public class ALBuffer implements NativeResource {
    SoundData data;
    int id;

    /**
     * Creates an ALBuffer object from a given SoundData, and loads data into the buffer 
     * <p>Note, this creates a real OpenAL buffer, so an OpenAL instance must exist on the current thread for it to work correctly</p>
     * @param data 
     */
    public ALBuffer(SoundData data){
        id = alGenBuffers();
        AL10.alBufferData(id, data.format, data.data, data.samplerate);
    }
    
    /**
     * Returns the size of the buffer
     * @return Size of buffer
     */
    public int getSize(){
        return alGetBufferi(id, AL_SIZE);
    }
    
    /**
     * Returns the frequency of the data in the buffer, in an integer
     * @return Buffer data frequency 
     */
    public int getFrequency(){
        return alGetBufferi(id, AL_FREQUENCY);
    }
    
    /**
     * Returns the bit depth for the buffer
     * @return Bit depth of buffer
     */
    public int getBitDepth(){
        return alGetBufferi(id, AL_BITS);
    }
    
    /**
     * Returns the amount of channels in the buffer data
     * @return Amount of channels, between 1 and 7
     */
    public int getChannels(){
        return alGetBufferi(id, AL_CHANNELS);
    }
    
    /**
     * Returns the sound data associated with this buffer
     * @return Sound data in buffer
     */
    public SoundData getData(){
        return data;
    }
    
    /**
     * Deletes the buffer from OpenAL
     */
    public void remove(){
        alDeleteBuffers(id);
    }

    @Override
    public Runnable onDestroy() {
        int nid = id;
        return () -> alDeleteBuffers(nid);
    }
}
