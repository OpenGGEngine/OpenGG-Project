/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.util.GGOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Warren
 */
public class Animation {
    public int currentFrame;
    public List<AnimatedFrame> frames;
    private String name;
    private float duration;
    private float animcounter;

    public Animation(String name, List<AnimatedFrame> frames, float duration) {
        this.name = name;
        this.frames = frames;
        this.duration = duration;
        currentFrame = 0;
    }

    public AnimatedFrame getCurrentFrame() {
        if(frames.isEmpty())
            return new AnimatedFrame(0);
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
    
    public float getFrameDuration(){
        return (float) (duration/frames.size());
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
    
    public void writeBuffer(GGOutputStream out) throws IOException{
        out.write(name);
        out.write(duration);
        out.write(frames.size());
        for (AnimatedFrame frame : frames) {
            frame.writeBuffer(out);
        }
    }

    public void updateAnimation(float delta){
        animcounter += delta;
        if(animcounter > getFrameDuration()){
            animcounter = 0;
            nextFrame();
        }
    }
}
