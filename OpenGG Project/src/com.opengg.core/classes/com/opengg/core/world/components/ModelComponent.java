/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.engine.Resource;
import com.opengg.core.model.Model;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.physics.CollisionComponent;

import java.io.IOException;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelComponent extends RenderComponent implements ResourceUser{
    Model model;
    
    public ModelComponent(){}

    public ModelComponent(Model model){
        this(model,false);
    }

    public ModelComponent(Model model, boolean collider){
        super();
        setModel(model);
        this.setFormat(model.isAnim? RenderEngine.tangentAnimVAOFormat: RenderEngine.tangentVAOFormat);
        this.setTransparency(true);

        if(collider){
            this.attach(new CollisionComponent(model.getCollider()));
        }
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
            this.setShader("object");
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
