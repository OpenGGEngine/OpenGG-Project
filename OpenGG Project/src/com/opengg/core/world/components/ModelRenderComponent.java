/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.model.Model;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelRenderComponent extends RenderComponent{
    Model model;
    
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
}
