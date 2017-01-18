package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.AudioController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.RenderGroup;
import com.opengg.core.gui.GUIItem;
import com.opengg.core.gui.GUIText;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import com.opengg.core.render.window.GLFWWindow;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import static com.opengg.core.render.window.WindowOptions.GLFW;
import com.opengg.core.world.Camera;
import com.opengg.core.world.WorldObject;
import com.opengg.core.world.components.KeyTrigger;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.TriggerableAudioComponent;
import com.opengg.core.world.components.particle.ParticleSystem;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.io.File;
import java.io.IOException;

public class OpenGGTest extends GGApplication implements KeyboardListener, MouseButtonListener {
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
    
    public static void main(String[] args) throws IOException, Exception {
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.height = 1024;
        w.width = 1280;
        w.resizable = false;
        w.type = GLFW;
        w.vsync = true;
        OpenGG.initialize(new OpenGGTest(), w);
        OpenGG.run();
    }

    @Override
    public  void setup(){
        KeyboardController.addToPool(this);
        MovementLoader.setup(80);

        c = new Camera(pos, rot);
        c.setPos(pos);
        c.setRot(rot);

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

        g = new GUIText("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n"
                + "\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + "the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n"
                + "\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...", f, 1f, new Vector2f(), 0.5f, false);

        base2 = f.loadText(g);

        OpenGG.curworld.floorLev = -10;

        ModelRenderComponent ep1;

        awps = new WorldObject();
        awps.attach(ep1 = new ModelRenderComponent(awp3));ep1.getDrawable();
        awps.setPosition(new Vector3f(5,5,5));

        ParticleSystem p = new ParticleSystem(2f,20f,100f,ObjectCreator.createOldModelBuffer(OpenGGTest.class.getResource("res/models/deer.obj")), t3);
        ModelRenderComponent r = new ModelRenderComponent(ModelLoader.loadModel("C:/res/bigbee/model.bmf"));
        r.setScale(new Vector3f(50,50,50));

        TriggerableAudioComponent test3 = new TriggerableAudioComponent(so2);
        KeyTrigger t = new KeyTrigger(KEY_P, KEY_I);
        t.addSubscriber(test3);

        terrain = new WorldObject();
        bad = new PhysicsComponent();
        bad.setParentInfo(terrain);
        terrain.attach(bad);
        terrain.attach(r);
        terrain.attach(p);
        terrain.attach(test3);

        for(int ii = 0; ii < 10; ii++){
            for(int j = ii + 1; j < 10; j++){
                //System.out.println(ii + ", " + j);
            }
        }

        RenderGroup text = new RenderGroup().add(ep1);
        text.setText(true);

        RenderEngine.setSkybox(ObjectCreator.createCube(1500f), Cubemap.get("C:/res/skybox/majestic"));
        RenderEngine.addGUIItem(new GUIItem(base2, new Vector2f()));
        RenderEngine.addRenderable(p);
        RenderEngine.addRenderable(r);
        RenderEngine.addRenderGroup(text);
        
    }
    
    @Override
    public void render() {
        rot = new Vector3f(yrot, xrot, 0);
        if(lock){
            rot = MovementLoader.processRotation(sens, false);
        }
        pos = MovementLoader.processMovement(pos, rot);
        
        as.setPos(pos);
        as.setRot(rot);
        AudioController.setListener(as);
        
        c.setPos(pos);
        c.setRot(rot);

        ShaderController.setLightPos(new Vector3f(40, 200, 40));
        ShaderController.setView(c);
        ShaderController.setPerspective(90, OpenGG.window.getRatio(), 1, 2500f);
        
        RenderEngine.draw();
    }

    @Override
    public void update() {
        xrot -= rot1 * 7;
        yrot -= rot2 * 7;
        //rottest.addDegrees(2);
        //System.out.println(rottest);
        terrain.setRotation(rottest);
    }

    @Override
    public void keyPressed(int key) {
        if (key == KEY_M) {
            ((GLFWWindow) OpenGG.window).setCursorLock(lock = !lock);
        }
        if (key == KEY_Q) {
            rot1 += 0.3;
        }
        if (key == KEY_E) {
            rot1 -= 0.3;
        }
        if (key == KEY_R) {
            rot2 += 0.3;
        }
        if (key == KEY_F) {
            rot2 -= 0.3;
        }
        if (key == KEY_G) {
            bad.velocity = new Vector3f(0,20,0);
        }
        if (key == KEY_ESCAPE) {
            OpenGG.endApplication();
        }
        if (key == KEY_U){
            RenderEngine.setShadowVolumes(!RenderEngine.getShadowsEnabled());
        }
    }

    @Override
    public void keyReleased(int key) {
        if (key == KEY_Q) {
            rot1 -= 0.3;
        }
        if (key == KEY_E) {
            rot1 += 0.3;
        }
        if (key == KEY_R) {
            rot2 -= 0.3;
        }
        if (key == KEY_F) {
            rot2 += 0.3;
        }
    }

    @Override
    public void buttonPressed(int button) {
        bad.velocity = new Vector3f(0,20,0);
    }

    @Override
    public void buttonReleased(int button) {}
}
