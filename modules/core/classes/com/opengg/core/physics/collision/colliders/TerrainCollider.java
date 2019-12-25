/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision.colliders;

import com.opengg.core.math.geom.Triangle;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.CollisionSolver;
import com.opengg.core.physics.collision.ContactManifold;
import com.opengg.core.world.Terrain;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Javier
 */
public class TerrainCollider extends Collider {
    public Terrain terrain;
    public List<MeshTriangle> mesh = new ArrayList<>();
    
    public TerrainCollider(Terrain t, List<Triangle> triangles){
        this.terrain = t;
        for(Triangle triangle : triangles){
            MeshTriangle m = new MeshTriangle(triangle);
            mesh.add(m);
            //m.aabb.parent = this; //todo
        }
    }
    
    @Override
    public Optional<ContactManifold> collide(Collider c) {
        if(c instanceof ConvexHull){
            return CollisionSolver.HullTerrain((ConvexHull) c, this).map(ContactManifold::reverse);
        }
            
        return null;
    }
    
}
