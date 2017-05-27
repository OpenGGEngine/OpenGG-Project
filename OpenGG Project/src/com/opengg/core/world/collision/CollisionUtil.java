/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.collision;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class CollisionUtil {
    public static float COLLISION_OFFSET = 0.5F;

    public static Collision SphereSphere(SphereCollider c1, SphereCollider c2){
        if(c1.getPosition().getDistance(c2.getPosition()) < c1.radius + c2.radius){
            Collision data = new Collision();
            data.collisionNormal = c1.getPosition().subtract(c2.getPosition());
            data.collisionPoint = c1.getPosition().add(data.collisionNormal.divide((float)(c1.radius/c2.radius)));
            data.collisionNormal = data.collisionNormal.normalize();
            data.overshoot = data.collisionNormal.multiply((float)
                    (c1.radius + c2.radius) - c1.getPosition().getDistance(c2.getPosition()));
            return data;
        }
        return null;
    }
    
    public static Collision SphereCylinder(SphereCollider c1, CylinderCollider c2){
        
        return null;
    }
    
    public static Collision CylinderCylinder(CylinderCollider c1, CylinderCollider c2){
        int ctype = 0;

        if(c1.getPosition().y > c2.height + c2.getPosition().y) return null;
        if(c2.getPosition().y > c1.height + c1.getPosition().y) return null;

        if(new Vector2f(c1.getPosition().x, c1.getPosition().z).getDistance(new Vector2f(c2.getPosition().x, c2.getPosition().z)) > c1.radius + c2.radius) return null;
        
        if(c1.getPosition().y > c2.height + c2.getPosition().y - COLLISION_OFFSET) ctype = 1;
        if(c2.getPosition().y > c1.height + c1.getPosition().y - COLLISION_OFFSET) ctype = 1;
        Collision data = new Collision();
        
        if(ctype == 1){ 
            data.collisionNormal = new Vector3f(0,1,0);
            data.overshoot = new Vector3f(0, (c1.getPosition().y - c2.getPosition().y)/2, 0);
        }else{
            Vector3f tnormal = c1.getPosition().subtract(c2.getPosition());
            data.collisionNormal = new Vector3f(tnormal.x, 0.1f, tnormal.z).normalize();
            data.overshoot = new Vector3f(c1.getPosition().x - c2.getPosition().x,0,c1.getPosition().z - c2.getPosition().z).divideThis(2);
        }    
        
        return data;
    }
    
    public static Collision SphereTerrain(SphereCollider c1, TerrainCollider c2){
        Vector3f np = c1.getPosition().subtract(c2.getPosition()).divide(c2.parent.getScale());
        float height = c2.t.getHeight(np.x, np.z);
        if(height == 12345)
            return null;
        height += c2.getPosition().y;
        height *= c2.parent.getScale().y;
        if(!(c1.getPosition().y < height))
            return null;
        Collision data = new Collision();
        data.collisionPoint = new Vector3f(c1.getPosition().x, height, c1.getPosition().z);
        data.collisionNormal = c2.t.getNormalAt(np.x, np.z);
        data.overshoot = new Vector3f(0,c1.getPosition().y-height,0);
        return data;
    }
    
    public static Collision CylinderTerrain(CylinderCollider c1, TerrainCollider c2){
        Vector3f np = c1.getPosition().subtract(c2.getPosition()).divide(c2.parent.getScale());
        float height = c2.t.getHeight(np.x, np.z);
        if(height == 12345)
            return null;
        height += c2.getPosition().y;
        height *= c2.parent.getScale().y;
        if(!(c1.getPosition().y < height))
            return null;
        Collision data = new Collision();
        data.collisionPoint = new Vector3f(c1.getPosition().x, height, c1.getPosition().z);
        data.collisionNormal = c2.t.getNormalAt(np.x, np.z);
        data.overshoot = new Vector3f(0,c1.getPosition().y-height,0);
        return data;
    }
}
