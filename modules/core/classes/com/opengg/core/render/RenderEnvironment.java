package com.opengg.core.render;

import com.opengg.core.render.light.Light;
import com.opengg.core.world.Skybox;

import java.util.ArrayList;
import java.util.List;

public class RenderEnvironment{
    private final List<SceneRenderUnit> renderUnits = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    private Skybox skybox;

    public void addRenderUnit(SceneRenderUnit newUnit){
        if(renderUnits.contains(newUnit)) return;
        for(int i = 0; i < renderUnits.size(); i++){
            var nPair = renderUnits.get(i);
            if(newUnit.compareTo(nPair) < 0){
                renderUnits.add(i, newUnit);
                return;
            }
        }

        renderUnits.add(newUnit);
    }

    public void removeRenderUnit(SceneRenderUnit obj) {
        renderUnits.remove(obj);
    }

    public void addLight(Light light){
        if(lights.contains(light)) return;
        lights.add(light);
    }

    public void removeLight(Light light){
        lights.remove(light);
    }

    public List<SceneRenderUnit> getRenderUnits(){
        return renderUnits;
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
