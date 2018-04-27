/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import org.lwjgl.openal.ALCCapabilities;

/**
 * Low abstraction object version of an ALC device
 * @author Javier
 */
public class ALCDevice {
    /**
     * ALC device handle
     */
    long device;
    
    /**
     * Capabilities available in the indicated device
     */
    ALCCapabilities caps;
    
    /**
     * Creates an ALC device using the given source (or uses default device if the source is null), 
     * and creates {@link org.lwjgl.openal.ALCCapabilities capabilities} available in the device
     * @param source Hardware device to use in creation of the ALC device, use null for system default
     */
    public ALCDevice(ByteBuffer source){
        device = alcOpenDevice(source);
        caps = createCapabilities();
    }
    
    /**
     * Creates capabilities for the device
     * @return Device capabilities
     */
    public ALCCapabilities createCapabilities(){
        return ALC.createCapabilities(device);
    }
    
    /**
     * Returns device {@link org.lwjgl.openal.ALCCapabilities capabilities} if they've been previously created
     * @return Device capabilites
     */
    public ALCCapabilities getCapabilities(){
        return caps;
    }
    
    /**
     * Creates an {@link ALCContext } from this device, given settings data (or default data if data is null)
     * @param data IntBuffer containing startup data for the context, or null for default
     * @return ALCContext object for use for this device
     */
    public ALCContext getContextFromDevice(IntBuffer data){
        return new ALCContext(this, data);
    }
    
    /**
     * Closes this device and frees all Resource and contexts
     */
    public void destroy(){
        alcCloseDevice(device);
    }
}
