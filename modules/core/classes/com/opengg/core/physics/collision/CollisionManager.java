/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.util.Tuple;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsProvider;
import com.opengg.core.physics.PhysicsSystem;
import com.opengg.core.physics.RigidBody;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class CollisionManager {
    private static final Map<Tuple<RigidBody, RigidBody>, Collision> contactCache = new HashMap<>();
    private static final List<RigidBody> test = new LinkedList<>();
    public static boolean parallelProcessing = false;
    public static boolean enableResponse = true;

    public static void clearCollisions(){
        test.clear();
    }

    public static void addToTest(RigidBody c){
        test.add(c);
    }

    public static void addToTest(List<RigidBody> c){
        test.addAll(c);
    }

    public static void runCollisionStep(PhysicsSystem system){
        var newCollisions = test.stream()
                .filter(Objects::nonNull)
                .flatMap(c -> test.stream()
                        .map(c2 -> Tuple.ofUnordered(c,c2)))
                .filter(t -> t.x() != t.y())
                .distinct()
                .filter(t -> t.x().hasPhysicsProvider() || t.y().hasPhysicsProvider())
                .map(t -> Tuple.of(t, t.x().checkForCollision(t.y())))
                .filter(t -> t.y().isPresent())
                .map(t -> new Collision(t.x().x(), t.x().y(), t.y().get()))
                .collect(Collectors.toList());

        var cacheHits = newCollisions.stream()
                .filter(collision -> contactCache.containsKey(Tuple.ofUnordered(collision.thiscollider,collision.other)))
                .map(collision -> Tuple.of(collision, contactCache.get(Tuple.ofUnordered(collision.thiscollider,collision.other))))
                .collect(Collectors.toList());

        var mergedFromHits = cacheHits.stream()
                .map(pair -> {
                    if(pair.x().thiscollider == pair.y().other){
                        return Tuple.of(pair.x(), Collision.reverse(pair.y()));
                    }else{
                        return pair;
                    }
                }).map(pair -> new Collision(pair.x().thiscollider, pair.x().other, ContactManifold.combineManifolds(List.of(pair.x().manifold, pair.y().manifold)))).collect(Collectors.toList());

        var cacheMisses = newCollisions.stream()
                .filter(c -> !contactCache.containsKey(Tuple.ofUnordered(c.thiscollider,c.other)))
                .collect(Collectors.toList());

        contactCache.clear();
        contactCache.putAll(mergedFromHits.stream()
                .collect(Collectors.toMap(c -> Tuple.ofUnordered(c.thiscollider,c.other), c -> c)));

        contactCache.putAll(cacheMisses.stream()
                .collect(Collectors.toMap(c -> Tuple.ofUnordered(c.thiscollider,c.other), c -> c)));


        var collisions = contactCache.values();

        if(parallelProcessing) collisions.parallelStream().forEach(CollisionManager::processCollision);
        else for(Collision c : collisions) processCollision(c);

        for(RigidBody next : test){
            if(next.hasPhysicsProvider())
                processResponse(next.getPhysicsProvider().get());
        }
    }

    public static void processCollision(Collision col) {
        var contacts = col.manifold.contactsInUse.stream().map(c -> processContact(col.thiscollider, col.other, c)).collect(Collectors.toList());
        col.thiscollider.getPhysicsProvider().ifPresent(c -> c.responses.add(new ResponseSet(contacts)));
        col.other.getPhysicsProvider().ifPresent(c -> c.responses.add(new ResponseSet(contacts.stream().map(Response::invert).collect(Collectors.toList()))));
    }

    private static Response processContact(RigidBody e1, RigidBody e2, Contact manifold) {
        var responseManifold = new Response();

        Vector3f R1 = manifold.offset1;
        Vector3f R2 = manifold.offset2;

        Vector3f v1 = e1.getPhysicsProvider().map(e -> e.velocity.add(R1.cross(e.angvelocity))).orElse(new Vector3f());
        Vector3f v2 = e2.getPhysicsProvider().map(e -> e.velocity.add(R2.cross(e.angvelocity))).orElse(new Vector3f());

        float m1 = e1.getPhysicsProvider().map(e -> e.mass).orElse(0f);
        float m2 = e2.getPhysicsProvider().map(e -> e.mass).orElse(0f);

        Vector3f v = v1.subtract(v2);

        float jnum = v.dot(manifold.normal) * -(1 + (e1.restitution + e2.restitution) / 2f);
        float jdenom = (m1 == 0 ? 0 : 1/m1) +
                       (m2 == 0 ? 0 : 1/m2) +
                (e1.hasPhysicsProvider() ? e1.getPhysicsProvider().get().inertialMatrix
                        .scale(m1)
                        .inverse()
                        .multiply(R1.cross(manifold.normal).cross(R1)) : new Vector3f())
                .add((e2.hasPhysicsProvider() ? e2.getPhysicsProvider().get().inertialMatrix
                        .scale(m2)
                        .inverse()
                        .multiply(R2.cross(manifold.normal).cross(R2)) : new Vector3f())).dot(manifold.normal);

        float jr = jnum / jdenom; //reaction force size

        float jd = (e1.dynamicfriction + e2.dynamicfriction) * 0.5f * jr; //dynamic friction
        float js = (e1.staticfriction + e2.staticfriction) * 0.5f * jr; //static friction

        Vector3f tan = new Vector3f(); //tangent vector to collision
        if (!v.cross(manifold.normal).rezero().equals(new Vector3f())) {
            tan = v.subtract(manifold.normal.multiply(v.dot(manifold.normal))).normalize();
        }
        Vector3f jf = tan.multiply(-jd); //friction force for dynamic collision

        if (js >= v.dot(tan) * (m1 + m2)) {
            jf = tan.multiply(v.dot(tan) * (m1 + m2) * -1f); //static friction
        }

        Vector3f jrv = manifold.normal.multiply(jr); //reaction force along normal vector

        responseManifold.jr = jrv;
        responseManifold.jf = jf;
        responseManifold.R1 = R1;
        responseManifold.R2 = R2;
        responseManifold.normal = manifold.normal;
        responseManifold.depth = manifold.depth;

        return responseManifold;
    }

    private static void processResponse(PhysicsProvider e) {
        if(e.responses.isEmpty()) return;

        for(var responseSet : e.responses) {
            for (var response : responseSet.responses) {
                var R = response.R1;//Vector3f.averageOf(response.manifolds.stream().map(m -> m.R1).collect(Collectors.toList()));
                var jf = response.jf;//Vector3f.averageOf(response.manifolds.stream().map(m -> m.jf).collect(Collectors.toList()));
                var jr = response.jr;//Vector3f.averageOf(response.manifolds.stream().map(m -> m.jr).collect(Collectors.toList()));

                var normal = response.normal;//Vector3f.averageOf(response.manifolds.stream().map(s -> s.normal).collect(Collectors.toList()));

                var depth = response.depth;
                if (enableResponse) {
                    var velChange = jr.add(jf).divide(e.mass).divide(responseSet.responses.size());

                    e.velocity = e.velocity.add(velChange);
                    if (e.applyRotationChangeOnCollision) {
                        var rotChange =
                                e.inertialMatrix.scale(e.mass).inverse().multiply(
                                        R.cross(normal)
                                ).multiply(jr.length())
                                .add(e.inertialMatrix.scale(e.mass).inverse().multiply(
                                        R.cross(jf)
                                ).multiply(jf.length()));
                        e.angvelocity = e.angvelocity.add(rotChange).divide(responseSet.responses.size());
                    }
                }
                e.grounded = e.velocity.y == 0;
            }
        }
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
            inverted.depth = depth;
            inverted.ratio = 1-ratio;

            return inverted;
        }
    }

    public static class ResponseSet{
        List<Response> responses = new ArrayList<>();

        public ResponseSet(List<Response> responses) {
            this.responses = responses;
        }
    }
}
