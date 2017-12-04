/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Triangle;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Face;
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
    
    public Mesh(com.opengg.core.model.Mesh model){
        for(Face face : model.faces){
            this.faces.add(new Triangle(face.v1.v, face.v2.v, face.v3.v));
        }
    }

    public List<Triangle> getFaces() {
        return faces;
    }
    
    public List<Vector3f> getPoints(){
        List<Vector3f> points = new ArrayList(faces.size()*3);
        for(Triangle t : faces){
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
    public Contact isColliding(Collider c) {
        if(c instanceof Mesh)
            return CollisionSolver.MeshMesh(this, (Mesh)c);
        else if(c instanceof ConvexHull){
            Contact contact = CollisionSolver.HullMesh((ConvexHull)c, this);
            if(contact == null) return null;
            return contact.reverse();
        }
        else if(c == null)
            return CollisionSolver.MeshGround(this);
        return null;
    }
}
