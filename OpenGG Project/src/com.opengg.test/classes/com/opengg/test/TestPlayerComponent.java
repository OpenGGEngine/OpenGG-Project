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
import com.opengg.core.math.Vector3fm;
import com.opengg.core.physics.Force;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ActionTransmitterComponent;
import com.opengg.core.world.components.WorldObject;
import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class TestPlayerComponent extends Component implements Actionable{
    private final PhysicsComponent playerphysics;
    private final ActionTransmitterComponent controller;
    private final CameraComponent camera;
    //private final GunComponent gun;
    
    Vector3fm control = new Vector3fm();
    Vector3fm controlrot = new Vector3fm();
    Vector3f currot = new Vector3f();
    Vector3f weaponpos = new Vector3f(0.5f,1.1f,-2f);
    Vector3f aweaponpos = new Vector3f(0f,1.2f,-2f);
    Vector3f cweaponpos = weaponpos;
    Force force = new Force();
    float speed = 20;
    float rotspeed = 30;
    float bobspeed = 30;
    boolean weaponbob = false;
    boolean aim = false;
    float bob = 0;
    private final WorldObject head;
    
    public TestPlayerComponent(){
        head = new WorldObject();
        camera = new CameraComponent();
        head.setPositionOffset(new Vector3f(0,2f,0));
        head.setAbsoluteOffset(true);
        controller = new ActionTransmitterComponent();
        playerphysics = new PhysicsComponent();
        ArrayList<Vector3f> v2 = new ArrayList<>();
        v2.add(new Vector3f(-1,-1,-1));
        v2.add(new Vector3f(-1,1,-1));
        v2.add(new Vector3f(-1,-1,1));
        v2.add(new Vector3f(-1,1,1));
        v2.add(new Vector3f(1,-1,-1));
        v2.add(new Vector3f(1,1,-1));
        v2.add(new Vector3f(1,-1,1));
        v2.add(new Vector3f(1,1,1));
        playerphysics.addCollider(new CollisionComponent(new AABB(3,3,3),new ConvexHull(v2)));
                
                //new CapsuleCollider(new Vector3f(-1,0,0), new Vector3f(1,0,0), 2)));
        playerphysics.getEntity().mass = 20f;
        playerphysics.getEntity().addForce(force);
        
        //gun = new GunComponent();
        //gun.setPositionOffset(weaponpos);
        //gun.setRotationOffset(new Quaternionf(new Vector3f(0,90,0)));

        head.attach(camera);
        //head.attach(gun);
        attach(controller);
        attach(playerphysics);
        attach(head);
    }
    
    @Override
    public void update(float delta){
        currot = currot.setX(currot.x + controlrot.x * rotspeed * delta);
        currot = currot.setY(currot.y + controlrot.y * rotspeed * delta);
        currot = currot.setZ(currot.z + controlrot.z * rotspeed * delta);
                
        this.setRotationOffset(new Quaternionf(new Vector3f(0, currot.y, currot.z)));      
        Vector3f movement = new Vector3f(control.x  * speed, 0 ,control.z  *speed);
        movement = getRotation().transform(movement);
        
        head.setRotationOffset(new Quaternionf(new Vector3f(currot.x,0,0)));
        
        force.force = new Vector3f(movement.x,0,movement.z);
        if((control.y == 1) && playerphysics.getEntity().lowestContact.y > 0.6f)
            playerphysics.getEntity().velocity = playerphysics.getEntity().velocity.add(new Vector3f(0,5,0));
        
        if(aim)
            cweaponpos = aweaponpos;
        else
            cweaponpos = weaponpos;
        
        if(weaponbob){
            if(playerphysics.getEntity().velocity.length() < 0.5f){
                bob = 0;
                //gun.setPositionOffset(cweaponpos.subtract(new Vector3f(0,2,0)));
                return;
            }
                
            Vector3f init = new Vector3f();
            Vector3f fin = new Vector3f(0,0.5f,0);
            
            bob += playerphysics.getEntity().velocity.length() * bobspeed * delta;
            
            Vector3f fpos = cweaponpos.add(Vector3f.lerp(init, fin, FastMath.sinDeg(bob)));
            fpos = fpos.subtract(new Vector3f(0,2,0));
            //gun.setPositionOffset(fpos);
        }else{
            //gun.setPositionOffset(cweaponpos.subtract(new Vector3f(0,2,0)));
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
                    controlrot.y -= 1;
                    break;
                case "lookleft":
                    controlrot.y += 1;
                    break;
                 case "lookup":
                    controlrot.x += 1;
                    break;
                case "lookdown":
                    controlrot.x -= 1;
                    break;
                case "aim":
                    aim = true;
                    break;
                case "fire":
                    //gun.fire();
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
                    controlrot.y += 1;
                    break;
                case "lookleft":
                    controlrot.y -= 1;
                    break;
                case "lookup":
                    controlrot.x -= 1;
                    break;
                case "lookdown":
                    controlrot.x += 1;
                    break;
                case "aim":
                    aim = false;
                    break;
            }
        }
    }
    
    public void use(){
        BindController.addController(controller);
        camera.use();
    }
    
}
