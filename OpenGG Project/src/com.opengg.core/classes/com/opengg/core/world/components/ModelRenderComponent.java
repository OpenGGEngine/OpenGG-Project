/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelRenderComponent extends RenderComponent{
    Model model;
    
    public ModelRenderComponent(){}

    public ModelRenderComponent(Model model){
        super();
        setModel(model);
        this.transparent = true;
    }
    
    public Model getModel(){
        return model;
    }
    
    public void setModel(Model model){
        this.model = model;
        if(model.isanimated){
            this.shader = "animation";
            this.format = RenderEngine.getAnimationFormat(); 
        }else{
            this.shader = "object";
            this.format = RenderEngine.getDefaultFormat();
        }
        
        OpenGG.asyncExec(() -> {
            setDrawable(model.getDrawable());
        });
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(model.getName());
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        String path = in.readString();
        model = ModelLoader.loadModel(path);
        OpenGG.asyncExec(() -> {
            this.g = model.getDrawable();
        });
    }
}
