/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import java.nio.IntBuffer;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;

/**
 *
 * @author Javier
 */
public class ALCContext {
    long context;
    
    public ALCContext(ALCDevice device, IntBuffer data){
        context = alcCreateContext(device.device, data);
    }
    
    public void makeCurrent(){
        alcMakeContextCurrent(context);
    }
    
    public void destroy(){
        alcDestroyContext(context);
    }
}
