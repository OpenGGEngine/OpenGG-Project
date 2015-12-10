/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.util.Time;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldManager;
import static com.opengg.core.world.entities.EntityFactory.AddStack;
import com.opengg.core.world.entities.resources.EntityFrame;
import com.opengg.core.world.entities.resources.EntitySupportEnums.Collide;
import com.opengg.core.world.entities.resources.EntitySupportEnums.EntityType;
import com.opengg.core.world.entities.resources.EntitySupportEnums.UpdateForce;
import com.opengg.core.world.entities.resources.EntitySupportEnums.UpdateXYZ;
import static com.opengg.core.world.physics.resources.PhysicsStruct.gravityVector;
import static com.opengg.core.world.physics.resources.PhysicsStruct.wind;
import java.io.Serializable;

/**
 *
 * @author ethachu19
 */
public class Entity implements Serializable{

    /* tags */
    public UpdateForce updateForce;
    public UpdateXYZ updatePosition;
    public Collide collision;
    public float mass;
    public Vector3f pos = new Vector3f();
    public Vector3f force = new Vector3f();
    public Vector3f airResistance = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector3f acceleration = new Vector3f();
    public boolean ground;
    public World currentWorld = null;

    /* Physics*/
    public EntityFrame ef;
    public Vector3f direction = new Vector3f();
    private final Time time = new Time();
    private float height = 5f, width = 5f, length = 5f;
    /* Max - 1, Min - 0 */
    public Vector3f[] boundingBox = {new Vector3f(), new Vector3f()};
    /*
    
     0---x---* 
     | bottom|
     z       |
     |       |
     *-------* 
    
     *---x---* 
     |  top  |
     z       |
     |       |
     *-------* 1
    
     */
    public OBJModel model;

    /**
     * Makes default Entity
     *
     */
    public Entity() {
        if(WorldManager.isEmpty())
            WorldManager.getWorld(new Camera());
        this.currentWorld = WorldManager.getDefaultWorld();
        setXYZ(0f, 0f, 0f);
        this.ground = true;
        setTags(EntityType.Physics);
        bindModel(new OBJModel());
        
        AddStack.add(this);
    }

    /**
     * Creates an entity based off of 5 parameters.
     *
     * @param f Force vector
     * @param position Current Position
     * @param mass Mass of Entity
     * @param type Type of entity
     * @param model Model to be bound to entity
     * @param current Current World
     */
    public Entity(EntityType type, Vector3f position, Vector3f f, float mass, OBJModel model, World current) {
        if (position == null) position = new Vector3f();
        if (f == null) f = new Vector3f();
        this.currentWorld = current;
        setXYZ(position);
        setForce(f);
        this.ground = (pos.y < 60);
        setTags(type);
        bindModel(model);
        this.mass = mass;
        AddStack.add(this);
    }

    /**
     * Creates a new entity based off another.
     *
     * @param v Entity to be copied
     */
    public Entity(Entity v) {
        this.currentWorld = v.currentWorld;
        setXYZ(v.pos.x, v.pos.y, v.pos.z);
        this.mass = v.mass;
        setVelocity(new Vector3f(v.velocity));
        this.ground = (pos.y <= 60);
        this.collision = v.collision;
        this.updatePosition = v.updatePosition;
        this.updateForce = v.updateForce;
        bindModel(v.model);

        AddStack.add(this);
    }

    /**
     * Sets the tag based on predefined entity types
     *
     * @param type Type of entity to be set
     */
    public final void setTags(EntityType type) {
        switch (type) {
            case Static:
                updatePosition = UpdateXYZ.Immovable;
                updateForce = UpdateForce.Realistic;
                collision = Collide.NoResponse;
                break;

            case Physics:
                updatePosition = UpdateXYZ.Movable;
                updateForce = UpdateForce.Realistic;
                collision = Collide.Collidable;
                break;

            case Particle:
                updatePosition = UpdateXYZ.Movable;
                updateForce = UpdateForce.Unrealistic;
                collision = Collide.Uncollidable;
                break;
        }
    }

