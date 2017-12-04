/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Triangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Mesh extends Collider{
    private List<Triangle> faces = new ArrayList<>();
    
    public Mesh(List<Triangle> faces){
        this.faces = faces;
    }

    public List<Triangle> getFaces() {
        return faces;
    }

    @Override
    public ContactManifold isColliding(Collider c) {
        if(c instanceof Mesh)
               return CollisionSolver.MeshMesh(this, (Mesh)c);
        return null;
    }
    
}
