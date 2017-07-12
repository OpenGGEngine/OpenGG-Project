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
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import java.io.IOException;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelRenderComponent extends RenderComponent{
    Model model;
    String mname;
    boolean frame = false;
    
    public static ModelRenderComponent getFramework(String name){
        return new ModelRenderComponent(name);
    }
    
    public ModelRenderComponent(){}
    
    private ModelRenderComponent(String name){
        frame = true;
        this.mname = name;
    }

    public ModelRenderComponent(Model model){
        super();
        OpenGG.addExecutable(() -> {
            setModel(model);
        });
        this.transparent = true;
    }
    
    public Model getModel(){
        return model;
    }
    
    public void setModel(Model model){
        this.model = model;
        setDrawable(model.getDrawable());
    }
    
    @Override
    public void serialize(GGByteOutputStream out) throws IOException{
        super.serialize(out);
        if(frame){
            out.write(mname);
        }else{
            out.write(model.getName());
        }
    }
    
    @Override
    public void deserialize(GGByteInputStream in) throws IOException{
        super.deserialize(in);
        String path = in.readString();
        if(model == null){
            model = ModelLoader.loadModel(Resource.getModelPath(path));
            OpenGG.addExecutable(() -> {
                this.g = model.getDrawable();
            });
        }
    }
}
