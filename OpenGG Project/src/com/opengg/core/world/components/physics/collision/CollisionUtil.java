/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics.collision;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class CollisionUtil {
    public static float COLLISION_OFFSET = 0.5F;

    public static CollisionData SphereSphere(SphereCollider c1, SphereCollider c2){
        if(c1.getPosition().getDistance(c2.getPosition()) < c1.radius + c2.radius){
            CollisionData data = new CollisionData();
            data.collisionNormal = c1.getPosition().subtract(c2.getPosition());
            data.collisionPoint = c1.getPosition().add(data.collisionNormal.divide((float)(c1.radius/c2.radius)));
            data.collider1 = c1.parent;
            data.collider2 = c2.parent;
            data.collisionNormal = data.collisionNormal.normalize();
            return data;
        }
        return null;
    }
    
    public static CollisionData SphereCylinder(SphereCollider c1, CylinderCollider c2){
        
        return null;
    }
    
    public static CollisionData CylinderCylinder(CylinderCollider c1, CylinderCollider c2){
        int ctype = 0;

        if(c1.getPosition().y > c2.height + c2.getPosition().y) return null;
        if(c2.getPosition().y > c1.height + c1.getPosition().y) return null;

        if(new Vector2f(c1.getPosition().x, c1.getPosition().z).getDistance(new Vector2f(c2.getPosition().x, c2.getPosition().z)) > c1.radius + c2.radius) return null;
        
        if(c1.getPosition().y > c2.height + c2.getPosition().y - COLLISION_OFFSET) ctype = 1;
        if(c2.getPosition().y > c1.height + c1.getPosition().y - COLLISION_OFFSET) ctype = 1;
        CollisionData data = new CollisionData();
        data.collider1 = c1.parent;
        data.collider2 = c2.parent;
        
        if(ctype == 1) data.collisionNormal = new Vector3f(0,1,0);
        else{
            Vector3f tnormal = c1.getPosition().subtract(c2.getPosition());
            data.collisionNormal = new Vector3f(tnormal.x, 0, tnormal.z).normalize();
        }    
        
        return data;
    }
}
