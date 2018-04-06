/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.FastMath;
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
    public static boolean parallelProcessing = true;
    
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
    
    public static void testForCollision(ColliderGroup next, PhysicsSystem system){
        Contact cm = null;
        for(Collider c : next.colliders){
            Contact nxt = c.isColliding(null);
            if(nxt != null) cm = nxt;
        }
        if(cm != null){
            Collision c = new Collision();
            c.thiscollider = next;
            c.other = coll;
            c.manifolds.addAll(cm.manifolds);
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
    
    public static void testForCollisions(PhysicsSystem system){
        collisions.clear();
        //if(parallelProcessing) test.parallelStream().forEach((c) -> testForCollision(c, system));
         for(ColliderGroup next : test) testForCollision(next, system);
    }

    public static void processCollisionResponse(Collision col){
        PhysicsEntity e1 = col.thiscollider.parent;
        PhysicsEntity e2 = col.other.parent;
        if(e1 != null && e2 != null){
            ArrayList<Vector3f> jfs = new ArrayList<>();
            ArrayList<Vector3f> invjfs = new ArrayList<>();
            ArrayList<Vector3f> jrs = new ArrayList<>();
            ArrayList<Vector3f> invjrs = new ArrayList<>();
            ArrayList<Vector3f> r1s = new ArrayList<>();
            ArrayList<Vector3f> r2s = new ArrayList<>();
            ArrayList<Vector3f> norms = new ArrayList<>();
            ArrayList<Vector3f> invnorms = new ArrayList<>();
            ArrayList<Float> depths = new ArrayList<>();

            for(ContactManifold mf : col.manifolds){
                depths.add(mf.depth);
                for(Vector3f point : mf.points){
                    Vector3f R1 = point.subtract(e1.getPosition());
                    Vector3f R2 = point.subtract(e2.getPosition());

                    Vector3f v1 = e1.velocity.add(R1.cross(e1.angvelocity));
                    Vector3f v2 = e2.velocity.add(R2.cross(e2.angvelocity));

                    Vector3f vr = v1.subtract(v2);

                    float jdenom = 1/e1.mass + 1/e2.mass + 
                            (e1.inertialMatrix.inverse().multiply(R1.cross(mf.normal)).cross(R1).add(
                                    e2.inertialMatrix.multiply(R2.cross(mf.normal)).cross(R2)))
                                    .dot(mf.normal);
                    float jnum = (vr.dot(mf.normal)*-(1+(e1.restitution + e2.restitution)/2f));
                    float jr = jnum/jdenom; //reaction force size

                    float jd = (e1.dynamicfriction+e2.dynamicfriction) * 0.5f * jr; //dynamic friction
                    float js = (e1.staticfriction+e2.staticfriction) * 0.5f * jr; //static friction

                    Vector3f t = new Vector3f(); //tangent vector to collision
                    if(!FastMath.isZero(vr.dot(mf.normal))){
                        Vector3f td = vr.subtract(mf.normal.multiply(vr.dot(mf.normal)));
                        t = td.normalize();    
                    }
                    Vector3f jf = t.multiply(-jd); //friction force for dynamic collision

                    if(js >= vr.dot(t) * (e1.mass+e2.mass)){
                        jf = t.multiply(vr.dot(t)*(e1.mass+e2.mass)*-1f); //static friction
                    }

                    Vector3f jrv = mf.normal.multiply(jr); //reaction force along normal vector
                    Vector3f jfv = jf;

                    jrs.add(jrv);
                    invjrs.add(jrv.inverse());
                    
                    jfs.add(jfv);
                    invjfs.add(jfv.inverse());
                    
                    r1s.add(R1);
                    r2s.add(R2);
                    
                    norms.add(mf.normal);
                    invnorms.add(mf.normal.inverse());
                }
            }

            e1.R.addAll(r1s);
            e2.R.addAll(r2s);
            
            e1.jr.addAll(jrs);
            e2.jr.addAll(invjrs);
            
            e1.jf.addAll(jfs);
            e2.jf.addAll(invjfs);
            
            e1.norms.addAll(norms);
            e2.norms.addAll(invnorms);

        }else if(e1 == null ^ e2 == null){
            PhysicsEntity e = e1;
            ArrayList<Vector3f> jfs = new ArrayList<>();
            ArrayList<Vector3f> jrs = new ArrayList<>();
            ArrayList<Vector3f> rs = new ArrayList<>();
            ArrayList<Vector3f> norms = new ArrayList<>();
            ArrayList<Float> depths = new ArrayList<>();
            for(ContactManifold mf : col.manifolds){
                depths.add(mf.depth);
                for(Vector3f point : mf.points){
                    Vector3f R = point.subtract(e.getPosition());
                    Vector3f v = e.velocity.add(R.cross(e.angvelocity));

                    float jdenom = 1/e.mass + (e.inertialMatrix.inverse().multiply(
                            R.cross(mf.normal)).cross(R)).dot(mf.normal);
                    float jnum = v.dot(mf.normal) * -(1 + e.restitution);
                    float jr = jnum/jdenom;

                    float jd = e.dynamicfriction * jr;
                    float js = e.staticfriction * jr;
                    Vector3f t = new Vector3f();
                    if(!FastMath.isZero(v.dot(mf.normal))){
                        Vector3f td = v.subtract(mf.normal.multiply(v.dot(mf.normal)));
                        t = td.normalize();   
                        if(FastMath.isZero(td.length())) t = new Vector3f();
                    }
                    Vector3f jf = t.multiply(-jd);

                    if(js >= v.dot(t)*e.mass || FastMath.isEqual(v.dot(t), 0)){
                        jf = t.multiply(v.dot(t)*e.mass*-1f);
                    }

                    Vector3f jrv = mf.normal.multiply(jr);
                    Vector3f jfv = mf.normal.multiply(jf.length());

                    jrs.add(jrv);
                    jfs.add(jfv);
                    rs.add(R);
                    norms.add(mf.normal);
                }
            }

            e.R.addAll(rs);
            e.jf.addAll(jfs);
            e.jr.addAll(jrs);
            e.norms.addAll(norms);
            e.depths.addAll(depths);
        }
    }
    
    public static void processCollisions(){ 
        if(parallelProcessing) collisions.parallelStream().forEach((c) -> { processCollisionResponse(c); });
        else for(Collision c : collisions) processCollisionResponse(c);
        
        for(ColliderGroup next : test){
            PhysicsEntity e = next.parent;
            
            Vector3f R = Vector3f.averageOf(e.R.toArray(new Vector3f[0]));
            Vector3f jfv = Vector3f.averageOf(e.jf.toArray(new Vector3f[0]));
            Vector3f jrv = Vector3f.averageOf(e.jr.toArray(new Vector3f[0]));
            Vector3f normal = Vector3f.averageOf(e.norms.toArray(new Vector3f[0])).normalize();
            float depth = e.depths.stream().max((i,j)->{
                return i > j ? 1 : 0;
            }).orElse(0f);
            if(e.norms.isEmpty())continue;
            
            e.setPosition(e.getPosition().add(normal.multiply(depth)));

            e.angvelocity = e.angvelocity.add(e.inertialMatrix.inverse().multiply(R.cross(normal)).multiply(jrv.length()).divide(e.mass));
            e.velocity = e.velocity.add(jrv.add(jfv).divide(e.mass));
            e.lowestContact = normal;
            
            e.R.clear();
            e.jf.clear();
            e.jr.clear();
            e.norms.clear();
            e.depths.clear();
        }
    }
}
