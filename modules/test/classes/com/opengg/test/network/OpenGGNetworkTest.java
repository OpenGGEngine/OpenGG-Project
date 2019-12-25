package com.opengg.test.network;

import com.opengg.core.engine.*;
import com.opengg.core.io.ControlType;
import com.opengg.core.io.input.mouse.MouseButton;
import com.opengg.core.math.Vector3f;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.physics.collision.colliders.ConvexHull;
import com.opengg.core.render.ProjectionData;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.RigidBodyComponent;
import com.opengg.test.network.components.Enemy;
import com.opengg.test.network.components.GrenadeItem;
import com.opengg.test.network.components.Player;
import com.opengg.test.network.components.WorldItemContainer;

import java.awt.*;
import java.util.List;

import static com.opengg.core.io.input.keyboard.Key.*;

public class OpenGGNetworkTest extends GGApplication {
    final boolean SERVER = true;
    public static void main(String[] args){
        OpenGG.initialize(new OpenGGNetworkTest(), new InitializationOptions()
                .setApplicationName("OpenGG Network Test")
                .setWindowInfo(new WindowInfo()
                        .setDisplayMode(WindowOptions.WINDOWED)
                        .setWidth(1280)
                        .setHeight(960)
                        .setVsync(true)
                        .setResizable(true)));
    }

    @Override
    public void setup() {
        var cube = List.of(
                new Vector3f(-1,-1,-1),
                new Vector3f(-1,-1,1),
                new Vector3f(-1,1,-1),
                new Vector3f(-1,1,1),
                new Vector3f(1,-1,-1),
                new Vector3f(1,-1,1),
                new Vector3f(1,1,-1),
                new Vector3f(1,1,1)
        );

        if(SERVER){
            //NetworkEngine.pollServer(new ConnectionData("localhost", "25565")).thenAccept(System.out::println);
                //NetworkEngine.connect(new NetworkEngine.ClientOptions("Jav", "localhost", "25565"));
            MainMenu.initialize();
        }else {
            WorldEngine.setOnlyActiveWorld(WorldLoader.loadWorld("Test"));

        /*var object2 = new RigidBodyComponent(new RigidBody(new AABB( 3, 3, 3),  new ConvexHull(cube)), true);
        WorldEngine.getCurrent().attach(
                new RenderComponent(new TextureRenderable(ObjectCreator.createQuadPrism(new Vector3f(-1,-1,-1), new Vector3f(1,1,1)),
                        Texture.ofColor(Color.GRAY))).attach(object2).setPositionOffset(new Vector3f(5, 10, 5)));

        var object4 = new RigidBodyComponent(new RigidBody(new AABB( 3, 3, 3),  new SphereCollider(1)), true);
        WorldEngine.getCurrent().attach(
                new RenderComponent(new TextureRenderable(ObjectCreator.createQuadPrism(new Vector3f(-1,-1,-1), new Vector3f(1,1,1)),
                        Texture.ofColor(Color.GRAY))).attach(object4).setPositionOffset(new Vector3f(5, 10, 5)));*/

            var object3 = new RigidBodyComponent(new RigidBody(new AABB(3, 3, 3), new ConvexHull(cube)), true);
            WorldEngine.getCurrent().attach(
                    new RenderComponent(new TextureRenderable(ObjectCreator.createQuadPrism(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1)),
                            Texture.ofColor(Color.GRAY))).attach(object3).setPositionOffset(new Vector3f(20, 2, 25)));
            /*WorldEngine.getCurrent().getSystem().addObject(new SpringGenerator(object2.getRigidBody(), new Vector3f(5,25,5),1f));*/

            WorldEngine.getCurrent().attach(new WorldItemContainer(new GrenadeItem()).setPositionOffset(new Vector3f(3, 3, 8)));


            var player = new Player();
            player.setPositionOffset(new Vector3f(
                    5, 3, 5
            ));
            WorldEngine.getCurrent().attach(player);

            var testEnemy = new Enemy();
            testEnemy.setPositionOffset(new Vector3f(
                    7, 3, 7
            ));
            WorldEngine.getCurrent().attach(testEnemy);

            WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(
                    Resource.getTexturePath("skybox\\majestic_ft.png"),
                    Resource.getTexturePath("skybox\\majestic_bk.png"),
                    Resource.getTexturePath("skybox\\majestic_up.png"),
                    Resource.getTexturePath("skybox\\majestic_dn.png"),
                    Resource.getTexturePath("skybox\\majestic_rt.png"),
                    Resource.getTexturePath("skybox\\majestic_lf.png")), 500f));
        }
        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "down", KEY_LEFT_SHIFT);
        BindController.addBind(ControlType.KEYBOARD, "fast", KEY_LEFT_CONTROL);
        BindController.addBind(ControlType.KEYBOARD, "aim", KEY_K);
        BindController.addBind(ControlType.MOUSEBUTTON, "use", MouseButton.LEFT);
        BindController.addBind(ControlType.SCROLLWHEEL, "next", MouseButton.SCROLL_UP);
        BindController.addBind(ControlType.SCROLLWHEEL, "previous", MouseButton.SCROLL_DOWN);
        BindController.addBind(ControlType.KEYBOARD, "fps", KEY_F5);
        BindController.addBind(ControlType.KEYBOARD, "test", KEY_P);

        RenderEngine.setProjectionData(ProjectionData.getPerspective(100, 0.2f, 3000f));
    }

    @Override
    public void render() {

    }

    @Override
    public void update(float delta) {

    }
}
