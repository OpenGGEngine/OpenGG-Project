/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * ok mom
 */
package com.opengg.core.world.components;

import com.opengg.core.animation.ComponentVarAccessor;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;

import java.io.IOException;
import java.util.*;
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
 * code that should be needed to use the features described above, so take care when overriding these default methods.</p>
 * @author Javier
 */
public abstract class Component{
    private long guid;
    private String name = "default";

    private boolean enabled = true;
    private boolean canUpdate = true;
    private float updateDistance = 0;

    private Component parent;

    private Vector3f posoffset = new Vector3f();
    private Quaternionf rotoffset = new Quaternionf();
    private Vector3f scaleOffset = new Vector3f(1,1,1);
    private PositionType positionType = PositionType.RELATIVE;
    private boolean absoluteRotation = false;

    private Vector3f pos = new Vector3f();
    private Quaternionf rot = new Quaternionf();
    private Vector3f scale = new Vector3f(1,1,1);

    private boolean serialize = true;
    private boolean updateSerialize = true;
    private Runnable whenAttachedToWorld = null;
    private final Function<Component, Boolean> parentValidator = (c) -> true;
    protected List<Component> children = Collections.synchronizedList(new ArrayList<>());

    private boolean firstAttachment = true;

    static{
        ComponentVarAccessor.register(Component.class,Vector3f.class,"position",(BiConsumer<Component,Vector3f>)Component::setPositionOffset,(Function<Component,Vector3f>)Component::getPositionOffset);
        ComponentVarAccessor.register(Component.class,Quaternionf.class,"rotation",(BiConsumer<Component,Quaternionf>)Component::setRotationOffset,(Function<Component,Quaternionf>)Component::getRotationOffset);
        ComponentVarAccessor.register(Component.class,Vector3f.class,"scale",(BiConsumer<Component,Vector3f>)Component::setScaleOffset,(Function<Component,Vector3f>)Component::getScale);
    }

