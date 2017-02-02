/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.postprocess;

import static com.opengg.core.render.postprocess.PostProcessPipeline.sceneQuad;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class StageSet {
    List<Stage> stages;
    int func;
    int loc;
    public static final int ADD = 1, MULT = 2, SUB = 3, DIV = 4, SET = 5;
    
    public StageSet(int func, int loc){
        stages = new ArrayList<>();
        this.func = func;
        this.loc = loc;
    }
    
    public StageSet(int func){
        stages = new ArrayList<>();
        this.func = func;
        this.loc = 0;
    }
    
    public void addStage(Stage s){
        stages.add(s);
    }
    
    public void render(){
        for(Stage s : stages){
            s.use();
            sceneQuad.render();
            s.save(loc);
        }
    }
}
