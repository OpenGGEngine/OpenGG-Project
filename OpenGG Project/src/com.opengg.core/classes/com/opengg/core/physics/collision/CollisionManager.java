/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.UnorderedTuple;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsEntity;
import com.opengg.core.physics.PhysicsSystem;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class CollisionManager {
    private static List<Collision> collisions = new ArrayList<>();
    private static final List<ColliderGroup> test = new LinkedList<>();
    private static final ColliderGroup coll = new ColliderGroup();
    public static boolean parallelProcessing = false;
    public static boolean enableResponse = true;

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
        collisions = test.stream()
                .filter(c -> c != null)
                .flatMap(c -> system.getColliders().stream()
                        .map(c2 ->new UnorderedTuple<>(c,c2)))
                .filter(t -> t.x != t.y)
                .distinct()
                .map(t -> t.x.testForCollision(t.y))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public static void processCollisions(){
        if(parallelProcessing) collisions.parallelStream().forEach((c) -> { processCollision(c); });
        else for(Collision c : collisions) processCollision(c);

        for(ColliderGroup next : test){
            processResponse((PhysicsEntity) next.parent);
        }
    }

    public static void processCollision(Collision col){
        ArrayList<Vector3f> frictions = new ArrayList<>();
        ArrayList<Vector3f> jrs = new ArrayList<>();
        ArrayList<Vector3f> r1s = new ArrayList<>();
        ArrayList<Vector3f> r2s = new ArrayList<>();
        ArrayList<Vector3f> norms = new ArrayList<>();
        ArrayList<Float> depths = new ArrayList<>();

        PhysicsEntity e1 = (PhysicsEntity) col.thiscollider.parent;
        PhysicsEntity e2 = (PhysicsEntity) col.other.parent;
        if(e1 != null && e2 != null){
            var contact = processContact(e1, e2, col.manifold);
            e1.responses.add(contact);
            e2.responses.add(contact.invert());
        }else if(e1 == null ^ e2 == null){
            e1.responses.add(processContact(e1, col.manifold));
        }
    }

    private static Response processContact(PhysicsEntity e1, ContactManifold manifold) {
        var responseManifold = new Response();

        var point = Vector3f.averageOf(manifold.points);

        Vector3f R = point.subtract(e1.getPosition());
        Vector3f v = e1.velocity;//e1.velocity.add(R.cross(e1.angvelocity));

        float jnum = v.dot(manifold.normal) * -(1 + e1.restitution);//v.multiply(-(1 + e1.restitution)).dot(manifold.normal);
        float jdenom = 1/ e1.mass + (e1.inertialMatrix.inverse().multiply(
                R.cross(manifold.normal)).cross(R)).dot(manifold.normal);
        float jr = jnum/jdenom;


        float jd = e1.dynamicfriction * v.dot(manifold.normal) * -1;
        float js = e1.staticfriction * v.dot(manifold.normal) * -1;

        Vector3f tan = new Vector3f();
        if(!v.cross(manifold.normal).rezero().equals(new Vector3f())){
            tan = v.subtract(manifold.normal.multiply(v.dot(manifold.normal))).normalize();
        }
        Vector3f jf = tan.multiply(-jd);

        if(js >= v.dot(tan) * e1.mass){
            jf = tan.multiply(v.dot(tan) * e1.mass * -1f);
        }

        Vector3f jrv = manifold.normal.multiply(jr);

        responseManifold.jr = jrv;
        responseManifold.jf = jf;
        responseManifold.R1 = R;
        responseManifold.normal = manifold.normal;
        responseManifold.depth = manifold.depth;
        responseManifold.ratio = 1;

        return responseManifold;
    }

    private static Response processContact(PhysicsEntity e1, PhysicsEntity e2, ContactManifold manifold) {
        var responseManifold = new Response();

        var point = Vector3f.averageOf(manifold.points);

        Vector3f R1 = point.subtract(e1.getPosition());
        Vector3f R2 = point.subtract(e2.getPosition());

        Vector3f v1 = e1.velocity.add(R1.cross(e1.angvelocity));
        Vector3f v2 = e2.velocity.add(R2.cross(e2.angvelocity));

        Vector3f v = v1.subtract(v2);

        float jnum = v.dot(manifold.normal) * -(1 + (e1.restitution + e2.restitution) / 2f);
        float jdenom = 1 / e1.mass + 1 / e2.mass +
                (e1.inertialMatrix.inverse().multiply(R1.cross(manifold.normal)).cross(R1).add(
                        e2.inertialMatrix.multiply(R2.cross(manifold.normal)).cross(R2)))
                        .dot(manifold.normal);

        float jr = jnum / jdenom; //reaction force size

        float jd = (e1.dynamicfriction + e2.dynamicfriction) * 0.5f * jr; //dynamic friction
        float js = (e1.staticfriction + e2.staticfriction) * 0.5f * jr; //static friction

        Vector3f tan = new Vector3f(); //tangent vector to collision
        if (!v.cross(manifold.normal).rezero().equals(new Vector3f())) {
            tan = v.subtract(manifold.normal.multiply(v.dot(manifold.normal))).normalize();
        }
        Vector3f jf = tan.multiply(-jd); //friction force for dynamic collision

        if (js >= v.dot(tan) * (e1.mass + e2.mass)) {
            jf = tan.multiply(v.dot(tan) * (e1.mass + e2.mass) * -1f); //static friction
        }

        Vector3f jrv = manifold.normal.multiply(jr); //reaction force along normal vector

        responseManifold.jr = jrv;
        responseManifold.jf = jf;
        responseManifold.R1 = R1;
        responseManifold.R2 = R2;
        responseManifold.normal = manifold.normal;
        responseManifold.depth = manifold.depth;
        responseManifold.ratio = e1.velocity.length()/e2.velocity.length();

        return responseManifold;
    }

    private static void processResponse(PhysicsEntity e) {
        if(e.responses.isEmpty()) return;

        Vector3f depthchanges = new Vector3f();

        for(var response : e.responses){
            var R  = response.R1;//Vector3f.averageOf(response.manifolds.stream().map(m -> m.R1).collect(Collectors.toList()));
            var jf = response.jf;//Vector3f.averageOf(response.manifolds.stream().map(m -> m.jf).collect(Collectors.toList()));
            var jr = response.jr;//Vector3f.averageOf(response.manifolds.stream().map(m -> m.jr).collect(Collectors.toList()));

            var normal = response.normal;//Vector3f.averageOf(response.manifolds.stream().map(s -> s.normal).collect(Collectors.toList()));

            var depth = response.depth;

            //e.angvelocity = e.angvelocity.subtract(e.inertialMatrix.inverse().multiply(R.cross(normal)).multiply(jr.length()));
            if(enableResponse)
                e.velocity = e.velocity.add(jr.add(jf).divide(e.mass));

            AABB depthaabb = new AABB(depthchanges, new Vector3f());
            if(!depthaabb.isColliding(normal.multiply(depth*response.ratio))){
                depthchanges = normal.multiply(depth*response.ratio);//depthchanges.add(normal.multiply(response.depth));
            }

            e.grounded = e.velocity.y == 0;

        }

        e.setPosition(e.getPosition().add(depthchanges));
        e.responses.clear();
    }

    private CollisionManager() {
    }

    public static class Response {
        Vector3f jr;
        Vector3f jf;
        Vector3f R1;
        Vector3f R2;
        Vector3f normal;
        float depth;
        float ratio;

        public Response invert(){
            var inverted = new Response();
            inverted.jr = jr.inverse();
            inverted.jf = jf.inverse();
            inverted.normal = normal.inverse();
            inverted.R1 = R2;
            inverted.R2 = R1;
            inverted.ratio = 1-ratio;

            return inverted;
        }
    }
}
