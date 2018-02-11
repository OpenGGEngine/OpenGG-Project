/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PostProcessingPass {
    public static final int ADD = 0, MULTIPLY = 1, SUBTRACT = 2, DIVIDE = 3, SET = 4;    
        
    List<Stage> stages = new ArrayList<>();
    int op = SET;
    
    public PostProcessingPass(int op, Stage stage){
        this(op, List.of(stage));
    }
    
    public PostProcessingPass(int op, Stage... stages){
        this(op, List.of(stages));
    }
    
    public PostProcessingPass(int op, List<Stage> stages){
        this.stages = stages;
        this.op = op;
    }
    
    public void render(){
        for(Stage stage : stages){
            stage.render();
        }
    }
}
