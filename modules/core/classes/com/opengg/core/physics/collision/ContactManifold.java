/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Javier
 */
public class ContactManifold {
    public List<Contact> contactsInUse = new ArrayList<>(4);
    public List<Contact> contactsNotInUse = new ArrayList<>(4);


    public ContactManifold(){}

    public ContactManifold(Contact... contacts){
        this.contactsInUse = List.of(contacts);
    }

    public ContactManifold(List<Contact> contacts){
        this.contactsInUse = contacts;
    }

    public ContactManifold(List<Contact> contacts, List<Contact> unused) {
        this.contactsInUse = contacts;
        this.contactsNotInUse = unused;
    }

    public ContactManifold reverse(){
        contactsInUse.forEach(c -> c.normal = c.normal.inverse());
        return this;
    }

    public static ContactManifold combineManifolds(List<ContactManifold> manifolds){
        if(manifolds.size() == 1) return manifolds.get(0);
        var points = manifolds.stream().flatMap(m -> Stream.concat(m.contactsInUse.stream(), m.contactsNotInUse.stream())).collect(Collectors.toList());
        var useful = new ArrayList<>();
        if(points.size() < 4) return new ContactManifold(points, List.of());
        else{
            points = points.stream().filter(p -> p.timestamp > PhysicsEngine.getInstance().getTick() - 5).collect(Collectors.toList());
            var p1 = getFarthestInDirection(new Vector3f(1,1,1), points);
            var p2 = ContactManifold.getFarthest(p1, points);
            var last2 = ContactManifold.getBothFarthestFromLine(Tuple.of(p1, p2), points);
            var p3 = last2.x;
            if(FastMath.isCollinear(p1.offset1, p2.offset1, p3.offset1)) return new ContactManifold(List.of(p1,p2));
            var p4 = last2.y;
            var unused = points.stream()
                    .filter(p -> p != p1 && p != p2 && p != p3 && p != p4)
                    .collect(Collectors.toList());
            return new ContactManifold(List.of(p1,p2,p3,p4), unused);
        }
    }
    public static Contact getFarthestInDirection(Vector3f dir, List<Contact> vertices) {
        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < vertices.size(); i++) {
            float dot = dir.dot(vertices.get(i).offset1);
            if (dot > max) {
                max = dot;
                index = i;
            }
        }
        return vertices.get(index);
    }

    public static Contact getFarthest(Contact point, List<Contact> vertices){
        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < vertices.size(); i++) {
            float dist2 = vertices.get(i).offset1.distanceToSquared(point.offset1);
            if (dist2 > max) {
                max = dist2;
                index = i;
            }
        }
        return vertices.get(index);
    }

    public static Tuple<Contact, Contact> getBothFarthestFromLine(Tuple<Contact, Contact> line, List<Contact> vertices){
        float max = Float.NEGATIVE_INFINITY;
        Vector3f closestApproachBig = new Vector3f();
        int index = 0;
        for (int i = 0; i < vertices.size(); i++) {
            var closestApproach = FastMath.closestPointTo(line.x.offset1, line.y.offset1, vertices.get(i).offset1, false);
            var dist2 = closestApproach.distanceToSquared(vertices.get(i).offset1);
            if (dist2 > max) {
                max = dist2;
                index = i;
                closestApproachBig = closestApproach;
            }
        }
        var furthestContact = vertices.get(index);

        var furthest2 = getFarthestInDirection(closestApproachBig.subtract(furthestContact.offset1), vertices);

        return Tuple.of(furthestContact, furthest2);
    }
    
    @Override
    public String toString(){
        String s = "";
        s += "Collision points: " + contactsInUse + "\n";
        return s;
    }
}
