/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsEntity;
import com.opengg.core.physics.PhysicsSystem;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class CollisionManager {
    private static List<Collision> collisions = new ArrayList<>();
    private static List<ColliderGroup> test = new LinkedList<>();
    
    public static void clearCollisions(){
        collisions.clear();
        test.clear();
    }
    
    public static void addToTest(ColliderGroup c){
        test.add(c);
    }
    
    public static void addToTest(List<ColliderGroup> c){
        test.addAll(c);
    }
    
    public static void testForCollisions(PhysicsSystem system){
        collisions.clear();
        for(ColliderGroup next : test){
            upper: for(ColliderGroup other : system.getColliders()){
                Collision col = null;
                if(next == other) continue;
                for(Collision c : collisions){
                    if(c.contains(next) > 0 && c.contains(other) > 0){
                        continue upper;
                    }
                }
                
                if(col == null){
                    col = next.testForCollision(other);
                    if(col != null) collisions.add(col);
                }
            }
        }
    }
    
    public static void processCollisionResponse(){
        for(Collision c : collisions){
            PhysicsEntity e1 = c.other.parent;
            PhysicsEntity e2 = c.thiscollider.parent;
            ContactManifold mf = c.manifolds.get(0);
            if(e1 != null && e2 != null){
                e1.setPosition(e1.getPosition().add(mf.normal.multiply(mf.depth).multiply(0.55f)));
                e2.setPosition(e2.getPosition().subtract(mf.normal.multiply(mf.depth).multiply(0.55f)));

                Vector3f R1 = mf.point.subtract(e1.getPosition());
                Vector3f R2 = mf.point.subtract(e2.getPosition());
                
                Vector3f v1 = e1.velocity.add(R1.cross(e1.angvelocity));
                Vector3f v2 = e2.velocity.add(R2.cross(e2.angvelocity));
                
                Vector3f vr = v1.subtract(v2);
                
                Vector3f td = vr.subtract(vr.multiply(vr.dot(mf.normal)));
                td = td.add(new Vector3f(0.000001f, 0.000001f, 0.000001f));
                Vector3f t = td.divide(td.abs());
                
                float jdiv = e1.mass + e2.mass + 
                        (e1.inertialMatrix.multiply(R1.cross(mf.normal)).cross(R1).add(
                                e2.inertialMatrix.multiply(R2.cross(mf.normal)).cross(R2)))
                                .dot(mf.normal);
                
                float jr = (vr.multiply(-(1+e1.restitution)).dot(mf.normal))/(
                        jdiv);
                
                float jd = Math.min(e1.frictionCoefficient+e2.frictionCoefficient,1)*jr;
                float jt = t.multiply(-jd).length();

                e1.angvelocity = e1.angvelocity.add(e1.inertialMatrix.multiply(R1.cross(mf.normal)).multiply(jr+jt));
                e2.angvelocity = e2.angvelocity.subtract(e2.inertialMatrix.multiply(R2.cross(mf.normal)).multiply(jr+jt));
                
                e1.velocity = e1.velocity.add(mf.normal.multiply(jr/e1.mass));
                e2.velocity = e2.velocity.subtract(mf.normal.multiply(jr/e2.mass));
            }
        }
    }
}
