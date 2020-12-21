/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision.colliders;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import com.opengg.core.math.geom.Ray;
import com.opengg.core.physics.PhysicsObject;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Represents an axis-aligned or oriented bounding box
 * @author ethachu19
 */
public class BoundingBox extends PhysicsObject{
    Vector3f relMin;
    Vector3f relMax;

    Matrix4f transform = new Matrix4f();

    Vector3f min = new Vector3f(1,1,1);
    Vector3f max = new Vector3f(-1,-1,-1);

    public BoundingBox(Matrix4f transform, List<Vector3f> points){
        this.transform = transform;

        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE,
                minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        for(Vector3f p : points){
            if(p.x > maxX) maxX = p.x;
            if(p.y > maxY) maxY = p.y;
            if(p.z > maxZ) maxZ = p.z;

            if(p.x < minX) minX = p.x;
            if(p.y < minY) minY = p.y;
            if(p.z < minZ) minZ = p.z;
        }

        relMin = new Vector3f(minX, minY, minZ);
        relMax = new Vector3f(maxX, maxY, maxZ);

        recalculate();
    }
    
    public BoundingBox(Matrix4f transform, Vector3f... points){
        this(transform, List.of(points));
    }

    public BoundingBox(List<Vector3f> points){
        this(new Matrix4f(), points);
    }

    public BoundingBox(Vector3f... points){
        this(new Matrix4f(), List.of(points));
    }
    
    public BoundingBox(Vector3f lwh) {
        relMin = lwh.multiply(-1);
        relMax = lwh;

        recalculate();
    }
    
    public BoundingBox(float length, float width, float height){
        this(new Vector3f(length, width, height));
    }

    public Vector3f getLWH() {
        return relMax.subtract(relMin).divide(2);
    }
    
    public Vector3f[] getAABBVertices(){
        return new Vector3f[] {relMin, relMax};
    }

    public void recalculate(){
        min = relMin.multiply(getScale()).add(getPosition());
        max = relMax.multiply(getScale()).add(getPosition());
    }
    
    public boolean isColliding(BoundingBox x) {
        return ! (max.x < x.min.x ||
                max.y < x.min.y ||
                max.z < x.min.z ||
                min.x > x.max.x ||
                min.y > x.max.y ||
                min.z > x.max.z);
    }
    
    public boolean isColliding(Vector3f pos){
        pos = transform.invert().transform(pos);
        return ! (max.x < pos.x ||
                max.y < pos.y ||
                max.z < pos.z ||
                min.x > pos.x ||
                min.y > pos.y ||
                min.z > pos.z);
    }

    public Optional<Vector3f> getCollision(Ray ray) {
        if(transform.equals(new Matrix4f())){
            return getAABBCollision(ray);
        }else{
            var newRay = new Ray(
                    transform.invert().transform(new Vector4f(ray.pos(), 1.0f)).truncate(),
                    transform.invert().transform(new Vector4f(ray.dir(), 0.0f)).normalize().truncate());
            return getAABBCollision(newRay);
        }
    }

    private Optional<Vector3f> getAABBCollision(Ray ray){
        Vector3f invDir = new Vector3f(1f / ray.dir().x, 1f / ray.dir().y, 1f / ray.dir().z);

        boolean signDirX = invDir.x < 0;
        boolean signDirY = invDir.y < 0;
        boolean signDirZ = invDir.z < 0;

        Vector3f bbox = signDirX ? max : min;
        double tmin = (bbox.x() - ray.pos().x()) * invDir.x();
        bbox = signDirX ? min : max;
        double tmax = (bbox.x() - ray.pos().x()) * invDir.x();
        bbox = signDirY ? max : min;
        double tymin = (bbox.y() - ray.pos().y()) * invDir.y();
        bbox = signDirY ? min : max;
        double tymax = (bbox.y() - ray.pos().y()) * invDir.y();

        if ((tmin > tymax) || (tymin > tmax)) {
            return Optional.empty();
        }
        if (tymin > tmin) {
            tmin = tymin;
        }
        if (tymax < tmax) {
            tmax = tymax;
        }

        bbox = signDirZ ? max : min;
        double tzmin = (bbox.z() - ray.pos().z()) * invDir.z;
        bbox = signDirZ ? min : max;
        double tzmax = (bbox.z() - ray.pos().z) * invDir.z;

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return Optional.empty();
        }
        if (tzmin > tmin) {
            tmin = tzmin;
        }
        if (tzmax < tmax) {
            tmax = tzmax;
        }
        //if ((tmin < maxDist) && (tmax > minDist)) {
        return Optional.of(ray.pos().add(ray.dir().multiply((float)tmin)));
        //  }
        // return Optional.empty();
    }

    @Override
    public String toString() {
        return "[" + relMin.toString() + " , " + relMax.toString() + "]";
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(relMin);
        out.write(relMax);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        relMin = in.readVector3f();
        relMax = in.readVector3f();
        recalculate();
    }
}
