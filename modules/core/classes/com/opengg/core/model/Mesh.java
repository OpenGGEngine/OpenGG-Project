package com.opengg.core.model;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.objects.DrawnObject;
import com.opengg.core.render.objects.MaterialRenderable;
import com.opengg.core.system.Allocator;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private FloatBuffer vbo;
    private IntBuffer indexBuffer;
    private Material material = Material.defaultmaterial; public int matIndex = -1;
    public boolean genAnim = false;
    private final boolean genTangents = true;
    private boolean isTriStrip = false;

    private static final int VBO_NOANIM = 8,VBO_ANIM = 16;

    private ArrayList<GGVertex> vertices = new ArrayList<>();

    private GGBone[] bones;

    private List<Vector3f> convexHull;

    public Mesh(ArrayList<GGVertex> vertices, int[] indices, boolean genTangents,boolean compressNormal){

        ByteBuffer realBuf = Allocator.alloc(vertices.size() * 28);

        for (GGVertex vertex : vertices) {
            Vector3f position = vertex.position;
            realBuf.putFloat(position.x).putFloat(position.y).putFloat(position.z);
            Vector3f normal = vertex.normal;
            normal = normal.normalize();
            realBuf.put((byte) 0);
            realBuf.put((byte) 255);
            realBuf.put((byte) 0);
            realBuf.put((byte) 255);
            realBuf.put((byte) 127);
            realBuf.put((byte) 127);
            realBuf.put((byte) 127);
            realBuf.put((byte) 127);
            Vector2f uv = vertex.uvs;
            realBuf.putFloat(uv.x).putFloat(uv.y);
        }
        realBuf.flip();
        vbo = realBuf.asFloatBuffer();
        setIndexBuffer(Allocator.allocInt(indices.length).put(indices).flip());
        this.genAnim = genAnim;
        this.vertices = vertices;
    }
    public Mesh(ArrayList<GGVertex> vertices, int[] indices, boolean genAnim){

        vbo = Allocator.allocFloat(vertices.size() * (  (genAnim?VBO_ANIM:VBO_NOANIM) + (genTangents ? 3 : 4) ) );

        for (GGVertex vertex : vertices) {
            Vector3f position = vertex.position;
            vbo.put(position.x).put(position.y).put(position.z);
            Vector3f normal = vertex.normal;
            vbo.put(normal.x).put(normal.y).put(normal.z);
            if (genTangents) {
                Vector3f tangent = vertex.tangent;
                vbo.put(tangent.x).put(tangent.y).put(tangent.z);
            } else {
                vbo.put(0f).put(0f).put(0f).put(0f);
            }
            Vector2f uv = vertex.uvs;
            vbo.put(uv.x).put(uv.y);
            if (genAnim) {
                vbo.put(vertex.jointIndices.x).put(vertex.jointIndices.y).put(vertex.jointIndices.z).put(vertex.jointIndices.w);
                vbo.put(vertex.weights.x).put(vertex.weights.y).put(vertex.weights.z).put(vertex.weights.w);
            }
        }
        vbo.flip();
        setIndexBuffer(Allocator.allocInt(indices.length).put(indices).flip());
        this.genAnim = genAnim;
        this.vertices = vertices;
    }

    public Mesh(FloatBuffer vbo, IntBuffer ibo){
        this.setVbo(vbo);
        this.setIndexBuffer(ibo);
    }

    public boolean hasConvexHull() {
        return convexHull != null;
    }

    public List<Vector3f> getConvexHull() {
        return convexHull;
    }

    public Renderable getDrawable(){
        DrawnObject temp = DrawnObject.create(genAnim ?
                RenderEngine.getTangentAnimVAOFormat() : RenderEngine.getTangentVAOFormat(),
                this.getIndexBuffer(), this.getVbo());
        if(isTriStrip)
            temp.setRenderType(DrawnObject.DrawType.TRIANGLE_STRIP);
        if(true){
            temp.setFormat(RenderEngine.ttFormat);
        }
        return new MaterialRenderable(temp, this.getMaterial());
    }

    public ArrayList<GGVertex> getVertices(){
        return vertices;
    }


    public FloatBuffer getVbo() {
        return vbo;
    }

    public void setVbo(FloatBuffer vbo) {
        this.vbo = vbo;
    }

    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public void setIndexBuffer(IntBuffer indexBuffer) {
        this.indexBuffer = indexBuffer;
    }

    public GGBone[] getBones() {
        return bones;
    }

    public void setBones(GGBone[] bones) {
        this.bones = bones;
    }

    public void setConvexHull(List<Vector3f> convexHull) {
        this.convexHull = convexHull;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setTriStrip(boolean triStrip){
        this.isTriStrip = triStrip;
    }
}
