/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.RenderOperation;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.physics.collision.CapsuleCollider;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.SphereCollider;
import com.opengg.core.render.drawn.Drawable;

/**
 *
 * @author Javier
 */
public class PhysicsRenderer {
    private static RenderOperation path;
    
    private static Model sphere;
    private static Model cylinder;
    
    private static Drawable sphereobj;
    private static Drawable cylinderobj;
    
    public static void initialize(){
        sphere = Resource.getModel("sphere");
        cylinder = Resource.getModel("cylinder");
        
        sphereobj = sphere.getDrawable();
        cylinderobj = cylinder.getDrawable();
        
        path = new RenderOperation("physics", () -> {
            RenderEngine.setWireframe(true);
            for(ColliderGroup group : PhysicsEngine.getInstance().getColliders()){
                for(Collider c : group.getColliders()){
                    renderCollider(c);
                }
            }
            RenderEngine.setWireframe(false);
        });
        path.setEnabled(true);
        RenderEngine.addRenderPath(path);
    }
    
    public static void setEnabled(boolean enabled){
        path.setEnabled(enabled);
    }
    
    public static void renderCollider(Collider c){
        if(c instanceof SphereCollider){
            sphereobj.setMatrix(new Matrix4f().translate(c.getPosition()).scale(new Vector3f(((SphereCollider)c).getRadius())));
            sphereobj.render();
        }else if(c instanceof CapsuleCollider){
            CapsuleCollider nc = (CapsuleCollider)c;
            sphereobj.setMatrix(new Matrix4f().translate(nc.getP1()).scale(new Vector3f(nc.getRadius())));
            sphereobj.render();
            
            Vector3f cdir = nc.getP1().subtract(nc.getP2()).normalize();
            cylinderobj.setMatrix(new Matrix4f().translate(Vector3f.lerp(nc.getP1(), nc.getP2(), 0.5f))
                    .rotate(new Quaternionf(new Vector3f(cdir.x, cdir.y, cdir.z)))
                    .scale(new Vector3f(nc.getRadius(), nc.getP1().subtract(nc.getP2()).length(), nc.getRadius())));
            cylinderobj.render();
            
            sphereobj.setMatrix(new Matrix4f().translate(nc.getP2()).scale(new Vector3f(nc.getRadius())));
            sphereobj.render();
        }
    }

    private PhysicsRenderer() {
    }
}
