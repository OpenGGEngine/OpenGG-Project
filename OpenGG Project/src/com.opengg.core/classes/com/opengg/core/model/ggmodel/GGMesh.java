package com.opengg.core.model.ggmodel;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.MaterialDrawnObject;
import com.opengg.core.system.Allocator;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class GGMesh {
    FloatBuffer vbo;
    IntBuffer ibo;
    Material main = Material.defaultmaterial; int matIndex = -1;

    private static final int VBO_NOANIM = 11,VBO_ANIM = 19;

    @Deprecated
    public GGMesh(Vector3f[] positions,Vector3f[] normals, Vector2f[] uvs) throws Exception{
        throw new Exception("Don't use the non-tangent constructor.");
    }
    public GGMesh(Vector3f[] positions,Vector3f[] normals,Vector3f[] tangents, Vector2f[] uvs,int[] indices){

        vbo = Allocator.allocFloat(positions.length * VBO_NOANIM);

        for(int i = 0;i < positions.length;i++){
            Vector3f position = positions[i];
            vbo.put(position.x).put(position.y).put(position.z);
            Vector3f normal = normals[i];
            vbo.put(normal.x).put(normal.y).put(normal.z);
            Vector3f tangent = positions[i];
            vbo.put(tangent.x).put(tangent.y).put(tangent.z);
            Vector2f uv = uvs[i];
            vbo.put(uv.x).put(uv.y);
        }
        vbo.flip();
        ibo = Allocator.allocInt(indices.length);
        ibo.put(indices);
        ibo.flip();
    }
    public GGMesh(FloatBuffer vbo,IntBuffer ibo){
        this.vbo = vbo;
        this.ibo = ibo;
    }

    public Drawable getDrawable(){
        System.out.println(this.main.mapKdFilename);
        DrawnObject temp =new DrawnObject(RenderEngine.tangentVAOFormat,this.ibo,this.vbo);
        return new MaterialDrawnObject(temp,this.main);
    }

}
