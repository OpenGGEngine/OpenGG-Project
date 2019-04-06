package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.FastMath;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.server.ConnectionListener;
import com.opengg.core.network.server.Server;
import com.opengg.core.engine.Resource;
import com.opengg.core.network.server.ServerClient;
import com.opengg.core.render.text.Text;
import com.opengg.core.world.Terrain;
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

import java.util.*;


public class OpenGGTestServer extends GGApplication{

    public static void main(String[] args){
        OpenGG.initializeHeadless(new OpenGGTestServer());
    }
    int i = 0;

    List<ModelComponent> comps = new ArrayList<>();

    @Override
    public  void setup(){
        OpenGG.setTargetUpdateTime(1/20f);

        WorldEngine.getCurrent().attach(new LightComponent(
                Light.createDirectional(new Quaternionf(new Vector3f(80f,0f,50)),
                        new Vector3f(1,1,1))));

        WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear")));


        Server server = NetworkEngine.initializeServer("sonicville", 25565);

        for(int i = 0; i < 3; i++){
            var ffc = new FreeFlyComponent();
            ffc.setUserId(i);

            var mod = new ModelComponent(Resource.getModel("pear"));
            mod.setScaleOffset(0.2f);
            mod.setPositionOffset(new Vector3f(0, -1,-1));
            ffc.attach(mod);

            WorldEngine.getCurrent().attach(ffc);

            var newcomp = new ModelComponent(Resource.getModel("pear"));
            comps.add(newcomp);
            WorldEngine.getCurrent().attach(newcomp.setPositionOffset(new Vector3f(FastMath.random()*30f,FastMath.random()*30f, FastMath.random()*30)));
        }

        server.subscribe(new ConnectionListener() {
            @Override
            public void onConnection(ServerClient user) {

            }

            @Override
            public void onDisconnection(ServerClient user) {

            }
        });

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 1500f));


    }

    @Override
    public void render() {}

    @Override
    public void update(float delta) {
        i++;

        if(i == 400){
            i = 0;

            var removecomp = comps.get(new Random().nextInt(comps.size()));
            WorldEngine.markForRemoval(removecomp);
            comps.remove(removecomp);

            var newcomp = new ModelComponent(Resource.getModel("pear"));
            comps.add(newcomp);
            WorldEngine.getCurrent().attach(newcomp.setPositionOffset(new Vector3f(FastMath.random()*30f,FastMath.random()*30f, FastMath.random()*30)));
        }
    }
}