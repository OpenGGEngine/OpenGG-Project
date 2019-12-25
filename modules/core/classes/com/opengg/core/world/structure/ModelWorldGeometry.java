package com.opengg.core.world.structure;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class ModelWorldGeometry extends WorldGeometry{
    private Model model;

    public void initialize(String model, Vector3f pos, Quaternionf rot, Vector3f scale, boolean render, boolean collide){
        this.initialize(Resource.getModel(model), pos, rot, scale, render, collide);
    }

    public void initialize(Model model, Vector3f pos, Quaternionf rot, Vector3f scale, boolean render, boolean collide){
        super.initalize(pos, rot, scale);
        this.model = model;
        OpenGG.asyncExec(() -> {
            if(render)
                this.setRenderable(model.getDrawable());
            getParent().remakeRenderGroups();
        });
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        out.write(model.getName());
    }

    @Override
    public void deserialize(GGInputStream in, boolean render, boolean collide) throws IOException{
        this.model = Resource.getModel(in.readString());
        initialize(model, getPosition(), getRotation(), getScale(), render, collide);
    }
}
