/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.components.Component;
import static com.opengg.core.world.components.physics.PhysicsConstants.BASE;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class PhysicsComponent extends Component {
    Collider c;
    public boolean gravEffect = true;
    public List<Vector3f> forces = new LinkedList<>();
    public List<Vector3f> rotforces = new LinkedList<>();
    
    public Vector3f velocity = new Vector3f();
    public Vector3f angvelocity = new Vector3f();
    
    public Vector3f acceleration = new Vector3f();
    public Vector3f angaccel = new Vector3f();
    
    private float mass = 10f;
    private float density = 1f;
    
    public PhysicsComponent(){}
    public PhysicsComponent(float mass){
        this.mass = mass;
    }

    @Override
    public void update(float delta) {
        pos = parent.getPosition();
        rot = parent.getRotation();
        delta /= 1000;
        
        Vector3f last = new Vector3f(acceleration);
        pos.addEquals(velocity.multiply(delta).add(last.multiply((float) Math.pow(delta, 2) * 0.5f)));
        
        Vector3f force = finalForce(delta);
        accel(last, force);
        velocity.addEquals(acceleration.multiply(delta));
        
        if((pos.y) < BASE){ 
            pos.y = BASE;
            velocity.y = 0;
        }
               
        parent.setPositionOffset(pos);
        parent.setRotationOffset(rot);
    }
    
    public void setCollider(Collider c){
       this.c = c;
       c.setParentPhysicsComponent(this);
    }
    
    public Collider getCollider() {
        return c;
    }
    
    private Vector3f finalForce(float delta) {
        Vector3f fforce = new Vector3f();
        for(Vector3f force : forces){
            fforce.add(force);
        }
        return fforce;
    }
    
    private void accel(Vector3f last, Vector3f force){
        acceleration = force.divide(mass);
        acceleration = (last.add(acceleration)).divide(2);
        
        if (gravEffect) {
            acceleration.x += getWorld().gravityVector.x;
            acceleration.y += getWorld().gravityVector.y;
            acceleration.z += getWorld().gravityVector.z;
        }
    }
    
    public static PhysicsComponent interpolate(PhysicsComponent a, PhysicsComponent b, float alpha) {
        PhysicsComponent state = b;
        state.pos = a.pos.multiply(1 - alpha).add(b.pos.multiply(alpha));
        state.velocity = a.velocity .multiply(1 - alpha).add(b.velocity .multiply(alpha));
        
        state.rot = Quaternionf.slerp(a.rot, b.rot, alpha);
        state.angvelocity = a.angvelocity.multiply(1 - alpha).add(b.angvelocity.multiply(alpha));
        
        state.acceleration = a.acceleration.multiply(1 - alpha).add(b.acceleration.multiply(alpha));
        state.angaccel = a.angaccel.multiply(1 - alpha).add(b.angaccel.multiply(alpha));
        return state;
    }
    
    @Override
    public void serialize(Serializer s){
        super.serialize(s);
        s.add(mass);
        s.add(density);
    }
    
    @Override
    public void deserialize(Deserializer s){
        super.deserialize(s);
        mass = s.getFloat();
        density = s.getFloat();
    }
}
