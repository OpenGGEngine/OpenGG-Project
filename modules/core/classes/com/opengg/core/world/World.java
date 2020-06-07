/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.physics.PhysicsEngine;
import com.opengg.core.physics.PhysicsSystem;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.RenderEnvironment;
import com.opengg.core.render.RenderGroup;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.structure.WorldStructure;

import java.io.IOException;

/**
 * <h1>Represents a world, the highest level object in the component/world system</h1>
 *
 * It contains all of the information required to run a specific instance of the world system.
 * This includes the related instance of the physics engine and the render environment for this specific world
 * <p>
 * Note, while this is a subclass of component for the purpose of implementation and abstraction, many functions related to
 * positioning and parents either return default values or throw errors, an example of the latter
 * being {@link Component#getParent()} ). Otherwise,
 * commands like {@link Component#attach(Component)}  }work as intended
 * @author Javier
 */
public class World extends Component implements Resource {
    private PhysicsSystem physics = new PhysicsSystem();
    private RenderEnvironment environment = new RenderEnvironment();
    private WorldStructure structure;
    private boolean forceUpdate = false;
    private boolean active = false;
    private boolean shouldMultipleInstancesExist = false;
    private boolean shouldSerializePhysics = false;

    public World(){
        forceUpdate = GGInfo.isServer();
        structure = new WorldStructure(this);
        setName("default");
    }

    public World(String name){
        this();
        setName(name);
    }

    /**
     * Adds a {@link com.opengg.core.world.components.RenderComponent} to the World's {@link RenderGroup} depending on certain values,
     * and creates a new group if none that fit the components traits are found
     * @param renderable
     */
    public void addRenderable(RenderComponent renderable){
        if(GGInfo.isServer()) return;
        boolean found = false;
        for(RenderGroup rg : environment.getGroups()){
            if (rg.isTransparent() == renderable.isTransparent() && rg.getPipeline().equals(renderable.getShader()) && rg.getFormat().equals(renderable.getFormat())) {
                if (!rg.getList().contains(renderable)) {
                    rg.add(renderable);
                }else{
                    found = true;
                    break;
                }
            }
        }

        if(!found){
            OpenGG.asyncExec(() -> {
                RenderGroup group = new RenderGroup("world " + getGUID() + " " + renderable.getShader() + " "
                            + renderable.getFormat().toString() + " group: " + (environment.getGroups().size() + 1),
                        renderable.getFormat());
                group.add(renderable);
                group.setTransparent(renderable.isTransparent());
                group.setPipeline(renderable.getShader());
                group.setEnabled(true);
                environment.addGroup(group);
            });
        }
    }

    /**
     * Removes a {@link com.opengg.core.world.components.RenderComponent} from the World's {@link RenderGroup}s
     * @param r RenderComponent to be removed
     */
    public void removeRenderable(Renderable r){
        for(RenderGroup rg : environment.getGroups()){
            rg.remove(r);
        }
    }

    /**
     * Recursively prints the Component layout of this world to the default {@link java.io.PrintStream}
     */
    public void printLayout(){
        GGConsole.log("\n" + printLayout(this, 1));
    }

    private String printLayout(Component comp, int tabCount){
        var string = comp.getClass().getSimpleName() + ": " + comp.getName() + " (" + comp.getGUID() + ")\n";
        for(var child : comp.getChildren()){
            string += " ".repeat(tabCount*3) + printLayout(child, tabCount + 1) + "";
        }
        return  string;
    }

    /**
     * Sets this world to be the primary world for the World Engine <br>
     *     The primary world is the world that is currently being rendered. Additionally,
     *     certain components {@link com.opengg.core.world.components.ControlledComponent such as ControlledComponent}
     *     can have differing functionality depending on what world is set as primary. For most components, however,
     *     there is no difference, meaning that what world is set as primary on a server is mostly meaningless
     */
    public void setAsPrimary(){
        PhysicsEngine.setInstance(physics);
        RenderEngine.setCurrentEnvironment(environment);
        this.localOnWorldMadePrimary();
    }

    /**
     * Changes this world to no longer be considered as the primary world
     */
    public void removeAsPrimary(){
        this.localOnWorldNoLongerPrimary();
    }

    /**
     * Returns whether or not this world is set as the primary
     * @return
     */
    public boolean isPrimaryWorld(){
        return this == WorldEngine.getCurrent();
    }

    /**
     * Enables the world for use
     */
    public void activate(){
        this.active = true;
        this.setEnabled(true);
        this.localOnWorldEnable();
    }

    /**
     * Safely deactivates the world
     */
    public void deactivate(){
        this.active = false;
        this.setEnabled(false);
        this.localOnWorldDisable();
    }

    /**
     * Returns if this World is currently active in the world system, whether or not it is updating
     */
    public boolean isActive(){
        return active;
    }

