/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.util.Time;
import com.opengg.core.io.objloader.parser.OBJModel;
import static com.opengg.core.util.GlobalUtil.print;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldManager;
import static com.opengg.core.world.entities.EntityFactory.AddStack;
import com.opengg.core.world.physics.ForceManipulation;

/**
 *
 * @author ethachu19
 */
public class Entity {

    public enum EntityType {
        /* Update Movement, Force Update, No Collsion Response*/ Static,
        /* Update Movement, Force Update, Collision Detection*/ Physics,
        /* Update Movement, No force update, No Collision*/ Particle,
        /* User Defined*/ Other
    }

    public enum Collide {

        Collidable, Uncollidable, NoResponse
    }

    public enum UpdateXYZ {

        Movable, Immovable
    }

    public enum UpdateForce {

        Realistic, Unrealistic
    }

    /* tags */
    public UpdateForce updateForce;
    public UpdateXYZ updatePosition;
    public Collide collision;

    /* Native to Entity*/
    public Vector3f pos = new Vector3f();
    public boolean ground;
    public float mass;
    public World currentWorld = null;

    /* Physics*/
    public Vector3f velocity = new Vector3f();
    public Vector3f direction = new Vector3f();
    private final Time time = new Time();
    public Vector3f acceleration = new Vector3f(0, 0, 0);
    public Vector3f lastAcceleration = new Vector3f(0, 0, 0);
    public ForceManipulation forceCalculator;
    private float timeStep;
    private float height = 5f;
    private float width = 5f;
    private float length = 5f;
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
        forceCalculator = new ForceManipulation(new Vector3f(), new Vector3f(), this);
        if(WorldManager.isEmpty())
            WorldManager.getWorld(new Camera());
        this.currentWorld = WorldManager.getDefaultWorld();
        setXYZ(0f, 0f, 0f);
        this.ground = true;
        this.mass = 40f;
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
        this.currentWorld = current;
        forceCalculator = new ForceManipulation(this);
        setXYZ(position);
        setForce(f);
        this.ground = (pos.y < 60);
        this.mass = mass;
        setTags(type);
        bindModel(model);

