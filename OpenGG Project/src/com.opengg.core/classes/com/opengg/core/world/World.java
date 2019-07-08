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
import com.opengg.core.render.RenderEnvironment;
import com.opengg.core.render.RenderGroup;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    private boolean forceUpdate = false;
    private boolean active = false;

    private List<Consumer<Component>> addSubs = new ArrayList<>();

    public World(){
        forceUpdate = GGInfo.isServer();
        setName("default");
    }

    public World(String name){
        this();
        setName(name);
    }


    /**
     * Regenerates the render groups for this world and sends them to {@link RenderEngine},
     * should only be used if adding many renderables. Otherwise, use {@link #addRenderable(com.opengg.core.world.components.RenderComponent) }
     */
    public void rescanRenderables(){
        for(Component c : getAllDescendants()){
            if(c instanceof RenderComponent){
                addRenderable((RenderComponent)c);
            }
        }
    }

    /**
     * Adds a {@link com.opengg.core.world.components.RenderComponent} to the World's {@link RenderGroup} depending on certain values,
     * and creates a new group if none that fit the components traits are found
     * @param renderable
     */
    public void addRenderable(RenderComponent renderable){
        boolean found = false;
        for(RenderGroup rg : environment.getGroups()){
            if(rg.isTransparent() == renderable.isTransparent()){
                if(rg.getPipeline().equals(renderable.getShader())){
                    if(rg.getFormat().equals(renderable.getFormat())){
                        if(!rg.getList().contains(renderable)){
                            rg.add(renderable);
                        }
                        found = true;
                        break;
                    }
                }
            }
        }

        if(!found){
            RenderGroup group = new RenderGroup("world " + getGUID() + " " + renderable.getShader() + " "
                    + renderable.getFormat().toString() +
                    " group: " + (environment.getGroups().size() + 1),
                    renderable.getFormat());
            group.add(renderable);
            group.setTransparent(renderable.isTransparent());
            group.setPipeline(renderable.getShader());
            environment.addGroup(group);
        }

        for(RenderGroup rg : environment.getGroups()){
            rg.setEnabled(true);
        }
    }

    /**
     * Removes a {@link com.opengg.core.world.components.RenderComponent} from the World's {@link RenderGroup}s
     * @param r RenderComponent to be removed
     */
    public void removeRenderable(RenderComponent r){
        for(RenderGroup rg : environment.getGroups()){
            rg.remove(r);
        }
    }

    /**
     * Returns the {@link com.opengg.core.physics.PhysicsSystem} associated with the World
     * @return PhysicsSystem associated with the world
     */
    public PhysicsSystem getSystem(){
        return physics;
    }

    /**
     * Recursively prints the Component layout of this world to the default {@link java.io.PrintStream}
     */
    public void printLayout(){
        GGConsole.log(printLayout(this, ""));
    }

    private String printLayout(Component comp, String string){
        string += comp.getClass().getSimpleName() + ": " + comp.getName() + "\n";
        for(var child : comp.getChildren()){
            string = "----" + printLayout(child, string);
        }
        return  string;
    }

    /**
     * Enables this world for use by the World Engine
     */
    public void setAsPrimary(){
        PhysicsEngine.setInstance(physics);
        RenderEngine.setCurrentEnvironment(environment);
        this.localOnWorldMadePrimary();
    }

    public void removeAsPrimary(){
        this.localOnWorldNoLongerPrimary();
    }

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
     * Gets the rendering environment used by all rendering related objects
     * @return
     */
    public RenderEnvironment getRenderEnvironment(){
        return environment;
    }

    public boolean isPrimaryWorld(){
        return this == WorldEngine.getCurrent();
    }

    public boolean isForcedUpdate() {
        return forceUpdate;
    }

    public void addNewChildListener(Consumer<Component> sub){
        this.addSubs.add(sub);
    }

    public void triggerNewChild(Component newChild){
        addSubs.forEach(c -> c.accept(newChild));
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
        out.write(environment.getSkybox() != null);
        if(environment.getSkybox() != null){
            for(TextureData data : environment.getSkybox().getCubemap().getData()){
                out.write(data.source);
            }
        }

        GGOutputStream out2 = new GGOutputStream();
        physics.serialize(out2);

        out.write(out2.asByteArray().length);
        out.write(out2.asByteArray());
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        var datums = new TextureData[6];
        boolean skybox = in.readBoolean();
        if(skybox){
            for (int i = 0; i < 6; i++) {
                var instring = in.readString();
                datums[i] = TextureManager.loadTexture(instring, false);
            }
        }

        OpenGG.asyncExec(() -> environment.setSkybox(new Skybox(Texture.create(Texture.cubemapConfig(), datums), 1000)));

        var size = in.readInt();
        var data = in.readByteArray(size);

        //physics.deserialize(new GGInputStream(data));
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
