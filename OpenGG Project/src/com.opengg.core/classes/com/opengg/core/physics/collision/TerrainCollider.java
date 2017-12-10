/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Triangle;
import com.opengg.core.world.Terrain;
import java.util.ArrayList;
import java.util.List;

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
    public Contact isColliding(Collider c) {
        if(c instanceof ConvexHull){
            Contact cc = CollisionSolver.HullTerrain((ConvexHull) c, this);
            return cc.reverse();
        }
            
        return null;
    }
    
}
