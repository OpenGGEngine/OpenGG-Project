/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

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
    
    public ModelRenderComponent(){}
    
    public ModelRenderComponent(Model model){
        super(model.getDrawable());
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
        s.add(model.getName());
    }
    
    @Override
    public void deserialize(Deserializer s){
        super.deserialize(s);
        Model m = ModelLoader.loadModel(Resource.getModelPath(s.getString()));
        this.model = m;
        //this.g = m.getDrawable();
    }
}
