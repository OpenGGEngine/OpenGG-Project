package com.opengg.core.render;

import com.opengg.core.render.light.Light;
import com.opengg.core.world.Skybox;

import java.util.ArrayList;
import java.util.List;

public class RenderEnvironment{
    private final List<RenderGroup> groups = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    private Skybox skybox;

    public void addGroup(RenderGroup group){
        groups.add(group);
    }

    public void addLight(Light light){
        lights.add(light);
    }

    public void removeLight(Light light){
        lights.remove(light);
    }

    public List<RenderGroup> getGroups(){
        return groups;
    }

    public List<Light> getLights(){
        return lights;
    }

    public Skybox getSkybox(){
        return skybox;
    }

    public void setSkybox(Skybox skybox){
        this.skybox = skybox;
    }
}
