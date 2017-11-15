/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.World;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents any object attachable to another Component that links with the WorldEngine for updating and serializing<br><br>
 * 
 * This class is the core class of the World system, which is what a developer would interact the most with during development. All component
 * have a position, scale, and rotation, regardless of whether or not they are used. In addition they have a unique ID, a name which may
 * or may not be unique depending on developer preference, and only one parent, and if it this is a top level component, the parent would be the 
 * corresponding World. <br><br>
 * To create a custom component, first extend this class. This class itself, while it has no practical use by itself, contains all of the
 * code that should be needed to use the features described above, so take care when overriding these default classes.
 * @author Javier
 */
public abstract class Component{
    private static int curid = 0;
    private int id;
    public boolean absoluteOffset = false;
    public boolean enabled = true;
    public float updatedistance = 0;
    private String name = "";
    private Component parent;
    private Vector3f posoffset = new Vector3f();
    private Quaternionf rotoffset = new Quaternionf();
    private Vector3f scale = new Vector3f(1,1,1);
    protected List<Component> children = new ArrayList<>();
    private Vector3f pos = new Vector3f();
    private Quaternionf rot = new Quaternionf();
    private boolean serialize = true;
    
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
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    /**
     * Set the parent of the component, should rarely be called directly
     * @param parent The object that represents the new parent to this component
     */
    private void setParentInfo(Component parent){
        this.parent = parent;
        
        regenPos();
        for(Component c : children) c.regenPos();
        regenRot();
        for(Component c : children) c.regenRot();
        
        if(parent instanceof World) localOnWorldChange();
        onParentChange(parent);
    }
    
    public void onParentChange(Component parent){
        
    }
    
    public final void localOnWorldChange(){
        for(Component c : children) c.localOnWorldChange();
        
        onWorldChange();
    }
    
    public void onWorldChange(){
        
    }
    
    /**
     * Sets the local position offset of the object relative to the parent
     * @param npos New position offset
     * @return This object
     */
    public final Component setPositionOffset(Vector3f npos){
        this.posoffset = npos;
        regenPos();
        return this;
    }
    
    public void onPositionChange(Vector3f npos){
        
    }
    
    /**
     * Sets the local rotation offset of the object relative to the parent
     * @param nrot New rotation offset
     */
    public final void setRotationOffset(Quaternionf nrot){
        this.rotoffset = nrot;
        regenRot();
        for(Component c : children) if(!c.getPositionOffset().equals(new Vector3f())) c.regenPos();
        
    }
    
    public void onRotationChange(Quaternionf nrot){
        
    }
    
    /**
     * Sets the scaling offset of the object relative to the parent
     * @param nscale New scale offset
     */
    public final void setScale(Vector3f nscale){
        this.scale = nscale;
        
        onScaleChange(nscale);
    }
    
    public void onScaleChange(Vector3f nscale){
        
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
        return pos;
    }
    
    /**
     * Returns the current position offset of the component
     * @return Position offset of the component
     */
    public Vector3f getPositionOffset(){
        return posoffset;
    }
    
    private void regenPos(){
        if(parent != null){
            if(absoluteOffset){
                pos = parent.getPosition().add(posoffset);
            }else{
                pos = parent.getPosition().add(parent.getRotation().transform(posoffset));
            }
        }else{
            pos = posoffset;
        }     
        
        for(Component c : children) c.regenPos();
        
        onPositionChange(pos);
    }
    
    /**
     * Returns the final rotation of the current object, derived by multiplying the parent's rotation by the current offset
     * @return Final rotation direction
     */
    public Quaternionf getRotation(){
        return rot;
    }
    
    /**
     * Returns the current rotation offset of the component
     * @return Rotation offset of the component
     */
    public Quaternionf getRotationOffset(){
        return rotoffset;
    }
    
    private void regenRot(){
        if(parent != null){
            rot = parent.getRotation().multiply(rotoffset);
        }else{
            rot = rotoffset;
        }
        
        onRotationChange(rot);
        for(Component c : children) c.regenRot();
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
    public void update(float delta){}
    
    /**
     * Called by various sources for serialization of the component, and by default only serializes position, rotation, and scale offsets<br><br>
     * 
     * For correct functionality, the variables serialized here must match the variables deserialized in {@link #deserialize(com.opengg.core.world.Deserializer) deserialize()}<br>
     * In addition, any object that overrides this must also override the {@link #Component() default constructor} for the serializer to function<br><br>
     * 
     * It is recommended to allow for complete recreation of the object using these two methods
     * @param out Output stream used for writing objects to the buffer
     */
    public void serialize(GGOutputStream out) throws IOException{
        out.write(posoffset);
        out.write(rotoffset);
        out.write(scale);
        out.write(name);
        out.write(enabled);
        out.write(absoluteOffset);
        out.write(updatedistance);
    }
    
    /**
     * Called for deserialization of a byte stream to a component, and by default only deserializes position, rotation, and scale<br><br>
     * 
     * For correct functionality, variable deserialization here must match the variables serialized in {@link #serialize(com.opengg.core.world.Serializer) serialize()}<br>
     * In addition, any object that overrides this must also override the {@link #Component() default constructor} for the deserializer to function<br><br>
     * 
     * As this method is normally run on a separate thread, any methods that have OpenGL calls have to be run in an {@link com.opengg.core.engine.Executable executable} to be run in the main thread. <br>
     * It is recommended to allow for complete recreation of the object using these methods
     * @param in Input steam used for reading objects from the buffer
     */
    public void deserialize(GGInputStream in) throws IOException{
        posoffset = in.readVector3f();
        rotoffset = in.readQuaternionf();
        scale = in.readVector3f();
        name = in.readString();
        enabled = in.readBoolean();
        absoluteOffset = in.readBoolean();
        updatedistance = in.readInt();
    }
    
    /**
     * Returns the current world of this component
     * @return Current world
     */
    public World getWorld(){
        return parent.getWorld();
    }
      
    public boolean isEnabled(){
        return enabled;
    }
    
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    public boolean shouldSerialize(){
        return serialize;
    }
    
    public void setSerializable(boolean serialize){
        this.serialize = serialize;
    }
    
    public void finalizeComponent(){
        for(Component child : children){
            child.finalizeComponent();
        }
    }
    
    public Component attach(Component c) {
        if(c == this)
            return this;
        if(c.getParent() == this)
            return this;
        if(c.getParent() != null)
            c.getParent().remove(c);
        c.setParentInfo((Component)this);
        children.add(c);
        return this;
    }  
    
    public List<Component> getChildren(){
        return children;
    }
    
    public Component getParent(){
        return parent;
    }
    
    public void remove(int i){
        children.remove(i);
    }
    
    public void remove(Component w){
        children.remove(w);
    }
}
