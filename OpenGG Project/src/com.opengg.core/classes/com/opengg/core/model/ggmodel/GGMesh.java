package com.opengg.core.model.ggmodel;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.MaterialDrawnObject;
import com.opengg.core.system.Allocator;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class GGMesh {
    public FloatBuffer vbo;
    public IntBuffer ibo;
    public Material main = Material.defaultmaterial; public int matIndex = -1;
    public boolean genAnim = false;
    public boolean genTangents = true;

    private static final int VBO_NOANIM = 8,VBO_ANIM = 16;

    ArrayList<GGVertex> vertices = new ArrayList<>();

    public GGBone[] bones;

    public ConvexHull convexHull;

    public GGMesh(ArrayList<GGVertex> vertices,int[] indices,boolean genAnim){

        vbo = Allocator.allocFloat(vertices.size() * (  (genAnim?VBO_ANIM:VBO_NOANIM) + (genTangents?3:4) ) );

        for(int i = 0;i < vertices.size();i++){
            GGVertex vertex = vertices.get(i);
            Vector3f position = vertex.position;
            vbo.put(position.x).put(position.y).put(position.z);
            Vector3f normal = vertex.normal;
            vbo.put(normal.x).put(normal.y).put(normal.z);
            if(genTangents) {
                Vector3f tangent = vertex.tangent;
                vbo.put(tangent.x).put(tangent.y).put(tangent.z);
            }else{
                vbo.put(0f).put(0f).put(0f).put(0f);
            }
            Vector2f uv = vertex.uvs;
            vbo.put(uv.x).put(uv.y);
            if(genAnim){
                vbo.put(vertex.jointIndices.x).put(vertex.jointIndices.y).put(vertex.jointIndices.z).put(vertex.jointIndices.w);
                vbo.put(vertex.weights.x).put(vertex.weights.y).put(vertex.weights.z).put(vertex.weights.w);
            }
        }
        vbo.flip();
        ibo = Allocator.allocInt(indices.length);
        ibo.put(indices);
        ibo.flip();
        this.genAnim = genAnim;
    }

    public GGMesh(FloatBuffer vbo,IntBuffer ibo){
        this.vbo = vbo;
        this.ibo = ibo;
    }

    public Drawable getDrawable(){
        DrawnObject temp =new DrawnObject(genAnim?RenderEngine.tangentAnimVAOFormat:RenderEngine.tangentVAOFormat,this.ibo,this.vbo);
        return new MaterialDrawnObject(temp,this.main);
    }

}
