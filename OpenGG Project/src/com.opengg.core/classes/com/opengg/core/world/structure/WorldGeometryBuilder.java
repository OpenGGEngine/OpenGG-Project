package com.opengg.core.world.structure;

import com.opengg.core.engine.Resource;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;

public class WorldGeometryBuilder{
    public static WorldGeometry fromModel(String path, Vector3f pos, Quaternionf rot, Vector3f scale, boolean render, boolean collide){
        return fromModel(Resource.getModel(path), pos, rot, scale, render, collide);
    }

    public static WorldGeometry fromModel(Model model, Vector3f pos, Quaternionf rot, Vector3f scale, boolean render, boolean collide){
        var geom = new ModelWorldGeometry();
        geom.initialize(model, pos, rot, scale, render, collide);
        return geom;
    }
}
