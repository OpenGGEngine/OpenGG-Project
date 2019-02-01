/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ConvexHull extends Collider{
    public List<Vector3f> vertices = new ArrayList<>();
    List<ColliderFace> faces = new ArrayList<>();

    public ConvexHull(){

    }

    public ConvexHull(List<Vector3f> vertices, List<ColliderFace> faces){
        this.vertices = vertices;
        this.faces = faces;
    }
    
    public ConvexHull(List<Vector3f> vertices){
        this.vertices = vertices;
    }

    @Override
    public List<ContactManifold> collide(Collider c) {
        if(c instanceof ConvexHull)
            return CollisionSolver.HullHull(this, (ConvexHull)c);
        else if(c instanceof Mesh)
            return CollisionSolver.HullMesh(this, (Mesh)c);
        else if(c instanceof TerrainCollider)
            return CollisionSolver.HullTerrain(this, (TerrainCollider) c);
        else if(c instanceof Floor)
            return CollisionSolver.HullGround(this);
        return new ArrayList<>();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(vertices.size());
        for(var v : vertices)
            out.write(v);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            vertices.add(in.readVector3f());
        }
    }
}
