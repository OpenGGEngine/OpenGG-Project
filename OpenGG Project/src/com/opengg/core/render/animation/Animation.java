/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.animation;

import com.opengg.core.render.animation.AnimatedFrame;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Warren
 */
public class Animation {

    private int currentFrame;

    private List<AnimatedFrame> frames;

    private String name;
    
    private double duration;

    public Animation(String name, List<AnimatedFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        currentFrame = 0;
        this.duration = duration;
    }

    public AnimatedFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    public double getDuration() {
        return this.duration;        
    }
    
    public List<AnimatedFrame> getFrames() {
        return frames;
    }

    public String getName() {
        return name;
    }

    public AnimatedFrame getNextFrame() {
        nextFrame();
        return this.frames.get(currentFrame);
    }

    public void nextFrame() {
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            currentFrame = 0;
        } else {
            currentFrame = nextFrame;
        }
    }
    
    public void writeBuffer(DataOutputStream ds) throws IOException{
        ds.writeDouble(duration);
        ds.writeInt(frames.size());
        for (AnimatedFrame frame : frames) {
            frame.writeBuffer(ds);
        }
    }

}
