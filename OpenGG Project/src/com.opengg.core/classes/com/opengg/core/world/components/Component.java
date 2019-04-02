/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.animation.ComponentVarAccessor;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>Represents any object attachable to another Component that links with the WorldEngine for updating and serializing</h1>
 * <p>
 * This class is the core class of the World system, which is what a developer would interact the most with during development. All component
 * have a position, scale, and rotation, regardless of whether or not they are used. In addition they have a unique ID, a name which may
 * or may not be unique depending on developer preference, and only one parent, and if it this is a top level component, the parent would be the 
 * corresponding World. </p>
 * <p>
 * To create a custom component, first extend this class. This class, while lacking practical use by itself, contains all of the
 * code that should be needed to use the features described above, so take care when overriding these default classes.</p>
 * @author Javier
 */
public abstract class Component{
    private static int curid = 0;
    private int id;
    private boolean absoluteOffset = false;
    private boolean enabled = true;
    private float updatedistance = 0;
    private String name = "default";
    private Component parent;
    private Vector3f posoffset = new Vector3f();
    private Quaternionf rotoffset = new Quaternionf();
    private Vector3f scaleoffset = new Vector3f(1,1,1);
    protected List<Component> children = Collections.synchronizedList(new ArrayList<>());
    private Vector3f pos = new Vector3f();
    private Quaternionf rot = new Quaternionf();
    private Vector3f scale = new Vector3f(1,1,1);
    private boolean serialize = true;
    private Runnable whenAttachedToWorld = null;

    static{
        ComponentVarAccessor.register(Component.class,Vector3f.class,"position",(BiConsumer<Component,Vector3f>)Component::setPositionOffset,(Function<Component,Vector3f>)Component::getPositionOffset);
        ComponentVarAccessor.register(Component.class,Quaternionf.class,"rotation",(BiConsumer<Component,Quaternionf>)Component::setRotationOffset,(Function<Component,Quaternionf>)Component::getRotationOffset);
        ComponentVarAccessor.register(Component.class,Vector3f.class,"scale",(BiConsumer<Component,Vector3f>)Component::setScaleOffset,(Function<Component,Vector3f>)Component::getScale);

    }

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
    public Component setName(String name){
        this.name = name;
        return this;
    }
    
