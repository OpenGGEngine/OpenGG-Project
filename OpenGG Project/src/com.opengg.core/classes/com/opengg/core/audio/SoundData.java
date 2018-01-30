/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Structure containing all data associated with a given sound
 * @author Javier
 */
public class SoundData {
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
}