        AddStack.add(this);
    }

    /**
     * Creates a new entity based off another.
     *
     * @param v Entity to be copied
     */
    public Entity(Entity v) {
        this.currentWorld = v.currentWorld;
        forceCalculator = new ForceManipulation(v.forceCalculator.airResistance, v.forceCalculator.force, this);
        setXYZ(v.pos.x, v.pos.y, v.pos.z);
        setVelocity(v.velocity);
        this.ground = (pos.y < 60);
        this.mass = v.mass;
        this.collision = v.collision;
        this.updatePosition = v.updatePosition;
        this.updateForce = v.updateForce;
        bindModel(v.model);

        AddStack.add(this);
    }

    /**
     * Sets the entity's tags
     *
     * @param collision Tag for Collision Detection
     * @return True
     */
    public boolean setTags(Collide collision) {
        this.collision = collision;
        return true;
    }

    /**
     * Sets the entity's tags
     *
     * @param updateForce Tag for realistic force calculation
     * @return True
     */
    public boolean setTags(UpdateForce updateForce) {
        this.updateForce = updateForce;
        if (this.updateForce == UpdateForce.Realistic && this.updatePosition != UpdateXYZ.Movable) {
            updatePosition = UpdateXYZ.Movable;
        }
        return true;
    }

    /**
     * Sets the entity's tags
     *
     * @param updatePosition Tag for movable entity
     * @return True
     */
    public boolean setTags(UpdateXYZ updatePosition) {
        this.updatePosition = updatePosition;
        if (this.updatePosition == UpdateXYZ.Immovable) {
            this.updateForce = UpdateForce.Unrealistic;
        }
        return true;
    }

    /**
     * Sets the entity's tags
     *
     * @param collision The tag for collision
     * @param updateForce The tag for realistic movement
     * @param updatePosition The tag for movement
     * @return Error
     */
    public boolean setTags(Collide collision, UpdateForce updateForce, UpdateXYZ updatePosition) {
        this.collision = collision;
        this.updatePosition = updatePosition;
        if (this.updatePosition == UpdateXYZ.Immovable) {
            this.updateForce = UpdateForce.Unrealistic;
            return this.updateForce == updateForce;
        }
        this.updateForce = updateForce;
        return true;
    }

    /**
     * Sets the tag based on predefined entity types
     *
     * @param type Type of entity to be set
     */
    public final void setTags(EntityType type) {
        switch (type) {
            case Static:
                updatePosition = UpdateXYZ.Movable;
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

        boundingBox[0].y = y;
        boundingBox[0].x = x - width / 2;
        boundingBox[0].z = z - length / 2;

        boundingBox[1].y = y + height;
        boundingBox[1].x = x + width / 2;
        boundingBox[1].z = z + length / 2;
        ground = (currentWorld.floorLev <= this.pos.y);
    }

    public final void setXYZ(Vector3f v) {
        this.pos = v;
        boundingBox[0].y = pos.y + height;
        boundingBox[0].x = pos.x - width / 2;
        boundingBox[0].z = pos.z + length / 2;

        boundingBox[1].y = pos.y;
        boundingBox[1].x = pos.x + width / 2;
        boundingBox[1].z = pos.z - length / 2;
        ground = (currentWorld.floorLev <= this.pos.y);
    }

    /**
     * Sets an amount of force to be pushed onto entity
     *
     * @param f Force vector
     */
    public final void setForce(Vector3f f) {
        this.forceCalculator.force.x = f.x;
        this.forceCalculator.force.y = f.y;
        this.forceCalculator.force.z = f.z;
    }

    /**
     * Sets velocity to a vector.
     *
     * @param v Vector for velocity
     */
    public final void setVelocity(Vector3f v) {
        this.velocity.x = v.x;
        this.velocity.y = v.y;
        this.velocity.z = v.z;
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
        timeStep = time.getDeltaSec();
//        print(timeStep);
        lastAcceleration = acceleration;
        pos.x += velocity.x * timeStep + (0.5 * lastAcceleration.x * timeStep * timeStep);
        pos.y += velocity.y * timeStep + (0.5 * lastAcceleration.y * timeStep * timeStep);
        pos.z += velocity.z * timeStep + (0.5 * lastAcceleration.z * timeStep * timeStep);
        for (Vector3f x : boundingBox)
            x.x += velocity.x * timeStep + (0.5 * lastAcceleration.x * timeStep * timeStep);
        for (Vector3f y : boundingBox)
            y.y += velocity.y * timeStep + (0.5 * lastAcceleration.y * timeStep * timeStep);
        for (Vector3f z : boundingBox)
            z.z += velocity.z * timeStep + (0.5 * lastAcceleration.z * timeStep * timeStep);
        ground = (pos.y <= currentWorld.floorLev);
        acceleration.x = forceCalculator.force.x / mass;
        acceleration.y = forceCalculator.force.y / mass;
        acceleration.z = forceCalculator.force.z / mass;
        velocity.x += (lastAcceleration.x + acceleration.x) / 2 * timeStep;
        velocity.y += (lastAcceleration.y + acceleration.y) / 2 * timeStep;
        velocity.z += (lastAcceleration.z + acceleration.z) / 2 * timeStep;
    }

    /**
     * The collision response when collision with another entity is detected
     *
     * @param force Force for how much it should react
     * @return Error
     */
    public boolean collisionResponse(Vector3f force) {
        this.forceCalculator.force.x += force.x;
        this.forceCalculator.force.y += force.y;
        this.forceCalculator.force.z += force.z;
        return true;
    }

    /**
     * Changes current world of Entity
     *
     * @param next The target world
     */
    public void changeWorld(World next) {
        currentWorld = next;
    }
}
