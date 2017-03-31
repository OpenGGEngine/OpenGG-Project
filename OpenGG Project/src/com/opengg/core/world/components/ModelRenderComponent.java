/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelRenderComponent extends RenderComponent{
    Model model;
    String name;
    boolean frame = false;
    
    public static ModelRenderComponent getFramework(String name){
        return new ModelRenderComponent(name);
    }
    
    public ModelRenderComponent(){}
    
    private ModelRenderComponent(String name){
        frame = true;
        this.name = name;
    }

    public ModelRenderComponent(Model model){
        super();
        OpenGG.addExecutable(() -> {
            this.g = model.getDrawable();
        });
        this.model = model;
    }
    
    public Model getModel(){
        return model;
    }
    
    public void setModel(Model model){
        this.model = model;
        setDrawable(model.getDrawable());
    }
    
    @Override
    public void serialize(Serializer s){
        super.serialize(s);
        if(frame){
            s.add(name);
        }else{
            s.add(model.getName());
        }
    }
    
    @Override
    public void deserialize(Deserializer s){
        super.deserialize(s);
        String path = s.getString();
        if(model == null){
            model = ModelLoader.loadModel(Resource.getModelPath(path));
            OpenGG.addExecutable(() -> {
                this.g = model.getDrawable();
            });
        }
    }
}
