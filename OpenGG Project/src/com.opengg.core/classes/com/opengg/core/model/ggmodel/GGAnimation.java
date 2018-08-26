package com.opengg.core.model.ggmodel;

public class GGAnimation {
    public String name;
    public double duration;
    public double ticksPerSec;

    public GGAnimation(String name, double duration, double ticksPerSec) {
        this.name = name;
        this.duration = duration;
        this.ticksPerSec = ticksPerSec;
    }

}
