/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.engine.Resource;
import com.opengg.core.model.Model;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.physics.RigidBodyComponent;

import java.io.IOException;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelComponent extends RenderComponent implements ResourceUser{
    private Model model;
    private boolean collider;
    
    public ModelComponent(){}

    public ModelComponent(Model model){
        this(model,false);
    }

    public ModelComponent(Model model, boolean collider){
        super();
        setModel(model);
        this.setFormat(model.isAnimated() ? RenderEngine.tangentAnimVAOFormat: RenderEngine.tangentVAOFormat);
        this.setTransparency(true);
        this.collider = collider;

        if(collider){
            setupCollider();
        }
    }

    private void setupCollider() {
        var collision = new RigidBodyComponent(model.getCollider(), false);
        collision.setSerializable(false);
        this.attach(collision);
    }
    public Model getModel(){
        return model;
    }
    
    public void setModel(Model model) {
        this.model = model;
        boolean hastan = model.getVaoFormat().contains("tangent");
        boolean hasanim = model.getVaoFormat().contains("anim");
        if(hastan&&hasanim){

        }else if(hastan){
            this.setFormat(RenderEngine.tangentVAOFormat);
            this.setShader("tangent");
        }else if(hasanim){

        }else{
        }

        OpenGG.asyncExec(() -> setRenderable(model.getDrawable()));
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(model.getName());
        out.write(collider);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        String path = in.readString();
        model = Resource.getModel(path);
        OpenGG.asyncExec(() -> setRenderable(model.getDrawable()));
        var collider = in.readBoolean();
        if (collider)
            setupCollider();
    }

    @Override
    public Resource getResource(){
        return getModel();
    }
}
