/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author ethachu19
 */
public class Collider{

    BoundingBox main;
    ArrayList<BoundingBox> boxes = new ArrayList<>();

    public Collider(BoundingBox main, Collection<BoundingBox> all) {
        this.main = main;
        boxes.addAll(all);
    }

    public boolean testCollision(Collider other) {
        if (!main.isColliding(other.main)) {
            return false;
        }
        for (BoundingBox x: this.boxes) {
            for(BoundingBox y: other.boxes) {
                if (x.isColliding(y))
                    return true;
            }
        }
        return false;
    }

}
