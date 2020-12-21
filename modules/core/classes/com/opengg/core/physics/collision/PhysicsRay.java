/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.math.geom.Ray;
import com.opengg.core.physics.collision.colliders.BoundingBox;

import java.util.Optional;

/**
 *
 * @author ethachu19
 */
public record PhysicsRay(Vector3f dir, Vector3f pos, float length) {
    public Vector3f vectorBetween(Vector3f vec) {
        return pos.subtract(vec).subtract(dir.multiply(pos.subtract(vec).dot(dir)));
    }
    
    public Optional<Vector3f> isColliding(BoundingBox box) {
        return box.getCollision(this.getRay());
    }

    public Ray getRay(){
        return new Ray(pos, dir);
    }

}
