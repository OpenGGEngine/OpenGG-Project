/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.test;

import com.opengg.core.engine.BindController;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.ComponentHolder;
import com.opengg.core.world.components.UserControlComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.core.world.components.physics.collision.BoundingBox;
import com.opengg.core.world.components.physics.collision.CollisionComponent;
import com.opengg.core.world.components.physics.collision.CylinderCollider;
import static java.lang.Math.abs;

/**
 *
 * @author Javier
 */
public class TestPlayerComponent extends ComponentHolder implements Actionable{
    private final PhysicsComponent playerphysics;
    private final UserControlComponent controller;
    private final CameraComponent camera;
    private final GunComponent gun;
    
    Vector3f control = new Vector3f();
    Vector3f controlrot = new Vector3f();
    Vector3f currot = new Vector3f();
    Vector3f weaponpos = new Vector3f(2,-2,-3);
    float speed = 80;
    float rotspeed = 30;
    boolean weaponbob = true;
    float bob = 0;
    
    public TestPlayerComponent(){
        camera = new CameraComponent();
        controller = new UserControlComponent();
        playerphysics = new PhysicsComponent();
        playerphysics.addCollider(new CollisionComponent(new BoundingBox(new Vector3f(),10,6,10), new CylinderCollider(3,2)));
        gun = new GunComponent();
        gun.setPositionOffset(weaponpos);
        gun.setRotationOffset(new Quaternionf(new Vector3f(90,0,0)));
        
        attach(camera);
        attach(controller);
        attach(playerphysics);
        attach(gun);
    }
    
    @Override
    public void update(float delta){
        gun.setRotationOffset(new Quaternionf(new Vector3f(0,pos.x,0)));
        float deltasec = delta / 1000;
        
        currot.x += controlrot.x * rotspeed * deltasec;
        currot.y += controlrot.y * rotspeed * deltasec;
        currot.z += controlrot.z * rotspeed * deltasec;
                
        this.setRotationOffset(new Quaternionf(currot));
        
        float xvel = control.x * deltasec * speed;
        if((abs(playerphysics.velocity.x) < 20))
            playerphysics.velocity.x += xvel;
        
        if(control.x == 0)
            playerphysics.velocity.x /= 2;
        
        float zvel = control.z * deltasec * speed;
        if(abs(playerphysics.velocity.z) < 20)
            playerphysics.velocity.z += zvel; 
        
        if(control.z == 0)
            playerphysics.velocity.z /= 2;
            
        if((control.y == 1) && (getPosition().y <= getWorld().floorLev + 0.001f))
            playerphysics.velocity.y += 5;
        
        if(weaponbob){
            if(playerphysics.velocity.length() < 0.5f){
                bob = 0;
                gun.setPositionOffset(weaponpos);
                return;
            }
                
            Vector3f init = new Vector3f();
            Vector3f fin = new Vector3f(0,0.5f,0);
            
            bob += playerphysics.velocity.length() * 30f * deltasec;
            
            Vector3f fpos = weaponpos.add(Vector3f.lerp(init, fin, FastMath.sinDeg(bob)));
            gun.setPositionOffset(fpos);
        }
    }
    
    @Override
    public void onAction(Action action) {
        if(action.type == ActionType.PRESS){
            switch(action.name){
                case "forward":
                    control.z -= 1;
                    break;
                case "backward":
                    control.z += 1;
                    break;
                case "left":
                    control.x -= 1;
                    break;
                case "right":
                    control.x += 1;
                    break;
                case "up":
                    control.y += 1;
                    break;
                case "lookright":
                    controlrot.y += 1;
                    break;
                case "lookleft":
                    controlrot.y -= 1;
                    break;
                 case "lookup":
                    controlrot.x += 1;
                    break;
                case "lookdown":
                    controlrot.x -= 1;
                    break;
                case "fire":
                    gun.fire();
                    break;
            }
        }else{
            switch(action.name){
                case "forward":
                    control.z += 1;
                    break;
                case "backward":
                    control.z -= 1;
                    break;
                case "left":
                    control.x += 1;
                    break;
                case "right":
                    control.x -= 1;
                    break;
                case "up":
                    control.y -= 1;
                    break;
                case "lookright":
                    controlrot.y -= 1;
                    break;
                case "lookleft":
                    controlrot.y += 1;
                    break;
                case "lookup":
                    controlrot.x -= 1;
                    break;
                case "lookdown":
                    controlrot.x += 1;
                    break;
            }
        }
    }
    
    public void use(){
        BindController.addController(controller);
        camera.use();
    }
    
}
