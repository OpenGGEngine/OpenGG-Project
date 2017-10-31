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
 *
 * @author Javier
 */
public class ALCDevice {
    long device;
    ALCCapabilities caps;
    
    public ALCDevice(ByteBuffer source){
        device = alcOpenDevice(source);
        caps = createCapabilities();
    }
    
    public ALCCapabilities createCapabilities(){
        return ALC.createCapabilities(device);
    }
    
    public ALCCapabilities getCapabilities(){
        return caps;
    }
    
    public ALCContext getContextFromDevice(IntBuffer data){
        return new ALCContext(this, data);
    }
    
    public void destroy(){
        alcCloseDevice(device);
    }
}
