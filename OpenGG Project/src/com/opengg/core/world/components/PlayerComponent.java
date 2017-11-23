/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.BindController;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector3fm;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.CapsuleCollider;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
/**
 *
 * @author Javier
 */
public class PlayerComponent extends Component implements Actionable{
    private final PhysicsComponent playerphysics;
    private final UserControlComponent controller;
    private final CameraComponent camera;
    
    Vector3fm control = new Vector3fm();
    Vector3fm controlrot = new Vector3fm();
    public Vector3f currot = new Vector3f();
    float speed = 80;
    float rotspeed = 1000;
    
    public PlayerComponent(){
        camera = new CameraComponent();
        controller = new UserControlComponent();
        playerphysics = new PhysicsComponent();
        playerphysics.addCollider(new CollisionComponent(new AABB(new Vector3f(),10,6,10), new CapsuleCollider(new Vector3f(0,3,0),2)));
        attach(camera);
        attach(controller);
        attach(playerphysics);
    }
    
    @Override
    public void update(float delta){
        
        currot = currot.setX(currot.x() + controlrot.x * rotspeed * delta);
        currot = currot.setY(currot.y() + controlrot.y * rotspeed * delta);
        currot = currot.setZ(currot.z() + controlrot.z * rotspeed * delta);
        /*
        this.setRotationOffset(new Quaternionf(currot));
        
        float xvel = control.x * delta * speed;
        if((abs(playerphysics.getEntity().velocity.x()) < 20))
            playerphysics.getEntity().velocity.x += xvel;
        
        if(control.x == 0)
            playerphysics.getEntity().velocity.x /= 2;
        
        float zvel = control.x * delta * speed;
        if(abs(playerphysics.getEntity().velocity.x()) < 20)
            playerphysics.getEntity().velocity.z += zvel; 
        
        if(control.x == 0)
            playerphysics.getEntity().velocity.z /= 2;
            
        if((control.x == 1) && (getPosition().x() <= getWorld().floorLev + 0.001f))
            playerphysics.getEntity().velocity.y += 5;*/
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
