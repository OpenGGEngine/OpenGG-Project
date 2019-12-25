/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision.colliders;

import com.opengg.core.math.geom.Triangle;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.CollisionSolver;
import com.opengg.core.physics.collision.ContactManifold;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Javier
 */
public class Mesh extends Collider {
    private List<MeshTriangle> faces = new ArrayList<>();
    
    public Mesh(List<Triangle> faces){
        for(Triangle tri : faces){
            MeshTriangle m = new MeshTriangle(tri);
            this.faces.add(m);
            //m.aabb.parent = this; //todo
        }
    }
    
    public Mesh(List<MeshTriangle> faces, boolean copy){
        this.faces = faces;
    }

    public List<MeshTriangle> getFaces() {
        return faces;
    }
    
    public List<Vector3f> getPoints(){
        List<Vector3f> points = new ArrayList(faces.size()*3);
        for(MeshTriangle t : faces){
            if(points.isEmpty()){
                points.add(t.a);
                points.add(t.b);
                points.add(t.c);
            }else{
                boolean a=false,b=false,c=false;
                for(Vector3f p : points){
                    if(p.equals(t.a)) a = true; 
                    if(p.equals(t.b)) b = true;
                    if(p.equals(t.c)) c = true;
                }
                if(!a) points.add(t.a);
                if(!b) points.add(t.b);
                if(!c) points.add(t.c);
            }
        }
        return points;
    }

    @Override
    public Optional<ContactManifold> collide(Collider c) {
        if(c instanceof ConvexHull){
            return CollisionSolver.HullMesh((ConvexHull)c, this).map(ContactManifold::reverse);
        }
        return null;
    }
}