    /**
     * Get the component's readable name
     * @return the component's name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Gets the component's unique ID
     * @return component's ID
     */
    public int getId(){
        return id;
    }
    
    
    /**
     * Sets the component's new ID, should only be used in very specific circumstances. If component identification 
     * is needed, use {@link #setName(java.lang.String) } and {@link  #getName() } instead
     * @param id component's new ID
     */
    public void setId(int id){
        this.id = id;
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

    public final Component setPositionOffset(float x, float y, float z){
        return setPositionOffset(new Vector3f(x,y,z));
    }
    
    /**
     * Called when the position of a component is changed, override to do something when this happens<br>
     * Note, this happens whenever the true position is changed, not just the offset, so it will be called if the position
     * of the parent of this component is changed
     * @param npos New true position of this component
     */
    public void onPositionChange(Vector3f npos){
        
    }

    /**
     * Returns the true position of the component<br>
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
            }else if(posoffset.equals(Vector3f.identity)) {
                pos = parent.getPosition();
            }else{
                pos = parent.getPosition().add(parent.getRotation().transform(posoffset).multiply(parent.getScale()));
            }
        }else{
            pos = posoffset;
        }
        
        for(Component c : children) c.regenPos();
        
        onPositionChange(pos);
    }
    
    /**
     * Returns the true rotation of this component
     * @return Final rotation direction
     */
    public Quaternionf getRotation(){
        return rot;
    }
    
    /**
     * Sets the local rotation offset of the component relative to the parent, in a euclidean vector
     * @param nrot New rotation offset
     * @return This component
     */
    public final Component setRotationOffset(Vector3f nrot){
        return this.setRotationOffset(new Quaternionf(nrot));
    }
    
    /**
     * Returns the current rotation offset of the component
     * @return Rotation offset of the component
     */
    public Quaternionf getRotationOffset(){
        return rotoffset;
    }
    
    /**
     * Sets the local rotation offset of the component relative to the parent
     * @param nrot New rotation offset
     * @return This component
     */
    public final Component setRotationOffset(Quaternionf nrot){
        this.rotoffset = nrot;
        regenRot();
        for(Component c : children) if(!c.getPositionOffset().equals(new Vector3f())) c.regenPos();
        return this;
    }
    
    /**
     * Called when the rotation of a component is changed, override to do something when this happens<br>
     * Note, this happens whenever the true rotation is changed, not just the offset, so it will be called if the rotation
     * of the parent of this component is changed
     * @param nrot New true rotation of this component
     */
    public void onRotationChange(Quaternionf nrot){
        
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
     * Sets the scaling offset of the component relative to the parent
     * @param nscale New scale offset
     * @return This component
     */
    public final Component setScaleOffset(Vector3f nscale){
        this.scaleoffset = nscale;
        regenScale();
        return this;
    }

    public final Component setScaleOffset(float scale){
        return this.setScaleOffset(new Vector3f(scale));
    }
    
    private void regenScale(){
        if(parent != null){
            scale = parent.getScale().multiply(scaleoffset);
        }else{
            scale = scaleoffset;
        }

        onScaleChange(scale);
        regenPos();
        for(Component c : children) c.regenScale();
    }
    
    
    /**
     * Called when the scale of a component is changed, override to do something when this happens<br>
     * Note, this happens whenever the true scale is changed, not just the offset, so it will be called if the scale
     * of the parent of this component is changed
     * @param nscale New true scale of this component
     */
    public void onScaleChange(Vector3f nscale){
        
    }
    
    /**
     * Gets the true scaling factor of this component
     * @return Final component scale
     */
    public Vector3f getScale(){
        return scale;
    }

    public Vector3f getScaleOffset(){
        return scaleoffset;
    }
    
    /**
     * Set whether the position offset should be absolute or take the rotation of the parent into account
     * @param abs True for absolute, false for relative
     */
    public void setAbsoluteOffset(boolean abs){
        absoluteOffset = abs;
    }
    
    /**
     * Returns whether or not absolute offset is enabled
     * @return Current state of absolute offset
     */
    public boolean isAbsoluteOffset(){
        return absoluteOffset;
    }

    /**
     * Returns the update distance of this component
     * @return
     */
    public float getUpdateDistance() {
        return updatedistance;
    }

    /**
     * Sets the update distance of this component, being in units away from the current camera
     * @param updatedistance
     */
    public void setUpdateDistance(float updatedistance) {
        this.updatedistance = updatedistance;
    }

    public final void localUpdate(float delta){
        if(getWorld() != WorldEngine.getCurrent()) return;
        if(!getWorld().isForcedUpdate()
                &&
                (!isEnabled()
                        ||
                        (getUpdateDistance()*getUpdateDistance() < getPosition().distanceToSquared(RenderEngine.getCurrentView().getPosition())
                                &&
                                getUpdateDistance() != 0))
        )
            return;
        update(delta);
        for(Component c2 : getChildren()){
            c2.localUpdate(delta);
        }
    }

    /**
     * Called once per update cycle, override for functionality
     * @param delta Delta time since last update cycle in seconds
     */
    public void update(float delta){}
    
    /**
     * Called by various sources for serialization of the component, and by default only serializes position, rotation, and scale offsets<br><br>
     * 
     * For correct functionality, the variables serialized here must match the variables deserialized in {@link #deserialize(GGInputStream)} ) deserialize()}<br>
     * In addition, any component that overrides this must also override the {@linkplain #Component() default constructor} for the serializer to function<br><br>
     * 
     * It is recommended to allow for complete recreation of the component using these two methods
     * @param out Output stream used for writing components to the buffer
     */
    public void serialize(GGOutputStream out) throws IOException{
        out.write(posoffset);
        out.write(rotoffset);
        out.write(scaleoffset);
        out.write(name);
        out.write(enabled);
        out.write(absoluteOffset);
        out.write(updatedistance);
    }
    
    /**
     * Called for deserialization of a byte stream to a component, and by default only deserializes position, rotation, and scale<br><br>
     * 
     * For correct functionality, variable deserialization here must match the variables serialized in {@link #serialize(GGOutputStream)} ) serialize()}<br>
     * In addition, any component that overrides this must also override the {@link #Component() default constructor} for the deserializer to function<br><br>
     * 
     * As this method is normally run on a separate thread, any methods that have OpenGL calls have to be run in an {@link Runnable Runnable} to be run in the main thread. <br>
     * It is recommended to allow for complete recreation of the component using these methods
     * @param in Input steam used for reading components from the buffer
     */
    public void deserialize(GGInputStream in) throws IOException{
        posoffset = in.readVector3f();
        rotoffset = in.readQuaternionf();
        scaleoffset = in.readVector3f();
        name = in.readString();
        enabled = in.readBoolean(); enabled = true;
        absoluteOffset = in.readBoolean();
        updatedistance = in.readInt();
    }

    public void onWorldLoad(){

    }

    public void serializeUpdate(GGOutputStream out) throws IOException{
        var set = new BitSet();
        set.set(0,true);
        set.set(1,true);
        set.set(2,false);
        set.set(3,enabled);

        out.write(set.toByteArray()[0]);
        out.write(posoffset);
        out.write(rotoffset);
        //out.write(scaleoffset);
    }

    public void deserializeUpdate(GGInputStream in) throws IOException{
        var set = BitSet.valueOf(new byte[]{in.readByte()});
        var getpos = set.get(0);
        var getrot = set.get(1);
        var getscale = set.get(2);
        enabled = set.get(3);

        if(getpos) setPositionOffset(in.readVector3f());
        if(getrot) setRotationOffset(in.readQuaternionf());
        if(getscale) setScaleOffset(in.readVector3f());
    }
    
    /**
     * Returns the current world of this component
     * @return Current world
     */
    public World getWorld(){
        if(parent == null) return null;
        return parent.getWorld();
    }
    
    /**
     * Returns if the component is currently enabled for rendering and updating
     * @return if the component is currently enabled
     */
    public boolean isEnabled(){
        return enabled;
    }
    
    /**
     * Sets if the component should be currently enabled for rendering and updating
     * @param enabled if the component should be currently enabled 
     */
    public Component setEnabled(boolean enabled){
        if(enabled)
            localOnEnable();
        else
            localOnDisable();
        this.enabled = enabled;
        return this;
    }

    private void localOnEnable(){
        onEnable();
        for(Component c : children) c.localOnEnable();
    }

    private void localOnDisable(){
         onDisable();
        for(Component c : children) c.localOnDisable();
    }

    /**
     * Called when this component is enabled <br>
     *     This includes if its world is changed or loaded, or if {@code setEnabled()} is set to true
     */
    public void onEnable(){

    }

    /**
     * Called when this component is disabled <br>
     *     This includes if its world is unloaded, or if {@code setEnabled()} is set to false
     */
    public void onDisable(){

    }
    
    /**
     *  If the component is marked for serialization, eg for world saving or networking
     * @return If should serialize
     */
    public final boolean shouldSerialize(){
        return serialize;
    }
    
    /**
     * Sets if serialization should occur, eg for world saving and loading
     * @param serialize Should serialize
     */
    public final void setSerializable(boolean serialize){
        this.serialize = serialize;
    }
    
    /**
     * Called when a component is removed, override if needed. It should not be called directly
     */
    public void finalizeComponent(){

    }

    /**
     * Attaches a component to this component<br>
     * This contains checks to prevent a component to be attached to itself. Additionally, it goes
     * through the cleanup of removing a component from its existing parent, and calls {@link #changeParent(com.opengg.core.world.components.Component) }
     * @param c Component to be attached
     * @return This component
     */
    public Component attach(Component c) {
        if(c == this)
            return this;
        if(c.getParent() == this)
            return this;
        if(c.getParent() != null)
            c.getParent().remove(c);

        c.changeParent(this);
        children.add(c);

        return this;
    }

    private void changeParent(Component parent){
        if(parent == null){
            WorldEngine.markForRemoval(this);
            return;
        }

        if(getWorld() != parent.getWorld() && parent.getWorld() != null){
            this.parent = parent;
            localOnWorldChange();
        }

        this.parent = parent;

        regenPos();
        regenRot();
        regenScale();

        onParentChange(parent);
    }

    /**
     * Called when the parent of this component is changed, override to do something on parent change
     * @param parent The new parent of this parent
     */
    public void onParentChange(Component parent){

    }

    public final void localOnWorldChange(){
        for(Component c : children) c.localOnWorldChange();
        onWorldChange();
        parent.getWorld().triggerNewChild(this);
        if(WorldEngine.getCurrent() == this.getWorld() && this.getWorld().isEnabled()) localOnWorldEnable();
        if(whenAttachedToWorld != null){
            whenAttachedToWorld.run();
        }
    }

    public final void localOnWorldEnable(){
        for(Component c : children) c.localOnWorldEnable();
        onWorldEnable();
        if(isEnabled()) onEnable();
    }

    public final void localOnWorldDisable(){
        for(Component c : children) c.localOnWorldDisable();
        onWorldDisable();
        if(isEnabled()) onDisable();
    }

    /**
     * Called when the world of this component is enabled. <br>
     *     This is also called if the component is initially attached to a world that is active
     */
    public void onWorldEnable(){

    }

    /**
     * Called when the world of this component is disabled.
     */
    public void onWorldDisable(){

    }

    /**
     * Called when the world of this component is changed, override to do something on world change<br>
     * Note, this is also called if the parent changes and the parent is in a new world, including the
     * first time the parent is changed
     */
    public void onWorldChange(){
    }

    /**
     * Lambda version of {@link #onWorldChange()}
     * @param onChange Runnable to run on world change
     */
    public final void onWorldChange(Runnable onChange){
        this.whenAttachedToWorld = onChange;
    }

    /**
     * Returns a list containing this component's direct children
     * @return
     */
    public List<Component> getChildren(){
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns all descendants of this component\n
     * This returns all components that are included as either children of this component or descendants of this component's children
     * @return List of descendants of this component
     */
    public List<Component> getAllDescendants(){
        return Stream.concat(children.stream()
                .flatMap(child -> child.getAllDescendants().stream()),
                    children.stream())
                .collect(Collectors.toList());
    }

    /**
     * Returns this component's parent, or null it has none
     * @return
     */
    public Component getParent(){
        return parent;
    }
    
    /**
     * Removes a component from this component's child list by list index
     * @param i Index of component
     */
    public void remove(int i){
        children.remove(i);
    }

    /**
     * Removes all components from this component's child list
     */
    public void removeAll(){
        for(var child : children){
            child.changeParent(null);
        }

        children.clear();
    }
    
    /**
     * Removes a component from this component's child list
     * @param child Component to be removed
     */
    public void remove(Component child){
        if(child == null) return;
        children.remove(child);
        child.changeParent(null);
    }

    public static int getCurrentIdCounter(){
        return curid;
    }

    public static void setCurrentIdCounter(int id){
        curid = id;
    }
}