    /**
     * Gets if multiple instances of this world should be allowed to exist concurrently. <br>
     *     If this is {@code true}, this world is referenced by the format name + @ + GUID. For example, if
     *     this world was loaded from a file named foo.bwf, this world, once loaded, would be aquired by calling
     *     {@code WorldEngine.getWorld("foo.bwf@GU82l1SI1")}. This is to allow for multiple of the same world to exist
     *     at the same time.<br>
     *     If this is {@code false}, this world is referenced by its name only, with no GUID. For example, a world from
     *     a file named foo.bwf would be gotten by calling {@code WorldEngine.getWorld("foo.bwf")}
     *
     * @return
     */
    public boolean shouldMultipleInstancesExist() {
        return shouldMultipleInstancesExist;
    }

    /**
     * Sets if multiple instances of this world should be allowed to exist concurrently
     * @param val
     * @see World#shouldMultipleInstancesExist()
     */
    public void setShouldMultipleInstancesExist(boolean val){
        this.shouldMultipleInstancesExist = val;
    }

    public boolean shouldSerializePhysics() {
        return shouldSerializePhysics;
    }

    public void setShouldSerializePhysics(boolean shouldSerializePhysics) {
        this.shouldSerializePhysics = shouldSerializePhysics;
    }

    public boolean isForcedUpdate() {
        return forceUpdate;
    }

    /**
     * Gets the rendering environment used by all rendering related objects
     * @return
     */
    public RenderEnvironment getRenderEnvironment(){
        return environment;
    }

    /**
     * Returns the {@link com.opengg.core.physics.PhysicsSystem} associated with the World
     * @return PhysicsSystem associated with the world
     */
    public PhysicsSystem getSystem(){
        return physics;
    }

    public WorldStructure getStructure() {
        return structure;
    }

    /**
     * Overrides {@link com.opengg.core.world.components.Component#getPosition()}, always returns 0,0,0
     * @return returns Vector3f containing 0,0,0
     */
    @Override
    public Vector3f getPosition(){
        return new Vector3f();
    }

    /**
     * Overrides {@link com.opengg.core.world.components.Component#getRotation()}, always returns a unit quaternion
     * @return returns a Quaternionf representing no rotation
     */
    @Override
    public Quaternionf getRotation(){
        return new Quaternionf();
    }

    /**
     * Overrides {@link com.opengg.core.world.components.Component#getScale()}, always returns 1,1,1
     * @return returns Vector3f containing 1,1,1
     */
    @Override
    public Vector3f getScale(){
        return new Vector3f(1,1,1);
    }

    @Override
    public boolean isAllEnabled() {
        return isEnabled();
    }

    /**
     * Overrides {@link Component#onParentChange(Component)}, throws an exception to
     * prevent attachment to any other components
     * @throws com.opengg.core.exceptions.InvalidParentException
     */
    @Override
    public void onParentChange(Component parent) {
        throw new InvalidParentException("World must be the top level component!");
    }

    @Override
    public void update(float delta){

    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(shouldMultipleInstancesExist);

        out.write(environment.getSkybox() != null);
        if(environment.getSkybox() != null){
            for(TextureData data : environment.getSkybox().getCubemap().getData()){
                out.write(data.source);
            }
        }

        GGOutputStream out3 = new GGOutputStream();
        structure.serialize(out3);

        out.write(out3.asByteArray().length);
        out.write(out3.asByteArray());

        if(!shouldSerializePhysics) return;
        GGOutputStream out2 = new GGOutputStream();
        physics.serialize(out2);

        out.write(out2.asByteArray().length);
        out.write(out2.asByteArray());
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        shouldMultipleInstancesExist = in.readBoolean();

        var datums = new TextureData[6];
        boolean skybox = in.readBoolean();
        if(skybox){
            for (int i = 0; i < 6; i++) {
                var instring = in.readString();
                datums[i] = Resource.getTextureData(instring);
            }
            OpenGG.asyncExec(() -> environment.setSkybox(new Skybox(Texture.create(Texture.cubemapConfig(), datums), 1000)));
        }

        var size2 = in.readInt();
        var data2 = in.readByteArray(size2);

        structure.deserialize(data2);
        OpenGG.onMainThread(() -> structure.remakeRenderGroups());
        if(in.available() <= 0) return;

        var size = in.readInt();
        var data = in.readByteArray(size);

        physics.deserialize(new GGInputStream(data));
    }

    /**
     * Overrides {@link com.opengg.core.world.components.Component#getWorld()}, returns this world
     * @return returns this world
     */
    @Override
    public World getWorld(){
        return this;
    }

    @Override
    public Type getType() {
        return Type.WORLD;
    }

    @Override
    public String getSource() {
        return "";
    }
}