    /**
     * Creates a component with a new ID
     */
    public Component(){
        guid = UUID.randomUUID().getLeastSignificantBits();

        name = String.valueOf(guid);

        if(OpenGG.getDebugOptions().logOnComponentCreation()){
            GGConsole.debug("Created component of type " + this.getClass().getName() + " with ID "  + this.guid);
        }
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
     * <p>
     * This method should be prioritized for identification of components over {@link Component#getGUID()}, as
     * certain cases of serialization and deserialization may not guarantee that the GUID is saved.
     * @return the component's name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Gets the component's unique ID
     * @return component's ID
     */
    public long getGUID(){
        return guid;
    }

    /**
     * Sets the component's uni
     * @param guid
     */
    public void setGUID(long guid) {
        this.guid = guid;
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
            if(positionType == PositionType.ABSOLUTE_RELATIVE){
                pos = parent.getPosition().add(posoffset);
            }else if (positionType == PositionType.ABSOLUTE){
                pos = posoffset;
            }else if(posoffset.equals(Vector3f.identity)){
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
        return this.setRotationOffset(Quaternionf.createYXZ(nrot));
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
        return this;
    }

    /**
     * Sets whether or not the rotation offset of this component should be treated as the actual rotation or as an offset
     * @param absolute
     * @return
     */
    public final Component setAbsoluteRotation(boolean absolute){
        absoluteRotation = absolute;
        regenRot();
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
        if(parent != null && !absoluteRotation){
            rot = parent.getRotation().multiply(rotoffset);
        }else{
            rot = rotoffset;
        }
        onRotationChange(rot);
        for(Component c : children) c.regenRot();
        for(Component c : children) if(!c.getPositionOffset().equals(new Vector3f())) c.regenPos();
    }
    
    /**
     * Sets the scaling offset of the component relative to the parent
     * @param nscale New scale offset
     * @return This component
     */
    public final Component setScaleOffset(Vector3f nscale){
        this.scaleOffset = nscale;
        regenScale();
        return this;
    }

    /**
     * Sets the scaling offset of the component relative to the parent
     * @param scale New scale offset
     * @return
     */
    public final Component setScaleOffset(float scale){
        return this.setScaleOffset(new Vector3f(scale));
    }
    
    private void regenScale(){
        if(parent != null){
            scale = parent.getScale().multiply(scaleOffset);
        }else{
            scale = scaleOffset;
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

    /**
     * Gets the scale offset for this component
     * @return
     */
    public Vector3f getScaleOffset(){
        return scaleOffset;
    }
    
    /**
     * Set the behavior of the position given to this component
     * @param type Position behavior for this component
     */
    public void setPositionType(PositionType type){
        this.positionType = type;
    }
    
    /**
     * Returns the usage type for this component's position
     */
    public PositionType getPositionType(){
        return positionType;
    }

    /**
     * Returns the update distance of this component
     * @return
     */
    public float getUpdateDistance() {
        return updateDistance;
    }

    /**
     * Sets the update distance of this component, being in units away from the current camera
     * @param updatedistance
     */
    public void setUpdateDistance(float updatedistance) {
        this.updateDistance = updatedistance;
    }

    public final void localUpdate(float delta){
        if(!canUpdate || !isEnabled() || (getUpdateDistance() != 0 && getUpdateDistance()*getUpdateDistance() < getPosition().distanceToSquared(RenderEngine.getCurrentView().getPosition())))
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
     * For correct functionality, the variables serialized here must match the variables deserialized in {@link #deserialize(GGInputStream)} ) deserializeWorld()}<br>
     * In addition, any component that overrides this must also override the {@linkplain #Component() default constructor} for the serializer to function<br><br>
     * 
     * It is recommended to allow for complete recreation of the component using these two methods
     * @param out Output stream used for writing components to the buffer
     */
    public void serialize(GGOutputStream out) throws IOException{
        out.write(posoffset);
        out.write(rotoffset);
        out.write(scaleOffset);
        out.write(name);
        out.write(enabled);
        out.write(positionType.name());
        out.write(absoluteRotation);
        out.write(updateDistance);
    }
    
    /**
     * Called for deserialization of a byte stream to a component<br><br>
     * 
     * For correct functionality, variable deserialization here must match the variables serialized in {@link #serialize(GGOutputStream)} ) serializeWorld()}<br>
     * In addition, any component that overrides this must also override the {@link #Component() default constructor} for the deserializer to function<br><br>
     * 
     * As this method is normally run on a separate thread, any methods that have OpenGL calls have to be run in an {@link Runnable Runnable} to be run in the main thread. <br>
     * It is recommended to allow for complete recreation of the component using these methods
     * @param in Input steam used for reading components from the buffer
     */
    public void deserialize(GGInputStream in) throws IOException{
        posoffset = in.readVector3f();
        rotoffset = in.readQuaternionf();
        scaleOffset = in.readVector3f();
        name = in.readString();
        enabled = in.readBoolean();
        positionType = PositionType.valueOf(in.readString());
        absoluteRotation = in.readBoolean();
        updateDistance = in.readInt();

        regenPos();
        regenRot();
        regenScale();
    }

    /**
     * Called once when this component's parent world is loaded from a file or stream
     */
    public void onWorldLoad(){

    }

    /**
     * Called for serialization of components while being updated in a networked application
     * <p>
     * This should be able to fully recreate any state in the component that may change, although it can be assumed
     * that the component is in a functional state before this is called.
     * <p>
     * As components are serialized constantly during the runtime of an application, care should be taken to ensure
     * that the output of this method is the smallest possible needed to recreate the component to save bandwidth.
     * @param out
     * @throws IOException
     */
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

    /**
     * Called for deserialization of components while being updated in a networked application
     * @param in
     * @param delta
     * @throws IOException
     */
    public void deserializeUpdate(GGInputStream in, float delta) throws IOException{
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
     *  If the component is marked for serialization, eg for world saving or networking
     * @return If should serializeWorld
     */
    public final boolean shouldSerialize(){
        return serialize;
    }

    /**
     * Sets if serialization should occur, eg for world saving and loading
     * @param serialize Should serializeWorld
     */
    public final Component setSerializable(boolean serialize){
        this.serialize = serialize;
        return this;
    }

    /**
     * Returns if this component should be serialized for updating <br>
     *     This does not affect whether this component is serialized for creation
     */
    public boolean shouldSerializeUpdate() {
        return updateSerialize;
    }

    /**
     * Sets if this component should be serialized for updating <br>
     *     This does not affect whether this component is serialized for creation
     * @param updateSerialize
     */
    public void setSerializableUpdate(boolean updateSerialize) {
        this.updateSerialize = updateSerialize;
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
     * Returns if the component is currently enabled for rendering and updating
     * @return if the component is currently enabled
     */
    public boolean isAllEnabled(){
        return enabled && this.getParent().isAllEnabled();
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

    public Component setUpdateEnabled(boolean update){
        this.canUpdate = update;
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

    private void localFinalizeComponent(){
        for(var comp : children)
            comp.localFinalizeComponent();
        this.finalizeComponent();
    }

    /**
     * Called when a component is removed, override if needed. It should not be called directly
     */
    public void finalizeComponent(){

    }

    public final void delete(){
        OpenGG.asyncExec(() -> {
            this.localFinalizeComponent();
            this.getParent().remove(this);
            WorldEngine.onComponentRemoved(this);
        });
    }
    /**
     * Attaches a component to this component<br>
     * This contains checks to prevent a component to be attached to itself. Additionally, it goes
     * through the cleanup of removing a component from its existing parent, and calls {@link #changeParent(com.opengg.core.world.components.Component) }
     * @param component Component to be attached
     * @return This component
     */
    public Component attach(Component component) {
        if(!component.parentValidator.apply(this))
            throw new IllegalArgumentException(this.getClass().getCanonicalName() + " failed to attach " + component.getClass().getName());
        if(component == this)
            return this;
        if(component.getParent() == this)
            return this;
        if(component.getParent() != null)
            component.getParent().remove(component);

        component.changeParent(this);
        children.add(component);

        return this;
    }

    private void changeParent(Component parent){
        if(parent == null){
            this.delete();
            return;
        }

        WorldEngine.onComponentMoved(this, parent);

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

        if(firstAttachment){
            firstAttachment = false;
            WorldEngine.onComponentAdded(this);
        }

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

    public final void localOnWorldMadePrimary(){
        for(var child : children){
            child.localOnWorldMadePrimary();
        }
        this.onWorldMadePrimary();
    }

    public final void localOnWorldNoLongerPrimary(){
        for(var child : children){
            child.localOnWorldNoLongerPrimary();
        }
        this.onWorldNoLongerPrimary();
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
     * Called when this component's world is made into the primary world
     */
    public void onWorldMadePrimary(){

    }

    /**
     * Called when this component's world is no longer the primary world
     */
    public void onWorldNoLongerPrimary(){

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
        return children;//Collections.unmodifiableList(children);
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
     * Finds the first {@link com.opengg.core.world.components.Component} with a certain simple name, returns {@code null} if none are found
     * @param name Name being searched for
     * @return Component with given name or {@code null}  if nonexistent
     */
    public List<Component> findByName(String name){
        return getAllDescendants()
                .stream()
                .filter(c -> c.getName().equals(name))
                .collect(Collectors.toList());
    }

    /**
     * Finds the first direct child {@link com.opengg.core.world.components.Component} with a certain simple name, returns {@code null} if none are found
     * @param name Name being searched for
     * @return Component with given name or {@code null}  if nonexistent
     */
    public List<Component> findChildByName(String name){
        return getChildren()
                .stream()
                .filter(c -> c.getName().equals(name))
                .collect(Collectors.toList());
    }

    /**
     * Returns if there is any descendant with the name given
     * @param name
     * @return
     */
    public boolean hasDescendantWithName(String name){
        return getAllDescendants()
                .stream()
                .filter(c -> c.getName().equals(name))
                .findFirst().isPresent();
    }

    /**
     * Returns if there is any direct child with the name given
     * @param name
     * @return
     */
    public boolean hasChildWithName(String name){
        return getChildren()
                .stream()
                .filter(c -> c.getName().equals(name))
                .findFirst().isPresent();
    }

    /**
     * Finds the first {@link com.opengg.core.world.components.Component} with a certain GUID in this component's hierarchy, returns {@code null} if none are found
     * @param guid GUID being searched for
     * @return Component with given GUID if nonexistent
     */
    public Optional<Component> findByGUID(long guid){
        return getAllDescendants()
                .stream()
                .filter(c -> c.getGUID() == guid)
                .findFirst();
    }

    /**
     * Finds the first direct child {@link com.opengg.core.world.components.Component} with a certain GUID, returns {@code null} if none are found
     * @param guid GUID being searched for
     * @return Component with given GUID if nonexistent
     */
    public Optional<Component> findChildByGUID(long guid){
        return getChildren()
                .stream()
                .filter(c -> c.getGUID() == guid)
                .findFirst();
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
        if(!children.contains(child)) {
            return;
        }
        children.remove(child);
        child.changeParent(null);
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + ": " + this.getName();
    }

    public enum PositionType{
        RELATIVE, ABSOLUTE_RELATIVE, ABSOLUTE
    }
}
