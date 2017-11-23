/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.MinkowskiSet;
import com.opengg.core.math.MinkowskiTriangle;
import com.opengg.core.math.Simplex;
import com.opengg.core.math.Vector3f;
import java.util.List;

/**
 *
 * @author Javier
 */
public class CollisionSolver {
    public static float COLLISION_OFFSET = 0.5F;

    
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
            data.point = c1.getPosition().add(data.normal.divide(c1.radius/c2.radius));
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
            data.point = c1.getPosition().add(data.normal.divide(c1.radius/c2.radius));
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
    
    public static ContactManifold CapsuleCapsule(CapsuleCollider c1, CapsuleCollider c2){
        Vector3f[] closest = FastMath.closestApproach(c1.getP1(), c1.getP2(), c2.getP1(), c2.getP2(), true, true);
        if(closest[0].getDistance(closest[1]) < c1.radius + c2.radius){
            ContactManifold data = new ContactManifold();
            data.normal = closest[0].subtract(closest[1]);
            data.point = closest[0].add(data.normal.divide(c1.radius/c2.radius));
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
        data.point = new Vector3f(c1.getPosition().x(), height, c1.getPosition().z());
        data.normal = c2.t.getNormalAt(np.x(), np.z());
        data.depth = height-(c1.getPosition().y()-c1.radius);
        return data;
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
        data.point = new Vector3f(c1.getPosition().x(), height, c1.getPosition().z());
        data.normal = c2.t.getNormalAt(np.x(), np.z());
        data.depth = height-c1.getPosition().y();
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
        
//        Vector4f transa = h1matrix.transform(new Vector4f(contact.a.a));
//        Vector3f na = new Vector3f(transa.x, transa.y, transa.z);
//        Vector4f transb = h1matrix.transform(new Vector4f(contact.b.a));
//        Vector3f nb = new Vector3f(transb.x, transb.y, transb.z);
//        Vector4f transc = h1matrix.transform(new Vector4f(contact.c.a));
//        Vector3f nc = new Vector3f(transc.x, transc.y, transc.z);
//        
//        cm.point = new Vector3f((na).multiply(bary.x).add((nb).multiply(bary.y)).add((nc).multiply(bary.z)));

        System.out.println(bary);
        
        cm.point = contact.a.a.multiply(bary.x()).add(contact.b.a.multiply(bary.y())).add(contact.c.a.multiply(bary.z()));
        
        cm.normal = contact.n;
        cm.depth = distanceFromOrigin;
        System.out.println(cm);
        return cm;
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
}
