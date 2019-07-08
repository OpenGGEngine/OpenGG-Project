package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Soundtrack;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.audio.SoundEngine;
import com.opengg.core.engine.*;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIButton;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.*;
import com.opengg.core.model.io.AssimpModelLoader;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.render.ProjectionData;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.gui.GUIController;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;
import static java.awt.Color.RED;

import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.text.impl.GGFont;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.world.components.*;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.core.world.components.viewmodel.ViewModelComponentRegistry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class  OpenGGTest extends GGApplication{
    private GGFont font;
    private Text text;
    private Texture worldterrain;
    float i = 0;
    Light l;
    boolean lastNew = false;
    private FreeFlyComponent player;

    public static void main(String[] args){
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.width = 1280;
        w.height = 960;
        w.resizable = true;
        w.type = "GLFW";
        w.vsync = true;
        w.glmajor = 4;
        w.glminor = 3;
        OpenGG.initialize(new OpenGGTest(), new InitializationOptions()
                                                .setApplicationName("OpenGG Test")
                                                .setWindowInfo(w));
    }

    @Override
    public  void setup(){
        Soundtrack track = new Soundtrack();
        track.addSong(Resource.getSoundData("windgarden.ogg"));
        track.addSong(Resource.getSoundData("battlerock.ogg"));
        //track.addSong(Resource.getSoundData("floaterland.ogg"));
        //track.addSong(Resource.getSoundData("hell.ogg"));
        //track.addSong(Resource.getSoundData("intogalaxy.ogg"));
        //track.addSong(Resource.getSoundData("koopa.ogg"));
        //track.addSong(Resource.getSoundData("megaleg.ogg"));
        //track.addSong(Resource.getSoundData("stardust.ogg"));
        track.shuffle();
        track.play();   
        SoundEngine.setGlobalGain(0f);
        SoundtrackHandler.setSoundtrack(track);
        
        font = Resource.getFont("test.fnt", "test.png");
        text = Text.from("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + " the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...")
                    .size(0.32f)
                    .kerning(true)
                    .center(false)
                    .maxLineSize(0.25f);

        GUI mainview = new GUI();
        //mainview.getRoot().addItem("aids", new GUIText(text, font, new Vector2f(1,0)));

        GUIController.addAndUse(mainview, "mainview");

        WorldEngine.setOnlyActiveWorld((World) new World().setName("world1"));

        WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear")).setPositionOffset(new Vector3f(0,0,-3)));
        WorldEngine.getCurrent().attach(new ModelComponent(Resource.getModel("pear")).setPositionOffset(new Vector3f(0,0,-6)));

        WorldEngine.getCurrent().attach(new LightComponent(
               // Light.createPointShadow(new Vector3f(0,4,4), new Vector3f(1), 100, 512, 512 )).setName("testlight"));
                Light.createDirectionalShadow(new Quaternionf(new Vector3f(0,0f,90)), new Vector3f(1,1,1),
                        new Vector3f(0,0,0),1000, Matrix4f.orthographic(-10,10,-10,10,-10,10), 4096, 4096)));

        player = new FreeFlyComponent();
        WorldEngine.getCurrent().attach(player);

        World w2 = new World();

        w2.setName("world2");
        w2.attach(new ModelComponent(Resource.getModel("pear")).setPositionOffset(new Vector3f(2,0,-3)));
        w2.attach(new FreeFlyComponent());

        WorldEngine.activateWorld(w2);

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(Resource.getTexturePath("skybox\\majestic_ft.png"),
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
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_RIGHT);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_LEFT);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_UP);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_DOWN);
        BindController.addBind(ControlType.KEYBOARD, "aim", KEY_K);

        RenderEngine.setProjectionData(ProjectionData.getPerspective(100, 0.2f, 3000f));

        WindowController.getWindow().setCursorLock(false);
    }

    @Override
    public void render() {}

    @Override
    public void update(float delta){
        i += delta;
        if(i >= 4){
            i = 0;
            if(this.lastNew) WorldEngine.setPrimaryWorld(WorldEngine.getExistingWorld("world1"));
            else WorldEngine.setPrimaryWorld(WorldEngine.getExistingWorld("world2"));
            lastNew = !lastNew;
        }
    }


}
