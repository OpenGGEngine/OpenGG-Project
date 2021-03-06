/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.engine.Resource;

import java.nio.ShortBuffer;

/**
 * Structure containing all data associated with a given sound
 * @author Javier
 */
public class SoundData implements Resource{
    /**
     * Buffer containing the actual sound
     */
    ShortBuffer data;
    
    /**
     * Origin of the sound, normally the relative path from which the sound was loaded
     */
    String origin;
    
    /**
     * Amount of channels in this sound
     */
    int channels;
    
    /**
     * Format of this sound
     */
    int format;
    
    /**
     * Sound sample rate, in kHz
     */
    int samplerate;

    public ShortBuffer getData() {
        return data;
    }

    public int getChannels() {
        return channels;
    }

    public int getFormat() {
        return format;
    }

    public int getSampleRate() {
        return samplerate;
    }

    @Override
    public Type getType() {
        return Type.SOUND;
    }

    @Override
    public String getSource() {
        return origin;
    }
}
