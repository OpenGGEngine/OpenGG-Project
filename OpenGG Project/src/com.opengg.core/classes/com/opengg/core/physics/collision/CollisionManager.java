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
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class CollisionManager {
    private static final List<Collision> collisions = new ArrayList<>();
    private static final List<ColliderGroup> test = new LinkedList<>();
    private static final ColliderGroup coll = new ColliderGroup();
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
        for(Collider c : next.getColliders()){
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
        //test.parallelStream().peek((c) -> testForCollision(c, system)).close();
        for(ColliderGroup next : test) testForCollision(next, system);
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
            col.manifolds.stream()
                    .map(m -> processContact(e1, e2, m))
                    .collect(Collectors.toList());
        }else if(e1 == null ^ e2 == null){
            col.manifolds.stream()
                    .map(m -> processContact(e1, m))
                    .peek(m -> e1.responses.add(m))
                    .collect(Collectors.toList());
        }
    }

    private static Response processContact(PhysicsEntity e1, ContactManifold manifold) {
        Response result = new Response();
        result.depth = manifold.depth;
        for(Vector3f point : manifold.points){
            var responseManifold = new ResponseManifold();

            Vector3f R = point.subtract(e1.getPosition());
            Vector3f v = e1.velocity.add(R.cross(e1.angvelocity));

            float jnum = v.dot(manifold.normal) * -(1 + e1.restitution);
            float jdenom = 1/ e1.mass + (e1.inertialMatrix.inverse().multiply(
                    R.cross(manifold.normal)).cross(R)).dot(manifold.normal);
            float jr = jnum/jdenom;

            float jd = e1.dynamicfriction * jr;
            float js = e1.staticfriction * jr;
            Vector3f tan = new Vector3f();
            if(!v.cross(manifold.normal).rezero().equals(new Vector3f())){
                tan = v.subtract(manifold.normal.multiply(v.dot(manifold.normal))).normalize();
            }
            Vector3f jf = tan.multiply(-jd);

            if(js >= v.dot(tan) * e1.mass ){
                jf = tan.multiply(v.dot(tan)* e1.mass * -1f);
            }

            Vector3f jrv = manifold.normal.multiply(jr);

            responseManifold.jr = jrv;
            responseManifold.jf = jf;
            responseManifold.R1 = R;
            responseManifold.normal = manifold.normal;

            result.manifolds.add(responseManifold);
        }

        return result;
    }

    private static Response processContact(PhysicsEntity e1, PhysicsEntity e2, ContactManifold manifold) {
        Response result = new Response();
        result.depth = manifold.depth;
        for(Vector3f point : manifold.points){
            var responseManifold = new ResponseManifold();


            Vector3f R1 = point.subtract(e1.getPosition());
            Vector3f R2 = point.subtract(e2.getPosition());

            Vector3f v1 = e1.velocity.add(R1.cross(e1.angvelocity));
            Vector3f v2 = e2.velocity.add(R2.cross(e2.angvelocity));

            Vector3f v = v1.subtract(v2);

            float jnum = (v.dot(manifold.normal)*-(1+(e1.restitution + e2.restitution)/2f));
            float jdenom = 1/e1.mass + 1/e2.mass +
                    (e1.inertialMatrix.inverse().multiply(R1.cross(manifold.normal)).cross(R1).add(
                            e2.inertialMatrix.multiply(R2.cross(manifold.normal)).cross(R2)))
                            .dot(manifold.normal);

            float jr = jnum/jdenom; //reaction force size

            float jd = (e1.dynamicfriction+e2.dynamicfriction) * 0.5f * jr; //dynamic friction
            float js = (e1.staticfriction+e2.staticfriction) * 0.5f * jr; //static friction

            Vector3f tan = new Vector3f(); //tangent vector to collision
            if(!v.cross(manifold.normal).rezero().equals(new Vector3f())){
                tan = v.subtract(manifold.normal.multiply(v.dot(manifold.normal))).normalize();
            }
            Vector3f jf = tan.multiply(-jd); //friction force for dynamic collision

            if(js >= v.dot(tan) * (e1.mass+e2.mass)){
                jf = tan.multiply(v.dot(tan)*(e1.mass+e2.mass)*-1f); //static friction
            }

            Vector3f jrv = manifold.normal.multiply(jr); //reaction force along normal vector

            responseManifold.jr = jrv;
            responseManifold.jf = jf;
            responseManifold.R1 = R1;
            responseManifold.R2 = R2;
            responseManifold.normal = manifold.normal;

            result.manifolds.add(responseManifold);
        }

        return result;
    }

    private static void processResponse(PhysicsEntity e) {
        if(e.responses.isEmpty()) return;
        Vector3f depthchanges = new Vector3f();

        for(var response : e.responses){
            for(var manifold : response.manifolds){
                var R = manifold.R1;
                var jfv = manifold.jf.divide(response.manifolds.size());
                var jrv = manifold.jr.divide(response.manifolds.size());
                var normal = manifold.normal;
                e.angvelocity = e.angvelocity.subtract(e.inertialMatrix.inverse().multiply(R.cross(normal)).multiply(jrv.length()));
                //e.velocity = e.velocity.add(jrv.add(jfv).divide(e.mass));
            }

            var normal = Vector3f.averageOf(response.manifolds.stream()
                    .map(s -> s.normal)
                    .collect(Collectors.toList()));

            AABB depthaabb = new AABB(depthchanges, new Vector3f());
            if(!depthaabb.isColliding(normal.multiply(response.depth))){
                depthchanges = depthchanges.add(normal.multiply(response.depth));
            }
        }

        e.setPosition(e.getPosition().add(depthchanges));
        e.responses.clear();
    }

    private CollisionManager() {
    }

    public static class Response {
        public List<ResponseManifold> manifolds = new ArrayList<>();
        float depth;

        public Response() {}

        public Response(float depth) {

            this.depth = depth;
        }

        public Response invert(){
            var inverted = new Response();

            inverted.manifolds = manifolds.stream().map(ResponseManifold::invert).collect(Collectors.toList());
            inverted.depth = depth;

            return inverted;
        }
    }

    public static class ResponseManifold{
        Vector3f jr;
        Vector3f jf;
        Vector3f R1;
        Vector3f R2;
        Vector3f normal;

        public ResponseManifold invert(){
            var inverted = new ResponseManifold();
            inverted.jr = jr.inverse();
            inverted.jf = jf.inverse();
            inverted.normal = normal.inverse();
            inverted.R1 = R2;
            inverted.R2 = R1;

            return inverted;
        }
    }
}
