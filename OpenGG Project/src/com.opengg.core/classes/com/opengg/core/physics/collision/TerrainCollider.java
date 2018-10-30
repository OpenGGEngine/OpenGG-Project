/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.geom.Triangle;
import com.opengg.core.world.Terrain;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class TerrainCollider extends Collider{
    Terrain t;
    List<MeshTriangle> mesh = new ArrayList<>();
    
    public TerrainCollider(Terrain t, List<Triangle> triangles){
        this.t = t;
        for(Triangle triangle : triangles){
            MeshTriangle m = new MeshTriangle(triangle);
            mesh.add(m);
            m.aabb.parent = this;
        }
    }
    
    @Override
    public List<ContactManifold> collide(Collider c) {
        if(c instanceof ConvexHull){
            List<ContactManifold> cc = CollisionSolver.HullTerrain((ConvexHull) c, this).stream().map(ContactManifold::reverse).collect(Collectors.toList());
        }
            
        return new ArrayList<>();
    }
    
}
