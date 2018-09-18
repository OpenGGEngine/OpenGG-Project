package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Soundtrack;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.audio.AudioController;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIText;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.model.modelloaderplus.AnimatedComponent;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.SphereCollider;
import com.opengg.core.render.ProjectionData;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.engine.Resource;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.window.GLFWWindow;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.gui.GUIController;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.text.impl.GGFont;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.world.components.FreeFlyComponent;
import com.opengg.core.world.components.LightComponent;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.TerrainComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.core.world.components.viewmodel.ViewModelComponentRegistry;


public class OpenGGTest extends GGApplication{
    private GGFont font;
    private Text text;
    private TerrainComponent world;
    private Texture worldterrain;
    private AudioListener listener;
    float i = 0;
    Light l;
    private FreeFlyComponent player;

    public static void main(String[] args){
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.width = 1280;
        w.height = 1024;
        w.resizable = true;
        w.type = "GLFW";
        w.vsync = true;
        w.glmajor = 4;
        w.glminor = 3;
        OpenGG.initialize(new OpenGGTest(), w);

        //ShaderController.testInitialize();
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
        AudioController.setGlobalGain(0f);
        SoundtrackHandler.setSoundtrack(track);
        
        font = Resource.getFont("test.fnt", "test.png");
        text = Text.from("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + " the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...")
                    .size(0.5f)
                    .kerning(true)
                    .center(false)
                    .maxLineSize(0.25f);

        GUI mainview = new GUI();
        mainview.getRoot().addItem("aids", new GUIText(text, font, new Vector2f(1,0)));

        GUIController.addAndUse(mainview, "mainview");

        //NetworkEngine.connect("localhost", 25565);


        WorldEngine.getCurrent().attach(new LightComponent(
                Light.createDirectional(new Quaternionf(new Vector3f(80f,0f,50)),
                        new Vector3f(1,1,1),
                        10000f)));

        WorldEngine.getCurrent().attach(new ModelRenderComponent(Resource.getModel("goldleaf")).setScaleOffset(new Vector3f(0.01f)).setRotationOffset(new Vector3f(90,0,0)));


        for (int i = 0; i < 20; i++) {
            PhysicsComponent sphere = new PhysicsComponent();
            sphere.getEntity().setPosition(new Vector3f(120f * (float)Math.random(), (float)Math.random() * 40f + 200, (float)Math.random() * 120f));
            sphere.addCollider(new ColliderGroup(new AABB( 3, 3, 3),  new SphereCollider(1)));
            //WorldEngine.getCurrent().attach(new ModelRenderComponent(Resource.getModel("sphere")).attach(sphere));//.attach(new LightComponent(new Light(new Vector3f(), new Vector3f(1,1,1), 100, 100))));
        }

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 1500f));

        /*try {
            MModel s = ModelLoaderPlus.loadModel(new File("C:/res/model2.bmf"),new File("C:/res/anim2.gga"));
            component = new AnimatedComponent(s);
            component.setFormat(RenderEngine.animation2VAOFormat);
            component.setShader("animation2");
            WorldEngine.getCurrent().attach(component);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        player = new FreeFlyComponent();
        player.use();

        var model = new ModelRenderComponent(Resource.getModel("45acp"));
        model.setName("ballmodel");
        model.setScaleOffset(new Vector3f(1f));

        WorldEngine.getCurrent().attach(player);
        WorldEngine.getCurrent().attach(model);


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

        ViewModelComponentRegistry.initialize();
        ViewModelComponentRegistry.createRegisters();
        
        RenderEngine.setProjectionData(ProjectionData.getPerspective(100, 0.2f, 3000f));

        WindowController.getWindow().setCursorLock(true);
    }

    @Override
    public void render() {}

    @Override
    public void update(float delta){
        i++;
//        l.setView();
    }
}
