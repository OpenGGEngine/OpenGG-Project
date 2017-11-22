/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ConvexHull extends Collider{
    List<Vector3f> vertices = new ArrayList<>();
    List<HullFace> faces = new ArrayList<>();
    
    public ConvexHull(List<Vector3f> vertices, List<HullFace> faces){
        this.vertices = vertices;
        this.faces = faces;
    }
    
    public ConvexHull(List<Vector3f> vertices){
        this.vertices = vertices;
    }

    @Override
    public ContactManifold isColliding(Collider c) {
        if(c instanceof ConvexHull){
            return CollisionSolver.HullHull(this, (ConvexHull)c);
        }
        return null;
    }
}
