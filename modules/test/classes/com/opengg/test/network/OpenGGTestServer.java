package com.opengg.test.network;

import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.InitializationOptions;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.common.ChatMessage;
import com.opengg.core.network.server.ConnectionListener;
import com.opengg.core.network.server.Server;
import com.opengg.core.engine.Resource;
import com.opengg.core.network.server.ServerClient;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldLoader;
import com.opengg.test.network.components.GrenadeItem;
import com.opengg.test.network.components.Player;
import com.opengg.test.network.components.WorldItemContainer;


public class OpenGGTestServer extends GGApplication{

    public static void main(String[] args){
        OpenGG.initialize(new OpenGGTestServer(), new InitializationOptions()
                .setApplicationName("OpenGG Network Test")
                .setHeadless(true));
    }

    @Override
    public  void setup(){
        OpenGG.setTargetUpdateTime(1/60f);
        Server server = NetworkEngine.initializeServer(new NetworkEngine.ServerOptions("Javicraft", "Back and better than ever!", 25565, 5));
        WorldEngine.setOnlyActiveWorld(WorldLoader.loadWorld("Test"));
        //WorldEngine.getCurrent().setShouldSerializePhysics(true);

        WorldEngine.getCurrent().attach(new WorldItemContainer(new GrenadeItem()).setPositionOffset(new Vector3f(3, 3, 8)));

        server.subscribe(new ConnectionListener() {
            @Override
            public void onConnection(ServerClient user) {
                server.getClients().forEach(c -> new ChatMessage("", user.getName() + " has joined the server").send(c.getConnection()));
                createPlayer(user.getId());
            }

            @Override
            public void onDisconnection(ServerClient user) {
                server.getClients().forEach(c -> new ChatMessage("", user.getName() + " has left the server").send(c.getConnection()));
                WorldEngine.getCurrent().findByName(user.getName() + user.getId()).get(0).delete();
            }
        });

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 1500f));
    }

    public static void createPlayer(int id){
        var player = new Player();
        player.setUserID(id);
        player.setPositionOffset(id == 1 ? new Vector3f(5,4,5) : new Vector3f(2,4,3));
        WorldEngine.getCurrent().attach(player);
    }

    @Override
    public void render() {}

    @Override
    public void update(float delta) {

        /*if(i == 400){
            i = 0;

            var removecomp = comps.get(new Random().nextInt(comps.size()));
            WorldEngine.markComponentForRemoval(removecomp);
            comps.remove(removecomp);

            var newcomp = new ModelComponent(Resource.getModel("pear"));
            comps.add(newcomp);
            WorldEngine.getCurrent().attach(newcomp.setPositionOffset(new Vector3f(FastMath.random()*30f,FastMath.random()*30f, FastMath.random()*30)));
        }*/
    }
}