package com.opengg.core.model;

import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObjectGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Model implements Resource {
    public ArrayList<Mesh> meshes = new ArrayList<>();
    public ArrayList<Material> materials = new ArrayList<>();
    public HashMap<String,GGAnimation> animations = new HashMap<>();
    GGAnimation currentAnimation;
    public boolean isAnim = false;
    public String fileLocation;
    private String name;
    public GGNode root;
    public long exportConfig;
    public String vaoFormat;


    private AABB colliderBox;

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

    public ColliderGroup getCollider(){
        var hulls = meshes.stream()
                .filter(Mesh::hasConvexHull)
                .map(Mesh::getConvexHull)
                .collect(Collectors.toList());


        var realHulls = hulls.stream()
                .map(ConvexHull::new)
                .collect(Collectors.toList());

        var points = hulls.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return new ColliderGroup(new AABB(points), realHulls);
    }

    public void setAnimation(String name){
        currentAnimation = animations.get(name);
    }

    public void setAnimationProgress(double value){
        currentAnimation.current = value;
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
