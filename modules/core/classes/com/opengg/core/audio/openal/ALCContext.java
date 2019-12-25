/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio.openal;

import java.nio.IntBuffer;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;

/**
 * Low abstraction object version of an ALC context
 * @author Javier
 */
public class ALCContext {
    /**
     * Handle to ALC side of context
     */
    long context;
    
    /**
     * Creates an ALC Context using the specified device and settings<br>
     * If the settings buffer is null, default values will be used
     * @param device ALC device to create context on
     * @param data Data buffer used on creation
     */
    public ALCContext(ALCDevice device, IntBuffer data){
        context = alcCreateContext(device.device, data);
    }
    
    /**
     * Makes the ALC context current on the current thread
     */
    public void makeCurrent(){
        alcMakeContextCurrent(context);
    }
    
    /**
     * Destroys the context and all data associated with it, should only be used on device change or shutdown
     */
    public void destroy(){
        alcDestroyContext(context);
    }
}
