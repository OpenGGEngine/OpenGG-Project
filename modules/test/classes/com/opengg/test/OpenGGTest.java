package com.opengg.test;

import com.opengg.core.engine.*;
import com.opengg.core.gui.GUI;
import com.opengg.core.math.*;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.physics.collision.colliders.ConvexHull;
import com.opengg.core.render.ProjectionData;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.TextureGenerator;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;

import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.*;
import com.opengg.core.world.components.physics.RigidBodyComponent;
import com.opengg.core.world.structure.WorldGeometryBuilder;

import java.awt.*;
import java.util.List;


public class  OpenGGTest extends GGApplication{
    private Font font;
    private Text text;
    private Texture worldterrain;
    float i = 0;
    Light l;
    boolean lastNew = false;
    private FreeFlyComponent player;

    public static void main(String[] args){
        WindowOptions w = new WindowOptions();
        w.displayMode = WindowOptions.DisplayMode.WINDOWED;
        w.width = 1280;
        w.height = 960;
        w.resizable = true;
        w.type = "GLFW";
        w.vsync = true;
        w.glmajor = 4;
        w.glminor = 3;
        w.renderer = WindowOptions.RendererType.OPENGL;
        OpenGG.initialize(new OpenGGTest(), new InitializationOptions()
                                                .setApplicationName("OpenGG Test")
                                                .setWindowOptions(w));
    }

