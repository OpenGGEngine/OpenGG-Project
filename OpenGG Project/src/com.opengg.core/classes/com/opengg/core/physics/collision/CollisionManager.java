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
    private static ColliderGroup coll = new ColliderGroup();
    
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
            ContactManifold cm = null;
            for(Collider c : next.colliders){
                ContactManifold nxt = c.isColliding(null);
                if(nxt != null) cm = nxt;
            }
            if(cm != null){
                Collision c = new Collision();
                c.thiscollider = next;
                c.other = coll;
                c.manifolds.add(cm);
                collisions.add(c);
            }
            
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
                
                float jdenom = 1/e1.mass + 1/e2.mass + 
                        (e1.inertialMatrix.multiply(R1.cross(mf.normal)).cross(R1).add(
                                e2.inertialMatrix.multiply(R2.cross(mf.normal)).cross(R2)))
                                .dot(mf.normal);
                float jnum = (vr.dot(mf.normal)*-(1+(e1.restitution + e2.restitution)/2f));
                float jr = jnum/jdenom;
                Vector3f force = mf.normal.multiply(jr);
                
                
                float jd = e1.dynamicfriction * jr;
                float js = e1.staticfriction * jr;
                Vector3f td = vr.subtract(vr.multiply(vr.dot(mf.normal))).add(1/Float.MAX_VALUE);
                Vector3f t = td.divide(td.abs());
                Vector3f jf = t.multiply(-jd);

                e1.angvelocity = e1.angvelocity.add(e1.inertialMatrix.inverse().multiply(R1.cross(force.divide(e1.mass))).add(jf));
                e2.angvelocity = e2.angvelocity.subtract(e2.inertialMatrix.inverse().multiply(R2.cross(force.divide(e2.mass))).add(jf));
                
                e1.velocity = e1.velocity.add(force.divide(e1.mass));
                e2.velocity = e2.velocity.subtract(force.divide(e2.mass));
                
                e1.lowestContact = mf.normal;
                e2.lowestContact = mf.normal.inverse();
            }else if(e1 == null ^ e2 == null){
                PhysicsEntity e;
                if(e1 != null){
                    e = e1;
                }else{
                    e = e2;
                    mf.normal = mf.normal.inverse();
                }
                
                e.setPosition(e.getPosition().add(mf.normal.multiply(mf.depth)));
                Vector3f R = mf.point.subtract(e.getPosition());
                Vector3f v = e.velocity.add(R.cross(e.angvelocity));
                
                
                float jdenom = 1/e.mass + (e.inertialMatrix.multiply(
                        R.cross(mf.normal)).cross(R)).dot(mf.normal);
                float jnum = v.dot(mf.normal) * -(1 + e.restitution);
                float jr = jnum/jdenom;
                Vector3f force = mf.normal.multiply(jr);
                
                float jd = e.dynamicfriction * jr;
                float js = e.staticfriction * jr;
                Vector3f td = v.subtract(v.multiply(v.dot(mf.normal))).add(1/Float.MAX_VALUE);
                Vector3f t = td.divide(td.abs());
                Vector3f jf = t.multiply(-jd);
                
                e.angvelocity = e.angvelocity.add(e.inertialMatrix.inverse().multiply(R.cross(force.divide(e.mass))).subtract(jf));
                e.velocity = e.velocity.add(force.divide(e.mass));
                e.lowestContact = mf.normal;
            }
        }
    }
}
