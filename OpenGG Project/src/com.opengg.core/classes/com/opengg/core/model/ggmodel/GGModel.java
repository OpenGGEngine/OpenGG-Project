package com.opengg.core.model.ggmodel;

import com.opengg.core.model.Material;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;

import java.util.ArrayList;

public class GGModel {
    public ArrayList<GGMesh> meshes = new ArrayList<>();
    public ArrayList<Material> materials = new ArrayList<Material>();
    public boolean isAnim = false;
    public String fileLocation;

    public GGModel(ArrayList<GGMesh> meshes){
        this.meshes = meshes;

    }
    public Drawable getDrawable(){
        ArrayList<Drawable> objects = new ArrayList<>();

        for(GGMesh mesh: meshes){
            objects.add(mesh.getDrawable());
        }

        DrawnObjectGroup group = new DrawnObjectGroup(objects);
        return group;
    }
}
