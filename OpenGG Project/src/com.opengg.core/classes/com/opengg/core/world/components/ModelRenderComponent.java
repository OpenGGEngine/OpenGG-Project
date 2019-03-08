/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.engine.Resource;
import com.opengg.core.model.Model;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelRenderComponent extends RenderComponent implements ResourceUser{
    Model model;
    
    public ModelRenderComponent(){}

    public ModelRenderComponent(Model model){
        super();
        setModel(model);
        this.setFormat(model.isAnim? RenderEngine.tangentAnimVAOFormat: RenderEngine.tangentVAOFormat);
        this.setTransparency(true);
    }
    
    public Model getModel(){
        return model;
    }
    
    public void setModel(Model model) {
        this.model = model;
        boolean hastan = model.vaoFormat.contains("tangent");
        boolean hasanim = model.vaoFormat.contains("anim");
        if(hastan&&hasanim){
        }else if(hastan){
            this.setFormat(RenderEngine.tangentVAOFormat);
        }else if(hasanim){

        }else{
        }

        OpenGG.asyncExec(() -> setDrawable(model.getDrawable()));
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
        model = Resource.getModel(path);
        OpenGG.asyncExec(() -> setDrawable(model.getDrawable()));
    }

    @Override
    public Resource getResource(){
        return getModel();
    }
}
