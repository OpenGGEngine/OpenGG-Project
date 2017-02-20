package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.AudioController;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.RenderGroup;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.gui.GUIItem;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import static com.opengg.core.render.window.WindowOptions.GLFW;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.WorldObject;
import com.opengg.core.world.components.KeyTrigger;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.PlayerComponent;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.TriggerableAudioComponent;
import com.opengg.core.world.components.UserControlComponent;
import com.opengg.core.world.components.particle.ParticleSystem;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.io.File;
import java.io.IOException;

public class OpenGGTest extends GGApplication implements MouseButtonListener {
    private final float sens = 0.25f;
    private float xrot, yrot;
    private boolean lock = false;
    private float rot1 = 0, rot2 = 0;
    private Vector3f rot = new Vector3f(0, 0, 0);
    private Vector3f pos = new Vector3f(0, -10, -30);
    private Quaternionf rottest = new Quaternionf(1,0,0,1);
    private WorldObject terrain;
    private Camera c;
    private MatDrawnObject awp3, base2;
    private GGFont f;
    private Texture t2, t3;
    private Sound so, so2;
    private AudioListener as;
    private WorldObject awps;
    private PhysicsComponent bad;
    private Light l;
    
    public static void main(String[] args) throws IOException, Exception {
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.height = 1024;
        w.width = 1280;
        w.resizable = false;
        w.type = GLFW;
        w.vsync = true;
        OpenGG.initialize(new OpenGGTest(), w);
    }

    @Override
    public  void setup(){
        as = new AudioListener();
        AudioController.setListener(as);

        so = new Sound(OpenGGTest.class.getResource("res/maw.wav"));
        so2 = new Sound(OpenGGTest.class.getResource("res/mgs.wav"));

        t3 = Texture.get("C:/res/deer.png");
        t2 = Texture.get("C:/res/test.png");

        f = new GGFont(t2, new File("C:/res/test.fnt"));
        GUIText g = new GUIText("It is a period of civil war. Rebel spaceships, striking from a hidden base,"
                + " have won their first victory against the evil Galactic Empire. During the battle,"
                + " Rebel spies managed to steal secret plans to the Empires ultimate weapon, the DEATH STAR,"
                + " an armored space station with enough power to destroy an entire planet. \n\n"
                + " Pursued by the Empires sinister agents, Princess Leia races home aboard her starship,"
                + " custodian of the stolen plans that can save her people and restore freedom to the galaxy...", f, 20f, new Vector2f(), 10, false);
        awp3 = f.loadText(g);

        g = new GUIText("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + " the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...", f, 1f, new Vector2f(), 0.5f, false);

        base2 = f.loadText(g);

        OpenGG.curworld.floorLev = -10;
        World w = OpenGG.curworld;

        RenderComponent ep1;

        awps = new WorldObject();
        awps.attach(ep1 = new RenderComponent(awp3));
        awps.setPositionOffset(new Vector3f(5,5,5));

        ParticleSystem p = new ParticleSystem(2f,20f,100f,ObjectCreator.createOldModelBuffer(OpenGGTest.class.getResource("res/models/deer.obj")), t3);
        
        ModelRenderComponent r = new ModelRenderComponent(ModelLoader.loadModel("C:/res/model/model.bmf"));

        TriggerableAudioComponent test3 = new TriggerableAudioComponent(so2);
        KeyTrigger t = new KeyTrigger(KEY_P, KEY_I);
        t.addSubscriber(test3);

        terrain = new WorldObject();
        bad = new PhysicsComponent();
        bad.setParentInfo(terrain);
        terrain.attach(bad);
        terrain.attach(r);
        terrain.attach(p);
        
        PlayerComponent player = new PlayerComponent();
        CameraComponent camera = new CameraComponent();
        UserControlComponent controller = new UserControlComponent();
        player.attach(camera);
        player.attach(controller);
        WorldEngine.getCurrent().attach(player);
        camera.use();
        BindController.addController(controller);
        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "down", KEY_LEFT_SHIFT);
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_Q);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_E);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_U);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_F);
        
        w.attach(test3);
        w.attach(terrain);
        w.attach(awps);

        RenderGroup text = new RenderGroup().add(ep1);
        text.setText(true);
        
        l = new Light(new Vector3f(10,10,10), new Vector3f(1,1,1), 80f, 0);
        
        RenderEngine.addLight(l);
        RenderEngine.setSkybox(ObjectCreator.createCube(1500f), Cubemap.get("C:/res/skybox/majestic"));
        RenderEngine.addGUIItem(new GUIItem(base2, new Vector2f()));
        RenderEngine.addRenderable(p);
        RenderEngine.addRenderable(r);
        RenderEngine.addRenderGroup(text);
        RenderEngine.setCulling(false);    
    }
    
    @Override
    public void render() {
        as.setPos(pos);
        as.setRot(rot);
        AudioController.setListener(as);
        ShaderController.setPerspective(90, OpenGG.window.getRatio(), 1, 5000f);
        
        RenderEngine.draw();
    }

    @Override
    public void update() {
        xrot -= rot1 * 7;
        yrot -= rot2 * 7;
        terrain.setRotationOffset(rottest);
    }

    @Override
    public void buttonPressed(int button) {
        bad.velocity = new Vector3f(0,20,0);
    }

    @Override
    public void buttonReleased(int button) {}
}
