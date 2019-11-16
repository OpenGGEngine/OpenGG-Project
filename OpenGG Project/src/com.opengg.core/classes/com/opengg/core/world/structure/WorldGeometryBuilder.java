package com.opengg.core.world.structure;

import com.opengg.core.engine.Resource;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.render.texture.TextureData;

public class WorldGeometryBuilder{
    public static ModelWorldGeometry fromModel(String path, Vector3f pos, Quaternionf rot, Vector3f scale, boolean render, boolean collide){
        return fromModel(Resource.getModel(path), pos, rot, scale, render, collide);
    }

    public static ModelWorldGeometry fromModel(Model model, Vector3f pos, Quaternionf rot, Vector3f scale, boolean render, boolean collide){
        var geom = new ModelWorldGeometry();
        geom.initialize(model, pos, rot, scale, render, collide);
        return geom;
    }

    public static CuboidWorldGeometry fromCuboid(Vector3f position, Quaternionf rot, Vector3f lwh, TextureData tex, boolean collide){
        var geom =  new CuboidWorldGeometry();
        geom.initialize(position, rot, lwh, tex, collide);
        return geom;
    }

    public static CuboidWorldGeometry fromCuboid(Vector3f position, Quaternionf rot, Vector3f lwh){
        var geom =  new CuboidWorldGeometry();
        geom.initialize(position, rot, lwh);
        return geom;
    }
}
