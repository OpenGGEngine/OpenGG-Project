package com.opengg.core.model;

import com.opengg.core.engine.Resource;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObjectGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class Model implements Resource {
    public ArrayList<Mesh> meshes = new ArrayList<>();
    public ArrayList<Material> materials = new ArrayList<Material>();
    public HashMap<String,GGAnimation> animations = new HashMap<>();
    public boolean isAnim = false;
    public String fileLocation;
    private String name;
    public GGNode root;

    public Model(ArrayList<Mesh> meshes, String name){
        this.meshes = meshes;
        this.name = name;
    }

    public Drawable getDrawable(){
        ArrayList<Drawable> objects = new ArrayList<>(meshes.size());

        for(Mesh mesh: meshes) objects.add(mesh.getDrawable());

        DrawnObjectGroup group = new DrawnObjectGroup(objects);
        return group;
    }

    public String getName(){
        return name;
    }

    public ArrayList<Mesh> getMeshes() {
        return meshes;
    }

    public ArrayList<Material> getMaterials() {
        return materials;
    }

    @Override
    public Type getType() {
        return Type.MODEL;
    }

    @Override
    public String getSource() {
        return name;
    }
}
