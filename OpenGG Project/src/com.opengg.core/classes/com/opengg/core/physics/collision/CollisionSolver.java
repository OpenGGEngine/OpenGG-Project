/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.*;
import com.opengg.core.math.geom.CollisionMath;
import com.opengg.core.physics.PhysicsEngine;
import com.opengg.core.physics.collision.colliders.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class CollisionSolver {

    public static boolean AABBAABB(AABB c1, AABB c2){
        return c1.isColliding(c2);
    }
    
    public static boolean AABBRay(AABB c1, PhysicsRay c2){
        return c2.isColliding(c1);
    }
    
    public static Optional<ContactManifold> SphereSphere(SphereCollider c1, SphereCollider c2){
        if(c1.getPosition().distanceTo(c2.getPosition()) < c1.getRadius() + c2.getRadius()){
            Contact data = new Contact();
            data.normal = c1.getPosition().subtract(c2.getPosition()).normalize();
            data.offset1 = data.normal.multiply(c1.getRadius()).subtract(c1.getPosition());
            data.offset2 = data.normal.multiply(c1.getRadius()).subtract(c2.getPosition());
            data.depth = Math.abs(c2.getPosition().subtract(c1.getPosition()).length()-c1.getRadius()-c2.getRadius());
            return Optional.of(new ContactManifold(data));
        }
        return Optional.empty();
    }

    public static Optional<ContactManifold> SphereCapsule(SphereCollider c1, CapsuleCollider c2){
        Vector3f closest = FastMath.closestPointTo(c2.getP1(), c2.getP2(), c1.getPosition(), true);
        if(c1.getPosition().distanceTo(closest) < c1.getRadius() + c2.getRadius()){
            Contact data = new Contact();
            data.normal = c1.getPosition().subtract(closest);
            data.offset1 = c1.getPosition().add(data.normal.divide(c1.getRadius()/c2.getRadius()));
            data.normal = data.normal.normalize();
            data.depth = closest.subtract(c1.getPosition()).length()-c1.getRadius()-c2.getRadius();
            return Optional.of(new ContactManifold(data));
        }
        return Optional.empty();
    }

    public static boolean SphereRay(SphereCollider c1, PhysicsRay c2){
        Vector3f closest = FastMath.closestPointTo(c2.pos, c2.dir, c1.getPosition(), false);
        return c1.getPosition().distanceTo(closest) < c1.getRadius();
    }

    public static Optional<ContactManifold> CapsuleCapsule(CapsuleCollider c1, CapsuleCollider c2){
        Vector3f[] closest = FastMath.closestApproach(c1.getP1(), c1.getP2(), c2.getP1(), c2.getP2(), true, true);
        if(closest[0].distanceTo(closest[1]) < c1.getRadius() + c2.getRadius()){
            Contact data = new Contact();
            data.normal = closest[0].subtract(closest[1]);
            data.offset1 =closest[0].add(data.normal.divide(c1.getRadius()/c2.getRadius()));
            data.normal = data.normal.normalize();
            data.depth = closest[1].subtract(closest[0]).length()-c1.getRadius()-c2.getRadius();
            return Optional.of(new ContactManifold(data));
        }
        return Optional.empty();
    }

    public static boolean CapsuleRay(CapsuleCollider c1, PhysicsRay c2){
        Vector3f[] closest = FastMath.closestApproach(c1.getP1(), c1.getP2(), c2.pos, c2.dir, true, false);
        return closest[0].distanceTo(closest[1]) < c1.getRadius();
    }

    public static Optional<ContactManifold> CapsuleGround(CapsuleCollider c1){
        Vector3f lowest;
        if(c1.getP1().y < c1.getP2().y) lowest = c1.getP1();
        else lowest = c1.getP2();

        float ground = PhysicsEngine.getInstance().getConstants().BASE;
        if(lowest.y - c1.getRadius() > ground) return Optional.empty();

        Contact data = new Contact();
        data.depth = ground - (lowest.y - c1.getRadius());
        data.normal = new Vector3f(0,1,0);
        data.offset1 =new Vector3f(lowest.x, ground, lowest.z);
        return Optional.of(new ContactManifold(data));
    }

    public static Optional<ContactManifold> HullSphere(ConvexHull h1, SphereCollider h2){
        Matrix4f h1matrix = new Matrix4f()
                .translate(h1.getPosition())
                .rotate(h1.getRotation())
                .scale(h1.getScale());

        var v1 = h1.vertices.stream()
                .map(v -> h1matrix.transform(new Vector4f(v)).truncate())
                .collect(Collectors.toList());

        var v2 = List.of(h2.getPosition());

        var result = CollisionMath.runGJK(v1, v2);
        if(!result.simplex.contact){
            var points = CollisionMath.getClosestPoints(result.simplex);

            if(points.x.distanceTo(points.y) < h2.getRadius()) {
                var contact = new Contact();
                contact.offset1 = CollisionMath.getClosestPoints(result.simplex).x.subtract(h1.getPosition());
                contact.offset2 = CollisionMath.getClosestPoints(result.simplex).x.subtract(h2.getPosition());
                contact.normal = points.x.subtract(points.y).normalize();
                contact.depth = Math.abs(points.x.distanceTo(points.y) - h2.getRadius());
                return Optional.of(new ContactManifold(contact));
            }else{
                return Optional.empty();
            }
        }else{
            //Deep collision
            return processEPASimplex(result.simplex, h1.getPosition(), h2.getPosition(), v1, v2);
        }
    }

    public static Optional<ContactManifold> HullHull(ConvexHull h1, ConvexHull h2){
        Matrix4f h1matrix = new Matrix4f()
                .translate(h1.getPosition())
                .rotate(h1.getRotation())
                .scale(h1.getScale());

        Matrix4f h2matrix = new Matrix4f()
                .translate(h2.getPosition())
                .rotate(h2.getRotation())
                .scale(h2.getScale());

        var v1 = h1.vertices.stream()
                .map(v -> h1matrix.transform(new Vector4f(v)).truncate())
                .collect(Collectors.toList());

        var v2 = h2.vertices.stream()
                .map(v -> h2matrix.transform(new Vector4f(v)).truncate())
                .collect(Collectors.toList());

        var result = CollisionMath.runGJK(v1, v2);
        if(!result.simplex.contact) return Optional.empty();
        return processEPASimplex(result.simplex, h1.getPosition(), h2.getPosition(), v1, v2);
    }

    private static Optional<ContactManifold> processEPASimplex(Simplex simplex, Vector3f pos1, Vector3f pos2, List<Vector3f> v1, List<Vector3f> v2){
        var epaResult = CollisionMath.runEPA(v1, v2, simplex);
        if(epaResult.isEmpty()) return Optional.empty();

        var contactTriangle = epaResult.get().contact;
        float distanceFromOrigin = contactTriangle.n.dot(contactTriangle.a.vec);

        Vector3f bary = CollisionMath.barycentric(contactTriangle.n.multiply(distanceFromOrigin), contactTriangle.a.vec, contactTriangle.b.vec, contactTriangle.c.vec);
        if (Math.abs(bary.x) > 1.0f || Math.abs(bary.y) > 1.0f || Math.abs(bary.z) > 1.0f)
            return Optional.empty();

        var contact = new Contact();
        contact.offset1 = CollisionMath.getClosestPoints(simplex).x.subtract(pos1);//contactTriangle.a.a.multiply(bary.x).add(contactTriangle.b.a.multiply(bary.y)).add(contactTriangle.c.a.multiply(bary.z));
        contact.offset2 = CollisionMath.getClosestPoints(simplex).x.subtract(pos2);//contactTriangle.a.a.multiply(bary.x).add(contactTriangle.b.a.multiply(bary.y)).add(contactTriangle.c.a.multiply(bary.z));
        contact.normal = contactTriangle.n.inverse();
        contact.depth = distanceFromOrigin;
        /*System.out.println();
        System.out.println(contact.position);
        System.out.println(CollisionMath.getClosestPoints(simplex).x);*/
        return Optional.of(new ContactManifold(contact));
    }

    public static Optional<ContactManifold> HullTerrain(ConvexHull h1, TerrainCollider t2){
       /* Mesh m = new Mesh(t2.mesh, false);
        m.setParent(t2.parent);
        m.setPosition(t2.getOffset());
        m.setRotation(t2.getRotationOffset());
        m.setScale(t2.getScale());
        m.system = t2.system;
        var c = HullMesh(h1, m);

        Vector3f d = h1.getPosition().subtract(t2.getPosition()).divide(t2.getScale());

        if(d.x>0 && d.x<1 && d.z>0 && d.z<1){
            if(d.y+0.03f < t2.terrain.getHeight(d.x, d.z)){
                float lowest = Float.MAX_VALUE;
                for(Vector3f f : h1.vertices){
                    if(lowest > f.multiply(h1.getScale()).add(h1.getPosition()).y) lowest = f.multiply(h1.getScale()).add(h1.getPosition()).y;
                }

                Contact mf = new Contact();
                mf.depth = Math.abs(t2.terrain.getHeight(d.x, d.z)*t2.getScale().y+t2.getPosition().y - lowest);
                mf.position =new Vector3f(h1.getPosition().setY(d.y*t2.getScale().y));
                mf.normal = new Vector3f(0,1,0);
                c = Optional.of(new ContactManifold(mf));
            }
        }
*/
        return null;
    }

    public static Optional<ContactManifold> HullGround(ConvexHull h1){
        Matrix4f h1matrix = new Matrix4f().translate(h1.getPosition()).rotate(h1.getRotation()).scale(h1.getScale());

        var points = h1.vertices.stream()
                .map(Vector4f::new)
                .map(v -> v.multiply(h1matrix))
                .map(Vector4f::truncate)
                .filter(v -> v.y < PhysicsEngine.getInstance().getConstants().BASE)
                .collect(Collectors.toList());

        if(points.isEmpty()) return Optional.empty();
        return Optional.of(new ContactManifold(points.stream()
                            .map(p -> new Contact(p.subtract(h1.getPosition()), p, new Vector3f(0,1,0), h1.getSystem().getConstants().BASE-p.y))
                            .collect(Collectors.toList())));
    }

    public static Optional<ContactManifold> HullMesh(ConvexHull hull, Mesh mesh){
        /*List<ContactManifold> contacts = new ArrayList<>();
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
            if(!((RigidBody)hull.parent).getBoundingBox().isColliding(f.aabb)) continue;
            contacts.add(HullHull(hull,h2).get());
        }
*/
        return null;// Optional.of(ContactManifold.combineManifolds(contacts));
    }

    private CollisionSolver() {
    }
}