    public final void updateBoundingBox(){
        boundingBox[0].y = pos.y;
        boundingBox[0].x = pos.x - width / 2;
        boundingBox[0].z = pos.z - length / 2;

        boundingBox[1].y = pos.y + height;
        boundingBox[1].x = pos.x + width / 2;
        boundingBox[1].z = pos.z + length / 2;
    }
    
    /**
     * Sets the Entity's XYZ Coordinates to something
     *
     * @param x X to be set
     * @param y Y to be set
     * @param z Z to be set
     */
    public final void setXYZ(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;

        updateBoundingBox();
        ground = (currentWorld.floorLev <= this.pos.y);
    }

    public final void setXYZ(Vector3f v) {
        this.pos = v;
        
        updateBoundingBox();
        ground = (currentWorld.floorLev <= this.pos.y);
    }

    /**
     * Sets an amount of force to be pushed onto entity
     *
     * @param f Force vector
     */
    public final void setForce(Vector3f f) {
        this.force = new Vector3f(f);
    }

    /**
     * Sets velocity to a vector.
     *
     * @param v Vector for velocity
     */
    public final void setVelocity(Vector3f v) {
        this.velocity = new Vector3f(v);
    }

    public final void setRotation(Vector3f v) {
        this.direction = v;
    }

    /**
     * Binds model to entity
     *
     * @param model Model to be bound
     */
    public final void bindModel(OBJModel model) {
        this.model = model;
//        Vector3f max = new Vector3f(model.getVertices().get(1).x, model.getVertices().get(1).y, model.getVertices().get(1).z);
//        Vector3f min = new Vector3f();
//        for(Vector3f vertice: this.model.getVertices())
//        {
//            if(vertice.x > max.x)
//                max.x = vertice.x;
//            else if(vertice.x < min.x)
//                min.x = vertice.x;
//            if(vertice.y > max.y)
//                max.y = vertice.y;
//            else if(vertice.y < max.y)
//                min.y = vertice.y;
//            if(vertice.z > max.z)
//                max.z = vertice.z;
//            else if(vertice.z < min.z)
//                min.z = vertice.z;
//        }
    }

    /**
     * Updates XYZ based on velocity and acceleration and calculates new values
     * for all of them
     */
    public void updateXYZ() {
        final float timeStep = time.getDeltaSec();
//        print(timeStep);
        ground = (pos.y <= currentWorld.floorLev);
//        System.out.println(ground);s
        if (ground){
            pos.y = currentWorld.floorLev;
            stop(1);
        }
        Vector3f deltaMovement = velocity.multiply(timeStep).add(acceleration.multiply(0.5f * timeStep * timeStep));
        pos = pos.add(deltaMovement);
        updateBoundingBox();
        if (updateForce == UpdateForce.Realistic)
            update(timeStep);
    }

    /**
     * The collision response when collision with another entity is detected
     *
     * @param force Force for how much it should react
     * @return Error
     */
    public boolean collisionResponse(Vector3f force) {
        this.force = force.add(force);
        this.acceleration = new Vector3f();
        this.velocity = velocity.multiply(-1/2);
        return true;
    }

    /**
     * Changes current world of Entity
     *
     * @param next The target world
     */
    public void changeWorld(World next) {
        currentWorld = next;
        gravityVector.y = next.gravity;
    }
    
    
    public final void update(float timeStep) {
        Vector3f lastAcceleration = new Vector3f(acceleration);
        acceleration = force.divide(mass);
        velocity = velocity.add(lastAcceleration.add(acceleration).divide(2f).multiply(timeStep));
        force = force.closertoZero(airResistance).add(wind).subtract(gravityVector);
    }
    
    public final boolean stop(int index){
        switch (index){
            case 0:
                force.x = 0;
                velocity.x = 0;
                acceleration.x = 0;
                break;
            case 1:
                force.y = 0;
                velocity.y = 0;
                acceleration.y = 0;
                break;
            case 2:
                force.z = 0;
                velocity.z = 0;
                acceleration.z = 0;
                break;
            default:
                force.zero();
                velocity.zero();
                acceleration.zero();
                break;
        }
        return true;
    }
    
    public final void changeWind(Vector3f w){
        wind = new Vector3f(w);
    }
}
