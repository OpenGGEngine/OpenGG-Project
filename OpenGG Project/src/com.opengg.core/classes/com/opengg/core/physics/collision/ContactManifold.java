/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class ContactManifold {
    public List<Contact> points = new ArrayList<>(3);

    public ContactManifold(){}

    public ContactManifold(Contact... points){
        this.points = List.of(points);
    }

    public ContactManifold(List<Contact> points) {
        this.points = points;
    }

    public static ContactManifold combineManifolds(ContactManifold... manifolds) {
        return combineManifolds(List.of(manifolds));
    }

    public static ContactManifold combineManifolds(List<ContactManifold> manifolds){
        var points = manifolds.stream().flatMap(m -> m.points.stream()).limit(4).collect(Collectors.toList());

        return new ContactManifold(points);
    }

    public ContactManifold reverse(){
        points.forEach(c -> c.normal = c.normal.inverse());
        return this;
    }
    
    @Override
    public String toString(){
        String s = "";
        s += "Collision points: " + points + "\n";
        return s;
    }
}
