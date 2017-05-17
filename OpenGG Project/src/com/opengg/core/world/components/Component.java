/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.World;

/**
 * Represents any object attachable to another Component that links with the WorldEngine for updating and serializing<br><br>
 * 
 * This class is the core class of the World system, which is what a developer would interact the most during development. All component
 * have a position, scale, and rotation, regardless of whether or not they are used. In addition they have a unique ID, a name which may
 * or may not be unique depending on developer preference, and only one parent, where if it this is a top level component, the parent would be the 
 * corresponding World. <br><br>
 * To create a custom component, first extend this class. This class itself, while it has no practical use by itself, contains all of the
 * code that should be needed to use the features described above, so take care when overriding these default classes. the 
 * @author Javier
 */
public abstract class Component{
    public static int curid = 0;
    public int id;
    public boolean absoluteOffset = false;
    public String name = "";
    public ComponentHolder parent;
    public Vector3f pos = new Vector3f();
    public Quaternionf rot = new Quaternionf();
    public Vector3f scale = new Vector3f(1,1,1);
    
    
    /**
     * Creates a component with a new ID
     */
    public Component(){
        id = curid;
        curid++;
    }
    
    /**
     * Sets the component's readable name
     * @param name The new name, should preferably be unique but is not required
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * Get the component's readable name
     * @return the component's name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Set the parent of the component, should rarely be called directly
     * @param parent The object that represents the new parent to this component
     */
    public void setParentInfo(ComponentHolder parent){
        this.parent = parent;
    }
    
    /**
     * Sets the local position offset of the object relative to the parent
     * @param pos New position offset
     */
    public void setPositionOffset(Vector3f pos){
        this.pos = pos;
    }
    
    /**
     * Sets the local rotation offset of the object relative to the parent
     * @param rot New rotation offset
     */
    public void setRotationOffset(Quaternionf rot){
        this.rot = rot;
    }
    
    /**
     * Sets the scaling offset of the object relative to the parent
     * @param scale New scale offset
     */
    public void setScale(Vector3f scale){
        this.scale = scale;
    }
    
    /**
     * Set whether the position offset should be absolute or take rotation into account (See {@link #getPosition() getPosition()})
     * @param abs True for absolute, false for relative
     */
    public void setAbsoluteOffset(boolean abs){
        absoluteOffset = abs;
    }
    
    /**
     * Returns whether or not there is absolute offset enabled
     * @return Current state of absolute offset
     */
    public boolean isAbsoluteOffset(){
        return absoluteOffset;
    }
    
    /**
     * Returns the component's position in world space, dependent on state of the absolute offset variable.<br><br>
     * 
     * With absolute offset, the position equation is {@code parent.getPosition().add(offset);}<br>
     * Without, it is {@code parent.getPosition().add(parent.getRotation().transform(pos));}
     * @return World position of this component
     */
    public Vector3f getPosition(){
        if(absoluteOffset){
            return parent.getPosition().add(pos);
        }else{
            return parent.getPosition().add(parent.getRotation().transform(pos));
        }
    }
    
    /**
     * Returns the current position offset of the component
     * @return Position offset of the component
     */
    public Vector3f getPositionOffset(){
        return pos;
    }
    
    /**
     * Returns the final rotation of the current object, derived by multiplying the parent's rotation by the current offset
     * @return Final rotation direction
     */
    public Quaternionf getRotation(){
        return parent.getRotation().multiply(rot);
    }
    
    /**
     * Returns the current rotation offset of the component
     * @return Rotation offset of the component
     */
    public Quaternionf getRotationOffset(){
        return rot;
    }
    
    /**
     * gets the total scaling factor for the object, derived by multiplying the parent's scale by the current offset
     * @return Final object scale
     */
    public Vector3f getScale(){
        return new Vector3f(scale).multiply(parent.getScale());
    }
    
    /**
     * Called once by WorldEngine per update cycle, override for functionality
     * @param delta Delta time since last update cycle in seconds
     */
    public void update(float delta){
        
    }
    
    /**
     * Called by various sources for serialization of the component, and by default only serialized position, rotation, and scale offsets<br><br>
     * 
     * For correct functionality, the variables serialized here must match the variables deserialized in {@link #deserialize(com.opengg.core.world.Deserializer) deserialize()}<br>
     * In addition, any object that overrides this must also override the {@link #Component() default constructor} for the serializer to function<br><br>
     * 
     * It is recommended to allow for complete recreation of the object using these two methods
     * @param s Serializer object used for writing objects to the buffer
     */
    public void serialize(Serializer s){
        s.add(pos);
        s.add(rot);
        s.add(scale);
    }
    
    /**
     * Called for deserialization of a byte stream to a component, and by default only deserialized position, rotation, and scale<br><br>
     * 
     * For correct functionality, variable deserialization here must match the variables serialized in {@link #serialize(com.opengg.core.world.Serializer) serialize()}<br>
     * In addition, any object that overrides this must also override the {@link #Component() default constructor} for the deserializer to function<br><br>
     * 
     * As this method is normally run on a separate thread, any methods that have OpenGL calls have to be run in an {@link com.opengg.core.engine.Executable executable} to be run in the main thread. <br>
     * It is recommended to allow for complete recreation of the object using these methods
     * @param d Deserializer object used for reading objects from the buffer
     */
    public void deserialize(Deserializer d){
        pos = d.getVector3f();
        rot = d.getQuaternionf();
        scale = d.getVector3f();
    }
    
    /**
     * Returns the current world of this component
     * @return Current world
     */
    public World getWorld(){
        return parent.getWorld();
    }
    
    /**
     * Called during removal of an object for cleanup
     */
    public void remove(){}
}
