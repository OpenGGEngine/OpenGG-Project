/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.physics.PhysicsEngine;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.geom.MinkowskiSet;
import com.opengg.core.math.geom.MinkowskiTriangle;
import com.opengg.core.math.Simplex;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    
    public static List<ContactManifold> SphereSphere(SphereCollider c1, SphereCollider c2){
        if(c1.getPosition().distanceTo(c2.getPosition()) < c1.radius + c2.radius){
            ContactManifold data = new ContactManifold();
            data.normal = c1.getPosition().subtract(c2.getPosition()).normalize();
            data.points.add(c1.getPosition().subtract(data.normal.multiply(c1.radius)));
            data.depth = Math.abs(c2.getPosition().subtract(c1.getPosition()).length()-c1.radius-c2.radius);
            List.of(data);
        }
        return new ArrayList<>();
    }
    
    public static List<ContactManifold> SphereCapsule(SphereCollider c1, CapsuleCollider c2){
        Vector3f closest = FastMath.closestPointTo(c2.getP1(), c2.getP2(), c1.getPosition(), true);  
        if(c1.getPosition().distanceTo(closest) < c1.radius + c2.radius){
            ContactManifold data = new ContactManifold();
            data.normal = c1.getPosition().subtract(closest);
            data.points.add(c1.getPosition().add(data.normal.divide(c1.radius/c2.radius)));
            data.normal = data.normal.normalize();
            data.depth = closest.subtract(c1.getPosition()).length()-c1.radius-c2.radius;
            List.of(data);
        }
        return new ArrayList<>();
    }
    
    public static boolean SphereRay(SphereCollider c1, Ray c2){
        Vector3f closest = FastMath.closestPointTo(c2.pos, c2.dir, c1.getPosition(), false);
        return c1.getPosition().distanceTo(closest) < c1.radius;
    }
    
    public static List<ContactManifold> SphereGround(SphereCollider c1){
        float ground = PhysicsEngine.getInstance().getConstants().BASE;
        if(c1.getPosition().y - c1.getRadius() > ground) return new ArrayList<>();
        
        ContactManifold data = new ContactManifold();
        data.depth = ground - (c1.getPosition().y - c1.getRadius());
        data.normal = new Vector3f(0,1,0);
        data.points.add(new Vector3f(c1.getPosition().x, ground, c1.getPosition().z));
        return List.of(data);
    }
    
    public static List<ContactManifold> CapsuleCapsule(CapsuleCollider c1, CapsuleCollider c2){
        Vector3f[] closest = FastMath.closestApproach(c1.getP1(), c1.getP2(), c2.getP1(), c2.getP2(), true, true);
        if(closest[0].distanceTo(closest[1]) < c1.radius + c2.radius){
            ContactManifold data = new ContactManifold();
            data.normal = closest[0].subtract(closest[1]);
            data.points.add(closest[0].add(data.normal.divide(c1.radius/c2.radius)));
            data.normal = data.normal.normalize();
            data.depth = closest[1].subtract(closest[0]).length()-c1.radius-c2.radius;
            List.of(data);
        }
        return new ArrayList<>();
    }
    
    public static boolean CapsuleRay(CapsuleCollider c1, Ray c2){
        Vector3f[] closest = FastMath.closestApproach(c1.getP1(), c1.getP2(), c2.pos, c2.dir, true, false);
        return closest[0].distanceTo(closest[1]) < c1.radius;
    }
    
    public static List<ContactManifold> CapsuleGround(CapsuleCollider c1){
        Vector3f lowest;
        if(c1.getP1().y < c1.getP2().y) lowest = c1.getP1();
        else lowest = c1.getP2();
        
        float ground = PhysicsEngine.getInstance().getConstants().BASE;
        if(lowest.y - c1.getRadius() > ground) return new ArrayList<>();
        
        ContactManifold data = new ContactManifold();
        data.depth = ground - (lowest.y - c1.getRadius());
        data.normal = new Vector3f(0,1,0);
        data.points.add(new Vector3f(lowest.x, ground, lowest.z));
        return List.of(data);
    }
    
    public static List<ContactManifold> HullHull(ConvexHull h1, ConvexHull h2){
        var randomness = new Vector3f(Float.MIN_VALUE * (float)((int)(Math.random() * 1000)),
                Float.MIN_VALUE * (float)((int)(Math.random() * 1000)),
                Float.MIN_VALUE * (float)((int)(Math.random() * 1000)));

        Matrix4f h1matrix = new Matrix4f()
                .translate(h1.getPosition())
                        .rotate(h1.getRotation())
                        .scale(h1.getScale());
        Matrix4f h2matrix = new Matrix4f()
                .translate(h2.getPosition())
                .rotate(h2.getRotation())
                .scale(h2.getScale());
        List<MinkowskiSet> msum = FastMath.minkowskiDifference(h1.vertices, h2.vertices, h1matrix, h2matrix);        
        Simplex s = FastMath.runGJK(msum);
        
        if(s == null) return new ArrayList<>();
        System.out.println(s);
        ContactManifold cm = new ContactManifold();
        MinkowskiTriangle contact = FastMath.runEPA(s, msum);
        
        if(contact == null) return new ArrayList<>();

        float distanceFromOrigin = contact.n.dot(contact.a.v);

        Vector3f bary = barycentric(contact.n.multiply(distanceFromOrigin), contact.a.v, contact.b.v, contact.c.v);

        if (Math.abs(bary.x) > 1.0f || Math.abs(bary.y) > 1.0f || Math.abs(bary.z) > 1.0f) 
            return new ArrayList<>();
        
        cm.points.add(contact.a.a.multiply(bary.x).add(contact.b.a.multiply(bary.y)).add(contact.c.a.multiply(bary.z)));
        cm.normal = contact.n.inverse();
        cm.depth = distanceFromOrigin;
        return List.of(cm);
    }
    
    public static List<ContactManifold> HullTerrain(ConvexHull h1, TerrainCollider t2){
        Mesh m = new Mesh(t2.mesh, false);
        m.setParent(t2.parent);
        m.setPosition(t2.getOffset());
        m.setRotation(t2.getRotationOffset());
        m.setScale(t2.getScale());
        m.system = t2.system;
        List<ContactManifold> c = HullMesh(h1, m);
        
        Vector3f d = h1.getPosition().subtract(t2.getPosition()).divide(t2.getScale());

        if(d.x>0 && d.x<1 && d.z>0 && d.z<1){
            if(d.y+0.03f < t2.t.getHeight(d.x, d.z)){
                float lowest = Float.MAX_VALUE;
                for(Vector3f f : h1.vertices){
                    if(lowest > f.multiply(h1.getScale()).add(h1.getPosition()).y) lowest = f.multiply(h1.getScale()).add(h1.getPosition()).y;
                }

                ContactManifold mf = new ContactManifold();
                mf.depth = Math.abs(t2.t.getHeight(d.x, d.z)*t2.getScale().y+t2.getPosition().y - lowest);
                mf.points.add(new Vector3f(h1.getPosition().setY(d.y*t2.getScale().y)));
                mf.normal = new Vector3f(0,1,0);
                c.add(mf);
            }
        }
        
        
        
        return c;
    }
    
    public static List<ContactManifold> HullGround(ConvexHull h1){
        Matrix4f h1matrix = new Matrix4f().translate(h1.getPosition()).rotate(h1.getRotation()).scale(h1.getScale());
        List<Vector3f> nlist = new ArrayList<>(h1.vertices.size());
        for(Vector3f v : h1.vertices){
            nlist.add(new Vector4f(v).multiply(h1matrix).truncate());
        }

        var points = nlist.stream()
                .filter(v -> v.y < PhysicsEngine.getInstance().getConstants().BASE)
                .collect(Collectors.toList());

        if(points.isEmpty()) return new ArrayList<>();
        return List.of(new ContactManifold(new Vector3f(0,1,0), points, (float) points.stream()
                .mapToDouble(v -> PhysicsEngine.getInstance().getConstants().BASE - v.y).min().getAsDouble()));
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

    public static List<ContactManifold> HullMesh(ConvexHull hull, Mesh mesh){
        List<ContactManifold> contacts = new ArrayList<>();
        List<Vector3f> triAsHull = new ArrayList<>(3);
        ConvexHull h2 = new ConvexHull(triAsHull);
        h2.setParent(mesh.parent);
        h2.setPosition(mesh.getOffset());
        h2.setRotation(mesh.getRotationOffset());
        h2.setScale(mesh.getScale());
        h2.system = mesh.system;
        
        for(MeshTriangle f : mesh.getFaces()){
            triAsHull.clear();
            triAsHull.add(f.a);
            triAsHull.add(f.b);
            triAsHull.add(f.c);

            f.aabb.recalculate();
            if(!((ColliderGroup)hull.parent).getBoundingBox().isColliding(f.aabb)) continue;
            contacts.addAll(HullHull(hull,h2));
        }

        Random random = new Random();
        while(contacts.size() > 5){
            contacts.remove(random.nextInt(contacts.size()));
        }

        return contacts;
    }

    public static List<ContactManifold> MeshMesh(Mesh m1, Mesh m2){/*
        Matrix4f m1matrix = new Matrix4f().translate(m1.getPosition()).rotate(m1.getRotation());
        Matrix4f m2matrix = new Matrix4f().translate(m2.getPosition()).rotate(m2.getRotation());
        List<Tuple<Triangle, Triangle>> collisions = new ArrayList<>(100);
        List<MeshTriangle> f1 = m1.getFaces();
        List<MeshTriangle> f2 = m2.getFaces();
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

            v = set.y.in.subtract(set.x.a);
            dist = v.dot(set.x.n);
            Vector3f p2 = set.y.in.subtract(set.x.n.multiply(dist));

            v = set.y.c.subtract(set.x.a);
            dist = v.dot(set.x.n);
            Vector3f p3 = set.y.c.subtract(set.x.n.multiply(dist));

            collisionPoints.add(Vector3f.averageOf(p1,p2,p3));
        }

        float maxdist = 0;

        ContactManifold cm = new ContactManifold();
        return new ContactManifold(cm);*/return null;
    }


    public static List<ContactManifold> MeshGround(Mesh m){
        ConvexHull h2 = new ConvexHull(m.getPoints());
        h2.setParent(m.parent);
        h2.setPosition(m.getOffset());
        h2.setRotation(m.getRotationOffset());
        h2.system = m.system;
        return HullGround(h2);
    }

    private CollisionSolver() {
    }
}
