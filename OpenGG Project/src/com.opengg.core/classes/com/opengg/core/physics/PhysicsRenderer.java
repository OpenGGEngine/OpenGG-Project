/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.RenderOperation;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.physics.collision.colliders.CapsuleCollider;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.colliders.SphereCollider;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.ShaderController;

import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class PhysicsRenderer {
    private static RenderOperation path;
    
    private static Model sphere;
    private static Model cylinder;
    
    private static Renderable sphereobj;
    private static Renderable cylinderobj;
    
    public static void initialize(){
        /*sphere = Resource.getModel("sphere");
        cylinder = Resource.getModel("cylinder");
        
        sphereobj = sphere.getDrawable();
        cylinderobj = cylinder.getDrawable();
        */
        path = new RenderOperation("physics", () -> {
            RenderEngine.setWireframe(true);
            for(var group : PhysicsEngine.getInstance().getObjects().stream().filter(c -> c instanceof RigidBody).map(c -> (RigidBody)c).collect(Collectors.toList())){
                for(Collider c : group.getColliders()){
                    renderCollider(c);
                }
            }
            RenderEngine.setWireframe(false);
        });
        path.setEnabled(false);
        RenderEngine.addRenderPath(path);
    }
    
    public static void setEnabled(boolean enabled){
        path.setEnabled(enabled);
    }
    
    public static void renderCollider(Collider c){
        if(c instanceof SphereCollider){
            ShaderController.setPosRotScale(c.getPosition(), new Quaternionf(), new Vector3f(((SphereCollider)c).getRadius()));
            sphereobj.render();
        }else if(c instanceof CapsuleCollider){
            CapsuleCollider nc = (CapsuleCollider)c;
            ShaderController.setPosRotScale(nc.getP1(), new Quaternionf(), new Vector3f(nc.getRadius()));
            sphereobj.render();
            
            Vector3f cdir = nc.getP1().subtract(nc.getP2()).normalize();
            ShaderController.setPosRotScale(Vector3f.lerp(nc.getP1(), nc.getP2(), 0.5f),
                    Quaternionf.createXYZ(new Vector3f(cdir.x, cdir.y, cdir.z)),
                    new Vector3f(nc.getRadius(), nc.getP1().subtract(nc.getP2()).length(), nc.getRadius()));
            cylinderobj.render();
            
            ShaderController.setPosRotScale(nc.getP2(), new Quaternionf(), new Vector3f(nc.getRadius()));
            sphereobj.render();
        }
    }

    private PhysicsRenderer() {
    }
}
