package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.server.ConnectionListener;
import com.opengg.core.network.server.Server;
import com.opengg.core.engine.Resource;
import com.opengg.core.network.server.ServerClient;
import com.opengg.core.render.text.Text;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.SphereCollider;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.text.impl.GGFont;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.components.*;
import com.opengg.core.world.components.physics.PhysicsComponent;

import java.util.ArrayDeque;
import java.util.Queue;


public class OpenGGTestServer extends GGApplication{
    private GGFont font;
    private Text text;
    private TerrainComponent world;
    private Texture worldterrain;
    private AudioListener listener;
    float i = 0;
    private FreeFlyComponent player;
    Queue<Component> components = new ArrayDeque<>();

    public static void main(String[] args){
        OpenGG.initializeHeadless(new OpenGGTestServer());
    }

    @Override
    public  void setup(){
        WorldEngine.getCurrent().attach(new LightComponent(
                Light.createDirectionalShadow(new Vector3f(0,0,0),
                        new Quaternionf(new Vector3f(0,0,0)),
                        new Vector3f(1,1,1),
                        400,
                        Matrix4f.orthographic(-100,100,-100,100,0,300),
                        4096, 4096)));
        WorldEngine.getCurrent().attach(new ModelRenderComponent(Resource.getModel("goldleaf")).setScaleOffset(new Vector3f(0.02f)).setRotationOffset(new Vector3f(90,0,0)));
        for (int i = 0; i < 2; i++) {
            PhysicsComponent sphere = new PhysicsComponent();
            sphere.getEntity().setPosition(new Vector3f(120f * (float)Math.random(), (float)Math.random() * 40f + 20, (float)Math.random() * 120f));
            sphere.addCollider(new ColliderGroup(new AABB( 3, 3, 3),  new SphereCollider(1)));
            WorldEngine.getCurrent().attach(new ModelRenderComponent(Resource.getModel("sphere")).attach(sphere));//.attach(new LightComponent(new Light(new Vector3f(), new Vector3f(1,1,1), 100, 100))));
            components.offer(sphere);
        }

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 1500f));

        Server server = NetworkEngine.initializeServer("sonicville", 25565);
        WorldEngine.saveWorld(WorldEngine.getCurrent(), "coolworld.bmf");

        for(int i = 0; i < 20; i++){
            var ffc = new FreeFlyComponent();
            ffc.setUserId(i);

            var mod = new ModelRenderComponent(Resource.getModel("sphere"));
            mod.setPositionOffset(new Vector3f(0, -2,-2));
            ffc.attach(mod);

            WorldEngine.getCurrent().attach(ffc);
        }

        server.subscribe(new ConnectionListener() {
            @Override
            public void onConnection(ServerClient user) {

            }

            @Override
            public void onDisconnection(ServerClient user) {

            }
        });
    }

    @Override
    public void render() {}

    @Override
    public void update(float delta){
        i++;
        if (i > 1 && false){
            i = 0;
            var sphere = new PhysicsComponent();
            sphere.getEntity().setPosition(new Vector3f(20f * (float)Math.random(), (float)Math.random() * 40f + 20, (float)Math.random() * 20f));
            sphere.addCollider(new ColliderGroup(new AABB( 3, 3, 3),  new SphereCollider(1)));
            sphere.attach(new LightComponent(Light.createPoint(new Vector3f(), new Vector3f(1,1,1), 100)));
            WorldEngine.getCurrent().attach(sphere);
            components.add(sphere);
            var comp =  components.remove();
            WorldEngine.markForRemoval(comp);
        }

    }
}