    @Override
    public  void setup(){
        //Soundtrack track = new Soundtrack();
        //track.addSong(Resource.getSoundData("windgarden.ogg"));
        //track.addSong(Resource.getSoundData("battlerock.ogg"));
        //track.addSong(Resource.getSoundData("floaterland.ogg"));
        //track.addSong(Resource.getSoundData("hell.ogg"));
        //track.addSong(Resource.getSoundData("intogalaxy.ogg"));
        //track.addSong(Resource.getSoundData("koopa.ogg"));
        //track.addSong(Resource.getSoundData("megaleg.ogg"));
        //track.addSong(Resource.getSoundData("stardust.ogg"));
        //track.shuffle();
        //track.play();
        //SoundEngine.setGlobalGain(0f);
        //SoundtrackHandler.setSoundtrack(track);

        font = Resource.getTruetypeFont("consolas.ttf");
        text = Text.from("""
                        Turmoil has engulfed the Galactic Republic.
                        The taxation of trade routes to outlying star systems is in dispute.
                        Hoping to resolve the matter with a blockade of deadly battleships,
                        the greedy Trade Federation has stopped all shipping to the small planet of Naboo.

                        While the congress of the Republic endlessly debates this alarming chain of events,
                        the Supreme Chancellor has secretly dispatched two Jedi Knights,
                        the guardians of peace and justice in the galaxy, to settle the conflict...
                        """)
                    .size(0.08f)
                    .kerning(true)
                    .center(false)
                    .maxLineSize(0.6f);

        GUI mainview = new GUI();

        //mainview.addItem(new UITextLine(text, font, new Vector2f(0,1)));
        //GUIController.addAndUse(mainview, "mainview");

        //NetworkEngine.connect("localhost", 25565);
        //WorldEngine.setOnlyActiveWorld(WorldEngine.getWorld("Test.bwf"));
        //WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear"), true).setPositionOffset(new Vector3f(2,0,-3)));
        //WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear"), true).setPositionOffset(new Vector3f(0,0,-6)));
        //WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear"), true).setPositionOffset(new Vector3f(0,0,6)));
        //WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear"), true).setPositionOffset(new Vector3f(5,0,0)));

        //WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear"), true).setPositionOffset(new Vector3f(35,0,-32)));
        //WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear"), true).setPositionOffset(new Vector3f(30,0,-28)));

        WorldEngine.getCurrent().getStructure().addGeometry(
                WorldGeometryBuilder.fromCuboid(new Vector3f(45,0,-30), new Quaternionf(), new Vector3f(2,10,30), TextureGenerator.ofColor(Color.RED, 1), true));
        WorldEngine.getCurrent().getStructure().addGeometry(
                WorldGeometryBuilder.fromCuboid(new Vector3f(15,0,-30), new Quaternionf(), new Vector3f(2,10,30), Resource.getTextureData("water.jpg"), true));
        WorldEngine.getCurrent().getStructure().addGeometry(
                WorldGeometryBuilder.fromCuboid(new Vector3f(30,0,-45), new Quaternionf(), new Vector3f(30,10,2), Resource.getTextureData("water.jpg"), true));
        WorldEngine.getCurrent().getStructure().addGeometry(
                WorldGeometryBuilder.fromCuboid(new Vector3f(30,0,-15), new Quaternionf(), new Vector3f(30,10,2), Resource.getTextureData("water.jpg"), true));
        WorldEngine.getCurrent().getStructure().addGeometry(
                WorldGeometryBuilder.fromCuboid(new Vector3f(0,-6,0), new Quaternionf(), new Vector3f(400,10,400), Resource.getTextureData("grass.png"), true));

        var light = new LightComponent(
                //Light.createPointShadow(new Vector3f(2,6,-4), new Vector3f(1), 100, 1024, 1024 )).setName("testlight");
                //Light.createDirectionalShadow(Quaternionf.createYXZ(new Vector3f(45,0,0)), new Vector3f(1, 1, 1),
                //        new Vector3f(0, 0, 0), Matrix4f.orthographic(-100, 100, -100, 100, -100, 100), 4096, 4096));
                Light.createDirectional(Quaternionf.createYXZ(new Vector3f(45,30,0)), new Vector3f(1, 1, 1)));
        WorldEngine.getCurrent().attach(light);
        player = new FreeFlyComponent();
        WorldEngine.getCurrent().attach(player);;


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

        for (int i = 0; i < 2; i++) {
            var multiple = i == 0 ? -1 : 1;

            RigidBody object = new RigidBody(new AABB( 3, 3, 3), new ConvexHull(cube));
            object.enablePhysicsProvider();
            object.getPhysicsProvider().get().velocity = new Vector3f(multiple * -8, multiple*0.01f, 5);
            if(i == 1) object.getPhysicsProvider().get().mass = 2;

           WorldEngine.getCurrent().attach(
                    new RenderComponent(
                            new TextureRenderable(ObjectCreator.createQuadPrism(new Vector3f(-1,-1,-1), new Vector3f(1,1,1)),
                                    i == 0 ? Texture.ofColor(Color.RED) : Texture.ofColor(Color.BLUE))).attach(new RigidBodyComponent(object, true))
                            .setPositionOffset(new Vector3f(10f * multiple, 10f, -15)));
        }

        var object = new RigidBodyComponent(new RigidBody(new AABB( 3, 3, 3),  new ConvexHull(cube)), true);
        WorldEngine.getCurrent().attach(
                new RenderComponent(new TextureRenderable(ObjectCreator.createQuadPrism(new Vector3f(-1,-1,-1), new Vector3f(1,1,1)),
                                Texture.ofColor(Color.GRAY))).attach(object).setPositionOffset(new Vector3f(28, 5, -33)));

        BindController.addTransmitter(a -> {
            if(a.type.equals(ActionType.PRESS) && a.name.equals("test"))
                object.getRigidBody().getPhysicsProvider().get().velocity = new Vector3f((FastMath.random() - 0.5f) * 20f, 0, (FastMath.random() - 0.5f)*20f);
        });

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.create(Texture.cubemapConfig(),
                Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 500f));

        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "down", KEY_LEFT_SHIFT);
        BindController.addBind(ControlType.KEYBOARD, "fast", KEY_LEFT_CONTROL);
        BindController.addBind(ControlType.KEYBOARD, "aim", KEY_K);
        BindController.addBind(ControlType.KEYBOARD, "test", KEY_P);

        RenderEngine.setProjectionData(ProjectionData.getPerspective(100, 0.2f, 3000f));
        WindowController.getWindow().setCursorLock(true);

        player = new FreeFlyComponent();
        WorldEngine.getCurrent().attach(player);
    }

    @Override
    public void render() {}

    @Override
    public void update(float delta){
    }
}
