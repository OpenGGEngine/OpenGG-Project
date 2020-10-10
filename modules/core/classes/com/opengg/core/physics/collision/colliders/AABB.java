/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision.colliders;

import com.opengg.core.math.Vector3f;
import com.opengg.core.math.geom.Ray;
import com.opengg.core.physics.PhysicsObject;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class AABB extends PhysicsObject{
    Vector3f lwh = new Vector3f();
    Vector3f offset = new Vector3f();
    
    Vector3f min = new Vector3f(1,1,1);
    Vector3f max = new Vector3f(-1,-1,-1);

    public AABB(List<Vector3f> points){
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE,
                minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        for(Vector3f p : points){
            if(p.x > maxX) maxX = p.x;
            if(p.y > maxY) maxY = p.y;
            if(p.z > maxZ) maxZ = p.z;

            if(p.x < minX) minX = p.x;
            if(p.y < minY) minY = p.y;
            if(p.z < minZ) minZ = p.z;
        }

        lwh = new Vector3f(maxX-minX, maxY-minY, maxZ - minZ).divide(2);
        offset = new Vector3f((maxX+minX)/2,(maxY+minY)/2,(maxZ+minZ)/2);

        recalculate();
    }
    
    public AABB(Vector3f... points){
        this(List.of(points));
    }
    
    public AABB(Vector3f lwh) {
        this.lwh = lwh;
        this.offset = new Vector3f();
        recalculate();
    }
    
    public AABB(float length, float width, float height){
        this(new Vector3f(length, width, height));
    }

    public Vector3f getLWH() {
        return lwh;
    }

    public void setLWH(Vector3f lwh) {
        this.lwh = lwh;
    }
    
    public Vector3f[] getAABBVertices(){
        return new Vector3f[] {min, max};
    }

    public void recalculate(){
        min = new Vector3f(-1,-1,-1).multiply(lwh).add(offset).multiply(getScale()).add(getPosition());
        max = new Vector3f(1,1,1).multiply(lwh).add(offset).multiply(getScale()).add(getPosition());
    }
    
    public boolean isColliding(AABB x) {
        return ! (max.x < x.min.x || 
                  max.y < x.min.y ||
                  max.z < x.min.z ||
                  min.x > x.max.x || 
                  min.y > x.max.y ||
                  min.z > x.max.z);
    }
    
    public boolean isColliding(Vector3f pos){
        return ! (max.x < pos.x || 
                  max.y < pos.y ||
                  max.z < pos.z ||
                  min.x > pos.x || 
                  min.y > pos.y ||
                  min.z > pos.z);
    }

    public boolean isColliding(Ray ray) {
        Vector3f invDir = ray.getDir().reciprocal();
        boolean signDirX = invDir.x < 0;
        boolean signDirY = invDir.y < 0;
        boolean signDirZ = invDir.z < 0;
        Vector3f bbox = signDirX ? max : min;
        float tmin = (bbox.x - ray.getPos().x) * invDir.x;
        bbox = signDirX ? min : max;
        float tmax = (bbox.x - ray.getPos().x) * invDir.x;
        bbox = signDirY ? max : min;
        float tymin = (bbox.y - ray.getPos().y) * invDir.y;
        bbox = signDirY ? min : max;
        float tymax = (bbox.y - ray.getPos().y) * invDir.y;
        if ((tmin > tymax) || (tymin > tmax)) {
            return false;
        }
        if (tymin > tmin) {
            tmin = tymin;
        }
        if (tymax < tmax) {
            tmax = tymax;
        }
        bbox = signDirZ ? max : min;
        float tzmin = (bbox.z - ray.getPos().z) * invDir.z;
        bbox = signDirZ ? min : max;
        float tzmax = (bbox.z - ray.getPos().z) * invDir.z;
        if ((tmin > tzmax) || (tzmin > tmax)) {
            return false;
        }
        if (tzmin > tmin) {
            tmin = tzmin;
        }
        if (tzmax < tmax) {
            tmax = tzmax;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + min.toString() + " , " + max.toString() + "]";
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(lwh);
        out.write(offset);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        lwh = in.readVector3f();
        offset = in.readVector3f();
        recalculate();
    }
}
