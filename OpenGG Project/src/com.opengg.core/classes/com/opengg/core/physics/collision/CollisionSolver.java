/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.engine.PhysicsEngine;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.MinkowskiSet;
import com.opengg.core.math.MinkowskiTriangle;
import com.opengg.core.math.Simplex;
import com.opengg.core.math.Triangle;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class CollisionSolver {

    public static boolean AABBAABB(AABB c1, AABB c2){
        return c1.isColliding(c2);
    }
    
    public static boolean AABBRay(AABB c1, Ray c2){
        return c2.isColliding(c1);
    }
    
    public static ContactManifold SphereSphere(SphereCollider c1, SphereCollider c2){
        if(c1.getPosition().getDistance(c2.getPosition()) < c1.radius + c2.radius){
            ContactManifold data = new ContactManifold();
            data.normal = c1.getPosition().subtract(c2.getPosition());
            data.points.add(c1.getPosition().add(data.normal.divide(c1.radius/c2.radius)));
            data.normal = data.normal.normalize();
            data.depth = c2.getPosition().subtract(c1.getPosition()).length()-c1.radius-c2.radius;
            return data;
        }
        return null;
    }
    
    public static ContactManifold SphereCapsule(SphereCollider c1, CapsuleCollider c2){
        Vector3f closest = FastMath.closestPointTo(c2.getP1(), c2.getP2(), c1.getPosition(), true);  
        if(c1.getPosition().getDistance(closest) < c1.radius + c2.radius){
            ContactManifold data = new ContactManifold();
            data.normal = c1.getPosition().subtract(closest);
            data.points.add(c1.getPosition().add(data.normal.divide(c1.radius/c2.radius)));
            data.normal = data.normal.normalize();
            data.depth = closest.subtract(c1.getPosition()).length()-c1.radius-c2.radius;
            return data;
        }
        return null;
    }
    
    public static boolean SphereRay(SphereCollider c1, Ray c2){
        Vector3f closest = FastMath.closestPointTo(c2.pos, c2.dir, c1.getPosition(), false);
        return c1.getPosition().getDistance(closest) < c1.radius;
    }
    
    public static ContactManifold SphereGround(SphereCollider c1){
        float ground = PhysicsEngine.getInstance().getConstants().BASE;
        if(c1.getPosition().y() - c1.getRadius() > ground) return null;
        
        ContactManifold data = new ContactManifold();
        data.depth = ground - (c1.getPosition().y() - c1.getRadius());
        data.normal = new Vector3f(0,1,0);
        data.points.add(new Vector3f(c1.getPosition().x(), ground, c1.getPosition().z()));
        return data;
    }
    
    public static ContactManifold SphereTerrain(SphereCollider c1, TerrainCollider c2){
        Vector3f np = c1.getPosition().subtract(c2.getPosition()).divide(c2.getScale());
        float height = c2.t.getHeight(np.x(), np.z());
        if(height == 12345)
            return null;
        height += c2.getPosition().y();
        height *= c2.getScale().y();
        if(!(c1.getPosition().y()-c1.radius < height))
            return null;
        ContactManifold data = new ContactManifold();
        data.points.add(new Vector3f(c1.getPosition().x(), height, c1.getPosition().z()));
        data.normal = c2.t.getNormalAt(np.x(), np.z());
        data.depth = height-(c1.getPosition().y()-c1.radius);
        return data;
    }
    
    public static ContactManifold CapsuleCapsule(CapsuleCollider c1, CapsuleCollider c2){
        Vector3f[] closest = FastMath.closestApproach(c1.getP1(), c1.getP2(), c2.getP1(), c2.getP2(), true, true);
        if(closest[0].getDistance(closest[1]) < c1.radius + c2.radius){
            ContactManifold data = new ContactManifold();
            data.normal = closest[0].subtract(closest[1]);
            data.points.add(closest[0].add(data.normal.divide(c1.radius/c2.radius)));
            data.normal = data.normal.normalize();
            data.depth = closest[1].subtract(closest[0]).length()-c1.radius-c2.radius;
            return data;
        }
        return null;
    }
    
    public static boolean CapsuleRay(CapsuleCollider c1, Ray c2){
        Vector3f[] closest = FastMath.closestApproach(c1.getP1(), c1.getP2(), c2.pos, c2.dir, true, false);
        return closest[0].getDistance(closest[1]) < c1.radius;
    }
     
    public static ContactManifold CylinderTerrain(CapsuleCollider c1, TerrainCollider c2){
        Vector3f np = c1.getPosition().subtract(c2.getPosition()).divide(c2.getScale());
        float height = c2.t.getHeight(np.x(), np.z());
        if(height == 12345)
            return null;
        height += c2.getPosition().y();
        height *= c2.getScale().y();
        if(!(c1.getPosition().y()-c1.radius < height))
            return null;
        ContactManifold data = new ContactManifold();
        data.points.add(new Vector3f(c1.getPosition().x(), height, c1.getPosition().z()));
        data.normal = c2.t.getNormalAt(np.x(), np.z());
        data.depth = height-c1.getPosition().y();
        return data;
    }
    
    public static ContactManifold CapsuleGround(CapsuleCollider c1){
        Vector3f lowest;
        if(c1.getP1().y() < c1.getP2().y()) lowest = c1.getP1();
        else lowest = c1.getP2();
        
        float ground = PhysicsEngine.getInstance().getConstants().BASE;
        if(lowest.y() - c1.getRadius() > ground) return null;
        
        ContactManifold data = new ContactManifold();
        data.depth = ground - (lowest.y() - c1.getRadius());
        data.normal = new Vector3f(0,1,0);
        data.points.add(new Vector3f(lowest.x(), ground, lowest.z()));
        return data;
    }
    
    public static ContactManifold HullHull(ConvexHull h1, ConvexHull h2){
        Matrix4f h1matrix = new Matrix4f().translate(h1.getPosition()).rotate(h1.getRotation());
        Matrix4f h2matrix = new Matrix4f().translate(h2.getPosition()).rotate(h2.getRotation());
        List<MinkowskiSet> msum = FastMath.minkowskiDifference(h1.vertices, h2.vertices, h1matrix, h2matrix);        
        Simplex s = FastMath.runGJK(msum);
        
        if(s == null) return null;
        
        ContactManifold cm = new ContactManifold();
        MinkowskiTriangle contact = FastMath.runEPA(s, msum);
        
        if(contact == null) return null;
        
        float distanceFromOrigin = contact.n.dot(contact.a.v);

        Vector3f bary = barycentric(contact.n.multiply(distanceFromOrigin), contact.a.v, contact.b.v, contact.c.v);

        if (Math.abs(bary.x()) > 1.0f || Math.abs(bary.y()) > 1.0f || Math.abs(bary.z()) > 1.0f) 
            return null;
        
        cm.points.add(contact.a.a.multiply(bary.x()).add(contact.b.a.multiply(bary.y())).add(contact.c.a.multiply(bary.z())));
        cm.normal = contact.n.inverse();
        cm.depth = distanceFromOrigin;
        return cm;
    }
    
    public static ContactManifold HullGround(ConvexHull h1){
        Matrix4f h1matrix = new Matrix4f().translate(h1.getPosition()).rotate(h1.getRotation());
        List<Vector3f> nlist = new ArrayList<>(h1.vertices.size());
        for(Vector3f v : h1.vertices){
            nlist.add(new Vector4f(v).multiply(h1matrix).truncate());
        }
        
        float lowest = Float.MAX_VALUE;
        List<Vector3f> lowestpoints = new LinkedList<>();
        for(Vector3f v : nlist){
            if(FastMath.isEqual(v.y(), lowest, FastMath.FLOAT_ROUNDING_ERROR*10)){
                lowestpoints.add(v);
            }else if(v.y() < lowest-FastMath.FLOAT_ROUNDING_ERROR){
                lowestpoints.clear();
                lowest = v.y();
                lowestpoints.add(v);
            }
        }
        
        float ground = PhysicsEngine.getInstance().getConstants().BASE;
        if(lowest > ground) return null;
        
        ContactManifold data = new ContactManifold();
        data.depth = ground - lowest;
        data.normal = new Vector3f(0,1,0);
        for(Vector3f low : lowestpoints){
            data.points.add(new Vector3f(low.x(), ground, low.z()));
        }
        return data;
    }
    
    public static Vector3f barycentric(Vector3f p,Vector3f a, Vector3f b, Vector3f c) {
        Vector3f v0 = b.subtract(a), v1 = c.subtract(a), v2 = p.subtract(a);
        float d00 = v0.dot(v0);
        float d01 = v0.dot(v1);
        float d11 = v1.dot(v1);
        float d20 = v2.dot(v0);
        float d21 = v2.dot(v1);
        float denom = d00 * d11 - d01 * d01;
        float bx = (d11 * d20 - d01 * d21) / denom;
        float by = (d00 * d21 - d01 * d20) / denom;
        return new Vector3f(bx, by, 1.0f - bx-by);
    }
    
    public static ContactManifold MeshMesh(Mesh m1, Mesh m2){
        Matrix4f m1matrix = new Matrix4f().translate(m1.getPosition()).rotate(m1.getRotation());
        Matrix4f m2matrix = new Matrix4f().translate(m2.getPosition()).rotate(m2.getRotation());
        List<Tuple<Triangle, Triangle>> collisions = new ArrayList<>(100);
        List<Triangle> f1 = m1.getFaces();
        List<Triangle> f2 = m2.getFaces();
        for(int i = 0; i < f1.size(); i++){
            for(int j = i; j < f2.size(); j++){
                Triangle t1 = new Triangle(f1.get(i)).transform(m1matrix);
                Triangle t2 = new Triangle(f2.get(j)).transform(m2matrix);
                if(FastMath.isIntersecting(t1, t2)){
                    collisions.add(new Tuple<>(t1,t2));
                }
            }
        }
        if(collisions.isEmpty()) return null;
        
        
        List<Vector3f> collisionPoints = new ArrayList(collisions.size());
        for(Tuple<Triangle, Triangle> set : collisions){
            Vector3f v = set.y.a.subtract(set.x.a);
            float dist = v.dot(set.x.n);
            Vector3f p1 = set.y.a.subtract(set.x.n.multiply(dist));
            
            v = set.y.b.subtract(set.x.a);
            dist = v.dot(set.x.n);
            Vector3f p2 = set.y.b.subtract(set.x.n.multiply(dist));
            
            v = set.y.c.subtract(set.x.a);
            dist = v.dot(set.x.n);
            Vector3f p3 = set.y.c.subtract(set.x.n.multiply(dist));
            
            collisionPoints.add(Vector3f.averageOf(p1,p2,p3));
        }
        
        float maxdist = 0;
        
        ContactManifold cm = new ContactManifold();
        return cm;
    }
}
