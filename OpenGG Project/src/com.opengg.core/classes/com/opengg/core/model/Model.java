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

/**
 * Represents a 3D model capable of being rendered <br>
 *     In the majority of cases, this will represent a model loaded from a file in the Binary Model Format (.bmf).
 *     All BMF models contain some form of renderable object stored in a list of individual meshes, with each mesh
 *     containing a separate material. <br>
 *     Additionally, each model can contain a set of animations and a set of skeletal transformation nodes.
 */
public class Model implements Resource {
    private ArrayList<Mesh> meshes;
    private ArrayList<Material> materials = new ArrayList<>();
    private HashMap<String,GGAnimation> animations = new HashMap<>();
    private boolean isAnim = false;
    private String fileLocation;
    private String name;
    private GGNode rootAnimationNode;
    private long exportConfig;
    private String vaoFormat;
    private AABB colliderBox;

    public Model(ArrayList<Mesh> meshes, String name){
        this.meshes = meshes;
        this.name = name;
    }

    /**
     * Generates a drawable usable to render this model
     * @return
     */
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

    public void setMaterials(ArrayList<Material> materials) {
        this.materials = materials;
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

    public boolean isAnimated() {
        return isAnim;
    }

    public void setAnimated(boolean anim) {
        isAnim = anim;
    }

    public String getVaoFormat() {
        return vaoFormat;
    }

    public void setVaoFormat(String vaoFormat) {
        this.vaoFormat = vaoFormat;
    }

    public AABB getColliderBox() {
        return colliderBox;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public HashMap<String, GGAnimation> getAnimations() {
        return animations;
    }

    public GGNode getRootAnimationNode() {
        return rootAnimationNode;
    }

    public void setRootAnimationNode(GGNode rootAnimationNode) {
        this.rootAnimationNode = rootAnimationNode;
    }


    public long getExportConfig() {
        return exportConfig;
    }

    public void setExportConfig(long exportConfig) {
        this.exportConfig = exportConfig;
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
