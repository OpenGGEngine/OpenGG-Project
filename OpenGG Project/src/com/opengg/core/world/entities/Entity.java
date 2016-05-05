/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import static com.opengg.core.util.GlobalUtil.print;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.engine.WorldManager;
import static com.opengg.core.world.entities.EntityBuilder.AddStack;
import com.opengg.core.world.entities.resources.EntityFrame;
import com.opengg.core.world.entities.resources.EntitySupportEnums;
import com.opengg.core.world.entities.resources.EntitySupportEnums.Collide;
import com.opengg.core.world.entities.resources.EntitySupportEnums.PhysicsType;
import com.opengg.core.world.entities.resources.EntitySupportEnums.UpdateForce;
import com.opengg.core.world.entities.resources.EntitySupportEnums.UpdateXYZ;
import com.opengg.core.world.entities.resources.PhysicsState;
import static com.opengg.core.world.physics.resources.PhysicsStruct.wind;
import java.io.Serializable;

/**
 *
 * @author ethachu19
 */
@Deprecated
public class Entity implements Serializable {

    /* tags */
    public UpdateXYZ updatePosition;
    public Collide collision;
    public boolean ground;
    public short id;

    /* Physics*/
    public EntityFrame ef;
    /**
     * Only to be used for smooth rendering with interpolate
     *
     * @see interpolate
     */
    public PhysicsState previous;
    public PhysicsState current;
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
        if (WorldManager.isEmpty()) {
            WorldManager.getWorld(new Camera());
        }
        current = new PhysicsState(WorldManager.getDefaultWorld(), 10f);
        setXYZ(0f, 0f, 0f);
        setTags(PhysicsType.Physics);
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
    public Entity(PhysicsType type, Vector3f position, Vector3f f, float mass, OBJModel model, World current) {
        if (position == null) {
            position = new Vector3f();
        }
        if (f == null) {
            f = new Vector3f();
        }
        this.current = new PhysicsState(current, mass);
        setXYZ(position);
        setForce(f);
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
        this.current = new PhysicsState(v.current);
        this.collision = v.collision;
        this.updatePosition = v.updatePosition;
        this.current.updateForce = v.current.updateForce;
        bindModel(v.model);

        AddStack.add(this);
    }

    /**
     * Sets the tag based on predefined entity types
     *
     * @param type Type of entity to be set
     */
    public final void setTags(PhysicsType type) {
        switch (type) {
            case Static:
                updatePosition = UpdateXYZ.Immovable;
                current.updateForce = UpdateForce.Realistic;
                collision = Collide.NoResponse;
                break;

            case Physics:
                updatePosition = UpdateXYZ.Movable;
                current.updateForce = UpdateForce.Realistic;
                collision = Collide.Collidable;
                break;

            case Particle:
                updatePosition = UpdateXYZ.Movable;
                current.updateForce = UpdateForce.Unrealistic;
                collision = Collide.Uncollidable;
                break;
        }
    }

    public final void updateBoundingBox() {
        boundingBox[0].y = current.pos.y;
        boundingBox[0].x = current.pos.x - current.width / 2;
        boundingBox[0].z = current.pos.z - current.length / 2;

        boundingBox[1].y = current.pos.y + current.height;
        boundingBox[1].x = current.pos.x + current.width / 2;
        boundingBox[1].z = current.pos.z + current.length / 2;
    }

    /**
     * Sets the Entity's XYZ Coordinates to something
     *
     * @param x X to be set
     * @param y Y to be set
     * @param z Z to be set
     */
    public final void setXYZ(float x, float y, float z) {
        this.current.pos.x = x;
        this.current.pos.y = y;
        this.current.pos.z = z;

        updateBoundingBox();
    }

    public final void setXYZ(Vector3f v) {
        this.current.pos = v;

        updateBoundingBox();
    }

    /**
     * Sets an amount of force to be pushed onto entity
     *
     * @param f Force vector
     */
    public final void setForce(Vector3f f) {
        this.current.momentum = new Vector3f(f);
    }

    /**
     * Sets velocity to a vector.
     *
     * @param v Vector for velocity
     */
    public final void setVelocity(Vector3f v) {
        this.current.velocity = new Vector3f(v);
    }

    public final void setRotation(Quaternion4f q) {
        this.current.rot = q;
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
     * The collision response when collision with another entity is detected
     *
     * @param force Force for how much it should react
     * @return Error
     */
    public boolean collisionResponse(Vector3f force) {

        return true;
    }

    /**
     * Changes current world of Entity
     *
     * @param next The target world
     */
    public void changeWorld(World next) {
        current.switchWorld(next);
    }

    public final void changeWind(Vector3f w) {
        wind = new Vector3f(w);
    }

    public void update(float t, float dt) {
        if (this.updatePosition == EntitySupportEnums.UpdateXYZ.Immovable)
            return;
        previous = new PhysicsState(current);
        current.integrate(current, t, dt);
        print (current.momentum.toString());
        updateBoundingBox();
    }
}
