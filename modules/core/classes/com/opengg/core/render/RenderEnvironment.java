package com.opengg.core.render;

import com.opengg.core.render.light.Light;
import com.opengg.core.world.Skybox;

import java.util.ArrayList;
import java.util.List;

public class RenderEnvironment{
    private final List<SceneRenderUnit> pairs = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    private Skybox skybox;

    public void addRenderUnit(SceneRenderUnit pair){
        for(int i = 0; i < pairs.size(); i++){
            var nPair = pairs.get(i);
            if(pair.compareTo(nPair) < 0){
                pairs.add(i, pair);
                return;
            }
        }

        pairs.add(pair);
/*
        int idx = pairs.size()/2;

        while (true){
            if(idx >= pairs.size()-1){
                pairs.add(pair);
                return;
            }else if(idx == 0){
                pairs.add(0, pair);
                return;
            }
            var nPair = pairs.get(idx);
            var nPair2 = pairs.get(idx+1);

            var nPairResult = pair.compareTo(nPair);
            var nPairResult2 = pair.compareTo(nPair2);

            if(nPairResult == 0 || (nPairResult < 0 && nPairResult2 > 0)){
                pairs.add(idx, pair);
                return;
            }else if(nPairResult < 0){
                idx *= 1.5;
            }else if(nPairResult2 > 0){
                idx /= 2;
            }
        }*/
    }



    public void removeRenderUnit(SceneRenderUnit obj) {
        pairs.remove(obj);
    }

    public void addLight(Light light){
        if(lights.contains(light)) return;
        lights.add(light);
    }

    public void removeLight(Light light){
        lights.remove(light);
    }

    public List<SceneRenderUnit> getRenderUnits(){
        return pairs;
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
